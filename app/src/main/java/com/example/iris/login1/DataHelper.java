package com.example.iris.login1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Iris on 16/7/20.
 */

public class DataHelper {
    //TODO ANDROID_ID is better but non-static, also SERIAL might not be totally unique
    //TODO JSON is better

    //TODO having a table per user profile and union'ing / iterating over them is cumbersome - JSON is much better

    public static final String SERIAL_ID = Build.SERIAL;
    private static String DB_NAME = "myXprizeProject_" + SERIAL_ID + ".db";

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
            user.setProfileIcon(cursor.getString(2));
            user.setUserVideo(cursor.getString(3));
            user.setRecordTime(cursor.getString(4));
            user.setGender(cursor.getString(5));
            user.setBirthDevice(cursor.getString(6));
            user.setBirthDate(cursor.getString(7));

            Cursor cursor2 = db.query(SqliteHelper.REC_NAME, new String[] {UserInfo.LAST_LOGIN_TIME}, UserInfo.ID + "=?", new String[] {String.valueOf(user.getID())}, null, null, null, null);
            if (!(cursor2.moveToFirst()) || cursor2.getCount() == 0) {
                user.setLastLoginTime(new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
                this.saveUserTime(user);
            } else {
                user.setLastLoginTime(cursor2.getString(0));
            }
            userList.add(user);
            cursor2.close();
            cursor.moveToNext();
        }
        Collections.sort(userList, new LoginTimeComparator());
        cursor.close();
        Log.e("users table getulist: ", this.getTableAsString(SqliteHelper.TB_NAME));
        Log.e("recen table getulist: ", this.getTableAsString(SqliteHelper.REC_NAME));
        return userList;
    }

    public Long saveUserInfo(UserInfo user){
        ContentValues values = new ContentValues();
        values.put(UserInfo.ID, user.getID());
        values.put(UserInfo.USERICON, user.getUserIcon());
        values.put(UserInfo.PROFILEICON, user.getProfileIcon());
        values.put(UserInfo.USERVIDEO,user.getUserVideo());
        values.put(UserInfo.RECORDTIME, user.getRecordTime());
        values.put(UserInfo.GENDER, user.getGender());
        values.put(UserInfo.BIRTH_DEVICE, user.getBirthDevice());
        values.put(UserInfo.BIRTH_DATE, user.getBirthDate());
        Long uid = db.insert(SqliteHelper.TB_NAME, null, values);
        Log.e("saveUserInfo", uid + "");
        Log.e("users table suserinf: ", this.getTableAsString(SqliteHelper.TB_NAME));
        return uid;
    }

    public Long saveUserTime(UserInfo user) {
        ContentValues values_time = new ContentValues();
        values_time.put(UserInfo.ID, user.getID());
        values_time.put(UserInfo.LAST_LOGIN_TIME, user.getLastLoginTime());
        Long uid = db.insert(SqliteHelper.REC_NAME, null, values_time);
        Log.e("saveUserTime", uid + "");
        Log.e("recency table sustime: ", this.getTableAsString(SqliteHelper.REC_NAME));
        return uid;
    }

    public int updateProfileIcon(UserInfo user){
        ContentValues values_time = new ContentValues();
        values_time.put(UserInfo.PROFILEICON, user.getProfileIcon());
        String where = UserInfo.ID + "=?";
        String[] whereArgs = new String[] {String.valueOf(user.getID())};
        int uid = db.update(SqliteHelper.TB_NAME, values_time, where, whereArgs);
        Log.e("updateProfileIcon", uid + "");
        Log.e("recen table uproficon: ", this.getTableAsString(SqliteHelper.REC_NAME));
        return uid;
    }

    public int updateUserTime(UserInfo user) {
        ContentValues values_time = new ContentValues();
        values_time.put(UserInfo.LAST_LOGIN_TIME, user.getLastLoginTime());
        String where = UserInfo.ID + "=?";
        String[] whereArgs = new String[] {String.valueOf(user.getID())};
        int uid = db.update(SqliteHelper.REC_NAME, values_time, where, whereArgs);
        Log.e("updateUserTime", uid + "");
        Log.e("recen table upustime: ", this.getTableAsString(SqliteHelper.REC_NAME));
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
        int id2 = db.delete(SqliteHelper.REC_NAME, UserInfo.ID + "=" + ID, null);
        System.out.println("Delete UserInfo " + ID);
        Log.e("users table deluinfo: ", this.getTableAsString(SqliteHelper.TB_NAME));
        Log.e("recen table deluinfo: ", this.getTableAsString(SqliteHelper.REC_NAME));
        return id;
    }
    public String getTableAsString(String tableName) {
        Log.d("DataHelper", "getTableAsString called");
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst() ){
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name: columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }

        return tableString;
    }

}
