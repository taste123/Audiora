package com.example.audiora.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "audiora.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TABLE_PLAYLISTS =
            String.format(
                    "CREATE TABLE %s"
                            + " (%s INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + " %s TEXT NOT NULL)",
                    DatabaseContract.PlaylistColumns.TABLE_NAME,
                    DatabaseContract.PlaylistColumns._ID,
                    DatabaseContract.PlaylistColumns.NAME
            );

    private static final String SQL_CREATE_TABLE_PLAYLIST_SONGS =
            String.format(
                    "CREATE TABLE %s"
                            + " (%s INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + " %s INTEGER NOT NULL," // PLAYLIST_ID
                            + " %s INTEGER NOT NULL," // TRACK_ID
                            + " %s TEXT NOT NULL,"    // TRACK_NAME
                            + " %s TEXT NOT NULL,"    // ARTIST_NAME
                            + " %s TEXT,"             // COLLECTION_NAME
                            + " %s TEXT,"             // ARTWORK_URL
                            + " %s TEXT,"             // PREVIEW_URL
                            + " FOREIGN KEY (%s) REFERENCES %s(%s) ON DELETE CASCADE)",
                    DatabaseContract.PlaylistSongColumns.TABLE_NAME,
                    DatabaseContract.PlaylistSongColumns._ID,
                    DatabaseContract.PlaylistSongColumns.PLAYLIST_ID,
                    DatabaseContract.PlaylistSongColumns.TRACK_ID,
                    DatabaseContract.PlaylistSongColumns.TRACK_NAME,
                    DatabaseContract.PlaylistSongColumns.ARTIST_NAME,
                    DatabaseContract.PlaylistSongColumns.COLLECTION_NAME,
                    DatabaseContract.PlaylistSongColumns.ARTWORK_URL,
                    DatabaseContract.PlaylistSongColumns.PREVIEW_URL,
                    DatabaseContract.PlaylistSongColumns.PLAYLIST_ID,
                    DatabaseContract.PlaylistColumns.TABLE_NAME,
                    DatabaseContract.PlaylistColumns._ID
            );


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_PLAYLISTS);
        db.execSQL(SQL_CREATE_TABLE_PLAYLIST_SONGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.PlaylistSongColumns.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.PlaylistColumns.TABLE_NAME);
        onCreate(db);
    }
}