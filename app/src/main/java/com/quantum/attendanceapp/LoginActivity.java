package com.quantum.attendanceapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {


    private EditText emailEt, passwordEt;
    private Button loginBtn;
    private TextView forgotPwTv;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loginBtn.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String email, password;
            email = emailEt.getText().toString();
            password = passwordEt.getText().toString();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Enter the credentials to login", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEt.setError("Enter valid E-mail");
                Toast.makeText(LoginActivity.this, "Enter Valid E-mail", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                signIn(email, password);
                loginBtn.setEnabled(false);
            }
        });

    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    startActivity(new Intent(LoginActivity.this,SplashScreen.class));
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void findViews() {
        emailEt = findViewById(R.id.email_login);
        passwordEt = findViewById(R.id.password_login);
        forgotPwTv = findViewById(R.id.forgot_login);

        progressBar = findViewById(R.id.progress_login);

        loginBtn = findViewById(R.id.login_btn);

    }

}