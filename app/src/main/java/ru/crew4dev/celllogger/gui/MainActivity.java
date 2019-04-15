package ru.crew4dev.celllogger.gui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.crew4dev.celllogger.Constants;
import ru.crew4dev.celllogger.MyService;
import ru.crew4dev.celllogger.R;
import ru.crew4dev.celllogger.data.Tower;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.net.wifi.ScanResult;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static ru.crew4dev.celllogger.gui.HistoryAdapter.dateFullFormat;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final String TAG = "MainActivity";

    final int REQUEST_CODE_PERMISSION_ACCESS_COARSE_LOCATION = 10;

    final Handler myHandler = new Handler();

    private RecyclerView taskRecyclerView;
    private TextView textViewUpdateTime;
    private Button buttonStart;
    private Button buttonStop;
    private EditText editPlaceName;

    private MyReceiver myReceiver;

    private HistoryAdapter adapter;
    private List<Tower> towerList = new ArrayList<>();

    WifiManager mainWifiObj;
    WifiScanReceiver wifiReciever;
    //WifiLevelReceiver receiver;
    String wifis[];

    Boolean permissionRequested = false;

    static class ScanInfo {
        List<Tower> towerList = new ArrayList<>();
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
            //loadData();
        }
    };

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub

        //Register BroadcastReceiver
        //to receive event from our service
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.UPDATE_DATA);
        intentFilter.addAction(Constants.WORK_DONE);
        registerReceiver(myReceiver, intentFilter);
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(myReceiver);
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Constants.UPDATE_DATA)) {
                int lac = intent.getIntExtra(MyService.LAC, 0);
                int cell_id = intent.getIntExtra(MyService.CELL_ID, 0);
                Tower tower = new Tower(cell_id, lac, 0, 0, 0);
                Log.d(TAG, tower.toString());
                towerList.add(tower);
                adapter.clearItems();
                adapter.setItems(towerList);
                adapter.notifyDataSetChanged();
                textViewUpdateTime.setText(dateFullFormat.format(new Date()));
            } else if (intent.getAction().equals(Constants.WORK_DONE)) {
                buttonStart.setEnabled(true);
                buttonStop.setEnabled(false);
            } else {

            }
            Toast.makeText(MainActivity.this, "Triggered by Service: " + intent.getAction(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //checkPermissions();

//        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//        wifiReciever = new WifiScanReceiver();
//        mainWifiObj.startScan();
        // listening to single list item on click
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // selected item
//                String ssid = ((TextView) view).getText().toString();
//                //connectToWifi(ssid);
//                Toast.makeText(MainActivity.this, "Wifi SSID : " + ssid, Toast.LENGTH_SHORT).show();
//
//            }
//        });

        taskRecyclerView = findViewById(R.id.historyRecyclerView);
        adapter = new HistoryAdapter();
        taskRecyclerView.setAdapter(adapter);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        textViewUpdateTime = findViewById(R.id.textViewUpdateTime);

        buttonStart = findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(v -> toStart());
        buttonStop = findViewById(R.id.buttonStop);
        buttonStop.setOnClickListener(v -> toStop());

        editPlaceName = findViewById(R.id.editPlaceName);

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateGUI();
            }
        }, 0, 10000);
    }

    private void toStart() {
        checkPermissions();
    }

    private void toStartService() {
        buttonStart.setEnabled(false);
        buttonStop.setEnabled(true);
        startService(new Intent(MainActivity.this, MyService.class));
    }

    private void toStop() {
        stopService(new Intent(MainActivity.this, MyService.class));
    }

    private void checkPermissions() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            toStartService();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_ACCESS_COARSE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    toStartService();
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

//        final SubscriptionManager subscriptionManager = SubscriptionManager.from(this);
//        final List<SubscriptionInfo> activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
//        for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
//            final CharSequence carrierName = subscriptionInfo.getCarrierName();
//            final CharSequence displayName = subscriptionInfo.getDisplayName();
//            final int mcc = subscriptionInfo.getMcc();
//            final int mnc = subscriptionInfo.getMnc();
//            final String subscriptionInfoNumber = subscriptionInfo.getNumber();
//        }

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
            //list.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item, R.id.label, filtered));
        }
    }
}
