package com.example.audiora.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.audiora.model.ResultsItem;
import com.example.audiora.object.UserPlaylist;

import java.util.ArrayList;

public class PlaylistHelper extends SQLiteOpenHelper {

    private static final String PLAYLIST_TABLE = DatabaseContract.PlaylistColumns.TABLE_NAME;
    private static final String SONG_TABLE = DatabaseContract.PlaylistSongColumns.TABLE_NAME;
    private static DatabaseHelper databaseHelper;
    private static SQLiteDatabase database;
    private static volatile PlaylistHelper INSTANCE;
    private static final String DATABASE_NAME = "audiora.db";
    private static final int DATABASE_VERSION = 1;

    private PlaylistHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        databaseHelper = new DatabaseHelper(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // This is handled by DatabaseHelper
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This is handled by DatabaseHelper
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
                String coverImage = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.PlaylistColumns.COVER_IMAGE));
                int songCount = getSongCountForPlaylist(String.valueOf(id));
                arrayList.add(new UserPlaylist(id, name, coverImage, songCount));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    public int updatePlaylistTitle(int playlistId, String newTitle) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PlaylistColumns.NAME, newTitle);
        return database.update(PLAYLIST_TABLE, values, 
            DatabaseContract.PlaylistColumns._ID + " = ?", 
            new String[]{String.valueOf(playlistId)});
    }

    public int updatePlaylistCover(int playlistId, String coverImageUri) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PlaylistColumns.COVER_IMAGE, coverImageUri);
        return database.update(PLAYLIST_TABLE, values, 
            DatabaseContract.PlaylistColumns._ID + " = ?", 
            new String[]{String.valueOf(playlistId)});
    }

    // --- Playlist Song Methods ---
    public long addSongToPlaylist(String playlistId, ResultsItem song) {
        try {
            Log.d("PlaylistHelper", "Adding song to playlist. Playlist ID: " + playlistId + ", Song: " + song.getTrackName());
            
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.PlaylistSongColumns.PLAYLIST_ID, playlistId);
            values.put(DatabaseContract.PlaylistSongColumns.TRACK_ID, song.getTrackId());
            values.put(DatabaseContract.PlaylistSongColumns.TRACK_NAME, song.getTrackName());
            values.put(DatabaseContract.PlaylistSongColumns.ARTIST_NAME, song.getArtistName());
            values.put(DatabaseContract.PlaylistSongColumns.COLLECTION_NAME, song.getCollectionName());
            values.put(DatabaseContract.PlaylistSongColumns.ARTWORK_URL, song.getArtworkUrl100());
            values.put(DatabaseContract.PlaylistSongColumns.PREVIEW_URL, song.getPreviewUrl());
            
            long result = database.insert(SONG_TABLE, null, values);
            Log.d("PlaylistHelper", "Insert result: " + result);
            return result;
        } catch (Exception e) {
            Log.e("PlaylistHelper", "Error adding song to playlist", e);
            return -1;
        }
    }

    public ArrayList<ResultsItem> getSongsFromPlaylist(String playlistId) {
        ArrayList<ResultsItem> arrayList = new ArrayList<>();
        Cursor cursor = null;
        try {
            Log.d("PlaylistHelper", "Getting songs from playlist ID: " + playlistId);
            
            cursor = database.query(SONG_TABLE,
                    null,
                    DatabaseContract.PlaylistSongColumns.PLAYLIST_ID + " = ?",
                    new String[]{playlistId},
                    null, null, null, null);
                    
            Log.d("PlaylistHelper", "Found " + cursor.getCount() + " songs in playlist");
            
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
        } catch (Exception e) {
            Log.e("PlaylistHelper", "Error getting songs from playlist", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
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

    public int deleteSongFromPlaylist(String playlistId, ResultsItem track) {
        try {
            Log.d("PlaylistHelper", "Deleting song from playlist. Playlist ID: " + playlistId + ", Track ID: " + track.getTrackId());
            int result = database.delete(SONG_TABLE,
                    DatabaseContract.PlaylistSongColumns.PLAYLIST_ID + " = ? AND " + DatabaseContract.PlaylistSongColumns.TRACK_ID + " = ?",
                    new String[]{playlistId, String.valueOf(track.getTrackId())});
            Log.d("PlaylistHelper", "Delete result: " + result);
            return result;
        } catch (Exception e) {
            Log.e("PlaylistHelper", "Error deleting song from playlist", e);
            return 0;
        }
    }
}