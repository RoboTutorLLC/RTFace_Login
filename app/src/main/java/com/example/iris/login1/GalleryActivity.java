package com.example.iris.login1;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.iris.login1.Common.*;
import static com.example.iris.login1.Common.STATE.*;

public class GalleryActivity extends AppCompatActivity implements SurfaceHolder.Callback {

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

    private int[] playListStart;
    private int[] playListRecord;
    private int[] playListAccept;
    private int[] playListAfterAccepting;
    private int[] playListDecide;
    private int[] playListAfterDeciding;

    private MediaPlayer mpAll;
    private MediaPlayer.OnCompletionListener onCompletionListener;

    private SurfaceView surfaceviewFullScreen;
    private ImageButton logo;
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
    private ImageView slideFinger;
    private int accountsNumber;
    private playVideo pv;
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

    private String languge = LANG_EN;
    private STATE _audioPlaying;

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

        setLogoOnClickListener();
        setCaptureOnClickListener();
        setLikeOnClickListener();
        setDislikeOnClickListener();

        mainHandler.postDelayed(playVideoOfGoodTapping, DELAY_TO_SHOW_VIDEO_FULL_SCREEN);
    }

    private void initVarsOfViews() {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_gallery);

        surfaceviewFullScreen = (SurfaceView) findViewById(R.id.show_full_screen);
        logo = (ImageButton) findViewById(R.id.logo);

        surfaceview = (SurfaceView) findViewById(R.id.id_content);
        coverSurface = (ImageView) findViewById(R.id.cover_surface);
        capture = (ImageView) findViewById(R.id.capture);
        like = (ImageView) findViewById(R.id.like);
        dislike = (ImageView) findViewById(R.id.dislike);
        slideFinger = (ImageView)findViewById(R.id.slide_finger);

        mGalleryScrollView = (MyScrollView) findViewById(R.id.id_scrollView);
    };

    private void initVarsOfMediaPlayer() {
        //choose audio according to language version
        switch(languge) {
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
                    case TO_SEE_MORE_PICTURES:
                        if (capture.getVisibility() == View.VISIBLE) {

                            _audioPlaying = SLIDE_THEM_LIKE_THIS;
                            releaseAndPlayAudioFile(playListStart[3]);

                            slideFinger.setVisibility(View.VISIBLE);
                            mainHandler.postDelayed(slideGallery, DELAY_TO_SHOW_CHANGE_OF_FINGER);
                        }
                        break;

                    // formerly mpStart4
                    case SLIDE_THEM_LIKE_THIS:
                        // do nothing
                        break;

                    // formerly mpStart5
                    case IF_YOU_DONT_FIND_YOUR_PICTURE:
                        if (capture.getVisibility() == View.VISIBLE) {
                            _audioPlaying = TAP_HERE_RECORD; // XXX is there a difference between this and another tap_here_record??
                            releaseAndPlayAudioFile(playListStart[5]);
                            startFlash(FLASH_CAPTURE);
                        }
                        break;

                    // formerly mpStart6
                    case TAP_HERE_RECORD:
                        // do nothing
                        break;

                    case PLEASE_SAY_YOUR_NAME:
                        //prompt "please say your name", then set timer to stop recording
                        thread.setTimerToStopRecording();
                        break;

                    case YOU_SAID:
                        //prompt "you said", then replay the newly taken video
                        thread.newReplay(thread.vPath);
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
                        // when the Confirm button is tapped, launch RoboTutor
                        // TODO pass the unique student id
                        // TODO test more fervently
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(ROBOTUTOR_PACKAGE_ADDRESS);
                        if (launchIntent != null) {
                            startActivity(launchIntent);
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

                v = userInfo.get(position).getUserVideo();
                p = userInfo.get(position).getUserIcon();
                realStartTime = Integer.parseInt(userInfo.get(position).getRecordTime());
                pauseAllAudios();

                //if it is recoding or replaying, now stop it
                if (thread != null) {
                    thread.stopRecord();
                    thread.stopPlayingVideo();
                    thread.interrupt();
                }

                //play clicked video
                if (pv != null) pv.stopPlayingVideo();
                pv = new playVideo(realStartTime, surfaceview, surfaceHolder, v, p, mHandler, GalleryActivity.this, false);
                pv.start();
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

    private void setLogoOnClickListener() {
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logo.setVisibility(View.INVISIBLE);
                mpAll.start();
            }
        });
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
                        if (pv != null) {
                            pv.stopPlayingVideo();
                            pv.interrupt();
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
                        if (pv != null) pv.stopPlayingVideo();
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
                        if (pv != null) pv.stopPlayingVideo();
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
        curUser.setRecordTime(thread.realStartTime + "");
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
                case REPLAY_OLD_VIDEO_DONE:
                    //go to the menu
                    like.setVisibility(View.VISIBLE);
                    dislike.setVisibility(View.VISIBLE);

                    mHandler.post(toDECIDE);
                    break;
                case PLAY_TAPPING_VIDEO_DONE:
                    if (surfaceviewFullScreen.getVisibility() == View.VISIBLE) {
                        logo.setVisibility(View.VISIBLE);
                        surfaceviewFullScreen.setVisibility(View.INVISIBLE);
                    } else
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

    private void startFlash(int viewToFlash) {
        switch (viewToFlash) {
            case FLASH_CAPTURE:
                capture_startFlashTIme = System.nanoTime();
                capture_lastFlashTime = System.nanoTime();
                capture_stopFlash = false;
                capture_nowClicked = false;
                mainHandler.post(flashCapture);
                break;

            case FLASH_LIKE:
                like_startFlashTIme = System.nanoTime();
                like_lastFlashTime = System.nanoTime();
                like_stopFlash = false;
                like_nowClicked = false;
                like.setImageResource(R.drawable.like_finger);
                mainHandler.post(flashLike);
                break;

            case FLASH_DISLIKE:
                dislike_startFlashTIme = System.nanoTime();
                dislike_lastFlashTime = System.nanoTime();
                dislike_stopFlash = false;
                dislike_nowClicked = false;
                dislike.setImageResource(R.drawable.dislike_finger);
                mainHandler.post(flashDislike);
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

    //change capture button's color from white to red consecutively 3 times.
    private Runnable flashCapture = new Runnable() {
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
                mainHandler.postDelayed(flashCapture, FLASH_FREQUENCE);
            else {
                capture.setImageResource(R.drawable.capture);
                capture_nowClicked = false;

                //if user hesitate, play the video of good tapping
                mainHandler.postDelayed(playVideoOfGoodTapping, DELAY_TO_SHOW_VIDEO);
            }
        }
    };

    private Runnable playVideoOfGoodTapping = new Runnable() {
        @Override
        public void run() {
            if (logo.getVisibility() == View.VISIBLE) {
                surfaceviewFullScreen.setVisibility(View.VISIBLE);
                logo.setVisibility(View.INVISIBLE);
                pv = new playVideo(0, surfaceviewFullScreen, surfaceviewFullScreen.getHolder(), "", "", mHandler, GalleryActivity.this, true);
                pv.start();
            } else if (readyToStartTimer){
                pv = new playVideo(0, surfaceview, surfaceHolder, "", "", mHandler, GalleryActivity.this, true);
                if (capture.getVisibility() == View.VISIBLE) pv.start();
            }
            readyToStartTimer = true;
        }
    };

    //change Like button's color from white to red consecutively 3 times.
    private Runnable flashLike = new Runnable() {
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
                mainHandler.postDelayed(flashLike, FLASH_FREQUENCE);
            else {
                like.setImageResource(R.drawable.like);
                like_nowClicked = false;
            }
        }
    };

    //change Dislike button's color from white to red consecutively 3 times.
    private Runnable flashDislike = new Runnable() {
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
                mainHandler.postDelayed(flashDislike, FLASH_FREQUENCE);
            else {
                dislike.setImageResource(R.drawable.dislike);
                dislike_nowClicked = false;
            }
        }
    };

    private Runnable slideGallery = new Runnable() {
        @Override
        public void run() {
            slideFinger.setImageResource(R.drawable.when_slide);
            int dy = mGalleryScrollView.mScreenHeight / 2;
            ObjectAnimator anim = ObjectAnimator.ofFloat(slideFinger, "translationY", 0, -dy, 0);
            anim.setDuration(2000);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    slideFinger.setImageResource(R.drawable.before_slide);
                    mainHandler.postDelayed(hideSlideFinger, DELAY_TO_SHOW_CHANGE_OF_FINGER);
                    mainHandler.postDelayed(promptIfNotFindPicture, DELAY_AFTER_SHOWING_SLIDE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    slideFinger.setImageResource(R.drawable.before_slide);
                    mainHandler.postDelayed(hideSlideFinger, DELAY_TO_SHOW_CHANGE_OF_FINGER);
                    mainHandler.postDelayed(promptIfNotFindPicture, DELAY_AFTER_SHOWING_SLIDE);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

            anim.start();
        }
    };

    private Runnable promptIfNotFindPicture = new Runnable() {
        @Override
        public void run() {
            if (capture.getVisibility() == View.VISIBLE) {

                _audioPlaying = IF_YOU_DONT_FIND_YOUR_PICTURE;
                releaseAndPlayAudioFile(playListStart[4]);
            }
        }
    };

    private Runnable hideSlideFinger = new Runnable() {
        @Override
        public void run() {
            slideFinger.setVisibility(View.INVISIBLE);
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

        // TODO TEST will this pop up in front of RoboTutor?
        // TODO TEST more fervently
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        if (launchIntent != null) {
            startActivity(launchIntent);
        }
    }

    private void pauseAllAudios() {
        if (mpAll.isPlaying()) mpAll.pause();
    }

}
