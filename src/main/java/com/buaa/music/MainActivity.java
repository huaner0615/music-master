package com.buaa.music;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.buaa.adapter.SongListAdapter;
import com.buaa.bean.Lyric;
import com.buaa.bean.LyricLine;
import com.buaa.bean.SongInfo;
import com.buaa.listener.FindFileProgressListener;
import com.buaa.music.service.IMusicService;
import com.buaa.listener.MusicListener;
import com.buaa.utils.DataFormater;
import com.buaa.utils.L;
import com.buaa.utils.SDCardAccessor;
import com.buaa.view.LrcView;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, MusicListener {

    private Button bt_play;
    private Button bt_pause;
    private IMusicService musicService;
    private Context context;
    private Button bt_replay;
    private SeekBar sk_progress;
    private SongInfo currentSong;
    private TextView tv_time;
    private MyHandler handler;
    private Button bt_updateSongList;
    private TextView tv_updateList;
    private ListView lv_songlist;
    private List<SongInfo> list;
    private Button bt_previous_song;
    private Button bt_next_song;
    private Lyric lyric;
    private TextView tv_lyric;
    private LrcView lrcView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        handler = new MyHandler(this);
        initView();//初始化view
        initService();//初始化service




    }

    private void initService() {
        Intent intent = new Intent("com.buaa.music.musicservice");
        intent.setPackage("com.buaa.music");
        startService(intent);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                L.d("musicService:" + "onServiceConnected");
                musicService = (IMusicService) service;
                musicService.setMusicListener(MainActivity.this);
                list = musicService.getSongList();
                setSongListData();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, BIND_AUTO_CREATE);
    }

    private void setSongListData() {

        SongListAdapter songListAdapter = new SongListAdapter(context,list);
        lv_songlist.setAdapter(songListAdapter);
        lv_songlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SongInfo songInfo = (SongInfo) parent.getItemAtPosition(position);
                musicService.playMusic(songInfo);
            }
        });
    }

    private void initView() {
        bt_play = (Button) findViewById(R.id.bt_play);
        bt_pause = (Button) findViewById(R.id.bt_pause);
        bt_replay = (Button) findViewById(R.id.bt_replay);
        sk_progress = (SeekBar) findViewById(R.id.sk_progress);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_updateList = (TextView) findViewById(R.id.tv_updateList);
        bt_updateSongList = (Button) findViewById(R.id.bt_updateSongList);
        lv_songlist = (ListView) findViewById(R.id.lv_songlist);
        bt_previous_song = (Button) findViewById(R.id.bt_previous_song);
        bt_next_song = (Button) findViewById(R.id.bt_next_song);
        tv_lyric = (TextView) findViewById(R.id.tv_lyric);
        lrcView = (LrcView) findViewById(R.id.lrcShowView);
        bt_play.setOnClickListener(this);
        bt_previous_song.setOnClickListener(this);
        bt_next_song.setOnClickListener(this);
        bt_pause.setOnClickListener(this);
        bt_replay.setOnClickListener(this);
        bt_updateSongList.setOnClickListener(this);
        sk_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                L.i("onProgressChanged:" + fromUser);
                if (fromUser) {
                    int progress1 = seekBar.getProgress();
                    musicService.setProgress(progress1);
                    if(lyric!=null){
                        lyric.seekTo(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_play:
                //musicService.playMusic();
                break;
            case R.id.bt_pause:
                musicService.pauseMusic();
                break;
            case R.id.bt_replay:
                musicService.replayMusic();
                break;
            case R.id.bt_updateSongList:
                updateSongList();
                break;
            case R.id.bt_previous_song:
                musicService.palyPrevious();
                break;
            case R.id.bt_next_song:
                musicService.playNext();
                break;

        }
    }

    /**
     * 更新播放列表
     */
    private void updateSongList() {
        final Bundle bundle = new Bundle();
        musicService.updateSongList(new FindFileProgressListener() {
            @Override
            public void onFindProgressChange(int totleSongs, String nowPath) {
                Message message = Message.obtain();
                bundle.putString("nowPath", nowPath);
                bundle.putInt("totleSongs", totleSongs);
                message.setData(bundle);
                message.what = MyHandler.FIND_FILE_UPDATE;
                handler.sendMessage(message);
                L.i("totleSongs:" + totleSongs + "    nowPath:" + nowPath);
            }

            @Override
            public void onFindComplete(List<SongInfo> list) {
                Message message = Message.obtain();
                message.obj = list;
                message.what = MyHandler.FIND_COMPLETE;
                handler.sendMessage(message);
            }
        });
    }

    /**
     * 当播放的进度改变时调用
     * @param now
     */
    @Override
    public void onProgressChange(int now) {
        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putInt("progress", now);

        if(lyric!=null){
            LyricLine lyricLine = lyric.getLyricLine(now);
            if(lyricLine!=null){
                bundle.putSerializable("LyricLine", lyricLine);
                lyric.log();
                //L.i("currentPosition: now:" + lyricLine.getTime()+"   "+lyricLine.getStr());
            }
        }
        message.what = MyHandler.PROGRESS_UPDATE;
        message.setData(bundle);
        handler.sendMessage(message);
        //L.i("currentPosition: nowMinite:" + DataFormater.miliToMinite(now));
    }

    /**
     * 当播放的歌曲改变时调用
     * @param songInfo 当前播放的歌曲
     */
    @Override
    public void onSongChange(SongInfo songInfo) {
        currentSong = songInfo;
        sk_progress.setMax(currentSong.getTotleTime());
        lyric = SDCardAccessor.getLyric(songInfo);
        L.i("onSongChange:" + songInfo);
    }

    /**
     * 当歌曲的播放状态改变时调用
     * @param start
     */
    @Override
    public void onStateChange(boolean start) {
        L.i("onStateChange start:" + start);
    }

    @Override
    public void onSongListChange(List<SongInfo> list) {

    }

    @Override
    protected void onDestroy() {
        L.i("MainActivity Destroy");
        musicService.clearListener(MainActivity.this);
        super.onDestroy();
    }

    /**
     * 处理子线程发来的Message
     */

    private static class MyHandler extends Handler {
        public static final int PROGRESS_UPDATE = 0;
        public static final int FIND_FILE_UPDATE = 1;
        public static final int FIND_COMPLETE = 2;
        WeakReference<MainActivity> weakReference;

        public MyHandler(MainActivity mainActivity) {
            weakReference = new WeakReference<MainActivity>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity mainActivity = weakReference.get();
            switch (msg.what) {
                case PROGRESS_UPDATE:
                    Bundle bundle1 = msg.getData();
                    int now = bundle1.getInt("progress");
                    LyricLine lyricLine = (LyricLine) bundle1.getSerializable("LyricLine");
                    if(lyricLine!=null){
                        mainActivity.tv_lyric.setText(lyricLine.getStr());
                        mainActivity.lrcView.setmLrcList(mainActivity.lyric, lyricLine);

//                        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f,1.0f);
//                        alphaAnimation.setDuration(1000);
//                        alphaAnimation.setRepeatMode(Animation.REVERSE);
                        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0.1f,Animation.RELATIVE_TO_SELF,0);
                        translateAnimation.setDuration(500);
                        translateAnimation.setFillAfter(false);
                        mainActivity.lrcView.setAnimation(translateAnimation);
                        mainActivity.lrcView.invalidate();
                        //mainActivity.lrcView.setAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.alpha_z));
                    }
                    String nowMinite = DataFormater.miliToMinite(now);
                    String totleMinite = mainActivity.currentSong.getMiniteTime();
                    mainActivity.sk_progress.setProgress(now);
                    mainActivity.tv_time.setText(nowMinite + "/" + totleMinite);
                    break;
                case FIND_FILE_UPDATE:
                    Bundle bundle2 = msg.getData();
                    mainActivity.tv_updateList.setText("找到" + bundle2.getInt("totleSongs") + "首歌曲，\n正在扫描目录\n:" + bundle2.getString("nowPath"));
                    break;
                case FIND_COMPLETE:
                    L.i("FIND_COMPLETE");
                    mainActivity.list = (List<SongInfo>) msg.obj;
                    mainActivity.setSongListData();
                    break;
            }

        }
    }
}
