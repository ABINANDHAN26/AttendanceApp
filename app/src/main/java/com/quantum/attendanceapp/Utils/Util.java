package com.quantum.attendanceapp.Utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.StrictMode;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {

    private static final long _1Hrs = 1000 * 60 * 60;

    private static final int TIMEOUT_MILLISECONDS = 5000;
    private static final String SHARED_PREF_NAME = "EMAIL_DATA";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

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



    public static boolean checkEt(EditText et) {
        return et.getText().toString().trim().isEmpty();
    }

    public static String getValue(EditText et) {
        return et.getText().toString().trim();
    }


    public static List<String> getDateList(String sd, String ed) {
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

    public static boolean isGivenDay(int year, int month, int day, String givenDay) {
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

    public static String[] getDatesInMonth(int month) {

        if (month < 1 || month > 12) {
            return new String[0];
        }

        int daysInMonth;
        if (month == 2) {
            daysInMonth = 29; // Assuming non-leap year for simplicity
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            daysInMonth = 30;
        } else {
            daysInMonth = 31;
        }

        String[] datesArray = new String[daysInMonth];
        for (int i = 0; i < daysInMonth; i++) {
            datesArray[i] = String.valueOf(i + 1);
        }
        return datesArray;
    }

    public static void storeEmailData(Context context, Map<String, String> data) {
        Log.i("TAG", "storeEmailData: "+data);
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("emailServerAddress", data.get("emailServerAddress"));
        editor.putString("sender_email", data.get("sender_email"));
        editor.putString("sender_password", data.get("sender_password"));
        editor.apply();
    }


    public static Map<String, String> getEmailData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String address = sharedPreferences.getString("emailServerAddress", null);
        String email = sharedPreferences.getString("sender_email", null);
        String pw = sharedPreferences.getString("sender_password", null);
        Map<String,String> data = new HashMap<>();
        data.put("emailServerAddress",address);
        data.put("sender_email",email);
        data.put("sender_password",pw);
        return data;
    }

    public static boolean checkAddress(String serverUrl) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        boolean retValue = false;
        try {
//            serverUrl = "http://192.168.98.165:5000/ping";
            URL url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT_MILLISECONDS); // Set connection timeout
            connection.connect();
            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            Log.e("TAG", "checkAddress: ",e );
        }

        return retValue;


    }
    public static String[] parseURL(String urlString) {
        Log.i("TAG", "parseURL: "+urlString);
        String[] retValue = new String[2];
        try {
            URL url = new URL(urlString);
            String ipAddress = url.getHost();
            int port = url.getPort();
            retValue[0] = ipAddress;
            retValue[1] = String.valueOf(port);
        } catch (MalformedURLException e) {
            Log.e("TAG", "parseURL: ",e );
            return null;
        }
        return retValue;
    }
    public static void sendPostRequest(String requestData,String url) throws Exception {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if(url == null || url.trim().isEmpty())
            return;
        HttpURLConnection con = getHttpURLConnection(requestData, url);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
    }

    @NonNull
    private static HttpURLConnection getHttpURLConnection(String requestData, String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setConnectTimeout(TIMEOUT_MILLISECONDS); // Set connection timeout
        con.setRequestProperty("Content-Type", "application/json");

        con.setDoOutput(true);
        con.setDoInput(true);

        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            byte[] postDataBytes = requestData.getBytes();
            wr.write(postDataBytes);
        }
        return con;
    }

    public static boolean isLoginSessionValid(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("Login_Details", MODE_PRIVATE);
        long loginTime = sharedPref.getLong("loginTime", 0);
        long currentTime = System.currentTimeMillis();
        long diff = currentTime - loginTime;
        return diff < _1Hrs;
    }

    public static String[] getCurrentDateTime() {
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        return new String[]{formatDate(localDate), localTime.toString()};
    }

    public static String formatDate(LocalDate localDate) {
        return localDate.format(formatter);
    }

    public static boolean isGpsEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;
        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            Log.e("TAG", "isGpsEnabled: ",e );
        }

        try {
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            Log.e("TAG", "isNetworkEnabled: ",e );
        }

        return gpsEnabled || networkEnabled;
    }


}
