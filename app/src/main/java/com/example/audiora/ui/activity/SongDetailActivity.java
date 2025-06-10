package com.example.audiora.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.audiora.R;
import com.example.audiora.databinding.ActivitySongDetailBinding;
import com.example.audiora.manager.MusicPlayerManager; // Make sure the package is correct
import com.example.audiora.model.ResultsItem;

public class SongDetailActivity extends AppCompatActivity implements MusicPlayerManager.PlayerStateListener {

    private ActivitySongDetailBinding binding;
    private MusicPlayerManager playerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySongDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the single instance of our player manager
        playerManager = MusicPlayerManager.getInstance();

        setupUIListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register this activity to listen for player state changes
        playerManager.setPlayerStateListener(this);
        // Immediately update the UI to match the current player state
        updateUI(playerManager.getCurrentTrack());
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister listener to prevent memory leaks and unwanted UI updates
        playerManager.setPlayerStateListener(null);
    }

    private void setupUIListeners() {
        // Back button to close the activity
        binding.backArrowButton.setOnClickListener(v -> onBackPressed());

        // Play/Pause button sends command to the manager
        binding.playPauseBtn.setOnClickListener(v -> {
            if (playerManager.getCurrentTrack() != null) {
                playerManager.playOrPause(playerManager.getCurrentTrack());
            }
        });

        // SeekBar sends seek commands to the manager
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    playerManager.seekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    // This method receives state updates FROM the manager
    @Override
    public void onStateChange(MusicPlayerManager.PlayerState state, ResultsItem track) {
        updateUI(track);
    }

    // This method receives progress updates FROM the manager
    @Override
    public void onProgressUpdate(int progress, int duration) {
        binding.seekBar.setMax(duration);
        binding.seekBar.setProgress(progress);
    }

    // A single method to refresh all views based on the current state
    private void updateUI(ResultsItem track) {
        if (track != null) {
            binding.songTitle.setText(track.getTrackName());
            binding.artistName.setText(track.getArtistName());
            binding.playPauseBtn.setImageResource(
                    playerManager.isPlaying() ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play
            );
            Glide.with(this)
                    .load(track.getArtworkUrl100().replace("100x100", "600x600")) // Get a larger image
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(binding.albumCover);
            binding.seekBar.setMax(playerManager.getDuration());
            binding.albumCover.setVisibility(View.VISIBLE);
            binding.songTitle.setVisibility(View.VISIBLE);
            binding.artistName.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
    }
}