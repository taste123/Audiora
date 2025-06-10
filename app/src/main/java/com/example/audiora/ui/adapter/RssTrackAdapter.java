package com.example.audiora.ui.adapter; // Or your adapter package

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.audiora.R;
import com.example.audiora.databinding.ItemTrackBinding;
import com.example.audiora.model.rss.Entry;

import java.util.List;

public class RssTrackAdapter extends RecyclerView.Adapter<RssTrackAdapter.RssTrackViewHolder> {

    private final List<Entry> trackList;
    private final OnRssTrackClickListener listener;
    private int currentPlayingTrackId = -1; // To track the playing item's ID

    public interface OnRssTrackClickListener {
        void onRssTrackClicked(Entry track);
    }

    public RssTrackAdapter(List<Entry> trackList, OnRssTrackClickListener listener) {
        this.trackList = trackList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RssTrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTrackBinding binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RssTrackViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RssTrackViewHolder holder, int position) {
        Entry currentTrack = trackList.get(position);
        // Determine if this item is the one currently playing
        boolean isPlaying = false;
        if (currentPlayingTrackId != -1) {
            isPlaying = Integer.parseInt(currentTrack.getId().getAttributes().getImId()) == currentPlayingTrackId;
        }
        holder.bind(currentTrack, listener, isPlaying);
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    // *** ADD THIS METHOD TO UPDATE THE PLAYING STATE FROM THE ACTIVITY ***
    public void setCurrentPlayingTrackId(int trackId) {
        int previousTrackId = currentPlayingTrackId;
        currentPlayingTrackId = trackId;
        // Notify changes for the old and new playing items to update their highlight
        if (previousTrackId != -1) notifyItemChangedByTrackId(previousTrackId);
        if (currentPlayingTrackId != -1) notifyItemChangedByTrackId(currentPlayingTrackId);
    }

    private void notifyItemChangedByTrackId(int trackId) {
        for (int i = 0; i < trackList.size(); i++) {
            if (Integer.parseInt(trackList.get(i).getId().getAttributes().getImId()) == trackId) {
                notifyItemChanged(i);
                return;
            }
        }
    }


    static class RssTrackViewHolder extends RecyclerView.ViewHolder {
        private final ItemTrackBinding binding;

        public RssTrackViewHolder(ItemTrackBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final Entry track, final OnRssTrackClickListener listener, boolean isPlaying) {
            binding.titleTrack.setText(track.getImName().getLabel());
            binding.artistName.setText(track.getImArtist().getLabel());
            binding.albumName.setText(track.getImCollection().getImName().getLabel());

            String imageUrl = "";
            if (track.getImImage() != null && !track.getImImage().isEmpty()) {
                imageUrl = track.getImImage().get(track.getImImage().size() - 1).getLabel();
            }

            Glide.with(binding.getRoot().getContext()).load(imageUrl).into(binding.albumCover);

            // *** VISUAL FEEDBACK LOGIC ***
            if (isPlaying) {
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.darker_gray));
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), android.R.color.transparent));
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRssTrackClicked(track);
                }
            });
        }
    }
}