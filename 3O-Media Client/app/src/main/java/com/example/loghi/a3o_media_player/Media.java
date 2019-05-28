package com.example.loghi.a3o_media_player;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Media extends MediaItem {
    private long id;
    private String  title, artist;
    private String mediaPath,mimeType;
    private Bitmap image;

    public Media(long id,String title, String artist,String mediaPath,String mimeType) {
        this.title = title;
        this.artist=artist;
        this.id=id;
        this.mediaPath=mediaPath;
        this.mimeType=mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public String getArtist() {
        return artist;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
    public void setMediaPath(String mediaPath){ this.mediaPath = mediaPath ;}

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
