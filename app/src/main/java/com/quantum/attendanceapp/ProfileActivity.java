package com.quantum.attendanceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.quantum.attendanceapp.model.UserData;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameTv,idTv,dobTv,dojTv,emailTv,qualTv,repManTv,branchTv;
    private ImageView backBtn;
    private Button backBtn1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilectivity);
        findViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String uid = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore.getInstance().collection("Data").document(uid)
                        .get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                UserData userData = task.getResult().toObject(UserData.class);
                                if (userData != null) {
                                    setValues(userData);
                                }
                                else
                                    finish();
                            }else{
                                finish();
                            }
                        });
        backBtn.setOnClickListener(v->{
            finish();
        });
        backBtn1.setOnClickListener(v->{
            finish();
        });

    }

    private void setValues(UserData userData){
        nameTv.setText(userData.getUserName());
        idTv.setText(userData.getEmailId());
        dobTv.setText(userData.getDob());
        dojTv.setText(userData.getDoj());
        qualTv.setText(userData.getQualification());
        emailTv.setText(userData.getEmailId());
        branchTv.setText(userData.getBranch());
        repManTv.setText(userData.getReportingManagerName());
    }

    private void findViews(){
        nameTv =findViewById(R.id.name_tv);
        idTv =findViewById(R.id.emp_id_tv);
        dobTv =findViewById(R.id.dob_tv);
        dojTv =findViewById(R.id.doj_tv);
        qualTv =findViewById(R.id.qual_tv);
        emailTv =findViewById(R.id.email_tv);
        repManTv =findViewById(R.id.rep_man_tv);
        branchTv =findViewById(R.id.branch_tv);

        backBtn = findViewById(R.id.back_btn);
        backBtn1 = findViewById(R.id.back_btn_1);
    }
}