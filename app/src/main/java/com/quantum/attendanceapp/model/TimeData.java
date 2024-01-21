package com.quantum.attendanceapp.model;

public class TimeData {
    private String date,inTime,outTime,inHrs,leaveType;
    private boolean isLeave;

    public TimeData() {
    }

    public TimeData(String date, String inTime, String outTime, String inHrs, String leaveType, boolean isLeave) {
        this.date = date;
        this.inTime = inTime;
        this.outTime = outTime;
        this.inHrs = inHrs;
        this.leaveType = leaveType;
        this.isLeave = isLeave;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getInHrs() {
        return inHrs;
    }

    public void setInHrs(String inHrs) {
        this.inHrs = inHrs;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public boolean isLeave() {
        return isLeave;
    }

    public void setLeave(boolean leave) {
        isLeave = leave;
    }
}

