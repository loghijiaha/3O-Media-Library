package com.example.loghi.a3o_media_player;

import android.graphics.Bitmap;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

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
class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MyViewHolder> {

    private Context mContext;
    private List<Media> mediaList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, artist;
        public ImageView image,overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.media_title);
            artist = (TextView) view.findViewById(R.id.media_artist);
            image = (ImageView) view.findViewById(R.id.media_image);
            overflow = (ImageView) view.findViewById(R.id.overflow);

        }
    }


    public MediaAdapter(Context mContext, List<Media> mediaList) {
        this.mContext = mContext;
        this.mediaList = mediaList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.media_element, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Media media = mediaList.get(position);
        holder.title.setText(media.getTitle());
        holder.artist.setText(media.getArtist());

        // loading media cover using Glide library
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        media.getImage().compress(Bitmap.CompressFormat.PNG, 100, stream);
        Glide.with(mContext)
                .load(stream.toByteArray())
                .asBitmap()
                .error(R.drawable.owl)
                .into(holder.image);
        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
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
}