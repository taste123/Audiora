package com.example.audiora.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.audiora.R;
import com.example.audiora.database.PlaylistHelper;
import com.example.audiora.databinding.FragmentHomeBinding;
import com.example.audiora.model.ChartCategory;
import com.example.audiora.object.UserPlaylist;
import com.example.audiora.ui.activity.MainActivity;
import com.example.audiora.ui.adapter.ChartCategoryAdapter;
import com.example.audiora.ui.adapter.PlaylistAdapter;
import com.example.audiora.api.ApiService;
import com.example.audiora.api.RetrofitClient;
import com.example.audiora.model.Response;
import com.example.audiora.model.rss.RssResponse;
import com.example.audiora.model.rss.Entry;
import com.example.audiora.model.rss.ImImageItem;
import com.example.audiora.helper.ThemeHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class HomeFragment extends Fragment implements ChartCategoryAdapter.OnChartClickListener, PlaylistAdapter.OnPlaylistClickListener {
    private FragmentHomeBinding binding;
    private PlaylistAdapter playlistAdapter;
    private PlaylistHelper playlistHelper;
    private ApiService apiService;
    private List<ChartCategory> chartCategories;
    private ThemeHelper themeHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        apiService = RetrofitClient.getApiService();
        themeHelper = new ThemeHelper(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.themeSwitch.setChecked(themeHelper.isDarkMode());

        binding.themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            themeHelper.setDarkMode(isChecked);
            if (getActivity() != null) {
                themeHelper.applyTheme(getActivity().getWindow().getDecorView());
            }
        });

        themeHelper.applyTheme(view);

        setupRecyclerView();
        setupCharts();
        loadPlaylists();
    }

    /**
     * Initializes the RecyclerView for displaying user playlists
     * Sets up a 2-column grid layout and attaches the playlist adapter
     */
    private void setupRecyclerView() {
        playlistAdapter = new PlaylistAdapter(this);
        binding.playlistHomeRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.playlistHomeRecyclerView.setAdapter(playlistAdapter);
    }

    /**
     * Loads all user playlists from the database and displays them
     * Shows a toast message if no playlists are found or if there's an error
     */
    public void loadPlaylists() {
        try {
            playlistHelper = PlaylistHelper.getInstance(requireContext());
            playlistHelper.open();
            ArrayList<UserPlaylist> playlists = playlistHelper.getAllPlaylists();
            playlistHelper.close();

            if (playlists.isEmpty()) {
                Toast.makeText(getContext(), "No playlists found", Toast.LENGTH_SHORT).show();
            } else {
                playlistAdapter.setPlaylists(playlists);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error loading playlists: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sets up the charts section by creating chart categories and initializing the adapter
     * Fetches chart data for each category to get their cover images
     */
    private void setupCharts() {
        chartCategories = createChartCategories();
        ChartCategoryAdapter chartAdapter = new ChartCategoryAdapter(chartCategories, this);
        binding.chartsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.chartsRecyclerView.setAdapter(chartAdapter);

        for (ChartCategory chart : chartCategories) {
            fetchChartData(chart);
        }
    }

    /**
     * Fetches chart data from the API for a specific chart category
     * Uses the first song's artwork as the chart cover image
     * @param chart The chart category to fetch data for
     */
    private void fetchChartData(ChartCategory chart) {
        Call<RssResponse> call = apiService.getTopSongs(chart.getCountry(), chart.getLimit());
        Log.d("HomeFragment", "Fetching chart data for " + chart.getTitle() + " with country: " + chart.getCountry());
        
        call.enqueue(new Callback<RssResponse>() {
            @Override
            public void onResponse(Call<RssResponse> call, retrofit2.Response<RssResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().getFeed().getEntry().isEmpty()) {
                    Entry firstEntry = response.body().getFeed().getEntry().get(0);
                    List<ImImageItem> images = firstEntry.getImImage();
                    if (images != null && !images.isEmpty()) {
                        String artworkUrl = images.get(images.size() - 1).getLabel();
                        Log.d("HomeFragment", "Setting cover image for " + chart.getTitle() + ": " + artworkUrl);
                        chart.setCoverImageUrl(artworkUrl);
                        
                        if (binding.chartsRecyclerView.getAdapter() != null) {
                            binding.chartsRecyclerView.getAdapter().notifyDataSetChanged();
                        }
                    } else {
                        Log.e("HomeFragment", "No images found for " + chart.getTitle());
                    }
                } else {
                    Log.e("HomeFragment", "Failed to get chart data for " + chart.getTitle() + 
                        ": " + (response.body() == null ? "null response" : "empty entries"));
                }
            }

            @Override
            public void onFailure(Call<RssResponse> call, Throwable t) {
                Log.e("HomeFragment", "Error loading chart for " + chart.getTitle() + ": " + t.getMessage());
                Toast.makeText(getContext(), "Error loading chart: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Creates a list of chart categories with predefined settings
     * @return List of chart categories including Top 50 Indonesia, Hot Hits Indonesia, and Top 50 Global
     */
    private List<ChartCategory> createChartCategories() {
        List<ChartCategory> list = new ArrayList<>();
        list.add(new ChartCategory("Top 50 Indonesia", "id", "top-songs", 50, R.drawable.top_50_id_cover));
        list.add(new ChartCategory("Hot Hits Indonesia", "id", "hot-tracks", 50, R.drawable.hot_hits_id_cover));
        list.add(new ChartCategory("Top 50 Global", "us", "top-songs", 50, R.drawable.top_50_global_cover));
        return list;
    }

    /**
     * Handles click events on chart items
     * Navigates to the SongListFragment to display the selected chart's songs
     * @param chart The selected chart category
     */
    @Override
    public void onChartClick(ChartCategory chart) {
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();

            SongListFragment songListFragment = SongListFragment.newInstanceFprChart(
                    chart.getTitle(),
                    chart.getCountry(),
                    chart.getFeedType(),
                    chart.getLimit()
            );
            mainActivity.replaceFragment(songListFragment);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Handles click events on playlist items
     * Navigates to the SongListFragment to display the selected playlist's songs
     * @param playlist The selected playlist
     */
    @Override
    public void onPlaylistClick(UserPlaylist playlist) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigateToSongList(playlist.getName(), playlist.getId());
        }
    }

    /**
     * Handles long click events on playlist items
     * Currently shows a toast message for future implementation of playlist editing
     * @param playlist The selected playlist
     */
    @Override
    public void onPlaylistLongClick(UserPlaylist playlist) {
        Toast.makeText(getContext(), "Edit playlist: " + playlist.getName(), Toast.LENGTH_SHORT).show();
    }
}

