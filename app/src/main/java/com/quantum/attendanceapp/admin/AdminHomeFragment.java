package com.quantum.attendanceapp.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.quantum.attendanceapp.R;
import com.quantum.attendanceapp.adapters.AttendanceListAdapter;
import com.quantum.attendanceapp.adapters.LeaveListAdapter;
import com.quantum.attendanceapp.model.LeaveData;
import com.quantum.attendanceapp.model.RegulariseData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;

public class AdminHomeFragment extends Fragment {


    private RecyclerView leaveRecyclerView,attendanceRecyclerView;
    private RecyclerView.LayoutManager rvl;

    private TextView leaveTv,attendanceTv;
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

        addEmpBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(),AddEmpActivity.class)));

        genRepBtn.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Still developing", Toast.LENGTH_SHORT).show();
            if(true)
                return;
            getTimeData();
            try {
                Gson gson = new Gson();
//                String json = gson.toJson(myObject);
                String json= "";
                JSONArray jsonArray = new JSONArray(json);
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet sheet = workbook.createSheet("Data");

                Row headerRow = sheet.createRow(0);
                JSONObject firstObject = jsonArray.getJSONObject(0);
                int cellIndex = 0;
                for (Iterator<String> iterator = firstObject.keys(); iterator.hasNext(); ) {
                    String key = iterator.next();
                    Cell cell = headerRow.createCell(cellIndex++);
                    cell.setCellValue(key);
                }
                int rowIndex = 1;
                for (int i = 0; i < jsonArray.length(); i++) {
                    Row row = sheet.createRow(rowIndex++);
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    cellIndex = 0;
                    for (Iterator<String> iterator = jsonObject.keys(); iterator.hasNext(); ) {
                        String key = iterator.next();
                        Cell cell = row.createCell(cellIndex++);
                        cell.setCellValue(jsonObject.getString(key));
                    }
                }

                // Save workbook to file
                String fileName = "AttendanceReport.xlsx";
                FileOutputStream fileOut = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/" + fileName);
                workbook.write(fileOut);
                fileOut.close();
                workbook.close();
                Log.d("ExcelUtils", "Excel file saved to: " + fileName);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        });

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
                                    if(leaveData.getApproverName() == null || leaveData.getApproverName().trim().isEmpty())
                                        leaveDataList.add(leaveData);
                            }
                            if(leaveDataList.size() <= 0) {
                                leaveRecyclerView.setVisibility(View.GONE);
                                leaveTv.setText("No Leave Request's");
                            }
                            else {
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
        }catch (NullPointerException e){
            Log.i("TAG", "getLeaveData: "+e.getMessage());
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
                                if (!regulariseData.isAccepted())
                                    regulariseDataList.add(regulariseData);
                            }
                            if(regulariseDataList.size() <= 0) {
                                attendanceRecyclerView.setVisibility(View.GONE);
                                attendanceTv.setText("No Attendance Request's");
                            }
                            else{
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
        }catch (NullPointerException e){
            Log.i("TAG", "getRegulariseData: "+e.getMessage());
        }

    }
    private void getTimeData(){

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