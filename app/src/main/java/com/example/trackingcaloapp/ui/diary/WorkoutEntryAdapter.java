package com.example.trackingcaloapp.ui.diary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.data.local.entity.WorkoutEntry;
import com.example.trackingcaloapp.utils.DateUtils;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class WorkoutEntryAdapter extends RecyclerView.Adapter<WorkoutEntryAdapter.ViewHolder> {

    private List<WorkoutEntry> entries = new ArrayList<>();

    public void setEntries(List<WorkoutEntry> entries) {
        this.entries = entries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkoutEntry entry = entries.get(position);
        holder.bind(entry);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout layoutCategoryIcon;
        private final ImageView ivCategoryIcon;
        private final TextView tvWorkoutName;
        private final TextView tvQuantity;
        private final Chip chipCategory;
        private final TextView tvCaloriesBurned;
        private final TextView tvTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutCategoryIcon = itemView.findViewById(R.id.layoutCategoryIcon);
            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvWorkoutName = itemView.findViewById(R.id.tvWorkoutName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            chipCategory = itemView.findViewById(R.id.chipCategory);
            tvCaloriesBurned = itemView.findViewById(R.id.tvCaloriesBurned);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        void bind(WorkoutEntry entry) {
            tvWorkoutName.setText("Bài tập #" + entry.getWorkoutId());
            tvQuantity.setText(String.format("%.0f", entry.getQuantity()));
            tvCaloriesBurned.setText(String.valueOf((int) entry.getCaloriesBurned()));
            tvTime.setText(DateUtils.formatTime(entry.getDate()));

            // Set category chip - default to cardio style since we don't have category from entry
            if (chipCategory != null) {
                chipCategory.setText("Cardio");
                chipCategory.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.cardio));
                chipCategory.setChipBackgroundColorResource(R.color.cardio_container);
            }

            // Update category icon tint
            if (ivCategoryIcon != null) {
                ivCategoryIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.cardio));
            }
        }
    }
}

