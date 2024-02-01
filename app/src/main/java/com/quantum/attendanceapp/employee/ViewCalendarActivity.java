package com.quantum.attendanceapp.employee;

import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.quantum.attendanceapp.R;
import com.quantum.attendanceapp.SplashScreen;
import com.quantum.attendanceapp.Utils.Util;
import com.quantum.attendanceapp.model.TimeData;
import com.quantum.attendanceapp.model.UserData;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ViewCalendarActivity extends AppCompatActivity {

    private List<TimeData> timeDataList;
    private ProgressBar progressBar;
    private TextView detailsTextView;
    private CalendarView calendarView;

    private ImageView backBtn;

    private int preMonth = -1;
    private String TAG = "TAG";
    private String selectedDate = "";

    private UserData userData;
    private String weeklyOff = "";

    public static String getMonth() {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M");
        return dateTime.format(formatter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_calendar);
        findViews();
        userData = SplashScreen.userData;
        weeklyOff = userData.getWeeklyOff();
        weeklyOff = weeklyOff.toUpperCase();
        calendarView.setVisibility(View.INVISIBLE);
        detailsTextView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        String date = getDate();
        selectedDate = date;
        String monthYear = date.substring(date.indexOf("-") + 1);
        preMonth = Integer.parseInt(getMonth());
        monthYear = monthYear.replace("-", "");
        getDetailsOfMonth(monthYear);

    }

    @Override
    protected void onResume() {
        super.onResume();
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            month += 1;
            boolean isWeekOff = Util.isGivenDay(year, month, dayOfMonth, DayOfWeek.valueOf(weeklyOff));
            String m = String.valueOf(month);
            if (month < 10) {
                m = "0" + month;
            }
            selectedDate = dayOfMonth + "-" + m + "-" + year;

            if (isWeekOff) {
                detailsTextView.setText("Date: "+selectedDate + "\nWeek off");
                return;
            }

            if (preMonth == month) {

                String details = getDetailsForDate();
                detailsTextView.setText(details);
            } else {
                String mYear = m + year;
                getDetailsOfMonth(mYear);
            }
            preMonth = month;
        });


        backBtn.setOnClickListener(view -> {
            finish();
        });

    }

    private void getDetailsOfMonth(String month) {
        String uid = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        calendarView.setVisibility(View.INVISIBLE);
        detailsTextView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        if (uid != null) {
            database.collection("TimeData").document(uid).collection(month).get()
                    .addOnCompleteListener(task -> {
                        calendarView.setVisibility(View.VISIBLE);
                        detailsTextView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();
                            timeDataList = result.toObjects(TimeData.class);
                            detailsTextView.setText(getDetailsForDate());
                        }
                    });
        }
    }

    private String getDetailsForDate() {
        String inTime = "";
        String outTime = "";
        String out = "";
        TimeData td = null;
        if (timeDataList != null && timeDataList.size() > 0) {
            for (TimeData timeData : timeDataList) {
                if (selectedDate.equals(timeData.getDate())) {
                    td = timeData;
                }
            }
        }
        if (td != null) {
            if (!td.isLeave()) {
                inTime = td.getInTime();
                outTime = td.getOutTime();
                if (inTime == null)
                    inTime = "";
                if (outTime == null)
                    outTime = "";
                out = "Date: " + selectedDate + "\n"
                        + "In Time: " + inTime + "\n"
                        + "Out Time: " + outTime + "\n";
            } else {
                out = "Date: " + selectedDate + "\n"
                        + td.getLeaveType();
            }
        } else {
            out = "Date: " + selectedDate + "\n"
                    + "No Data Found";
        }
        return out;
    }

    private String getDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private void findViews() {
        progressBar = findViewById(R.id.progress_view_cal);
        detailsTextView = findViewById(R.id.detailsTextView);
        calendarView = findViewById(R.id.calendarView);
        backBtn = findViewById(R.id.back_btn);
    }
}