package com.example.iris.login1;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ErrorHandler {
    private HashMap queueMap    = new HashMap();
    private static String TAG = "ERROR_HANDLER";
    private LogThread logThread;
    private boolean   isLogging = false;
    private Handler logHandler;
    private boolean mDisabled = false;
    protected static String sessionStartTime;
    private String sequenceID;

    private void post(String msg, Exception e) {
        enQueue(new ErrorHandler.Queue(msg,e));
    }

    private void enQueue(Queue qCommand) {
        if(!mDisabled)
            queueMap.put(qCommand, qCommand);
        logHandler.post(qCommand);
    }

    private final class LogThread extends HandlerThread {

        public LogThread(String name) {
            super(name);
        }

        public LogThread(String name, int priority) {
            super(name, priority);
        }
    }
    

    public void startLogging(String SEQUENCE_ID_STRING) {


        // Restart the log if necessary
        //
        sequenceID = SEQUENCE_ID_STRING;
        sessionStartTime = new SimpleDateFormat("yyyyMMdd_HHmmss.SSS").format(new Date());
        stopLogging();

        isLogging = true;
        mDisabled = false;

        logThread = new LogThread(TAG);
        logThread.start();

        try {
            logHandler = new Handler(logThread.getLooper());
        }
        catch(Exception e) {
            LogHandler.logError("Handler Create Failed:",e);
            Log.e(TAG, "Handler Create Failed:" + e);
        }

    }

    public void stopLogging() {

        if(isLogging) {
            Log.i(TAG, "Shutdown begun");

            isLogging = false;
            mDisabled = true;

            // Terminate the log thread - flush the queue prior to exit
            //
            try {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    logThread.getLooper().quitSafely();
                }

                logThread.join();            // waits until it finishes
                Log.i(TAG, "Shutdown complete");

            } catch (InterruptedException e) {
                LogHandler.logError("Stop logging error.",e);
            }


        }
    }

    public class Queue implements Runnable {

        protected String errorMsg;
        protected Exception exception;

        public Queue(String msg, Exception e) {

            exception = e;
            errorMsg = msg;
        }
        @Override
        public void run() {
            try {
                queueMap.remove(this);
                if(errorMsg != null){
                    Log.e("ErrorHandler errorMsg: ", errorMsg);
                    addToErrorLog(errorMsg,exception);
                }

            } catch (Exception e) {
                LogHandler.logError("ErrorHandler Exception",e);
                Log.e("ErrorHandler Exception", e.toString());
                postError("Write Error:", e);
            }
        }
    }

    private void addToErrorLog(String errorMsg, Exception e) {
        if(e == null){
            String report = errorMsg +"\n\n";
            createErrorFile(report);
            return;
        }
        StackTraceElement[] arr = e.getStackTrace();
        String report = e.toString()+"\n\n";
        report += "--------- Stack trace ---------\n\n";
        for (int i=0; i<arr.length; i++) {
            report += "    "+arr[i].toString()+"\n";
        }
        report += "-------------------------------\n\n";

        report += "--------- Cause ---------\n\n";
        Throwable cause = e.getCause();
        if(cause != null) {
            report += cause.toString() + "\n\n";
            arr = cause.getStackTrace();
            for (int i=0; i<arr.length; i++) {
                report += "    "+arr[i].toString()+"\n";
            }
        }
        report += "-------------------------------\n\n";
        createErrorFile(report);
    }

    private void createErrorFile(String report) {
        try {
//            Make sure this is accurate

            String deviceId = Build.SERIAL;
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss.SSS").format(new Date());
            String _directory = Common.RT_PATH + "/facelogin_errors/";
            File logFileDir = new File(_directory);
            if(!logFileDir.exists()){
                logFileDir.mkdirs();
            }

            //Add version how ?? BuildConfig.VERSION_NAME + "_" not working for this package need to get version from robotutor
            File logFile = new File(Common.RT_PATH + "/facelogin_errors/ERROR_" + sessionStartTime + "_" + BuildConfig.BUILD_TYPE + "_" + sequenceID + "_" +  timestamp +  deviceId + "_" + BuildConfig.VERSION_NAME + ".txt");
            logFile.createNewFile();
            FileOutputStream trace = new FileOutputStream(logFile, false);
            trace.write(report.getBytes());
            trace.close();
        } catch(IOException ioe) {
            LogHandler.logError("CEF",ioe);
            Log.d("CEF",ioe.getMessage());
            ioe.printStackTrace();
        }
    }


    public void postError(String Msg, Exception e) {
        post(Msg,e);
    }

}
