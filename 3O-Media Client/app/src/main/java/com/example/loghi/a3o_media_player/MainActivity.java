package com.example.loghi.a3o_media_player;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private MediaPlayer mediaPlayer;
    public static TextView songName;
    private double timeElapsed = 0, finalTime = 0;
    private int forwardTime = 2000, backwardTime = 2000;
    private Handler durationHandler = new Handler();
    public static SeekBar seekbar;
    public static ImageButton playBtn;
    public static ImageButton pauseBtn;
    public static ImageButton forwardBtn;
    public static ImageButton rewindBtn;
    public static ImageView mp3Image;
    private MediaMetadataRetriever mmr;
    private VideoView video;
    private RecyclerView recyclerView;
    private ArrayList<MediaItem> songList;
    private ArrayList<MediaItem> movieList;
    private ArrayList<MediaItem> ebookList;
    private Bitmap owl;
    private HashMap<String,ArrayList<Media>> catagory;
    private HashMap<String,ArrayList<Media>> catagory1;
    private HashMap<String,ArrayList<Media>> catagory2;
    public SlidingUpPanelLayout layout;
    private MediaAdapter mediaAdapter;
    public static VideoView videoView;

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private static final  String TAG = "MainActivity";

//    Above api level 28
//    private ListReceiver mListReceiver;
//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(mListReceiver);
//        stopService(fileDearcherIntent);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        startService(fileDearcherIntent);
//        mListReceiver = new ListReceiver();
//        registerReceiver(mListReceiver, new IntentFilter(MediaFileSearcher.BROADCAST_ACTION));
//    }
//    private class ListReceiver extends BroadcastReceiver{
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            //verify if the message we received is the one that we want
//                //get the arrayList we have sent with intent (the one from BroadcastSender)
//                //notice that we needed the key "value"
//                songList = intent.getParcelableArrayListExtra("songList");
//
//                //sets the adapter that provides data to the list.
//            songListView.setAdapter(new MediaAdapter(songList,MainActivity.this));
//
//
//
//        }
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initializeViews();
        pauseBtn.setVisibility(View.INVISIBLE);
        video.setVisibility(View.INVISIBLE);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        final GridLayoutManager mLayoutManager = new GridLayoutManager(this,2);
        mediaAdapter=new MediaAdapter(MainActivity.this,songList,mediaPlayer,layout);

        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mediaAdapter.getItemViewType(position)== 0 ?2:1;
            }
        });
        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mediaAdapter);

    }
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

