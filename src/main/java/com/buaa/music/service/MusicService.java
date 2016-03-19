package com.buaa.music.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.buaa.bean.SongInfo;
import com.buaa.database.SongListDao;
import com.buaa.listener.FindFileProgressListener;
import com.buaa.listener.MusicListener;
import com.buaa.utils.Config;
import com.buaa.utils.L;
import com.buaa.utils.SDCardAccessor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    private Context context;
    private Config config;
    private MediaPlayer player;
    private SongListDao songListDao;
    /**
     * 歌曲是否处于暂停状态
     **/
    private boolean started;
    /**
     * 歌曲监听对象列表
     **/
    private Set<MusicListener> musicListenerSet;
    /**
     * 进度更新任务计划
     **/
    private Timer timer;
    /**
     * 当前播放的歌曲
     **/
    private SongInfo currentSong;
    private TimerTask timerTask;
    private List<SongInfo> list;

    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        musicListenerSet = new HashSet<MusicListener>();
        timer = new Timer();
        context = this;
        config = new Config(context);
        songListDao = new SongListDao(context);
        list = songListDao.getAllSongs();
        noticeSongListChange();
        L.i("MusicService onCreate");
        for(SongInfo songInfo : list){
            L.i(songInfo.toString());
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        L.i("MusicService onBind");
        return new MusicBinder();
    }

    private class MusicBinder extends Binder implements IMusicService {

        @Override
        public void playMusic(SongInfo songInfo) {
            L.i("MusicService playMusic");
            try {

                if (songInfo == null) {
                    //TODO 如果歌曲失效
                }
                player.reset();
                player.setDataSource(songInfo.getFilePath());
                player.prepare();
                player.start();
                started = true;
                songInfo.setTotleTime(player.getDuration());
                currentSong = songInfo;
                noticeMusicChange();
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        int playMode = config.getPlayMode();
                        switch (playMode) {
                            case Config.PLAY_LOOP_LIST_MODE:
                                playNext();
                                break;
                            case Config.PLAY_LOOP_SINGLE_MODE:
                                playMusic(currentSong);
                                break;
                            case Config.PLAY_SINGLE_MODE:
                                mp.stop();
                                break;
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        public void replayMusic() {
            L.i("MusicService replayMusic");
            if (!started) {
                player.start();
                started = !started;
                noticeProgressChange();
                noticeStateChange();
                return;
            }
        }

        @Override
        public void pauseMusic() {
            L.i("MusicService pauseMusic");
            if (started) {
                timerTask.cancel();
                player.pause();
                started = !started;
                noticeStateChange();
            }
        }

        @Override
        public void stopMusic() {
            L.i("MusicService stopMusic");
            timerTask.cancel();
            player.stop();
            started = false;
            noticeStateChange();
        }

        @Override
        public void setMusicListener(MusicListener musicListener) {
            musicListenerSet.add(musicListener);
        }

        @Override
        public void clearListener(MusicListener musicListener) {
            musicListenerSet.remove(musicListener);
        }

        @Override
        public void setProgress(int progress) {
            player.seekTo(progress);
        }

        @Override
        public void updateSongList(final FindFileProgressListener listener) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<File> allMp3 = SDCardAccessor.findAllMp3(listener);
                    //去掉小于2M的MP3
                    if (config.getScanIngoreMode() == Config.IGNORE_LOWER_2M) {
                        removeLower2mMp3(allMp3);
                    }
                    //将文件转为SongInfo
                    List<SongInfo> list = new ArrayList<SongInfo>();
                    for (File file : allMp3) {
                        list.add(SDCardAccessor.getSongInfo(file));
                    }
                    //保存数据库
                    SongListDao songListDao = new SongListDao(context);
                    songListDao.deleteAll();
                    songListDao.saveSongInfo(list);
                    MusicService.this.list = list;
                    listener.onFindComplete(list);
                    //通知歌曲列表改变
                    noticeSongListChange();
                }


            }).start();
        }

        @Override
        public List<SongInfo> getSongList() {

            return list;
        }

        @Override
        public void playNext() {
            if (currentSong == null) {
                playMusic(list.get(0));
            }
            int now = list.indexOf(currentSong);
            if (now == list.size() - 1) {
                playMusic(list.get(0));
                return;
            }
            playMusic(list.get(now + 1));
        }

        @Override
        public void palyPrevious() {
            if (currentSong == null) {
                playMusic(list.get(0));
            }
            int now = list.indexOf(currentSong);
            if (now == 0) {
                playMusic(list.get(0));
                return;
            }
            playMusic(list.get(now - 1));
        }

        @Override
        public void getSongInfo() {
            noticeSongListChange();
            noticeMusicChange();
            noticeStateChange();
        }
    }

    /**
     * 删除小于2M的MP3
     *
     * @param allMp3
     */
    private void removeLower2mMp3(List<File> allMp3) {
        Iterator<File> iterator = allMp3.iterator();
        while (iterator.hasNext()) {
            File file = iterator.next();
            if (file.length() < Config.LOWER_SIZE) {
                iterator.remove();
            }
        }
    }

    /**
     * 通知注册监听的对象，歌曲播放进度改变了
     */

    private void noticeProgressChange() {
        if (timerTask != null) {
            timerTask.cancel();
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
                //L.i("updateProgress run...");
                Iterator<MusicListener> iterator = musicListenerSet.iterator();
                while (iterator.hasNext()) {
                    MusicListener ml = iterator.next();
                    int currentPosition = player.getCurrentPosition();
                    ml.onProgressChange(currentPosition);
                }
            }
        };
        timer.schedule(timerTask, 100, 500);
    }

    /**
     * 通知注册监听的对象，歌曲播放状态改变了
     */
    private void noticeStateChange() {
        Iterator<MusicListener> iterator = musicListenerSet.iterator();
        while (iterator.hasNext()) {
            MusicListener ml = iterator.next();
            ml.onStateChange(started);
        }
    }

    /**
     * 通知注册监听的对象，歌曲改变了
     */
    private void noticeMusicChange() {
        if(currentSong ==null){
            return;
        }
        Iterator<MusicListener> iterator = musicListenerSet.iterator();
        while (iterator.hasNext()) {
            MusicListener ml = iterator.next();
            ml.onSongChange(currentSong);
        }
        noticeProgressChange();
        noticeStateChange();
    }

    private void noticeSongListChange() {
        Iterator<MusicListener> iterator = musicListenerSet.iterator();
        while (iterator.hasNext()) {
            MusicListener ml = iterator.next();
            ml.onSongListChange(list);
        }
    }

    private SongInfo getSongInfoByPath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        SongInfo songInfo = new SongInfo();
        songInfo.setFilePath(path);
        songInfo.setName(getSongNameByPath(path));
        return songInfo;
    }

    private String getSongNameByPath(String path) {
        int start = path.lastIndexOf("/");
        return path.substring(start + 1, path.length());
    }

}
