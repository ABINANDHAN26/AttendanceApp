package com.quantum.attendanceapp.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.quantum.attendanceapp.R;
import com.quantum.attendanceapp.Utils.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsActivity extends AppCompatActivity {

    private EditText ipEt, portEt, emailEt, tokenEt;

    private Button updateBtn, backBtn;
    private ImageView backBtn1;
    private ProgressBar progressBar;
    private String ipStr, portStr, emailStr, tokenStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        findViews();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Map<String, String> emailData = Util.getEmailData(this);
        setData(emailData);
        updateBtn.setOnClickListener(v -> {
            try {
                update();
            }catch (Exception e){
                finish();
            }
        });
        backBtn1.setOnClickListener(v -> {
            finish();
        });
        backBtn.setOnClickListener(v -> {
            finish();
        });
    }


    private void setData(Map<String, String> data) {
        if (data == null) {
            return;
        }
        String emailServerAddress = data.get("emailServerAddress");
        String[] urlData = Util.parseURL(emailServerAddress);
        String ip = "";
        String port = "";
        if (urlData != null) {
            ip = urlData[0];
            port = urlData[1];
        }
        String senderEmail = data.get("sender_email");
        String senderPassword = data.get("sender_password");
        ipEt.setText(ip);
        portEt.setText(port);
        emailEt.setText(senderEmail);
        tokenEt.setText(senderPassword);

    }

    private void update() {

        progressBar.setVisibility(View.VISIBLE);
        ipStr = Util.getValue(ipEt);
        portStr = Util.getValue(portEt);
        emailStr = Util.getValue(emailEt);
        tokenStr = Util.getValue(tokenEt);
        String regex = "(\\d{1,3}\\.{3}\\d{1,3})";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ipStr);
        String TAG = "TAG";
        Log.i(TAG, "update: ipStr: " + ipStr);
        Log.i(TAG, "update: (!matcher.matches()): " + (!matcher.matches()));
        if (matcher.matches()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "IP is not valid", Toast.LENGTH_SHORT).show();
            return;
        }
        String address = "http://" + ipStr + ":" + portStr + "/ping";
        if (!Util.checkAddress(address)) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "There's a problem contacting the server check the ip and port number", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.GONE);
        String emailServerAddress = "http://" + ipStr + ":" + portStr + "/send_email";
        Map<String, String> data = new HashMap<>();
        data.put("pingServerAddress", address);
        data.put("emailServerAddress", emailServerAddress);
        data.put("sender_email", emailStr);
        data.put("sender_password", tokenStr);
        Toast.makeText(this, "Data updated", Toast.LENGTH_SHORT).show();
        Util.storeEmailData(SettingsActivity.this, data);

    }


    private void findViews() {
        ipEt = findViewById(R.id.stg_email_ip);
        portEt = findViewById(R.id.stg_email_port);
        emailEt = findViewById(R.id.stg_email_id);
        tokenEt = findViewById(R.id.stg_email_token);
        updateBtn = findViewById(R.id.update_btn);
        backBtn = findViewById(R.id.back_btn_1);
        backBtn1 = findViewById(R.id.back_btn);
        progressBar = findViewById(R.id.progress_bar);
    }
}