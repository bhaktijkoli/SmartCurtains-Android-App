package com.bhaktijkoli.smartcurtains;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bhaktijkoli.smartcurtains.utils.NsdUtils;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private NsdUtils nsdUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nsdUtils = new NsdUtils(this);
        nsdUtils.setNsdUtilListner(new NsdUtils.NsdUtilListner() {
            @Override
            public void onScanStart() {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Searching for device...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
            }

            @Override
            public void onScanStop() {
                progressDialog.dismiss();
            }

            @Override
            public void onDeviceFound(String ipaddress) {
                progressDialog.dismiss();
            }
        });
    }
}
