package ru.crew4dev.celllogger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.crew4dev.celllogger.data.TowerInfo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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

import android.net.wifi.ScanResult;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final String TAG = "MainActivity";

    final int REQUEST_CODE_PERMISSION_ACCESS_COARSE_LOCATION = 10;
    final Handler myHandler = new Handler();

    private RecyclerView taskRecyclerView;
    private HistoryAdapter adapter;
    private List<TowerInfo> towerList = new ArrayList<>();
    private TowerInfo lastTower;

    WifiManager mainWifiObj;
    WifiScanReceiver wifiReciever;
    ListView list;
    String wifis[];

    Boolean permissionRequested = false;

    //private List<ScanInfo> scanInfos = new ArrayList<>();

    class ScanInfo {
        List<TowerInfo> towerList = new ArrayList<>();
        Date date;

        public ScanInfo() {
            date = new Date();
        }
    }

    private void updateGUI() {
        myHandler.post(myRunnable);
    }

    final Runnable myRunnable = new Runnable() {
        public void run() {
            loadData();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = findViewById(R.id.list);
//        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//        wifiReciever = new WifiScanReceiver();
//        mainWifiObj.startScan();
        // listening to single list item on click
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // selected item
                String ssid = ((TextView) view).getText().toString();
                //connectToWifi(ssid);
                Toast.makeText(MainActivity.this, "Wifi SSID : " + ssid, Toast.LENGTH_SHORT).show();

            }
        });

        taskRecyclerView = findViewById(R.id.historyRecyclerView);
        adapter = new HistoryAdapter();
        taskRecyclerView.setAdapter(adapter);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadData();

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateGUI();
            }
        }, 0, 10000);
    }

    private void loadData() {
        long st, en;
        st = System.nanoTime();
        if (adapter.getItemCount() > 0)
            adapter.clearItems();

        ScanInfo scanInfo = check();
        towerList.addAll(scanInfo.towerList);
        //adapter.setItems(scanInfo.towerList);
        adapter.setItems(towerList);
        adapter.notifyDataSetChanged();
    }

    private ScanInfo check() {
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            //getWifi();
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
                    //getWifi();
                    getCellInfo(this);
                } else {
                    // permission denied
                }
                return;
        }
    }

    private void getWifi() {
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        String name = wifiInfo.getSSID();
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

    protected void onPause() {
//        unregisterReceiver(wifiReciever);
        super.onPause();
    }

    protected void onResume() {
//        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    class WifiScanReceiver extends BroadcastReceiver {
        @SuppressLint("UseValueOf")
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
            wifis = new String[wifiScanList.size()];
            for (int i = 0; i < wifiScanList.size(); i++) {
                wifis[i] = ((wifiScanList.get(i)).toString());
            }
            String filtered[] = new String[wifiScanList.size()];
            int counter = 0;
            for (String eachWifi : wifis) {
                String[] temp = eachWifi.split(",");
                filtered[counter] = temp[0].substring(5).trim();//+"\n" + temp[2].substring(12).trim()+"\n" +temp[3].substring(6).trim();//0->SSID, 2->Key Management 3-> Strength
                counter++;
            }
            list.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item, R.id.label, filtered));
        }
    }
}
