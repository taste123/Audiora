package com.example.audiora.ui.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.audiora.R;
import com.example.audiora.database.PlaylistHelper;
import com.example.audiora.databinding.FragmentHomeBinding;
import com.example.audiora.model.ChartCategory;
import com.example.audiora.object.UserPlaylist;
import com.example.audiora.ui.activity.MainActivity;
import com.example.audiora.ui.adapter.ChartCategoryAdapter;
import com.example.audiora.ui.adapter.UserPlaylistAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements ChartCategoryAdapter.OnChartClickListener, UserPlaylistAdapter.OnPlaylistClickListener {
    private FragmentHomeBinding binding;
    private PlaylistHelper playlistHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playlistHelper = PlaylistHelper.getInstance(requireContext());
        setupCharts();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupUserPlaylists();
    }

    private void setupUserPlaylists() {
        playlistHelper.open();
        ArrayList<UserPlaylist> allPlaylists = playlistHelper.getAllPlaylists();
        playlistHelper.close();

        if (allPlaylists.isEmpty()) {
            binding.playlistHomeRecyclerView.setVisibility(View.GONE);
        } else {
            binding.playlistHomeRecyclerView.setVisibility(View.VISIBLE);

            List<UserPlaylist> playlistsToShow = allPlaylists.subList(0, Math.min(allPlaylists.size(), 4));
            UserPlaylistAdapter playlistAdapter = new UserPlaylistAdapter(playlistsToShow, this);
            binding.playlistHomeRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            binding.playlistHomeRecyclerView.setAdapter(playlistAdapter);
        }
    }

    private void setupCharts() {
        List<ChartCategory> chartCategories = createChartCategories();
        ChartCategoryAdapter chartAdapter = new ChartCategoryAdapter(chartCategories, this);
        binding.chartsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.chartsRecyclerView.setAdapter(chartAdapter);
    }

    private List<ChartCategory> createChartCategories() {
        List<ChartCategory> list = new ArrayList<>();
        list.add(new ChartCategory("Top 50 Indonesia", "id", "top-songs", 50, R.drawable.top_50_id_cover));
        list.add(new ChartCategory("Hot Hits Indonesia", "id", "hot-tracks", 50, R.drawable.hot_hits_id_cover));
        list.add(new ChartCategory("Top 50 Global", "us", "top-songs", 50, R.drawable.top_50_global_cover));
        return list;
    }

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

    @Override
    public void onPlaylistClick(UserPlaylist playlist) {
        if (getActivity() instanceof MainActivity) {
            SongListFragment songListFragment = SongListFragment.newInstanceForPlaylist(
                    playlist.getName(),
                    playlist.getId()
            );
            ((MainActivity) getActivity()).replaceFragment(songListFragment);
        }
    }
}

