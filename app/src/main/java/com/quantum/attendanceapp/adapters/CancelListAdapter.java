package com.quantum.attendanceapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.quantum.attendanceapp.R;
import com.quantum.attendanceapp.model.LeaveData;
import com.quantum.attendanceapp.model.RegulariseData;

import java.util.List;


public class CancelListAdapter extends RecyclerView.Adapter<CancelListAdapter.CancelViewHolder> {
    private List<LeaveData> leaveDataList;
    private List<RegulariseData> regulariseDataList;
    private Context context;

    public CancelListAdapter(List<LeaveData> leaveDataList, List<RegulariseData> regulariseDataList, Context context) {
        this.leaveDataList = leaveDataList;
        this.regulariseDataList = regulariseDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public CancelListAdapter.CancelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CancelListAdapter.CancelViewHolder cvh;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cancel_leave_list_layout, parent, false);
        cvh = new CancelListAdapter.CancelViewHolder(view);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull CancelViewHolder holder, int position) {
        int cancelWhat = -1;
        LeaveData leaveData;
        RegulariseData regulariseData;
        if (leaveDataList != null) {
            cancelWhat = 1;
            leaveData = leaveDataList.get(position);
            holder.tv1.setText(leaveData.getFromData());
            holder.tv2.setText(leaveData.getToData());
            holder.tv3.setVisibility(View.INVISIBLE);
            holder.reasonT.setText(leaveData.getReason());
            leaveDataList.remove(position);
            notifyDataSetChanged();
        } else {
            leaveData = null;
        }

        if(regulariseDataList != null){
            cancelWhat = 2;
            regulariseData = regulariseDataList.get(position);
            holder.tv1.setText(regulariseData.getDate());
            holder.tv2.setText(regulariseData.getNewInTime());
            holder.tv2.setText(regulariseData.getNewOutTime());
            holder.tv3.setVisibility(View.VISIBLE);
            holder.reasonT.setText(regulariseData.getReason());
            regulariseDataList.remove(position);
            notifyDataSetChanged();
        } else {
            regulariseData = null;
        }
        int finalCancelWhat = cancelWhat;
        holder.cancelBtn.setOnClickListener(v->{
           if(finalCancelWhat == 1){
               FirebaseFirestore database = FirebaseFirestore.getInstance();
               Query query = FirebaseFirestore.getInstance()
                       .collection("LeaveData").whereEqualTo("leaveId", leaveData.getLeaveId());
               query.get().addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {
                       for (QueryDocumentSnapshot document : task.getResult()) {
                           database.collection("LeaveData").document(document.getId()).delete();
                       }
                   }
               });

           }else if (finalCancelWhat == 2){
               FirebaseFirestore database = FirebaseFirestore.getInstance();
               Query query = database.collection("RegulariseData").whereEqualTo("id", regulariseData.getId());
               query.get().addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {
                       for (QueryDocumentSnapshot document : task.getResult()) {
                           database.collection("RegulariseData").document(document.getId()).delete();
                       }
                   }
               });

           }
        });
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (leaveDataList != null)
            size += leaveDataList.size();
        if (regulariseDataList != null)
            size += regulariseDataList.size();
        return size;
    }


    public static class CancelViewHolder extends RecyclerView.ViewHolder {

        TextView tv1, tv2, tv3, reasonT;
        Button cancelBtn;

        public CancelViewHolder(@NonNull View itemView) {
            super(itemView);
            tv1 = itemView.findViewById(R.id.tv_1);
            tv2 = itemView.findViewById(R.id.tv_2);
            tv3 = itemView.findViewById(R.id.tv_3);
            reasonT = itemView.findViewById(R.id.reason_tv);
            cancelBtn = itemView.findViewById(R.id.cancel_btn);
        }
    }
}