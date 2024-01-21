package com.quantum.attendanceapp.Utils;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.widget.EditText;

import androidx.annotation.NonNull;

import java.text.DecimalFormat;
import java.util.Locale;

public class Util {

    public static final String USER = "User";
    public static final String ID = "ID";
    public static final String MODE = "Mode";

    private static final String DECI_FORMAT = "#.00";

    public static final int EDIT_MODE = 2;
    public static final int ADD_MODE = 1;

    private static boolean isNetworkAvail = true;
    private static final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            isNetworkAvail = true;
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            isNetworkAvail = false;
        }

        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            isNetworkAvail = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);

        }
    };

    public static boolean checkNetwork(ConnectivityManager connectivityManager) {
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();

        connectivityManager.requestNetwork(networkRequest, networkCallback);
        return isNetworkAvail;
    }

    public static String generateId(String type, String uniqueId) {
        long timestamp = System.currentTimeMillis();
        uniqueId = uniqueId.replace(" ", "");
        return type + timestamp + uniqueId;
    }

    public static String getDisplayDate(int y, int m, int d) {
        String t1, t2;
        if (d < 10)
            t1 = "0" + d;
        else
            t1 = String.valueOf(d);
        if (m <= 10)
            t2 = "0" + (m + 1);
        else
            t2 = String.valueOf(m + 1);
        return t1 + "-" + t2 + "-" + y;
    }

    public static String getDisplayTime(int hour, int min) {
        String h, m;
        if (hour < 10)
            h = "0" + hour;
        else
            h = String.valueOf(hour);
        if (min < 10)
            m = "0" + min;
        else
            m = String.valueOf(min);

        // on below line we are setting selected time in our text view.
        return h + ":" + m;
    }

    public static String getFormattedString(Object s) {
        String format = "%.2f";
        try {
            return String.format(Locale.getDefault(), format, s);
        } catch (Exception e) {
            return String.valueOf(s);
        }

    }

    public static double formatDecimal(double d) {
        DecimalFormat decimalFormat = new DecimalFormat(DECI_FORMAT);
        try {
            return Double.parseDouble(decimalFormat.format(d));
        } catch (Exception e) {
            return d;
        }
    }

    public static boolean checkEt(EditText et) {
        return et.getText().toString().trim().isEmpty();
    }

    public static double getDoubleValue(EditText editText) {
        String text = editText.getText().toString().trim();
        try {
            return text.isEmpty() ? 0 : Double.parseDouble(text);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }





}
