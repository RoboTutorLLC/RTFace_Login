package com.example.iris.login1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Iris on 16/7/20.
 */

public class DataHelper {
    private static String DB_NAME = "myXprizeProject.db";
    private static int DB_VERSION = 3;
    private SQLiteDatabase db;
    private SqliteHelper dbHelper;

    public DataHelper(Context context){
        dbHelper = new SqliteHelper(context, DB_NAME, null, DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public void Close(){
        db.close();
        dbHelper.close();
    }

    public List<UserInfo> getUserList(){
        List<UserInfo> userList = new ArrayList<UserInfo>();
        Cursor cursor = db.query(SqliteHelper.TB_NAME, null, null, null, null, null, UserInfo.ID + " DESC");
        cursor.moveToFirst();
        while(!cursor.isAfterLast() && (cursor.getString(0) != null)) {
            UserInfo user = new UserInfo();
            user.setID(Integer.parseInt(cursor.getString(0)));
            user.setUserIcon(cursor.getString(1));
            user.setUserVideo(cursor.getString(2));
            user.setRecordTime(cursor.getString(3));
            userList.add(user);
            cursor.moveToNext();
        }
        cursor.close();
        return userList;
    }

    public Long saveUserInfo(UserInfo user){
        ContentValues values = new ContentValues();
        values.put(UserInfo.ID, user.getID());
        values.put(UserInfo.USERICON, user.getUserIcon());
        values.put(UserInfo.USERVIDEO,user.getUserVideo());
        values.put(UserInfo.RECORDTIME, user.getRecordTime());
        Long uid = db.insert(SqliteHelper.TB_NAME, null, values);
        Log.e("saveUserInfo", uid + "");
        return uid;
    }

    public Boolean haveUserInfo(String UserId){
        Boolean b = false;
        Cursor cursor = db.query(SqliteHelper.TB_NAME, null, UserInfo.ID + "=" + UserId, null, null, null, null);
        b = cursor.moveToFirst();
        Log.e("HaveUserInfo", b.toString());
        cursor.close();
        return b;
    }

    public int deletUserInfo(String ID){
        int id = db.delete(SqliteHelper.TB_NAME, UserInfo.ID + "=" + ID, null);
        System.out.println("Delete UserInfo " + ID);
        return id;
    }
}
