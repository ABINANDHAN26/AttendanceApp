package com.quantum.attendanceapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;

public class CompanyData implements Parcelable {
    private String companyName,companyEmail,companyAddress,companyPhone;
    private List<String> branchList;
    private Map<String,List<String>> branchManagerList;

    public CompanyData() {
    }

    public CompanyData(String companyName, String companyEmail, String companyAddress, String companyPhone, List<String> branchList, Map<String, List<String>> branchManagerList) {
        this.companyName = companyName;
        this.companyEmail = companyEmail;
        this.companyAddress = companyAddress;
        this.companyPhone = companyPhone;
        this.branchList = branchList;
        this.branchManagerList = branchManagerList;
    }

    protected CompanyData(Parcel in) {
        companyName = in.readString();
        companyEmail = in.readString();
        companyAddress = in.readString();
        companyPhone = in.readString();
        branchList = in.createStringArrayList();
    }

    public static final Creator<CompanyData> CREATOR = new Creator<CompanyData>() {
        @Override
        public CompanyData createFromParcel(Parcel in) {
            return new CompanyData(in);
        }

        @Override
        public CompanyData[] newArray(int size) {
            return new CompanyData[size];
        }
    };

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public List<String> getBranchList() {
        return branchList;
    }

    public void setBranchList(List<String> branchList) {
        this.branchList = branchList;
    }

    public Map<String, List<String>> getBranchManagerList() {
        return branchManagerList;
    }

    public void setBranchManagerList(Map<String, List<String>> branchManagerList) {
        this.branchManagerList = branchManagerList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(companyName);
        dest.writeString(companyEmail);
        dest.writeString(companyAddress);
        dest.writeString(companyPhone);
        dest.writeStringList(branchList);
    }
}
