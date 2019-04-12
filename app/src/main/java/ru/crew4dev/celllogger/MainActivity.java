package ru.crew4dev.celllogger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.crew4dev.celllogger.data.TowerInfo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final String TAG = "MainActivity";

    final int REQUEST_CODE_PERMISSION_ACCESS_COARSE_LOCATION = 10;
    final Handler myHandler = new Handler();

    private RecyclerView taskRecyclerView;
    private HistoryAdapter adapter;

    private static TowerInfo lastTower;

    Boolean permissionRequested = false;

    //private List<ScanInfo> scanInfos = new ArrayList<>();
    int i = 0;

    class ScanInfo {
        List<TowerInfo> towerList = new ArrayList<>();
        Date date;

        public ScanInfo() {
            date = new Date();
        }
    }

    private void UpdateGUI() {
        check();
        i++;
        //tv.setText(String.valueOf(i));
        myHandler.post(myRunnable);
    }

    final Runnable myRunnable = new Runnable() {
        public void run() {
            if (lastTower != null) {
                loadData();
//                cellId.setText(String.valueOf(lastTower.getCellId()));
//                lac.setText(String.valueOf(lastTower.getTac()));
//                dbm.setText(String.valueOf(lastTower.getDbm()));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskRecyclerView = findViewById(R.id.historyRecyclerView);
        adapter = new HistoryAdapter(this);
        taskRecyclerView.setAdapter(adapter);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));

//        cellId = findViewById(R.id.cellId);
//        lac = findViewById(R.id.lac);
//        dbm = findViewById(R.id.dbm);
        loadData();

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                UpdateGUI();
            }
        }, 0, 10000);
    }

    private void loadData() {
        long st, en;
        st = System.nanoTime();
        if (adapter.getItemCount() > 0)
            adapter.clearItems();

        ScanInfo scanInfo = check();
        //ScanInfo scanInfo = getCellInfo(this);
        adapter.setItems(scanInfo.towerList);
        adapter.notifyDataSetChanged();
    }

    private ScanInfo check() {
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            return getCellInfo(this);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE_PERMISSION_ACCESS_COARSE_LOCATION);
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCellInfo(this);
                } else {
                    // permission denied
                }
                return;
        }
    }

    public ScanInfo getCellInfo(Context ctx) {
        ScanInfo scanInfo = new ScanInfo();
        TelephonyManager tel = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        //JSONArray cellList = new JSONArray();
// Type of the network
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
                //ScanInfo scanInfo = new ScanInfo();
                for (int i = 0; i < infos.size(); ++i) {
                    try {
                        JSONObject cellObj = new JSONObject();
                        CellInfo info = infos.get(i);
                        if (info instanceof CellInfoGsm) {
                            CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                            CellIdentityGsm identityGsm = ((CellInfoGsm) info).getCellIdentity();
                            cellObj.put("cellId", identityGsm.getCid());
//                        cellObj.put("lac", identityGsm.getLac());
//                        cellObj.put("dbm", gsm.getDbm());
                            TowerInfo tower = new TowerInfo(identityGsm.getCid(), identityGsm.getLac(), gsm.getDbm());
                            lastTower = tower;
//                            cellId.setText(tower.getCellId());
//                            lac.setText(tower.getTac());
//                            dbm.setText(tower.getDbm());
                            scanInfo.towerList.add(tower);
                            Log.d(TAG, new Date() + " \t" + tower.toString());
                            //cellList.put(cellObj);
                        } else if (info instanceof CellInfoLte) {
                            CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                            CellIdentityLte identityLte = ((CellInfoLte) info).getCellIdentity();
//                        cellObj.put("cellId", identityLte.getCi());
//                        cellObj.put("tac", identityLte.getTac());
//                        cellObj.put("dbm", lte.getDbm());
                            TowerInfo tower = new TowerInfo(identityLte.getCi(), identityLte.getTac(), lte.getDbm());
                            lastTower = tower;
//                            cellId.setText(tower.getCellId());
//                            lac.setText(tower.getTac());
//                            dbm.setText(tower.getDbm());
                            Log.d(TAG, new Date() + " \t" + tower.toString());
                            scanInfo.towerList.add(tower);
                            //cellList.put(cellObj);
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
}
