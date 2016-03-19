package com.buaa.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.buaa.bean.SongInfo;
import com.buaa.utils.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Window10 on 2016/3/17.
 */
public class SongListDao {
    private SongListHelper songListHelper;

    public SongListDao(Context context) {
        this.songListHelper = new SongListHelper(context);
    }

    public void deleteAll() {
        SQLiteDatabase db = songListHelper.getReadableDatabase();
        db.delete("songlist", null, null);
        db.close();
    }

    public void saveSongInfo(SongInfo songInfo) {
        SQLiteDatabase db = songListHelper.getReadableDatabase();
        ContentValues values = getContentValues(songInfo);
        db.insert("songlist", null, values);
        db.close();
    }

    public void saveSongInfo(List<SongInfo> list) {
        SQLiteDatabase db = songListHelper.getReadableDatabase();
        for (SongInfo songInfo : list) {
            ContentValues values = getContentValues(songInfo);
            db.insert("songlist", null, values);
        }
        db.close();
    }

    public List<SongInfo> getAllSongs() {
        List<SongInfo> list = new ArrayList<>();
        SQLiteDatabase db = songListHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from songlist",null);
        if(cursor!=null && cursor.getCount()>0){
            while (cursor.moveToNext()){
                //_id integer primary key autoincrement,totleTime integer(10),name varchar(20),filePath varchar(100),lrcPath varchar(100),singer varchar(20)
                SongInfo songInfo = new SongInfo();
                songInfo.setId(cursor.getInt(0));
                songInfo.setTotleTime(cursor.getInt(1));
                songInfo.setName(cursor.getString(2));
                songInfo.setFilePath(cursor.getString(3));
                songInfo.setLrcPath(cursor.getString(4));
                //songInfo.setSinger(cursor.getString(5));
                list.add(songInfo);
                L.i("SongInfo:"+songInfo);
            }
        }
        cursor.close();
        db.close();
        return  list;
    }

    private ContentValues getContentValues(SongInfo songInfo) {
        if (songInfo == null) {
            return null;
        }
        ContentValues values = new ContentValues();
        values.put("totleTime", songInfo.getTotleTime());
        values.put("name", songInfo.getName());
        values.put("filePath", songInfo.getFilePath());
        values.put("lrcPath", songInfo.getLrcPath());
        values.put("singer", songInfo.getSinger());
        return values;
    }
}
