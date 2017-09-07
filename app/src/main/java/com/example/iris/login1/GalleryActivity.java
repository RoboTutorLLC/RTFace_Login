package com.example.iris.login1;

import android.animation.Animator;
import android.animation.ObjectAnimator;
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

    //mpIntro: This is roboTutor
    private MediaPlayer mpIntro;
    //mpStart1 & mpStart2: If you see your picture, please tap on your picture
    //mpStart3 & mpStart4: To see more pictures, slide them like this.
    //mpStart5 & mpStart6: If you don't find your picture, tap here.
    private MediaPlayer mpStart1, mpStart2, mpStart3, mpStart4, mpStart5, mpStart6;
    //mpRecord1: Please say your name.
    //mpRecord2: You said
    private MediaPlayer mpRecord1, mpRecord2;
    //mpAccept1 & mpAccept2 & mpAccept3: If you like your picture and how you said your name, please tap here, otherwise tap here.
    private MediaPlayer mpAccept1, mpAccept2, mpAccept3;
    //mpAfterAccepting1 & mpAfterAccepting2: Good! Now...
    //mpAfterAccepting3: OK, let's try again.
    private MediaPlayer mpAfterAccepting1, mpAfterAccepting2, mpAfterAccepting3;
    //mpDecide1 & mpDecide2: If this is you, please tap here.
    //mpDecide3 & mpDecide4: If this is not you, please tap here.
    private MediaPlayer mpDecide1, mpDecide2, mpDecide3, mpDecide4;
    //mpAfterDeciding1: Let's get started.
    //mpAfterDeciding2: Let's try again.
    private MediaPlayer mpAfterDeciding1, mpAfterDeciding2;

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

    private String languge = Common.LANG_EN;

    private MediaPlayer playMediaInAccept(MediaPlayer mp, int file) {
        mp = MediaPlayer.create(this, playListAccept[file]);
        return mp;
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

        SurfaceHolder holder = this.surfaceview.getHolder();// get holder
        holder.addCallback(this);    //add the callback interface to the holder
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        setLogoOnClickListener();
        setCaptureOnClickListener();
        setLikeOnClickListener();
        setDislikeOnClickListener();

        mainHandler.postDelayed(playVideoOfGoodTapping, Common.DELAY_TO_SHOW_VIDEO_FULL_SCREEN);
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
            case Common.LANG_EN:
                mpIntro = MediaPlayer.create(this, R.raw.eng_thisisrobotutor);
                playListStart = mediaListStartEng;
                playListRecord = mediaListRecordEng;
                playListAccept = mediaListAcceptEng;
                playListAfterAccepting = mediaListAfterAcceptingEng;
                playListDecide = mediaListDecideEng;
                playListAfterDeciding = mediaListAfterDecidingEng;
                break;

            case Common.LANG_SW:
                mpIntro = MediaPlayer.create(this, R.raw.swa_thisisrobotutor);
                playListStart = mediaListStartSwa;
                playListRecord = mediaListRecordSwa;
                playListAccept = mediaListAcceptSwa;
                playListAfterAccepting = mediaListAfterAcceptingSwa;
                playListDecide = mediaListDecideSwa;
                playListAfterDeciding = mediaListAfterDecidingSwa;
                break;
        }

        mpStart1 = MediaPlayer.create(this,playListStart[0]);
        mpStart2 = MediaPlayer.create(this,playListStart[1]);
        mpStart3 = MediaPlayer.create(this,playListStart[2]);
        mpStart4 = MediaPlayer.create(this,playListStart[3]);
        mpStart5 = MediaPlayer.create(this,playListStart[4]);
        mpStart6 = MediaPlayer.create(this,playListStart[5]);

        mpRecord1 = MediaPlayer.create(this,playListRecord[0]);
        mpRecord2 = MediaPlayer.create(this,playListRecord[1]);

        mpAccept1 = MediaPlayer.create(this,playListAccept[0]);
        mpAccept2 = MediaPlayer.create(this,playListAccept[1]);
        mpAccept3 = MediaPlayer.create(this,playListAccept[2]);

        mpAfterAccepting1 = MediaPlayer.create(this,playListAfterAccepting[0]);
        mpAfterAccepting2 = MediaPlayer.create(this,playListAfterAccepting[1]);
        mpAfterAccepting3 = MediaPlayer.create(this,playListAfterAccepting[2]);

        mpDecide1 = MediaPlayer.create(this,playListDecide[0]);
        mpDecide2 = MediaPlayer.create(this,playListDecide[1]);
        mpDecide3 = MediaPlayer.create(this,playListDecide[2]);
        mpDecide4 = MediaPlayer.create(this,playListDecide[3]);

        mpAfterDeciding1 = MediaPlayer.create(this,playListAfterDeciding[0]);
        mpAfterDeciding2 = MediaPlayer.create(this,playListAfterDeciding[1]);

        setMeidaPlayerListeners();
    }

    private void setMeidaPlayerListeners() {

        mpIntro.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                toSTART();
            }
        });

        mpStart1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (capture.getVisibility() == View.VISIBLE) {
                    mpStart2.seekTo(0);
                    mpStart2.start();
                }
            }
        });

        mpStart2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (capture.getVisibility() == View.VISIBLE) {
                    if (mGalleryScrollView.exceedScreen(userInfo.size())) {
                        mpStart3.seekTo(0);
                        mpStart3.start();
                    } else {
                        mpStart5.seekTo(0);
                        mpStart5.start();
                    }
                }
            }
        });

        mpStart3.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (capture.getVisibility() == View.VISIBLE) {
                    mpStart4.seekTo(0);
                    mpStart4.start();

                    slideFinger.setVisibility(View.VISIBLE);
                    mainHandler.postDelayed(slideGallery, Common.DELAY_TO_SHOW_CHANGE_OF_FINGER);
                }
            }
        });

        mpStart5.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (capture.getVisibility() == View.VISIBLE) {
                    mpStart6.seekTo(0);
                    mpStart6.start();
                    startFlash(Common.FLASH_CAPTURE);
                }
            }
        });

        mpRecord1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //prompt "please say your name", then set timer to stop recording
                thread.setTimerToStopRecording();
            }
        });

        mpRecord2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //prompt "you said", then replay the newly taken video
                thread.newReplay(thread.vPath);
            }
        });

        mpAccept1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (needConfirm) {
                    mpAccept2.seekTo(0);
                    mpAccept2.start();
                    startFlash(Common.FLASH_LIKE);
                }
            }
        });

        mpAccept2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (needConfirm) {
                    mpAccept3.seekTo(0);
                    mpAccept3.start();
                    startFlash(Common.FLASH_DISLIKE);
                }
            }
        });

        mpAccept3.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (needConfirm) {
                    mHandler.postDelayed(toACCEPT, Common.DELAY_TO_REPROMPT);
                }
            }
        });

        mpAfterAccepting1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mpAfterAccepting2.seekTo(0);
                mpAfterAccepting2.start();
            }
        });

        mpAfterAccepting2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                toSTART();
            }
        });

        mpAfterAccepting3.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                toSTART();
            }
        });

        mpDecide1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (like.getVisibility() == View.VISIBLE && dislike.getVisibility() == View.VISIBLE && !needConfirm) {
                    mpDecide2.seekTo(0);
                    mpDecide2.start();
                    startFlash(Common.FLASH_LIKE);
                }
            }
        });

        mpDecide2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (like.getVisibility() == View.VISIBLE && dislike.getVisibility() == View.VISIBLE && !needConfirm) {
                    mpDecide3.seekTo(0);
                    mpDecide3.start();
                }
            }
        });

        mpDecide3.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (like.getVisibility() == View.VISIBLE && dislike.getVisibility() == View.VISIBLE && !needConfirm) {
                    mpDecide4.seekTo(0);
                    mpDecide4.start();
                    startFlash(Common.FLASH_DISLIKE);
                }
            }
        });

        mpDecide4.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (like.getVisibility() == View.VISIBLE && dislike.getVisibility() == View.VISIBLE && !needConfirm) {
                    mHandler.postDelayed(toDECIDE, Common.DELAY_TO_REPROMPT);
                }
            }
        });

        mpAfterDeciding1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                System.exit(0);
            }
        });
        mpAfterDeciding2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                toSTART();
            }
        });
    }

    private void toSTART() {
        if (capture.getVisibility() == View.VISIBLE) {
            if (userInfo.size() > 0) {
                //If you see your picture, please tap on it, otherwise tap here.
                mpStart1.seekTo(0);
                mpStart1.start();
            } else {
                //Please tap here
                mpStart6.seekTo(0);
                mpStart6.start();
                startFlash(Common.FLASH_CAPTURE);
            }
        }
    }

    private Runnable toDECIDE = new Runnable() {
        @Override
        public void run() {
            if (like.getVisibility() == View.VISIBLE && dislike.getVisibility() == View.VISIBLE && !needConfirm) {
                mpDecide1.seekTo(0);
                mpDecide1.start();
            }
        }
    };

    private Runnable toACCEPT = new Runnable() {
        @Override
        public void run() {
            if (needConfirm) {
                mpAccept1.seekTo(0);
                mpAccept1.start();
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
                    stopFlash(Common.FLASH_LIKE);
                    stopFlash(Common.FLASH_DISLIKE);
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
                mpIntro.start();
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
                        stopFlash(Common.FLASH_CAPTURE);
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
                        thread = new RecordThread(Common.RECORD_TIME, surfaceview, surfaceHolder, accountsNumber, dbHelper, mDatas, mHandler);
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
                        stopFlash(Common.FLASH_LIKE);
                        stopFlash(Common.FLASH_DISLIKE);
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

                            mpAfterAccepting1.seekTo(0);
                            mpAfterAccepting1.start();
                        } else {
                            mpAfterDeciding1.seekTo(0);
                            mpAfterDeciding1.start();
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
                        stopFlash(Common.FLASH_LIKE);
                        stopFlash(Common.FLASH_DISLIKE);
                        like.setVisibility(View.INVISIBLE);
                        dislike.setVisibility(View.INVISIBLE);
                        capture.setVisibility(View.VISIBLE);
                        coverSurface.setVisibility(View.VISIBLE);

                        pauseAllAudios();
                        if (needConfirm) {
                            needConfirm = false;
                            thread.deleteVideoAndPicture();
                            mpAfterAccepting3.seekTo(0);
                            mpAfterAccepting3.start();
                        } else {
                            mpAfterDeciding2.seekTo(0);
                            mpAfterDeciding2.start();
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
                case Common.READY_TO_RECORD:
                    isPreparing = false;
                    coverSurface.setVisibility(View.INVISIBLE);
                    //prompt "please say your name"
                    mpRecord1.seekTo(0);
                    mpRecord1.start();
                    break;
                case Common.RECORD_DONE:
                    //record done, prompt "you said", then replay video
                    mpRecord2.seekTo(0);
                    mpRecord2.start();
                    break;
                case Common.REPLAY_NEW_VIDEO_DONE:
                    //if you like how you said your name, please tap here
                    //add newly taken photo to the gallery though not in the database
                    //refreshGalleryBeforeConfirm();
                    like.setVisibility(View.VISIBLE);
                    dislike.setVisibility(View.VISIBLE);

                    mHandler.post(toACCEPT);
                    needConfirm = true;
                    break;
                case Common.REFRESH_GALLERY:
                    refreshGallery();
                    break;
                case Common.REPLAY_OLD_VIDEO_DONE:
                    //go to the menu
                    like.setVisibility(View.VISIBLE);
                    dislike.setVisibility(View.VISIBLE);

                    mHandler.post(toDECIDE);
                    break;
                case Common.PLAY_TAPPING_VIDEO_DONE:
                    if (surfaceviewFullScreen.getVisibility() == View.VISIBLE) {
                        logo.setVisibility(View.VISIBLE);
                        surfaceviewFullScreen.setVisibility(View.INVISIBLE);
                    } else
                        coverSurface.setVisibility(View.VISIBLE);
                    break;
                case Common.UNCOVER_SCREEN:
                    coverSurface.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    private void startFlash(int viewToFlash) {
        switch (viewToFlash) {
            case Common.FLASH_CAPTURE:
                capture_startFlashTIme = System.nanoTime();
                capture_lastFlashTime = System.nanoTime();
                capture_stopFlash = false;
                capture_nowClicked = false;
                mainHandler.post(flashCapture);
                break;

            case Common.FLASH_LIKE:
                like_startFlashTIme = System.nanoTime();
                like_lastFlashTime = System.nanoTime();
                like_stopFlash = false;
                like_nowClicked = false;
                like.setImageResource(R.drawable.like_finger);
                mainHandler.post(flashLike);
                break;

            case Common.FLASH_DISLIKE:
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
            case Common.FLASH_CAPTURE:
                capture_startFlashTIme = System.nanoTime();
                capture_lastFlashTime = System.nanoTime();
                capture_stopFlash = true;
                break;

            case Common.FLASH_LIKE:
                like_startFlashTIme = System.nanoTime();
                like_lastFlashTime = System.nanoTime();
                like_stopFlash = true;
                break;

            case Common.FLASH_DISLIKE:
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
            if(timepass >  Common.FLASH_FREQUENCE) {
                if(!capture_nowClicked) {
                    capture.setImageResource(R.drawable.capture_finger_clicked);
                    capture_nowClicked = true;
                } else {
                    capture.setImageResource(R.drawable.capture_finger);
                    capture_nowClicked = false;
                }
                capture_lastFlashTime = System.nanoTime();
            }

            if ((System.nanoTime() - capture_startFlashTIme)/1000000 <= Common.FLASH_DURATION)
                mainHandler.postDelayed(flashCapture, Common.FLASH_FREQUENCE);
            else {
                capture.setImageResource(R.drawable.capture);
                capture_nowClicked = false;

                //if user hesitate, play the video of good tapping
                mainHandler.postDelayed(playVideoOfGoodTapping, Common.DELAY_TO_SHOW_VIDEO);
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
            if(timepass > Common.FLASH_FREQUENCE) {
                if(!like_nowClicked) {
                    like.setImageResource(R.drawable.like_finger_clicked);
                    like_nowClicked = true;
                } else {
                    like_nowClicked = false;
                    like.setImageResource(R.drawable.like_finger);
                }
                like_lastFlashTime = System.nanoTime();
            }

            if ((System.nanoTime() - like_startFlashTIme)/1000000 <= Common.FLASH_DURATION)
                mainHandler.postDelayed(flashLike, Common.FLASH_FREQUENCE);
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
            if(timepass > Common.FLASH_FREQUENCE) {
                if(!dislike_nowClicked) {
                    dislike.setImageResource(R.drawable.dislike_finger_clicked);
                    dislike_nowClicked = true;
                } else {
                    dislike.setImageResource(R.drawable.dislike_finger);
                    dislike_nowClicked = false;
                }
                dislike_lastFlashTime = System.nanoTime();
            }

            if ((System.nanoTime() - dislike_startFlashTIme)/1000000 <= Common.FLASH_DURATION)
                mainHandler.postDelayed(flashDislike, Common.FLASH_FREQUENCE);
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
                    mainHandler.postDelayed(hideSlideFinger, Common.DELAY_TO_SHOW_CHANGE_OF_FINGER);
                    mainHandler.postDelayed(promptIfNotFindPicture, Common.DELAY_AFTER_SHOWING_SLIDE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    slideFinger.setImageResource(R.drawable.before_slide);
                    mainHandler.postDelayed(hideSlideFinger, Common.DELAY_TO_SHOW_CHANGE_OF_FINGER);
                    mainHandler.postDelayed(promptIfNotFindPicture, Common.DELAY_AFTER_SHOWING_SLIDE);
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
                mpStart5.seekTo(0);
                mpStart5.start();
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
    }

    private void pauseAllAudios() {
        if (mpIntro.isPlaying()) mpIntro.pause();
        if (mpStart1.isPlaying()) mpStart1.pause();
        if (mpStart2.isPlaying()) mpStart2.pause();
        if (mpStart3.isPlaying()) mpStart3.pause();
        if (mpStart4.isPlaying()) mpStart4.pause();
        if (mpStart5.isPlaying()) mpStart5.pause();
        if (mpStart6.isPlaying()) mpStart6.pause();
        if (mpRecord1.isPlaying()) mpRecord1.pause();
        if (mpRecord2.isPlaying()) mpRecord2.pause();
        if (mpAccept1.isPlaying()) mpAccept1.pause();
        if (mpAccept2.isPlaying()) mpAccept2.pause();
        if (mpAccept3.isPlaying()) mpAccept3.pause();
        if (mpAfterAccepting1.isPlaying()) mpAfterAccepting1.pause();
        if (mpAfterAccepting2.isPlaying()) mpAfterAccepting2.pause();
        if (mpAfterAccepting3.isPlaying()) mpAfterAccepting3.pause();
        if (mpDecide1.isPlaying()) mpDecide1.pause();
        if (mpDecide2.isPlaying()) mpDecide2.pause();
        if (mpDecide3.isPlaying()) mpDecide3.pause();
        if (mpDecide4.isPlaying()) mpDecide4.pause();
        if (mpAfterDeciding1.isPlaying()) mpAfterDeciding1.pause();
        if (mpAfterDeciding2.isPlaying()) mpAfterDeciding2.pause();
    }

}
