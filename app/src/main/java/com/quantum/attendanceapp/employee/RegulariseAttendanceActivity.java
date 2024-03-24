package com.quantum.attendanceapp.employee;

import static com.quantum.attendanceapp.SplashScreen.*;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.quantum.attendanceapp.R;
import com.quantum.attendanceapp.SplashScreen;
import com.quantum.attendanceapp.Utils.PickerUtils;
import com.quantum.attendanceapp.Utils.Util;
import com.quantum.attendanceapp.model.RegulariseData;

import java.util.Calendar;
import java.util.List;

public class RegulariseAttendanceActivity extends AppCompatActivity {

    private EditText newInEt, newOutEt, dateEt, reasonEt;
    private Button submitBtn;
    private ProgressBar progressBar;

    private String selDate = "", selInTime = "", selOutTime = "", reason = "";
    private ImageView backBtn;
    private Button backBtn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regularise_attendance);

        findViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        backBtn.setOnClickListener(v->{
            finish();
        });
        backBtn1.setOnClickListener(v->{
            finish();
        });
        dateEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                dateEt.clearFocus();
                Calendar calendar = Calendar.getInstance();

                PickerUtils.showDatePicker(RegulariseAttendanceActivity.this, ((year, month, day) -> {
                    selDate = Util.getDisplayDate(year, month, day);
                    boolean isWeekOff = Util.isGivenDay(year, month, day, userData.getWeeklyOff());
                    if(isWeekOff){
                        Toast.makeText(this, "Selected date is your weekly off", Toast.LENGTH_SHORT).show();
                    }
                    dateEt.setText(selDate);

                }), calendar);
            }
        });

        newInEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                newInEt.clearFocus();
                Calendar calendar = Calendar.getInstance();
                PickerUtils.showTimePicker(RegulariseAttendanceActivity.this, ((hour, min) -> {
                    selInTime = Util.getDisplayTime(hour, min);
                    if (!selOutTime.trim().isEmpty()) {
                        if (selInTime.equals(selOutTime)) {
                            Toast.makeText(this, "In and Out time can't be same", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    newInEt.setText(selInTime);
                }), calendar, true);
            }
        });

        newOutEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                newOutEt.clearFocus();
                Calendar calendar = Calendar.getInstance();
                PickerUtils.showTimePicker(RegulariseAttendanceActivity.this, ((hour, min) -> {
                    selOutTime = Util.getDisplayTime(hour, min);
                    if (!selInTime.trim().isEmpty()) {
                        if (selOutTime.equals(selInTime)) {
                            Toast.makeText(this, "In and Out time can't be same", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    newOutEt.setText(selOutTime);
                }), calendar, true);
            }
        });

        submitBtn.setOnClickListener(v -> {
            reason = reasonEt.getText().toString().trim();
            if (reason.isEmpty()) {
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
            progressBar.setVisibility(View.VISIBLE);
            RegulariseData regulariseData = new RegulariseData();
            regulariseData.setDate(selDate);
            regulariseData.setNewInTime(selInTime);
            regulariseData.setNewOutTime(selOutTime);
            regulariseData.setReason(reason);
            regulariseData.setUserName(userData.getUserName());
            String uid = userData.getUserId();
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

        FirebaseFirestore.getInstance().collection("RegulariseData").get().
                addOnCompleteListener(task -> {
                    boolean anyMatch = false;
                    List<RegulariseData> objects = task.getResult().toObjects(RegulariseData.class);
                    for (RegulariseData rd : objects) {
                        if (rd.getDate().equals(regulariseData.getDate())) {
                            anyMatch = true;
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "You applied regularize for this date's", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    if (!anyMatch) {
                        database.collection("RegulariseData").document().set(regulariseData).addOnCompleteListener(t -> {
                            if (t.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(this, "Request sent", Toast.LENGTH_SHORT).show();
                                sleepAndFinish();
                            }
                        });
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

        progressBar = findViewById(R.id.progress_bar);

        newInEt.setShowSoftInputOnFocus(false);
        newOutEt.setShowSoftInputOnFocus(false);
        dateEt.setShowSoftInputOnFocus(false);

        backBtn = findViewById(R.id.back_btn);
        backBtn1 = findViewById(R.id.back_btn_1);
    }
}
