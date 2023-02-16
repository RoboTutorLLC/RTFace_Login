package com.example.iris.login1;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by Iris on 16/7/20.
 */
    /*
    TODO update database during enrollment only once student has picked a photo and icon AND has entered Robotutor. Otherwise should store a "provisional" enrollment
    TODO migrate to JSON in future for sync convenience*/

public class SqliteHelper extends SQLiteOpenHelper {

    public static final String TB_NAME = "users";
    public static final String REC_NAME = "last_login";
    public static final String TB_NAME_BACKUP = "tb_name_backup";
    public static final String REC_NAME_BACKUP = "rec_name_backup";


    public SqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{TB_NAME_BACKUP});
        if (cursor.getCount() > 0){
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS "+
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
            db.execSQL("INSERT INTO " + TB_NAME + " SELECT * FROM " + TB_NAME_BACKUP);
        }
        else{
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS "+
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
        }
        cursor.close();

        Cursor cursor_ = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{REC_NAME_BACKUP});
        if (cursor.getCount() > 0){
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS "+
                            REC_NAME + "(" +
                            UserInfo.ID + " integer," +
                            UserInfo.LAST_LOGIN_TIME + " varchar" +
                            ")"
            );
            db.execSQL("INSERT INTO " + REC_NAME + " SELECT * FROM " + REC_NAME_BACKUP);
        }
        else{
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS "+
                            REC_NAME + "(" +
                            UserInfo.ID + " integer," +
                            UserInfo.LAST_LOGIN_TIME + " varchar" +
                            ")"
            );
        }
        cursor_.close();

        Log.e("database", "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("CREATE TABLE tb_name_backup AS SELECT * FROM " + TB_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);

        db.execSQL("CREATE TABLE rec_name_backup AS SELECT * FROM " + REC_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + REC_NAME);

        onCreate(db);
        Log.e("database", "onUpgrade");
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
