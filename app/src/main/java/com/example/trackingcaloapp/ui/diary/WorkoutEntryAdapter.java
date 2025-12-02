package com.example.trackingcaloapp.ui.diary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.data.local.entity.WorkoutEntry;
import com.example.trackingcaloapp.utils.DateUtils;

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
        private final View viewCategoryIndicator;
        private final TextView tvWorkoutName;
        private final TextView tvQuantity;
        private final TextView tvCategory;
        private final TextView tvCaloriesBurned;
        private final TextView tvTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewCategoryIndicator = itemView.findViewById(R.id.viewCategoryIndicator);
            tvWorkoutName = itemView.findViewById(R.id.tvWorkoutName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvCaloriesBurned = itemView.findViewById(R.id.tvCaloriesBurned);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        void bind(WorkoutEntry entry) {
            tvWorkoutName.setText("Bài tập #" + entry.getWorkoutId());
            tvQuantity.setText(String.format("%.0f", entry.getQuantity()));
            tvCategory.setText(""); // Would need to join with Workout table
            tvCaloriesBurned.setText(String.valueOf((int) entry.getCaloriesBurned()));
            tvTime.setText(DateUtils.formatTime(entry.getDate()));

            viewCategoryIndicator.setBackgroundColor(
                    itemView.getContext().getColor(R.color.calories_burned));
        }
    }
}

