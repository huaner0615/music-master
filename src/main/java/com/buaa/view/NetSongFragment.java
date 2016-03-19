package com.buaa.view;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.buaa.adapter.NetListAdapter;
import com.buaa.bean.SongInfo;
import com.buaa.music.MusicActivity;
import com.buaa.music.R;
import com.buaa.utils.NetUtils;

import java.lang.ref.WeakReference;
import java.util.List;


/**
 * 本类为推荐活动的界面
 *
 * @author Linux
 */
public class NetSongFragment extends Fragment {
    private List<SongInfo> list;
    private MusicActivity activity;
    private ListView listView;
    private TextView title;
    private MyHandler handler;
    private ListView fragement_lv_net;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new MyHandler(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_netlist, null);
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MusicActivity) getActivity();
        NetUtils.getAllNewsForNetWork(handler);
    }


    private void initViews(View view) {
        title = (TextView) view.findViewById(R.id.activity_title_tv);
        title.setText("网络歌曲");
        fragement_lv_net = (ListView) view.findViewById(R.id.fragement_lv_net);

    }

    public static class MyHandler extends Handler {
        public static final int NET_DATA_UPDATE = 0;
        public static final int NET_ERROR = 1;

        WeakReference<NetSongFragment> weakReference;
        public MyHandler(NetSongFragment fragment) {
            weakReference = new WeakReference<NetSongFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            final NetSongFragment fragment = weakReference.get();
            switch (msg.what) {
                case NET_DATA_UPDATE:
                    fragment.list = (List<SongInfo>) msg.obj;
                    fragment.setListViewData();
                    break;
                case NET_ERROR:
                    Toast.makeText(fragment.activity, "网络异常，请重试!", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }

    private void setListViewData() {
        NetListAdapter adapter = new NetListAdapter(activity, list);
        fragement_lv_net.setAdapter(adapter);
        fragement_lv_net.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SongInfo songInfo = list.get(position);
                Toast.makeText(activity, songInfo.getName(), Toast.LENGTH_SHORT).show();
//                SongInfo songInfo = (SongInfo) parent.getItemAtPosition(position);
//                service.playMusic(songInfo);
            }
        });
    }
}
