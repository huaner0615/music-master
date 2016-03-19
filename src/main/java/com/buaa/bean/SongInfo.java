package com.buaa.bean;

import com.buaa.utils.DataFormater;

/**
 * Created by Window10 on 2016/3/17.
 */
public class SongInfo {
    private Integer id;
    private int totleTime;
    private String name;
    private String filePath;
    private String lrcPath;
    private String singer;
    private String miniteTime;
    private String cleanName;
    private String netFilePath;
    private String netLrcPath;
    private String netImgPath;
    private boolean vip;

    public int getTotleTime() {
        return totleTime;
    }

    public void setTotleTime(int totleTime) {
        this.totleTime = totleTime;
        this.miniteTime = DataFormater.miliToMinite(totleTime);
    }

    public String getMiniteTime() {
        return miniteTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if (name == null) {
            return;
        }
        String temp = name.contains(".") ? (name.substring(0, name.lastIndexOf("."))) : name;
        if (temp.contains("-")) {
            String[] arr = temp.split("-");
            this.singer = arr[0].trim();
            this.cleanName = arr[1].trim();
        } else {
            this.cleanName = name;
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getLrcPath() {
        return lrcPath;
    }

    public void setLrcPath(String lrcPath) {
        this.lrcPath = lrcPath;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "SongInfo{" +
                "name='" + name + '\'' +
                ", singer='" + singer + '\'' +
                ", cleanName='" + cleanName + '\'' +
                ", totleTime=" + totleTime +
                '}';
    }

    public String getCleanName() {
        return cleanName;
    }

    public String getNetFilePath() {
        return netFilePath;
    }

    public void setNetFilePath(String netFilePath) {
        this.netFilePath = netFilePath;
    }

    public String getNetLrcPath() {
        return netLrcPath;
    }

    public void setNetLrcPath(String netLrcPath) {
        this.netLrcPath = netLrcPath;
    }

    public String getNetImgPath() {
        return netImgPath;
    }

    public void setNetImgPath(String netImgPath) {
        this.netImgPath = netImgPath;
    }

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }
}
