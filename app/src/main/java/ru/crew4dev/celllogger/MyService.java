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
import java.util.Date;
import java.util.List;

import ru.crew4dev.celllogger.data.TowerInfo;

import static android.content.ContentValues.TAG;

public class MyService extends Service {
    final static String LOG_TAG = "MyServiceOld";
    final static String MY_ACTION = "MY_ACTION";

    static final int SLEEP_MC = 20000;
    static final SimpleDateFormat dateFullFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
    private TowerInfo lastTower;

    public static final String LAC = "LAC";
    public static final String CELL_ID = "CELL_ID";

    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyThread myThread = new MyThread();
        myThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    public MainActivity.ScanInfo getCellInfo() {
        MainActivity.ScanInfo scanInfo = new MainActivity.ScanInfo();
        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int phoneTypeInt = tel.getPhoneType();
        String phoneType = null;
        phoneType = phoneTypeInt == TelephonyManager.PHONE_TYPE_GSM ? "gsm" : phoneType;
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
                                TowerInfo tower = new TowerInfo(identityGsm.getCid(), identityGsm.getLac(), gsm.getDbm(), identityGsm.getMcc(), identityGsm.getMnc());
                                lastTower = tower;
                                scanInfo.towerList.add(tower);
                                Log.d(TAG, new Date() + " \t" + tower.toString());
                            }
                        } else if (info instanceof CellInfoLte) {
                            CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                            CellIdentityLte identityLte = ((CellInfoLte) info).getCellIdentity();
                            if (identityLte.getCi() != Integer.MAX_VALUE && identityLte.getTac() != Integer.MAX_VALUE) {
                                Log.d(TAG, new Date() + " \t" + identityLte.getTac() + " \t" + identityLte.getCi());
                                if (lastTower == null || lastTower.getTac() != identityLte.getTac() || lastTower.getCellId() != identityLte.getCi()) {
                                    TowerInfo tower = new TowerInfo(identityLte.getCi(), identityLte.getTac(), lte.getDbm(), identityLte.getMcc(), identityLte.getMnc());
                                    lastTower = tower;
                                    scanInfo.towerList.add(tower);
                                }
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
        return scanInfo;
    }

    private void saveLine(MainActivity.ScanInfo scanInfo) {
        for (TowerInfo towerInfo : scanInfo.towerList) {
            StringBuilder out = new StringBuilder();
            out.append(dateFullFormat.format(towerInfo.getDate()));
            out.append(";");
            out.append(towerInfo.getTac());
            out.append(";");
            out.append(towerInfo.getCellId());
            out.append("\n");
            writeToFile(out.toString());
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
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
            // TODO Auto-generated method stub
            do {
                try {
                    MainActivity.ScanInfo scanInfo = getCellInfo();
                    if (scanInfo != null && scanInfo.towerList != null) {
                        saveLine(scanInfo);
                        for (TowerInfo item : scanInfo.towerList) {
                            Intent intent = new Intent();
                            intent.putExtra(LAC, item.getTac());
                            intent.putExtra(CELL_ID, item.getCellId());
                            item.getDbm();
                            intent.setAction(MY_ACTION);
                            sendBroadcast(intent);
                        }
                    }
                    Thread.sleep(SLEEP_MC);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } while (true);
            //}
            //stopSelf();
        }
    }
}