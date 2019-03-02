package com.example.loghi.a3o_media_player;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MediaFileSearcher extends IntentService {
    private ArrayList<Media> mediaList;
    public static final String BROADCAST_ACTION="com.example.loghi.a3o_media_player.updateMedia";
    public static final Handler handler = new Handler();
    Intent intent;
    int counter=0;

    public MediaFileSearcher() {
        super("MediaFileSearcher");
    }


    @Override
    protected void onHandleIntent(Intent workIntent) {
//        intent = new Intent(BROADCAST_ACTION);
//        handler.removeCallbacks(sendUpdatesToUI);
//        handler.postDelayed(sendUpdatesToUI, 1000);
//        mediaList= new ArrayList<>();
//        Log.i("hello",String.valueOf( Environment.getExternalStorageDirectory().getAbsolutePath()));
//        mediaList=getPlayList(Environment.getExternalStorageDirectory().getAbsolutePath());
//
//        Collections.sort(mediaList, new Comparator<Media>(){
//            public int compare(Media a, Media b){
//                return a.getTitle().compareTo(b.getTitle());
//            }
//        });
    }


//    public ArrayList<Media> getPlayList(String rootPath) {
//        ArrayList<Media> mediaList = new ArrayList<>();
//
//        try {
//            File rootFolder = new File(rootPath);
//            File[] files = rootFolder.listFiles(); //here you will get NPE if directory doesn't contains  any file,handle it like this.
//            for (File file : files) {
//                if (file.isDirectory()) {
//                    getPlayList(file.getAbsolutePath());
//                } else if (file.getName().endsWith(".mp3")) {
//                    mediaList.add(new Media(file.getName(),file.getAbsolutePath()));
//                    Log.i("list",file.getName());
//                }
//            }
//            return mediaList;
//        } catch (Exception e) {
//            return null;
//        }
//    }


}
