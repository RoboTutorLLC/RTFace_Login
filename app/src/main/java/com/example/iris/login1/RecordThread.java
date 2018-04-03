package com.example.iris.login1;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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
    public MediaRecorder mediarecorder;// class to record video
    private SurfaceHolder surfaceHolder;
    public MediaPlayer mPlayer;
    private int recordTime;
    public long absoluteStartTime;
    public long relativeStartTime;
    private SurfaceView surfaceview;// place to show the video
    public Camera mCamera;
    public int chooseReplay = 0;
    public String vPath="";
    public String pPath="";
    private int accountsNumber;
    private UserInfo curUser;
    private DataHelper dbHelper;
    private List<Pair<Bitmap, Integer>> mDatas;
    private Handler mHandler;
    private int chosenCamera;
    public boolean isRecording = false;
    public boolean isPlaying = false;

    public RecordThread(int recordTime, SurfaceView surfaceview,
                        SurfaceHolder surfaceHolder, int accountsNumber_, DataHelper dbHelper_, List<Pair<Bitmap, Integer>> mDatas_, Handler mhandler_) {
        this.recordTime = recordTime;
        this.surfaceview = surfaceview;
        this.surfaceHolder = surfaceHolder;
        this.accountsNumber = accountsNumber_;
        this.dbHelper = dbHelper_;
        this.mDatas = mDatas_;
        this.mHandler = mhandler_;
        curUser = new UserInfo();
        if(dbHelper.haveUserInfo("" + accountsNumber))
            dbHelper.deletUserInfo("" + accountsNumber);
    }

    @Override
    public void run() {
        startRecord();
    }

    public void startRecord() {
        isRecording = true;
        mCamera = getCameraInstance();
        //swap between front and rear cameras
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                if (mCamera != null) {
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                }
                mCamera = Camera.open(i);
                chosenCamera=i;
                try {
                    mCamera.setPreviewDisplay(surfaceHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File path = new File(Common.FACE_LOGIN_PATH);
        if (!path.exists()) path.mkdir();
        vPath = Common.FACE_LOGIN_PATH + "/" + "VIDEO_" + Build.SERIAL + "_" +  timeStamp + ".3gp";

        mCamera.setDisplayOrientation(0);
        Camera.Parameters params = mCamera.getParameters();
        params.setPictureSize(640,480); // 640x480,320x240,176x144,160x120
        mCamera.setParameters(params);
        //mCamera.startPreview();
        mCamera.unlock();

        _startRecord();
    }

    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            // Wrong occurs when open the camera
            Log.i("info", "打开摄像头错误");
        }
        return c;
    }

    public void _startRecord() {
        mediarecorder = new MediaRecorder();
        mediarecorder.setCamera(mCamera);
        mediarecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //vPath=getExternalFilesDir(null)+vPath;
        mediarecorder.setOutputFile(vPath);
        mediarecorder.setVideoSize(640,480);
        //mediarecorder.setVideoFrameRate(24);
        mediarecorder.setVideoEncodingBitRate(10*1024*1024);
        mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());
        try {
            long before = System.currentTimeMillis();
            mediarecorder.prepare();
            long after = System.currentTimeMillis();
            Log.i("DEBUG", "prepare:" + (after - before));
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // start to record

        long before = System.currentTimeMillis();
        mediarecorder.start();
        long after = System.currentTimeMillis();
        Log.i("DEBUG", "start:" + (after - before));
        //initiate absoluteStartTime
        absoluteStartTime = System.currentTimeMillis();
        mHandler.sendEmptyMessage(Common.READY_TO_RECORD);
    }

    public void setTimerToStopRecording() {
        Log.i("DEBUG", "absoluteStartTime=" + absoluteStartTime);
        relativeStartTime = System.currentTimeMillis() - absoluteStartTime;
        Log.i("DEBUG", "relativeStartTime=" + relativeStartTime);
        Log.i("DEBUG", "recordTime=" + recordTime);
        //set timer, stop recording in 3s.
        Timer timer = new Timer();
        timer.schedule(new TimerThread(), recordTime);
    }

    public void stopRecord() {
        isRecording = false;

        if (mediarecorder != null) {
            // stop recording
            mediarecorder.stop();
            mediarecorder.reset();
            // release the resources
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
            saveFrame();
            //should play "you said"
            // KIMTAR this is called when the video is done recording
            mHandler.sendEmptyMessage(Common.RECORD_DONE);
            //newReplay(vPath);
            //this.cancel();
        }
    }

    private void createImageFile()  {
        //create the name of image according to the Timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File path = new File(Common.FACE_LOGIN_PATH);
        if (!path.exists()) path.mkdir();

        pPath = Common.FACE_LOGIN_PATH + "/";
        pPath += Common.IMAGE_FILE_PREFIX + Build.SERIAL + "_" + timeStamp + Common.IMAGE_FILE_SUFFIX;
    }

    public void saveFrame() {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        File file = new File(vPath);
        if(file.exists()) {
            mmr.setDataSource(file.getAbsolutePath());
            //we need to transfer the real start time from ms to us
            //Capture frame (call it F) [2 sec after] when prompt ends.
            Bitmap bitmap = mmr.getFrameAtTime((relativeStartTime + Common.CAPTURE_FRAME_TIME_GAP) * 1000);
            // MARCH save if first registration

            //if (bitmap != null && GalleryActivity.getFirstRegistration()) {
            if (bitmap != null) {
                createImageFile();
                File pictureFile = new File(pPath);
                FileOutputStream fos = null;

                try {
                    fos = new FileOutputStream(pictureFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * replay the new video
     * @param vp
     */
    public void newReplay(String vp, long trailingSilence) {
        isPlaying = true;
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setDisplay(surfaceHolder);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                backToFrame();
                mHandler.sendEmptyMessage(Common.REPLAY_NEW_VIDEO_DONE);
            }
        });

        try {
            mPlayer.setDataSource(vp);
            mPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        int seekToMe = (int) (relativeStartTime - trailingSilence);
        Log.i("DEBUG", "relativeStartTime=" + relativeStartTime + "; trailingSilence=" + trailingSilence + "; diff=" + seekToMe);
        mPlayer.seekTo(2013);
        //mPlayer.seekTo(0);

        mPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mediaPlayer) {
                if (isPlaying) mPlayer.start();
            }
        });

    }

    public void backToFrame() {
        isPlaying = false;
        if (mPlayer != null) {
            mPlayer.seekTo((int) relativeStartTime + mPlayer.getDuration()); // set frame shown to last frame of video
        }


    }

    public void stopPlayingVideo() {
        isPlaying = false;
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void deleteVideoAndPicture() {
        File video = new File(vPath);
        if (video.exists()) video.delete();

        File picture = new File(pPath);
        if (picture.exists()) picture.delete();
    }
}
