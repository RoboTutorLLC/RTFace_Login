package com.example.iris.login1;

import android.util.Pair;

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
        LETS_TRY_AGAIN, THIS_IS_ROBOTUTOR,PLEASE_TAP_HERE_TO_GO_ON, TAP_HERE_RECORD_REC, TO_TAKE_ANOTHER_PICTURE,
        // MARCH NEW STATES
        IF_YOUVE_USED_ROBOTUTOR_BEFORE, TAP_HERE_YES, IF_YOUVE_NEVER_USED_ROBOTUTOR_BEFORE,
        TAP_HERE_NO, IF_YOURE_A_BOY, TAP_HERE_BOY, IF_YOURE_A_GIRL, TAP_HERE_GIRL,
        IF_YOU_LIKE_THIS_PICTURE, TAP_HERE_YES_ICON, IF_YOU_WANT_TO_SEE_A_DIFFERENT_PICTURE,
        TAP_HERE_NO_ICON,
        IF_YOU_WANT_TO_SEE_A_DIFFERENT_PICTURE_LOGIN, TAP_HERE_NO_LOGIN_ICON, TAP_HERE_CANT_FIND
    }

    public static final String[] ANIMAL_NAMES_ENG = {"bat", "bear", "bee", "buffalo", "butterfly",
        "camel", "cat", "cheetah", "chicken", "chimpanzee", "cow", "crocodile", "dog", "donkey", "dove",
        "duck", "eagle", "elephant", "fish", "flamingo", "fox", "frog", "giraffe", "goat", "gorilla", "hippo", "horse",
        "hyena", "kangaroo", "leopard", "lion", "monkey", "mouse", "ostrich", "parrot","rabbit",
            "sheep", "snake", "spider", "squirrel", "turtle", "wolf", "zebra"};

    // chimp and gorilla => sokwe
    // zebra and donkey => punda

    public static final String[] ANIMAL_NAMES_SWA = {"popo", "dubu", "nyuki", "nyati", "kipepeo",
            "ngamia", "paka", "duma", "kuku", "sokwe (chimpanzee)", "ng'ombe", "mamba", "mbwa", "punda (donkey)", "njiwa",
            "bata", "tai", "tembo", "samaki", "korongo", "mbweha", "chupa", "twiga", "mbuzi", "sokwe (gorilla)", "kiboko", "farasi",
            "fisi", "kangaruu", "chui", "simba", "tumbili", "panya", "mbuni", "kasuku","sungura",
            "kondoo", "nyoka", "buibui", "kuchakuro", "kobe", "mbwa mwitu", "punda (zebra)"};

    public static final int[] ANIMAL_PATHS = {R.drawable.bat, R.drawable.bear, R.drawable.bee, R.drawable.buffalo,
            R.drawable.butterfly, R.drawable.camel, R.drawable.cat, R.drawable.cheetah,
            R.drawable.chicken, R.drawable.chimpanzee, R.drawable.cow, R.drawable.crocodile, R.drawable.dog,
            R.drawable.donkey, R.drawable.dove, R.drawable.duck, R.drawable.eagle, R.drawable.elephant,
            R.drawable.fish, R.drawable.flamingo, R.drawable.fox, R.drawable.frog, R.drawable.giraffe,
            R.drawable.goat, R.drawable.gorilla, R.drawable.hippo, R.drawable.horse, R.drawable.hyena, R.drawable.kangaroo, R.drawable.leopard,
            R.drawable.lion, R.drawable.monkey, R.drawable.mouse,
            R.drawable.ostrich, R.drawable.parrot, R.drawable.rabbit, R.drawable.sheep, R.drawable.snake,
            R.drawable.spider, R.drawable.squirrel, R.drawable.turtle,
            R.drawable.wolf, R.drawable.zebra};

    public static final int[] ANIMAL_SOUNDS = {R.raw.popo, R.raw.dubu, R.raw.nyuki, R.raw.nyati, R.raw.kipepeo,
            R.raw.ngamia, R.raw.paka, R.raw.duma, R.raw.kuku, R.raw.sokwe, R.raw.ng_ombe, R.raw.mamba, R.raw.mbwa,
            R.raw.punda, R.raw.njiwa, R.raw.bata, R.raw.tai, R.raw.tembo, R.raw.samaki, R.raw.korongo, R.raw.mbweha,
            R.raw.chupa, R.raw.twiga, R.raw.mbuzi, R.raw.sokwe, R.raw.kiboko, R.raw.farasi, R.raw.fisi, R.raw.kangaruu,
            R.raw.chui, R.raw.simba, R.raw.tumbili, R.raw.panya, R.raw.mbuni, R.raw.kasuku, R.raw.sungura, R.raw.kondoo,
            R.raw.nyoka, R.raw.buibui, R.raw.kuchakuro, R.raw.kobe, R.raw.mbwa_mwitu, R.raw.punda};

    public static final Map<String, Pair<Integer, Integer>> ANIMALS_ENG = new HashMap<String, Pair<Integer, Integer>>() {{
        for(int i = 0; i < ANIMAL_NAMES_ENG.length; i++){
            put(ANIMAL_NAMES_ENG[i], Pair.create(ANIMAL_PATHS[i], ANIMAL_SOUNDS[i]));
        }
    }};

    public static final Map<String, Pair<Integer, Integer>> ANIMALS_SWA = new HashMap<String, Pair<Integer,Integer>>() {{
        for(int i = 0; i < ANIMAL_NAMES_SWA.length; i++){
            put(ANIMAL_NAMES_SWA[i], Pair.create(ANIMAL_PATHS[i], ANIMAL_SOUNDS[i]));
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
