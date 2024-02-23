package com.quantum.attendanceapp.Utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opencsv.CSVWriter;
import com.quantum.attendanceapp.model.ReportData;
import com.quantum.attendanceapp.model.TimeData;
import com.quantum.attendanceapp.model.UserData;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class GenerateReport {
    private final Context context;

    private List<UserData> userDataList;

    private Map<String, List<TimeData>> userReportData = new HashMap<>();

    public GenerateReport(Context context) {
        this.context = context;
    }

    public void genReport() {
        userDataList = new ArrayList<>();
        getUserData();
    }

    private void getUserData() {
        FirebaseFirestore.getInstance().collection("Data").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot result = task.getResult();
                        userDataList = result.toObjects(UserData.class);
                        getTimeData();
                    }
                });
    }

    private void getTimeData() {
        String date = getDate();
        String monthYear = date.substring(date.indexOf("-") + 1);
        monthYear = monthYear.replace("-", "");

        for (int i = 0; i < userDataList.size(); i++) {
            UserData userData = userDataList.get(i);
            int finalI = i;
            FirebaseFirestore.getInstance().collection("TimeData").document(userData.getUserId())
                    .collection(monthYear).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<TimeData> timeDataList = task.getResult().toObjects(TimeData.class);
                                userReportData.put(userData.getUserId(), timeDataList);
                                if (finalI == userDataList.size() - 1) {
                                    generateRep();
                                }
                            }
                        }
                    });
        }
    }

    private void generateRep() {
        int size = userReportData.size();
        Set<String> keys = userReportData.keySet();
        UserData userData = null;
        String weekOff = "";
        List<ReportData> reportDataList = new ArrayList<>();
        for (String key : keys) {
            List<TimeData> userTimeList = userReportData.get(key);
            for (UserData ud : userDataList) {
                if (ud.getUserId().equals(key))
                    userData = ud;
            }
            if (userData != null) {
                weekOff = userData.getWeeklyOff();
                int noOfLeaves = 0;
                int presentDays = 0;
                if (userTimeList != null) {
                    for (TimeData timeData : userTimeList) {
                        if (!timeData.isLeave()) {
                            presentDays++;
                        } else {
                            noOfLeaves++;
                        }
                    }
                }
                String empId = userData.getEmpId();
                String name = userData.getUserName();
                String presentDaysStr = String.valueOf(presentDays);
                String leaveDaysStr = String.valueOf(noOfLeaves);
                ReportData reportData = new ReportData(empId, name, presentDaysStr, leaveDaysStr);
                reportDataList.add(reportData);
            }
        }
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String jsonData = gson.toJson(reportDataList);
        JsonArray jsonArray = JsonParser.parseReader(new StringReader(jsonData)).getAsJsonArray();
        String[] colOrder = {"empId", "empName", "presentDays", "leavesTaken"};
        saveCsvToFile(convertJsonToCsv(jsonArray, colOrder));
    }

    private String convertJsonToCsv(JsonArray jsonArray, String[] columnOrder) {

        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer);
;
        String[] colOrder = {"EmpId", "EmpName", "Present Days", "Leave Taken"};
        csvWriter.writeNext(colOrder);
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            List<String> rowData = new ArrayList<>();
            for (String columnName : columnOrder) {
                rowData.add(jsonObject.has(columnName) ? jsonObject.get(columnName).getAsString() : "");
            }
            csvWriter.writeNext(rowData.toArray(new String[0]));
        }

        try {
            csvWriter.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return writer.toString();
    }


    private void saveCsvToFile(String csvString) {
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String s = Util.getCurrentDateTime()[0];
            s = s.replace("-", "");
            File file = new File(dir, "Attendance_Report_" + s + ".xlsx");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(csvString);
            fileWriter.close();
            Toast.makeText(context, "File generated", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String getDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }


}
