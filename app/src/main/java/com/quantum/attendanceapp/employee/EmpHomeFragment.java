package com.quantum.attendanceapp.employee;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.quantum.attendanceapp.R;
import com.quantum.attendanceapp.model.TimeData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EmpHomeFragment extends Fragment {


    private FirebaseFirestore database;
    private EditText currTimeEt, currDateEt, inTimeEt, outTimeEt;
    private Button timeBtn,viewCalBtn,applyLeaveBtn;
    private List<TimeData> timeDataList;
    private TimeData timeData;

    public EmpHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_emp_home,container,false);
        findViews(view);
        database = FirebaseFirestore.getInstance();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        verifyPreviousCheckIn();
        isCheckInNeed();
        currDateEt.setText(getDate());
        timeBtn.setOnClickListener(v -> {
            if (timeData == null)
                timeData = new TimeData();
            String s = timeBtn.getText().toString();
            if (timeData != null) {
                if (s.equals("Time In")) {
                    timeBtn.setText("Time Out");
                    timeBtn.setBackground(getResources().getDrawable(R.drawable.time_out_btn));
                    String inTime = currTimeEt.getText().toString();
                    inTimeEt.setText(inTime);
                    timeData.setInTime(inTime);
                } else if (s.equals("Time Out")) {
                    timeBtn.setVisibility(View.INVISIBLE);
                    timeBtn.setEnabled(false);
                    String outTime = currTimeEt.getText().toString();
                    outTimeEt.setText(outTime);
                    timeData.setOutTime(outTime);
                    String inTime = timeData.getInTime();
                    timeData.setInHrs(getInHrs(inTime, outTime));
                }
                if (timeData.getDate() == null)
                    timeData.setDate(getDate());
                updateData(timeData, timeData.getDate());
            }
        });

        viewCalBtn.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ViewCalendarActivity.class));
        });

        applyLeaveBtn.setOnClickListener(v->{
            startActivity(new Intent(getContext(), ApplyLeaveActivity.class));
        });

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                updateTime();
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void updateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(calendar.getTime());
        currTimeEt.setText(currentTime);
    }

    private String getTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    private String getDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private String getPreviousDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1); // Subtract one day
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private void verifyPreviousCheckIn() {
        try {
            String pDate = getPreviousDate();
            String monthYear = pDate.substring(pDate.indexOf("-") + 1);
            monthYear = monthYear.replace("-", "");
            String uid = FirebaseAuth.getInstance().getUid();
            if (uid != null) {
                database.collection("TimeData").document(uid).collection(monthYear).document(pDate).get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot result = task.getResult();
                                TimeData td = result.toObject(TimeData.class);
                                if (td == null)
                                    return;
                                if (td.isLeave())
                                    return;
                                if (td.getOutTime() == null || td.getOutTime().isEmpty()) {
                                    td.setLeave(true);
                                }
                                td.setInHrs("00:00");
                                td.setLeaveType("Absent");
                                updateData(td, pDate);
                            }
                        });
            }
        }catch (Exception e){

        }
    }

    private void updateData(TimeData timeData, String date) {
        String uid = FirebaseAuth.getInstance().getUid();
        String monthYear = date.substring(date.indexOf("-") + 1);
        monthYear = monthYear.replace("-","");
        database.collection("TimeData").document(uid).collection(monthYear)
                .document(date).set(timeData);
    }

    private void isCheckInNeed() {
        String currDate = getDate();
        String monthYear = currDate.substring(currDate.indexOf("-") + 1);
        monthYear = monthYear.replace("-","");
        String uid = FirebaseAuth.getInstance().getUid();
        database.collection("TimeData").document(uid).collection(monthYear).document(currDate).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot result = task.getResult();
                        timeData = result.toObject(TimeData.class);
                        if (timeData != null) {
                            if (!timeData.isLeave()) {
                                if (timeData.getDate().equals(currDate)) {
                                    if (timeData.getInTime() == null && timeData.getOutTime() == null) {
                                        timeBtn.setEnabled(true);
                                        timeBtn.setVisibility(View.VISIBLE);
                                        timeBtn.setText("Time In");
                                        timeBtn.setBackground(getResources().getDrawable(R.drawable.time_in_btn));
                                    }
                                    if (timeData.getInTime() != null && timeData.getOutTime() == null) {
                                        timeBtn.setEnabled(true);
                                        timeBtn.setVisibility(View.VISIBLE);
                                        inTimeEt.setText(timeData.getInTime());
                                        timeBtn.setText("Time Out");
                                        timeBtn.setBackground(getResources().getDrawable(R.drawable.time_out_btn));
                                    }
                                    if (timeData.getInTime() != null && timeData.getOutTime() != null) {
                                        inTimeEt.setText(timeData.getInTime());
                                        outTimeEt.setText(timeData.getOutTime());
                                        timeBtn.setVisibility(View.INVISIBLE);
                                        timeBtn.setEnabled(false);
                                    }
                                }
                            } else {
                                inTimeEt.setText(timeData.getLeaveType());
                                outTimeEt.setText(timeData.getLeaveType());
                                timeBtn.setVisibility(View.INVISIBLE);
                                timeBtn.setEnabled(false);
                            }
                        }
                    }
                });

    }


    private String getInHrs(String startTime, String endTime) {
        String value = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);
            long timeDifferenceMillis = endDate.getTime() - startDate.getTime();
            long hours = timeDifferenceMillis / (60 * 60 * 1000);
            long minutes = (timeDifferenceMillis % (60 * 60 * 1000)) / (60 * 1000);
            value = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
        } catch (Exception e) {
        }
        return value;
    }

    private void findViews(View view){
        currTimeEt = view.findViewById(R.id.curr_time_et);
        currDateEt = view.findViewById(R.id.curr_date_et);
        inTimeEt = view.findViewById(R.id.in_time_et);
        outTimeEt = view.findViewById(R.id.out_time_et);
        timeBtn = view.findViewById(R.id.time_btn);
        viewCalBtn = view.findViewById(R.id.view_calendar_btn);
        applyLeaveBtn = view.findViewById(R.id.apply_leave_btn);

        currTimeEt.setShowSoftInputOnFocus(false);
        currDateEt.setShowSoftInputOnFocus(false);
        inTimeEt.setShowSoftInputOnFocus(false);
        outTimeEt.setShowSoftInputOnFocus(false);
    }
}