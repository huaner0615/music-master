package com.buaa.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Window10 on 2016/3/17.
 */
public class SongListHelper extends SQLiteOpenHelper {
    public SongListHelper(Context context) {
        super(context, "mymusic", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table songlist (_id integer primary key autoincrement,totleTime integer(10),name varchar(20),filePath varchar(100),lrcPath varchar(100),singer varchar(20))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
