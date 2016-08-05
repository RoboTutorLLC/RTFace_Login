package com.example.iris.login1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Iris on 16/7/21.
 */

public class RecordThread extends Thread{
    private MediaRecorder mediarecorder;// 录制视频的类
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mPlayer;
    private long recordTime;
    private SurfaceView surfaceview;// 显示视频的控件
    public Camera mCamera;
    public int chooseReplay=0;
    public String vPath="";
    public String pPath="";
    private int accountsNumber;
    private UserInfo curUser;
    private DataHelper dbHelper;
    private List<Bitmap> mDatas;
    private Handler mHandler;
    private int chosenCamera;
    public boolean isRecording=false;
    public boolean isPlaying=false;

    public RecordThread(long recordTime, SurfaceView surfaceview,
                        SurfaceHolder surfaceHolder, int accountsNumber_, DataHelper dbHelper_, List<Bitmap> mDatas_, Handler mhandler_) {
        this.recordTime = recordTime;
        this.surfaceview = surfaceview;
        this.surfaceHolder = surfaceHolder;
        this.accountsNumber=accountsNumber_;
        this.dbHelper=dbHelper_;
        this.mDatas=mDatas_;
        this.mHandler=mhandler_;
        curUser=new UserInfo();
        if(dbHelper.haveUserInfo(""+accountsNumber)){
            dbHelper.deletUserInfo(""+accountsNumber);
        }
    }

    @Override
    public void run() {
        startRecord();
        Timer timer = new Timer();
        timer.schedule(new TimerThread(), recordTime);    //启动定时器，到规定时间recordTime后执行停止录像任务
    }

    public Camera getCameraInstance() {     //获取摄像头实例
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            // 打开摄像头错误
            Log.i("info", "打开摄像头错误");
        }
        return c;
    }

    public void startRecord() {
        isRecording=true;
        mCamera = getCameraInstance();
        //切换前后摄像头
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();       //得到摄像头的个数
        int cameraPosition = 1;
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);         //得到每一个摄像头的信息
            if (cameraPosition == 1) {                    //现在是后置，变更为前置
                //代表摄像头的方位，CAMERA_FACING_FRONT前置,CAMERA_FACING_BACK后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    if (mCamera != null) {
                        mCamera.stopPreview();      //停掉原来摄像头的预览
                        mCamera.release();          //释放资源
                        mCamera = null;             //取消原来摄像头
                    }
                    mCamera = Camera.open(i);       //打开当前选中的摄像头,1代表前置摄像头
                    chosenCamera=i;
                    try {
                        mCamera.setPreviewDisplay(surfaceHolder);   //通过surfaceview显示取景画面
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mCamera.startPreview();//开始预览
                    cameraPosition = 0;
                    break;
                }
            }
            else {                         //现在是前置， 变更为后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    mCamera.stopPreview();//停掉原来摄像头的预览
                    mCamera.release();//释放资源
                    mCamera = null;//取消原来摄像头
                    mCamera = Camera.open(i);//打开当前选中的摄像头
                    try {
                        mCamera.setPreviewDisplay(surfaceHolder);     //通过surfaceview显示取景画面
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mCamera.startPreview();//开始预览
                    cameraPosition = 1;
                    break;
                }
            }
        }
        mCamera.setDisplayOrientation(0);// 解决竖屏的时候，摄像头旋转90度的问题
        Camera.Parameters params=mCamera.getParameters();
        params.setPictureSize(640,480);// 640x480,320x240,176x144,160x120
        mCamera.setParameters(params);
        mCamera.unlock(); // 解锁camera

        // 第1步:解锁并将摄像头指向MediaRecorder
        mediarecorder = new MediaRecorder();
        mediarecorder.setCamera(mCamera);
        // 第2步:指定源
        mediarecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // 第3步:指定CamcorderProfile(需要API Level 8以上版本)
        // mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        //如果使用CamcorderProfile做配置的话，就应该注释设置输出格式，音频编码，视频编码3条语句
        // 第3步:设置输出格式和编码格式(针对低于API Level 8版本)
        mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        vPath = "VIDEO_" + timeStamp + "_" + ".3gp";
        vPath = "/sdcard/"+ vPath;
        System.out.println(vPath);
        //vPath=getExternalFilesDir(null)+vPath;
        mediarecorder.setOutputFile(vPath);
        mediarecorder.setVideoSize(640,480);//设置视频分辨率，这里很重要，设置错start()报未知错误
        //mediarecorder.setVideoFrameRate(24);//设置视频帧率  这个我把它去掉了，感觉没什么用
        mediarecorder.setVideoEncodingBitRate(10*1024*1024);//在这里我提高了帧频率,然后就清晰了,解决了花屏、绿屏的问题

        mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());
        try {
            // 准备录制
            mediarecorder.prepare();
            // 开始录制
            mediarecorder.start();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        isRecording=false;
        System.out.print("stopRecord()");
        //surfaceview = null;
        //surfaceHolder = null;
        if (mediarecorder != null) {
            // 停止录制
            mediarecorder.stop();
            mediarecorder.reset();
            // 释放资源
            mediarecorder.release();
            mediarecorder = null;

            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
        }
    }
    class TimerThread extends TimerTask {
        @Override
        public void run() {
            stopRecord();
            saveFirstFrame();
            newReplay(vPath);

            //this.cancel();
        }
    }
    private void createImageFile()  {          //根据当前时间生成mPath
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        pPath = "IMAGE_" + timeStamp + "_" + ".jpg";
        pPath = "/sdcard/"+ pPath;
    }
    public void saveFirstFrame()
    {
        MediaMetadataRetriever mmr=new MediaMetadataRetriever();//实例化MediaMetadataRetriever对象
        File file=new File(vPath);
        if(file.exists()) {
            mmr.setDataSource(file.getAbsolutePath());
            Bitmap bitmap = mmr.getFrameAtTime();
            if (bitmap != null) {
                createImageFile();
                File pictureFile = new File(pPath);
                FileOutputStream fos= null;
                mDatas.add(bitmap);
                try {
                    fos = new FileOutputStream(pictureFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                    fos.flush();
                    fos.close();
                    System.out.println("savedb"+curUser.getID());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void newReplay(String vp)
    {
        isPlaying=true;
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setDisplay(surfaceHolder);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                videostop();
                mHandler.sendEmptyMessage(2);
            }
        }
        );
        try {
            mPlayer.setDataSource(vp);
            mPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPlayer.start();
    }

    public void videostop() {
        isPlaying=false;
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}
