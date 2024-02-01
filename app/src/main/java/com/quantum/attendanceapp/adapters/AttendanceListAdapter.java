package com.quantum.attendanceapp.adapters;

import static com.quantum.attendanceapp.SplashScreen.userData;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.quantum.attendanceapp.R;
import com.quantum.attendanceapp.Utils.Util;
import com.quantum.attendanceapp.admin.AdminHomeFragment;
import com.quantum.attendanceapp.model.LeaveData;
import com.quantum.attendanceapp.model.RegulariseData;
import com.quantum.attendanceapp.model.TimeData;
import com.quantum.attendanceapp.model.UserData;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AttendanceListAdapter extends RecyclerView.Adapter<AttendanceListAdapter.AttendanceViewHolder> {

    private final Context context;
    private final List<RegulariseData> regulariseDataList;

    public AttendanceListAdapter(Context context, List<RegulariseData> regulariseDataList) {
        this.context = context;
        this.regulariseDataList = regulariseDataList;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AttendanceViewHolder lvh;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.regularise_request_list_layout, parent, false);
        lvh = new AttendanceViewHolder(view);
        return lvh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RegulariseData regulariseData = regulariseDataList.get(position);
        if(regulariseData.getUserName() != null)
            holder.nameTv.setText("Name: "+regulariseData.getUserName());
        else
            holder.nameTv.setText("");
        holder.dateTv.setText("Date: "+regulariseData.getDate());
        holder.newIntimeTv.setText("New in time: "+regulariseData.getNewInTime());
        holder.newOuttimeTv.setText("New out time: "+regulariseData.getNewOutTime());
        holder.reasonTv.setText("Reason: "+regulariseData.getReason());

        holder.acceptBtn.setOnClickListener(v -> {
            regulariseData.setAccepted(true);
            updateData(regulariseData);
            regulariseDataList.remove(position);
            notifyDataSetChanged();
        });
        holder.denyBtn.setOnClickListener(v -> {
            regulariseData.setAccepted(false);
            updateData(regulariseData);
            regulariseDataList.remove(position);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return regulariseDataList.size();
    }


    private String getDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private void updateData(RegulariseData regulariseData) {
        regulariseData.setAcceptedOn(getDate());
        regulariseData.setAcceptedBy(userData.getUserName());
        String uid = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        if(uid != null) {
            Query query = database.collection("RegulariseData").whereEqualTo("id", regulariseData.getId());
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        database.collection("RegulariseData").document(document.getId()).update("accepted", regulariseData.isAccepted());
                        database.collection("RegulariseData").document(document.getId()).update("acceptedOn", regulariseData.getAcceptedOn());
                        database.collection("RegulariseData").document(document.getId()).update("acceptedBy", regulariseData.getAcceptedBy());
                    }
                    String subject = "";
                    String body = "";
                    if(regulariseData.isAccepted()){
                        subject = "Attendance Application Request : Approved";
                        body = "Your request for regularize attendance on "+regulariseData.getDate()+" as been accepted by "+regulariseData.getAcceptedBy()+".";
                        FirebaseFirestore timeDataDb = FirebaseFirestore.getInstance();
                        String date = regulariseData.getDate();
                        String monthYear = date.substring(date.indexOf("-") + 1);
                        monthYear = monthYear.replace("-", "");

                        TimeData timeData = new TimeData();
                        timeData.setDate(regulariseData.getDate());
                        timeData.setInTime(regulariseData.getNewInTime());
                        timeData.setOutTime(regulariseData.getNewOutTime());
                        timeData.setInHrs(getInHrs(regulariseData.getNewInTime(), regulariseData.getNewOutTime()));
                        timeData.setLeave(false);
                        timeData.setLeaveType("");
                        timeDataDb.collection("TimeData").document(regulariseData.getUid()).collection(monthYear).document(date).set(timeData);
                    }else{
                        subject = "Attendance Application Request : Rejected";
                        body = "Your request for regularize attendance on "+regulariseData.getDate()+" as been rejected by "+regulariseData.getAcceptedBy()+".";
                    }
                    Map<String, String> requestData = new HashMap<>();
                    String finalSubject = subject;
                    String finalBody = body;
                    FirebaseFirestore.getInstance().collection("Data").document(regulariseData.getUid()).get()
                            .addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()){
                                    DocumentSnapshot result = task1.getResult();
                                    UserData object = result.toObject(UserData.class);
                                    String emailId = "abinandhan952@gmail.com";
                                    if(object != null)
                                        emailId = object.getEmailId();
                                    Log.i("TAG", "updateData: "+emailId);
                                    requestData.put("email", emailId);
                                    requestData.put("subject", finalSubject);
                                    requestData.put("body", finalBody);
                                    Gson gson = new Gson();
                                    String jsonData = gson.toJson(requestData);
                                    try {
                                        Util.sendPostRequest("http://192.168.245.165:5000/send_email", jsonData);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Log.i("TAG", "updateData: "+e.getMessage());
                                    }
                                }
                            });

                }
            });

        }
    }


    private String getInHrs(String startTime, String endTime) {
        String value = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);
            long timeDifferenceMillis = endDate.getTime() - startDate.getTime();
            long hours = timeDifferenceMillis / (60 * 60 * 1000);
            long minutes = (timeDifferenceMillis % (60 * 60 * 1000)) / (60 * 1000);
            value = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
        } catch (Exception e) {
        }
        return value;
    }

    public static class AttendanceViewHolder extends RecyclerView.ViewHolder {

        TextView nameTv,dateTv, newIntimeTv, newOuttimeTv, reasonTv;
        Button acceptBtn, denyBtn;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.emp_name_tv);
            dateTv = itemView.findViewById(R.id.date_tv);
            newIntimeTv = itemView.findViewById(R.id.time_in_tv);
            newOuttimeTv = itemView.findViewById(R.id.time_out_tv);
            reasonTv = itemView.findViewById(R.id.reason_tv);
            acceptBtn = itemView.findViewById(R.id.accept_btn);
            denyBtn = itemView.findViewById(R.id.deny_btn);
        }
    }
}
