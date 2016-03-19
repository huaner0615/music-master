package com.buaa.listener;

import com.buaa.bean.SongInfo;

import java.util.List;

/**
 * Created by Window10 on 2016/3/17.
 */
public interface MusicListener {
    void onProgressChange(int now);
    void onSongChange(SongInfo songInfo);
    void onStateChange(boolean start);
    void onSongListChange(List<SongInfo> list);
}
