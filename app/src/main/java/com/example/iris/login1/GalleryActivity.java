package com.example.iris.login1;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.iris.login1.Common.*;
import static com.example.iris.login1.Common.STATE.*;

public class GalleryActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    /* --------------------------------- */
    /* ---------- Audio files ---------- */
    /* --------------------------------- */
    //Lack for Swahili audio here, replace them later
    private int[] mediaListStartSwa = {R.raw.swa_ifyouseeyourpicture, R.raw.swa_pleasetaponyourpicture, R.raw.swa_toseemorepictures, R.raw.swa_slidethemlikethis, R.raw.swa_ifyoudontfindyourpicture, R.raw.swa_pleasetaphere2};
    private int[] mediaListStartEng = {R.raw.eng_ifyouseeyourpicture, R.raw.eng_pleasetaponyourpicture, R.raw.eng_toseemorepictures, R.raw.eng_slidethemlikethis, R.raw.eng_ifyoudontfindyourpicture, R.raw.eng_pleasetaphere2};
    private int[] mediaListRecordSwa = {R.raw.swa_pleasesayyourname, R.raw.swa_yousaid};
    private int[] mediaListRecordEng = {R.raw.eng_pleasesayyourname, R.raw.eng_yousaid};
    private int[] mediaListAcceptSwa = {R.raw.swa_ifyoulikeyourpictureandhowyousaidyourname, R.raw.swa_pleasetaphere1, R.raw.swa_otherwisetaphere};
    private int[] mediaListAcceptEng = {R.raw.eng_ifyoulikeyourpictureandhowyousaidyourname, R.raw.eng_pleasetaphere1, R.raw.eng_otherwisetaphere};

    //Lack for Swahili audio here, replace them later
    private int[] mediaListAfterAcceptingSwa = {R.raw.swa_good, R.raw.swa_now, R.raw.swa_okletstryagain};
    private int[] mediaListAfterAcceptingEng = {R.raw.eng_good, R.raw.eng_now, R.raw.eng_okletstryagain};
    private int[] mediaListDecideSwa = {R.raw.swa_ifthisisyou, R.raw.swa_pleasetaphere2, R.raw.swa_ifthisisnotyou, R.raw.swa_pleasetaphere2};
    private int[] mediaListDecideEng = {R.raw.eng_ifthisisyou, R.raw.eng_pleasetaphere2, R.raw.eng_ifthisisnotyou, R.raw.eng_pleasetaphere2};
    private int[] mediaListAfterDecidingSwa = {R.raw.swa_letsgetstarted, R.raw.swa_letstryagain};
    private int[] mediaListAfterDecidingEng = {R.raw.eng_letsgetstarted, R.raw.eng_letstryagain};

    /* ---------------------------------------- */
    /* ---------- Audio file holders ---------- */
    /* ---------------------------------------- */
    private int[] playListStart;
    private int[] playListRecord;
    private int[] playListAccept;
    private int[] playListAfterAccepting;
    private int[] playListDecide;
    private int[] playListAfterDeciding;

    private MediaPlayer mpAll;
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
    private List<Bitmap> mDatas =  new ArrayList<Bitmap>();     //list of user's icon
    private DataHelper dbHelper;
    private List<UserInfo> userInfo = new ArrayList<UserInfo>();
    private ImageView capture;
    private ImageView like;
    private ImageView dislike;
    /* RoboFinger */
    private ImageView slideRoboFinger;
    private int accountsNumber;
    private PlayVideoThread videoThread;
    private boolean readyToStartTimer = false;
    private boolean isPreparing = false;
    private boolean needConfirm = false;
    private boolean firstAttempt = true;

    private long capture_lastFlashTime = System.nanoTime();
    private long capture_startFlashTIme = System.nanoTime();
    private long like_lastFlashTime = System.nanoTime();
    private long like_startFlashTIme = System.nanoTime();
    private long dislike_lastFlashTime = System.nanoTime();
    private long dislike_startFlashTIme = System.nanoTime();

    private boolean capture_nowClicked = false;
    private boolean like_nowClicked = false;
    private boolean dislike_nowClicked = false;

    private boolean capture_stopFlash = false;
    private boolean like_stopFlash = false;
    private boolean dislike_stopFlash = false;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private UserInfo currentUser;

    private STATE _audioPlaying;
    private String language = BuildConfig.LANGUAGE_FEATURE_ID;


    private MediaPlayer playMediaInAccept(MediaPlayer mp, int file) {
        mp = MediaPlayer.create(this, playListAccept[file]);
        return mp;
    }

    private void releaseAndPlayAudioFile(int audioFile) {
        mpAll.release();
        mpAll = MediaPlayer.create(this, audioFile);
        mpAll.setOnCompletionListener(onCompletionListener);
        mpAll.seekTo(0);
        mpAll.start();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

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

        _audioPlaying = THIS_IS_ROBOTUTOR;

        SurfaceHolder holder = this.surfaceview.getHolder();// get holder
        holder.addCallback(this);    //add the callback interface to the holder
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        setLogoOnTouchListener();
        setCaptureOnClickListener();
        setLikeOnClickListener();
        setDislikeOnClickListener();

        mainHandler.postDelayed(splashRoboFingerSlideRunnable, DELAY_TO_SHOW_CHANGE_OF_FINGER);
        mainHandler.postDelayed(playVideoOfGoodTappingRunnable, DELAY_TO_SHOW_VIDEO_FULL_SCREEN);

    }

    private void initVarsOfViews() {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
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
                playListStart = mediaListStartEng;
                playListRecord = mediaListRecordEng;
                playListAccept = mediaListAcceptEng;
                playListAfterAccepting = mediaListAfterAcceptingEng;
                playListDecide = mediaListDecideEng;
                playListAfterDeciding = mediaListAfterDecidingEng;
                break;

            case LANG_SW:
                mpAll = MediaPlayer.create(this, R.raw.swa_thisisrobotutor);
                playListStart = mediaListStartSwa;
                playListRecord = mediaListRecordSwa;
                playListAccept = mediaListAcceptSwa;
                playListAfterAccepting = mediaListAfterAcceptingSwa;
                playListDecide = mediaListDecideSwa;
                playListAfterDeciding = mediaListAfterDecidingSwa;
                break;
        }

        setMeidaPlayerListeners();
    }

    private void setMeidaPlayerListeners() {

        onCompletionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                switch (_audioPlaying) {

                    // formerly mpIntro
                    case THIS_IS_ROBOTUTOR:
                        toSTART();
                        break;

                    // formerly mpStart1
                    case IF_YOU_SEE_YOUR_PICTURE:

                        if (capture.getVisibility() == View.VISIBLE) {
                            _audioPlaying = PLEASE_TAP_ON_YOUR_PICTURE;
                            releaseAndPlayAudioFile(playListStart[1]);
                        }
                        break;

                    // formerly mpStart2
                    case PLEASE_TAP_ON_YOUR_PICTURE:

                        // formerly mpStart2
                        if (capture.getVisibility() == View.VISIBLE) {
                            if (mGalleryScrollView.exceedScreen(userInfo.size())) {
                                _audioPlaying = TO_SEE_MORE_PICTURES;
                                releaseAndPlayAudioFile(playListStart[2]);
                            } else {
                                _audioPlaying = IF_YOU_DONT_FIND_YOUR_PICTURE;
                                releaseAndPlayAudioFile(playListStart[4]);
                            }
                        }
                        break;

                    // formerly mpStart3
                    // XXX this is where RoboFinger is called!
                    case TO_SEE_MORE_PICTURES:
                        if (capture.getVisibility() == View.VISIBLE) {
                            _audioPlaying = SLIDE_THEM_LIKE_THIS;
                            releaseAndPlayAudioFile(playListStart[3]);
                            slideRoboFinger.setVisibility(View.VISIBLE);
                            mainHandler.postDelayed(slideGalleryRunnable, DELAY_TO_SHOW_CHANGE_OF_FINGER);
                        }
                        break;

                    // formerly mpStart4
                    case SLIDE_THEM_LIKE_THIS:
                        // do nothing
                        break;

                    case PLEASE_SAY_YOUR_NAME:
                        //prompt "please say your name", then set timer to stop recording
                        Log.w("TIMING", "PLEASE SAY YOUR NAME " + System.currentTimeMillis());
                        thread.setTimerToStopRecording();
                        Log.w("TIMING", "PLEASE SAY YOUR NAME " + System.currentTimeMillis());
                        break;

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
                        break;

                    // formerly mpStart5
                    case IF_YOU_DONT_FIND_YOUR_PICTURE:
                        if (capture.getVisibility() == View.VISIBLE) {
                            _audioPlaying = TAP_HERE_RECORD;
                            releaseAndPlayAudioFile(playListStart[5]);
                            startFlash(FLASH_CAPTURE);
                        }
                        break;

                    // formerly mpStart6
                    case TAP_HERE_RECORD:
                        // do nothing
                        break;

                    case IF_YOU_LIKE_YOUR_PICTURE_AND_HOW_YOU_SAID_YOUR_NAME:
                        if (needConfirm) {
                            _audioPlaying = TAP_HERE_LIKE_PICTURE;
                            releaseAndPlayAudioFile(playListAccept[1]);
                            startFlash(FLASH_LIKE);
                        }
                        break;

                    case TAP_HERE_LIKE_PICTURE:
                        if (needConfirm) {
                            _audioPlaying = TAP_HERE_DISLIKE_PICTURE;
                            releaseAndPlayAudioFile(playListAccept[2]);
                            startFlash(FLASH_DISLIKE);
                        }
                        break;

                    case TAP_HERE_DISLIKE_PICTURE:
                        if (needConfirm) {
                            mHandler.postDelayed(toACCEPT, DELAY_TO_REPROMPT);
                        }
                        break;

                    case GOOD:
                        _audioPlaying = NOW;
                        releaseAndPlayAudioFile(playListAfterAccepting[1]);
                        break;

                    case NOW:
                        toSTART();
                        break;

                    case OKAY_LETS_TRY_AGAIN:
                        toSTART();
                        break;

                    case IF_THIS_IS_YOU:
                        if (like.getVisibility() == View.VISIBLE && dislike.getVisibility() == View.VISIBLE && !needConfirm) {
                            _audioPlaying = TAP_HERE_IF_IS_YOU;
                            releaseAndPlayAudioFile(playListDecide[1]);
                            startFlash(FLASH_LIKE);
                        }
                        break;

                    case TAP_HERE_IF_IS_YOU:
                        if (like.getVisibility() == View.VISIBLE && dislike.getVisibility() == View.VISIBLE && !needConfirm) {
                            _audioPlaying = IF_THIS_IS_NOT_YOU;
                            releaseAndPlayAudioFile(playListDecide[2]);
                        }
                        break;

                    case IF_THIS_IS_NOT_YOU:
                        if (like.getVisibility() == View.VISIBLE && dislike.getVisibility() == View.VISIBLE && !needConfirm) {
                            _audioPlaying = TAP_HERE_IF_IS_NOT_YOU;
                            releaseAndPlayAudioFile(playListDecide[3]);
                            startFlash(FLASH_DISLIKE);
                        }
                        break;

                    case TAP_HERE_IF_IS_NOT_YOU:
                        if (like.getVisibility() == View.VISIBLE && dislike.getVisibility() == View.VISIBLE && !needConfirm) {
                            mHandler.postDelayed(toDECIDE, DELAY_TO_REPROMPT);
                        }
                        break;

                    case LETS_GET_STARTED:
                        // reset firstAttempt
                        firstAttempt = true;
                        // when the Confirm button is tapped, launch RoboTutor
                        // TODO pass the unique student id
                        // TODO test more fervently
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(ROBOTUTOR_PACKAGE_ADDRESS);
                        Bundle sessionBundle = new Bundle();
                        Log.w("BUNDLE", currentUser.getUserIcon());
                        String uniqueUserID = generateUniqueIdFromFilename(currentUser.getUserIcon());
                        sessionBundle.putString(Common.STUDENT_ID_VAR, uniqueUserID);
                        sessionBundle.putString(Common.SESSION_ID_VAR, generateSessionID());
                        launchIntent.putExtras(sessionBundle);

                        if (launchIntent != null) {
                            startActivity(launchIntent);
                        } else {
                            Log.e("ACTIVITY", "New Activity failed to start!");
                        }
                        // System.exit(0);
                        break;

                    case LETS_TRY_AGAIN:
                        toSTART();
                        break;

                }

            }
        };
        mpAll.setOnCompletionListener(onCompletionListener);

    }

    /**
     * Generates a unique SessionID for RoboTutor
     *
     * @return
     */
    private String generateSessionID() {
        String deviceId = Build.SERIAL;
        String timestamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
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
        if (capture.getVisibility() == View.VISIBLE) {
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
        }
    }

    private Runnable toDECIDE = new Runnable() {
        @Override
        public void run() {
            if (like.getVisibility() == View.VISIBLE && dislike.getVisibility() == View.VISIBLE && !needConfirm) {
                _audioPlaying = IF_THIS_IS_YOU;
                releaseAndPlayAudioFile(playListDecide[0]);
            }
        }
    };

    private Runnable toACCEPT = new Runnable() {
        @Override
        public void run() {
            if (needConfirm) {
                _audioPlaying = IF_YOU_LIKE_YOUR_PICTURE_AND_HOW_YOU_SAID_YOUR_NAME;
                releaseAndPlayAudioFile(playListAccept[0]);
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
                //If tap on the picture while the camera is preparing to record, LOGIN will exit directly because of the exception.
                if (isPreparing) {
                    view.setBackgroundColor(Color.YELLOW);
                    return;
                }

                capture.setVisibility(View.GONE);
                like.setVisibility(View.INVISIBLE);
                dislike.setVisibility(View.INVISIBLE);

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
            Bitmap bmp = BitmapFactory.decodeFile(tempUrl);
            mDatas.add(bmp);
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
                        logo.setVisibility(View.INVISIBLE);
                        logoShadow.setVisibility(View.INVISIBLE);
                        splash.setVisibility(View.INVISIBLE);
                        splashRoboFinger.setVisibility(View.INVISIBLE);
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

    private void setCaptureOnClickListener(){
        capture.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        capture.setImageResource(R.drawable.capture_clicked);
                        break;
                    case MotionEvent.ACTION_UP:
                        isPreparing = true;
                        capture.setImageResource(R.drawable.capture);
                        pauseAllAudios();
                        capture.setVisibility(View.GONE);
                        stopFlash(FLASH_CAPTURE);
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
                        surfaceHolder = surfaceview.getHolder();
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

    private void setLikeOnClickListener(){
        like.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean  onTouch(View v, MotionEvent event) {
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
                        like.setVisibility(View.INVISIBLE);
                        dislike.setVisibility(View.INVISIBLE);
                        capture.setVisibility(View.VISIBLE);
                        coverSurface.setVisibility(View.VISIBLE);

                        pauseAllAudios();
                        if (needConfirm) {
                            if (firstAttempt) firstAttempt = false;
                            else deleteLastUserInfo();
                            saveUserInfo();
                            needConfirm = false;

                            _audioPlaying = GOOD;
                            releaseAndPlayAudioFile(playListAfterAccepting[0]);
                        } else {
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

    private void setDislikeOnClickListener(){
        dislike.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean  onTouch(View v, MotionEvent event) {
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
                        like.setVisibility(View.INVISIBLE);
                        dislike.setVisibility(View.INVISIBLE);
                        capture.setVisibility(View.VISIBLE);
                        coverSurface.setVisibility(View.VISIBLE);

                        pauseAllAudios();
                        if (needConfirm) {
                            needConfirm = false;
                            thread.deleteVideoAndPicture();
                            _audioPlaying = OKAY_LETS_TRY_AGAIN;
                            releaseAndPlayAudioFile(playListAfterAccepting[2]);
                        } else {
                            _audioPlaying = LETS_TRY_AGAIN;
                            releaseAndPlayAudioFile(playListAfterDeciding[1]);
                            mGalleryScrollView.clearAllBackground();
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
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

    private void saveUserInfo() {
        //save info and update UI Interface
        UserInfo curUser = new UserInfo();
        curUser.setID(accountsNumber);
        curUser.setUserIcon(thread.pPath);
        curUser.setUserVideo(thread.vPath);
        curUser.setRecordTime(thread.relativeStartTime + "");
        dbHelper.saveUserInfo(curUser);

        accountsNumber++;
        refreshGallery();
    }

    private void refreshGallery() {
        mDatas.clear();
        initUserInfo();
        mAdapter = new ScrollViewAdapter(this, mDatas);
        mGalleryScrollView.initDatas(mAdapter, needConfirm);
        mGalleryScrollView.emphasizeNewPicture();
    }

    /**
     * After taking a new video, refresh the gallery before confirming
     */
    private void refreshGalleryBeforeConfirm(){
        Bitmap bmp = BitmapFactory.decodeFile(thread.pPath);
        mDatas.clear();
        initUserInfo();
        mDatas.add(0, bmp);
        if(userInfo.size() == 0) mAdapter = new ScrollViewAdapter(this, mDatas);

        mAdapter.setMDatas(mDatas);
        mAdapter.accountNumber = mDatas.size();
        mGalleryScrollView.initDatas(mAdapter, needConfirm);
        //mGalleryScrollView.shrinkPicture(mAdapter, surfaceview.getWidth(), surfaceview.getHeight());
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case READY_TO_RECORD:
                    isPreparing = false;
                    coverSurface.setVisibility(View.INVISIBLE);
                    //prompt "please say your name"
                    _audioPlaying = PLEASE_SAY_YOUR_NAME;
                    releaseAndPlayAudioFile(playListRecord[0]);
                    break;
                case RECORD_DONE:
                    //record done, prompt "you said", then replay video
                    _audioPlaying = YOU_SAID;
                    releaseAndPlayAudioFile(playListRecord[1]);
                    break;
                case REPLAY_NEW_VIDEO_DONE:
                    //if you like how you said your name, please tap here
                    //add newly taken photo to the gallery though not in the database
                    //refreshGalleryBeforeConfirm();
                    like.setVisibility(View.VISIBLE);
                    dislike.setVisibility(View.VISIBLE);

                    mHandler.post(toACCEPT);
                    needConfirm = true;
                    break;
                case REFRESH_GALLERY:
                    refreshGallery();
                    break;
                case REPLAY_EXISTING_VIDEO_DONE:
                    //go to the menu
                    like.setVisibility(View.VISIBLE);
                    dislike.setVisibility(View.VISIBLE);

                    mHandler.post(toDECIDE);
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
                    break;
                case UNCOVER_SCREEN:
                    coverSurface.setVisibility(View.INVISIBLE);
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
     * Close system pop-ups
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus) {
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }

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
                like.setImageResource(R.drawable.like_finger);
                mainHandler.post(flashLikeRunnable);
                break;

            case FLASH_DISLIKE:
                dislike_startFlashTIme = System.nanoTime();
                dislike_lastFlashTime = System.nanoTime();
                dislike_stopFlash = false;
                dislike_nowClicked = false;
                dislike.setImageResource(R.drawable.dislike_finger);
                mainHandler.post(flashDislikeRunnable);
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
                mainHandler.postDelayed(playVideoOfGoodTappingRunnable, DELAY_TO_SHOW_VIDEO);
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
        Log.i("RecordDemoActivity", "onResume()");
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.i("RecordDemoActivity", "onPause()");
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        pauseAllAudios();
        Log.i("RecordDemoActivity", "onDestroy()");

        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        if (launchIntent != null) {
            startActivity(launchIntent);
        }

    }

    private void pauseAllAudios() {
        if (mpAll.isPlaying()) mpAll.pause();
    }

}
