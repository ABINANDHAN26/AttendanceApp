package com.quantum.attendanceapp.Utils;

import android.content.Context;
import android.os.Environment;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.quantum.attendanceapp.model.ReportData;
import com.quantum.attendanceapp.model.TimeData;
import com.quantum.attendanceapp.model.UserData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
    private final String monthYear;

    private final boolean onlyToday;
    private List<UserData> userDataList;

    private Map<String, List<TimeData>> userReportData = new HashMap<>();
    private static String TAG = "TAG";
    public GenerateReport(Context context, String monthYear, boolean onlyToday) {
        this.context = context;
        this.monthYear = monthYear;
        this.onlyToday = onlyToday;
    }

    public void genReport() {
        userDataList = new ArrayList<>();
        getUserData();
    }

    private void getUserData() {
        Log.i(TAG, "getUserData: ");
        FirebaseFirestore.getInstance().collection("Data").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "getUserData: got user data");
                        QuerySnapshot result = task.getResult();
                        userDataList = result.toObjects(UserData.class);
                        getTimeData();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: error gettinhg user data");
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.printStackTrace(pw);
                        String stackTraceString = sw.toString();
                        saveErrorToTextFile("" + e.getMessage() + "\n" + stackTraceString + "\n");
                        e.printStackTrace();
                    }
                });
    }

    private void getTimeData() {
        Log.i(TAG, "getTimeData: ");
        String date = getDate();
//        String monthYear = date.substring(date.indexOf("-") + 1);
//        monthYear = monthYear.replace("-", "");

        for (int i = 0; i < userDataList.size(); i++) {
            UserData userData = userDataList.get(i);
            int finalI = i;
            FirebaseFirestore.getInstance().collection("TimeData").document(userData.getUserId())
                    .collection(monthYear).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "getTimeData: got time data");
                            List<TimeData> timeDataList = task.getResult().toObjects(TimeData.class);
                            userReportData.put(userData.getUserName(), timeDataList);

                            if (finalI == userDataList.size() - 1) {
                                try {
                                    if(onlyToday)
                                        todayReport();
                                    else
                                        generateRep();
                                } catch (FileNotFoundException e) {
                                    Log.i(TAG, "getTimeData: errir getting tiome data");
                                    StringWriter sw = new StringWriter();
                                    PrintWriter pw = new PrintWriter(sw);
                                    e.printStackTrace(pw);
                                    String stackTraceString = sw.toString();
                                    saveErrorToTextFile("" + e.getMessage() + "\n" + stackTraceString + "\n");
                                    e.printStackTrace();
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    });
        }

    }

    private void generateRep() throws FileNotFoundException {
        Log.i(TAG, "generateRep: generating report");
        String[] currentDateTime = Util.getCurrentDateTime();
        String date = currentDateTime[0].replace("-", "_");
        String fileName = "Report_" + date + ".pdf";
        String pdfFilePath = "";
        pdfFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName;
        PdfWriter writer = new PdfWriter(pdfFilePath);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);
        Set<String> keys = userReportData.keySet();
        if (keys.isEmpty())
            return;
        Table table = new Table(5);
        table.setTextAlignment(TextAlignment.CENTER);
        table.setFixedLayout();
        table.addCell(createCell("Emp No", true));
        table.addCell(createCell("Emp Name", true));
        table.addCell(createCell("Present Days", true));
        table.addCell(createCell("Absent Days", true));
        table.addCell(createCell("Salary", true));
        Log.i(TAG, "generateRep: data size: "+keys.size());
        for (String key : keys) {
            List<TimeData> tempTimeList = userReportData.get(key);
            if (tempTimeList == null || tempTimeList.isEmpty())
                continue;
            String empNo = "";
            for (UserData userData : userDataList) {
                if (userData.getUserName().equals(key))
                    empNo = userData.getEmpId();
            }
            int leaveCount = 0;
            int presentCount = 0;
            for (TimeData timeData : tempTimeList) {
                if (timeData.isLeave()) {
                    leaveCount++;
                } else {
                    presentCount++;
                }
            }
            table.addCell(createCell(empNo, false));
            table.addCell(createCell(key, false));
            table.addCell(createCell(presentCount + "", false));
            table.addCell(createCell(leaveCount + "", false));
            double amount = presentCount * 600.0;
            table.addCell(createCell(amount + "", false));
        }
        document.add(table);
        document.close();
        Toast.makeText(context, "Report saved in " + pdfFilePath, Toast.LENGTH_SHORT).show();
    }

    private static Cell createCell(String content, boolean isHeader) {
        if (content == null)
            content = "";
        Cell cell = new Cell();
        cell.add(new Paragraph(content));
        if (isHeader) {
            cell.setBold();
        }
        return cell;
    }

    private void saveErrorToTextFile(String errorMessage) {
        try {

            String documentsDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();
            String fileName = "error_log.txt";
            String filePath = documentsDirectoryPath + File.separator + fileName;
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            FileWriter writer = new FileWriter(file, true); // Use "true" to append to the file
            writer.append(errorMessage);
            writer.append("\n"); // Add a newline for readability
            writer.close();
            Log.i("TAG", "Error message saved to " + filePath);
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTraceString = sw.toString();
            e.printStackTrace();
            Log.e("TAG", "Error saving error message: " + e.getLocalizedMessage());
        }
    }

    private String getDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    public void todayReport(){
        Log.i("TAG", "todayReport: ");
        Map<String,List<String>> dailyRep = new HashMap<>();
        String[] currentDateTime = Util.getCurrentDateTime();
        if(userReportData == null || userReportData.size() == 0)
            return;
        Log.i("TAG", "todayReport: rep: "+userReportData);
        Set<String> keySet = userReportData.keySet();
        for(String key:keySet){
            List<TimeData> timeDataList = userReportData.get(key);
            for(TimeData timeData:timeDataList){
                String date = timeData.getDate();
                if (date.trim().equals(currentDateTime[0].trim()))
                {
                    List<String> temp = new ArrayList<>();
                    if(!timeData.isLeave()) {
                        temp.add(timeData.getInTime());
                        temp.add(timeData.getOutTime());
                    }else{
                        temp.add("Leave");
                        temp.add("Leave");
                    }
                    dailyRep.put(key,temp);
                }
            }
        }
        try {
            generateRep(dailyRep);
        } catch (FileNotFoundException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTraceString = sw.toString();
            saveErrorToTextFile("" + e.getMessage() + "\n" + stackTraceString + "\n");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        Log.i("TAG", "todayReport: "+dailyRep);
    }

    private void generateRep(Map<String,List<String>> userReportData) throws FileNotFoundException {
        String[] currentDateTime = Util.getCurrentDateTime();
        String date = currentDateTime[0].replace("-", "_");
        String fileName = "Report_" + date + ".pdf";
        String pdfFilePath = "";
        pdfFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName;
        PdfWriter writer = new PdfWriter(pdfFilePath);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);
        Set<String> keys = userReportData.keySet();
        if (keys.isEmpty())
            return;
        Table table = new Table(3);
        table.setTextAlignment(TextAlignment.CENTER);
        table.setFixedLayout();
        table.addCell(createCell("Emp Name", true));
        table.addCell(createCell("Time in", true));
        table.addCell(createCell("Time out", true));


        for (String key : keys) {
            List<String> tempTimeList = userReportData.get(key);
            if (tempTimeList == null || tempTimeList.isEmpty())
                continue;

            table.addCell(createCell(key, false));
            table.addCell(createCell(tempTimeList.get(0), false));
            table.addCell(createCell(tempTimeList.get(1), false));
        }
        document.add(table);
        document.close();
        Toast.makeText(context, "Report saved in " + pdfFilePath, Toast.LENGTH_SHORT).show();
    }


}
