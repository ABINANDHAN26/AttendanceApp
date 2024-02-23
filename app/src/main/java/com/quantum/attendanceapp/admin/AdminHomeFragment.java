package com.quantum.attendanceapp.admin;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.quantum.attendanceapp.R;
import com.quantum.attendanceapp.Utils.GenerateReport;
import com.quantum.attendanceapp.adapters.AttendanceListAdapter;
import com.quantum.attendanceapp.adapters.LeaveListAdapter;
import com.quantum.attendanceapp.model.LeaveData;
import com.quantum.attendanceapp.model.RegulariseData;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeFragment extends Fragment {


    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private RecyclerView leaveRecyclerView, attendanceRecyclerView;
    private RecyclerView.LayoutManager rvl;
    private TextView leaveTv, attendanceTv;
    private LeaveListAdapter lla;
    private AttendanceListAdapter ala;
    private Button addEmpBtn, genRepBtn;

    public AdminHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);
        findViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getLeaveData();
        getRegulariseData();

        addEmpBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddEmpActivity.class)));

        genRepBtn.setOnClickListener(v -> {
            GenerateReport generateReport = new GenerateReport(getContext());

            generateReport.genReport();


        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check if the request is for storage permission
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GenerateReport generateReport = new GenerateReport(getContext());
                generateReport.genReport();
            } else {
                Toast.makeText(getContext(), "Permission required to store the file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLeaveData() {
        try {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection("LeaveData").get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<LeaveData> leaveDataList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                LeaveData leaveData = document.toObject(LeaveData.class);
                                if (!leaveData.isApproved())
                                    if (leaveData.getApproverName() == null || leaveData.getApproverName().trim().isEmpty())
                                        leaveDataList.add(leaveData);
                            }
                            if (leaveDataList.size() <= 0) {
                                leaveRecyclerView.setVisibility(View.GONE);
                                leaveTv.setText("No Leave Request's");
                            } else {
                                rvl = new LinearLayoutManager(getActivity());
                                lla = new LeaveListAdapter(getActivity(), leaveDataList);
                                lla.setHasStableIds(true);
                                leaveRecyclerView.setHasFixedSize(true);
                                leaveRecyclerView.setLayoutManager(rvl);
                                leaveRecyclerView.setHasFixedSize(true);
                                leaveRecyclerView.setItemViewCacheSize(100);
                                leaveRecyclerView.setAdapter(lla);
                            }
                        }
                    });
        } catch (NullPointerException e) {
            Log.i("TAG", "getLeaveData: " + e.getMessage());
        }

    }

    private void getRegulariseData() {
        try {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection("RegulariseData").get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<RegulariseData> regulariseDataList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                RegulariseData regulariseData = document.toObject(RegulariseData.class);
                                if (!regulariseData.isAccepted())
                                    regulariseDataList.add(regulariseData);
                            }
                            if (regulariseDataList.size() <= 0) {
                                attendanceRecyclerView.setVisibility(View.GONE);
                                attendanceTv.setText("No Attendance Request's");
                            } else {
                                rvl = new LinearLayoutManager(getActivity());
                                ala = new AttendanceListAdapter(getActivity(), regulariseDataList);
                                ala.setHasStableIds(true);
                                attendanceRecyclerView.setHasFixedSize(true);
                                attendanceRecyclerView.setLayoutManager(rvl);
                                attendanceRecyclerView.setHasFixedSize(true);
                                attendanceRecyclerView.setItemViewCacheSize(100);
                                attendanceRecyclerView.setAdapter(ala);
                            }
                        }
                    });
        } catch (NullPointerException e) {
            Log.i("TAG", "getRegulariseData: " + e.getMessage());
        }

    }

    private void getTimeData() {

    }


    private void findViews(View view) {
        leaveRecyclerView = view.findViewById(R.id.leave_recycler_view);
        attendanceRecyclerView = view.findViewById(R.id.regularise_recycler_view);
        addEmpBtn = view.findViewById(R.id.add_emp_btn);
        genRepBtn = view.findViewById(R.id.gen_rep_btn);

        leaveTv = view.findViewById(R.id.leave_tv);
        attendanceTv = view.findViewById(R.id.attendance_tv);

    }
}