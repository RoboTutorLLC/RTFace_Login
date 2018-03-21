package com.example.iris.login1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by Iris on 16/7/20.
 */
    /*TODO create column to store icon (although it seems like it's there already so not sure what icon is in this sense)
    TODO update database during enrollment only once student has picked a photo and icon AND has entered Robotutor. Otherwise should store a "provisional" enrollment
    TODO migrate to JSON in future for sync convenience*/

public class SqliteHelper extends SQLiteOpenHelper {

    public static final String TB_NAME = "users";
    public static final String REC_NAME = "last_login";


    public SqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
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
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS "+
                        REC_NAME + "(" +
                        UserInfo.ID + " integer," +
                        UserInfo.LAST_LOGIN_TIME + " varchar" +
                        ")"
        );
        Log.e("database", "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
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
