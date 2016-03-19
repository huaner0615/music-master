package com.buaa.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Window10 on 2016/3/17.
 */
public class LyricLine implements Comparable<LyricLine>,Serializable{
    private int time;
    private String str;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    @Override
    public int compareTo(LyricLine another) {
        return this.getTime() - another.getTime();
    }
}
