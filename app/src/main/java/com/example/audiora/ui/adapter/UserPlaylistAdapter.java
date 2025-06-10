package com.example.audiora.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.audiora.R;
import com.example.audiora.databinding.ItemPlaylistHomeBinding;
import com.example.audiora.object.UserPlaylist;
import java.util.List;

public class UserPlaylistAdapter extends RecyclerView.Adapter<UserPlaylistAdapter.PlaylistViewHolder> {

    private final List<UserPlaylist> playlistList;
    private final OnPlaylistClickListener listener;

    public interface OnPlaylistClickListener {
        void onPlaylistClick(UserPlaylist playlist);
        void onPlaylistLongClick(UserPlaylist playlist);
    }

    public UserPlaylistAdapter(List<UserPlaylist> playlistList, OnPlaylistClickListener listener) {
        this.playlistList = playlistList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPlaylistHomeBinding binding = ItemPlaylistHomeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PlaylistViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        UserPlaylist currentPlaylist = playlistList.get(position);
        holder.bind(currentPlaylist, listener);
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        private final ItemPlaylistHomeBinding binding;

        public PlaylistViewHolder(ItemPlaylistHomeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final UserPlaylist playlist, final OnPlaylistClickListener listener) {
            binding.playlistName.setText(playlist.getName());
            binding.playlistSongCount.setText(playlist.getSongCount() + " songs");
            // You can set a default image or one based on the first song later
            binding.playlistCoverImageView.setImageResource(R.drawable.ic_launcher_background);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPlaylistClick(playlist);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onPlaylistLongClick(playlist);
                    return true;
                }
                return false;
            });
        }
    }
}