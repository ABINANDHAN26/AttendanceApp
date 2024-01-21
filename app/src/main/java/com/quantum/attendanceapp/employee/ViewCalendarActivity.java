package com.quantum.attendanceapp.employee;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
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
import com.quantum.attendanceapp.model.TimeData;

import java.text.SimpleDateFormat;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_calendar);
        findViews();
        calendarView.setVisibility(View.INVISIBLE);
        detailsTextView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        String date = getDate();
        String monthYear = date.substring(date.indexOf("-") + 1);
        monthYear = monthYear.replace("-", "");
        getDetailsOfMonth(monthYear);

    }

    @Override
    protected void onResume() {
        super.onResume();
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            if(preMonth == month) {
                month += 1;
                String m = String.valueOf(month);
                if (month < 10) {
                    m = "0" + month;
                }
                String date1 = dayOfMonth + "-" + m + "-" + year;
                String details = getDetailsForDate(date1);
                detailsTextView.setText(details);
            }else{
                month += 1;
                String m = String.valueOf(month);
                if (month < 10) {
                    m = "0" + month;
                }
                String date1 = dayOfMonth + "-" + m + "-" + year;
                getDetailsOfMonth(date1);
            }
            preMonth = month-1;
        });

        backBtn.setOnClickListener(view -> {
            finish();
        });

    }

    private void getDetailsOfMonth(String month) {
        String uid = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        if(uid != null) {
            database.collection("TimeData").document(uid).collection(month).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String date = getDate();
                            QuerySnapshot result = task.getResult();
                            timeDataList = result.toObjects(TimeData.class);
                            calendarView.setVisibility(View.VISIBLE);
                            detailsTextView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            detailsTextView.setText(getDetailsForDate(date));
                        }
                    });
        }
    }

    private String getDetailsForDate(String date) {
        String inTime = "";
        String outTime = "";
        String out = "";
        if (timeDataList != null && timeDataList.size() > 0) {
            TimeData td = null;
            for(TimeData timeData : timeDataList){
                if(date.equals(timeData.getDate())){
                    td = timeData;
                }
            }

            if(td != null){
                if (!td.isLeave()) {
                    inTime = td.getInTime();
                    outTime = td.getOutTime();
                    if (inTime == null)
                        inTime = "";
                    if (outTime == null)
                        outTime = "";
                    out = "Date: " + date + "\n"
                            + "In Time: " + inTime + "\n"
                            + "Out Time: " + outTime + "\n";
                } else {
                    out = "Date: " + date + "\n"
                            + td.getLeaveType();
                }
            }else{
                out = "Date: "+date + "\n"
                        +"No Data Found";
            }
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