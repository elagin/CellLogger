package ru.crew4dev.celllogger.gui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.crew4dev.celllogger.App;
import ru.crew4dev.celllogger.Constants;
import ru.crew4dev.celllogger.MyService;
import ru.crew4dev.celllogger.R;
import ru.crew4dev.celllogger.data.Place;
import ru.crew4dev.celllogger.data.Tower;
import ru.crew4dev.celllogger.data.TowerList;

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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static ru.crew4dev.celllogger.Constants.PLACE_ID;

public class TowerActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final String TAG = "TowerActivity";

    final int REQUEST_CODE_PERMISSION_ACCESS_COARSE_LOCATION = 10;

    final Handler myHandler = new Handler();

    private RecyclerView taskRecyclerView;
    private TextView textViewUpdateTime;
    private TextView currentTower;
    private Button buttonStart;
    private Button buttonStop;
    private EditText editPlaceName;

    private MyReceiver myReceiver;
    private TowerAdapter adapter;
    private TowerList towerList = new TowerList();
    private Menu menu;

    WifiManager mainWifiObj;
    WifiScanReceiver wifiReciever;
    //WifiLevelReceiver receiver;
    String wifis[];
    private Place place;

    Boolean permissionRequested = false;

//    private void updateGUI() {
//        myHandler.post(myRunnable);
//    }

    final Runnable myRunnable = new Runnable() {
        public void run() {
            //loadData();
        }
    };

    @Override
    protected void onStart() {
        //Register BroadcastReceiver to receive event from our service
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
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(Constants.UPDATE_DATA)) {
                    int lac = intent.getIntExtra(MyService.LAC, 0);
                    int cell_id = intent.getIntExtra(MyService.CELL_ID, 0);
                    int dbm = intent.getIntExtra(MyService.DBM, 0);
                    //Tower tower = new Tower(cell_id, lac, dbm, 0, 0);
                    //Log.d(TAG, tower.toString());
                    loadData();
                    textViewUpdateTime.setText(Constants.timeFormat.format(new Date()));
                    StringBuilder current = new StringBuilder();
                    current.append("cellId: " + String.valueOf(cell_id));
                    current.append(" lac: " + String.valueOf(lac));
                    current.append(" " + String.valueOf(dbm) + "dB");
                    currentTower.setText(current.toString());
                } else if (intent.getAction().equals(Constants.WORK_DONE)) {
                    buttonStart.setEnabled(true);
                    buttonStop.setEnabled(false);
                }
            }
            Toast.makeText(TowerActivity.this, "Triggered by Service: " + intent.getAction(), Toast.LENGTH_LONG).show();
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
//                Toast.makeText(TowerActivity.this, "Wifi SSID : " + ssid, Toast.LENGTH_SHORT).show();
//
//            }
//        });

        taskRecyclerView = findViewById(R.id.historyRecyclerView);
        adapter = new TowerAdapter(this);
        taskRecyclerView.setAdapter(adapter);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        textViewUpdateTime = findViewById(R.id.textViewUpdateTime);
        currentTower = findViewById(R.id.currentTower);

        buttonStart = findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(v -> toStart());
        buttonStop = findViewById(R.id.buttonStop);
        buttonStop.setOnClickListener(v -> toStop());

        editPlaceName = findViewById(R.id.editPlaceName);

        Intent intent = getIntent();
        if (intent != null) {
            if (getIntent().hasExtra(PLACE_ID)) {
                place = App.db().collectDao().getPlace(intent.getLongExtra(PLACE_ID, 0));
                editPlaceName.setText(place.getName());
            }
        }
//        Timer myTimer = new Timer();
//        myTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                updateGUI();
//            }
//        }, 0, 10000);
    }

    private void loadData() {
        Log.d(TAG, "loadData");
        if (place != null) {
            towerList.addAll(App.db().collectDao().getTowers(place.placeId));
            adapter.clearItems();
            adapter.setItems(towerList.getTowers());
            adapter.notifyDataSetChanged();
        }
    }

    private void toStart() {
        checkPermissions();
    }

    private void toStartService() {
        buttonStart.setEnabled(false);
        buttonStop.setEnabled(true);
        Intent intent = new Intent(TowerActivity.this, MyService.class);
        place = new Place();
        if (!editPlaceName.getText().toString().isEmpty()) {
            place.setName(editPlaceName.getText().toString());
        }
        place.placeId = App.db().collectDao().insert(place);
        Log.d(TAG, "Insert " + place.toString());
        intent.putExtra(PLACE_ID, place.placeId);
        startService(intent);
    }

    private void toStop() {
        stopService(new Intent(TowerActivity.this, MyService.class));
        if (place != null) {
            place.endDate = new Date();
            place.setName(editPlaceName.getText().toString());
            App.db().collectDao().update(place);
            Log.d(TAG, "update : " + place.toString());
//            StringBuilder out = new StringBuilder();
//            out.append(dateFullFormat.format(new Date()));
//            out.append(" - MyService onDestroy");
//            writeToFile(out.toString());
        }
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
        loadData();
        if (place != null) {
            if (place.endDate == null) {
                buttonStart.setEnabled(false);
                buttonStop.setEnabled(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.tower_list_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        List<Long> selectedList = new ArrayList<>();
        for (Tower tower : towerList.getTowers())
            if (tower.isSelected())
                selectedList.add(tower.towerId);
        return super.onOptionsItemSelected(item);
    }

    public void enableActionMark(boolean value) {
        menu.findItem(R.id.action_mark).setVisible(value);
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
