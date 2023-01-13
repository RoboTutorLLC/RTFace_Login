package com.example.iris.login1;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.iris.login1.Common.ANIMAL_NAMES_ENG;
import static com.example.iris.login1.Common.ANIMAL_NAMES_SWA;
import static com.example.iris.login1.Common.FACE_LOGIN_PATH;

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

    public boolean checkNullOrMissing(UserInfo user){
        if(user.getUserIcon() == null || user.getProfileIcon() == null || user.getGender() == null ||
                user.getUserVideo() == null || user.getRecordTime() == null){
            return true;
        }

        if(!Arrays.asList(ANIMAL_NAMES_ENG).contains(user.getProfileIcon().toLowerCase()) &&
                !Arrays.asList(ANIMAL_NAMES_SWA).contains(user.getProfileIcon().toLowerCase())){
            return true;
        }

        File folder = new File(FACE_LOGIN_PATH);

        File[] fileList = folder.listFiles();

        ArrayList<String> fileNames = new ArrayList<String>();

        for (File f: fileList){
            fileNames.add(f.getName());
        }

        String iconpath = user.getUserIcon().split(FACE_LOGIN_PATH + "/")[1];
        String videopath = user.getUserVideo().split(FACE_LOGIN_PATH + "/")[1];

        return !fileNames.contains(iconpath) || !fileNames.contains(videopath);
    }

    public List<UserInfo> getUserList(){
        List<UserInfo> userList = new ArrayList<UserInfo>();
        Cursor cursor = db.query(SqliteHelper.TB_NAME, null, null, null, null, null, UserInfo.ID + " DESC");
        cursor.moveToFirst();
        int count = 0;
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
            if(!checkNullOrMissing(user)) {
                userList.add(user);
            } else {
                this.deletUserInfo(String.valueOf(user.getID()));
                Log.e("getUserInfo", "deleted User with ID = " + String.valueOf(user.getID()));
            }
            count += 1;
            cursor2.close();
            cursor.moveToNext();
        }
        Collections.sort(userList, new LoginTimeComparator());
        Log.e("getUserInfo", "count = " + count + ", retrieved = " + userList.size());
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

    public int updateIconVideo(UserInfo user){

        ContentValues values_time = new ContentValues();
        values_time.put(UserInfo.USERICON, user.getUserIcon());
        values_time.put(UserInfo.USERVIDEO, user.getUserVideo());
        values_time.put(UserInfo.RECORDTIME, user.getRecordTime());

        String where = UserInfo.ID + "=?";
        String[] whereArgs = new String[] {String.valueOf(user.getID())};
        int uid = db.update(SqliteHelper.TB_NAME, values_time, where, whereArgs);
        Log.e("updateIconVideo", uid + "");
        Log.e("recen table uiconvid: ", this.getTableAsString(SqliteHelper.REC_NAME));
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
    public String[] getImageOrder() {
        Log.d("DataHelper","getImageOrder called");

        //Cursor values = db.rawQuery("SELECT Count(_id), profileIcon from users Group by profileicon order By Count(_id) ASC", null);


        db.execSQL("CREATE TABLE IF NOT EXISTS animal_names (" +
                "icon varchar)");

        Cursor temp = db.rawQuery("SELECT * from animal_names", null);

        int tempnu = temp.getCount();


        boolean tempTest = (tempnu < 40);
        if(temp.getCount() < 40){
            for (String animal:
                    ANIMAL_NAMES_ENG) {
                ContentValues values = new ContentValues();
                values.put("icon", animal);
                db.insert("animal_names", null, values);
            }
        }


        //nested query
        @SuppressLint("Recycle") Cursor values = db.rawQuery("SELECT icon from animal_names a ORDER BY (SELECT Count(_id) from users u WHERE (u.profileIcon = a.icon)) DESC", null);


        Log.d("DataHelper" , "sql query: "+values.toString());
        ArrayList<String> animal_names = new ArrayList<String>();
        ArrayList<String> animal_names_swa = new ArrayList<String>();

        values.moveToFirst();

        while(!values.isAfterLast()){
            @SuppressLint("Range") String val = values.getString(values.getColumnIndex("icon"));

            if(val.toLowerCase(Locale.ROOT).equals("mbuni")){
                animal_names.add("ostrich");

            }
            else {
                animal_names.add(val.toLowerCase(Locale.ROOT));
            }
            try {
                String swa_name = Common.build_conn().get(val.toLowerCase(Locale.ROOT));
                if(swa_name != null) {
                    animal_names_swa.add(swa_name);
                }
                if(val.toLowerCase(Locale.ROOT).equals("mbuni")){
                    animal_names_swa.add("mbuni");
                }
            }
            catch (Exception e){
                Log.e("DataHelper", e.toString());
                //animal_names_swa.add(val.toLowerCase(Locale.ROOT));
            }
            values.moveToNext();
        }
        if (animal_names.size() == 0){
            return null;
        }
        Log.e("DataHelper", String.valueOf(animal_names.size()));

//
//        for (int i = 0; i < ANIMAL_NAMES_ENG.length; i++) {
//
//            boolean is_in_list = true;
//
//            for (int k = 0; k < animal_names.size(); k++) {
//
//                if (animal_names.get(k).equals(ANIMAL_NAMES_ENG[i])) {
//                    is_in_list = false;
//                    break;
//                }
//            }
//            if(is_in_list){
//                animal_names.add(0,ANIMAL_NAMES_ENG[i]);
//            }
//        }
//
//        for (int i = 0; i < ANIMAL_NAMES_SWA.length; i++) {
//
//            boolean is_in_list = true;
//
//            for (int k = 0; k < animal_names_swa.size(); k++) {
//                Log.println(Log.ERROR,"DataHelper", String.valueOf(k));
//                String swa_name = animal_names_swa.get(k);
//
//                if (swa_name.equals(ANIMAL_NAMES_SWA[i])) {
//                    is_in_list = false;
//                    break;
//                }
//            }
//            if(is_in_list){
//                animal_names_swa.add(0,ANIMAL_NAMES_SWA[i]);
//            }
//        }

        for (int i = 0; i < ANIMAL_NAMES_SWA.length; i++) {

            ANIMAL_NAMES_SWA[i] = animal_names_swa.get(i);
            ANIMAL_NAMES_ENG[i] = animal_names.get(i);

            String tempName = animal_names_swa.get(i);

            Pair<Integer, Integer> animalData = Common.ANIMALS_SWA.get(tempName);


            if (animalData == null) {
                //animalData = Common.ANIMALS_ENG.get(tempName);
            }
            Common.ANIMAL_PATHS[i] = animalData.first;
            Common.ANIMAL_SOUNDS[i] = animalData.second;

        }

        String[] response = new String[animal_names.size()];
        response = animal_names.toArray(response);
        return response;
    }
    +


}
