package com.example.iris.login1;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shivenmian on 20/04/18.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler defaultUEH;

    public CrashHandler() {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread t, Throwable e) {
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

        try {
            String deviceId = Build.SERIAL;
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File logFileDir = new File(Common.RT_PATH + "/facelogin_crashlogs");
            if(!logFileDir.exists()){
                logFileDir.mkdirs(); // incase RoboTutor folder is nonexistent
            }
            File logFile = new File(Common.RT_PATH + "/facelogin_crashlogs/CRASH_" + timestamp + "_" + deviceId + "_" + BuildConfig.BUILD_TYPE + "_" + BuildConfig.VERSION_NAME + ".txt");
            logFile.createNewFile();
            FileOutputStream trace = new FileOutputStream(logFile, false);
            trace.write(report.getBytes());
            trace.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }

        defaultUEH.uncaughtException(t, e);
    }
}