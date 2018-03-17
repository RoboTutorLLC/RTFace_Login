package com.example.iris.login1;

/**
 * Created by Iris on 16/7/20.
 */

public class UserInfo {
    public static final String ID = "_id";
    public static final String USERICON = "userIcon";
    public static final String USERVIDEO = "userVideo";
    public static final String RECORDTIME = "recordTime";
    public static final String LAST_LOGIN_TIME = "lastLogin";
    public static final String BIRTH_DEVICE = "birthDevice"; // i.e creation device
    public static final String BIRTH_DATE = "birthDate"; // i.e creation time

    private int id;
    private String userIcon;
    private String userVideo;
    private String recordTime;
    private String lastLogin;
    private String birthDevice;
    private String birthDate;

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getUserVideo() {
        return userVideo;
    }

    public void setUserVideo(String userVideo) {
        this.userVideo = userVideo;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public String getLastLoginTime() {
        return lastLogin;
    }

    public void setLastLoginTime(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getBirthDevice() {
        return birthDevice;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setBirthDevice(String birthDevice) {
        this.birthDevice = birthDevice;
    }

}
