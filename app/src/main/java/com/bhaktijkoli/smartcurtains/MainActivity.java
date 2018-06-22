package com.bhaktijkoli.smartcurtains;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bhaktijkoli.smartcurtains.utils.NsdUtils;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "MainActivity";
    private ProgressDialog progressDialog;
    private NsdUtils nsdUtils;
    private Boolean webSocketConnected = false;
    private WebSocket webSocket;
    private Button btnOpen;
    private Button btnClose;

    private OkHttpClient okHttpClient = new OkHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOpen = (Button) findViewById(R.id.btnOpen);
        btnClose = (Button) findViewById(R.id.btnClose);

        btnOpen.setOnClickListener(this);
        btnClose.setOnClickListener(this);

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
                Request request = new Request.Builder().url("ws://" + ipaddress + ":81").build();
                EchoWebSocketListener echoWebSocketListener = new EchoWebSocketListener();
                webSocket = okHttpClient.newWebSocket(request, echoWebSocketListener);
                okHttpClient.dispatcher().executorService().shutdown();
            }
        });
        nsdUtils.scan();
    }

    @Override
    public void onClick(View view) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "TOGGLE");
            if(view == btnOpen) {
                jsonObject.put("channel", "1");
            } else if(view == btnClose) {
                jsonObject.put("channel", "2");
            }
            webSocket.send(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private final class EchoWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            Log.i(TAG, "onOpen: ");
            webSocketConnected = true;
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            Log.i(TAG, "onMessage: " + text);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            webSocketConnected = false;
        }
    }

}
