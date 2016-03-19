package com.buaa.utils;

import android.os.Handler;
import android.os.Message;

import com.buaa.bean.SongInfo;
import com.buaa.view.NetSongFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NetUtils {

    public static final String newsPath_url = "http://music.ygiot.cn/songList";

    //封装新闻的假数据到list中返回
    public static ArrayList<SongInfo> getAllNewsForNetWork(final Handler handler) {

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //1.请求服务器获取新闻数据
                            //获取一个url对象，通过url对象得到一个urlconnnection对象
                            URL url = new URL(newsPath_url);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            //设置连接的方式和超时时间
                            connection.setRequestMethod("GET");
                            connection.setConnectTimeout(10 * 1000);
                            //获取请求响应码
                            int code = connection.getResponseCode();
                            if (code == 200) {
                                InputStream inputStream = connection.getInputStream();
                                String result = StreamUtils.streamToString(inputStream);

                                //2.解析获取的新闻数据到List集合中。
                                ArrayList<SongInfo> arrayList = new ArrayList<SongInfo>();
                                JSONObject root_json = new JSONObject(result);//将一个字符串封装成一个json对象。
                                JSONArray jsonArray = root_json.getJSONArray("data");//获取root_json中的newss作为jsonArray对象

                                for (int i = 0; i < jsonArray.length(); i++) {//循环遍历jsonArray
                                    JSONObject song_json = jsonArray.getJSONObject(i);//获取一条新闻的json

                                    SongInfo song = new SongInfo();
                                    song.setName(song_json.getString("name"));
                                    song.setNetFilePath(song_json.getString("songPath"));
                                    song.setNetLrcPath(song_json.getString("lrcPath"));
                                    song.setNetImgPath(song_json.getString("songPath"));
                                    song.setVip(song_json.getBoolean("vip"));
                                    arrayList.add(song);
                                }
                                Message message = new Message();
                                message.what = NetSongFragment.MyHandler.NET_DATA_UPDATE;
                                message.obj = arrayList;
                                handler.sendMessage(message);
                                return;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).start();
        handler.sendEmptyMessage(NetSongFragment.MyHandler.NET_ERROR);
        return null;
    }

//	//从数据库中获取上次缓存的新闻数据做listview的展示
//	public  static ArrayList<NewsBean> getAllNewsForDatabase(Context context) {
//
//		return new NewsDaoUtils(context).getNews();
//
//	}
}
