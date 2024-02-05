package com.quantum.attendanceapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.quantum.attendanceapp.Utils.Util;
import com.quantum.attendanceapp.model.CompanyData;
import com.quantum.attendanceapp.model.UserData;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    public static UserData userData;
    public static CompanyData companyData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getUid();
            if (uid == null) {
                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                finish();
            } else {
                if (Util.isLoginSessionValid(SplashScreen.this)) {
                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                    firebaseFirestore.collection("Data").document(uid).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                userData = documentSnapshot.toObject(UserData.class);
                                FirebaseFirestore.getInstance().collection("CompanyData").document("cbhsTYrU1xjRRLGPrPAc").get()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                CompanyData object = task.getResult().toObject(CompanyData.class);
                                                if (object != null) {
                                                    companyData = object;
                                                }
                                                Intent intent = new Intent(SplashScreen.this, HomePage.class);
                                                intent.putExtra("UserData", userData);
                                                intent.putExtra("CompanyData", companyData);
                                                startActivity(intent);
                                                finish();

                                            }
                                        });

                            }).addOnFailureListener(e -> {
                                Toast.makeText(this, "Can't find user", Toast.LENGTH_SHORT).show();
                            });
                }else{
                    Toast.makeText(this, "Your session has been expired login again", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    finish();
                }
            }
        } else {
            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            finish();
        }
    }
}