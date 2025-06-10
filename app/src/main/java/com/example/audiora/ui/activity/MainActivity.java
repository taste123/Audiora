package com.example.audiora.ui.activity;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.audiora.R;
import com.example.audiora.api.ApiService;
import com.example.audiora.api.RetrofitClient;
import com.example.audiora.databinding.ActivityMainBinding;
import com.example.audiora.manager.MusicPlayerManager;
import com.example.audiora.model.Response;
import com.example.audiora.model.ResultsItem;
import com.example.audiora.ui.fragment.HomeFragment;
import com.example.audiora.ui.fragment.LibraryFragment;
import com.example.audiora.ui.fragment.SearchFragment;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity implements MusicPlayerManager.PlayerStateListener {
    private ActivityMainBinding binding;
    private MusicPlayerManager playerManager;
    private static final String TAG = "MainActivity";
    private ApiService apiService;

    private MediaPlayer mediaPlayer;
    private ResultsItem currentPlayingTrack;
    private Handler seekBarHandler = new Handler();
    private Runnable updateSeekBarAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        playerManager = MusicPlayerManager.getInstance();

        apiService = RetrofitClient.getApiService();

        replaceFragment(new HomeFragment());
        binding.homeBtn.setOnClickListener(v -> replaceFragment(new HomeFragment()));
        binding.searchBtn.setOnClickListener(v -> replaceFragment(new SearchFragment()));
        binding.libraryBtn.setOnClickListener(v -> replaceFragment(new LibraryFragment()));

        setupPlayerControls();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerManager.setPlayerStateListener(this);
        updatePlayerUI(playerManager.getCurrentTrack());
    }

    @Override
    protected void onPause() {
        super.onPause();
        playerManager.setPlayerStateListener(null);
    }
    private void setupPlayerControls() {
        binding.playPauseBtn.setOnClickListener(v -> {
            if (playerManager.getCurrentTrack() != null) {
                playerManager.playOrPause(playerManager.getCurrentTrack());
            }
        });

        binding.playCard.setOnClickListener(v -> {
            if (playerManager.getCurrentTrack() != null) {
                Intent intent = new Intent(this, SongDetailActivity.class);
                startActivity(intent);
            }
        });

        binding.playSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    playerManager.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
//
//    public void playOrPauseTrack(ResultsItem track) {
//        if (currentPlayingTrack == null || currentPlayingTrack.getTrackId() != track.getTrackId()) {
//            playNewTrack(track);
//        } else if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//            pauseTrack();
//        } else {
//            resumeTrack();
//        }
//    }
//
//    private void playNewTrack(ResultsItem track) {
//        stopPlayBack();
//        currentPlayingTrack = track;
//        String trackUrl = currentPlayingTrack.getPreviewUrl();
//
//        if (trackUrl == null || trackUrl.isEmpty()) {
//            Toast.makeText(this, "No Preview Available", Toast.LENGTH_SHORT).show();
//            currentPlayingTrack = null;
//            return;
//        }
//
//        updatePlayCard(currentPlayingTrack, true);
//        binding.playCard.setVisibility(View.VISIBLE);
//
//        mediaPlayer = new MediaPlayer();
//        mediaPlayer.setAudioAttributes(
//                new AudioAttributes.Builder()
//                        .setUsage(AudioAttributes.USAGE_MEDIA)
//                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                        .build()
//        );
//
//        try {
//            mediaPlayer.setDataSource(trackUrl);
//            mediaPlayer.setOnPreparedListener(mp -> {
//                mp.start();
//                binding.playSeekBar.setMax(mp.getDuration());
//                startSeekBarUpdate();
//            });
//            mediaPlayer.setOnCompletionListener(mp -> stopPlayBack());
//            mediaPlayer.prepareAsync();
//        } catch (IOException e) {
//            stopPlayBack();
//        }
//    }
//
//    private void pauseTrack() {
//        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//            mediaPlayer.pause();
//            stopSeekBarUpdate();
//            binding.playPauseBtn.setImageResource(android.R.drawable.ic_media_play);
//        }
//    }
//
//    private void resumeTrack() {
//        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
//            mediaPlayer.start();
//            startSeekBarUpdate();
//            binding.playPauseBtn.setImageResource(android.R.drawable.ic_media_pause);
//        }
//
//    }
//    private void stopPlayBack() {
//        if (mediaPlayer != null) {
//            stopSeekBarUpdate();
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//    }
//    private void updatePlayCard(ResultsItem track, boolean isPlaying) {
//        binding.playTitle.setText(track.getTrackName());
//        binding.playArtist.setText(track.getArtistName());
//        binding.playPauseBtn.setImageResource(isPlaying ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
//
//        Glide.with(this)
//                .load(track.getArtworkUrl100())
//                .into(binding.playCover);
//    }
//
//    private void startSeekBarUpdate() {
//        if (updateSeekBarAction == null) {
//            updateSeekBarAction = () -> {
//                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                    binding.playSeekBar.setProgress(mediaPlayer.getCurrentPosition());
//                    seekBarHandler.postDelayed(updateSeekBarAction, 500);
//                }
//            };
//        }
//        seekBarHandler.post(updateSeekBarAction);
//    }
//
//    private void stopSeekBarUpdate() {
//        if (seekBarHandler != null && updateSeekBarAction != null) {
//            seekBarHandler.removeCallbacks(updateSeekBarAction);
//        }
//    }

    public void lookUpAndPlayTrack(String id) {
        Call<Response> call = apiService.lookupTrackById(id);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().getResults().isEmpty()) {
                    ResultsItem track = response.body().getResults().get(0);
                    playerManager.playOrPause(track);
                } else {
                    Toast.makeText(MainActivity.this, "Track not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error fetching track: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStateChange(MusicPlayerManager.PlayerState state, ResultsItem track) {
        updatePlayerUI(track);
    }

    @Override
    public void onProgressUpdate(int progress, int duration) {
        binding.playSeekBar.setMax(duration);
        binding.playSeekBar.setProgress(progress);
    }

    private void updatePlayerUI(ResultsItem track) {
        if (track == null) {
            binding.playCard.setVisibility(View.GONE);
            return;
        }

        binding.playCard.setVisibility(View.VISIBLE);
        binding.playTitle.setText(track.getTrackName());
        binding.playArtist.setText(track.getArtistName());

        if (playerManager.isPlaying()) {
            binding.playPauseBtn.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            binding.playPauseBtn.setImageResource(android.R.drawable.ic_media_play);
        }

        Glide.with(this)
                .load(track.getArtworkUrl100())
                .into(binding.playCover);
    }

    public void playOrPauseTrack(ResultsItem track) {
        if (playerManager != null) {
            playerManager.playOrPause(track);
        }
    }


    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragment instanceof HomeFragment) {
            transaction.replace(R.id.fragmentContainer, fragment, "homeFragment");
        } else {
            transaction.replace(R.id.fragmentContainer, fragment);
        }
        transaction.commit();
    }
}

