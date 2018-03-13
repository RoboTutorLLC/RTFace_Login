package com.example.iris.login1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

    public Long saveUserTime(UserInfo user) {
        ContentValues values_time = new ContentValues();
        values_time.put(UserInfo.ID, user.getID());
        values_time.put(UserInfo.LAST_LOGIN_TIME, user.getLastLoginTime());
        Long uid = db.insert(SqliteHelper.REC_NAME, null, values_time);
        Log.e("saveUserTime", uid + "");
        return uid;
    }

    public int updateUserTime(UserInfo user) {
        ContentValues values_time = new ContentValues();
        values_time.put(UserInfo.LAST_LOGIN_TIME, user.getLastLoginTime());
        String where = UserInfo.ID + "=?";
        String[] whereArgs = new String[] {String.valueOf(user.getID())};
        int uid = db.update(SqliteHelper.REC_NAME, values_time, where, whereArgs);
        Log.e("updateUserTime", uid + "");
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
