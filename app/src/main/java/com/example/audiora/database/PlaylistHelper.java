package com.example.audiora.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.audiora.model.ResultsItem;
import com.example.audiora.object.UserPlaylist;

import java.util.ArrayList;

public class PlaylistHelper {

    private static final String PLAYLIST_TABLE = DatabaseContract.PlaylistColumns.TABLE_NAME;
    private static final String SONG_TABLE = DatabaseContract.PlaylistSongColumns.TABLE_NAME;
    private static DatabaseHelper databaseHelper;
    private static SQLiteDatabase database;
    private static volatile PlaylistHelper INSTANCE;

    private PlaylistHelper(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public static PlaylistHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PlaylistHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    public void open() throws SQLException {
        database = databaseHelper.getWritableDatabase();
    }

    public void close() {
        databaseHelper.close();
        if (database.isOpen()) {
            database.close();
        }
    }

    // --- Playlist Methods ---
    public long createPlaylist(String name) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PlaylistColumns.NAME, name);
        return database.insert(PLAYLIST_TABLE, null, values);
    }

    public ArrayList<UserPlaylist> getAllPlaylists() {
        ArrayList<UserPlaylist> arrayList = new ArrayList<>();
        Cursor cursor = database.query(PLAYLIST_TABLE, null, null, null, null, null, DatabaseContract.PlaylistColumns.NAME + " ASC", null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PlaylistColumns._ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.PlaylistColumns.NAME));
                int songCount = getSongCountForPlaylist(String.valueOf(id));
                arrayList.add(new UserPlaylist(id, name, songCount));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    // --- Playlist Song Methods ---
    public long addSongToPlaylist(String playlistId, ResultsItem song) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PlaylistSongColumns.PLAYLIST_ID, playlistId);
        values.put(DatabaseContract.PlaylistSongColumns.TRACK_ID, song.getTrackId());
        values.put(DatabaseContract.PlaylistSongColumns.TRACK_NAME, song.getTrackName());
        values.put(DatabaseContract.PlaylistSongColumns.ARTIST_NAME, song.getArtistName());
        values.put(DatabaseContract.PlaylistSongColumns.COLLECTION_NAME, song.getCollectionName());
        values.put(DatabaseContract.PlaylistSongColumns.ARTWORK_URL, song.getArtworkUrl100());
        values.put(DatabaseContract.PlaylistSongColumns.PREVIEW_URL, song.getPreviewUrl());
        return database.insert(SONG_TABLE, null, values);
    }

    public ArrayList<ResultsItem> getSongsFromPlaylist(String playlistId) {
        ArrayList<ResultsItem> arrayList = new ArrayList<>();
        Cursor cursor = database.query(SONG_TABLE,
                null,
                DatabaseContract.PlaylistSongColumns.PLAYLIST_ID + " = ?",
                new String[]{playlistId},
                null, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                arrayList.add(new ResultsItem(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PlaylistSongColumns.TRACK_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.PlaylistSongColumns.TRACK_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.PlaylistSongColumns.ARTIST_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.PlaylistSongColumns.COLLECTION_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.PlaylistSongColumns.ARTWORK_URL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.PlaylistSongColumns.PREVIEW_URL))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    public int getSongCountForPlaylist(String playlistId) {
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM " + SONG_TABLE + " WHERE " + DatabaseContract.PlaylistSongColumns.PLAYLIST_ID + " = ?", new String[]{playlistId});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
}