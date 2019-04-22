package ru.crew4dev.celllogger;

import android.annotation.SuppressLint;
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
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

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
    static final SimpleDateFormat fileNameDdateFullFormat = new SimpleDateFormat("yy.MM.dd");
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
        writeToFile(" - MyService onCreate");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        writeToFile(" - MyService onDestroy");
//        if (place != null) {
//            place.endDate = new Date();
//            App.db().collectDao().update(place);
//            Log.d(TAG, "update : " + place.toString());
//        }
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
        writeToFile(" - MyService onStartCommand");
        if (intent.hasExtra(PLACE_ID)) {
            place = App.db().collectDao().getPlace(intent.getLongExtra(PLACE_ID, 0));
        }
        myThread = new MyThread();
        myThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    private List<Tower> getNeighboring(TelephonyManager tel) {
        List<Tower> list = new ArrayList<>();
        @SuppressLint("MissingPermission") List<NeighboringCellInfo> neighCells = tel.getNeighboringCellInfo();
        if (neighCells != null && neighCells.size() > 0) {
            String networkOperator = tel.getNetworkOperator();
            String mcc = networkOperator.substring(0, 3);
            String mnc = networkOperator.substring(3);
            for (int i = 0; i < neighCells.size(); i++) {
                try {
                    NeighboringCellInfo thisCell = neighCells.get(i);
                    if (thisCell.getCid() != Integer.MAX_VALUE && thisCell.getLac() != Integer.MAX_VALUE) {
                        Log.d(TAG, new Date() + " \t" + thisCell.getLac() + " \t" + thisCell.getCid());
                        Tower tower = new Tower(thisCell.getCid(), thisCell.getLac(), (-113 + 2 * thisCell.getRssi()), Integer.valueOf(mcc), Integer.valueOf(mnc));
                        list.add(tower);
                        saveInfo(tower, "getNeighboring");
                    }
                } catch (Exception e) {
                    Log.d(TAG, new Date() + " \t" + e.getLocalizedMessage());
                }
            }
        }
        return list;
    }

    private void saveInfo(Tower tower, String func){
        StringBuilder out = new StringBuilder();
        out.append(tower.getCellId());
        out.append(";");
        out.append(tower.getLac());
        out.append(";");
        out.append(func);
        writeToFile(out.toString());
    }

    public List<Tower> getCellInfo() {
        List<Tower> towerList = new ArrayList<>();
        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        int phoneTypeInt = tel.getPhoneType();
//        String phoneType = phoneTypeInt == TelephonyManager.PHONE_TYPE_GSM ? "gsm" : phoneType;
//        phoneType = phoneTypeInt == TelephonyManager.PHONE_TYPE_CDMA ? "cdma" : phoneType;
        //from Android M up must use getAllCellInfo
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            towerList.addAll(getNeighboring(tel));
//            List<NeighboringCellInfo> neighCells = tel.getNeighboringCellInfo();
//            for (int i = 0; i < neighCells.size(); i++) {
//                try {
//                    JSONObject cellObj = new JSONObject();
//                    NeighboringCellInfo thisCell = neighCells.get(i);
//                    cellObj.put("cellId", thisCell.getCid());
//                    cellObj.put("lac", thisCell.getLac());
//                    cellObj.put("rssi", thisCell.getRssi());
//                    //cellList.put(cellObj);
//                } catch (Exception e) {
//                    Log.d(TAG, new Date() + " \t" + e.getLocalizedMessage());
//                }
//            }
        } else {
            @SuppressLint("MissingPermission") List<CellInfo> infos = tel.getAllCellInfo();
            if (infos != null) {
                for (int i = 0; i < infos.size(); ++i) {
                    try {
                        CellInfo info = infos.get(i);
                        Tower tower = null;
                        if (info instanceof CellInfoGsm) {
                            tower = getCellInfoGsm((CellInfoGsm) info);
                        } else if (info instanceof CellInfoLte) {
                            tower = getLteCell((CellInfoLte) info);
                        }
                        if (tower != null) {
                            towerList.add(tower);
                        }
                    } catch (Exception ex) {
                        Log.d(TAG, new Date() + " \t" + ex.getLocalizedMessage());
                    }
                }
            } else {
                Log.d(TAG, new Date() + " \t" + "getAllCellInfo return null");
                Tower tower = getGsmCellLocation(tel);
                if (tower != null) {
                    towerList.add(tower);
                } else {
                    towerList.addAll(getNeighboring(tel));
                }
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

    private Tower getCellInfoGsm(CellInfoGsm info) {
        CellSignalStrengthGsm gsm = info.getCellSignalStrength();
        CellIdentityGsm identityGsm = info.getCellIdentity();
        Log.d(TAG, "getLteCell:" + identityGsm.getCid() + " " + identityGsm.getLac());
        if (identityGsm.getCid() != Integer.MAX_VALUE && identityGsm.getLac() != Integer.MAX_VALUE) {
            Tower tower = new Tower(identityGsm.getCid(), identityGsm.getLac(), gsm.getDbm(), identityGsm.getMcc(), identityGsm.getMnc());
            saveInfo(tower, "getCellInfoGsm");
            return tower;
        }
        return null;
    }

    private Tower getLteCell(CellInfoLte info) {
        CellSignalStrengthLte lte = info.getCellSignalStrength();
        CellIdentityLte identityLte = info.getCellIdentity();
        Log.d(TAG, "getLteCell:" + identityLte.getTac() + " " + identityLte.getCi());
        if (identityLte.getCi() != Integer.MAX_VALUE && identityLte.getTac() != Integer.MAX_VALUE) {
            Tower tower = new Tower(identityLte.getCi(), identityLte.getTac(), lte.getDbm(), identityLte.getMcc(), identityLte.getMnc());
            saveInfo(tower, "getLteCell");
            return tower;
        }
        return null;
    }

    private Tower getGsmCellLocation(TelephonyManager tel) {
        @SuppressLint("MissingPermission") GsmCellLocation cellLocation = (GsmCellLocation) tel.getCellLocation();
        if (cellLocation != null) {
            Log.d(TAG, "getGsmCellLocation:" + cellLocation.getCid() + " " + cellLocation.getLac());
            if (cellLocation.getCid() != -1 && cellLocation.getLac() != -1) {
                String networkOperator = tel.getNetworkOperator();
                String mcc = networkOperator.substring(0, 3);
                String mnc = networkOperator.substring(3);
                Tower tower = new Tower(cellLocation.getCid(), cellLocation.getLac(), -1, Integer.valueOf(mcc), Integer.valueOf(mnc));
                saveInfo(tower, "getGsmCellLocation");
                return tower;
            }
        }
        return null;
    }

    private void saveTowerInfo(List<Tower> towerList) {
        for (Tower tower : towerList) {
            StringBuilder out = new StringBuilder();
            out.append(tower.getLac());
            out.append(";");
            out.append(tower.getCellId());
            writeToFile(out.toString());
            Log.d(TAG, out.toString());
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
                File file = new File(path, fileNameDdateFullFormat.format(new Date()) + ".txt");
                Long startSize = file.length();
                FileOutputStream outputStream = new FileOutputStream(file, true);
                String writeData = dateFullFormat.format(new Date()) + ";" + data + "\n";
                outputStream.write(writeData.getBytes());
                outputStream.close();
                Long endSize = file.length();
                //Log.d(TAG, "File diff is: " + (endSize - startSize));
            } catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        } else {
            Log.e(TAG, "is not ExternalStorageWritable");
        }
    }

    private void createIntent(int lac, int cellId, int dbm) {
        Intent intent = new Intent();
        intent.setAction(Constants.UPDATE_DATA);
        intent.putExtra(LAC, lac);
        intent.putExtra(CELL_ID, cellId);
        intent.putExtra(DBM, dbm);
        sendBroadcast(intent);
    }

    public class MyThread extends Thread {
        @Override
        public void run() {
            boolean isWork = true;
            do {
                try {
                    final List<Tower> towerList = getCellInfo();
                    if (towerList.size() > 0) {
                        //saveTowerInfo(towerList);
                        for (Tower tower : towerList) {
                            //Log.d(TAG, new Date() + " \t" + identityLte.getTac() + " \t" + identityLte.getCi());
                            createIntent(tower.getLac(), tower.getCellId(), tower.getDbm());
                            if (!tower.equals(lastTower)) {
                                closePrevTower();
                                lastTower = tower;
//                            if (!towers.isExistTower(tower)) {//todo Будет плохо, если опять вернемся в соту, например на обратной дороге.
                                tower.placeId = place.placeId;
                                towers.add(tower);
                                tower.towerId = App.db().collectDao().insert(tower);
                                Log.d(TAG, "Insert " + tower.toString());
//                            } else {
//                                Log.d(TAG, "Exist tower");
//                            }
                            } else {
                                lastTower.setEndDate(new Date());
                                App.db().collectDao().update(lastTower);
                                Log.d(TAG, "Old tower");
                            }
                        }
                    } else {
                        createIntent(0, 0, 0);
                        closePrevTower();
                        writeToFile(" - MyService getCellInfo is empty");
                    }
                    Thread.sleep(SLEEP_MC);
                } catch (InterruptedException e) {
                    isWork = false;
                    writeToFile(" - MyService onDestroy");
                }
            } while (isWork);
            stopSelf();
        }
    }

    //Из предыдущей точки мы точно выехали
    private void closePrevTower() {
        if (towers.size() > 0) {
            Tower prevTower = towers.getLast();
            prevTower.setEndDate(new Date());
            App.db().collectDao().update(prevTower);
        }
    }
}