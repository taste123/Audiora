package com.example.audiora.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.audiora.R;
import com.example.audiora.databinding.ItemTrackBinding;
import com.example.audiora.model.ResultsItem;

import java.util.ArrayList;
import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {
    private List<ResultsItem> trackList;
    private OnTrackClickListener listener;
    private int currentPlayingTrackId = -1;

    public interface OnTrackClickListener {
        void onTrackClick(ResultsItem track);
        void onInfoClick(ResultsItem track);
        void onTrackLongClick(ResultsItem track);
    }

    public TrackAdapter(OnTrackClickListener listener) {
        this.trackList = new ArrayList<>();
        this.listener = listener;
    }

    public TrackAdapter(List<ResultsItem> tracks, OnTrackClickListener listener) {
        this.trackList = tracks != null ? new ArrayList<>(tracks) : new ArrayList<>();
        this.listener = listener;
    }

    public TrackAdapter(List<ResultsItem> tracks) {
        this.trackList = tracks != null ? new ArrayList<>(tracks) : new ArrayList<>();
        // this.listener can be null if not set via constructor
    }

    @NonNull
    @Override
    public TrackAdapter.TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTrackBinding binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TrackViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackAdapter.TrackViewHolder holder, int position) {
        ResultsItem currentTrack = trackList.get(position);
        boolean isPlaying = currentTrack.getTrackId() == currentPlayingTrackId;
        holder.bind(currentTrack, listener, isPlaying);
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    public void setTrack(List<ResultsItem> newTrackList) {
        this.trackList.clear();
        if (newTrackList != null) {
            this.trackList.addAll(newTrackList);
        }
        this.currentPlayingTrackId = -1; //maybe search song while play music, the current playing position will be reset?
        notifyDataSetChanged();
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder {
        private ItemTrackBinding binding;
        public TrackViewHolder(ItemTrackBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(ResultsItem track, OnTrackClickListener listener, boolean isPlaying) {
            binding.titleTrack.setText(track.getTrackName());
            binding.artistName.setText(track.getArtistName());
            binding.albumName.setText(track.getCollectionName());

            Glide.with(binding.getRoot().getContext())
                    .load(track.getArtworkUrl100())
                    .placeholder(R.drawable.ic_launcher_background) // Placeholder image
                    .error(R.drawable.ic_launcher_background) // Error image
                    .into(binding.albumCover);

            if (isPlaying) {
                itemView.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.darker_gray, null)); // Highlight color for playing track
            } else {
                itemView.setBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.transparent, null)); // Default color for non-playing tracks
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTrackClick(track);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onTrackLongClick(track);
                    return true;
                }
                return false;
            });

            binding.infoBtn.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onInfoClick(track);
                }
            });
        }
    }

    public void setCurrentPlayingTrackId(int trackId) {
        int previousPosition = currentPlayingTrackId;
        currentPlayingTrackId = trackId;
        if (previousPosition != -1) {
            notifyItemChangedByTrackId(previousPosition);
        }
        if (currentPlayingTrackId != -1) {
            notifyItemChangedByTrackId(currentPlayingTrackId);
        }
    }

    private void notifyItemChangedByTrackId(int trackId) {
        for (int i = 0; i < trackList.size(); i++) {
            if (trackList.get(i).getTrackId() == trackId) {
                notifyItemChanged(i);
                return;
            }
        }
    }
}
