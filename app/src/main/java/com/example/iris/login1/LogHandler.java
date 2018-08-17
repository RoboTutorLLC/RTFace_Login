package com.example.iris.login1;

import android.os.Build;
import android.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shivenmian on 15/08/18.
 */

public class LogHandler {

    private UserInfo user;
    public LogHandler(UserInfo user){
        this.user = user;
    }

    public void log(Pair... extras){

        String report = "--------- UserInfo ---------\n\n";

        report += String.format("Last login: %s\nProfile Icon: %s\nGender: %s\nBirth Date: %s\nBirth Device: %s\n",
                user.getLastLoginTime(), user.getProfileIcon(), user.getGender(), user.getBirthDate(), user.getBirthDevice());

        report += "-------------------------------\n\n";

        report += "--------- Extras ---------\n\n";
        for (Pair p: extras){
            report += p.first + ": " + p.second + "\n";
        }
        report += "-------------------------------\n\n";

        try {
            String deviceId = Build.SERIAL;
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File logFileDir = new File(Common.RT_PATH + "/facelogin_logs");
            if(!logFileDir.exists()){
                logFileDir.mkdirs(); // incase RoboTutor folder is nonexistent
            }
            File logFile = new File(Common.RT_PATH + "/facelogin_logs/LOG_" + timestamp + "_" + deviceId + "_" + BuildConfig.BUILD_TYPE + "_" + BuildConfig.VERSION_NAME + ".txt");
            logFile.createNewFile();
            FileOutputStream trace = new FileOutputStream(logFile, false);
            trace.write(report.getBytes());
            trace.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
