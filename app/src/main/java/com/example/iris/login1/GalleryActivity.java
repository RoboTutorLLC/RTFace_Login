package com.example.iris.login1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.util.Pools;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RunnableFuture;

public class GalleryActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    private SoundPool soundPool;
    private int pleaseSayYourName;
    private int[] medialist1={R.raw.ifyoulikeyourpicture,R.raw.pleasetaphere,R.raw.otherwisetaphere};
    private int[] medialist1_eng={R.raw.eng_ifyoulikeyourpicture,R.raw.eng_pleasetaphere,R.raw.eng_otherwisetaphere};
    private int[] medialist2={R.raw.ifyouseeyourpicture,R.raw.pleasesayyourname};
    private int[] medialist2_eng={R.raw.eng_ifyouseeyourpicturepleasetaponit,R.raw.eng_pleasesayyourname};
    private int[] playlist1;
    private int[] playlist2;
    private MediaPlayer mp1,mp2,mp3;
    private int index1=1;
    private int index2=0;

    private MyScrollView mHorizontalScrollView;
    private ScrollViewAdapter mAdapter;
    private SurfaceView surfaceview;
    private SurfaceHolder surfaceHolder;
    private RecordThread thread;
    private List<Bitmap> mDatas =  new ArrayList<Bitmap>();     //list of user's icon
    private DataHelper dbHelper;
    private List<UserInfo> userInfo = new ArrayList<UserInfo>();
    private ImageView capture;
    private int accountsNumber;
    private playVideo pv;
    private int chosenId;      //chosen position
    private boolean needConfirm=false;

    private long lastFlashTime = System.nanoTime();
    private long button_lastFlashTime = System.nanoTime();
    private long startFlashTime = System.nanoTime();
    private long button_startFlashTIme = System.nanoTime();
    private boolean nowRed = false;
    private boolean button_nowRed = false;
    private RelativeLayout screenMargin;
    private RelativeLayout buttonMargin;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private boolean englishVersion = true;

    private void initUserInfo(){
        dbHelper = new DataHelper(this);
        userInfo = dbHelper.getUserList();
        for (int i = 0; i != userInfo.size(); i++) {
            String tempUrl = userInfo.get(i).getUserIcon();
            System.out.println("user "+userInfo.get(i).getID()+" "+userInfo.get(i).getUserIcon());
            Bitmap bmp = BitmapFactory.decodeFile(tempUrl);
            mDatas.add(bmp);
        }
        mAdapter.accountNumber = userInfo.size();
        accountsNumber=userInfo.size();
        System.out.println("userInfo"+userInfo.size());
    }

    private void playFile1(int file) {
        mp1=MediaPlayer.create(this,playlist1[file]);
    }

    private void playFile2(int file) {
        mp2=MediaPlayer.create(this,playlist2[file]);
    }

    private void playFile3(){
        if(!englishVersion)
            mp2=MediaPlayer.create(this,R.raw.pleasesayyourname);
        else
            mp2=MediaPlayer.create(this,R.raw.eng_pleasetaphereandsayyourname);
        button_startFlashTIme = System.nanoTime();
        button_lastFlashTime = System.nanoTime();
        mainHandler.post(flashButton);
        nowRed = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        englishVersion = true;
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gallery);
        surfaceview = (SurfaceView) findViewById(R.id.id_content);
        capture=(ImageView) findViewById(R.id.capture);
        screenMargin = (RelativeLayout) findViewById(R.id.margin);
        buttonMargin = (RelativeLayout) findViewById(R.id.button_margin);
        mHorizontalScrollView = (MyScrollView) findViewById(R.id.id_horizontalScrollView);

        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        if(englishVersion==false) {     //not English version
            pleaseSayYourName = soundPool.load(this, R.raw.pleasesayyourname, 1);
            mp2=MediaPlayer.create(this,R.raw.thisisrobotutor);
            mp3=MediaPlayer.create(this,R.raw.yousaid);
            playlist1 = medialist1;
            playlist2 = medialist2;
        }
        else{
            pleaseSayYourName = soundPool.load(this, R.raw.eng_pleasesayyourname, 1);
            mp2=MediaPlayer.create(this,R.raw.eng_thisisrobotutor);
            mp3=MediaPlayer.create(this,R.raw.eng_yousaid);
            playlist1 = medialist1_eng;
            playlist2 = medialist2_eng;
        }
        mp3.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {    //replay the newly taken video
                thread.newReplay(thread.vPath);
            }
        });
        mp1=MediaPlayer.create(this,playlist1[0]);
        mp1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(index1<playlist1.length){
                    mp1.release();
                    playFile1(index1);
                    mp1.start();
                    index1++;
                }
            }
        });
        mp2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(userInfo.size()!=0&&index2==0){
                    mp2.release();
                    playFile2(index2);
                    mp2.start();
                    index2++;
                    System.out.println("index2: "+index2);
                }
                else if(userInfo.size()==0||index2==playlist2.length-1){
                    mp2.release();
                    playFile3();
                    mp2.start();
                }
            }
        });

        //添加滚动回调
        mHorizontalScrollView.setCurrentImageChangeListener(new MyScrollView.CurrentImageChangeListener() {
            @Override
            public void onCurrentImgChanged(int position, View viewIndicator) {
                //mImg.setImageBitmap(mDatas.get(position));
                //play the video
                viewIndicator.setBackgroundColor(Color.RED);
            }
        });
        //添加点击回调
        mHorizontalScrollView.setOnItemClickListener(new MyScrollView.OnItemClickListener() {

            @Override
            public void onClick(View view, int position) {
                //mImg.setImageBitmap(mDatas.get(position));
                capture.setVisibility(View.GONE);     //dismiss the capure button when record
                if(needConfirm) {
                    if(position!=0)
                        needConfirm = false;
                    System.out.println("click scroll position: " + position);
                    String v,p;
                    if(position!=0) {
                         v = userInfo.get(position - 1).getUserVideo();
                         p = userInfo.get(position - 1).getUserIcon();
                    }
                    else{
                        v=thread.vPath;
                        p=thread.pPath;
                    }
                    if (thread != null) {     //if it is recoding or replaying, now stop it
                        System.out.println("stop the recoding processing");
                        thread.stopRecord();
                        thread.videostop();
                        thread.interrupt();
                    }
                    chosenId = position-1;
                        //System.out.println("dbhelper"+s);
                    if (pv != null) {
                        pv.videostop();
                    }
                    pv = new playVideo(surfaceview, surfaceHolder, v, p, mHandler);
                    pv.start();
                    if(position!=0)
                        mHandler.sendEmptyMessage(3);    //refresh the gallery
                    view.setBackgroundColor(Color.RED);

                }
                else {
                    if (thread != null) {     //if it is recoding or replaying, now stop it
                        System.out.println("stop the recoding processing");
                        thread.stopRecord();
                        thread.videostop();
                        thread.interrupt();
                    }
                    System.out.println("click scroll position: " + position);
                    String v = userInfo.get(position).getUserVideo();
                    String p = userInfo.get(position).getUserIcon();
                    chosenId = position;
                    //System.out.println("dbhelper"+s);
                    if (pv != null) {
                        pv.videostop();
                    }
                    pv = new playVideo(surfaceview, surfaceHolder, v, p, mHandler);
                    pv.start();
                    view.setBackgroundColor(Color.RED);
                }
            }
        });

        initUserInfo();
        if(userInfo.size()!=0) {
            mAdapter = new ScrollViewAdapter(this, mDatas);
            mHorizontalScrollView.initDatas(mAdapter);
        }
        mp2.start();

        SurfaceHolder holder = this.surfaceview.getHolder();// 取得holder
        holder.addCallback(this);    //holder加入回调接口
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);   // 设置setType
        capture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                capture.setVisibility(View.GONE);
                if(pv!=null){
                    pv.videostop();
                    pv.interrupt();
                }
                if (thread==null) {
                    if(needConfirm){
                        needConfirm=false;
                        mDatas.clear();
                        initUserInfo();
                        setAdapter();
                        mAdapter.setMDatas(mDatas);
                        mHorizontalScrollView.initDatas(mAdapter);
                    }
                    thread = new RecordThread(3*1000, surfaceview,surfaceHolder,accountsNumber,dbHelper,mDatas,mHandler);
                    System.out.println("thread accountnumber: "+ accountsNumber);
                    thread.start();
                    soundPool.play(pleaseSayYourName, 1.0f, 1.0f, 1, 0, 1.0f);
                }else {
                    if(thread.mPlayer != null)
                        thread.videostop();
                    if(thread.mediarecorder!=null)
                        thread.stopRecord();
                    thread.interrupt();
                    thread = new RecordThread(3*1000, surfaceview,surfaceHolder,accountsNumber,dbHelper,mDatas,mHandler);
                    thread.start();
                    soundPool.play(pleaseSayYourName, 1.0f, 1.0f, 1, 0, 1.0f);
                }
            }
        });
        surfaceview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(needConfirm){       //when plat back end, need touch the TV Screen to confirm, then saved in Database and log kid in
                    saveUserInfo();     //else if not click TV screen instead touch capture,record new video
                    needConfirm=false;
                    System.out.println("go to activity");
                    //then go to menu 1
                }
                needConfirm=false;
            }
        });
    }

    private void saveUserInfo(){      //save info and update UI Interface
        UserInfo curUser=new UserInfo();
        curUser.setID(accountsNumber);
        curUser.setUserIcon(thread.pPath);
        curUser.setUserVideo(thread.vPath);
        dbHelper.saveUserInfo(curUser);      //save
        accountsNumber++;
        mDatas.clear();
        initUserInfo();
        setAdapter();
        mAdapter.setMDatas(mDatas);
        mHorizontalScrollView.initDatas(mAdapter);
        System.out.println("refresh gallery");
    }

    private void setAdapter(){
        if(userInfo.size()==1){
            mAdapter = new ScrollViewAdapter(this, mDatas);
        }
    }

    private void refreshGallery(){    //after took a new video, refresh the gallery before confirm
        Bitmap bmp = BitmapFactory.decodeFile(thread.pPath);
        System.out.println(thread.pPath);
        mDatas.clear();
        initUserInfo();
        mDatas.add(0,bmp);
        if(userInfo.size()==0){
            mAdapter = new ScrollViewAdapter(this, mDatas);
        }
        mAdapter.setMDatas(mDatas);
        mAdapter.accountNumber = mDatas.size();
        System.out.println(mDatas.size());
        mHorizontalScrollView.initDatas(mAdapter);
        System.out.println("refresh gallery1");
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:    //play back end : enrolled id
                    //go to the menu, curid=accountnumber-chosenid-1
                    capture.setVisibility(View.VISIBLE);
                    System.out.println("go to menu");
                    break;
                case 2:     //plat back end: new id
                    //if you like how you said your name,please tap here
                    startFlashTime = System.nanoTime();
                    lastFlashTime = System.nanoTime();
                    mainHandler.post(flashScreen);
                    nowRed = false;
                    capture.setVisibility(View.VISIBLE);
                    refreshGallery();      //the aim is to add newly taken photo to the gallery though not in the database
                    mp1.start();
                    needConfirm=true;
                    break;
                case 3:
                    mDatas.clear();
                    initUserInfo();
                    setAdapter();
                    mAdapter.setMDatas(mDatas);
                    mHorizontalScrollView.initDatas(mAdapter);
                    break;
                case 4:
                    saveUserInfo();
                    break;
                case 5:     //record done, play "you said", then replay video
                    mp3.start();
                    break;
                default:
                    break;
            }
        }
    };

    private Runnable flashScreen = new Runnable() {
        @Override
        public void run() {
            long timepass = (System.nanoTime() - lastFlashTime) / 1000000;
            if(timepass >800)
            {
                if(!nowRed) {
                    screenMargin.setBackgroundColor(Color.RED);
                    nowRed = true;
                }
                else {
                    screenMargin.setBackgroundColor(Color.WHITE);
                    nowRed = false;
                }
                lastFlashTime = System.nanoTime();
            }
            if((System.nanoTime()-startFlashTime)/1000000 <= 5000)
                mainHandler.postDelayed(flashScreen, 400);
            else {
                screenMargin.setBackgroundColor(Color.WHITE);
                nowRed = false;
            }
        }
    };

    private Runnable flashButton = new Runnable() {
        @Override
        public void run() {
            long timepass = (System.nanoTime() - button_lastFlashTime) / 1000000;
            if(timepass > 800)
            {
                if(!button_nowRed) {
                    buttonMargin.setBackgroundColor(Color.RED);
                    button_nowRed = true;
                }
                else {
                    buttonMargin.setBackgroundColor(Color.WHITE);
                    button_nowRed = false;
                }
                button_lastFlashTime = System.nanoTime();
            }
            if((System.nanoTime()-button_startFlashTIme)/1000000 <= 5000)
                mainHandler.postDelayed(flashButton, 400);
            else {
                buttonMargin.setBackgroundColor(Color.WHITE);
                button_nowRed = false;
            }
        }
    };

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub
        // 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
        Log.i("SurfaceHolder", "surfaceChanged()");
        surfaceHolder = holder;
    }
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        Log.i("SurfaceHolder", Thread.currentThread().getName());
        // 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
        surfaceHolder = holder;
        // 录像线程，当然也可以在别的地方启动，但是一定要在onCreate方法执行完成以及surfaceHolder被赋值以后启动
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
        Log.i("SurfaceHolder", "surfaceDestroyed()");
        // surfaceDestroyed的时候同时对象设置为null
        surfaceview = null;
        surfaceHolder = null;
		/*释放资源 mediarecorder mCamera */
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
        Log.i("RecordDemoActivity", "onDestroy()");
    }
}
