package com.buaa.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Window10 on 2016/3/17.
 */
public class Config {
    /**
     * 通知公告界面
     */
    public static final int HOME_FRAGMENT_INDEX = 0;
    /**
     * 通讯录界面
     */
    public static final int CATEGORY_FRAGMENT_INDEX = 1;
    /**
     * 推荐活动的界面
     */
    public static final int COLLECT_FRAGMENT_INDEX = 2;
    /**
     * 专属客户的界面
     */
    public static final int SETTING_FRAGMENT_INDEX = 3;
    public static final int IGNORE_LOWER_2M = 1;
    public static final int LOWER_SIZE = 1024 * 1024 * 2;
    /**单曲播放**/
    public static final int PLAY_SINGLE_MODE = 0;
    /**列表循环**/
    public static final int PLAY_LOOP_LIST_MODE = 1;
    /**单曲循环**/
    public static final int PLAY_LOOP_SINGLE_MODE = 2;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public Config(Context context) {
        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        editor = sp.edit();
    }


    public int getScanIngoreMode() {
        return sp.getInt("ScanIngoreMode", 1);
    }

    public void setScanIngoreMode(int mode) {
        editor.putInt("ScanIngoreMode", mode);
        editor.commit();
    }

    public int getPlayMode() {
        return sp.getInt("PlayMode", 1);
    }

    public void setPlayMode(int mode) {
        editor.putInt("PlayMode", mode);
        editor.commit();
    }
}
