package com.example.audiora.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.audiora.R;
import com.example.audiora.databinding.ItemChartCategoryBinding;
import com.example.audiora.model.ChartCategory;
import java.util.List;

public class ChartCategoryAdapter extends RecyclerView.Adapter<ChartCategoryAdapter.ChartViewHolder> {

    private final List<ChartCategory> chartCategories;
    private final OnChartClickListener listener;

    public interface OnChartClickListener {
        void onChartClick(ChartCategory chart);
    }

    public ChartCategoryAdapter(List<ChartCategory> chartCategories, OnChartClickListener listener) {
        this.chartCategories = chartCategories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChartCategoryBinding binding = ItemChartCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ChartViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChartViewHolder holder, int position) {
        ChartCategory chart = chartCategories.get(position);
        holder.bind(chart, listener);
    }

    @Override
    public int getItemCount() {
        return chartCategories.size();
    }

    static class ChartViewHolder extends RecyclerView.ViewHolder {
        private final ItemChartCategoryBinding binding;

        public ChartViewHolder(ItemChartCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final ChartCategory chart, final OnChartClickListener listener) {
            binding.chartTitleTextView.setText(chart.getTitle());

            // Load the cover image using Glide
            if (chart.getCoverImageUrl() != null) {
                Glide.with(binding.getRoot().getContext())
                    .load(chart.getCoverImageUrl())
                    .placeholder(chart.getDefaultCoverResId())
                    .error(chart.getDefaultCoverResId())
                    .into(binding.chartCoverImageView);
            } else {
                binding.chartCoverImageView.setImageResource(chart.getDefaultCoverResId());
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onChartClick(chart);
                }
            });
        }
    }
}