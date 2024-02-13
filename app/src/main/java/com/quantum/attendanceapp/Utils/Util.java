package com.quantum.attendanceapp.Utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Util {

    public static final String USER = "User";
    public static final String ID = "ID";
    public static final String MODE = "Mode";
    public static final int EDIT_MODE = 2;
    public static final int ADD_MODE = 1;
    private static final String DECI_FORMAT = "#.00";
    private static boolean isNetworkAvail = true;
    private static final long _24Hrs = 1000 * 60 * 60 * 24;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
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

    public static long compareDate(String date1, String date2) {
        LocalDate localDate1 = LocalDate.parse(date1, formatter);
        LocalDate localDate2 = LocalDate.parse(date2, formatter);
        Instant instant1 = localDate1.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant instant2 = localDate2.atStartOfDay(ZoneOffset.UTC).toInstant();
        return ChronoUnit.DAYS.between(instant1, instant2);
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

    public static String getValue(EditText et) {
        return et.getText().toString();
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

    public static List<String> getDateList(String sd, String ed){
        List<String> retList = new ArrayList<>();

        LocalDate startDate = LocalDate.parse(sd, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDate endDate = LocalDate.parse(ed, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            dateList.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        for (LocalDate date : dateList) {
            retList.add(date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }
        return retList;
    }

    public static boolean isGivenDate(String d,String day){
        String[] date = d.split("-");
        int year = Integer.parseInt(date[0]);
        int month = Integer.parseInt(date[1]);
        int d1 = Integer.parseInt(date[2]);
        return isGivenDay(year,month,d1,day);
    }

    public static boolean isGivenDay(int year, int month, int day, String givenDay ) {
        givenDay = givenDay.toUpperCase();
        DayOfWeek givenD = DayOfWeek.valueOf(givenDay);
        LocalDate givenDate = LocalDate.of(year, month, day);
        return givenDate.getDayOfWeek() == givenD;
    }

    public static LocalDate stringToLocalDate(String d1) {
        return LocalDate.parse(d1, formatter);
    }

    public static long stringToTimeStamp(String d1) {
        LocalDate localDate = stringToLocalDate(d1);
        LocalDateTime localDateTime = localDate.atStartOfDay();
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        return instant.toEpochMilli();
    }


    public static void sendPostRequest(String url, String requestData) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");

        con.setRequestProperty("Content-Type", "application/json");

        con.setDoOutput(true);
        con.setDoInput(true);

        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            byte[] postDataBytes = requestData.getBytes();
            wr.write(postDataBytes);
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
    }

    public static boolean isLoginSessionValid(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("Login_Details", MODE_PRIVATE);
        long loginTime = sharedPref.getLong("loginTime", 0);
        long currentTime = System.currentTimeMillis();
        long diff = currentTime - loginTime;
        if(diff >= _24Hrs)
            return false;
        return true;
    }

    public static boolean isGpsEnabled(Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;
        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return gpsEnabled || networkEnabled;
    }



}
