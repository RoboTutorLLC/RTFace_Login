package com.example.iris.login1;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.example.iris.login1.Common.*;
import static com.example.iris.login1.Common.STATE.*;

public class GalleryActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    /* --------------------------------- */
    /* ---------- Audio files ---------- */
    /* --------------------------------- */
    // KIMTAR this is where audio files are. Their names are what is said.
    // MARCH DOTHIS add media files for new states
    //Lack for Swahili audio here, replace them later

    private int[] mediaListRTEng = {R.raw.eng_thisisrobotutor};
    private int[] mediaListRTSwa = {R.raw.swa_thisisrobotutor};
    private int[] mediaListStartSwa =
            {R.raw.swa_ifyouseeyourpicture, R.raw.swa_pleasetaponyourpicture,
                    R.raw.swa_toseemorepictures, R.raw.swa_slidethemlikethis,
                    R.raw.swa_ifyoudontfindyourpicture, R.raw.swa_pleasetaphere2};
    private int[] mediaListStartEng =
            {R.raw.eng_ifyouseeyourpicture, R.raw.eng_pleasetaponyourpicture,
                    R.raw.eng_toseemorepictures, R.raw.eng_slidethemlikethis,
                    R.raw.eng_ifyoudontfindyourpicture, R.raw.eng_pleasetaphere2};

    private int[] mediaListRecordSwa = {R.raw.swa_pleasesayyourname, R.raw.swa_yousaid};
    private int[] mediaListRecordEng = {R.raw.eng_pleasesayyourname, R.raw.eng_yousaid};

    // MARCH [LOGIN_APPROVE_VIDEO] MEDIA LIST
    private int[] mediaListAcceptSwa =
            {R.raw.swa_ifyoulikeyourpictureandhowyousaidyourname, R.raw.swa_pleasetaphere1,
                    R.raw.swa_otherwisetaphere};
    private int[] mediaListAcceptEng =
            {R.raw.eng_ifyoulikeyourpictureandhowyousaidyourname, R.raw.eng_pleasetaphere1,
                    R.raw.eng_otherwisetaphere};

    //Lack for Swahili audio here, replace them later
    private int[] mediaListAfterAcceptingSwa =
            {R.raw.swa_good, R.raw.swa_now, R.raw.swa_okletstryagain};
    private int[] mediaListAfterAcceptingEng =
            {R.raw.eng_good, R.raw.eng_now, R.raw.eng_okletstryagain};

    private int[] mediaListDecideSwa =
            {R.raw.swa_ifthisisyou, R.raw.swa_pleasetaphere2, R.raw.swa_ifthisisnotyou,
                    R.raw.swa_pleasetaphere2};
    private int[] mediaListDecideEng =
            {R.raw.eng_ifthisisyou, R.raw.eng_pleasetaphere2, R.raw.eng_ifthisisnotyou,
                    R.raw.eng_pleasetaphere2};

    private int[] mediaListAfterDecidingSwa = {R.raw.swa_letsgetstarted, R.raw.swa_letstryagain};
    private int[] mediaListAfterDecidingEng = {R.raw.eng_letsgetstarted, R.raw.eng_letstryagain};

    // MARCH [CHECK_IF_EXPECTED] MEDIA LIST
    // MARCH PROMPTS ARE SAME AS DECIDE
    private int[] mediaListExpectedSwa =
            {R.raw.swa_ifthisisyou, R.raw.swa_pleasetaphere2, R.raw.swa_ifthisisnotyou,
                    R.raw.swa_pleasetaphere2};
    private int[] mediaListExpectedEng =
            {R.raw.eng_ifthisisyou, R.raw.eng_pleasetaphere2, R.raw.eng_ifthisisnotyou,
                    R.raw.eng_pleasetaphere2};

    // MARCH [OLD_OR_NEW] MEDIA LIST
    private int[] mediaListOldNewSwa =
            {R.raw.swa_ifyouveusedrobotutorbefore, R.raw.swa_pleasetaphere4,
                    R.raw.swa_ifyouveneverusedrobotutorbefore, R.raw.swa_pleasetaphere5};
    private int[] mediaListOldNewEng =
            {R.raw.eng_ifyouveusedrobotutorbefore, R.raw.eng_pleasetaphere4,
                    R.raw.eng_ifyouveneverusedrobotutorbefore, R.raw.eng_pleasetaphere5};

    // MARCH [ENROLL_GENDER] MEDIA LIST
    // MARCH HOW TO SET UP GENDER VIDEOS

    //TODO add english gender media list
    private int[] mediaListGenderEng = {R.raw.eng_if_youre_a_boy, R.raw.eng_boy_please_tap_here, R.raw.eng_if_youre_a_girl, R.raw.eng_girl_please_tap_here};

    private int[] mediaListGenderSwa = {R.raw.swa_if_youre_a_boy, R.raw.swa_boy_please_tap_here, R.raw.swa_if_youre_a_girl, R.raw.swa_girl_please_tap_here};

    // MARCH R.raw.swa_ifyouwanttoseeadifferentpciture is the same for [ENROLL_ICON] and [LOGIN_ICON]

    // MARCH [ENROLL_ICON] MEDIA LIST
    private int[] mediaListIconSwa =
            {R.raw.swa_ifyoulikethispicture, R.raw.swa_pleasetaphere6,
                    R.raw.swa_ifyouwanttoseeadifferentpicture, R.raw.swa_pleasetaphere7};
    private int[] mediaListIconEng =
            {R.raw.eng_ifyoulikethispicture, R.raw.eng_pleasetaphere6,
                    R.raw.eng_ifyouwanttoseeadifferentpicture, R.raw.eng_pleasetaphere7};

    // MARCH [LOGIN_ICON] MEDIA LIST
    private int[] mediaListLoginIconSwa =
            {R.raw.swa_pleasetapheretogoon,
                    R.raw.swa_ifyouwanttoseeadifferentpicture, R.raw.swa_pleasetaphere7};
    private int[] mediaListLoginIconEng =
            {R.raw.eng_pleasetapheretogoon,
                    R.raw.eng_ifyouwanttoseeadifferentpicture, R.raw.eng_pleasetaphere7};

    private int[] mediaListRecordRecSwa =
            {R.raw.swa_pleasetapheretogoon,
                    R.raw.swa_ifyouwanttoseeadifferentpicture, R.raw.swa_pleasetaphere7};
    private int[] mediaListRecordRecEng =
            {R.raw.eng_pleasetapheretogoon,
                    R.raw.eng_ifyouwanttoseeadifferentpicture, R.raw.eng_pleasetaphere7};

    /* ---------------------------------------- */
    /* ---------- Audio file holders ---------- */
    /* ---------------------------------------- */
    private int[] playListStart;
    private int[] playListRecord;
    private int[] playListAccept;
    private int[] playListAfterAccepting;
    private int[] playListDecide;
    private int[] playListAfterDeciding;
    // MARCH PLAYLIST FOR NEW MEDIA LIST
    private int[] playListExpected;
    private int[] playListOldNew;
    private int[] playListGender;
    private int[] playListIcon;
    private int[] playListLoginIcon;
    private int[] playListRT;
    private int[] playListRecordRec;

    private MediaPlayer mpAll;
    private MediaPlayer mpAnimal;
    private MediaPlayer.OnCompletionListener onCompletionListener;

    private View masterLayout;

    /* -- Splash screen views -- */
    private SurfaceView surfaceviewFullScreen;
    private ImageButton logo;
    private ImageView logoShadow;
    private int logoToShadow3dOffset = 10;
    private View splash;
    private View centerAnchor;
    private ImageView splashRoboFinger;

    private MyScrollView mGalleryScrollView;
    private ScrollViewAdapter mAdapter;
    private SurfaceView surfaceview;
    private SurfaceHolder surfaceHolder;
    private ImageView coverSurface;
    private RecordThread thread;

    //list of user's icon and profile pic
    private List<Pair<Bitmap, Integer>> mDatas =  new ArrayList<Pair<Bitmap, Integer>>();

    private DataHelper dbHelper;
    private List<UserInfo> userInfo = new ArrayList<UserInfo>();
    private ImageView capture;
    private ImageView like;
    private ImageView dislike;

    private ImageView genderboy;
    private ImageView gendergirl;
    private LinearLayout genderlay;
    private LinearLayout activity_gal;
    private LinearLayout iconlay;
    private LinearLayout oldnewlay;
    private RelativeLayout gallery_pics;
    private ImageView iconlike;
    private ImageView icondislike;
    private ImageView iconpic;
    private ImageView oldnewlike;
    private ImageView oldnewdislike;
    private TextView icontext;
    /* RoboFinger */
    private ImageView slideRoboFinger;
    private int accountsNumber;
    private PlayVideoThread videoThread;
    private boolean readyToStartTimer = false;
    private boolean isPreparing = false;
    private boolean needConfirm = false;
    private boolean firstAttempt = true;
    private boolean newReg = false;
    private boolean recordAgain = false;
    private boolean genderRegd = false;
    private boolean iconRegd = false;
    private boolean iconRepeat = false;
    private boolean iconpick2 = false;
    private boolean newUser2 = false;
    private boolean newUserOldNew = false;
    private boolean isGalleryPick = false;
    private boolean recordRec = false;
    private boolean pauseWhileRecord = false;
    private Integer RT_STARTED = 12345;
    // MARCH boolean check for first registration
    private static boolean firstRegistration = false;

    // MARCH counter variable
    private int counter = 0;

    private long capture_lastFlashTime = System.nanoTime();
    private long capture_startFlashTIme = System.nanoTime();
    private long like_lastFlashTime = System.nanoTime();
    private long like_startFlashTIme = System.nanoTime();
    private long dislike_lastFlashTime = System.nanoTime();
    private long dislike_startFlashTIme = System.nanoTime();

    private long boy_lastFlashTime = System.nanoTime();
    private long boy_startFlashTIme = System.nanoTime();
    private long girl_lastFlashTime = System.nanoTime();
    private long girl_startFlashTIme = System.nanoTime();

    private boolean capture_nowClicked = false;
    private boolean like_nowClicked = false;
    private boolean dislike_nowClicked = false;
    private boolean boy_nowClicked = false;
    private boolean girl_nowClicked = false;

    private boolean capture_stopFlash = false;
    private boolean like_stopFlash = false;
    private boolean dislike_stopFlash = false;
    private boolean boy_stopFlash = false;
    private boolean girl_stopFlash = false;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private UserInfo currentUser;
    private UserInfo curUser;

    private STATE _audioPlaying;
    private String language = BuildConfig.LANGUAGE_FEATURE_ID;


    private MediaPlayer playMediaInAccept(MediaPlayer mp, int file) {
        mp = MediaPlayer.create(this, playListAccept[file]);
        return mp;
    }

    // MARCH get firstRegistration variable
    public static boolean getFirstRegistration() {
        return firstRegistration;
    }

    /**
     * KIMTAR this is the method that is called anytime an audio file must be played
     *
     * @param audioFile
     */
    private void releaseAndPlayAudioFile(int audioFile) {
        Log.e("audionow", getResources().getResourceEntryName(audioFile));
        mpAll.release();
        mpAll = MediaPlayer.create(this, audioFile);
        mpAll.setOnCompletionListener(onCompletionListener);
        mpAll.seekTo(0);
        mpAll.start();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // KIMTAR this is the first method called when FaceLogin starts

        super.onCreate(savedInstanceState);

        initVarsOfViews();
        initVarsOfMediaPlayer();
        setScrollViewListeners();
        initUserInfo();

        if(userInfo.size() != 0) {
            mAdapter = new ScrollViewAdapter(this, mDatas);
            mGalleryScrollView.initDatas(mAdapter, needConfirm);
            mGalleryScrollView.clearAllBackground();
        }

        // KIMTAR, _audioPlaying this is the state that specifies which part of "onCompletionListener" is played
        // MARCH START WITH THIS_IS_ROBOTUTOR
        _audioPlaying = THIS_IS_ROBOTUTOR;

        SurfaceHolder holder = this.surfaceview.getHolder();// get holder
        holder.addCallback(this);    //add the callback interface to the holder
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        setLogoOnTouchListener();

        setLikeOnClickListener();
        setDislikeOnClickListener();
        setCaptureOnClickListener();
        setIconDislikeOnClickListener();
        setIconLikeOnClickListener();
        setGenderGirlOnClickListener();
        setGenderBoyOnClickListener();
        setOldNewDislikeOnClickListener();
        setOldNewLikeOnClickListener();

        //capture, like and dislike onclicks moved to toStart

        mainHandler.postDelayed(splashRoboFingerSlideRunnable, DELAY_TO_SHOW_CHANGE_OF_FINGER);
        mainHandler.postDelayed(playVideoOfGoodTappingRunnable, DELAY_TO_SHOW_VIDEO_FULL_SCREEN);

    }

    private void initVarsOfViews() {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_gallery);

        masterLayout = (RelativeLayout) findViewById(R.id.masterLayout);

        /* Find the Splash screen Views */
        surfaceviewFullScreen = (SurfaceView) findViewById(R.id.show_full_screen);
        logo = (ImageButton) findViewById(R.id.logo);
        logoShadow = (ImageView) findViewById(R.id.logoShadow);
        splash = (View) findViewById(R.id.splash);
        centerAnchor = (View) findViewById(R.id.centerAnchor);
        splashRoboFinger = (ImageView) findViewById(R.id.splash_finger);


        // in order to set View locations programmatically, we must wait for RelativeLayout to finish setting up
        masterLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        initializeAndPlaceLogo();

                        // remove listener when done (only works in certain android version???)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            masterLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                }
        );

        surfaceview = (SurfaceView) findViewById(R.id.id_content);
        coverSurface = (ImageView) findViewById(R.id.cover_surface);
        capture = (ImageView) findViewById(R.id.capture);
        like = (ImageView) findViewById(R.id.like);
        dislike = (ImageView) findViewById(R.id.dislike);
        slideRoboFinger = (ImageView)findViewById(R.id.slide_finger);

        mGalleryScrollView = (MyScrollView) findViewById(R.id.id_scrollView);

        genderboy = (ImageView) findViewById(R.id.genderboy);
        gendergirl = (ImageView) findViewById(R.id.gendergirl);
        genderlay = (LinearLayout) findViewById(R.id.genderlay);
        activity_gal = (LinearLayout) findViewById(R.id.activity_gallery);
        iconlay = (LinearLayout) findViewById(R.id.iconlay);
        iconpic = (ImageView) findViewById(R.id.iconpic);
        icondislike = (ImageView) findViewById(R.id.icondislike);
        iconlike = (ImageView) findViewById(R.id.iconlike);
        icontext = (TextView) findViewById(R.id.icontext);
        oldnewlay = (LinearLayout) findViewById(R.id.oldnewlay);
        oldnewlike = (ImageView) findViewById(R.id.oldnewlike);
        oldnewdislike = (ImageView) findViewById(R.id.oldnewdislike);
        gallery_pics = (RelativeLayout) findViewById(R.id.gallery_pics);
    };

    /**
     * XXX places the logo and the logo shadow
     */
    private void initializeAndPlaceLogo() {

        float centerX = centerAnchor.getX();
        float centerY = centerAnchor.getY();
        Log.d("LOCATIONS", "x: " + centerX + ", " + centerY);
        logo.setX(centerX - logo.getWidth() / 2 - logoToShadow3dOffset);
        logo.setY(centerY - logo.getHeight() / 2 - logoToShadow3dOffset);

        logoShadow.setX(centerX - logoShadow.getWidth() / 2 + logoToShadow3dOffset);
        logoShadow.setY(centerY - logoShadow.getHeight() / 2 + logoToShadow3dOffset);
    }

    private void initVarsOfMediaPlayer() {
        //choose audio according to language version
        switch(language) {
            case LANG_EN:
                mpAll = MediaPlayer.create(this, R.raw.eng_thisisrobotutor);
                playListRT = mediaListRTEng;
                playListStart = mediaListStartEng;
                playListRecord = mediaListRecordEng;
                playListAccept = mediaListAcceptEng;
                playListAfterAccepting = mediaListAfterAcceptingEng;
                playListDecide = mediaListDecideEng;
                playListAfterDeciding = mediaListAfterDecidingEng;
                // MARCH SET PLAYLIST FOR NEW MEDIA LIST IN EN
                playListExpected = mediaListExpectedEng;
                playListOldNew = mediaListOldNewEng;
                playListGender = mediaListGenderEng;
                playListIcon = mediaListIconEng;
                playListLoginIcon = mediaListLoginIconEng;
                playListRecordRec = mediaListRecordRecEng;
                break;

            case LANG_SW:
                mpAll = MediaPlayer.create(this, R.raw.swa_thisisrobotutor);
                playListRT = mediaListRTSwa;
                playListStart = mediaListStartSwa;
                playListRecord = mediaListRecordSwa;
                playListAccept = mediaListAcceptSwa;
                playListAfterAccepting = mediaListAfterAcceptingSwa;
                playListDecide = mediaListDecideSwa;
                playListAfterDeciding = mediaListAfterDecidingSwa;
                // MARCH SET PLAYLIST FOR NEW MEDIA LIST IN SWA
                playListExpected = mediaListExpectedSwa;
                playListOldNew = mediaListOldNewSwa;
                playListGender = mediaListGenderSwa;
                playListIcon = mediaListIconSwa;
                playListLoginIcon = mediaListLoginIconSwa;
                playListRecordRec = mediaListRecordRecSwa;
                break;
        }

        setMeidaPlayerListeners();
    }

    private void setMeidaPlayerListeners() {

        onCompletionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                // MARCH DOTHIS check which of three cases (empty, previous user, regular user)

                switch (_audioPlaying) {

                    // MARCH [WELCOME]
                    // formerly mpIntro
                    case THIS_IS_ROBOTUTOR:
                        // MARCH go to toSTART
                        newReg = false;
                        recordAgain = false;
                        genderRegd = false;
                        iconRepeat = false;
                        iconRegd = false;
                        iconpick2 = false;
                        newUser2 = false;
                        toSTART();
                        break;

                    // MARCH [LOGIN_GALLERY 1]
                    // formerly mpStart1
                    case IF_YOU_SEE_YOUR_PICTURE:
                        isGalleryPick = false;
                        if (dislike.getVisibility() == View.VISIBLE && activity_gal.getVisibility() == View.VISIBLE) {
                            _audioPlaying = PLEASE_TAP_ON_YOUR_PICTURE;
                            releaseAndPlayAudioFile(playListStart[1]);
                        }
                        break;

                    // MARCH [LOGIN_GALLERY 1]
                    // formerly mpStart2
                    case PLEASE_TAP_ON_YOUR_PICTURE:

                        // formerly mpStart2
                        if (dislike.getVisibility() == View.VISIBLE && activity_gal.getVisibility() == View.VISIBLE) {
                            if (mGalleryScrollView.exceedScreen(userInfo.size())) {
                                _audioPlaying = TO_SEE_MORE_PICTURES;
                                releaseAndPlayAudioFile(playListStart[2]);
                            } else {
                                _audioPlaying = IF_YOU_DONT_FIND_YOUR_PICTURE;
                                releaseAndPlayAudioFile(playListStart[4]);
                            }
                        }
                        // MARCH DOTHIS go to LOGIN_VERIFY_IDENTITY 1
                        break;

                    // MARCH [LOGIN_GALLERY 2]
                    // formerly mpStart3
                    // XXX this is where RoboFinger is called!
                    case TO_SEE_MORE_PICTURES:
                        if (activity_gal.getVisibility() == View.VISIBLE) {
                            _audioPlaying = SLIDE_THEM_LIKE_THIS;
                            releaseAndPlayAudioFile(playListStart[3]);
                            slideRoboFinger.setVisibility(View.VISIBLE);
                            mainHandler.postDelayed(slideGalleryRunnable, DELAY_TO_SHOW_CHANGE_OF_FINGER);
                        }
                        break;

                    // MARCH [LOGIN_GALLERY 2]
                    // formerly mpStart4
                    case SLIDE_THEM_LIKE_THIS:
                        _audioPlaying = IF_YOU_DONT_FIND_YOUR_PICTURE;
                        releaseAndPlayAudioFile(playListStart[4]);
                        break;

                    // MARCH [ENROLL_RECORD]
                    case PLEASE_SAY_YOUR_NAME:
                        //prompt "please say your name", then set timer to stop recording
                        Log.w("TIMING", "PLEASE SAY YOUR NAME " + System.currentTimeMillis());
                        thread.setTimerToStopRecording();
                        Log.w("TIMING", "PLEASE SAY YOUR NAME " + System.currentTimeMillis());
                        // MARCH DOTHIS record video; play back video; go to LOGIN_GALLERY 1
                        break;

                    // MARCH DOTHIS relocate if needed
                    // MARCH [LOGIN_VERIFY_IDENTITY]
                    case YOU_SAID:
                        //prompt "you said", then replay the newly taken video

                        long silenceInMs = 0;
                        switch(language) {
                            case LANG_EN:
                                silenceInMs = TRAILING_SILENCE_EN;
                                break;
                            case LANG_SW:
                                silenceInMs = TRAILING_SILENCE_SW;
                                break;
                        }
                        Log.i("TIMING", "setting record at " + silenceInMs);
                        thread.newReplay(thread.vPath, silenceInMs);
                        // MARCH DOTHIS IF YES AND NEW USER go to LOGIN_APPROVE_VIDEO 1
                        // MARCH DOTHIS IF YES AND NOT NEW USER go to LOGIN_APPROVE_VIDEO 2
                        // MARCH DOTHIS IS NO go to LOGIN_GALLERY 1
                        break;

                    // MARCH [LOGIN_GALLERY 3]
                    // formerly mpStart5
                    case IF_YOU_DONT_FIND_YOUR_PICTURE:
                        if (dislike.getVisibility() == View.VISIBLE && activity_gal.getVisibility() == View.VISIBLE) {
                            _audioPlaying = TAP_HERE_CANT_FIND;
                            releaseAndPlayAudioFile(playListStart[5]);
                            startFlash(FLASH_DISLIKE);
                        }
                        // MARCH DOTHIS go to OLD_OR_NEW
                        break;

                    case TAP_HERE_CANT_FIND:
                        if (dislike.getVisibility() == View.VISIBLE && activity_gal.getVisibility() == View.VISIBLE) {
                            mHandler.postDelayed(toDECIDEFindPic, DELAY_TO_REPROMPT);
                        }

                        break;
                    // MARCH DOTHIS IMPLEMENT RECORD FOR FUN FUNCTIONALITY

                    // MARCH how does this record?
                    // formerly mpStart6

                    case TAP_HERE_RECORD:
                        mHandler.postDelayed(toDECIDERecord, DELAY_TO_REPROMPT);
                        // do nothing
                        break;

                    // MARCH [LOGIN_APPROVE_VIDEO 1A]
                    // MARCH NEWSTATE [LOGIN_APPROVE_VIDEO 2]
                    case IF_YOU_LIKE_YOUR_PICTURE_AND_HOW_YOU_SAID_YOUR_NAME:
                        if (needConfirm) {
                            _audioPlaying = TAP_HERE_LIKE_PICTURE;
                            releaseAndPlayAudioFile(playListAccept[1]);
                            startFlash(FLASH_LIKE);
                        }
                        break;

                    // MARCH [LOGIN_APPROVE_VIDEO 1A]
                    case TAP_HERE_LIKE_PICTURE:
                        if (needConfirm) {
                            _audioPlaying = TAP_HERE_DISLIKE_PICTURE;
                            releaseAndPlayAudioFile(playListAccept[2]);
                            startFlash(FLASH_DISLIKE);
                        }
                        // MARCH [LOGIN_APPROVE_VIDEO 1A] DOTHIS go to LOGIN_ICON 1

                        // MARCH [LOGIN_APPROVE_VIDEO 2] DOTHIS IF TAP RECORD "PLEASE SAY YOUR NAME"; record video;
                        // MARCH [LOGIN_APPROVE_VIDEO 2] DOTHIS play back video; go to LOGIN_APPROVE_VIDEO 2
                        break;

                    // MARCH [LOGIN_APPROVE_VIDEO 1B]
                    case TAP_HERE_DISLIKE_PICTURE:
                        if (needConfirm) {
                            //counter += 1;
                            mHandler.postDelayed(toACCEPT, DELAY_TO_REPROMPT);
                        }
                        // MARCH [LOGIN_APPROVE_VIDEO 1B] DOTHIS "PLEASE SAY YOUR NAME"; record video; play back video;
                        // MARCH [LOGIN_APPROVE_VIDEO 1B] DOTHIS go to LOGIN_APPROVE_VIDEO 1

                        // MARCH [LOGIN_APPROVE_VIDEO 2] DOTHIS IF TAP GO ON go to LOGIN_ICON 1
                        break;

                    case GOOD:
                        _audioPlaying = NOW;
                        releaseAndPlayAudioFile(playListAfterAccepting[1]);
                        break;

                    case NOW:
                        if(newReg){
                            toSTART();
                            break;
                        }

                        if (genderRegd) {
                            if (!iconRegd) {
                                String icntext = icontext.getText().toString();
                                releaseAndPlayAudioFileAnimal(language.equals(LANG_EN) ?
                                                Common.ANIMALS_ENG.get(icntext.toLowerCase()).second : Common.ANIMALS_SWA.get(icntext.toLowerCase()).second,
                                        new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mp) {
                                                iconpic.setVisibility(View.VISIBLE);
                                                _audioPlaying = IF_YOU_LIKE_THIS_PICTURE;
                                                releaseAndPlayAudioFile(playListIcon[0]);
                                            }
                                        });
                            } else {
                                mGalleryScrollView.clearAllBackground();
                                setCaptureOnClickListener();
                                setLikeOnClickListener();
                                setDislikeOnClickListener();

                                //TODO multiple occurrences of this due to clashing audio: simplify it
                                //mHandler.removeCallbacksAndMessages(null);
                                mHandler.post(toDECIDERecord);
                            }
                        } else {
                            toSTART();
                        }
                        break;

                    case OKAY_LETS_TRY_AGAIN:
                        if(iconRepeat) {
                            String icntext = icontext.getText().toString();
                            if(iconpick2){
                                releaseAndPlayAudioFileAnimal(language.equals(LANG_EN) ?
                                                Common.ANIMALS_ENG.get(icntext.toLowerCase()).second : Common.ANIMALS_SWA.get(icntext.toLowerCase()).second,
                                        new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mp) {
                                                iconpic.setVisibility(View.VISIBLE);
                                                _audioPlaying = PLEASE_TAP_HERE_TO_GO_ON;
                                                releaseAndPlayAudioFile(playListLoginIcon[0]);
                                                stopFlash(FLASH_LIKE);
                                                startFlash(FLASH_LIKE);
                                            }
                                        });
                            } else {
                                releaseAndPlayAudioFileAnimal(language.equals(LANG_EN) ?
                                                Common.ANIMALS_ENG.get(icntext.toLowerCase()).second : Common.ANIMALS_SWA.get(icntext.toLowerCase()).second,
                                        new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mp) {
                                                iconpic.setVisibility(View.VISIBLE);
                                                _audioPlaying = IF_YOU_LIKE_THIS_PICTURE;
                                                releaseAndPlayAudioFile(playListIcon[0]);
                                            }
                                        });
                            }
                        } else {
                            toSTART();
                        }
                        break;

                    // MARCH [CHECK_IF_EXPECTED 1]
                    case IF_THIS_IS_YOU:
                        if (like.getVisibility() == View.VISIBLE && dislike.getVisibility() == View.VISIBLE && !needConfirm) {
                            _audioPlaying = TAP_HERE_IF_IS_YOU;
                            releaseAndPlayAudioFile(playListDecide[1]);
                            startFlash(FLASH_LIKE);
                        }
                        break;

                    // MARCH [CHECK_IF_EXPECTED 1]
                    case TAP_HERE_IF_IS_YOU:
                        if (like.getVisibility() == View.VISIBLE && dislike.getVisibility() == View.VISIBLE && !needConfirm) {
                            _audioPlaying = IF_THIS_IS_NOT_YOU;
                            releaseAndPlayAudioFile(playListDecide[2]);
                        }
                        break;

                    // MARCH [CHECK_IF_EXPECTED 2]
                    case IF_THIS_IS_NOT_YOU:
                        if (like.getVisibility() == View.VISIBLE && dislike.getVisibility() == View.VISIBLE && !needConfirm) {
                            _audioPlaying = TAP_HERE_IF_IS_NOT_YOU;
                            releaseAndPlayAudioFile(playListDecide[3]);
                            startFlash(FLASH_DISLIKE);
                        }
                        break;

                    // MARCH [CHECK_IF_EXPECTED 2]
                    case TAP_HERE_IF_IS_NOT_YOU:
                        if (like.getVisibility() == View.VISIBLE && dislike.getVisibility() == View.VISIBLE && !needConfirm) {
                            //counter += 1;
                            mHandler.postDelayed(toDECIDE, DELAY_TO_REPROMPT);
                            // MARCH DOTHIS IF_IS_YOU go to LOGIN_APPROVE_VIDEO 2
                            // MARCH DOTHIS IF_IS_NOT_YOU go to OLD_OR_NEW 1
                        }
                        break;

                    // MARCH [LOGIN_FINISH]
                    case LETS_GET_STARTED:
                        // reset firstAttempt
                        firstAttempt = true;
                        String newSessId = generateSessionID();
                        currentUser.setLastLoginTime(newSessId.split("_", 2)[1]);
                        dbHelper.updateUserTime(currentUser);

                        // when the Confirm button is tapped, launch RoboTutor
                        // TODO pass the unique student id
                        // TODO test more fervently
                        removeOldVideos();
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(ROBOTUTOR_PACKAGE_ADDRESS);
                        Bundle sessionBundle = new Bundle();
                        Log.w("BUNDLE", currentUser.getUserIcon());
                        //String uniqueUserID = generateUniqueIdFromFilename(currentUser.getUserIcon());

                        String uniqueUserID = currentUser.getBirthDate() + "_" + String.valueOf(currentUser.getID()) + "_" +
                                currentUser.getBirthDevice();
                        sessionBundle.putString(Common.STUDENT_ID_VAR, uniqueUserID);
                        sessionBundle.putString(Common.SESSION_ID_VAR, newSessId);
                        launchIntent.putExtras(sessionBundle); 
                        launchIntent.setFlags(0);

                        if (launchIntent != null) {
                            startActivityForResult(launchIntent, RT_STARTED);
                        } else {
                            Log.e("ACTIVITY", "New Activity failed to start!");
                        }
                        // MARCH go to WELCOME
                        // System.exit(0);
                        break;


                    // MARCH NEWSTATE [OLD_OR_NEW]
                    case IF_YOUVE_USED_ROBOTUTOR_BEFORE:
                        if (oldnewlike.getVisibility() == View.VISIBLE && oldnewdislike.getVisibility() == View.VISIBLE) {
                            _audioPlaying = TAP_HERE_YES;
                            releaseAndPlayAudioFile(playListOldNew[1]);
                            startFlash(FLASH_LIKE);
                        }
                        break;

                    // MARCH CASE A
                    case TAP_HERE_YES:
                        if (oldnewlike.getVisibility() == View.VISIBLE && oldnewdislike.getVisibility() == View.VISIBLE) {
                            _audioPlaying = IF_YOUVE_NEVER_USED_ROBOTUTOR_BEFORE;
                            releaseAndPlayAudioFile(playListOldNew[2]);
                        }
                        break;

                    case IF_YOUVE_NEVER_USED_ROBOTUTOR_BEFORE:
                        if (oldnewlike.getVisibility() == View.VISIBLE && oldnewdislike.getVisibility() == View.VISIBLE) {
                            _audioPlaying = TAP_HERE_NO;
                            releaseAndPlayAudioFile(playListOldNew[3]);
                            startFlash(FLASH_DISLIKE);
                        }
                        break;

                    // MARCH CASE B
                    case TAP_HERE_NO:
                        if (oldnewlike.getVisibility() == View.VISIBLE && oldnewdislike.getVisibility() == View.VISIBLE) {
                            //counter += 1;
                            mHandler.postDelayed(toDECIDEOldNew, DELAY_TO_REPROMPT);
                            // MARCH DOTHIS IF TAP YES go to LOGIN_GALLERY 1
                            // MARCH DOTHIS IF TAP NO go to INTRO 1
                        }
                        break;


                    // MARCH NEWSTATE [ENROLL_GENDER]

                    case IF_YOURE_A_BOY:
                        if (genderboy.getVisibility() == View.VISIBLE && gendergirl.getVisibility() == View.VISIBLE) {
                            _audioPlaying = TAP_HERE_BOY;
                            releaseAndPlayAudioFile(playListGender[1]);

                            startFlash(FLASH_BOY);
                        }
                        break;

                    case TAP_HERE_BOY:
                        if (genderboy.getVisibility() == View.VISIBLE && gendergirl.getVisibility() == View.VISIBLE) {
                            _audioPlaying = IF_YOURE_A_GIRL;
                            releaseAndPlayAudioFile(playListGender[2]);
                        }
                        break;

                    case IF_YOURE_A_GIRL:
                        if (genderboy.getVisibility() == View.VISIBLE && gendergirl.getVisibility() == View.VISIBLE) {
                            _audioPlaying = TAP_HERE_GIRL;
                            releaseAndPlayAudioFile(playListGender[3]);
                            startFlash(FLASH_GIRL);

                        }
                        break;

                    case TAP_HERE_GIRL:
                        if (genderboy.getVisibility() == View.VISIBLE && gendergirl.getVisibility() == View.VISIBLE) {
                            //counter += 1;
                            mHandler.postDelayed(toDECIDEGender, DELAY_TO_REPROMPT);
                            // MARCH DOTHIS IF TAP EITHER GIRL OR BOY go to ENROLL_ICON 1
                        }
                        break;


                    // MARCH NEWSTATE [ENROLL_ICON]
                    //TODO need !needConfirm?
                    case IF_YOU_LIKE_THIS_PICTURE:
                        if (iconlike.getVisibility() == View.VISIBLE && icondislike.getVisibility() == View.VISIBLE) {
                            setIconLikeOnClickListener();
                            setIconDislikeOnClickListener();
                            _audioPlaying = TAP_HERE_YES_ICON;
                            releaseAndPlayAudioFile(playListIcon[1]);
                            startFlash(FLASH_LIKE);
                        }
                        break;

                    // MARCH CASE A
                    case TAP_HERE_YES_ICON:
                        if (iconlike.getVisibility() == View.VISIBLE && icondislike.getVisibility() == View.VISIBLE) {
                            _audioPlaying = IF_YOU_WANT_TO_SEE_A_DIFFERENT_PICTURE;
                            releaseAndPlayAudioFile(playListIcon[2]);
                        }
                        break;

                    case IF_YOU_WANT_TO_SEE_A_DIFFERENT_PICTURE:
                        if (iconlike.getVisibility() == View.VISIBLE && icondislike.getVisibility() == View.VISIBLE) {
                            _audioPlaying = TAP_HERE_NO_ICON;
                            releaseAndPlayAudioFile(playListIcon[3]);
                            startFlash(FLASH_DISLIKE);
                        }
                        break;

                    // MARCH CASE B
                    case TAP_HERE_NO_ICON:
                        if (iconlike.getVisibility() == View.VISIBLE && icondislike.getVisibility() == View.VISIBLE) {
                            //counter += 1;
                            mHandler.postDelayed(toDECIDEIcon, DELAY_TO_REPROMPT);
                            // MARCH DOTHIS IF TAP YES go to ENROLL_RECORD 1
                            // MARCH DOTHIS IF TAP NO go to ENROLL_ICON 1
                        }
                        break;


                    // MARCH NEWSTATE [LOGIN_ICON]
                    case PLEASE_TAP_HERE_TO_GO_ON:
                        if(recordRec){
                            if (like.getVisibility() == View.VISIBLE && capture.getVisibility() == View.VISIBLE){
                                _audioPlaying = TO_TAKE_ANOTHER_PICTURE;

                                //TODO change this
                                releaseAndPlayAudioFile(playListRecordRec[1]);
                            }
                        } else {
                            if (iconlike.getVisibility() == View.VISIBLE && icondislike.getVisibility() == View.VISIBLE) {
                                _audioPlaying = IF_YOU_WANT_TO_SEE_A_DIFFERENT_PICTURE_LOGIN;
                                releaseAndPlayAudioFile(playListLoginIcon[1]);
                            }
                        }
                        break;

                    case IF_YOU_WANT_TO_SEE_A_DIFFERENT_PICTURE_LOGIN:
                        if (iconlike.getVisibility() == View.VISIBLE && icondislike.getVisibility() == View.VISIBLE) {
                            _audioPlaying = TAP_HERE_NO_LOGIN_ICON;
                            releaseAndPlayAudioFile(playListLoginIcon[2]);
                            startFlash(FLASH_DISLIKE);
                        }
                        break;

                    // MARCH CASE B
                    case TAP_HERE_NO_LOGIN_ICON:
                        //counter += 1;
                        mHandler.postDelayed(toDECIDEIcon, DELAY_TO_REPROMPT);
                        // MARCH DOTHIS IF TAP YES go to LOGIN_FINISH
                        // MARCH DOTHIS IF TAP NO randomly pick any icon, go to LOGIN_ICON 1
                        break;

                    case TO_TAKE_ANOTHER_PICTURE:
                        _audioPlaying = TAP_HERE_RECORD_REC;
                        releaseAndPlayAudioFile(playListRecordRec[2]);
                        startFlash(FLASH_CAPTURE);
                        break;

                    case TAP_HERE_RECORD_REC:
                        mHandler.postDelayed(toDECIDERecordRec, DELAY_TO_REPROMPT);
                        break;

                    // MARCH DOTHIS account for three cases (empty, previous user, regular user)
                    case LETS_TRY_AGAIN:
                        toSTART();
                        break;

                }

            }
        };
        mpAll.setOnCompletionListener(onCompletionListener);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RT_STARTED){
            Log.e("onActivityResult", "called");
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

    public void removeOldVideos(){
        List<UserInfo> userslist = dbHelper.getUserList();
        List<String> userIcons = new ArrayList<>();
        List<String> userVideos = new ArrayList<>();

        for (UserInfo user: userslist){
            userIcons.add(user.getUserIcon().split(FACE_LOGIN_PATH + "/")[1]);
            userVideos.add(user.getUserVideo().split(FACE_LOGIN_PATH + "/")[1]);
        }

        File folder = new File(FACE_LOGIN_PATH);

        File[] fileList = folder.listFiles();

        for (File f: fileList){
            if (!userIcons.contains(f.getName()) && !userVideos.contains(f.getName())){
                Log.e("removeVids", f.getName() + " - " + f.delete());
            }
        }
    }

    /**
     * Generates a unique SessionID for RoboTutor
     *
     * @return
     */
    private String generateSessionID() {
        String deviceId = Build.SERIAL;
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return deviceId + "_" + timestamp;
    }

    /**
     * Generate a unique ID for a student based on their audio filename
     *
     * @param filename
     */
    private String generateUniqueIdFromFilename(String filename) {
        String uniqueId;
        String prefix = Common.FACE_LOGIN_PATH + "/" + Common.IMAGE_FILE_PREFIX;
        String suffix = Common.IMAGE_FILE_SUFFIX;

        if(filename.startsWith(prefix)) {
            uniqueId = filename.substring(prefix.length());
            uniqueId = uniqueId.substring(0, uniqueId.length() - suffix.length());
        } else {
            uniqueId = "DEFAULT";
        }

        return uniqueId;
    }


    private void toSTART() {
        //if (capture.getVisibility() == View.VISIBLE) {
            // MARCH if this is you tap here
            // MARCH if this is not you tap here

        if (userInfo.size() > 0 && !newUserOldNew) {
            setCaptureOnClickListener();
            setLikeOnClickListener();
            setDislikeOnClickListener();

            if (recordAgain || newReg) {

                like.setVisibility(View.GONE);
                dislike.setVisibility(View.VISIBLE);
                capture.setVisibility(View.GONE);
                _audioPlaying = IF_YOU_SEE_YOUR_PICTURE;
                releaseAndPlayAudioFile(playListStart[0]);
            } else {
                newUser2 = true;
                capture.setVisibility(View.GONE);
                mGalleryScrollView.getOnItemClickListener().onClick(mAdapter.getView(0, null, (LinearLayout) mGalleryScrollView.getChildAt(0)), 0);
                    //_audioPlaying = IF_THIS_IS_YOU;
                    //releaseAndPlayAudioFile(playListExpected[0]);

                //mpStart1.seekTo(0);
                //mpStart1.start();
            }
        } else {

            if(recordAgain){
                setCaptureOnClickListener();
                setLikeOnClickListener();
                setDislikeOnClickListener();
                _audioPlaying = TAP_HERE_RECORD;
                releaseAndPlayAudioFile(playListStart[5]);
                startFlash(FLASH_CAPTURE);
            } else {
                //TODO add intro video
                //newUserOldNew = false;
                curUser = new UserInfo();

                setGenderBoyOnClickListener();
                setGenderGirlOnClickListener();

                _audioPlaying = IF_YOURE_A_BOY;
                releaseAndPlayAudioFile(playListGender[0]);
            }
            //startFlash(FLASH_CAPTURE);
        }
            // OLD
            /*
            if (userInfo.size() > 0) {
                //If you see your picture, please tap on it, otherwise tap here.
                _audioPlaying = IF_YOU_SEE_YOUR_PICTURE;
                releaseAndPlayAudioFile(playListStart[0]);
                //mpStart1.seekTo(0);
                //mpStart1.start();
            } else {
                //Please tap here
                _audioPlaying = TAP_HERE_RECORD;
                releaseAndPlayAudioFile(playListStart[5]);
                startFlash(FLASH_CAPTURE);
            }
            */
        //}
    }

    private MotionEvent getDefaultResponseMotionEvent(){
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 100;
        float x = 0.0f;
        float y = 0.0f;

        int metaState = 0;
        return MotionEvent.obtain(
                downTime,
                eventTime,
                MotionEvent.ACTION_UP,
                x,
                y,
                metaState
        );
    }
    private Runnable toDECIDEGender = new Runnable() {
        @Override
        public void run() {
            if(mpAll.isPlaying()){
                mHandler.removeCallbacks(this);
                return;
            }
            if (!checkCount()) {
                if (genderboy.getVisibility() == View.VISIBLE && gendergirl.getVisibility() == View.VISIBLE) {
                    counter += 1;
                    _audioPlaying = IF_YOURE_A_BOY;
                    releaseAndPlayAudioFile(playListGender[0]);
                }
            } else {
                mHandler.removeCallbacks(this);
                curUser.setGender("unspecified");
                Log.e("Gender set", curUser.getGender());
                stopFlash(FLASH_GIRL);
                stopFlash(FLASH_BOY);
                genderlay.setVisibility(View.GONE);

                //TODO pick less used
                String[] ANIMAL_NAMES = (language.equals(LANG_EN) ? ANIMAL_NAMES_ENG : ANIMAL_NAMES_SWA);

                String icntext = ANIMAL_NAMES[new Random().nextInt(ANIMAL_NAMES.length)];
                icontext.setText(icntext.toUpperCase());
                iconpic.setImageDrawable(getResources().getDrawable((language.equals(LANG_EN) ?
                        ANIMALS_ENG.get(icntext).first : ANIMALS_SWA.get(icntext).first)));
                iconlay.setVisibility(View.VISIBLE);

                genderRegd = true;
                _audioPlaying = GOOD;
                pauseAllAudios();
                //mHandler.removeCallbacksAndMessages(null);
                releaseAndPlayAudioFile(playListAfterAccepting[0]);
            }
        }
    };

    private Runnable toDECIDEIcon = new Runnable() {
        @Override
        public void run() {
            if(mpAll.isPlaying()){
                mHandler.removeCallbacks(this);
                return;
            }
            if (!checkCount()) {
                if (iconlike.getVisibility() == View.VISIBLE && icondislike.getVisibility() == View.VISIBLE) {
                    counter += 1;
                    if (!iconpick2) {
                        _audioPlaying = IF_YOU_LIKE_THIS_PICTURE;
                        releaseAndPlayAudioFile(playListIcon[0]);
                    } else {
                        _audioPlaying = PLEASE_TAP_HERE_TO_GO_ON;
                        releaseAndPlayAudioFile(playListLoginIcon[0]);
                        stopFlash(FLASH_LIKE);
                        startFlash(FLASH_LIKE);
                    }
                }
            } else {
                mHandler.removeCallbacks(this);

                iconlike.dispatchTouchEvent(getDefaultResponseMotionEvent());
            }
        }
    };

    private Runnable toDECIDERecord = new Runnable() {
        @Override
        public void run() {
            if(mpAll.isPlaying()){
                mHandler.removeCallbacks(this);
                return;
            }

            if (!checkCount()) {
                if (!checkCount() && capture.getVisibility() == View.VISIBLE) {
                    counter += 1;
                    _audioPlaying = TAP_HERE_RECORD;
                    releaseAndPlayAudioFile(playListStart[5]);
                    startFlash(FLASH_CAPTURE);
                }
            } else {
                mHandler.removeCallbacks(this);
                capture.dispatchTouchEvent(getDefaultResponseMotionEvent());
            }
        }
    };


    private Runnable toDECIDERecordRec = new Runnable() {
        @Override
        public void run() {
            if(mpAll.isPlaying()){
                mHandler.removeCallbacks(this);
                return;
            }

            if (!checkCount()) {
                if (!checkCount() && like.getVisibility() == View.VISIBLE && capture.getVisibility() == View.VISIBLE) {
                    counter += 1;
                    _audioPlaying = PLEASE_TAP_HERE_TO_GO_ON;
                    releaseAndPlayAudioFile(playListRecordRec[0]);
                    stopFlash(FLASH_LIKE);
                    startFlash(FLASH_LIKE);
                }
            } else {
                mHandler.removeCallbacks(this);
                like.dispatchTouchEvent(getDefaultResponseMotionEvent());
            }
        }
    };

    private Runnable toDECIDEFindPic = new Runnable(){

        @Override
        public void run() {
            if(mpAll.isPlaying()){
                mHandler.removeCallbacks(this);
                return;
            }
            if(!checkCount()) {
                if (dislike.getVisibility() == View.VISIBLE && activity_gal.getVisibility() == View.VISIBLE) {
                    counter += 1;
                    _audioPlaying = IF_YOU_SEE_YOUR_PICTURE;
                    releaseAndPlayAudioFile(playListStart[0]);
                }
            } else {
                mHandler.removeCallbacks(this);
                if(newReg){
                    dislike.setVisibility(View.GONE);
                    mGalleryScrollView.getOnItemClickListener().onClick(mAdapter.getView(0, null, (LinearLayout) mGalleryScrollView.getChildAt(0)), 0);
                } else {
                    dislike.dispatchTouchEvent(getDefaultResponseMotionEvent());
                }
            }
        }
    };


    private Runnable toDECIDE = new Runnable() {
        @Override
        public void run() {
            if(mpAll.isPlaying()){
                mHandler.removeCallbacks(this);
                return;
            }
            if (!checkCount()) {
                if (like.getVisibility() == View.VISIBLE && dislike.getVisibility() == View.VISIBLE && !needConfirm) {
                    counter += 1;
                    _audioPlaying = IF_THIS_IS_YOU;
                    releaseAndPlayAudioFile(playListDecide[0]);
                }
            } else {
                mHandler.removeCallbacks(this);
                if(newUser2){
                    dislike.dispatchTouchEvent(getDefaultResponseMotionEvent());
                } else {
                    like.dispatchTouchEvent(getDefaultResponseMotionEvent());
                }
            }
        }
    };

    private Runnable toDECIDEOldNew = new Runnable() {
        @Override
        public void run() {
            if(mpAll.isPlaying()){
                mHandler.removeCallbacks(this);
                return;
            }
            if (!checkCount()) {
                if (oldnewlike.getVisibility() == View.VISIBLE && oldnewdislike.getVisibility() == View.VISIBLE) {
                    counter += 1;
                    _audioPlaying = IF_YOUVE_USED_ROBOTUTOR_BEFORE;
                    releaseAndPlayAudioFile(playListOldNew[0]);
                }
            } else {
                mHandler.removeCallbacks(this);
                if(!newUser2) {
                    oldnewdislike.dispatchTouchEvent(getDefaultResponseMotionEvent());
                } else {
                    oldnewlike.dispatchTouchEvent(getDefaultResponseMotionEvent());
                }
            }
        }
    };

    private Runnable toACCEPT = new Runnable() {
        @Override
        public void run() {
            if(mpAll.isPlaying()){
                mHandler.removeCallbacks(this);
                return;
            }
            if (!checkCount()) {
                if (needConfirm) {
                    counter += 1;
                    _audioPlaying = IF_YOU_LIKE_YOUR_PICTURE_AND_HOW_YOU_SAID_YOUR_NAME;
                    releaseAndPlayAudioFile(playListAccept[0]);
                }
            } else {
                if(needConfirm) {
                    mHandler.removeCallbacks(this);
                    like.dispatchTouchEvent(getDefaultResponseMotionEvent());
                }
            }
        }
    };

    private void setScrollViewListeners() {

        mGalleryScrollView.setCurrentImageChangeListener(new MyScrollView.CurrentImageChangeListener() {
            @Override
            public void onCurrentImgChanged(int position, View viewIndicator) {
                //mImg.setImageBitmap(mDatas.get(position));
                //play the video
                //viewIndicator.setBackgroundColor(Color.YELLOW);
            }
        });

        mGalleryScrollView.setOnItemClickListener(new MyScrollView.OnItemClickListener() {

            @Override
            public void onClick(View view, int position) {
                counter = 0;
                isGalleryPick = true;
                //If tap on the picture while the camera is preparing to record, LOGIN will exit directly because of the exception.
                if (isPreparing) {
                    view.setBackgroundColor(Color.YELLOW);
                    return;
                }

                //view.setBackgroundColor(Color.YELLOW);

                capture.setVisibility(View.GONE);
                like.setVisibility(View.GONE);
                dislike.setVisibility(View.GONE);

                String v,p;
                int realStartTime;
                //if waiting for confirm to save new record
                if(needConfirm) {
                    needConfirm = false;
                    stopFlash(FLASH_LIKE);
                    stopFlash(FLASH_DISLIKE);
                    //If waiting for confirm && user tap on an old one, delete the temporary new one.
                    thread.deleteVideoAndPicture();
                }

                currentUser = userInfo.get(position);
                v = currentUser.getUserVideo();
                p = currentUser.getUserIcon();
                realStartTime = Integer.parseInt(userInfo.get(position).getRecordTime());

                //if it is recoding or replaying, now stop it
                if (thread != null) {
                    thread.stopRecord();
                    thread.stopPlayingVideo();
                    thread.interrupt();
                }

                pauseAllAudios();
                //play clicked video
                if (videoThread != null) videoThread.stopPlayingVideo();
                int startTimeWithSilence = realStartTime - (language.equals(LANG_EN) ? Common.TRAILING_SILENCE_EN : Common.TRAILING_SILENCE_SW);
                // create a new EnrollmentVideo thread
                videoThread = new PlayEnrollmentVideo(surfaceHolder, mHandler, GalleryActivity.this, v, p, startTimeWithSilence);
                videoThread.start();
                view.setBackgroundColor(Color.YELLOW);
            }
        });
    }

    private void initUserInfo(){
        dbHelper = new DataHelper(this);
        userInfo = dbHelper.getUserList();
        for (int i = 0; i < userInfo.size(); i++) {
            String tempUrl = userInfo.get(i).getUserIcon();
            String profIconName = userInfo.get(i).getProfileIcon().toLowerCase();

            Integer profIcon = (language.equals(LANG_EN) ? ANIMALS_ENG.get(profIconName).first : ANIMALS_SWA.get(profIconName).first);

            Bitmap bmp = BitmapFactory.decodeFile(tempUrl);
            mDatas.add(Pair.create(bmp, profIcon));
        }
        mAdapter.accountNumber = userInfo.size();
        accountsNumber = userInfo.size();
    }


    private void setLogoOnTouchListener() {
        logo.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                // XXX when the logo is clicked, we enter the true "FaceLogin" mode
                switch(motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        // XXX
                        pressLogoAction();
                        break;

                    case MotionEvent.ACTION_UP:
                        Log.d("SPLASH", "ACTION_UP");
                        depressLogoAction();
                        logo.setVisibility(View.GONE);
                        logoShadow.setVisibility(View.GONE);
                        splash.setVisibility(View.GONE);
                        splashRoboFinger.setVisibility(View.GONE);
                        mainHandler.removeCallbacks(splashRoboFingerSlideRunnable);

                        if (userInfo.size() > 0) {
                            activity_gal.setVisibility(View.VISIBLE);
                        } else {
                            //TODO add intro video
                            genderlay.setVisibility(View.VISIBLE);
                            curUser = new UserInfo(); //incase press before prompt over
                        }

                        mpAll.start();

                        break;
                }

                return true;
            }


        });
    }

    /**
     * makes it look like Logo is being pressed
     */
    private void pressLogoAction() {
        float centerX = centerAnchor.getX();
        float centerY = centerAnchor.getY();
        Log.d("LOCATIONS", "x: " + centerX + ", " + centerY);
        logo.setX(centerX - logo.getWidth() / 2 + logoToShadow3dOffset);
        logo.setY(centerY - logo.getHeight() / 2 + logoToShadow3dOffset);

    }

    /**
     * makes it look like Logo is being depressed / unpressed
     */
    private void depressLogoAction() {
        float centerX = centerAnchor.getX();
        float centerY = centerAnchor.getY();
        Log.d("LOCATIONS", "x: " + centerX + ", " + centerY);
        logo.setX(centerX - logo.getWidth() / 2 - logoToShadow3dOffset);
        logo.setY(centerY - logo.getHeight() / 2 - logoToShadow3dOffset);
    }

    /**
     * KIMTAR this is what happens when the red "record/capture" button is pressed
     */
    private void setCaptureOnClickListener(){
        capture.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                counter = 0;
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        capture.setImageResource(R.drawable.capture_clicked);
                        break;
                    case MotionEvent.ACTION_UP:
                        isPreparing = true;
                        if(recordRec) {
                            like.setVisibility(View.GONE);
                        }
                        capture.setImageResource(R.drawable.capture);
                        pauseAllAudios();
                        capture.setVisibility(View.GONE);
                        stopFlash(FLASH_CAPTURE);
                        surfaceHolder = surfaceview.getHolder();
                        if (videoThread != null) {
                            videoThread.stopPlayingVideo();
                            videoThread.interrupt();
                        }
                        if (thread == null) {
                            if (needConfirm) {
                                needConfirm = false;
                                refreshGallery();
                            }
                        } else {
                            thread.stopPlayingVideo();
                            thread.stopRecord();
                            thread.interrupt();
                        }
                        thread = new RecordThread(RECORD_TIME, surfaceview, surfaceHolder, accountsNumber, dbHelper, mDatas, mHandler);
                        thread.start();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void startVideoThread(UserInfo user){
        String vid, p;
        int realStartTime;

        vid = user.getUserVideo();
        p = user.getUserIcon();
        realStartTime = Integer.parseInt(user.getRecordTime());
        pauseAllAudios();

        //if it is recoding or replaying, now stop it
        if (thread != null) {
            thread.stopRecord();
            thread.stopPlayingVideo();
            thread.interrupt();
        }

        //play clicked video
        if (videoThread != null) videoThread.stopPlayingVideo();
        int startTimeWithSilence = realStartTime - (language.equals(LANG_EN) ? Common.TRAILING_SILENCE_EN : Common.TRAILING_SILENCE_SW);

        // create a new EnrollmentVideo thread
        videoThread = new PlayEnrollmentVideo(surfaceHolder, mHandler, GalleryActivity.this, vid, p, startTimeWithSilence);
        videoThread.start();
    }

    /**
     * KIMTAR this is what happens when the green smiley face button is pressed
     */
    private void setLikeOnClickListener(){
        like.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean  onTouch(View v, MotionEvent event) {
                counter = 0;
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        like.setImageResource(R.drawable.like_clicked);
                        break;
                    case MotionEvent.ACTION_UP:
                        like.setImageResource(R.drawable.like);
                        if (videoThread != null) videoThread.stopPlayingVideo();
                        if (thread != null) thread.stopPlayingVideo();

                        stopFlash(FLASH_LIKE);
                        stopFlash(FLASH_DISLIKE);

                        like.setVisibility(View.GONE);
                        dislike.setVisibility(View.GONE);
                        capture.setVisibility(View.GONE);
                        coverSurface.setVisibility(View.VISIBLE);

                        pauseAllAudios();
                        //mHandler.removeCallbacksAndMessages(null);
                        if (needConfirm) {
                            if (firstAttempt) firstAttempt = false;
                            else deleteLastUserInfo();
                            saveUserInfo();
                            needConfirm = false;
                            newReg = true;
                            newUserOldNew = false;
                            //dislike.setVisibility(View.VISIBLE);

                            _audioPlaying = GOOD;
                            releaseAndPlayAudioFile(playListAfterAccepting[0]);

                        } else {
                            if(!recordRec) {
                                gallery_pics.setVisibility(View.GONE);
                                recordRec = true;
                                //mHandler.removeCallbacksAndMessages(null);
                                startVideoThread(currentUser);

                            } else{
                                recordRec = false;

                                //dbHelper.updateIconVideo(currentUser);

                                activity_gal.setVisibility(View.GONE);
                                iconpick2 = true;

                                String icntext = currentUser.getProfileIcon();
                                icontext.setText(icntext.toUpperCase());
                                iconpic.setImageDrawable(getResources().getDrawable((language.equals(LANG_EN) ?
                                        Common.ANIMALS_ENG.get(icntext.toLowerCase()).first : Common.ANIMALS_SWA.get(icntext.toLowerCase()).first)));

                                iconlay.setVisibility(View.VISIBLE);
                                releaseAndPlayAudioFileAnimal(language.equals(LANG_EN) ?
                                                Common.ANIMALS_ENG.get(icntext.toLowerCase()).second : Common.ANIMALS_SWA.get(icntext.toLowerCase()).second,
                                        new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mp) {
                                                iconpic.setVisibility(View.VISIBLE);
                                                setIconLikeOnClickListener();
                                                setIconDislikeOnClickListener();
                                                _audioPlaying = PLEASE_TAP_HERE_TO_GO_ON;
                                                releaseAndPlayAudioFile(playListLoginIcon[0]);
                                                stopFlash(FLASH_LIKE);
                                                startFlash(FLASH_LIKE);
                                            }
                                        });

                            }
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    /**
     * KIMTAR this is what happens when the red frowny face is pressed
     */
    private void setDislikeOnClickListener(){
        dislike.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean  onTouch(View v, MotionEvent event) {
                counter = 0;
                // MARCH check if first registration
                if (_audioPlaying == TAP_HERE_NO) {
                    firstRegistration = true;
                }
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dislike.setImageResource(R.drawable.dislike_clicked);
                        break;
                    case MotionEvent.ACTION_UP:
                        dislike.setImageResource(R.drawable.dislike);
                        if (videoThread != null) videoThread.stopPlayingVideo();
                        if (thread != null) thread.stopPlayingVideo();
                        stopFlash(FLASH_LIKE);
                        stopFlash(FLASH_DISLIKE);
                        like.setVisibility(View.GONE);
                        dislike.setVisibility(View.GONE);

                        if(!newUser2) {
                            capture.setVisibility(View.VISIBLE);
                            coverSurface.setVisibility(View.VISIBLE);

                            pauseAllAudios();
                            //mHandler.removeCallbacksAndMessages(null);
                            if (needConfirm) {
                                needConfirm = false;
                                thread.deleteVideoAndPicture();
                                recordAgain = true;
                                _audioPlaying = OKAY_LETS_TRY_AGAIN;
                                releaseAndPlayAudioFile(playListAfterAccepting[2]);
                            } else {
                                /*
                                _audioPlaying = LETS_TRY_AGAIN;
                                releaseAndPlayAudioFile(playListAfterDeciding[1]);
                                mGalleryScrollView.clearAllBackground();
                                */
                                if(isGalleryPick) {
                                    Log.e("isGalleryPick", "here1");
                                    mGalleryScrollView.clearAllBackground();
                                    activity_gal.setVisibility(View.VISIBLE);
                                    dislike.setVisibility(View.VISIBLE);
                                    like.setVisibility(View.GONE);
                                    capture.setVisibility(View.GONE);
                                    coverSurface.setVisibility(View.VISIBLE);
                                    isGalleryPick = false;
                                    //TODO go to "if you see your picture"
                                    _audioPlaying = IF_YOU_SEE_YOUR_PICTURE;
                                    pauseAllAudios();
                                    //mHandler.removeCallbacksAndMessages(null);
                                    releaseAndPlayAudioFile(playListStart[0]);
                                } else {
                                    Log.e("isGalleryPick", "here2");
                                    activity_gal.setVisibility(View.GONE);
                                    oldnewlay.setVisibility(View.VISIBLE);
                                    setOldNewLikeOnClickListener();
                                    setOldNewDislikeOnClickListener();
                                    pauseAllAudios();
                                    //mHandler.removeCallbacksAndMessages(null);
                                    _audioPlaying = IF_YOUVE_USED_ROBOTUTOR_BEFORE;
                                    releaseAndPlayAudioFile(playListOldNew[0]);
                                }
                            }
                        } else {
                            Log.e("isGalleryPick", "here3");
                            activity_gal.setVisibility(View.GONE);
                            oldnewlay.setVisibility(View.VISIBLE);
                            setOldNewLikeOnClickListener();
                            setOldNewDislikeOnClickListener();
                            pauseAllAudios();
                            //mHandler.removeCallbacksAndMessages(null);
                            _audioPlaying = IF_YOUVE_USED_ROBOTUTOR_BEFORE;
                            releaseAndPlayAudioFile(playListOldNew[0]);
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void setGenderBoyOnClickListener(){
        genderboy.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                counter = 0;
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        genderboy.setImageResource(R.drawable.gender_boy_clicked);
                        break;
                    case MotionEvent.ACTION_UP:
                        genderboy.setImageResource(R.drawable.gender_boy);

                        curUser.setGender("male");

                        Log.e("Gender set", curUser.getGender());
                        stopFlash(FLASH_BOY);
                        stopFlash(FLASH_GIRL);
                        genderlay.setVisibility(View.GONE);

                        //TODO pick less used
                        String[] ANIMAL_NAMES = (language.equals(LANG_EN) ? ANIMAL_NAMES_ENG : ANIMAL_NAMES_SWA);

                        String icntext = ANIMAL_NAMES[new Random().nextInt(ANIMAL_NAMES.length)];
                        icontext.setText(icntext.toUpperCase());
                        iconpic.setImageDrawable(getResources().getDrawable((language.equals(LANG_EN) ?
                                ANIMALS_ENG.get(icntext).first : ANIMALS_SWA.get(icntext).first)));
                        iconlay.setVisibility(View.VISIBLE);

                        genderRegd = true;
                        _audioPlaying = GOOD;
                        pauseAllAudios();
                        //mHandler.removeCallbacksAndMessages(null);
                        releaseAndPlayAudioFile(playListAfterAccepting[0]);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void setGenderGirlOnClickListener(){
        gendergirl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                counter = 0;
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        gendergirl.setImageResource(R.drawable.gender_girl_clicked);
                        break;
                    case MotionEvent.ACTION_UP:
                        gendergirl.setImageResource(R.drawable.gender_girl);

                        curUser.setGender("female");
                        Log.e("Gender set", curUser.getGender());
                        stopFlash(FLASH_GIRL);
                        stopFlash(FLASH_BOY);
                        genderlay.setVisibility(View.GONE);

                        //TODO pick less used
                        String[] ANIMAL_NAMES = (language.equals(LANG_EN) ? ANIMAL_NAMES_ENG : ANIMAL_NAMES_SWA);

                        String icntext = ANIMAL_NAMES[new Random().nextInt(ANIMAL_NAMES.length)];
                        icontext.setText(icntext.toUpperCase());
                        iconpic.setImageDrawable(getResources().getDrawable((language.equals(LANG_EN) ?
                                ANIMALS_ENG.get(icntext).first : ANIMALS_SWA.get(icntext).first)));
                        iconlay.setVisibility(View.VISIBLE);

                        genderRegd = true;
                        _audioPlaying = GOOD;
                        pauseAllAudios();
                        //mHandler.removeCallbacksAndMessages(null);
                        releaseAndPlayAudioFile(playListAfterAccepting[0]);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void setIconLikeOnClickListener(){
        iconlike.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                counter = 0;
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        iconlike.setImageResource(R.drawable.like_clicked);
                        break;
                    case MotionEvent.ACTION_UP:
                        iconlike.setImageResource(R.drawable.like);
                        if (!iconpick2) {
                            curUser.setProfileIcon(icontext.getText().toString().trim());
                            Log.e("Icon set", curUser.getProfileIcon());

                            stopFlash(FLASH_LIKE);
                            stopFlash(FLASH_DISLIKE);
                            iconlay.setVisibility(View.GONE);
                            iconpic.setVisibility(View.INVISIBLE);

                            iconRegd = true;
                            iconRepeat = false;
                            activity_gal.setVisibility(View.VISIBLE);
                            capture.setVisibility(View.VISIBLE);
                            coverSurface.setVisibility(View.VISIBLE);
                            _audioPlaying = GOOD;
                            pauseAllAudios();
                            //mHandler.removeCallbacksAndMessages(null);
                            releaseAndPlayAudioFile(playListAfterAccepting[0]);
                        } else {

                            // currently disabled recreational icon update

                            //currentUser.setProfileIcon(icontext.getText().toString().trim());
                            //Log.e("Icon set again", currentUser.getProfileIcon());
                            //dbHelper.updateProfileIcon(currentUser);
                            pauseAllAudios();
                            //mHandler.removeCallbacksAndMessages(null);
                            _audioPlaying = LETS_GET_STARTED;
                            releaseAndPlayAudioFile(playListAfterDeciding[0]);
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void setIconDislikeOnClickListener(){
        icondislike.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                counter = 0;
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        icondislike.setImageResource(R.drawable.dislike_clicked);
                        break;
                    case MotionEvent.ACTION_UP:
                        icondislike.setImageResource(R.drawable.dislike);

                        stopFlash(FLASH_DISLIKE);
                        stopFlash(FLASH_LIKE);
                        //TODO pick less used

                        String[] ANIMAL_NAMES = (language.equals(LANG_EN) ? ANIMAL_NAMES_ENG : ANIMAL_NAMES_SWA);

                        String icntext = ANIMAL_NAMES[new Random().nextInt(ANIMAL_NAMES.length)];
                        icontext.setText(icntext.toUpperCase());
                        iconpic.setImageDrawable(getResources().getDrawable((language.equals(LANG_EN) ?
                                ANIMALS_ENG.get(icntext).first : ANIMALS_SWA.get(icntext).first)));
                        iconpic.setVisibility(View.INVISIBLE);

                        iconRepeat = true;
                        _audioPlaying = OKAY_LETS_TRY_AGAIN;
                        pauseAllAudios();
                        //mHandler.removeCallbacksAndMessages(null);
                        releaseAndPlayAudioFile(playListAfterAccepting[2]);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void setOldNewLikeOnClickListener(){
        oldnewlike.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                counter = 0;
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        oldnewlike.setImageResource(R.drawable.like_clicked);
                        break;
                    case MotionEvent.ACTION_UP:
                        oldnewlike.setImageResource(R.drawable.like);
                        newUser2 = false;
                        isGalleryPick = false;

                        Log.e("Robotutor used", "yes");
                        stopFlash(FLASH_LIKE);
                        stopFlash(FLASH_DISLIKE);

                        mGalleryScrollView.clearAllBackground();

                        oldnewlay.setVisibility(View.GONE);

                        activity_gal.setVisibility(View.VISIBLE);
                        dislike.setVisibility(View.VISIBLE);
                        like.setVisibility(View.GONE);
                        capture.setVisibility(View.GONE);
                        coverSurface.setVisibility(View.VISIBLE);

                        //TODO go to "if you see your picture"
                        _audioPlaying = IF_YOU_SEE_YOUR_PICTURE;
                        pauseAllAudios();
                        //mHandler.removeCallbacksAndMessages(null);
                        releaseAndPlayAudioFile(playListStart[0]);

                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void setOldNewDislikeOnClickListener(){
        oldnewdislike.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                counter = 0;
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        oldnewdislike.setImageResource(R.drawable.dislike_clicked);
                        break;
                    case MotionEvent.ACTION_UP:
                        oldnewdislike.setImageResource(R.drawable.dislike);

                        Log.e("Robotutor used", "no");
                        stopFlash(FLASH_DISLIKE);
                        stopFlash(FLASH_LIKE);
                        newUser2 = false;
                        oldnewlay.setVisibility(View.GONE);
                        pauseAllAudios();
                        genderlay.setVisibility(View.VISIBLE);
                        newUserOldNew = true;
                        _audioPlaying = THIS_IS_ROBOTUTOR;
                        curUser = new UserInfo();

                        //mHandler.removeCallbacksAndMessages(null);
                        releaseAndPlayAudioFile(playListRT[0]);

                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    public void releaseAndPlayAudioFileAnimal(int id, MediaPlayer.OnCompletionListener next){
        if(mpAnimal != null) {
            mpAnimal.release();
        }
        mpAnimal = MediaPlayer.create(this, id);
        mpAnimal.setOnCompletionListener(next);
        mpAnimal.start();
    }
    private void deleteLastUserInfo() {
        //delete video
        String vPath = userInfo.get(0).getUserVideo();
        File video = new File(vPath);
        if (video.exists()) video.delete();

        //delete picture
        String pPath = userInfo.get(0).getUserIcon();
        File picture = new File(pPath);
        if (picture.exists()) picture.delete();

        //delete user info in DB
        accountsNumber--;
        dbHelper.deletUserInfo(accountsNumber + "");
    }


    // MARCH ADDTHIS icon and recency info for arranging gallery
    private void saveUserInfo() {
        //save info and update UI Interface
        // only save when all fields are there
        String birthDate = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        curUser.setID(accountsNumber);
        curUser.setUserIcon(thread.pPath);
        curUser.setUserVideo(thread.vPath);
        curUser.setRecordTime(thread.relativeStartTime + "");
        curUser.setLastLoginTime(birthDate);
        curUser.setBirthDevice(DataHelper.SERIAL_ID);
        curUser.setBirthDate(birthDate);
        dbHelper.saveUserInfo(curUser);
        dbHelper.saveUserTime(curUser);

        Log.e("users table gallact: ", dbHelper.getTableAsString(SqliteHelper.TB_NAME));
        Log.e("recency table gallact: ", dbHelper.getTableAsString(SqliteHelper.REC_NAME));

        accountsNumber++;
        refreshGallery();
    }


    private void refreshGallery() {
        mDatas.clear();
        initUserInfo();
        mAdapter = new ScrollViewAdapter(this, mDatas);
        mGalleryScrollView.initDatas(mAdapter, needConfirm);
        //mGalleryScrollView.emphasizeNewPicture();
    }

    /**
     * After taking a new video, refresh the gallery before confirming
     */
    private void refreshGalleryBeforeConfirm(){
        Bitmap bmp = BitmapFactory.decodeFile(thread.pPath);
        mDatas.clear();
        initUserInfo();
        String profIconName = currentUser.getProfileIcon(); //TODO when this is used, put check for curUser
        //TODO  or currentUser

        Integer profIcon = (language.equals(LANG_EN) ? ANIMALS_ENG.get(profIconName).first : ANIMALS_SWA.get(profIconName).first);

        mDatas.add(0, Pair.create(bmp, profIcon));
        if(userInfo.size() == 0) mAdapter = new ScrollViewAdapter(this, mDatas);

        mAdapter.setMDatas(mDatas);
        mAdapter.accountNumber = mDatas.size();
        mGalleryScrollView.initDatas(mAdapter, needConfirm);
        //mGalleryScrollView.shrinkPicture(mAdapter, surfaceview.getWidth(), surfaceview.getHeight());
    }

    public boolean checkCount(){
        if (this.counter == 2) {
            this.counter = 0;
            return true;
            //toSTART()
        } else {
            return false;
        }
    }

    /**
     * KIMTAR this is where various messages are handled from "RecordThread" and other places
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // MARCH if counter reaches 3 then go to WELCOME
            super.handleMessage(msg);

            switch (msg.what) {
                case READY_TO_RECORD:
                    isPreparing = false;
                    coverSurface.setVisibility(View.INVISIBLE);
                    //prompt "please say your name"
                    _audioPlaying = PLEASE_SAY_YOUR_NAME;
                    releaseAndPlayAudioFile(playListRecord[0]);
                    // MARCH reset counter to 0
                    counter = 0;
                    break;
                case RECORD_DONE:
                    //record done, prompt "you said", then replay video
                    _audioPlaying = YOU_SAID;
                    releaseAndPlayAudioFile(playListRecord[1]);
                    // MARCH reset counter to 0
                    counter = 0;
                    break;
                case REPLAY_NEW_VIDEO_DONE:
                    //if you like how you said your name, please tap here
                    //add newly taken photo to the gallery though not in the database
                    //refreshGalleryBeforeConfirm();
                    // MARCH DOTHIS ONLY SAVE VIDEO IF IN [LOGIN_APPROVE_VIDEO 1]
                    // MARCH USE BOOL FIRSTVIDEO TO HELP DISTINGUISH BETWEEN FIRST TIME AND NOT
                    if(!recordRec) {
                        like.setVisibility(View.VISIBLE);
                        dislike.setVisibility(View.VISIBLE);

                        mHandler.post(toACCEPT);
                        needConfirm = true;
                    } else {
                        like.setVisibility(View.VISIBLE);
                        capture.setVisibility(View.VISIBLE);
                        currentUser.setUserVideo(thread.vPath);
                        currentUser.setUserIcon(thread.pPath);

                        currentUser.setRecordTime(thread.relativeStartTime + "");
                        //mHandler.removeCallbacksAndMessages(null);
                        mHandler.post(toDECIDERecordRec);
                    }
                    // MARCH reset counter to 0
                    counter = 0;
                    break;
                case REFRESH_GALLERY:
                    refreshGallery();
                    // MARCH reset counter to 0
                    counter = 0;
                    break;
                case REPLAY_EXISTING_VIDEO_DONE:
                    //go to the menu
                    if(!recordRec) {
                        like.setVisibility(View.VISIBLE);
                        dislike.setVisibility(View.VISIBLE);
                        mHandler.post(toDECIDE);
                        // MARCH reset counter to 0
                        counter = 0;
                    } else{
                        like.setVisibility(View.VISIBLE);
                        capture.setVisibility(View.VISIBLE);
                        mHandler.post(toDECIDERecordRec);
                        counter = 0;
                    }
                    break;
                case PLAY_TAPPING_VIDEO_DONE:
                    // XXX when video is done, return to Splash/logo screen
                    if (surfaceviewFullScreen.getVisibility() == View.VISIBLE) {
                        logo.setVisibility(View.VISIBLE);
                        logoShadow.setVisibility(View.VISIBLE);
                        splash.setVisibility(View.VISIBLE);
                        surfaceviewFullScreen.setVisibility(View.INVISIBLE);
                        splashRoboFinger.setVisibility(View.INVISIBLE);
                        mainHandler.postDelayed(splashRoboFingerSlideRunnable, DELAY_TO_SHOW_CHANGE_OF_FINGER);
                    } else
                        // when does this happen?
                        coverSurface.setVisibility(View.VISIBLE);
                    // MARCH reset counter to 0
                    counter = 0;
                    break;
                case UNCOVER_SCREEN:
                    coverSurface.setVisibility(View.INVISIBLE);
                    // MARCH reset counter to 0
                    counter = 0;
                    break;
                default:
                    break;
            }
        }
    };

    private Runnable splashRoboFingerSlideRunnable = new Runnable() {
        @Override
        public void run() {

            // XXX run an animation here...
            // RoboFinger becomes visible and starts (100,100) away from logo
            splashRoboFinger.setVisibility(View.VISIBLE);
            float startX = logo.getX() + logo.getWidth() + 5f;
            float startY = logo.getY() + logo.getWidth() + 5f;

            float endX = logo.getX() + (logo.getWidth() / 2);
            float endY = logo.getY() + (logo.getHeight() / 2);

            splashRoboFinger.setX(startX);
            splashRoboFinger.setY(startY);

            AnimatorSet diagonalMovement = new AnimatorSet();

            ObjectAnimator animX = ObjectAnimator.ofFloat(splashRoboFinger, "translationX", startX, endX);
            ObjectAnimator animY = ObjectAnimator.ofFloat(splashRoboFinger, "translationY", startY, endY);
            diagonalMovement.playTogether(animX, animY);

            diagonalMovement.setDuration(2000);

            diagonalMovement.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    splashRoboFinger.setImageResource(R.drawable.robofinger_press);
                    pressLogoAction();

                    mainHandler.postDelayed(splashRoboFingerUntapRunnable, 500);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    splashRoboFinger.setImageResource(R.drawable.robofinger_press);
                    pressLogoAction();
                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

            diagonalMovement.start();

        }
    };

    /**
     * the motion of pressing down the RoboTutor logo
     */
    private Runnable spashRoboFingerTapRunnable = new Runnable() {

        @Override
        public void run() {
            splashRoboFinger.setImageResource(R.drawable.robofinger_press);
            pressLogoAction();
        }
    };

    /**
     * the motion of untapping / depressing the RoboTutor logo
     */
    private Runnable splashRoboFingerUntapRunnable = new Runnable() {
        @Override
        public void run() {
            splashRoboFinger.setImageResource(R.drawable.robofinger_extend);
            depressLogoAction();
        }
    };

    private void startFlash(int viewToFlash) {
        switch (viewToFlash) {
            case FLASH_CAPTURE:
                capture_startFlashTIme = System.nanoTime();
                capture_lastFlashTime = System.nanoTime();
                capture_stopFlash = false;
                capture_nowClicked = false;
                mainHandler.post(flashCaptureRunnable);
                break;

            case FLASH_LIKE:
                like_startFlashTIme = System.nanoTime();
                like_lastFlashTime = System.nanoTime();
                like_stopFlash = false;
                like_nowClicked = false;
                if(!recordRec && (_audioPlaying == TAP_HERE_YES_ICON || _audioPlaying == PLEASE_TAP_HERE_TO_GO_ON)){
                    iconlike.setImageResource(R.drawable.like_finger);
                    mainHandler.post(flashLikeRunnableIcon);
                } else if (_audioPlaying == TAP_HERE_YES) {
                    oldnewlike.setImageResource(R.drawable.like_finger);
                    mainHandler.post(flashLikeRunnableOldNew);
                } else {
                    like.setImageResource(R.drawable.like_finger);
                    mainHandler.post(flashLikeRunnable);
                }
                break;

            case FLASH_BOY:
                boy_startFlashTIme = System.nanoTime();
                boy_lastFlashTime = System.nanoTime();
                boy_stopFlash = false;
                boy_nowClicked = false;
                genderboy.setImageResource(R.drawable.gender_boy_finger);
                mainHandler.post(flashBoyRunnable);
                break;

            case FLASH_GIRL:
                girl_startFlashTIme = System.nanoTime();
                girl_lastFlashTime = System.nanoTime();
                girl_stopFlash = false;
                girl_nowClicked = false;
                gendergirl.setImageResource(R.drawable.gender_girl_finger);
                mainHandler.post(flashGirlRunnable);
                break;

            case FLASH_DISLIKE:
                dislike_startFlashTIme = System.nanoTime();
                dislike_lastFlashTime = System.nanoTime();
                dislike_stopFlash = false;
                dislike_nowClicked = false;
                if(_audioPlaying == TAP_HERE_NO_ICON || _audioPlaying == TAP_HERE_NO_LOGIN_ICON){
                    icondislike.setImageResource(R.drawable.dislike_finger);
                    mainHandler.post(flashDislikeRunnableIcon);
                } else if(_audioPlaying == TAP_HERE_NO){
                    oldnewdislike.setImageResource(R.drawable.dislike_finger);
                    mainHandler.post(flashDislikeRunnableOldNew);
                } else {
                    dislike.setImageResource(R.drawable.dislike_finger);
                    mainHandler.post(flashDislikeRunnable);
                }
                break;

            default:
                break;
        }
    }

    private void stopFlash(int viewToStop) {
        switch (viewToStop) {
            case FLASH_CAPTURE:
                capture_startFlashTIme = System.nanoTime();
                capture_lastFlashTime = System.nanoTime();
                capture_stopFlash = true;
                break;

            case FLASH_LIKE:
                like_startFlashTIme = System.nanoTime();
                like_lastFlashTime = System.nanoTime();
                like_stopFlash = true;
                break;

            case FLASH_DISLIKE:
                dislike_startFlashTIme = System.nanoTime();
                dislike_lastFlashTime = System.nanoTime();
                dislike_stopFlash = true;
                break;

            case FLASH_BOY:
                boy_startFlashTIme = System.nanoTime();
                boy_lastFlashTime = System.nanoTime();
                boy_stopFlash = true;
                break;

            case FLASH_GIRL:
                girl_startFlashTIme = System.nanoTime();
                girl_lastFlashTime = System.nanoTime();
                girl_stopFlash = true;
                break;

            default:
                break;
        }
    }

    /**
     * change capture button's color from white to red consecutively 3 times.
     */
    private Runnable flashCaptureRunnable = new Runnable() {
        @Override
        public void run() {
            if (capture_stopFlash) {
                capture.setImageResource(R.drawable.capture);
                capture_nowClicked = false;
                return;
            }
            //transfer nanosecond to millisecond
            long timepass = (System.nanoTime() - capture_lastFlashTime) / 1000000;
            if(timepass >  FLASH_FREQUENCE) {
                if(!capture_nowClicked) {
                    capture.setImageResource(R.drawable.capture_finger_clicked);
                    capture_nowClicked = true;
                } else {
                    capture.setImageResource(R.drawable.capture_finger);
                    capture_nowClicked = false;
                }
                capture_lastFlashTime = System.nanoTime();
            }

            if ((System.nanoTime() - capture_startFlashTIme)/1000000 <= FLASH_DURATION)
                mainHandler.postDelayed(flashCaptureRunnable, FLASH_FREQUENCE);
            else {
                capture.setImageResource(R.drawable.capture);
                capture_nowClicked = false;

                //if user hesitate, play the video of good tapping
                //mainHandler.postDelayed(playVideoOfGoodTappingRunnable, DELAY_TO_SHOW_VIDEO);

            }
        }
    };

    /**
     * show tapping video
     */
    private Runnable playVideoOfGoodTappingRunnable = new Runnable() {
        @Override
        public void run() {
            if (logo.getVisibility() == View.VISIBLE) {
                surfaceviewFullScreen.setVisibility(View.VISIBLE);
                logo.setVisibility(View.INVISIBLE);
                logoShadow.setVisibility(View.INVISIBLE);
                splash.setVisibility(View.INVISIBLE);
                videoThread = new PlayTappingVideo(surfaceviewFullScreen.getHolder(), mHandler, GalleryActivity.this);
                videoThread.start();
            } else if (readyToStartTimer){
                videoThread = new PlayTappingVideo(surfaceHolder, mHandler, GalleryActivity.this);
                if (capture.getVisibility() == View.VISIBLE) videoThread.start();
            }
            readyToStartTimer = true;
        }
    };

    //change Like button's color from white to red consecutively 3 times.
    private Runnable flashLikeRunnable = new Runnable() {
        @Override
        public void run() {
            if (like_stopFlash) {
                like.setImageResource(R.drawable.like);
                like_nowClicked = false;
                return;
            }
            //transfer nanosecond to millisecond
            long timepass = (System.nanoTime() - like_lastFlashTime) / 1000000;
            if(timepass > FLASH_FREQUENCE) {
                if(!like_nowClicked) {
                    like.setImageResource(R.drawable.like_finger_clicked);
                    like_nowClicked = true;
                } else {
                    like_nowClicked = false;
                    like.setImageResource(R.drawable.like_finger);
                }
                like_lastFlashTime = System.nanoTime();
            }

            if ((System.nanoTime() - like_startFlashTIme)/1000000 <= FLASH_DURATION)
                mainHandler.postDelayed(flashLikeRunnable, FLASH_FREQUENCE);
            else {
                like.setImageResource(R.drawable.like);
                like_nowClicked = false;
            }
        }
    };

    private Runnable flashLikeRunnableIcon = new Runnable() {
        @Override
        public void run() {
            if (like_stopFlash) {
                iconlike.setImageResource(R.drawable.like);
                like_nowClicked = false;
                return;
            }
            //transfer nanosecond to millisecond
            long timepass = (System.nanoTime() - like_lastFlashTime) / 1000000;
            if(timepass > FLASH_FREQUENCE) {
                if(!like_nowClicked) {
                    iconlike.setImageResource(R.drawable.like_finger_clicked);
                    like_nowClicked = true;
                } else {
                    like_nowClicked = false;
                    iconlike.setImageResource(R.drawable.like_finger);
                }
                like_lastFlashTime = System.nanoTime();
            }

            if ((System.nanoTime() - like_startFlashTIme)/1000000 <= FLASH_DURATION)
                mainHandler.postDelayed(flashLikeRunnableIcon, FLASH_FREQUENCE);
            else {
                iconlike.setImageResource(R.drawable.like);
                like_nowClicked = false;
            }
        }
    };

    private Runnable flashLikeRunnableOldNew = new Runnable() {
        @Override
        public void run() {
            if (like_stopFlash) {
                oldnewlike.setImageResource(R.drawable.like);
                like_nowClicked = false;
                return;
            }
            //transfer nanosecond to millisecond
            long timepass = (System.nanoTime() - like_lastFlashTime) / 1000000;
            if(timepass > FLASH_FREQUENCE) {
                if(!like_nowClicked) {
                    oldnewlike.setImageResource(R.drawable.like_finger_clicked);
                    like_nowClicked = true;
                } else {
                    like_nowClicked = false;
                    oldnewlike.setImageResource(R.drawable.like_finger);
                }
                like_lastFlashTime = System.nanoTime();
            }

            if ((System.nanoTime() - like_startFlashTIme)/1000000 <= FLASH_DURATION)
                mainHandler.postDelayed(flashLikeRunnableOldNew, FLASH_FREQUENCE);
            else {
                oldnewlike.setImageResource(R.drawable.like);
                like_nowClicked = false;
            }
        }
    };

    /**
     *  change Dislike button's color from white to red consecutively 3 times.
     */
    private Runnable flashDislikeRunnable = new Runnable() {
        @Override
        public void run() {
            if (dislike_stopFlash) {
                dislike.setImageResource(R.drawable.dislike);
                dislike_nowClicked = false;
                return;
            }
            //transfer nanosecond to millisecond
            long timepass = (System.nanoTime() - dislike_lastFlashTime) / 1000000;
            if(timepass > FLASH_FREQUENCE) {
                if(!dislike_nowClicked) {
                    dislike.setImageResource(R.drawable.dislike_finger_clicked);
                    dislike_nowClicked = true;
                } else {
                    dislike.setImageResource(R.drawable.dislike_finger);
                    dislike_nowClicked = false;
                }
                dislike_lastFlashTime = System.nanoTime();
            }

            if ((System.nanoTime() - dislike_startFlashTIme)/1000000 <= FLASH_DURATION)
                mainHandler.postDelayed(flashDislikeRunnable, FLASH_FREQUENCE);
            else {
                dislike.setImageResource(R.drawable.dislike);
                dislike_nowClicked = false;
            }
        }
    };

    private Runnable flashDislikeRunnableIcon = new Runnable() {
        @Override
        public void run() {
            if (dislike_stopFlash) {
                icondislike.setImageResource(R.drawable.dislike);
                dislike_nowClicked = false;
                return;
            }
            //transfer nanosecond to millisecond
            long timepass = (System.nanoTime() - dislike_lastFlashTime) / 1000000;
            if(timepass > FLASH_FREQUENCE) {
                if(!dislike_nowClicked) {
                    icondislike.setImageResource(R.drawable.dislike_finger_clicked);
                    dislike_nowClicked = true;
                } else {
                    icondislike.setImageResource(R.drawable.dislike_finger);
                    dislike_nowClicked = false;
                }
                dislike_lastFlashTime = System.nanoTime();
            }

            if ((System.nanoTime() - dislike_startFlashTIme)/1000000 <= FLASH_DURATION)
                mainHandler.postDelayed(flashDislikeRunnableIcon, FLASH_FREQUENCE);
            else {
                icondislike.setImageResource(R.drawable.dislike);
                dislike_nowClicked = false;
            }
        }
    };

    private Runnable flashDislikeRunnableOldNew = new Runnable() {
        @Override
        public void run() {
            if (dislike_stopFlash) {
                oldnewdislike.setImageResource(R.drawable.dislike);
                dislike_nowClicked = false;
                return;
            }
            //transfer nanosecond to millisecond
            long timepass = (System.nanoTime() - dislike_lastFlashTime) / 1000000;
            if(timepass > FLASH_FREQUENCE) {
                if(!dislike_nowClicked) {
                    oldnewdislike.setImageResource(R.drawable.dislike_finger_clicked);
                    dislike_nowClicked = true;
                } else {
                    oldnewdislike.setImageResource(R.drawable.dislike_finger);
                    dislike_nowClicked = false;
                }
                dislike_lastFlashTime = System.nanoTime();
            }

            if ((System.nanoTime() - dislike_startFlashTIme)/1000000 <= FLASH_DURATION)
                mainHandler.postDelayed(flashDislikeRunnableOldNew, FLASH_FREQUENCE);
            else {
                oldnewdislike.setImageResource(R.drawable.dislike);
                dislike_nowClicked = false;
            }
        }
    };

    private Runnable flashBoyRunnable = new Runnable() {
        @Override
        public void run() {
            if (boy_stopFlash) {
                genderboy.setImageResource(R.drawable.gender_boy);
                boy_nowClicked = false;
                return;
            }
            //transfer nanosecond to millisecond
            long timepass = (System.nanoTime() - boy_lastFlashTime) / 1000000;
            if(timepass > FLASH_FREQUENCE) {
                if(!boy_nowClicked) {
                    genderboy.setImageResource(R.drawable.gender_boy_finger_clicked);
                    boy_nowClicked = true;
                } else {
                    genderboy.setImageResource(R.drawable.gender_boy_finger);
                    boy_nowClicked = false;
                }
                boy_lastFlashTime = System.nanoTime();
            }

            if ((System.nanoTime() - boy_startFlashTIme)/1000000 <= FLASH_DURATION)
                mainHandler.postDelayed(flashBoyRunnable, FLASH_FREQUENCE);
            else {
                genderboy.setImageResource(R.drawable.gender_boy);
                boy_nowClicked = false;
            }
        }
    };

    private Runnable flashGirlRunnable = new Runnable() {
        @Override
        public void run() {
            if (girl_stopFlash) {
                gendergirl.setImageResource(R.drawable.gender_girl);
                girl_nowClicked = false;
                return;
            }
            //transfer nanosecond to millisecond
            long timepass = (System.nanoTime() - girl_lastFlashTime) / 1000000;
            if(timepass > FLASH_FREQUENCE) {
                if(!girl_nowClicked) {
                    gendergirl.setImageResource(R.drawable.gender_girl_finger_clicked);
                    girl_nowClicked = true;
                } else {
                    gendergirl.setImageResource(R.drawable.gender_girl_finger);
                    girl_nowClicked = false;
                }
                girl_lastFlashTime = System.nanoTime();
            }

            if ((System.nanoTime() - girl_startFlashTIme)/1000000 <= FLASH_DURATION)
                mainHandler.postDelayed(flashGirlRunnable, FLASH_FREQUENCE);
            else {
                gendergirl.setImageResource(R.drawable.gender_girl);
                girl_nowClicked = false;
            }
        }
    };


    /**
     * A Runnable that slides RoboFinger along the enrollment photo gallery
     */
    private Runnable slideGalleryRunnable = new Runnable() {
        @Override
        public void run() {
            // XXX aha, so this is where the RoboFinger does the sliding
            slideRoboFinger.setImageResource(R.drawable.robofinger_press);
            int dy = mGalleryScrollView.mScreenHeight / 2;
            ObjectAnimator anim = ObjectAnimator.ofFloat(slideRoboFinger, "translationY", 0, -dy, 0);
            anim.setDuration(2000);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    slideRoboFinger.setImageResource(R.drawable.robofinger_extend);
                    mainHandler.postDelayed(hideSlideFingerRunnable, DELAY_TO_SHOW_CHANGE_OF_FINGER);
                    mainHandler.postDelayed(promptIfNotFindPictureRunnable, DELAY_AFTER_SHOWING_SLIDE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    slideRoboFinger.setImageResource(R.drawable.robofinger_extend);
                    mainHandler.postDelayed(hideSlideFingerRunnable, DELAY_TO_SHOW_CHANGE_OF_FINGER);
                    mainHandler.postDelayed(promptIfNotFindPictureRunnable, DELAY_AFTER_SHOWING_SLIDE);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

            anim.start();
        }
    };

    private Runnable promptIfNotFindPictureRunnable = new Runnable() {
        @Override
        public void run() {
            if (capture.getVisibility() == View.VISIBLE) {

                _audioPlaying = IF_YOU_DONT_FIND_YOUR_PICTURE;
                releaseAndPlayAudioFile(playListStart[4]);
            }
        }
    };

    /**
     * Runnable that gets run by the Main Handler
     */
    private Runnable hideSlideFingerRunnable = new Runnable() {
        @Override
        public void run() {
            slideRoboFinger.setVisibility(View.INVISIBLE);
        }
    };

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub
        // the "holder" is get in oncreat() at the beginning
        Log.i("SurfaceHolder", "surfaceChanged()");
        surfaceHolder = holder;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        Log.i("SurfaceHolder", Thread.currentThread().getName());
        // the "holder" is get in oncreat() at the beginning
        surfaceHolder = holder;
        // 录像线程，当然也可以在别的地方启动，但是一定要在onCreate方法执行完成以及surfaceHolder被赋值以后启动
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
        Log.i("SurfaceHolder", "surfaceDestroyed()");
        //release the resources
/*        surfaceview = null;
        surfaceHolder = null;*/
		/* mediarecorder mCamera */
        if (thread!=null) {
            thread.stopRecord();
            thread=null;
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if(pauseWhileRecord) {
            pauseWhileRecord = false;
            pauseAllAudios();
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }

        Log.i("RecordDemoActivity", "onResume()");
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        pauseAllAudios();
        if(thread != null){
            if(thread.isRecording || thread.isPlaying) {
                pauseWhileRecord = true;
                thread.stopRecord();
                thread.stopPlayingVideo();
                thread.interrupt();
            }
        }
        Log.i("RecordDemoActivity", "onPause()");
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        pauseAllAudios();
        if(thread != null){
            if(thread.isRecording || thread.isPlaying) {
                thread.stopRecord();
                thread.stopPlayingVideo();
                thread.interrupt();
            }
        }
        Log.i("RecordDemoActivity", "onDestroy()");

    }

    private void pauseAllAudios() {
        if (mpAll.isPlaying()) mpAll.pause();
    }

}
