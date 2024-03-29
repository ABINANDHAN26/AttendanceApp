package com.quantum.attendanceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPwActivity extends AppCompatActivity {
    private EditText emailEt;
    private Button resetBtn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    private ImageView backBtn;
    private Button backBtn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pw);
        mAuth = FirebaseAuth.getInstance();

        findViews();
    }

    @Override
    protected void onResume() {
        super.onResume();


        resetBtn.setOnClickListener(view -> {

            String email = emailEt.getText().toString();
            if(email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Toast.makeText(ForgotPwActivity.this, "Enter Registered Email-Id", Toast.LENGTH_SHORT).show();
                emailEt.setError("Enter Valid Email-Id");
                emailEt.requestFocus();

            }else{
                progressBar.setVisibility(View.VISIBLE);
                requestPassChange(email);
            }
        });

        backBtn.setOnClickListener(view ->{
            finish();
        });
        backBtn1.setOnClickListener(view ->{
            finish();
        });
    }

    private void requestPassChange(String email){
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(ForgotPwActivity.this, "Check Your email to reset password", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(ForgotPwActivity.this, "Try again later", Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.INVISIBLE);
        });
    }

    private void findViews(){
        emailEt = findViewById(R.id.email_fpw);
        resetBtn = findViewById(R.id.reset_pw_btn);
        progressBar = findViewById(R.id.progress_fpw);
        backBtn = findViewById(R.id.back_btn);
        backBtn1 = findViewById(R.id.back_btn_1);
    }
}