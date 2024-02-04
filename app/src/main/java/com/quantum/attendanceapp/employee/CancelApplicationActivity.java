package com.quantum.attendanceapp.employee;

import static com.quantum.attendanceapp.SplashScreen.userData;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.quantum.attendanceapp.R;
import com.quantum.attendanceapp.SplashScreen;
import com.quantum.attendanceapp.adapters.AttendanceListAdapter;
import com.quantum.attendanceapp.adapters.CancelListAdapter;
import com.quantum.attendanceapp.adapters.LeaveListAdapter;
import com.quantum.attendanceapp.model.LeaveData;
import com.quantum.attendanceapp.model.RegulariseData;

import java.util.ArrayList;
import java.util.List;

public class CancelApplicationActivity extends AppCompatActivity {

    private RecyclerView cancelLeaveRecyclerView,cancelRegRecyclerView;
    private RecyclerView.LayoutManager rvl;
    private CancelListAdapter cla;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_application);
        findViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLeaveData();
        getRegulariseData();
    }


    private void getLeaveData(){
        try {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection("LeaveData").get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<LeaveData> leaveDataList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                LeaveData leaveData = document.toObject(LeaveData.class);
                                if (!leaveData.isApproved())
                                    if(leaveData.getUserId().equals(userData.getUserId()) && (leaveData.getApproverName() == null || leaveData.getApproverName().trim().isEmpty()))
                                        leaveDataList.add(leaveData);
                            }
                            if(leaveDataList.size() <= 0) {
                                cancelLeaveRecyclerView.setVisibility(View.GONE);
                            }
                            else {
                                rvl = new LinearLayoutManager(CancelApplicationActivity.this);
                                cla = new CancelListAdapter(leaveDataList,null,CancelApplicationActivity.this);
                                cla.setHasStableIds(true);
                                cancelLeaveRecyclerView.setHasFixedSize(true);
                                cancelLeaveRecyclerView.setLayoutManager(rvl);
                                cancelLeaveRecyclerView.setHasFixedSize(true);
                                cancelLeaveRecyclerView.setItemViewCacheSize(100);
                                cancelLeaveRecyclerView.setAdapter(cla);
                            }
                        }
                    });
        }catch (NullPointerException e){
        }

    }

    private void getRegulariseData(){
        try {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection("RegulariseData").get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<RegulariseData> regulariseDataList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                RegulariseData regulariseData = document.toObject(RegulariseData.class);
                                if (regulariseData.getUserName().equals(userData.getUserName()) && !regulariseData.isAccepted())
                                    regulariseDataList.add(regulariseData);
                            }
                            if(regulariseDataList.size() <= 0) {
                                cancelRegRecyclerView.setVisibility(View.GONE);
                            }
                            else{
                                rvl = new LinearLayoutManager(CancelApplicationActivity.this);
                                cla = new CancelListAdapter(null,regulariseDataList,CancelApplicationActivity.this);
                                cla.setHasStableIds(true);
                                cancelRegRecyclerView.setHasFixedSize(true);
                                cancelRegRecyclerView.setLayoutManager(rvl);
                                cancelRegRecyclerView.setHasFixedSize(true);
                                cancelRegRecyclerView.setItemViewCacheSize(100);
                                cancelRegRecyclerView.setAdapter(cla);
                            }
                        }
                    });
        }catch (NullPointerException e){
            Log.i("TAG", "getRegulariseData: "+e.getMessage());
        }

    }

    private void findViews(){
        cancelLeaveRecyclerView = findViewById(R.id.cancel_leave_list);
        cancelRegRecyclerView = findViewById(R.id.cancel_reg_list);
    }

}