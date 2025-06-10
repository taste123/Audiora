package com.example.audiora.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.audiora.database.PlaylistHelper;
import com.example.audiora.databinding.FragmentLibraryBinding;
import com.example.audiora.object.UserPlaylist;
import com.example.audiora.ui.activity.MainActivity;
import com.example.audiora.ui.adapter.UserPlaylistAdapter;
import java.util.ArrayList;

public class LibraryFragment extends Fragment implements UserPlaylistAdapter.OnPlaylistClickListener {

    private FragmentLibraryBinding binding;
    private PlaylistHelper playlistHelper;
    private UserPlaylistAdapter playlistAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLibraryBinding.inflate(inflater, container, false);
        playlistHelper = PlaylistHelper.getInstance(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        binding.createPlaylistItem.setOnClickListener(v -> showCreatePlaylistDialog());
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPlaylists(); // Refresh list every time the fragment is shown
    }

    private void setupRecyclerView() {
        binding.libraryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        playlistAdapter = new UserPlaylistAdapter(new ArrayList<>(), this);
        binding.libraryRecyclerView.setAdapter(playlistAdapter);
    }

    private void loadPlaylists() {
        playlistHelper.open();
        ArrayList<UserPlaylist> playlists = playlistHelper.getAllPlaylists();
        playlistHelper.close();
        if (playlists.isEmpty()) {
            binding.noLibraryItemsCard.setVisibility(View.VISIBLE);
        } else {
            binding.noLibraryItemsCard.setVisibility(View.GONE);
        }
        playlistAdapter = new UserPlaylistAdapter(playlists, this);
        binding.libraryRecyclerView.setAdapter(playlistAdapter);
    }

    private void showCreatePlaylistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("New Playlist");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Playlist Name");
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String playlistName = input.getText().toString().trim();
            if (!playlistName.isEmpty()) {
                playlistHelper.open();
                playlistHelper.createPlaylist(playlistName);
                playlistHelper.close();
                loadPlaylists(); // Refresh the list
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}