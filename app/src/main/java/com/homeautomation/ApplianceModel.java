package com.homeautomation;

public class ApplianceModel {

    public String title;
    public int status;
    public String appID;
    public int pinNo;

    public ApplianceModel() {
    }

    public ApplianceModel(String title, int status, String appID, int pinNo) {
        this.title = title;
        this.status = status;
        this.appID = appID;
        this.pinNo = pinNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public int getpinNo() {
        return pinNo;
    }

    public void setpinNo(int pinNo) {
        this.pinNo = pinNo;
    }
}
