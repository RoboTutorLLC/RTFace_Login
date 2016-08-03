package com.example.iris.login1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Iris on 16/7/20.
 */

public class SqliteHelper extends SQLiteOpenHelper {
    public static final String TB_NAME = "users";
    public SqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS "+
                        TB_NAME + "(" +
                        UserInfo.ID + " integer," +
                        UserInfo.USERICON + " varchar," +
                        UserInfo.USERVIDEO + " varchar" +
                        ")"
        );
        Log.e("database", "onCreate");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS" + TB_NAME);
        onCreate(db);
        Log.e("database", "onUpgrade");
    }

    public void updateColumn(SQLiteDatabase db, String oldColumn, String newColumn, String typeColumn){
        try {
            db.execSQL(
                    "ALTER TABLE" +
                            TB_NAME + "CHANGE" +
                            oldColumn + " " + newColumn +
                            " " + typeColumn
            );
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
