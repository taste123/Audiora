package com.example.audiora.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.audiora.api.ApiService;
import com.example.audiora.api.RetrofitClient;
import com.example.audiora.database.PlaylistHelper;
import com.example.audiora.model.ResultsItem;
import com.example.audiora.object.UserPlaylist;
import com.example.audiora.model.rss.Entry;
import com.example.audiora.model.rss.RssResponse;
import com.example.audiora.ui.activity.MainActivity;
import com.example.audiora.ui.adapter.RssTrackAdapter;
import com.example.audiora.ui.adapter.TrackAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.audiora.databinding.FragmentSongListBinding;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.ArrayList;

import retrofit2.Call;

public class SongListFragment extends Fragment implements RssTrackAdapter.OnRssTrackClickListener {

    private FragmentSongListBinding binding;
    private RssTrackAdapter rssTrackAdapter;
    private TrackAdapter trackAdapter;
    private ApiService apiService;
    private int currentPlaylistId;
    private PlaylistHelper playlistHelper;

    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_COUNTRY = "ARG_COUNTRY";
    private static final String ARG_FEED_TYPE = "ARG_FEED_TYPE";
    private static final String ARG_LIMIT = "ARG_LIMIT";
    private static final String ARG_TYPE = "ARG_TYPE";
    private static final String ARG_PLAYLIST_ID = "ARG_PLAYLIST_ID";

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        updatePlaylistCover(selectedImageUri);
                    }
                }
            }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSongListBinding.inflate(inflater, container, false);
        apiService = RetrofitClient.getApiService();
        playlistHelper = PlaylistHelper.getInstance(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();

        if (getArguments() != null) {
            String type = getArguments().getString(ARG_TYPE);
            String title = getArguments().getString(ARG_TITLE);
            binding.songListTitle.setText(title);

            if ("user_playlist".equals(type)) {
                currentPlaylistId = getArguments().getInt(ARG_PLAYLIST_ID);
                loadSongsFromPlaylist(currentPlaylistId);
                setupPlaylistCover();
                setupEditableTitle();
            } else {
                String country = getArguments().getString(ARG_COUNTRY);
                String feedType = getArguments().getString(ARG_FEED_TYPE);
                int limit = getArguments().getInt(ARG_LIMIT, 50);
                fetchChartSongs(country, feedType, limit);
                // Make title non-editable for charts
                binding.songListTitle.setFocusable(false);
                binding.songListTitle.setFocusableInTouchMode(false);
            }
        }
    }

    private void setupPlaylistCover() {
        playlistHelper.open();
        ArrayList<UserPlaylist> playlists = playlistHelper.getAllPlaylists();
        playlistHelper.close();

        for (UserPlaylist playlist : playlists) {
            if (playlist.getId() == currentPlaylistId) {
                if (playlist.getCoverImage() != null && !playlist.getCoverImage().isEmpty()) {
                    Glide.with(this)
                            .load(playlist.getCoverImage())
                            .into(binding.playlistCover);
                }
                break;
            }
        }

        binding.playlistCover.setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void updatePlaylistCover(Uri imageUri) {
        try {
            playlistHelper.open();
            int result = playlistHelper.updatePlaylistCover(currentPlaylistId, imageUri.toString());
            playlistHelper.close();

            if (result > 0) {
                Glide.with(this)
                        .load(imageUri)
                        .into(binding.playlistCover);
                Toast.makeText(getContext(), "Playlist cover updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to update playlist cover", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error updating cover: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupEditableTitle() {
        binding.songListTitle.setOnClickListener(v -> {
            if (getArguments() != null && "user_playlist".equals(getArguments().getString(ARG_TYPE))) {
                showEditTitleDialog();
            }
        });
    }

    private void showEditTitleDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Playlist Title");

        // Set up the input
        final android.widget.EditText input = new android.widget.EditText(requireContext());
        input.setText(binding.songListTitle.getText());
        input.setSelection(input.getText().length());
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newTitle = input.getText().toString().trim();
            if (!newTitle.isEmpty()) {
                binding.songListTitle.setText(newTitle);
                updatePlaylistTitle(newTitle);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updatePlaylistTitle(String newTitle) {
        try {
            playlistHelper.open();
            int result = playlistHelper.updatePlaylistTitle(currentPlaylistId, newTitle);
            playlistHelper.close();

            if (result > 0) {
                Toast.makeText(getContext(), "Playlist title updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to update playlist title", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error updating title: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static SongListFragment newInstanceFprChart(String title, String country, String feedType, int limit) {
        SongListFragment fragment = new SongListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, "chart");
        args.putString(ARG_TITLE, title);
        args.putString(ARG_COUNTRY, country);
        args.putString(ARG_FEED_TYPE, feedType);
        args.putInt(ARG_LIMIT, limit);
        fragment.setArguments(args);
        return fragment;
    }

    public static SongListFragment newInstanceForPlaylist(String title, int playlistId) {
        SongListFragment fragment = new SongListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, "user_playlist");
        args.putString(ARG_TITLE, title);
        args.putInt(ARG_PLAYLIST_ID, playlistId);
        fragment.setArguments(args);
        return fragment;
    }

    private void loadSongsFromPlaylist(int playlistId) {
        try {
            binding.progressBar.setVisibility(View.VISIBLE);
            
            // Initialize TrackAdapter for playlist songs
            trackAdapter = new TrackAdapter(new ArrayList<>(), new TrackAdapter.OnTrackClickListener() {
                @Override
                public void onTrackClick(ResultsItem track) {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).playOrPauseTrack(track);
                    }
                }

                @Override
                public void onInfoClick(ResultsItem track) {
                    showAddToPlaylistDialog(track);
                }
            });
            binding.songListRecyclerView.setAdapter(trackAdapter);

            // Load songs from database
            playlistHelper.open();
            ArrayList<ResultsItem> songs = playlistHelper.getSongsFromPlaylist(String.valueOf(playlistId));
            playlistHelper.close();

            binding.progressBar.setVisibility(View.GONE);
            
            if (songs.isEmpty()) {
                Toast.makeText(getContext(), "No songs in this playlist", Toast.LENGTH_SHORT).show();
            } else {
                trackAdapter.setTrack(songs);
            }
        } catch (Exception e) {
            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Error loading playlist: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddToPlaylistDialog(ResultsItem track) {
        try {
            playlistHelper.open();
            ArrayList<UserPlaylist> playlists = playlistHelper.getAllPlaylists();
            playlistHelper.close();

            if (playlists.isEmpty()) {
                Toast.makeText(getContext(), "No playlists available", Toast.LENGTH_SHORT).show();
                return;
            }

            // Filter out the current playlist if we're in a playlist view
            if (getArguments() != null && "user_playlist".equals(getArguments().getString(ARG_TYPE))) {
                playlists.removeIf(playlist -> playlist.getId() == currentPlaylistId);
            }

            if (playlists.isEmpty()) {
                Toast.makeText(getContext(), "No other playlists available", Toast.LENGTH_SHORT).show();
                return;
            }

            String[] playlistNames = new String[playlists.size()];
            for (int i = 0; i < playlists.size(); i++) {
                playlistNames[i] = playlists.get(i).getName();
            }

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
            builder.setTitle("Add to Playlist");
            builder.setItems(playlistNames, (dialog, which) -> {
                UserPlaylist selectedPlaylist = playlists.get(which);
                addSongToPlaylist(selectedPlaylist.getId(), track);
            });
            builder.show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error loading playlists: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void addSongToPlaylist(int playlistId, ResultsItem track) {
        try {
            playlistHelper.open();
            long result = playlistHelper.addSongToPlaylist(String.valueOf(playlistId), track);
            playlistHelper.close();

            if (result != -1) {
                Toast.makeText(getContext(), "Added to playlist", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to add to playlist", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error adding to playlist: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {
        binding.songListRecyclerView.setHasFixedSize(true);
        binding.songListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void fetchChartSongs(String country, String feedType, int limit) {
        binding.progressBar.setVisibility(View.VISIBLE);

        Call<RssResponse> call = apiService.getTopSongs(country, limit);

        call.enqueue(new retrofit2.Callback<RssResponse>() {
            @Override
            public void onResponse(Call<RssResponse> call, retrofit2.Response<RssResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getFeed() != null) {
                    List<Entry> tracks = response.body().getFeed().getEntry();
                    rssTrackAdapter = new RssTrackAdapter(tracks, SongListFragment.this);
                    binding.songListRecyclerView.setAdapter(rssTrackAdapter);
                } else {
                    Toast.makeText(getContext(), "failed to load chart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RssResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRssTrackClicked(Entry track) {
        if (getActivity() instanceof MainActivity) {
            String trackId = track.getId().getAttributes().getImId();
            ((MainActivity) getActivity()).lookUpAndPlayTrack(trackId);
        }
    }

    @Override
    public void onRssTrackInfoClicked(Entry track) {
        // Convert Entry to ResultsItem
        ResultsItem resultsItem = new ResultsItem(
            Integer.parseInt(track.getId().getAttributes().getImId()),
            track.getImName().getLabel(),
            track.getImArtist().getLabel(),
            track.getImCollection() != null ? track.getImCollection().getImName().getLabel() : "",
            track.getImImage().get(track.getImImage().size() - 1).getLabel(),
            track.getLink().get(0).getAttributes().getHref()
        );
        showAddToPlaylistDialog(resultsItem);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

