package com.quantum.attendanceapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.DayOfWeek;

public class UserData implements Parcelable {
    private String userName,emailId,empId,userId,userType,dob,doj,qualification,address,phone,branch;
    private String reportingManagerName,reportingManagerId,reportingManagerEmail;
    private String availableLeave,availedLeave;
    private String weeklyOff;
    public UserData() {
    }

    public UserData(String userName, String emailId, String empId, String userId, String userType, String dob, String doj, String qualification, String address, String phone, String branch, String reportingManagerName, String reportingManagerId, String reportingManagerEmail, String availableLeave, String availedLeave, String weeklyOff) {
        this.userName = userName;
        this.emailId = emailId;
        this.empId = empId;
        this.userId = userId;
        this.userType = userType;
        this.dob = dob;
        this.doj = doj;
        this.qualification = qualification;
        this.address = address;
        this.phone = phone;
        this.branch = branch;
        this.reportingManagerName = reportingManagerName;
        this.reportingManagerId = reportingManagerId;
        this.reportingManagerEmail = reportingManagerEmail;
        this.availableLeave = availableLeave;
        this.availedLeave = availedLeave;
        this.weeklyOff = weeklyOff;
    }

    protected UserData(Parcel in) {
        userName = in.readString();
        emailId = in.readString();
        empId = in.readString();
        userId = in.readString();
        userType = in.readString();
        dob = in.readString();
        doj = in.readString();
        qualification = in.readString();
        address = in.readString();
        phone = in.readString();
        branch = in.readString();
        reportingManagerName = in.readString();
        reportingManagerId = in.readString();
        reportingManagerEmail = in.readString();
        availableLeave = in.readString();
        availedLeave = in.readString();
        weeklyOff = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(emailId);
        dest.writeString(empId);
        dest.writeString(userId);
        dest.writeString(userType);
        dest.writeString(dob);
        dest.writeString(doj);
        dest.writeString(qualification);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(branch);
        dest.writeString(reportingManagerName);
        dest.writeString(reportingManagerId);
        dest.writeString(reportingManagerEmail);
        dest.writeString(availableLeave);
        dest.writeString(availedLeave);
        dest.writeString(weeklyOff);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserData> CREATOR = new Creator<UserData>() {
        @Override
        public UserData createFromParcel(Parcel in) {
            return new UserData(in);
        }

        @Override
        public UserData[] newArray(int size) {
            return new UserData[size];
        }
    };

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getDoj() {
        return doj;
    }

    public void setDoj(String doj) {
        this.doj = doj;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getReportingManagerName() {
        return reportingManagerName;
    }

    public void setReportingManagerName(String reportingManagerName) {
        this.reportingManagerName = reportingManagerName;
    }

    public String getReportingManagerId() {
        return reportingManagerId;
    }

    public void setReportingManagerId(String reportingManagerId) {
        this.reportingManagerId = reportingManagerId;
    }

    public String getReportingManagerEmail() {
        return reportingManagerEmail;
    }

    public void setReportingManagerEmail(String reportingManagerEmail) {
        this.reportingManagerEmail = reportingManagerEmail;
    }

    public String getAvailableLeave() {
        return availableLeave;
    }

    public void setAvailableLeave(String availableLeave) {
        this.availableLeave = availableLeave;
    }

    public String getAvailedLeave() {
        return availedLeave;
    }

    public void setAvailedLeave(String availedLeave) {
        this.availedLeave = availedLeave;
    }

    public String getWeeklyOff() {
        return weeklyOff;
    }

    public void setWeeklyOff(String weeklyOff) {
        this.weeklyOff = weeklyOff;
    }
}

