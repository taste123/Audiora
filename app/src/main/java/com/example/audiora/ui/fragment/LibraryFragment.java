package com.example.audiora.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.audiora.database.PlaylistHelper;
import com.example.audiora.databinding.FragmentLibraryBinding;
import com.example.audiora.object.UserPlaylist;
import com.example.audiora.ui.activity.MainActivity;
import com.example.audiora.ui.adapter.PlaylistAdapter;

import java.util.ArrayList;

public class LibraryFragment extends Fragment implements PlaylistAdapter.OnPlaylistClickListener {

    private FragmentLibraryBinding binding;
    private PlaylistAdapter playlistAdapter;
    private PlaylistHelper playlistHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLibraryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupCreatePlaylistButton();
        loadPlaylists();
    }

    private void setupRecyclerView() {
        playlistAdapter = new PlaylistAdapter(this);
        binding.libraryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.libraryRecyclerView.setAdapter(playlistAdapter);
    }

    private void setupCreatePlaylistButton() {
        binding.createPlaylistItem.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showCreatePlaylistDialog();
            }
        });
    }

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

    @Override
    public void onPlaylistClick(UserPlaylist playlist) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigateToSongList(playlist.getName(), playlist.getId());
        }
    }

    @Override
    public void onPlaylistLongClick(UserPlaylist playlist) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showEditPlaylistDialog(playlist);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPlaylists();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}