package com.quantum.attendanceapp.model;
import com.google.gson.annotations.Expose;
public class ReportData {
    @Expose
    private String empId,empName,presentDays,leavesTaken;

    public ReportData(String empId, String empName, String presentDays, String leavesTaken) {
        this.empId = empId;
        this.empName = empName;
        this.presentDays = presentDays;
        this.leavesTaken = leavesTaken;
    }

    public ReportData() {
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getPresentDays() {
        return presentDays;
    }

    public void setPresentDays(String presentDays) {
        this.presentDays = presentDays;
    }

    public String getLeavesTaken() {
        return leavesTaken;
    }

    public void setLeavesTaken(String leavesTaken) {
        this.leavesTaken = leavesTaken;
    }
}
