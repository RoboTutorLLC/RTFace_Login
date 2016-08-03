package com.example.iris.login1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Image;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
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

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    private SoundPool soundPool;
    private int thisisrobotutor,pleaseSayYourName,ifYouLikeHowYouSaid,pleaseTapHere,otherwiseTapHere;
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
    private int chosenId;      //current user id
    private SwipeRefreshLayout swipeRefreshLayout;

    private void initUserInfo(){
        dbHelper = new DataHelper(this);
        userInfo = dbHelper.getUserList();
        for (int i = 0; i != userInfo.size(); i++) {
            String tempUrl = userInfo.get(i).getUserIcon();
            System.out.println("user "+userInfo.get(i).getID()+" "+userInfo.get(i).getUserIcon());
            Bitmap bmp = BitmapFactory.decodeFile(tempUrl);
            mDatas.add(bmp);
        }
        /*if(userInfo.size()==0)
        {
            //add default video and picture;
            Resources res=getResources();
            Bitmap bmp = BitmapFactory.decodeResource(res, R.mipmap.ic_launcher);
            mDatas.add(bmp);
            mAdapter.accountNumber = userInfo.size()+1;
        }
        else*/
            mAdapter.accountNumber = userInfo.size();
        accountsNumber=userInfo.size();
        System.out.println("userInfo"+userInfo.size());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gallery);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        thisisrobotutor=soundPool.load(this, R.raw.thisisrobotutor, 1);
        pleaseSayYourName=soundPool.load(this, R.raw.pleasesayyourname, 1);
        ifYouLikeHowYouSaid=soundPool.load(this, R.raw.ifyoulikehowyousaidyourname, 1);
        pleaseTapHere=soundPool.load(this, R.raw.pleasetaphere, 1);
        otherwiseTapHere=soundPool.load(this, R.raw.otherwisetaphere, 1);
        soundPool.play(thisisrobotutor, 1.0f, 1.0f, 1, 0, 1.0f);       //play the sound "this is roboTutor"

        surfaceview = (SurfaceView) findViewById(R.id.id_content);
        capture=(ImageView) findViewById(R.id.capture);
        mHorizontalScrollView = (MyScrollView) findViewById(R.id.id_horizontalScrollView);

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
                System.out.println("click scroll position: "+position);
                String v=userInfo.get(position).getUserVideo();
                String p=userInfo.get(position).getUserIcon();
                chosenId=position;
                //System.out.println("dbhelper"+s);
                if(pv!=null){
                    pv.videostop();
                }
                pv= new playVideo(surfaceview, surfaceHolder,v,p);
                pv.start();
                view.setBackgroundColor(Color.RED);
            }
        });

        initUserInfo();
        if(userInfo.size()!=0) {
            mAdapter = new ScrollViewAdapter(this, mDatas);
            mHorizontalScrollView.initDatas(mAdapter);
        }

        /*swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("swipeListener");
                        mDatas.clear();
                        initUserInfo();
                        mAdapter.setMDatas(mDatas);
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mHandler.sendEmptyMessage(1);
                    }
                }).start();
            }
        });*/

        SurfaceHolder holder = this.surfaceview.getHolder();// 取得holder
        holder.addCallback(this);    //holder加入回调接口
        // 设置setType
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        capture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                soundPool.play(pleaseSayYourName, 1.0f, 1.0f, 1, 0, 1.0f);
                if (thread==null) {
                    thread = new RecordThread(3*1000, surfaceview,surfaceHolder,accountsNumber,dbHelper,mDatas,mHandler);
                    System.out.println("thread accountnumber: "+ accountsNumber);
                    accountsNumber++;
                    thread.start();
                }else {
                    thread=null;
                    thread = new RecordThread(3*1000, surfaceview,surfaceHolder,accountsNumber,dbHelper,mDatas,mHandler);
                    accountsNumber++;
                    thread.start();
                    //Toast.makeText(nameVideo.this, "recording……", Toast.LENGTH_SHORT).show();
                }
            }
        });
        surfaceview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialog();
            }
        });
    }

    private void setAdapter(){
        if(userInfo.size()==1){
            mAdapter = new ScrollViewAdapter(this, mDatas);
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                /*case 1:
                    swipeRefreshLayout.setRefreshing(false);
                    mHorizontalScrollView.initDatas(mAdapter);
                    //mHorizontalScrollView.notifyCurrentImgChanged();
                    System.out.println("swipeHandler");
                    //swipeRefreshLayout.setEnabled(false);
                    break;*/
                case 1:
                    mDatas.clear();
                    initUserInfo();
                    setAdapter();
                    mAdapter.setMDatas(mDatas);
                    mHorizontalScrollView.initDatas(mAdapter);
                    System.out.println("refresh gallery");
                    break;
                default:
                    break;
            }
        }
    };

    private void showMessage(String title, String context){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(context);
        builder.show();
    }

    public void dialog()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("");
        builder.setTitle("");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //yes enroll
                System.out.println("choose "+chosenId);
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                thread = new RecordThread(3*1000, surfaceview,surfaceHolder,accountsNumber-chosenId-1,dbHelper,mDatas,mHandler);
                System.out.println("thread accountnumber: "+ accountsNumber);
                thread.start();
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
        soundPool.play(ifYouLikeHowYouSaid, 1.0f, 1.0f, 3, 0, 1.0f);
        //soundPool.play(pleaseTapHere, 1.0f, 1.0f, 2, 0, 1.0f);
        //soundPool.play(otherwiseTapHere, 1.0f, 1.0f, 1, 0, 1.0f);
    }

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
