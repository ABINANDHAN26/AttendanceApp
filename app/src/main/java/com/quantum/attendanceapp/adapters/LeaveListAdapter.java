package com.quantum.attendanceapp.adapters;

import static com.quantum.attendanceapp.SplashScreen.*;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.quantum.attendanceapp.R;
import com.quantum.attendanceapp.SplashScreen;
import com.quantum.attendanceapp.model.LeaveData;
import com.quantum.attendanceapp.model.TimeData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LeaveListAdapter extends RecyclerView.Adapter<LeaveListAdapter.LeaveViewHolder> {

    private final Context context;
    private final List<LeaveData> leaveDataList;

    public LeaveListAdapter(Context context, List<LeaveData> leaveDataList) {
        this.context = context;
        this.leaveDataList = leaveDataList;
    }

    @NonNull
    @Override
    public LeaveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LeaveViewHolder lvh;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leave_request_list_layout, parent, false);
        lvh = new LeaveViewHolder(view);
        return lvh;
    }

    @Override
    public void onBindViewHolder(@NonNull LeaveViewHolder holder, int position) {
        LeaveData leaveData = leaveDataList.get(position);
        if(leaveData.getName() != null)
            holder.nameTv.setText(leaveData.getName());
        else
            holder.nameTv.setText("");
        holder.fromTv.setText(leaveData.getFromData());
        holder.toTv.setText(leaveData.getToData());
        holder.leaveTypeTv.setText(leaveData.getLeaveType());

        holder.acceptBtn.setOnClickListener(v -> {
            leaveData.setApproved(true);
            updateData(leaveData);
        });
        holder.denyBtn.setOnClickListener(v -> {
            leaveData.setApproved(false);
            updateData(leaveData);
        });
    }

    @Override
    public int getItemCount() {
        return leaveDataList.size();
    }


    private String getDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private void updateData(LeaveData leaveData) {
        leaveData.setApprovedDate(getDate());
        leaveData.setApproverName(userData.getUserName());
        String uid = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        if(uid != null) {
            Query query = database.collection("LeaveData").whereEqualTo("leaveId", leaveData.getLeaveId());
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        database.collection("LeaveData").document(document.getId()).update("approved", leaveData.isApproved());
                        database.collection("LeaveData").document(document.getId()).update("approvedDate", leaveData.getApprovedDate());
                        database.collection("LeaveData").document(document.getId()).update("approverName", leaveData.getApproverName());
                    }
                    //Send E-mail
                }
            });

        }
    }

    public static class LeaveViewHolder extends RecyclerView.ViewHolder {

        TextView nameTv, fromTv, toTv, leaveTypeTv;
        Button acceptBtn, denyBtn;

        public LeaveViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.emp_name_tv);
            fromTv = itemView.findViewById(R.id.from_date_tv);
            toTv = itemView.findViewById(R.id.to_date_tv);
            leaveTypeTv = itemView.findViewById(R.id.leave_type_tv);
            acceptBtn = itemView.findViewById(R.id.leave_accept_btn);
            denyBtn = itemView.findViewById(R.id.leave_deny_btn);
        }
    }
}
