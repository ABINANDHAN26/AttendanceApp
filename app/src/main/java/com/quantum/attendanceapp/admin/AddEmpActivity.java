package com.quantum.attendanceapp.admin;

import static com.quantum.attendanceapp.Utils.Util.getValue;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.quantum.attendanceapp.R;
import com.quantum.attendanceapp.SplashScreen;
import com.quantum.attendanceapp.Utils.Util;
import com.quantum.attendanceapp.employee.ApplyLeaveActivity;
import com.quantum.attendanceapp.model.UserData;

import java.util.List;

public class AddEmpActivity extends AppCompatActivity {

    String TAG = "TAG";
    private EditText empNameEt, empEmailEt, empDojEt, empDobEt, empQualEt, empAddressEt, empPhoneEt;
    private AutoCompleteTextView weeklyOffSpinner, empBranchSpinner;
    private Button addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_emp);
        findViews();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setBranchSpinner();
        setWeeklySpinner();
        addBtn.setOnClickListener(v -> {
            if (Util.checkEt(empNameEt)) {
                empNameEt.setError("Enter employee name!!");
                empNameEt.requestFocus();
                return;
            }
            if (Util.checkEt(empDojEt)) {
                empDojEt.setError("Enter employee doj!!");
                empDojEt.requestFocus();
                return;
            }
            if (Util.checkEt(empDobEt)) {
                empDobEt.setError("Enter employee dob!!");
                empDobEt.requestFocus();
                return;
            }
            if (Util.checkEt(empQualEt)) {
                empQualEt.setError("Enter employee qualification!!");
                empQualEt.requestFocus();
                return;
            }
            if (Util.checkEt(empAddressEt)) {
                empAddressEt.setError("Enter employee address!!");
                empAddressEt.requestFocus();
                return;
            }
            if (Util.checkEt(empPhoneEt)) {
                empPhoneEt.setError("Enter employee phone!!");
                empPhoneEt.requestFocus();
                return;
            }

            if (Util.checkEt(empBranchSpinner)){
                empBranchSpinner.setError("Select employee branch!!");
                empBranchSpinner.requestFocus();
                return;
            }
            if (Util.checkEt(weeklyOffSpinner)){
                weeklyOffSpinner.setError("Select employee weekly off!!");
                weeklyOffSpinner.requestFocus();
                return;
            }

            UserData userData = new UserData();
            userData.setUserName(getValue(empNameEt));
            userData.setEmailId(getValue(empEmailEt));
            userData.setDoj(getValue(empDojEt));
            userData.setDob(getValue(empDobEt));
            userData.setUserType("Employee");
            userData.setQualification(getValue(empQualEt));
            userData.setAddress(getValue(empAddressEt));
            userData.setPhone(getValue(empPhoneEt));
            String branch = getValue(empBranchSpinner);
            String weeklyOff = getValue(weeklyOffSpinner);
            userData.setBranch(branch);
            userData.setWeeklyOff(weeklyOff);
            List<String> managerDetails = SplashScreen.companyData.getBranchManagerList().get(branch);
            userData.setReportingManagerName(managerDetails.get(0));
            userData.setReportingManagerId(managerDetails.get(1));
            userData.setReportingManagerEmail(managerDetails.get(2));

            addEmployee(userData);

        });
    }

    private void addEmployee(UserData userData) {
        try {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String uid = mAuth.getUid();
            mAuth.signOut();
            String email = userData.getEmailId();
            String password = "saec@123";

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String empuid = mAuth.getCurrentUser().getUid();
                            userData.setUserId(empuid);
                            database.collection("Data").get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    QuerySnapshot result = task1.getResult();
                                    int userId = result.size() + 1;
                                    userData.setEmpId(String.valueOf(userId));
                                    if (uid != null) {
                                        database.collection("Data").document(empuid).set(userData)
                                                .addOnCompleteListener(task11 -> {
                                                    if (task11.isSuccessful()) {
                                                        mAuth.signOut();
                                                        signIn();
                                                    }
                                                });
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(this, "Can't add user", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            finish();
        }

    }

    private void signIn() {
        try {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            SharedPreferences sharedPref = this.getSharedPreferences("Login_Details", Context.MODE_PRIVATE);
            String pass = sharedPref.getString("pass", "");
            String email = sharedPref.getString("email", "");
            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {
                        finish();
                    });
        } catch (Exception e) {
            finish();
        }
    }

    private void setBranchSpinner() {

        String[] expenseArray = SplashScreen.companyData.getBranchList().toArray(new String[0]);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddEmpActivity.this, R.layout.drop_down_item, expenseArray);
        arrayAdapter.notifyDataSetChanged();
        empBranchSpinner.setAdapter(arrayAdapter);
    }

    private void setWeeklySpinner() {

        String[] expenseArray = getResources().getStringArray(R.array.weekly_off);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddEmpActivity.this, R.layout.drop_down_item, expenseArray);
        arrayAdapter.notifyDataSetChanged();
        weeklyOffSpinner.setAdapter(arrayAdapter);
    }



    private void findViews() {
        empNameEt = findViewById(R.id.emp_name_et);
        empEmailEt = findViewById(R.id.emp_email_et);
        empDojEt = findViewById(R.id.emp_doj_et);
        empDobEt = findViewById(R.id.emp_dob_et);
        empQualEt = findViewById(R.id.emp_qual_et);
        empAddressEt = findViewById(R.id.emp_address_et);
        empPhoneEt = findViewById(R.id.emp_phone_et);

        empBranchSpinner = findViewById(R.id.branch_spinner);
        weeklyOffSpinner = findViewById(R.id.weekly_spinner);

        addBtn = findViewById(R.id.add_btn);
    }
}