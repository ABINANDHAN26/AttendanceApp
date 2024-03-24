package com.quantum.attendanceapp.employee;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.quantum.attendanceapp.R;
import com.quantum.attendanceapp.SplashScreen;
import com.quantum.attendanceapp.Utils.PickerUtils;
import com.quantum.attendanceapp.Utils.Util;
import com.quantum.attendanceapp.model.LeaveData;

import java.util.Calendar;
import java.util.List;

public class ApplyLeaveActivity extends AppCompatActivity {

    private EditText fromDateEt, toDateEt, reasonEt;

    private Button applyBtn;
    private ImageView backBtn;
    private Button backBtn1;
    private AutoCompleteTextView leaveSpinner;

    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_leave);

        findViews();
        setLeaveSpinner();
    }

    @Override
    protected void onResume() {
        super.onResume();

        applyBtn.setOnClickListener(view -> {
            String uid = FirebaseAuth.getInstance().getUid();
            if (uid == null) {
                Toast.makeText(this, "Can't apply for leave", Toast.LENGTH_SHORT).show();
                finish();
            }
            String fromDate = fromDateEt.getText().toString();
            String toDate = toDateEt.getText().toString();
            String reason = reasonEt.getText().toString();
            String leaveType = leaveSpinner.getText().toString();
            if (fromDate.isEmpty() || toDate.isEmpty() || reason.trim().isEmpty() || leaveType.isEmpty()) {
                Toast.makeText(this, "Enter data", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            LeaveData leaveData = new LeaveData();
            leaveData.setFromData(fromDate);
            leaveData.setToData(toDate);
            leaveData.setLeaveType(leaveType);
            leaveData.setReason(reason);
            leaveData.setApprovedDate(null);
            leaveData.setApproved(false);
            leaveData.setCancelled(false);
            leaveData.setLeaveId(Util.generateId(leaveType, uid));
            leaveData.setUserId(uid);
            leaveData.setName(SplashScreen.userData.getUserName());
            addLeave(leaveData, uid);
        });

        fromDateEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                fromDateEt.clearFocus();
                Calendar calendar = Calendar.getInstance();
                PickerUtils.showDatePicker(ApplyLeaveActivity.this, ((year, month, day) -> {
                    String selectedDate = Util.getDisplayDate(year, month, day);
                    String td = toDateEt.getText().toString();
                    if (!td.trim().isEmpty()) {
                        long l = Util.compareDate(selectedDate, td);
                        if (l < 0) {
                            toDateEt.setError("To date can't be lesser than From date");
                            return;
                        }
                    }
                    fromDateEt.setText(selectedDate);
                }), calendar, System.currentTimeMillis());
            }
        });

        toDateEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                toDateEt.clearFocus();
                String fd = fromDateEt.getText().toString().trim();
                if(fd.isEmpty()){
                    Toast.makeText(this, "Select From date first", Toast.LENGTH_SHORT).show();
                    return;
                }

                Calendar calendar = Calendar.getInstance();
                long minDate = Util.stringToTimeStamp(fd);
                PickerUtils.showDatePicker(ApplyLeaveActivity.this, ((year, month, day) -> {
                    String selectedDate = Util.getDisplayDate(year, month, day);

                    long l = Util.compareDate(fd, selectedDate);
                    if (l < 0) {
                        Toast.makeText(this, "To date can't be lesser than From date", Toast.LENGTH_LONG).show();
                        toDateEt.setError("To date can't be lesser than From date");
                        return;
                    }
                    toDateEt.setText(selectedDate);
                }), calendar, minDate);
            }
        });

        backBtn.setOnClickListener(view -> {
            finish();
        });
        backBtn1.setOnClickListener(view -> {
            finish();
        });
    }

    private void addLeave(LeaveData leaveData, String uid) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        if (uid != null) {

            FirebaseFirestore.getInstance().collection("LeaveData").get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            boolean anyMatch = false;
                            List<LeaveData> objects = task.getResult().toObjects(LeaveData.class);
                            outerloop:
                            for (LeaveData ld : objects) {
                                if (ld.getUserId().equals(uid)) {
                                    List<String> previousDateList = Util.getDateList(ld.getFromData(), ld.getToData());
                                    List<String> currentDateList = Util.getDateList(leaveData.getFromData(), leaveData.getToData());

                                    for (String preDate : previousDateList) {
                                        if (currentDateList.contains(preDate)) {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(ApplyLeaveActivity.this, "You applied leave for this date's", Toast.LENGTH_LONG).show();
                                            anyMatch = true;
                                            break outerloop;
                                        }
                                    }
                                }
                            }
                            if (!anyMatch) {
                                database.collection("LeaveData").document().set(leaveData)
                                        .addOnCompleteListener(task1 -> {
                                            progressBar.setVisibility(View.GONE);
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(ApplyLeaveActivity.this, "Leave Applied", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                Toast.makeText(ApplyLeaveActivity.this, "Can't apply leave", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });
                            }
                        }
                    });


        }

    }

    private void setLeaveSpinner() {

        String[] expenseArray = getResources().getStringArray(R.array.leave_types);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ApplyLeaveActivity.this, R.layout.drop_down_item, expenseArray);
        arrayAdapter.notifyDataSetChanged();
        leaveSpinner.setAdapter(arrayAdapter);
    }

    private void findViews() {
        fromDateEt = findViewById(R.id.from_date_et);
        toDateEt = findViewById(R.id.to_date_et);
        reasonEt = findViewById(R.id.reason_et);
        leaveSpinner = findViewById(R.id.leave_spinner);
        applyBtn = findViewById(R.id.apply_leave_btn);
        backBtn = findViewById(R.id.back_btn);
        backBtn1 = findViewById(R.id.back_btn_1);

        progressBar = findViewById(R.id.progress_bar);

        fromDateEt.setShowSoftInputOnFocus(false);
        toDateEt.setShowSoftInputOnFocus(false);
    }
}