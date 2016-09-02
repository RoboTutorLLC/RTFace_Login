package com.example.iris.login1;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Iris on 16/7/25.
 */

public class playVideo extends Thread {
    private SurfaceView surfaceview;// 显示视频的控件
    private SurfaceHolder surfaceHolder;
    public String vPath="";
    public String pPath="";
    private MediaPlayer mPlayer;
    private Handler mHandler;
    public playVideo(SurfaceView surfaceview,
                        SurfaceHolder surfaceHolder, String vPath_,String pPath_,Handler mhandler_) {
        this.surfaceview = surfaceview;
        this.surfaceHolder = surfaceHolder;
        this.vPath=vPath_;
        this.pPath=pPath_;
        this.mHandler=mhandler_;
    }
    public void run() {
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setDisplay(surfaceHolder);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                videostop();
                mHandler.sendEmptyMessage(1);      //after replay,login kids in
            }
        });
        try {
            mPlayer.setDataSource(vPath);
            mPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPlayer.start();
    }
    public void videostop() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}
