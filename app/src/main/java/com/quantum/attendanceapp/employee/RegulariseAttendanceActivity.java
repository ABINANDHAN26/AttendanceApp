package com.quantum.attendanceapp.employee;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.quantum.attendanceapp.R;
import com.quantum.attendanceapp.SplashScreen;
import com.quantum.attendanceapp.Utils.PickerUtils;
import com.quantum.attendanceapp.Utils.Util;
import com.quantum.attendanceapp.model.RegulariseData;

import java.util.Calendar;

public class RegulariseAttendanceActivity extends AppCompatActivity {

    private EditText newInEt, newOutEt, dateEt, reasonEt;
    private Button submitBtn;

    private String selDate, selInTime, selOutTime, reason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regularise_attendance);
        findViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        dateEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                dateEt.clearFocus();
                Calendar calendar = Calendar.getInstance();

                PickerUtils.showDatePicker(RegulariseAttendanceActivity.this, ((year, month, day) -> {
                    selDate = Util.getDisplayDate(year, month, day);
                    dateEt.setText(selDate);
                }), calendar);
            }
        });

        newInEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                Calendar calendar = Calendar.getInstance();
                PickerUtils.showTimePicker(RegulariseAttendanceActivity.this, ((hour, min) -> {
                    selInTime = Util.getDisplayTime(hour, min);
                    newInEt.setText(selInTime);
                }), calendar,true);
            }
        });

        newOutEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                Calendar calendar = Calendar.getInstance();
                PickerUtils.showTimePicker(RegulariseAttendanceActivity.this, ((hour, min) -> {
                    selOutTime = Util.getDisplayTime(hour, min);
                    newOutEt.setText(selOutTime);
                }), calendar,true);
            }
        });

        submitBtn.setOnClickListener(v -> {
            reason = reasonEt.getText().toString();
            if (reason.trim().isEmpty()) {
                reasonEt.setError("Enter reason");
                reasonEt.requestFocus();
                return;
            }
            if (selInTime.trim().isEmpty()) {
                reasonEt.setError("Select in time");
                reasonEt.requestFocus();
                return;
            }
            if (selOutTime.trim().isEmpty()) {
                reasonEt.setError("Select out time");
                reasonEt.requestFocus();
                return;
            }
            if (selDate.trim().isEmpty()) {
                reasonEt.setError("Select date");
                reasonEt.requestFocus();
                return;
            }
            RegulariseData regulariseData = new RegulariseData();
            regulariseData.setDate(selDate);
            regulariseData.setNewInTime(selInTime);
            regulariseData.setNewOutTime(selOutTime);
            regulariseData.setReason(reason);
            regulariseData.setUserName(SplashScreen.userData.getUserName());
            String uid = SplashScreen.userData.getUserId();
            regulariseData.setUid(uid);
            regulariseData.setId(Util.generateId(selDate, uid));
            regulariseData.setAcceptedBy("");
            regulariseData.setAcceptedOn("");
            regulariseData.setAccepted(false);
            submitRequest(regulariseData);

        });
    }

    private void submitRequest(RegulariseData regulariseData) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection("RegulariseData").document().set(regulariseData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Request sended", Toast.LENGTH_SHORT).show();
                sleepAndFinish();
            }
        });
    }

    private void sleepAndFinish() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            finish();
        }
    }

    private void findViews() {
        newInEt = findViewById(R.id.new_in_time_et);
        newOutEt = findViewById(R.id.new_out_time_et);
        dateEt = findViewById(R.id.date_et);
        reasonEt = findViewById(R.id.reason_et);

        submitBtn = findViewById(R.id.submit_btn);

        newInEt.setShowSoftInputOnFocus(false);
        newOutEt.setShowSoftInputOnFocus(false);
        dateEt.setShowSoftInputOnFocus(false);
    }
}