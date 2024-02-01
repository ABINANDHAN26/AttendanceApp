package com.quantum.attendanceapp.model;

public class RegulariseData {
    private String userName,uid,date,newInTime,newOutTime,reason,acceptedOn,acceptedBy,id;
    private boolean isAccepted;

    public RegulariseData() {
    }

    public RegulariseData(String userName, String uid, String date, String newInTime, String newOutTime, String reason, String acceptedOn, String acceptedBy, String id, boolean isAccepted) {
        this.userName = userName;
        this.uid = uid;
        this.date = date;
        this.newInTime = newInTime;
        this.newOutTime = newOutTime;
        this.reason = reason;
        this.acceptedOn = acceptedOn;
        this.acceptedBy = acceptedBy;
        this.id = id;
        this.isAccepted = isAccepted;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNewInTime() {
        return newInTime;
    }

    public void setNewInTime(String newInTime) {
        this.newInTime = newInTime;
    }

    public String getNewOutTime() {
        return newOutTime;
    }

    public void setNewOutTime(String newOutTime) {
        this.newOutTime = newOutTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAcceptedOn() {
        return acceptedOn;
    }

    public void setAcceptedOn(String acceptedOn) {
        this.acceptedOn = acceptedOn;
    }

    public String getAcceptedBy() {
        return acceptedBy;
    }

    public void setAcceptedBy(String acceptedBy) {
        this.acceptedBy = acceptedBy;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