//    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
//
//        private int spanCount;
//        private int spacing;
//        private boolean includeEdge;
//
//        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
//            this.spanCount = spanCount;
//            this.spacing = spacing;
//            this.includeEdge = includeEdge;
//        }
//
//        @Override
//        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//            int position = parent.getChildAdapterPosition(view); // item position
//            int column = position % spanCount; // item column
//
//            if (includeEdge) {
//                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
//                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
//
//                if (position < spanCount) { // top edge
//                    outRect.top = spacing;
//                }
//                outRect.bottom = spacing; // item bottom
//            } else {
//                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
//                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
//                if (position >= spanCount) {
//                    outRect.top = spacing; // item top
//                }
//            }
//        }
//    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            getsongList();
            getmovieList();
            getebookList();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Clicked item one", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_ebook) {
            Toast.makeText(this, "Ebook", Toast.LENGTH_SHORT).show();
            mediaAdapter.setMediaList(ebookList);
        } else if (id == R.id.nav_music) {
            mediaAdapter.setMediaList(songList);
            Toast.makeText(this, "Songs", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_movie) {
            mediaAdapter.setMediaList(movieList);
            Toast.makeText(this, "Videos", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    public void opnPlayer(View view) {
//        if (playerIntent == null) {
//            playerIntent = new Intent(this, SecondActivity.class);
//
//            Snackbar.make(view, "Player is running", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
//            startActivity(playerIntent);
//        }
//        else{
//            playerIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//            startActivity(playerIntent);
//        }
//    }
    public void initializeViews(){
        songName = (TextView) findViewById(R.id.songName);
        mediaPlayer = MediaPlayer.create(this, R.raw.sample);
        finalTime = mediaPlayer.getDuration();
        seekbar = (SeekBar) findViewById(R.id.seekBar);
        songName.setText("Sample_Song.mp3");
        mp3Image = (ImageView) findViewById(R.id.mp3Image) ;
        video = (VideoView) findViewById(R.id.video);
        seekbar.setMax((int) finalTime);
        playBtn=(ImageButton)findViewById(R.id.media_play);
        pauseBtn=(ImageButton)findViewById(R.id.media_pause);
        forwardBtn= (ImageButton)findViewById(R.id.media_ff);
        rewindBtn=(ImageButton)findViewById(R.id.media_rew);
        songList = new ArrayList<>();
        movieList = new ArrayList<>();
        ebookList = new ArrayList<>();
        owl= BitmapFactory.decodeResource(this.getResources(),R.drawable.owl);
        layout = (SlidingUpPanelLayout)findViewById(R.id.slideUp_layout);
        videoView = (VideoView)findViewById(R.id.video);
        catagory = new HashMap<>();
        catagory1 = new HashMap<>();
        catagory2 =new HashMap<>();

    }

    public void play(View view) {
        if(timeElapsed>0){
            mediaPlayer.start();
        }else {
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mediaPlayer.prepareAsync();
        }
        timeElapsed = mediaPlayer.getCurrentPosition();
        seekbar.setProgress((int) timeElapsed);
        durationHandler.postDelayed(updateSeekBarTime, 100);
        finalTime = mediaPlayer.getDuration();
        playBtn.setVisibility(View.INVISIBLE);
        pauseBtn.setVisibility(View.VISIBLE);
    }

    public void forward(View view) {
        if ((timeElapsed + forwardTime)  <= finalTime) {
            timeElapsed = timeElapsed +forwardTime;
            mediaPlayer.seekTo((int) timeElapsed);
        }
    }

    public void rewind(View view) {
        if ((timeElapsed -backwardTime)  >=0) {
            timeElapsed = timeElapsed -backwardTime;
            mediaPlayer.seekTo((int) timeElapsed);
        }
    }
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            //get current position
            timeElapsed = mediaPlayer.getCurrentPosition();
            //set seekbar progress
            seekbar.setProgress((int) timeElapsed);
            //repeat yourself that again in 100 miliseconds
            durationHandler.postDelayed(this, 100);
        }
    };

    public void pause(View view) {
        pauseBtn.setVisibility(View.INVISIBLE);
        playBtn.setVisibility(View.VISIBLE);
        mediaPlayer.pause();
    }

    public void getBitmap(Media media){
        try {
            mmr = new MediaMetadataRetriever();
            mmr.setDataSource(media.getMediaPath());

            byte[] artBytes =  mmr.getEmbeddedPicture();
            if(artBytes!=null)
            {
                //     InputStream is = new ByteArrayInputStream(mmr.getEmbeddedPicture());
                Bitmap bm = BitmapFactory.decodeByteArray(artBytes, 0, artBytes.length);
                media.setImage(bm);
            }else{
                media.setImage(owl);
            }
        }catch (Exception ex){
            media.setImage(owl);

        }


    }
    public void getsongList(){
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);

            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int dataColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int mimeColumn = musicCursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisData = musicCursor.getString(dataColumn);
                String thisMime = musicCursor.getString(mimeColumn);
                Media media = new Media(thisId, thisTitle, thisArtist,thisData,thisMime);
                getBitmap(media);
                ArrayList<Media> medList = catagory.get(thisArtist);
                if (medList !=null ){
                    medList.add(media);
                }else {
                    ArrayList<Media> tempMed = new ArrayList<>();
                    tempMed.add(media);
                    catagory.put(thisArtist,tempMed);
                }

            }
            while (musicCursor.moveToNext());
            for (Map.Entry<String, ArrayList<Media>> cat : catagory.entrySet()){
                songList.add(new Header(cat.getKey()));
                for(Media media : cat.getValue()){
                    songList.add(media);
                }

            }


        }

    }
    public void getmovieList(){

        ContentResolver videoResolver = getContentResolver();
        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor videoCursor = videoResolver.query(videoUri, null, null, null, null);
        if(videoCursor!=null && videoCursor.moveToFirst()){
            //get columns
            int titleColumn = videoCursor.getColumnIndex
                    (android.provider.MediaStore.Video.Media.TITLE);

            int idColumn = videoCursor.getColumnIndex
                    (android.provider.MediaStore.Video.Media._ID);
            int artistColumn = videoCursor.getColumnIndex
                    (android.provider.MediaStore.Video.Media.ARTIST);
            int dataColumn = videoCursor.getColumnIndex(MediaStore.Video.Media.DATA);
            int mimeColumn = videoCursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE);
            //add songs to list
            do {
                long thisId = videoCursor.getLong(idColumn);
                String thisTitle = videoCursor.getString(titleColumn);
                String thisArtist = videoCursor.getString(artistColumn);
                String thisData = videoCursor.getString(dataColumn);
                String thisMime = videoCursor.getString(mimeColumn);
                Media media = new Media(thisId, thisTitle, thisArtist,thisData,thisMime);
                getBitmap(media);
                Log.i("mime",thisMime);
                ArrayList<Media> medList = catagory1.get(thisArtist);

                if (medList !=null ){
                    medList.add(media);
                }else {
                    ArrayList<Media> tempMed = new ArrayList<>();
                    tempMed.add(media);
                    catagory1.put(thisArtist,tempMed);
                }

                Log.i("videolist",media.getMediaPath());
            }
            while (videoCursor.moveToNext());
            for (Map.Entry<String, ArrayList<Media>> cat : catagory1.entrySet()){
                movieList.add(new Header(cat.getKey()));
                for(Media media : cat.getValue()){
                    movieList.add(media);
                }

            }

        }

    }
    public void getebookList(){
        String selection = "_data LIKE '%.pdf'";
        try (Cursor cursor = getApplicationContext().getContentResolver().query(MediaStore.Files.getContentUri("external"), null, selection, null, "_id DESC")) {
            if (cursor== null || cursor.getCount() <= 0 || !cursor.moveToFirst()) {
                // this means error, or simply no results found
                return;
            }
            int titleColumn = cursor.getColumnIndex
                    (MediaStore.Files.FileColumns.TITLE);

            int idColumn = cursor.getColumnIndex
                    (android.provider.MediaStore.Files.FileColumns._ID);
            int artistColumn = cursor.getColumnIndex
                    (MediaStore.Files.FileColumns.PARENT);
            int dataColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            int mimeColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE);
            do {

                String thisTitle = cursor.getString(titleColumn);
                long thisId = cursor.getLong(idColumn);
                String thisArtist = cursor.getString(artistColumn);
                String thisData = cursor.getString(dataColumn);
                String thisMime = cursor.getString(mimeColumn);
                Media media = new Media(thisId, thisTitle, thisArtist,thisData,thisMime);
                getBitmap(media);
                Log.i("pdfffff",thisArtist);
                ArrayList<Media> medList = catagory2.get(thisArtist);

                if (medList !=null ){
                    medList.add(media);
                }else {
                    ArrayList<Media> tempMed = new ArrayList<>();
                    tempMed.add(media);
                    catagory2.put(thisArtist,tempMed);
                }
                // your logic goes here
            } while (cursor.moveToNext());
            for (Map.Entry<String, ArrayList<Media>> cat : catagory2.entrySet()){
                ebookList.add(new Header(cat.getKey()));
                for(Media media : cat.getValue()){
                    ebookList.add(media);
                }

            }
        }
    }


    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }
    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                    Toast.makeText(this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }


}
