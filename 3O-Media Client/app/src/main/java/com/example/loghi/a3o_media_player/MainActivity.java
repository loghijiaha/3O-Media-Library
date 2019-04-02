package com.example.loghi.a3o_media_player;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.blikoon.qrcodescanner.QrCodeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_CODE_QR_SCAN = 101;
    private static final int REQUEST_CODE_LOGIN= 102;

    private final String LOGTAG = "QRCScanner-MainActivity";
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
    private static String serverURL;
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
    private static boolean remoteLoaded=false;
    private String changer;

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
class JSONAsyncTaskAud extends AsyncTask<String, Void, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(String... urls) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast

            URL url = new URL(urls[0]);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            forecastJsonStr = buffer.toString();
            return forecastJsonStr;
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            Toast.makeText(MainActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();

            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
    }
    private Bitmap getBitmapFromString(String jsonString) {
        /*
         * This Function converts the String back to Bitmap
         * */
        byte[] decodedString = Base64.decode(jsonString, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
    protected void onPostExecute(String result) {
        Log.i("Resulr out",result);
        try {
            JSONObject fileLists= new JSONObject(result);
            Log.i("filelist",fileLists.toString());
            JSONArray j = new JSONArray(fileLists.getString("file_list"));
            if (j != null) {
                songList.clear();
                for (int i=0;i<j.length();i++){
                    String x =j.getString(i);
                    Log.i("json data",x);
                    JSONObject fileList =new JSONObject(x);
                    Media m=new Media(fileList.getLong("id"),fileList.getString("name"),fileList.getString("artist_name"),fileList.getString("path"),"audio/mpeg");
                   JSONObject image = new JSONObject(fileList.getString("image"));
                    m.setImage(getBitmapFromString(image.getString("data")));
                    songList.add(m);
                    Toast.makeText(MainActivity.this, "Songs Loaded", Toast.LENGTH_SHORT).show();
                    remoteLoaded=true;
                }
            }
//
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
class JSONAsyncTaskVid extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                URL url = new URL(urls[0]);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                return forecastJsonStr;
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                Toast.makeText(MainActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();

                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
        }
        private Bitmap getBitmapFromString(String jsonString) {
            /*
             * This Function converts the String back to Bitmap
             * */
            byte[] decodedString = Base64.decode(jsonString, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            return decodedByte;
        }
        protected void onPostExecute(String result) {
            Log.i("Resulr out",result);
            try {
                JSONObject fileLists= new JSONObject(result);
                Log.i("filelist",fileLists.toString());
                JSONArray j = new JSONArray(fileLists.getString("file_list"));
                if (j != null) {
                    movieList.clear();
                    for (int i=0;i<j.length();i++){
                        String x =j.getString(i);
                        Log.i("json data",x);
                        JSONObject fileList =new JSONObject(x);
                        Media m=new Media(fileList.getLong("id"),fileList.getString("name"),fileList.getString("artist_name"),fileList.getString("path"),"audio/mpeg");
                        JSONObject image = new JSONObject(fileList.getString("image"));
                        m.setImage(getBitmapFromString(image.getString("data")));
                        movieList.add(m);
                        Toast.makeText(MainActivity.this, "Videos Loaded", Toast.LENGTH_SHORT).show();
                    }
                }
//
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
class JSONAsyncTaskEbo extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                URL url = new URL(urls[0]);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                return forecastJsonStr;
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                Toast.makeText(MainActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();

                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
        }
        private Bitmap getBitmapFromString(String jsonString) {
            /*
             * This Function converts the String back to Bitmap
             * */
            byte[] decodedString = Base64.decode(jsonString, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            return decodedByte;
        }
        protected void onPostExecute(String result) {
            Log.i("Resulr out",result);
            try {
                JSONObject fileLists= new JSONObject(result);
                Log.i("filelist",fileLists.toString());
                JSONArray j = new JSONArray(fileLists.getString("file_list"));
                if (j != null) {
                    ebookList.clear();
                    for (int i=0;i<j.length();i++){
                        String x =j.getString(i);
                        Log.i("json data",x);
                        JSONObject fileList =new JSONObject(x);
                        Media m=new Media(fileList.getLong("id"),fileList.getString("name"),fileList.getString("artist_name"),fileList.getString("path"),"audio/mpeg");
                        JSONObject image = new JSONObject(fileList.getString("image"));
                        m.setImage(getBitmapFromString(image.getString("data")));
                        ebookList.add(m);
                        Toast.makeText(MainActivity.this, "Ebook Loaded", Toast.LENGTH_SHORT).show();

                    }
                }
//
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

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


    public static String getServerURL() {
        return serverURL;
    }

    public static void setServerURL(String serverURL) {
        MainActivity.serverURL = serverURL;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TokenSaver tk  = new TokenSaver();
        tk.clear(this);
        Log.i("closing",tk.getToken(this));
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case (REQUEST_CODE_QR_SCAN): {
                if(data==null)
                    return;


                //Getting the passed result
                String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
                TokenSaver tk = new TokenSaver();
                tk.setIP(this,result);
                Log.i(LOGTAG,"Have scan result in your app activity :"+ result);
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Scan result");
                alertDialog.setMessage("Code scanned successfully");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                                startActivityForResult(i,REQUEST_CODE_LOGIN);

                            }
                        });
                alertDialog.show();
            }
            break;

            case (REQUEST_CODE_LOGIN): {
                TokenSaver tk = new TokenSaver();
                String token=tk.getToken(this);
                Log.i("Login succesfull",token);
                Log.i("Login succesfull","hellllllllllllllo");
            }
            break;
        }

        if(resultCode != Activity.RESULT_OK)
        {
            Log.d(LOGTAG,"COULD NOT GET A GOOD RESULT.");
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if( result!=null)
            {
                setServerURL(result);
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            return;

        }
//    if(requestCode == REQUEST_CODE_QR_SCAN)
//    {
//
//
//    }
//    if(requestCode == REQUEST_CODE_LOGIN){
//
//
//    }
}
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }


    public String  performPostCall(String requestURL,
                                   HashMap<String, String> postDataParams) {

        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.search);

        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);



        final SearchView searchView;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // Toast like print
                    ArrayList searchL = new ArrayList();
                    try {
                        for (Map.Entry<String, ArrayList<Media>> entry : catagory.entrySet()) {
                            if(entry.getKey().toLowerCase().startsWith(query.toLowerCase())){
                                query=entry.getKey();
                                break;
                            }
                        }
                        for (Map.Entry<String, ArrayList<Media>> entry : catagory1.entrySet()) {
                            if(entry.getKey().toLowerCase().startsWith(query.toLowerCase())){
                                query=entry.getKey();
                                break;
                            }
                        }
                        for (Map.Entry<String, ArrayList<Media>> entry : catagory2.entrySet()) {
                            if(entry.getKey().toLowerCase().startsWith(query.toLowerCase())){
                                query=entry.getKey();
                                break;
                            }
                        }
                        ArrayList<Media> cat = catagory.get(query);
                        cat.addAll(catagory1.get(query)==null?new ArrayList<Media>():catagory1.get(query));
                        cat.addAll(catagory2.get(query)==null?new ArrayList<Media>():catagory2.get(query));
                        if(cat==null){
                            searchL.add(new Header("No Item found"));
                        }else {
                            searchL.add(new Header(query));
                            for (Media media : cat) {
                                searchL.add(media);
                            }

                        }
                    }catch (Exception e){
                        searchL.add(new Header("No Item found"));

                    }

                    mediaAdapter.setMediaList(searchL);
                    if( ! searchView.isIconified()) {
                        searchView.setIconified(true);
                    }
                    searchItem.collapseActionView();
                    return false;
                }
                @Override
                public boolean onQueryTextChange(String s) {
                    // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                    return false;
                }
            });
        }

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Loggedout one", Toast.LENGTH_SHORT).show();
//            new LongOperation().execute();


            loggedOut();

        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        TokenSaver tk = new TokenSaver();
        int id = item.getItemId();

        if (id == R.id.nav_ebook) {
            Toast.makeText(this, "Ebook", Toast.LENGTH_SHORT).show();
            mediaAdapter.setMediaList(ebookList);
        } else if (id == R.id.nav_music) {
            mediaAdapter.setMediaList(songList);
            Toast.makeText(this, "Songs", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_movie) {
            Toast.makeText(this, "Videos", Toast.LENGTH_SHORT).show();
            mediaAdapter.setMediaList(movieList);

        } else if (id == R.id.nav_share) {
//            try {
//                mediaPlayer.release();
//                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                mediaPlayer.setDataSource(this, Uri.parse("http://10.10.5.166:8012/playAudio?path=des.mp3"));
//                mediaPlayer.prepareAsync();
//
//            } catch (IllegalArgumentException e) {
//                e.printStackTrace();
//            } catch (SecurityException e) {
//                e.printStackTrace();
//            } catch (IllegalStateException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        } else if (id == R.id.nav_send) {


//

        }else if (id == R.id.nav_remote) {
            if (!isLoggedIn()) {
                Intent i = new Intent(MainActivity.this, QrCodeActivity.class);
                startActivityForResult(i, REQUEST_CODE_QR_SCAN);

            }else{
                if(!remoteLoaded) {
                    String request = "http://" + tk.getIP(this) + ":8012/getAllAudio";
                    new JSONAsyncTaskAud().execute(request);
                    String request1 = "http://" + tk.getIP(this) + ":8012/getAllVideo";
                    new JSONAsyncTaskVid().execute(request1);
                    String request2 = "http://" + tk.getIP(this) + ":8012/getAllEbook";
                    new JSONAsyncTaskEbo().execute(request2);
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Message from remote");
                    alertDialog.setMessage("Wait until load all contents");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }else{
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Message from remote");
                    alertDialog.setMessage("Already Loaded");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
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
    private void startHeavyProcessing() {
        new LongOperation().execute("");
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //some heavy processing resulting in a Data String
            try {
                if (checkPermissionREAD_EXTERNAL_STORAGE(MainActivity.this)) {
                    getsongList();
                    getebookList();
                    getmovieList();
                }

            } catch (Exception e) {
                Thread.interrupted();
            }

            return "whatever result you have";
        }
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected void onPostExecute(String result) {

        }
    }
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
        if (checkPermissionREAD_EXTERNAL_STORAGE(MainActivity.this)) {
            getsongList();
        }
        Toast.makeText(MainActivity.this,"Loading all contents",Toast.LENGTH_LONG).show();
        startHeavyProcessing();

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
    private boolean isLoggedIn(){
        TokenSaver tk = new TokenSaver();
        if(tk.getIP(this)!=null && tk.getToken(this)!=null){
            Log.i("Token",tk.getToken(this));
            Log.i("IP",tk.getIP(this));
            return true;
        }else{
            return false;
        }
    }
    private void loggedOut(){

        TokenSaver.clear(this);

    }

}
