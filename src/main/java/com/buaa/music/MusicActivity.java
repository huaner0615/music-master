package com.buaa.music;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.buaa.bean.Lyric;
import com.buaa.bean.LyricLine;
import com.buaa.bean.SongInfo;
import com.buaa.listener.FindFileProgressListener;
import com.buaa.listener.MusicListener;
import com.buaa.music.service.IMusicService;
import com.buaa.utils.Config;
import com.buaa.utils.SDCardAccessor;
import com.buaa.view.NetSongFragment;
import com.buaa.view.LyricFragment;
import com.buaa.view.KeHuFragment;
import com.buaa.view.LrcView;
import com.buaa.view.MyTabWidget;
import com.buaa.view.SongListFragment;
import com.buaa.view.SlidingMenu;

import java.lang.ref.WeakReference;
import java.util.List;


public class MusicActivity extends Activity implements
        MyTabWidget.OnTabSelectedListener, View.OnClickListener ,MusicListener{
    //system
    private MyHandler handler;
   //layout
    public static int mIndex = Config.HOME_FRAGMENT_INDEX;
    private MyTabWidget mTabWidget;// 底部导航
    private SongListFragment songListFragment;// 主页面
    private LyricFragment lyricFragment;// 通讯录界面
    private NetSongFragment netSongFragment;// 推荐活动界面
    private KeHuFragment serverFragment;// 客户服务界面
    private FragmentManager mFragmentManager;// 界面管理器
    //service
    private IMusicService musicService;
    //song
    private List<SongInfo> list;
    private Lyric lyric;
    private LrcView lrcView;
    private SongInfo currentSong;
    //view
    private TextView setting_name;// 设置界面姓名
    private Context context;
    private View CustomView;
    private String errorInfo;
    private SlidingMenu slidMenu;
    private LyricLine lyricLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main2);
        slidMenu = (SlidingMenu) findViewById(R.id.id_menu);
        context = MusicActivity.this;
        handler = new MyHandler(this);
        mFragmentManager = getFragmentManager();
        initView();
        initEvents();
        initService();//初始化service

    }

    private void initService() {
        Intent intent = new Intent("com.buaa.music.musicservice");
        intent.setPackage("com.buaa.music");
        startService(intent);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                musicService = (IMusicService) service;
                musicService.setMusicListener(MusicActivity.this);
                list = musicService.getSongList();
                songListFragment.onServiceConnected(name, service);
                lyricFragment.onServiceConnected(name, service);
                //setSongListData();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                songListFragment.onServiceDisconnected(name);
                lyricFragment.onServiceDisconnected(name);
            }
        }, BIND_AUTO_CREATE);
    }


    private void initView() {

        mTabWidget = (MyTabWidget) findViewById(R.id.tab_widget);
        setting_name = (TextView) findViewById(R.id.setting_name);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        songListFragment = new SongListFragment();
        transaction.add(R.id.abc_layout, songListFragment);
        lyricFragment = new LyricFragment();
        transaction.add(R.id.abc_layout, lyricFragment);
        netSongFragment = new NetSongFragment();
        transaction.add(R.id.abc_layout, netSongFragment);
        serverFragment = new KeHuFragment();
        transaction.add(R.id.abc_layout, serverFragment);
        transaction.commitAllowingStateLoss();
    }

    private void initEvents() {
        mTabWidget.setOnTabSelectedListener(this);
        mTabWidget.setTabsDisplay(this, mIndex);
        onTabSelected(mIndex);
    }


    /**
     * 点击下边的选项卡后执行的方法
     */
    @Override
    public void onTabSelected(int index) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        hideFragments(transaction);
        switch (index) {
            case Config.HOME_FRAGMENT_INDEX:
                if (null == songListFragment) {
                    songListFragment = new SongListFragment();
                    transaction.add(R.id.abc_layout, songListFragment);
                } else {
                    transaction.show(songListFragment);
                }
                break;
            case Config.CATEGORY_FRAGMENT_INDEX:
                if (null == lyricFragment) {
                    lyricFragment = new LyricFragment();
                    transaction.add(R.id.abc_layout, lyricFragment);
                } else {
                    transaction.show(lyricFragment);
                }
                break;
            case Config.COLLECT_FRAGMENT_INDEX:
                if (null == netSongFragment) {
                    netSongFragment = new NetSongFragment();
                    transaction.add(R.id.abc_layout, netSongFragment);
                } else {
                    transaction.show(netSongFragment);
                }
                break;
            case Config.SETTING_FRAGMENT_INDEX:
                if (null == serverFragment) {
                    serverFragment = new KeHuFragment();
                    transaction.add(R.id.abc_layout, serverFragment);
                } else {
                    transaction.show(serverFragment);
                }
                break;

            default:
                break;
        }
        mIndex = index;
        transaction.commitAllowingStateLoss();

    }

    private void hideFragments(FragmentTransaction transaction) {
        if (null != songListFragment) {
            transaction.hide(songListFragment);
        }
        if (null != lyricFragment) {
            transaction.hide(lyricFragment);
        }
        if (null != netSongFragment) {
            transaction.hide(netSongFragment);
        }
        if (null != serverFragment) {
            transaction.hide(serverFragment);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("index", mIndex);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mIndex = savedInstanceState.getInt("index");
    }

    /**
     * 打开侧栏菜单
     */
    public void closeMenu() {
        slidMenu.openMenu();
    }


    @Override
    public void onClick(View v) {

    }

    public IMusicService getMusicService() {
        return musicService;
    }
    public void slideButtonClick(View view){
        switch (view.getId()){
            case R.id.slide_button_scan:{
                updateSongList();
                Toast.makeText(context, "正在扫描...", Toast.LENGTH_SHORT).show();
            }
            case R.id.slide_button_setting:{
               Intent intent = new Intent(this,SettingActivity.class);
                startActivity(intent);
            }
        }
    }
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

    @Override
    public void onProgressChange(int now) {
        if(lyric!=null){
            lyricLine = lyric.getLyricLine(now);
        }
        songListFragment.onProgressChange(now);
        lyricFragment.onProgressChange(now);
    }

    @Override
    public void onSongChange(SongInfo songInfo) {
        lyric = SDCardAccessor.getLyric(songInfo);
        songListFragment.onSongChange(songInfo);
        lyricFragment.onSongChange(songInfo);
    }

    public Lyric getLyric() {
        return lyric;
    }

    @Override
    public void onStateChange(boolean start) {
        songListFragment.onStateChange(start);
        lyricFragment.onStateChange(start);
    }

    @Override
    public void onSongListChange(List<SongInfo> list) {
        songListFragment.onSongListChange(list);
        lyricFragment.onSongListChange(list);
    }

    public LyricLine getLyricLine() {
        return lyricLine;
    }

    public void setLyricLine(LyricLine lyricLine) {
        this.lyricLine = lyricLine;
    }

    /**
     * 处理子线程发来的Message
     */

    private static class MyHandler extends Handler {
        public static final int PROGRESS_UPDATE = 0;
        public static final int FIND_FILE_UPDATE = 1;
        public static final int FIND_COMPLETE = 2;
        WeakReference<MusicActivity> weakReference;

        public MyHandler(MusicActivity mainActivity) {
            weakReference = new WeakReference<MusicActivity>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            MusicActivity musicActivity = weakReference.get();
            switch (msg.what) {
//                case PROGRESS_UPDATE:
//                    Bundle bundle1 = msg.getData();
//                    int now = bundle1.getInt("progress");
//                    LyricLine lyricLine = (LyricLine) bundle1.getSerializable("LyricLine");
//                    if(lyricLine!=null){
//                        musicActivity.tv_lyric.setText(lyricLine.getStr());
//                        musicActivity.lrcView.setmLrcList(musicActivity.lyric, lyricLine);
//
////                        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f,1.0f);
////                        alphaAnimation.setDuration(1000);
////                        alphaAnimation.setRepeatMode(Animation.REVERSE);
//                        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0.1f,Animation.RELATIVE_TO_SELF,0);
//                        translateAnimation.setDuration(500);
//                        translateAnimation.setFillAfter(false);
//                        musicActivity.lrcView.setAnimation(translateAnimation);
//                        musicActivity.lrcView.invalidate();
//                        //mainActivity.lrcView.setAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.alpha_z));
//                    }
//                    String nowMinite = DataFormater.miliToMinite(now);
//                    String totleMinite = musicActivity.currentSong.getMiniteTime();
//                    musicActivity.sk_progress.setProgress(now);
//                    musicActivity.tv_time.setText(nowMinite + "/" + totleMinite);
//                    break;
//                case FIND_FILE_UPDATE:
//                    Bundle bundle2 = msg.getData();
//                    musicActivity.tv_updateList.setText("找到" + bundle2.getInt("totleSongs") + "首歌曲，\n正在扫描目录\n:" + bundle2.getString("nowPath"));
//                    break;
                case FIND_COMPLETE:
//                    L.i("FIND_COMPLETE");
                    List<SongInfo> list = (List<SongInfo>) msg.obj;
                    //musicActivity.setSongListData();
                    Toast.makeText(musicActivity, "扫描完成,找到"+list.size()+"首歌曲", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }
}
