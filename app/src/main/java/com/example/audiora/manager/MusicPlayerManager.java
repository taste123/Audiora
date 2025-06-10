package com.example.audiora.manager;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import com.example.audiora.model.ResultsItem;

import java.io.IOException;

public class MusicPlayerManager implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static final String TAG = "MusicPlayerManager";
    private static MusicPlayerManager instance;

    private MediaPlayer mediaPlayer;
    private ResultsItem currentTrack;
    private final Handler seekBarHandler = new Handler();
    private Runnable updateSeekBarAction;

    private PlayerStateListener listener;

    public interface PlayerStateListener {
        void onStateChange(PlayerState state, ResultsItem track);
        void onProgressUpdate(int progress, int duration);
    }

    public enum PlayerState {
        PLAYING, PAUSED, STOPPED, PREPARING, ERROR
    }

    // Singleton pattern: private constructor and a public static getter
    private MusicPlayerManager() {}

    public static synchronized MusicPlayerManager getInstance() {
        if (instance == null) {
            instance = new MusicPlayerManager();
        }
        return instance;
    }

    public void setPlayerStateListener(PlayerStateListener listener) {
        this.listener = listener;
    }

    public void playOrPause(ResultsItem track) {
        if (currentTrack == null || currentTrack.getTrackId() != track.getTrackId()) {
            playNewTrack(track);
        } else if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            pause();
        } else if (mediaPlayer != null) {
            resume();
        }
    }

    private void playNewTrack(ResultsItem track) {
        stop();
        currentTrack = track;
        String url = currentTrack.getPreviewUrl();
        if (url == null || url.isEmpty()) {
            if (listener != null) listener.onStateChange(PlayerState.ERROR, track);
            return;
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build());

        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.prepareAsync();
            if (listener != null) listener.onStateChange(PlayerState.PREPARING, currentTrack);
        } catch (IOException e) {
            Log.e(TAG, "setDataSource failed", e);
            stop();
        }
    }

    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            stopSeekBarUpdates();
            if (listener != null) listener.onStateChange(PlayerState.PAUSED, currentTrack);
        }
    }

    private void resume() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            startSeekBarUpdates();
            if (listener != null) listener.onStateChange(PlayerState.PLAYING, currentTrack);
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            stopSeekBarUpdates();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (listener != null && currentTrack != null) {
            listener.onStateChange(PlayerState.STOPPED, currentTrack);
        }
        currentTrack = null;
    }

    public void seekTo(int progress) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(progress);
        }
    }

    private void startSeekBarUpdates() {
        if (updateSeekBarAction == null) {
            updateSeekBarAction = () -> {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    if (listener != null) {
                        listener.onProgressUpdate(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());
                    }
                    seekBarHandler.postDelayed(updateSeekBarAction, 500);
                }
            };
        }
        seekBarHandler.post(updateSeekBarAction);
    }

    private void stopSeekBarUpdates() {
        seekBarHandler.removeCallbacks(updateSeekBarAction);
    }

    // MediaPlayer Listeners
    @Override
    public void onPrepared(MediaPlayer mp) {
        resume(); // Start playing once prepared
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stop(); // Stop and clean up when the track finishes
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "MediaPlayer Error: what=" + what + ", extra=" + extra);
        stop();
        return true;
    }

    // --- Public Getters for UI state ---
    public ResultsItem getCurrentTrack() {
        return currentTrack;
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public int getDuration() {
        return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
    }
}