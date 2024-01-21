package com.quantum.attendanceapp.employee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

public class ApplyLeaveActivity extends AppCompatActivity {

    private EditText fromDateEt,toDateEt,reasonEt;

    private Button applyBtn;
    private ImageView backBtn;
    private AutoCompleteTextView leaveSpinner;


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

        applyBtn.setOnClickListener(view ->{
            String uid = FirebaseAuth.getInstance().getUid();
            if(uid == null){
                Toast.makeText(this, "Can't apply for leave", Toast.LENGTH_SHORT).show();
                finish();
            }
            String fromDate = fromDateEt.getText().toString();
            String toDate = toDateEt.getText().toString();
            String reason = reasonEt.getText().toString();
            String leaveType = leaveSpinner.getText().toString();
            if(fromDate.isEmpty() || toDate.isEmpty() || reason.isEmpty() || leaveType.isEmpty()){
                Toast.makeText(this, "Enter data", Toast.LENGTH_SHORT).show();
                return;
            }
            LeaveData leaveData = new LeaveData();
            leaveData.setFromData(fromDate);
            leaveData.setToData(toDate);
            leaveData.setLeaveType(leaveType);
            leaveData.setReason(reason);
            leaveData.setApprovedDate(null);
            leaveData.setApproved(false);
            leaveData.setCancelled(false);
            leaveData.setLeaveId(Util.generateId(leaveType,uid));
            leaveData.setUserId(uid);
            leaveData.setName(SplashScreen.userData.getUserName());
            addLeave(leaveData,uid);
        });

        fromDateEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                fromDateEt.clearFocus();
                Calendar calendar = Calendar.getInstance();
                PickerUtils.showDatePicker(ApplyLeaveActivity.this, ((year, month, day) -> {
                    String selectedDate = Util.getDisplayDate(year, month, day);
                    fromDateEt.setText(selectedDate);
                }), calendar);
            }
        });

        toDateEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                toDateEt.clearFocus();
                Calendar calendar = Calendar.getInstance();
                PickerUtils.showDatePicker(ApplyLeaveActivity.this, ((year, month, day) -> {
                    String selectedDate = Util.getDisplayDate(year, month, day);
                    toDateEt.setText(selectedDate);
                }), calendar);
            }
        });

        backBtn.setOnClickListener(view->{
            finish();
        });
    }

    private void addLeave(LeaveData leaveData,String uid){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        if(uid != null)
        {
            database.collection("LeaveData").document().set(leaveData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ApplyLeaveActivity.this, "Leave Applied", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(ApplyLeaveActivity.this, "Can't apply leave", Toast.LENGTH_SHORT).show();
                                finish();
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

    private void findViews(){
        fromDateEt = findViewById(R.id.from_date_et);
        toDateEt = findViewById(R.id.to_date_et);
        reasonEt = findViewById(R.id.reason_et);
        leaveSpinner = findViewById(R.id.leave_spinner);
        applyBtn = findViewById(R.id.apply_leave_btn);
        backBtn = findViewById(R.id.back_btn);

        fromDateEt.setShowSoftInputOnFocus(false);
        toDateEt.setShowSoftInputOnFocus(false);
    }
}