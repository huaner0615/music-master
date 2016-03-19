package com.buaa.view;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;
import android.widget.TextView;

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

import java.lang.ref.WeakReference;
import java.util.List;



public class LyricFragment extends Fragment implements MusicListener,ServiceConnection {
	//system
	private MusicActivity activity;
	private IMusicService service;
	private MyHandler handler;
	//lyric
	private LrcView lyricView;
	private Lyric lyric;
	private LyricLine lyricLine;
	//song
	private SongInfo currentSong;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new MyHandler(this);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_lyric, container, false);
		return view;
	}


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		lyricView = (LrcView) getActivity().findViewById(R.id.fragement_lyric_lrcShowView);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.activity = (MusicActivity) getActivity();
	}


	@Override
	public void onResume() {
		super.onResume();

	}




	@Override
	public void onDestroy() {
		super.onDestroy();
		L.i("LyricFragment onDestroy");
	}

	@Override
	public void onProgressChange(int now) {
		Message message = Message.obtain();
		Bundle bundle = new Bundle();
		bundle.putInt("progress", now);

		if(lyric!=null){
			LyricLine lyricLine = activity.getLyricLine();
			L.i("onProgressChange "+lyricLine);
			lyric.log();
			if(lyricLine!=null){
				bundle.putSerializable("LyricLine", lyricLine);
				//lyric.log();
				//L.i("currentPosition: now:" + lyricLine.getTime()+"   "+lyricLine.getStr());
			}
		}
		message.what = MyHandler.PROGRESS_UPDATE;
		message.setData(bundle);
		handler.sendMessage(message);
	}

	@Override
	public void onSongChange(SongInfo songInfo) {
		currentSong = songInfo;
		lyric = activity.getLyric();
		handler.sendEmptyMessage(MyHandler.SONG_CHANGE);
	}

	@Override
	public void onStateChange(boolean start) {

	}

	@Override
	public void onSongListChange(List<SongInfo> list) {

	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		this.service = (IMusicService) service;
		this.service.getSongInfo();
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {

	}
	private static class MyHandler extends Handler {
		public static final int SONG_LIST_CHANGE = 0;
		public static final int FIND_FILE_UPDATE = 1;
		public static final int FIND_COMPLETE = 2;
		public static final int PROGRESS_UPDATE = 3;
		public static final int SONG_CHANGE = 4;
		WeakReference<LyricFragment> weakReference;

		public MyHandler(LyricFragment fragment) {
			weakReference = new WeakReference<LyricFragment>(fragment);
		}

		@Override
		public void handleMessage(Message msg) {
			final LyricFragment fragment = weakReference.get();
			switch (msg.what) {
				case PROGRESS_UPDATE:
					Bundle bundle1 = msg.getData();
					int now = bundle1.getInt("progress");

                    LyricLine lyricLine = (LyricLine) bundle1.getSerializable("LyricLine");
//					if(lyricLine.getTime()==fragment.lyricLine.getTime()){
//						return;
//					}
					L.i("PROGRESS_UPDATE "+lyricLine+"    "+now);
                    if(lyricLine!=null){
                        fragment.lyricView.setmLrcList(fragment.lyric, lyricLine);

						L.i("lyricLine:"+lyricLine.getTime()+"   "+lyricLine.getStr());
//                        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f,1.0f);
//                        alphaAnimation.setDuration(1000);
//                        alphaAnimation.setRepeatMode(Animation.REVERSE);
                        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,-0.05f);
						translateAnimation.setDuration(1000);
                       translateAnimation.setFillAfter(true);
						fragment.lyricView.startAnimation(translateAnimation);
						fragment.handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								fragment.lyricView.invalidate();
							}
						}, 1000);

						//mainActivity.lrcView.setAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.alpha_z));
                    }
//					String nowMinite = DataFormater.miliToMinite(now);
//					String totleMinite = fragment.currentSong.getMiniteTime();
					//fragment.sk_progress.setProgress(now);
					// fragment.tv_time.setText(nowMinite + "/" + totleMinite);
					break;

				case SONG_LIST_CHANGE:

					break;
				case  SONG_CHANGE:
					fragment.lyricView.setmLrcList(fragment.lyric, null);
					if(TextUtils.isEmpty(fragment.currentSong.getSinger())){
						fragment.lyricView.setText(fragment.currentSong.getCleanName());
					}else{
						fragment.lyricView.setText(fragment.currentSong.getCleanName()+"\n"+"演唱:"+fragment.currentSong.getSinger());
					}
					L.i("SONG_CHANGE");
					break;
			}

		}
	}
}
