package com.example.iris.login1;

/**
 * Created by Iris on 16/7/20.
 */

public class UserInfo {
    public static final String ID = "_id";
    public static final String USERICON = "userIcon";
    public static final String USERVIDEO = "userVideo";
    public static final String RECORDTIME = "recordTime";

    private int id;
    private String userIcon;
    private String userVideo;
    private String recordTime;

    public int getID(){
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getUserIcon(){ return userIcon;}

    public void setUserIcon(String userIcon){
        this.userIcon = userIcon;
    }

    public String getUserVideo(){return userVideo;}

    public void setUserVideo(String userVideo) {this.userVideo = userVideo;}

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }
}
