package ru.crew4dev.celllogger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
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
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    Boolean permissionRequested = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        check();
    }

    final int REQUEST_CODE_PERMISSION_ACCESS_COARSE_LOCATION = 10;

    private void check() {
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            JSONArray array = getCellInfo(this);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_PERMISSION_ACCESS_COARSE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCellInfo(this);
                } else {
                    // permission denied
                }
                return;
        }
    }

    public JSONArray getCellInfo(Context ctx) {
        TelephonyManager tel = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        JSONArray cellList = new JSONArray();
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
                    cellList.put(cellObj);
                } catch (Exception e) {
                }
            }

        } else {
            List<CellInfo> infos = tel.getAllCellInfo();
            for (int i = 0; i < infos.size(); ++i) {
                try {
                    JSONObject cellObj = new JSONObject();
                    CellInfo info = infos.get(i);
                    if (info instanceof CellInfoGsm) {
                        CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                        CellIdentityGsm identityGsm = ((CellInfoGsm) info).getCellIdentity();
                        cellObj.put("cellId", identityGsm.getCid());
                        cellObj.put("lac", identityGsm.getLac());
                        cellObj.put("dbm", gsm.getDbm());
                        cellList.put(cellObj);
                    } else if (info instanceof CellInfoLte) {
                        CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                        CellIdentityLte identityLte = ((CellInfoLte) info).getCellIdentity();
                        cellObj.put("cellId", identityLte.getCi());
                        cellObj.put("tac", identityLte.getTac());
                        cellObj.put("dbm", lte.getDbm());
                        cellList.put(cellObj);
                    }

                } catch (Exception ex) {

                }
            }
        }
        return cellList;
    }
}
