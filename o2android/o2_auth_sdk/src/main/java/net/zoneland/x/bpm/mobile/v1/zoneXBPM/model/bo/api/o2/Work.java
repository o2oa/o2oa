package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.WorkVO;

import java.util.List;

/**
 * Created by fancyLou on 02/05/2018.
 * Copyright © 2018 O2. All rights reserved.
 */
public class Work extends WorkVO {


    private String serial;
    private String activity;
    private String activityType;
    private String activityName;
    private String activityToken;
    private String activityArrivedTime;
    private String workStatus;
    private boolean inquired;
    private int errorRetry;
    private List<String> manualTaskIdentityList;
    private boolean splitting;
    private List<String> splitTokenList;
    private String form;
    private boolean forceRoute;


    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityToken() {
        return activityToken;
    }

    public void setActivityToken(String activityToken) {
        this.activityToken = activityToken;
    }

    public String getActivityArrivedTime() {
        return activityArrivedTime;
    }

    public void setActivityArrivedTime(String activityArrivedTime) {
        this.activityArrivedTime = activityArrivedTime;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    public boolean isInquired() {
        return inquired;
    }

    public void setInquired(boolean inquired) {
        this.inquired = inquired;
    }

    public int getErrorRetry() {
        return errorRetry;
    }

    public void setErrorRetry(int errorRetry) {
        this.errorRetry = errorRetry;
    }

    public List<String> getManualTaskIdentityList() {
        return manualTaskIdentityList;
    }

    public void setManualTaskIdentityList(List<String> manualTaskIdentityList) {
        this.manualTaskIdentityList = manualTaskIdentityList;
    }

    public boolean isSplitting() {
        return splitting;
    }

    public void setSplitting(boolean splitting) {
        this.splitting = splitting;
    }

    public List<String> getSplitTokenList() {
        return splitTokenList;
    }

    public void setSplitTokenList(List<String> splitTokenList) {
        this.splitTokenList = splitTokenList;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public boolean isForceRoute() {
        return forceRoute;
    }

    public void setForceRoute(boolean forceRoute) {
        this.forceRoute = forceRoute;
    }
}
