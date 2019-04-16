package ru.crew4dev.celllogger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.crew4dev.celllogger.data.Place;
import ru.crew4dev.celllogger.data.Tower;
import ru.crew4dev.celllogger.data.TowerList;

import static ru.crew4dev.celllogger.Constants.PLACE_ID;
import static ru.crew4dev.celllogger.Constants.WORK_DONE;

public class MyService extends Service {
    final static String TAG = "MyService";

    static final int SLEEP_MC = 5000;
    static final SimpleDateFormat dateFullFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
    private Tower lastTower;
    private Place place;
    private TowerList towers;

    private MyThread myThread = null;

    public static final String LAC = "LAC";
    public static final String CELL_ID = "CELL_ID";
    public static final String DBM = "DBM";

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        towers = new TowerList();
        StringBuilder out = new StringBuilder();
        out.append(dateFullFormat.format(new Date()));
        out.append(" - MyService onCreate");
        writeToFile(out.toString());
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (place != null) {
//            place.endDate = new Date();
//            App.db().collectDao().update(place);
//            Log.d(TAG, "update : " + place.toString());
            StringBuilder out = new StringBuilder();
            out.append(dateFullFormat.format(new Date()));
            out.append(" - MyService onDestroy");
            writeToFile(out.toString());
        }
        if (myThread != null) {
            myThread.interrupt();
            Intent intent = new Intent();
            intent.setAction(WORK_DONE);
            sendBroadcast(intent);
            myThread = null;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        if (intent.hasExtra(PLACE_ID)) {
            place = App.db().collectDao().getPlace(intent.getLongExtra(PLACE_ID, 0));
        }
        myThread = new MyThread();
        myThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    public List<Tower> getCellInfo() {
        List<Tower> towerList = new ArrayList<>();
        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        int phoneTypeInt = tel.getPhoneType();
//        String phoneType = phoneTypeInt == TelephonyManager.PHONE_TYPE_GSM ? "gsm" : phoneType;
//        phoneType = phoneTypeInt == TelephonyManager.PHONE_TYPE_CDMA ? "cdma" : phoneType;

        //from Android M up must use getAllCellInfo
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            List<NeighboringCellInfo> neighCells = tel.getNeighboringCellInfo();
            for (int i = 0; i < neighCells.size(); i++) {
                try {
                    JSONObject cellObj = new JSONObject();
                    NeighboringCellInfo thisCell = neighCells.get(i);
                    cellObj.put("cellId", thisCell.getCid());
                    cellObj.put("lac", thisCell.getLac());
                    cellObj.put("rssi", thisCell.getRssi());
                    //cellList.put(cellObj);
                } catch (Exception e) {
                }
            }

        } else {
            List<CellInfo> infos = tel.getAllCellInfo();
            if (infos != null) {
                for (int i = 0; i < infos.size(); ++i) {
                    try {
                        CellInfo info = infos.get(i);
                        if (info instanceof CellInfoGsm) {
                            CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                            CellIdentityGsm identityGsm = ((CellInfoGsm) info).getCellIdentity();
                            if (identityGsm.getCid() != Integer.MAX_VALUE && identityGsm.getLac() != Integer.MAX_VALUE) {
                                Tower tower = new Tower(identityGsm.getCid(), identityGsm.getLac(), gsm.getDbm(), identityGsm.getMcc(), identityGsm.getMnc());
                                lastTower = tower;
                                towerList.add(tower);
                                Log.d(TAG, new Date() + " \t" + tower.toString());
                            }
                        } else if (info instanceof CellInfoLte) {
                            CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                            CellIdentityLte identityLte = ((CellInfoLte) info).getCellIdentity();
                            if (identityLte.getCi() != Integer.MAX_VALUE && identityLte.getTac() != Integer.MAX_VALUE) {
                                Log.d(TAG, new Date() + " \t" + identityLte.getTac() + " \t" + identityLte.getCi());
                                Tower tower = new Tower(identityLte.getCi(), identityLte.getTac(), lte.getDbm(), identityLte.getMcc(), identityLte.getMnc());
                                towerList.add(tower);
                            }
                        }
                    } catch (Exception ex) {
                        Log.d(TAG, new Date() + " \t" + ex.getLocalizedMessage());
                    }
                }
                //scanInfos.add(scanInfo);
            } else {
                Log.d(TAG, new Date() + " \t" + "getAllCellInfo return null");
            }
            //Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + context.getApplicationInfo().processName;
        }

//        final SubscriptionManager subscriptionManager = SubscriptionManager.from(this);
//        final List<SubscriptionInfo> activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
//        for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
//            final CharSequence carrierName = subscriptionInfo.getCarrierName();
//            final CharSequence displayName = subscriptionInfo.getDisplayName();
//            final int mcc = subscriptionInfo.getMcc();
//            final int mnc = subscriptionInfo.getMnc();
//            final String subscriptionInfoNumber = subscriptionInfo.getNumber();
//        }
        return towerList;
    }

    private void saveTowerInfo(List<Tower> towerList) {
        for (Tower tower : towerList) {
            StringBuilder out = new StringBuilder();
            out.append(dateFullFormat.format(tower.getDate()));
            out.append(";");
            out.append(tower.getLac());
            out.append(";");
            out.append(tower.getCellId());
            out.append("\n");
            writeToFile(out.toString());
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private void writeToFile(String data) {
        if (isExternalStorageWritable()) {
            try {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getApplicationInfo().processName;
                File dir = new File(path);
                if (!dir.exists())
                    dir.mkdir();
                File file = new File(path, "log.txt");
                FileOutputStream outputStream = new FileOutputStream(file, true);
                outputStream.write(data.getBytes());
                outputStream.close();
            } catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }
    }

    public class MyThread extends Thread {
        @Override
        public void run() {
            boolean isWork = true;
            do {
                try {
                    final List<Tower> towerList = getCellInfo();
                    saveTowerInfo(towerList);
                    for (Tower tower : towerList) {
                        //Log.d(TAG, new Date() + " \t" + identityLte.getTac() + " \t" + identityLte.getCi());
                        Intent intent = new Intent();
                        intent.setAction(Constants.UPDATE_DATA);
                        intent.putExtra(LAC, tower.getLac());
                        intent.putExtra(CELL_ID, tower.getCellId());
                        intent.putExtra(DBM, tower.getDbm());
                        sendBroadcast(intent);
                        if (lastTower == null || lastTower.getLac() != tower.getLac() || lastTower.getCellId() != tower.getCellId()) {
                            lastTower = tower;
                            if (!towers.isExistTower(tower)) {
                                tower.placeId = place.placeId;
                                towers.add(tower);
                                App.db().collectDao().insert(tower);
                                Log.d(TAG, "Insert " + tower.toString());
                            } else {
                                Log.d(TAG, "Exist tower");
                            }
                        } else {
                            Log.d(TAG, "Old tower");
                        }
                    }
                    Thread.sleep(SLEEP_MC);
                } catch (InterruptedException e) {
                    isWork = false;
                }
            } while (isWork);
            stopSelf();
        }
    }
}