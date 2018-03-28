package com.example.iris.login1;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zero on 2016/9/16.
 */
public class Common {

    // different app states
    public enum STATE {
        IF_YOU_SEE_YOUR_PICTURE, PLEASE_TAP_ON_YOUR_PICTURE, TO_SEE_MORE_PICTURES,
        IF_YOU_DONT_FIND_YOUR_PICTURE, SLIDE_THEM_LIKE_THIS, TAP_HERE_RECORD, PLEASE_SAY_YOUR_NAME,
        YOU_SAID, IF_YOU_LIKE_YOUR_PICTURE_AND_HOW_YOU_SAID_YOUR_NAME, TAP_HERE_LIKE_PICTURE,
        TAP_HERE_DISLIKE_PICTURE, GOOD, NOW, OKAY_LETS_TRY_AGAIN, IF_THIS_IS_YOU,
        TAP_HERE_IF_IS_YOU, IF_THIS_IS_NOT_YOU, TAP_HERE_IF_IS_NOT_YOU, LETS_GET_STARTED,
        LETS_TRY_AGAIN, THIS_IS_ROBOTUTOR,PLEASE_TAP_HERE_TO_GO_ON,
        // MARCH NEW STATES
        IF_YOUVE_USED_ROBOTUTOR_BEFORE, TAP_HERE_YES, IF_YOUVE_NEVER_USED_ROBOTUTOR_BEFORE,
        TAP_HERE_NO, IF_YOURE_A_BOY, TAP_HERE_BOY, IF_YOURE_A_GIRL, TAP_HERE_GIRL,
        IF_YOU_LIKE_THIS_PICTURE, TAP_HERE_YES_ICON, IF_YOU_WANT_TO_SEE_A_DIFFERENT_PICTURE,
        TAP_HERE_NO_ICON,
        IF_YOU_WANT_TO_SEE_A_DIFFERENT_PICTURE_LOGIN, TAP_HERE_NO_LOGIN_ICON, TAP_HERE_CANT_FIND
    }

    public static final String[] ANIMAL_NAMES = {"bat", "bear", "bee", "bird", "bug", "butterfly",
        "camel", "cat", "cheetah", "chicken", "cow", "crocodile", "dinosaur", "dog", "dolphin", "dove",
        "duck", "eagle", "elephant", "fish", "flamingo", "fox", "frog", "giraffe", "gorilla", "horse",
        "kangaroo", "koala", "leopard", "lion", "monkey", "mouse", "palm", "panda", "parrot", "penguin",
        "shark", "sheep", "snake", "spider", "squirrel", "starfish", "tiger", "turtle", "wolf", "zebra"};

    public static final int[] ANIMAL_PATHS = {R.drawable.bat, R.drawable.bear, R.drawable.bee, R.drawable.bird,
            R.drawable.bug, R.drawable.butterfly, R.drawable.camel, R.drawable.cat, R.drawable.cheetah,
            R.drawable.chicken, R.drawable.cow, R.drawable.crocodile, R.drawable.dinosaur, R.drawable.dog,
            R.drawable.dolphin, R.drawable.dove, R.drawable.duck, R.drawable.eagle, R.drawable.elephant,
            R.drawable.fish, R.drawable.flamingo, R.drawable.fox, R.drawable.frog, R.drawable.giraffe,
            R.drawable.gorilla, R.drawable.horse, R.drawable.kangaroo, R.drawable.koala, R.drawable.leopard,
            R.drawable.lion, R.drawable.monkey, R.drawable.mouse, R.drawable.palm, R.drawable.panda,
            R.drawable.parrot, R.drawable.penguin, R.drawable.shark, R.drawable.sheep, R.drawable.snake,
            R.drawable.spider, R.drawable.squirrel, R.drawable.starfish, R.drawable.tiger, R.drawable.turtle,
            R.drawable.wolf, R.drawable.zebra};

    public static final Map<String, Integer> ANIMALS = new HashMap<String, Integer>() {{
        for(int i = 0; i < ANIMAL_NAMES.length; i++){
            put(ANIMAL_NAMES[i], ANIMAL_PATHS[i]);
        }
    }};

    //indicate the delay time
    public static final int DELAY_TO_SHOW_VIDEO_FULL_SCREEN = 7000;
    public static final int DELAY_TO_SHOW_VIDEO = 5000;
    public static final int DELAY_TO_REPROMPT = 5000;
    public static final int DELAY_TO_SHOW_CHANGE_OF_FINGER = 300;
    public static final int DELAY_AFTER_SHOWING_SLIDE = 3000;

    //indicate duration of recording
    public static final int RECORD_TIME = 2000;
    public static final int CAPTURE_FRAME_TIME_GAP = 1000;

    // indicate duration of RECORD prompt

    // open the "[eng|swa]_pleasesayourname.wav" audio files with a sound-editing app like
    // Audacity and check the length of silence at the end
    public static final int TRAILING_SILENCE_EN = 400;
    public static final int TRAILING_SILENCE_SW = 1200;

    //indicate type of message
    public static final int READY_TO_RECORD = 1;
    public static final int RECORD_DONE = 2;
    public static final int REPLAY_NEW_VIDEO_DONE = 3;
    public static final int REFRESH_GALLERY = 4;
    public static final int REPLAY_EXISTING_VIDEO_DONE = 5;
    public static final int PLAY_TAPPING_VIDEO_DONE = 6;
    public static final int UNCOVER_SCREEN = 7;

    //indicate which view to flash or stop flashing
    public static final int FLASH_CAPTURE = 1;
    public static final int FLASH_LIKE = 2;
    public static final int FLASH_DISLIKE = 3;
    public static final int FLASH_BOY = 4;
    public static final int FLASH_GIRL = 5;

    //indicate time when flash
    public static final int FLASH_DURATION = 2000;
    public static final int FLASH_FREQUENCE = 200;

    public static final String LANG_SW = "LANG_SW";
    public static final String LANG_EN = "LANG_EN";

    // For passing data to RoboTutor
    public static final String ROBOTUTOR_PACKAGE_ADDRESS = "cmu.xprize.robotutor";
    public static final String STUDENT_ID_VAR = "studentId";
    public static final String SESSION_ID_VAR = "sessionId";

    // File-naming conventions
    public static final String FACE_LOGIN_PATH = "/sdcard/FaceLogin";
    public static final String IMAGE_FILE_PREFIX = "IMAGE_";
    public static final String IMAGE_FILE_SUFFIX = ".jpg";
}
