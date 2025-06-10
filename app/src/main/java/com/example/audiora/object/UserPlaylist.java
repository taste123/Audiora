package com.example.audiora.object;

import android.os.Parcel;
import android.os.Parcelable;

public class UserPlaylist implements Parcelable {
    private int id;
    private String name;
    private int songCount; // To show how many songs are in the playlist

    public UserPlaylist(int id, String name, int songCount) {
        this.id = id;
        this.name = name;
        this.songCount = songCount;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getSongCount() { return songCount; }
    public void setSongCount(int count) { this.songCount = count; }


    // Parcelable Implementation
    protected UserPlaylist(Parcel in) {
        id = in.readInt();
        name = in.readString();
        songCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(songCount);
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