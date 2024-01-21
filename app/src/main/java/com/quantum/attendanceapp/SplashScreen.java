package com.quantum.attendanceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.quantum.attendanceapp.model.UserData;

public class SplashScreen extends AppCompatActivity {

    public static UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            String uid = mAuth.getUid();
            if(uid == null){
                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                finish();
            }else {
                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                firebaseFirestore.collection("Data").document(uid).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            userData = documentSnapshot.toObject(UserData.class);
                            Intent intent = new Intent(SplashScreen.this, HomePage.class);
                            startActivity(intent);
                            finish();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, "Can't find user", Toast.LENGTH_SHORT).show();
                        });

            }
        }else{
            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            finish();
        }
    }
}