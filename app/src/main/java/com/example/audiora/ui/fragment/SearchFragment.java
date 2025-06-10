package com.example.audiora.ui.fragment;

import android.app.AlertDialog;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;

import com.example.audiora.R;
import com.example.audiora.api.ApiService;
import com.example.audiora.api.RetrofitClient;
import com.example.audiora.database.PlaylistHelper;
import com.example.audiora.databinding.FragmentSearchBinding;
import com.example.audiora.model.ResultsItem;
import com.example.audiora.object.UserPlaylist;
import com.example.audiora.ui.activity.MainActivity;
import com.example.audiora.ui.adapter.TrackAdapter;
import com.example.audiora.model.Response;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class SearchFragment extends Fragment implements TrackAdapter.OnTrackClickListener {

    private FragmentSearchBinding binding;
    private ApiService apiService;
    private TrackAdapter trackAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        apiService = RetrofitClient.getApiService();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupSearchView();
        setupReloadButton();
    }

    private void setupRecyclerView() {
        trackAdapter = new TrackAdapter(new ArrayList<>(), this);
        binding.searchRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.searchRecyclerView.setAdapter(trackAdapter);
    }

    private void setupSearchView() {
        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.isEmpty()) {
                    performSearch(query);
                    binding.searchBar.clearFocus();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setupReloadButton() {
        binding.reloadButton.setOnClickListener(v -> {
            if (binding.searchBar.getQuery() != null && !binding.searchBar.getQuery().toString().isEmpty()) {
                performSearch(binding.searchBar.getQuery().toString());
            }
        });
    }

    private void showError(String message, boolean showReload) {
        binding.progressBar.setVisibility(View.GONE);
        binding.searchRecyclerView.setVisibility(View.GONE);
        binding.errorLayout.setVisibility(View.VISIBLE);
        binding.errorText.setText(message);
        binding.reloadButton.setVisibility(showReload ? View.VISIBLE : View.GONE);
    }

    private void hideError() {
        binding.errorLayout.setVisibility(View.GONE);
        binding.searchRecyclerView.setVisibility(View.VISIBLE);
    }

    private void performSearch(String query) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.searchRecyclerView.setVisibility(View.GONE);
        binding.errorLayout.setVisibility(View.GONE);

        Call<Response> call = apiService.searchMusicSongs(query, 20);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                binding.progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    List<ResultsItem> tracks = response.body().getResults();
                    if (tracks.isEmpty()) {
                        showError("We couldn't find any songs matching '" + query + "'", false);
                    } else {
                        hideError();
                        trackAdapter.setTrack(tracks);
                    }
                } else {
                    showError("We're having trouble finding songs right now. Please try again.", true);
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                showError("Looks like you're offline. Please check your internet connection.", true);
            }
        });
    }

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

    @Override
    public void onTrackLongClick(ResultsItem track) {
        // No action needed for search results
    }

    private void showAddToPlaylistDialog(ResultsItem track) {
        try {
            PlaylistHelper playlistHelper = PlaylistHelper.getInstance(requireContext());
            playlistHelper.open();
            ArrayList<UserPlaylist> playlists = playlistHelper.getAllPlaylists();
            playlistHelper.close();

            Log.d("SearchFragment", "Number of playlists found: " + playlists.size());

            if (playlists.isEmpty()) {
                Toast.makeText(getContext(), "No playlists created yet. Create a playlist first.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a list of playlist names for the dialog
            CharSequence[] playlistNames = new CharSequence[playlists.size()];
            for (int i = 0; i < playlists.size(); i++) {
                playlistNames[i] = playlists.get(i).getName();
                Log.d("SearchFragment", "Playlist " + i + ": " + playlists.get(i).getName() + " (ID: " + playlists.get(i).getId() + ")");
            }

            // Show the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Add to Playlist");
            builder.setItems(playlistNames, (dialog, which) -> {
                // 'which' is the index of the selected item
                UserPlaylist selectedPlaylist = playlists.get(which);
                Log.d("SearchFragment", "Selected playlist: " + selectedPlaylist.getName() + " (ID: " + selectedPlaylist.getId() + ")");
                addSongToSelectedPlaylist(selectedPlaylist.getId(), track);
            });
            builder.show();
        } catch (Exception e) {
            Log.e("SearchFragment", "Error in showAddToPlaylistDialog", e);
            Toast.makeText(getContext(), "Error loading playlists: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void addSongToSelectedPlaylist(int playlistId, ResultsItem track) {
        try {
            PlaylistHelper playlistHelper = PlaylistHelper.getInstance(requireContext());
            playlistHelper.open();
            
            // Check if song already exists in playlist
            ArrayList<ResultsItem> existingSongs = playlistHelper.getSongsFromPlaylist(String.valueOf(playlistId));
            for (ResultsItem existingSong : existingSongs) {
                if (existingSong.getTrackId() == track.getTrackId()) {
                    Toast.makeText(getContext(), "This song is already in the playlist", Toast.LENGTH_SHORT).show();
                    playlistHelper.close();
                    return;
                }
            }
            
            long result = playlistHelper.addSongToPlaylist(String.valueOf(playlistId), track);
            playlistHelper.close();

            if (result > 0) {
                Toast.makeText(getContext(), "Added '" + track.getTrackName() + "' to playlist", Toast.LENGTH_SHORT).show();
                
                // Refresh HomeFragment if it exists
                if (getActivity() instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    HomeFragment homeFragment = mainActivity.getHomeFragment();
                    if (homeFragment != null) {
                        homeFragment.loadPlaylists();
                    }
                }
            } else {
                Toast.makeText(getContext(), "Failed to add song to playlist", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error adding song: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

