package com.quantum.attendanceapp.model;

public class LeaveData {
    private String name,fromData,toData,leaveType,reason,approvedDate,leaveId,userId,approverName;
    private boolean isApproved,isCancelled;

    public LeaveData() {
    }

    public LeaveData(String name, String fromData, String toData, String leaveType, String reason, String approvedDate, String leaveId, String userId, String approverName, boolean isApproved, boolean isCancelled) {
        this.name = name;
        this.fromData = fromData;
        this.toData = toData;
        this.leaveType = leaveType;
        this.reason = reason;
        this.approvedDate = approvedDate;
        this.leaveId = leaveId;
        this.userId = userId;
        this.approverName = approverName;
        this.isApproved = isApproved;
        this.isCancelled = isCancelled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFromData() {
        return fromData;
    }

    public void setFromData(String fromData) {
        this.fromData = fromData;
    }

    public String getToData() {
        return toData;
    }

    public void setToData(String toData) {
        this.toData = toData;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(String approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(String leaveId) {
        this.leaveId = leaveId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }
}
