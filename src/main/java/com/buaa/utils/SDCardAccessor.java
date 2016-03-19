package com.buaa.utils;

import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

import com.buaa.bean.Lyric;
import com.buaa.bean.LyricLine;
import com.buaa.bean.SongInfo;
import com.buaa.listener.FindFileProgressListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Window10 on 2016/3/17.
 */
public class SDCardAccessor {
    private static File lyricDir;
    private static String dir;

    private SDCardAccessor() {

    }

    static {
        lyricDir = new File(getRootPath() + "/BuaaMusic/Lyric");
        if (!lyricDir.exists()) {
            lyricDir.mkdirs();
        }
    }

    public static String getRootPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    public static List<File> findAllMp3() {
        return findAllMp3(null);
    }

    public static List<File> findAllMp3(final FindFileProgressListener findFileProgressListener) {
        File root = new File(getRootPath());
        final List<File> list = new ArrayList<>();
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                int count = list.size();
                if (findFileProgressListener != null) {
                    findFileProgressListener.onFindProgressChange(count, dir);
                }
            }
        };
        timer.schedule(timerTask, 100, 50);
        getDirectoryMp3(root, list);
        timerTask.cancel();
        timer.cancel();
        return list;
    }

    private static void getDirectoryMp3(File directory, List<File> list) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            //L.e(file.getAbsolutePath());
            if (file.isDirectory()) {
                dir = file.getAbsolutePath();
                getDirectoryMp3(file, list);
            } else {
                if (file.getName().endsWith(".mp3") || file.getName().endsWith(".MP3")) {

                    list.add(file);
                }
            }
        }
    }

    /**
     * 通过路径获取歌曲信息
     *
     * @param path
     * @return
     */
    public static SongInfo getSongInfo(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        return getSongInfo(file);
    }

    /**
     * 通过文件获取歌曲信息
     *
     * @param file
     * @return
     */
    public static SongInfo getSongInfo(File file) {

        SongInfo songInfo = new SongInfo();
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(file.getAbsolutePath());
            String totleTime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            songInfo.setTotleTime(totleTime == null ? 0 : Integer.parseInt(totleTime));
            songInfo.setName(getSongNameByPath(file.getPath()));
            songInfo.setSinger("");
            songInfo.setFilePath(file.getAbsolutePath());
            return songInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        songInfo.setName(getSongNameByPath(file.getPath()));
        songInfo.setFilePath(file.getAbsolutePath());
        return songInfo;
    }

    /**
     * 通过路径获取歌曲名
     *
     * @param path
     * @return
     */
    private static String getSongNameByPath(String path) {
        int start = path.lastIndexOf("/");
        return path.substring(start + 1, path.length());
    }

    public static Lyric getLyric(SongInfo songInfo) {
        String name = songInfo.getName();
        String nameWithOutExtend = name.substring(0, name.lastIndexOf("."));
        L.i("nameWithOutExtend:" + nameWithOutExtend);
        File file = new File(lyricDir, nameWithOutExtend + ".lrc");
        if (file.exists()) {
            Lyric lyric = new Lyric(songInfo);
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String result = "";
                while ((result = br.readLine()) != null) {
                    if (result.matches("\\[\\d\\d:\\d\\d.\\d\\d\\].*")) {//[00:04.59]搭一辆车去远方
                        LyricLine lyricLine = parseStringToLyricLine(result);
                        lyric.addLyricLine(lyricLine);
                    }
                    //L.i("result:"+result);
                }
                br.close();
                lyric.commit();
                return lyric;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static LyricLine parseStringToLyricLine(String result) {
        LyricLine lyricLine = new LyricLine();
        String timeString = result.substring(1, result.indexOf("]"));
        String word = result.substring(result.indexOf("]") + 1, result.length());
        int time = getTimeFromString(timeString);
        lyricLine.setStr(word);
        lyricLine.setTime(time);
        return lyricLine;

    }

    private static int getTimeFromString(String timeString) {
        int minite = Integer.parseInt(timeString.substring(0,2));//00:04.59
        int second = Integer.parseInt(timeString.substring(3,5));
        int mili = Integer.parseInt(timeString.substring(6,8));
        return minite*60*1000+second*1000+mili;
    }

}
