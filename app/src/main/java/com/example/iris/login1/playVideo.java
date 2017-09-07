package com.example.iris.login1;

import android.content.Context;
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
    private int realStartTime;
    //surfaceview is used to show the video
    private SurfaceView surfaceview;
    private SurfaceHolder surfaceHolder;
    public String vPath = "";
    public String pPath = "";
    private MediaPlayer mPlayer;
    private Handler mHandler;
    private Context context;
    private boolean isResVideo = false;
    private boolean isPlaying = true;

    public playVideo(int realStartTime, SurfaceView surfaceview, SurfaceHolder surfaceHolder,
                     String vPath_, String pPath_, Handler mhandler_, Context context, boolean isResVideo) {
        this.realStartTime = realStartTime;
        this.surfaceview = surfaceview;
        this.surfaceHolder = surfaceHolder;
        this.vPath = vPath_;
        this.pPath = pPath_;
        this.context = context;
        this.mHandler = mhandler_;
        this.isResVideo = isResVideo;
    }

    public void run() {
        if (isResVideo)
            playResVideo();
        else
            playSdcardVideo();
    }

    public void playResVideo() {
        mPlayer = MediaPlayer.create(context, R.raw.makesuretoholdthetabletbythesidesandtaplightlywithonefingerlikethis);
        mPlayer.setDisplay(surfaceHolder);
        mPlayer.start();
        mHandler.sendEmptyMessage(Common.UNCOVER_SCREEN);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mHandler.sendEmptyMessage(Common.PLAY_TAPPING_VIDEO_DONE);
                mPlayer.release();
                mPlayer = null;
            }
        });
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                mPlayer.reset();
                return false;
            }
        });
    }

    public void playSdcardVideo() {
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setDisplay(surfaceHolder);
        try {
            mPlayer.setDataSource(vPath);
            mPlayer.prepareAsync();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mPlayer.seekTo(realStartTime);
                isPlaying = true;
            }
        });

        mPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            public void onSeekComplete(MediaPlayer m) {
                if (isPlaying) {
                    mPlayer.start();
                    mHandler.sendEmptyMessage(Common.UNCOVER_SCREEN);
                    isPlaying = false;
                }
            }
        });

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                if (mPlayer != null) {
                    mPlayer.seekTo(realStartTime + mPlayer.getDuration()); // set frame shown to last frame of video
                }
                mHandler.sendEmptyMessage(Common.REPLAY_OLD_VIDEO_DONE);      //after replay,login kids in
            }
        });

        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                mPlayer.reset();
                return false;
            }
        });
    }

    public void stopPlayingVideo() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }


}
