package com.bhaktijkoli.smartcurtains.utils;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.net.InetAddress;

/**
 * Created by Bhaktij on 22/06/18.
 * Github: https://github.com/bhaktijkoli
 * Email: bhaktijkoli121@gmail.com
 */

public class NsdUtils {
    private Context context;
    private NsdManager mNsdManager;
    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager.ResolveListener mResolveListener;
    private NsdServiceInfo mServiceInfo;
    private NsdUtilListner nsdUtilListner;

    private static final String SERVICE_TYPE = "_ws._tcp.";
    private static final String SERVICE_NAME = "smartcurtains";


    public NsdUtils(Context context) {
        this.context = context;
        mNsdManager = (NsdManager)(context.getSystemService(Context.NSD_SERVICE));
        initializeResolveListener();
        initializeDiscoveryListener();
    }

    public void scan() {
        mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    public void setNsdUtilListner(NsdUtilListner nsdUtilListner) {
        this.nsdUtilListner = nsdUtilListner;
    }

    public void dismiss() {
        if(mDiscoveryListener != null) mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    private void initializeDiscoveryListener() {
        mDiscoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d("NSD", "Discovery Started");
                if(nsdUtilListner != null) nsdUtilListner.onScanStart();
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found!  Do something with it.
                String name = service.getServiceName();
                String type = service.getServiceType();
                Log.d("NSD", "Service Name=" + name);
                Log.d("NSD", "Service Type=" + type);
                if (type.equals(SERVICE_TYPE) && name.contains(SERVICE_NAME)) {
                    Log.d("NSD", "Service Found @ '" + name + "'");
                    mNsdManager.resolveService(service, mResolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                nsdUtilListner.onScanStop();
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.d("NSD", "Discovery Stopped");
                if(nsdUtilListner != null) nsdUtilListner.onScanStop();
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.d("NSD", "On Start Discovery Failed");
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.d("NSD", "On Stop Discovery Failed");
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }

    private void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e("NSD", "Resolve failed" + errorCode);
                mNsdManager.stopServiceDiscovery(mDiscoveryListener);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                mServiceInfo = serviceInfo;
                InetAddress host = mServiceInfo.getHost();
                String address = host.getHostAddress();
                Log.d("NSD", "Resolved address = " + address);
                if(nsdUtilListner != null) nsdUtilListner.onDeviceFound(address);
                mNsdManager.stopServiceDiscovery(mDiscoveryListener);
            }
        };
    }

    public interface NsdUtilListner {
        public void onScanStart();
        public void onScanStop();
        public void onDeviceFound(String ipaddress);
    }

}
