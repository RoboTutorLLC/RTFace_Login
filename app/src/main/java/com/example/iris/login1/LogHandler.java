package com.example.iris.login1;

import android.os.Build;
import android.util.Log;
import android.util.Pair;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shivenmian on 15/08/18.
 */

public class LogHandler {

    private String report = "";
    private UserInfo user;
    private File logFile;
    private Date currentTime;

    public LogHandler() {
        try {
            String deviceId = Build.SERIAL;
            currentTime = new Date();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss.SSS").format(currentTime);
            File logFileDir = new File(Common.RT_PATH + "/facelogin_logs");
            if(!logFileDir.exists()){
                logFileDir.mkdirs(); // incase RoboTutor folder is nonexistent
            }
            logFile =  new File(Common.RT_PATH + "/facelogin_logs/LOG_" + timestamp + "_" + deviceId + "_" + BuildConfig.BUILD_TYPE + "_" + BuildConfig.VERSION_NAME + ".json");
            logFile.createNewFile();
            FileOutputStream trace = new FileOutputStream(logFile, false);
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("logFile",new JSONArray());
            trace.write(jsonObj.toString().getBytes());
            trace.close();

        } catch(IOException ioe) {
            ioe.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void logUser(UserInfo user) {
        this.user = user;
        JSONObject newUser = new JSONObject();
        try {
            newUser.put("last_login_time", user.getLastLoginTime());
            newUser.put("profile_icon", user.getProfileIcon());
            newUser.put("gender", user.getGender());
            newUser.put("birth_date", user.getBirthDate());
            newUser.put("birth_device", user.getBirthDevice());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        appendToJSON(newUser);
    }

    public void log(Pair... extras){
        JSONObject newLog = new JSONObject();
        Date timestamp = new Date();
        long hiatusTime = timestamp.getTime() - currentTime.getTime();
        currentTime = timestamp;
        for (Pair p: extras){
            try {
                newLog.put("hiatus_ms", hiatusTime);
                newLog.put("timestamp", new SimpleDateFormat("yyyyMMdd_HHmmss.SSS").format(timestamp));
                newLog.put("eventType", p.first);
                newLog.put("eventValue", p.second);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        appendToJSON(newLog);
    }

    public void appendToJSON(JSONObject jsonObject) {
        String prevJson = "";
        try {
            FileReader fileReader = new FileReader(logFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null){
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            prevJson = stringBuilder.toString();

        } catch(Exception e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream trace = new FileOutputStream(logFile);
            JSONObject previousJsonObj = new JSONObject(prevJson);
            JSONArray array = previousJsonObj.getJSONArray("logFile");
            array.put(jsonObject);
            JSONObject currentJsonObject = new JSONObject();
            currentJsonObject.put("logFile",array);
            Log.e("currentJsonObject", currentJsonObject.toString());
            trace.write(currentJsonObject.toString().getBytes());
            trace.close();
        } catch(Exception ioe) {
            ioe.printStackTrace();
        }
    }


}
