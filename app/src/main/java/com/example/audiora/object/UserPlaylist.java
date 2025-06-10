package com.example.audiora.object;

import android.os.Parcel;
import android.os.Parcelable;

public class UserPlaylist implements Parcelable {
    private int id;
    private String name;
    private String coverImage;
    private int songCount;

    public UserPlaylist(int id, String name, String coverImage, int songCount) {
        this.id = id;
        this.name = name;
        this.coverImage = coverImage;
        this.songCount = songCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }

    // Parcelable Implementation
    protected UserPlaylist(Parcel in) {
        id = in.readInt();
        name = in.readString();
        songCount = in.readInt();
        coverImage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(songCount);
        dest.writeString(coverImage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserPlaylist> CREATOR = new Creator<UserPlaylist>() {
        @Override
        public UserPlaylist createFromParcel(Parcel in) {
            return new UserPlaylist(in);
        }

        @Override
        public UserPlaylist[] newArray(int size) {
            return new UserPlaylist[size];
        }
    };
}