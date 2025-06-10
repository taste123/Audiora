package com.example.audiora.database;

import android.provider.BaseColumns;

public class DatabaseContract {

    public static final class PlaylistColumns implements BaseColumns {
        public static final String TABLE_NAME = "playlists";
        public static final String NAME = "name";
    }

    public static final class PlaylistSongColumns implements BaseColumns {
        public static final String TABLE_NAME = "playlist_songs";
        public static final String PLAYLIST_ID = "playlist_id"; // Foreign key
        // We will store all song details directly in this table
        public static final String TRACK_ID = "track_id";
        public static final String TRACK_NAME = "track_name";
        public static final String ARTIST_NAME = "artist_name";
        public static final String COLLECTION_NAME = "collection_name";
        public static final String ARTWORK_URL = "artwork_url";
        public static final String PREVIEW_URL = "preview_url";
    }
}