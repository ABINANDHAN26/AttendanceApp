package com.quantum.attendanceapp.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.quantum.attendanceapp.R;
import com.quantum.attendanceapp.adapters.LeaveListAdapter;
import com.quantum.attendanceapp.model.LeaveData;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeFragment extends Fragment {


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager rvl;
    private LeaveListAdapter lla;
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

    }

    private void getLeaveData(){
        try {
            String uid = FirebaseAuth.getInstance().getUid();
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection("LeaveData").get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<LeaveData> leaveDataList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                LeaveData leaveData = document.toObject(LeaveData.class);
                                if (!leaveData.isApproved())
                                    leaveDataList.add(leaveData);
                            }

                            rvl = new LinearLayoutManager(getActivity());
                            lla = new LeaveListAdapter(getActivity(), leaveDataList);
                            lla.setHasStableIds(true);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(rvl);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setItemViewCacheSize(100);
                            recyclerView.setAdapter(lla);
                        }
                    });
        }catch (NullPointerException e){
            Log.i("TAG", "getLeaveData: "+e.getMessage());
        }

    }


    private void findViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        addEmpBtn = view.findViewById(R.id.add_emp_btn);
        genRepBtn = view.findViewById(R.id.gen_rep_btn);
    }
}