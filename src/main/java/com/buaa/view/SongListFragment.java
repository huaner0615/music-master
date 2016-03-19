package com.buaa.view;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.buaa.adapter.SongListAdapter;
import com.buaa.bean.Lyric;
import com.buaa.bean.LyricLine;
import com.buaa.bean.SongInfo;
import com.buaa.listener.MusicListener;
import com.buaa.music.MusicActivity;
import com.buaa.music.R;
import com.buaa.music.service.IMusicService;
import com.buaa.utils.DataFormater;
import com.buaa.utils.L;
import com.buaa.utils.SDCardAccessor;

import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.util.List;


/**
 * 本类为通知公告显示的类
 *
 * @author Linux
 */
public class SongListFragment extends Fragment implements MusicListener, ServiceConnection, View.OnClickListener {
    //System
    private MusicActivity activity;
    private IMusicService service;
    private MyHandler handler;
    //Music
    private List<SongInfo> list;
    private SongInfo currentSong;
    private Lyric lyric;
    //view
    private ListView songListView;
    private TextView title;
    private Button startAndPause;
    private SeekBar sk_progress;
    private Button bt_next_song;
    //flag
    private boolean started;
    private TextView tv_songname;
    private TextView playview_tv_time;
    private TextView playview_tv_word;


    /**
     * 界面创建时执行的方法
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new MyHandler(this);

    }

    /**
     * 创建View时执行的方法
     */
//	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songlist, container, false);
        return view;
    }

    /**
     * view创建完毕后执行的方法
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        L.i("SongListFragment onViewCreated");
    }

    private void initView(View view) {
        //标题
        title = (TextView) view.findViewById(R.id.songlist_title_tv);
        title.setText(R.string.fragement_songlist_title);
        //歌曲列表
        songListView = (ListView) view.findViewById(R.id.fragement_lv_songlist);
        //按钮
        startAndPause = (Button) view.findViewById(R.id.playview_bt_start_pause);
        startAndPause.setOnClickListener(this);
        bt_next_song = (Button) view.findViewById(R.id.playview_bt_next);
        bt_next_song.setOnClickListener(this);
        //歌曲名
        tv_songname = (TextView) view.findViewById(R.id.playview_tv_name);
        playview_tv_time = (TextView) view.findViewById(R.id.playview_tv_time);
        playview_tv_word = (TextView) view.findViewById(R.id.playview_tv_word);
        //seekbar
        sk_progress = (SeekBar) view.findViewById(R.id.playview_sb_seekbar);
        sk_progress.setActivated(false);
        sk_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int progress1 = seekBar.getProgress();
                    service.setProgress(progress1);
                    if (lyric != null) {
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.activity = (MusicActivity) getActivity();
        L.i("SongListFragment onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        L.i("SongListFragment onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        L.i("SongListFragment onResume");
    }

    @Override
    public void onProgressChange(int now) {
        if(currentSong==null){
            return;
        }
        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putInt("progress", now);

        if(lyric!=null){
            LyricLine lyricLine = activity.getLyricLine();
            if(lyricLine!=null){
                bundle.putSerializable("LyricLine", lyricLine);
                //lyric.log();
            }
        }
        message.what = MyHandler.PROGRESS_UPDATE;
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @Override
    public void onSongChange(SongInfo songInfo) {
        currentSong = songInfo;
        started = true;
        currentSong = songInfo;
        sk_progress.setMax(currentSong.getTotleTime());
        lyric = activity.getLyric();
        handler.sendEmptyMessage(MyHandler.SONG_CHANGE);
    }

    @Override
    public void onStateChange(boolean start) {
        started = start;
    }

    @Override
    public void onSongListChange(List<SongInfo> list) {
        this.list = list;
        handler.sendEmptyMessage(MyHandler.SONG_LIST_CHANGE);
    }

    private void setListViewData() {
        SongListAdapter adapter = new SongListAdapter(activity, list,currentSong);
        songListView.setAdapter(adapter);
        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SongInfo songInfo = (SongInfo) parent.getItemAtPosition(position);
                service.playMusic(songInfo);
            }
        });
    }

    @Override
    public void onDestroy() {
        service.clearListener(this);
        super.onDestroy();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.service = (IMusicService) service;
        this.service.getSongInfo();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playview_bt_start_pause:
                if (currentSong == null) {
                    service.playNext();
                } else {
                    if (started) {
                        service.pauseMusic();
                        startAndPause.setBackgroundResource(R.drawable.play_start);
                    } else {
                        service.replayMusic();
                        startAndPause.setBackgroundResource(R.drawable.play_stop);
                    }
                    sk_progress.setActivated(started);
                }
                break;
            case R.id.playview_bt_next:
                service.playNext();
                break;

        }
    }

    private static class MyHandler extends Handler {
        public static final int SONG_LIST_CHANGE = 0;
        public static final int FIND_FILE_UPDATE = 1;
        public static final int FIND_COMPLETE = 2;
        public static final int PROGRESS_UPDATE = 3;
        public static final int SONG_CHANGE = 4;
        WeakReference<SongListFragment> weakReference;

        public MyHandler(SongListFragment fragment) {
            weakReference = new WeakReference<SongListFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            SongListFragment fragment = weakReference.get();
            switch (msg.what) {
                case PROGRESS_UPDATE:
                    Bundle bundle1 = msg.getData();
                    int now = bundle1.getInt("progress");
                    LyricLine lyricLine = (LyricLine) bundle1.getSerializable("LyricLine");
                    if(lyricLine!=null){
                        fragment.playview_tv_word.setText(lyricLine.getStr());
                    }
                    String nowMinite = DataFormater.miliToMinite(now);
                    String totleMinite = fragment.currentSong.getMiniteTime();
                    fragment.sk_progress.setProgress(now);
                    fragment.playview_tv_time.setText(nowMinite + "/" + totleMinite);
                    break;
//                case FIND_FILE_UPDATE:
//                    Bundle bundle2 = msg.getData();
//                    musicActivity.tv_updateList.setText("找到" + bundle2.getInt("totleSongs") + "首歌曲，\n正在扫描目录\n:" + bundle2.getString("nowPath"));
//                    break;
                case SONG_LIST_CHANGE:
                    fragment.setListViewData();
                    break;
                case  SONG_CHANGE:
                    fragment.setListViewData();
                    fragment.playview_tv_time.setText(fragment.currentSong.getMiniteTime());
                    fragment.tv_songname.setText(fragment.currentSong.getCleanName());
                    fragment.songListView.setSelection(fragment.list.indexOf(fragment.currentSong));//滚动到正在播放的位置
                    fragment.playview_tv_word.setText(fragment.currentSong.getName());
                    break;
            }

        }
    }
}
