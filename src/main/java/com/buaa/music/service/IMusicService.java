package com.buaa.music.service;

import com.buaa.bean.SongInfo;
import com.buaa.listener.FindFileProgressListener;
import com.buaa.listener.MusicListener;

import java.util.List;

/**
 * Created by Window10 on 2016/3/17.
 */
public interface IMusicService {
    /**
     * 播放一首歌曲
     * @param songInfo
     */
    void playMusic(SongInfo songInfo);

    /**
     * 暂停后，重新播放
     */
    void replayMusic();

    /**
     * 暂停播放
     */
    void pauseMusic();

    /**
     * 停止播放
     */
    void stopMusic();

    /**
     * 设置播放状态等信息改变后监听的对象
     * @param musicListener
     */
    void setMusicListener(MusicListener musicListener);

    /**
     * 清除一个监听对象
     * @param musicListener
     */
    void clearListener(MusicListener musicListener);

    /**
     * 设置播放尽速
     * @param progress
     */
    void setProgress(int progress);

    /**
     * 更新播放列表
     * @param listener
     */
    void updateSongList(FindFileProgressListener listener);

    /**
     * 获取播放列表
     * @return
     */
    List<SongInfo> getSongList();

    /**
     * 播放下一首
     */
    void playNext();

    /**
     * 播放前一首
     */
    void palyPrevious();

    void getSongInfo();
}
