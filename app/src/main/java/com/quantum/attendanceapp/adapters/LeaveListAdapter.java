package com.quantum.attendanceapp.adapters;

import static androidx.core.content.ContextCompat.startActivity;
import static com.quantum.attendanceapp.SplashScreen.userData;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.quantum.attendanceapp.R;
import com.quantum.attendanceapp.SplashScreen;
import com.quantum.attendanceapp.Utils.Util;
import com.quantum.attendanceapp.admin.AdminHomeFragment;
import com.quantum.attendanceapp.model.LeaveData;
import com.quantum.attendanceapp.model.TimeData;
import com.quantum.attendanceapp.model.UserData;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LeaveViewHolder holder, int position) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        LeaveData leaveData = leaveDataList.get(position);
        if(leaveData.getName() != null)
            holder.nameTv.setText("Name: "+leaveData.getName());
        else
            holder.nameTv.setText("");
        holder.fromTv.setText("From: "+leaveData.getFromData());
        holder.toTv.setText("To: "+leaveData.getToData());
        holder.leaveTypeTv.setText("Leave Type: "+leaveData.getLeaveType());
        holder.reasonTv.setText("Reason: "+leaveData.getReason());
        holder.acceptBtn.setOnClickListener(v -> {
            leaveData.setApproved(true);
            updateData(leaveData);
            leaveDataList.remove(position);
            notifyDataSetChanged();
        });
        holder.denyBtn.setOnClickListener(v -> {
            leaveData.setApproved(false);
            updateData(leaveData);
            leaveDataList.remove(position);
            notifyDataSetChanged();
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
            FirebaseFirestore.getInstance().collection("Data").document(leaveData.getUserId()).get()
                    .addOnCompleteListener(t -> {
                       if(t.isSuccessful()){
                           UserData empData = t.getResult().toObject(UserData.class);
                           Query query = database.collection("LeaveData").whereEqualTo("leaveId", leaveData.getLeaveId());
                           query.get().addOnCompleteListener(task -> {
                               if (task.isSuccessful()) {
                                   for (QueryDocumentSnapshot document : task.getResult()) {
                                       database.collection("LeaveData").document(document.getId()).update("approved", leaveData.isApproved());
                                       database.collection("LeaveData").document(document.getId()).update("approvedDate", leaveData.getApprovedDate());
                                       database.collection("LeaveData").document(document.getId()).update("approverName", leaveData.getApproverName());
                                   }

                                   String subject = "";
                                   String body = "";
                                   if(leaveData.isApproved()){
                                       subject = "Leave Application Request : Approved";
                                       body = "Your leave request from "+leaveData.getFromData()+" to "+leaveData.getToData()+" as been approved by "+leaveData.getApproverName()+".";
                                       FirebaseFirestore timeDataDb = FirebaseFirestore.getInstance();
                                       String fromDate = leaveData.getFromData();
                                       String toDate = leaveData.getToData();
                                       List<String> dateList = Util.getDateList(fromDate,toDate);
                                       for(String date:dateList){
                                           assert empData != null;
                                           if(Util.isGivenDate(date, empData.getWeeklyOff())){
                                               continue;
                                           }
                                           String monthYear = date.substring(date.indexOf("-") + 1);
                                           monthYear = monthYear.replace("-", "");
                                           TimeData timeData = new TimeData();
                                           timeData.setDate(date);
                                           timeData.setInTime("");
                                           timeData.setOutTime("");
                                           timeData.setInHrs("");
                                           timeData.setLeave(true);
                                           timeData.setLeaveType(leaveData.getLeaveType());
                                           timeDataDb.collection("TimeData").document(leaveData.getUserId()).collection(monthYear).document(date).set(timeData);
                                       }

                                   }else{
                                       subject = "Leave Application Request : Rejected";
                                       body = "Your leave request from "+leaveData.getFromData()+" to "+leaveData.getToData()+" as been rejected by "+leaveData.getApproverName()+".";
                                   }

                                   String finalSubject = subject;
                                   String finalBody = body;
                                   FirebaseFirestore.getInstance().collection("Data").document(leaveData.getUserId()).get()
                                           .addOnCompleteListener(task1 -> {
                                               if(task1.isSuccessful()){
                                                   DocumentSnapshot result = task1.getResult();
                                                   UserData object = result.toObject(UserData.class);
                                                   String emailId = "abinandhan952@gmail.com";
                                                   if(object!=null)
                                                       emailId = object.getEmailId();
                                                   Map<String, String> requestData = new HashMap<>();
                                                   requestData.put("email", emailId);
                                                   requestData.put("subject", finalSubject);
                                                   requestData.put("body", finalBody);
                                                   Gson gson = new Gson();
                                                   String jsonData = gson.toJson(requestData);
                                                   try {
                                                       Util.sendPostRequest("http://192.168.245.165:5000/send_email", jsonData);
                                                   } catch (IOException e) {
                                                       e.printStackTrace();
                                                   }
                                               }
                                           });


                               }
                           });

                       }
                    });

        }
    }

    public static class LeaveViewHolder extends RecyclerView.ViewHolder {

        TextView nameTv, fromTv, toTv, leaveTypeTv,reasonTv;
        Button acceptBtn, denyBtn;

        public LeaveViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.emp_name_tv);
            fromTv = itemView.findViewById(R.id.from_date_tv);
            toTv = itemView.findViewById(R.id.to_date_tv);
            leaveTypeTv = itemView.findViewById(R.id.leave_type_tv);
            reasonTv = itemView.findViewById(R.id.reason_tv);
            acceptBtn = itemView.findViewById(R.id.leave_accept_btn);
            denyBtn = itemView.findViewById(R.id.leave_deny_btn);
        }
    }
}
