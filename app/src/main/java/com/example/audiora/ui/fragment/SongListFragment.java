package com.example.audiora.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.audiora.api.ApiService;
import com.example.audiora.api.RetrofitClient;
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

import java.util.List;

import retrofit2.Call;

public class SongListFragment extends Fragment implements RssTrackAdapter.OnRssTrackClickListener {

    private FragmentSongListBinding binding;
    private RssTrackAdapter rssTrackAdapter;
    private TrackAdapter trackAdapter;
    private ApiService apiService;

    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_COUNTRY = "ARG_COUNTRY";
    private static final String ARG_FEED_TYPE = "ARG_FEED_TYPE";
    private static final String ARG_LIMIT = "ARG_LIMIT";
    private static final String ARG_TYPE = "ARG_TYPE";
    private static final String ARG_PLAYLIST_ID = "ARG_PLAYLIST_ID";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSongListBinding.inflate(inflater, container, false);
        apiService = RetrofitClient.getApiService();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();

        if (getArguments() != null) {
            String title = getArguments().getString(ARG_TITLE);
            String country = getArguments().getString(ARG_COUNTRY);
            String feedType = getArguments().getString(ARG_FEED_TYPE);
            int limit = getArguments().getInt(ARG_LIMIT, 50);

            binding.songListTitle.setText(title);
            fetchChartSongs(country, feedType, limit);
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
        // Here you would use PlaylistHelper to get songs and update an adapter
        // This part needs a new adapter that works with ResultsItem, like your TrackAdapter
        // For now, let's just show a Toast
        Toast.makeText(getContext(), "Loading songs for playlist ID: " + playlistId, Toast.LENGTH_LONG).show();
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

