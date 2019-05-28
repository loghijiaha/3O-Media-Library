package com.example.loghi.a3o_media_player;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import static com.example.loghi.a3o_media_player.MainActivity.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;

//public class MediaAdapter extends BaseAdapter {
//    private ArrayList<Media> medias;
//    private LayoutInflater mediaInf;
//
//    public MediaAdapter(ArrayList<Media> medias, Context cont) {
//        this.medias = medias;
//        mediaInf=LayoutInflater.from(cont);
//    }
//
//    @Override
//    public int getCount() {
//        return medias.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return null;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        LinearLayout songLay = (LinearLayout)mediaInf.inflate
//                (R.layout.media_element, parent, false);
//        //get title and artist views
//        TextView mediaView = (TextView)songLay.findViewById(R.id.media_title);
//        TextView artistView = (TextView)songLay.findViewById(R.id.media_artist);
//        ImageView imageView =(ImageView)songLay.findViewById(R.id.media_image);
//        //get song using position
//        Media currSong =medias.get(position);
//        //get title and artist strings
//        mediaView.setText(currSong.getTitle());
//        artistView.setText(currSong.getArtist());
//        imageView.setImageBitmap(currSong.getImage());
//        //set position as tag
//        songLay.setTag(position);
//        return songLay;    }
//}
class MediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private MediaPlayer mediaPlayer;
    private Context mContext;

   

    private List<MediaItem> mediaList;
    public SlidingUpPanelLayout layout;

    class MItem extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView image,overflow;
        public Media media;
        public MItem(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.media_title);
            image = (ImageView) view.findViewById(R.id.media_image);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }
    class MHeader extends RecyclerView.ViewHolder{
        TextView txtTitle;
        public MHeader(View itemView) {
            super(itemView);
            this.txtTitle = (TextView)itemView.findViewById(R.id.txtHeader);
        }
    }

    public MediaAdapter(Context mContext, List<MediaItem> mediaList,MediaPlayer mediaPlayer,SlidingUpPanelLayout layout) {
        this.mediaPlayer=mediaPlayer;
        this.mContext = mContext;
        this.mediaList = mediaList;
        this.layout=layout;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==TYPE_HEADER)
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_author, parent, false);
            return  new MHeader(v);
        }
        else
        {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.media_element, parent, false);

            return new MItem(itemView);
        }
        // return null;
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MHeader)
        {
            // MHeader MHeader = (MHeader)holder;
            Header  currentItem = (Header) mediaList.get(position);
            MHeader MHeader = (MHeader)holder;
            MHeader.txtTitle.setText(currentItem.getHeader());
        }
        else if(holder instanceof MItem)
        {
            final Media media = (Media)mediaList.get(position);
            final MItem mItem =(MItem)holder;
            mItem.media= media;
            mItem.title.setText(media.getTitle());


            // loading media cover using Glide library
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if(media.getImage()!=null) {
                media.getImage().compress(Bitmap.CompressFormat.PNG, 100, stream);
            }
            Glide.with(mContext)
                    .load(stream.toByteArray())
                    .asBitmap()
                    .error(R.drawable.owl)
                    .into(mItem.image);
            mItem.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(mItem.overflow);
                }
            });

            mItem.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaPlayer.reset();
                    if(MainActivity.pauseBtn.getVisibility()==View.VISIBLE){
                        MainActivity.pauseBtn.setVisibility(View.INVISIBLE);
                        MainActivity.playBtn.setVisibility(View.VISIBLE);
                    }
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }

                    });
                    try {
                        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                mItem.media.getId());
                        if(mItem.media.getMimeType().startsWith("audio")) {
                            MainActivity.videoView.setVisibility(View.INVISIBLE);
                            try {
                                mediaPlayer.setDataSource(mContext, trackUri);

                            }
                            catch (Exception ex){

                                String url = "http://"+TokenSaver.getIP(mContext)+":8012/Music/Music"+media.getMediaPath();
                                mediaPlayer.setDataSource( url);
                                Log.i("Song plaing", url);

                            }
//                            try{
//
//                                if(checkPermissionREAD_EXTERNAL_STORAGE(mContext)) {
//                                    String url = "http://127.0.0.1:8012/Music/MusicMaaris%20Aanandhi%20-%20SenSongsmp3.Co.mp3";
////                                Map<String, String> headers = new HashMap<String, String>();
////                                headers.put("token", TokenSaver.getToken(mContext));
////                                headers.put("path", mItem.media.getMediaPath());
////                                Method method = mediaPlayer.getClass().getMethod("setDataSource", new Class[] { Context.class, Uri.class, Map.class });
////                                method.invoke(mediaPlayer, new Object[] {this, url, headers});
//                                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                                    mediaPlayer.setDataSource( url);
//                                    mediaPlayer.prepare();
//
//
//                                }
//                            }catch (Exception e){
//
//                            }


                            MainActivity.songName.setText(media.getTitle());
                            MainActivity.mp3Image.setImageResource(R.drawable.owl);
                            MainActivity.mp3Image.setVisibility(View.VISIBLE);

                            MainActivity.mp3Image.setImageBitmap(media.getImage());
                            MainActivity.playBtn.setVisibility(View.VISIBLE);
                            MainActivity.pauseBtn.setVisibility(View.VISIBLE);
                            MainActivity.rewindBtn.setVisibility(View.VISIBLE);
                            MainActivity.forwardBtn.setVisibility(View.VISIBLE);
                            MainActivity.seekbar.setVisibility(View.VISIBLE);
                            layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                        }else {
                            try{
                                MainActivity.videoView.setVideoPath(mItem.media.getMediaPath());
                            }catch(Exception er){
                                String urlw = "http://"+TokenSaver.getIP(mContext)+":8012/Video/Video"+media.getMediaPath();
                                MainActivity.videoView.setVideoPath(urlw);                                Log.i("Video playing", urlw);
                            }
                            MainActivity.songName.setText(media.getTitle());
                            MainActivity.mp3Image.setVisibility(View.INVISIBLE);
                            MainActivity.playBtn.setVisibility(View.INVISIBLE);
                            MainActivity.pauseBtn.setVisibility(View.INVISIBLE);
                            MainActivity.rewindBtn.setVisibility(View.INVISIBLE);
                            MainActivity.forwardBtn.setVisibility(View.INVISIBLE);
                            MainActivity.seekbar.setVisibility(View.INVISIBLE);
                            MediaController mediaController= new MediaController(mContext);
                            mediaController.setAnchorView(MainActivity.videoView);
                            //Location of Media File
                            Uri uri = Uri.parse(mItem.media.getMediaPath());
                            //Starting VideoView By Setting MediaController and URI
                            MainActivity.videoView.setVisibility(View.VISIBLE);
                            MainActivity.videoView.setMediaController(mediaController);


                            layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                        }
                    } catch (Exception e) {
                        Log.e("MUSIC SERVICE", "Error setting data source", e);                    }
                }
            });
        }
      
    }
    

    /**
     * Showing popup menu when tapping on 3 image
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_media, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_play_next:
                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }
    public int getItemViewType(int position) {
        if(isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    public boolean isPositionHeader(int position)
    {

        return mediaList.get(position) instanceof Header;

    }

    public void setMediaList(List<MediaItem> mediaList) {

        this.mediaList = mediaList;
        notifyDataSetChanged();
    }
    public void clear(){
        int size = mediaList.size();
        mediaList.clear();
        notifyItemRangeRemoved(0,size);
    }
}