package com.example.audiora.ui.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.audiora.R;
import com.example.audiora.database.PlaylistHelper;
import com.example.audiora.databinding.ActivitySongDetailBinding;
import com.example.audiora.manager.MusicPlayerManager; // Make sure the package is correct
import com.example.audiora.model.ResultsItem;
import com.example.audiora.object.UserPlaylist;

import java.util.ArrayList;

public class SongDetailActivity extends AppCompatActivity implements MusicPlayerManager.PlayerStateListener {

    private ActivitySongDetailBinding binding;
    private MusicPlayerManager playerManager;
    private PlaylistHelper playlistHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySongDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the single instance of our player manager
        playerManager = MusicPlayerManager.getInstance();
        playlistHelper = PlaylistHelper.getInstance(this);

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

        // Add to playlist button
        binding.infoBtn.setOnClickListener(v -> {
            if (playerManager.getCurrentTrack() != null) {
                showAddToPlaylistDialog(playerManager.getCurrentTrack());
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

    private void showAddToPlaylistDialog(ResultsItem track) {
        try {
            playlistHelper.open();
            ArrayList<UserPlaylist> playlists = playlistHelper.getAllPlaylists();
            playlistHelper.close();

            Log.d("SongDetailActivity", "Number of playlists found: " + playlists.size());

            if (playlists.isEmpty()) {
                Toast.makeText(this, "No playlists created yet. Create a playlist first.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a list of playlist names for the dialog
            CharSequence[] playlistNames = new CharSequence[playlists.size()];
            for (int i = 0; i < playlists.size(); i++) {
                playlistNames[i] = playlists.get(i).getName();
                Log.d("SongDetailActivity", "Playlist " + i + ": " + playlists.get(i).getName() + " (ID: " + playlists.get(i).getId() + ")");
            }

            // Show the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add to Playlist");
            builder.setItems(playlistNames, (dialog, which) -> {
                // 'which' is the index of the selected item
                UserPlaylist selectedPlaylist = playlists.get(which);
                Log.d("SongDetailActivity", "Selected playlist: " + selectedPlaylist.getName() + " (ID: " + selectedPlaylist.getId() + ")");
                addSongToSelectedPlaylist(selectedPlaylist.getId(), track);
            });
            builder.show();
        } catch (Exception e) {
            Log.e("SongDetailActivity", "Error in showAddToPlaylistDialog", e);
            Toast.makeText(this, "Error loading playlists: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void addSongToSelectedPlaylist(int playlistId, ResultsItem track) {
        try {
            playlistHelper.open();
            
            // Check if song already exists in playlist
            ArrayList<ResultsItem> existingSongs = playlistHelper.getSongsFromPlaylist(String.valueOf(playlistId));
            for (ResultsItem existingSong : existingSongs) {
                if (existingSong.getTrackId() == track.getTrackId()) {
                    Toast.makeText(this, "This song is already in the playlist", Toast.LENGTH_SHORT).show();
                    playlistHelper.close();
                    return;
                }
            }
            
            long result = playlistHelper.addSongToPlaylist(String.valueOf(playlistId), track);
            playlistHelper.close();

            if (result > 0) {
                Toast.makeText(this, "Added '" + track.getTrackName() + "' to playlist", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to add song to playlist", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error adding song: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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