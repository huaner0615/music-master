package com.buaa.listener;

import com.buaa.bean.SongInfo;

import java.util.List;

/**
 * Created by Window10 on 2016/3/17.
 */
public interface FindFileProgressListener {
    void onFindProgressChange(int totleSongs,String nowPath);
    void onFindComplete(List<SongInfo> list);
}
