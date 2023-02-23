package com.example.iris.login1;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Iris on 16/7/20.
 */
    /*
    TODO update database during enrollment only once student has picked a photo and icon AND has entered Robotutor. Otherwise should store a "provisional" enrollment
    TODO migrate to JSON in future for sync convenience*/

public class SqliteHelper extends SQLiteOpenHelper {

    public static final String TB_NAME = "users";
    public static final String REC_NAME = "last_login";

    private Date currentTime = new Date();
    private String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss.SSS").format(currentTime);
    public String deviceId = Build.SERIAL;

    public SqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " +
                        TB_NAME + "(" +
                        UserInfo.ID + " integer," +
                        UserInfo.USERICON + " varchar," +
                        UserInfo.PROFILEICON + " varchar," +
                        UserInfo.USERVIDEO + " varchar," +
                        UserInfo.RECORDTIME + " varchar," +
                        UserInfo.GENDER + " varchar," +
                        UserInfo.BIRTH_DEVICE + " varchar," +
                        UserInfo.BIRTH_DATE + " varchar" +
                        ")"
        );
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " +
                        REC_NAME + "(" +
                        UserInfo.ID + " integer," +
                        UserInfo.LAST_LOGIN_TIME + " varchar" +
                        ")"
        );
        restoreUserProfiles();
        Log.e("database", "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Cursor cursor = db.rawQuery("SELECT * FROM "+ TB_NAME, null);
        if(cursor.moveToFirst()) {
            dumpUserProfiles();
        }
        db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + REC_NAME);

        onCreate(db);
        Log.e("database", "onUpgrade");
    }

    @SuppressLint("Range")
    private void dumpUserProfiles() {
        try {
            File dumpFileDir = new File(Common.RT_PATH + "/facelogin_userProfiles_" + BuildConfig.VERSION_NAME);
            if(!dumpFileDir.exists()){
                dumpFileDir.mkdirs();
            }
            File userProfileFile = new File(Common.RT_PATH + "/facelogin_userProfiles_" + BuildConfig.VERSION_NAME + "/userProfile_" + timestamp + "_" + deviceId + "_" + BuildConfig.VERSION_NAME + ".json");
            userProfileFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(userProfileFile);

            JSONArray jsonArray = new JSONArray();

            SQLiteDatabase db= getReadableDatabase();
            Cursor cursor =db.rawQuery("SELECT * FROM "+ TB_NAME, null);
            while(cursor.moveToNext()){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("ID", cursor.getString(cursor.getColumnIndex(UserInfo.ID)));
                jsonObject.put("USERICON", cursor.getString(cursor.getColumnIndex(UserInfo.USERICON)));
                jsonObject.put("PROFILEICON", cursor.getString(cursor.getColumnIndex(UserInfo.PROFILEICON)));
                jsonObject.put("USERVIDEO", cursor.getString(cursor.getColumnIndex(UserInfo.USERVIDEO)));
                jsonObject.put("RECORDTIME", cursor.getString(cursor.getColumnIndex(UserInfo.RECORDTIME)));
                jsonObject.put("GENDER", cursor.getString(cursor.getColumnIndex(UserInfo.GENDER)));
                jsonObject.put("BIRTH_DEVICE", cursor.getString(cursor.getColumnIndex(UserInfo.BIRTH_DEVICE)));
                jsonObject.put("BIRTH_DATE", cursor.getString(cursor.getColumnIndex(UserInfo.BIRTH_DATE)));
                jsonArray.put(jsonObject);
            }
            cursor.close();
            outputStream.write(jsonArray.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void restoreUserProfiles() {
        try {
            File restoreFile = new File(Common.RT_PATH + "/facelogin_userProfiles_" + BuildConfig.VERSION_NAME + "/userProfile_" + timestamp + "_" + deviceId + "_" + BuildConfig.VERSION_NAME + ".json");
            FileInputStream inputStream = new FileInputStream(restoreFile);
            byte[] data = new byte[(int) restoreFile.length()];
            inputStream.read(data);
            inputStream.close();
            String jsonString = new String(data);
            insertUserProfiles(jsonString);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void insertUserProfiles(String jsonString){
        try{
            JSONArray jsonArray = new JSONArray(jsonString);
            SQLiteDatabase db = getWritableDatabase();
            db.beginTransaction();
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ContentValues contentValues= new ContentValues();
                contentValues.put(UserInfo.ID, jsonObject.getString("ID"));
                contentValues.put(UserInfo.USERICON, jsonObject.getString("USERICON"));
                contentValues.put(UserInfo.PROFILEICON, jsonObject.getString("PROFILEICON"));
                contentValues.put(UserInfo.USERVIDEO, jsonObject.getString("USERVIDEO"));
                contentValues.put(UserInfo.RECORDTIME, jsonObject.getString("RECORDTIME"));
                contentValues.put(UserInfo.GENDER, jsonObject.getString("GENDER"));
                contentValues.put(UserInfo.BIRTH_DEVICE, jsonObject.getString("BIRTH_DEVICE"));
                contentValues.put(UserInfo.BIRTH_DATE, jsonObject.getString("BIRTH_DATE"));
                db.insert(TB_NAME, null, contentValues);
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void updateColumn(SQLiteDatabase db, String name, String oldColumn, String newColumn, String typeColumn) {
        try {
            db.execSQL(
                    "ALTER TABLE" +
                            name + "CHANGE" +
                            oldColumn + " " + newColumn +
                            " " + typeColumn
            );
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
