package com.example.audiora.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiora.databinding.ItemChartCategoryBinding;
import com.example.audiora.model.ChartCategory;

import java.util.List;

public class ChartCategoryAdapter extends RecyclerView.Adapter<ChartCategoryAdapter.ChartViewHolder> {

    private final List<ChartCategory> chartList;
    private final OnChartClickListener listener;

    public interface OnChartClickListener {
        void onChartClick(ChartCategory chart);
    }

    public ChartCategoryAdapter(List<ChartCategory> chartList, OnChartClickListener listener) {
        this.chartList = chartList;
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
        ChartCategory currentChart = chartList.get(position);
        holder.bind(currentChart, listener);
    }

    @Override
    public int getItemCount() {
        return chartList.size();
    }

    static class ChartViewHolder extends RecyclerView.ViewHolder {
        private final ItemChartCategoryBinding binding;

        public ChartViewHolder(ItemChartCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final ChartCategory chart, final OnChartClickListener listener) {
            binding.chartTitleTextView.setText(chart.getTitle());
            binding.chartCoverImageView.setImageResource(chart.getCoverImageResource());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onChartClick(chart);
                }
            });
        }
    }
}