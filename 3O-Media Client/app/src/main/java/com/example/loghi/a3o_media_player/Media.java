package com.example.loghi.a3o_media_player;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Media implements Parcelable {
    private long id;
    private String  title, artist;
    private String mediaPath;
    private Bitmap image;

    public Media(long id,String title, String artist,String mediaPath) {
        this.title = title;
        this.artist=artist;
        this.id=id;
        this.mediaPath=mediaPath;
    }
    public Media(Parcel source) {

        //you have to call the other constructor to initialize the arrayList
        // reconstruct from the parcel
        title= source.readString();
        mediaPath=source.readString();

    }
    public static final Creator CREATOR = new Creator() {
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mediaPath);
        dest.writeString(title);
    }
}
