package com.buaa.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.buaa.bean.SongInfo;
import com.buaa.music.R;

import java.util.List;

/**
 * Created by Window10 on 2016/3/17.
 */
public class NetListAdapter extends BaseAdapter {
    private Context context;
    private List<SongInfo> list;

    public NetListAdapter(Context context, List<SongInfo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if(convertView!=null){
            view = convertView;
        }else {
            view = View.inflate(context, R.layout.item_songlist_layout,null);
        }
        TextView item_songlist_name = (TextView) view.findViewById(R.id.item_songlist_name);
        TextView item_songlist_singer = (TextView) view.findViewById(R.id.item_songlist_singer);
        TextView item_songlist_time = (TextView) view.findViewById(R.id.item_songlist_time);
        SongInfo songInfo = list.get(position);
        item_songlist_name.setText(songInfo.getCleanName());
        item_songlist_singer.setText(songInfo.getSinger());
        if(songInfo.isVip()){
            item_songlist_time.setText("vip");
        }
        return view;
    }
}
