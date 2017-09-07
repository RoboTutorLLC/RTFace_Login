package com.example.iris.login1;

/**
 * Created by Zero on 2016/9/16.
 */
public class Common {

    //indicate the delay time
    public static final int DELAY_TO_SHOW_VIDEO_FULL_SCREEN = 7000;
    public static final int DELAY_TO_SHOW_VIDEO = 5000;
    public static final int DELAY_TO_REPROMPT = 5000;
    public static final int DELAY_TO_SHOW_CHANGE_OF_FINGER = 300;
    public static final int DELAY_AFTER_SHOWING_SLIDE = 3000;

    //indicate duration of recording
    public static final int RECORD_TIME = 2000;
    public static final int CAPTURE_FRAME_TIME_GAP = 1000;

    //indicate type of message
    public static final int READY_TO_RECORD = 1;
    public static final int RECORD_DONE = 2;
    public static final int REPLAY_NEW_VIDEO_DONE = 3;
    public static final int REFRESH_GALLERY = 4;
    public static final int REPLAY_OLD_VIDEO_DONE = 5;
    public static final int PLAY_TAPPING_VIDEO_DONE = 6;
    public static final int UNCOVER_SCREEN = 7;

    //indicate which view to flash or stop flashing
    public static final int FLASH_CAPTURE = 1;
    public static final int FLASH_LIKE = 2;
    public static final int FLASH_DISLIKE = 3;

    //indicate time when flash
    public static final int FLASH_DURATION = 2000;
    public static final int FLASH_FREQUENCE = 200;

    public static final String LANG_SW = "LANG_SW";
    public static final String LANG_EN = "LANG_EN";

    // RoboTutor Package address
    public static final String ROBOTUTOR_PACKAGE_ADDRESS = "cmu.xprize.robotutor";
}
