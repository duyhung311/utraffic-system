package com.hcmut.admin.utrafficsystem.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.util.Log;

import androidx.annotation.NonNull;

public class NetworkStateManager {
    private boolean connected = false;
    private boolean networkAvailable = false;

    private static NetworkStateManager networkStateManager;

    private NetworkStateManager() {}

    public static NetworkStateManager getInstance(Context context) {
        if (networkStateManager == null) {
            networkStateManager = new NetworkStateManager();
            networkStateManager.registerNetworkStateReceiver(context);
        }
        return networkStateManager;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isNetworkAvailable() {
        return networkAvailable;
    }

    private void registerNetworkStateReceiver(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conn != null) {
            NetworkRequest request = new NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                    .build();
            conn.registerNetworkCallback(request, new NetworkStateCallback());

            // check network is capability
            Network network = conn.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities networkCapabilities = conn.getNetworkCapabilities(network);
                if (networkCapabilities != null) {
                    checkConnected(networkCapabilities);
                }
            }
        }
    }

    /*
     * Set up connect status
     */
    private void setupConnected(boolean status) {
        connected = status;
    }

    private void checkConnected(NetworkCapabilities networkCapabilities) {
        // If network is connected then:
        //      + set isConnected = true
        //      + hide network state banner
        //      + push data from local db to server
        //  Else: isConnected = false
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            setupConnected(true);
        } else {
            setupConnected(false);
        }
    }

    private class NetworkStateCallback extends ConnectivityManager.NetworkCallback {
        @Override
        public void onAvailable(@NonNull Network network) {
            Log.e("network", "available");
            networkAvailable = true;
        }

        @Override
        public void onLost(@NonNull Network network) {
            Log.e("network", "lost");
            networkAvailable = false;
            setupConnected(false);
        }

        /*
         * Just run when wifi of 3G is turnning on
         * When turn of wifi, this method is not running (only 'onLost()' run)
         */
        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
            Log.e("network", "capability");
            checkConnected(networkCapabilities);
        }
    }
}
