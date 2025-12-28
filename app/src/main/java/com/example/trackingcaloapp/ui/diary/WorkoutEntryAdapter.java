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
import com.example.trackingcaloapp.model.WorkoutEntryWithWorkout;
import com.example.trackingcaloapp.model.WorkoutWithEntry;
import com.example.trackingcaloapp.utils.DateUtils;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class WorkoutEntryAdapter extends RecyclerView.Adapter<WorkoutEntryAdapter.ViewHolder> {

    private List<WorkoutWithEntry> entries = new ArrayList<>();
    private OnWorkoutEntryClickListener listener;

    public interface OnWorkoutEntryClickListener {
        void onWorkoutEntryClick(WorkoutWithEntry entry);
        void onWorkoutEntryLongClick(WorkoutWithEntry entry);
    }

    public WorkoutEntryAdapter() {
        this.listener = null;
    }

    public WorkoutEntryAdapter(OnWorkoutEntryClickListener listener) {
        this.listener = listener;
    }

    public void setListener(OnWorkoutEntryClickListener listener) {
        this.listener = listener;
    }

    public void setEntries(List<WorkoutEntryWithWorkout> entriesWithWorkout) {
        this.entries.clear();
        if (entriesWithWorkout != null) {
            for (WorkoutEntryWithWorkout item : entriesWithWorkout) {
                this.entries.add(item.toWorkoutWithEntry());
            }
        }
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
        WorkoutWithEntry entry = entries.get(position);
        holder.bind(entry, listener);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public WorkoutWithEntry getEntryAt(int position) {
        if (position >= 0 && position < entries.size()) {
            return entries.get(position);
        }
        return null;
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

        void bind(WorkoutWithEntry entry, OnWorkoutEntryClickListener listener) {
            // Hiển thị tên bài tập thay vì ID
            tvWorkoutName.setText(entry.getWorkoutName());
            tvQuantity.setText(entry.getQuantityDisplay());
            tvCaloriesBurned.setText(String.valueOf((int) entry.getCaloriesBurned()));
            tvTime.setText(DateUtils.formatTime(entry.getDate()));

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onWorkoutEntryClick(entry);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onWorkoutEntryLongClick(entry);
                    return true;
                }
                return false;
            });

            // Set category chip với thông tin thực từ workout
            String category = entry.getCategory();
            if (chipCategory != null) {
                chipCategory.setText(entry.getCategoryDisplayName());

                int colorRes;
                int containerColorRes;
                switch (category) {
                    case "cardio":
                        colorRes = R.color.cardio;
                        containerColorRes = R.color.cardio_container;
                        break;
                    case "strength":
                        colorRes = R.color.strength;
                        containerColorRes = R.color.strength_container;
                        break;
                    case "flexibility":
                        colorRes = R.color.flexibility;
                        containerColorRes = R.color.flexibility_container;
                        break;
                    default:
                        colorRes = R.color.cardio;
                        containerColorRes = R.color.cardio_container;
                }

                chipCategory.setTextColor(ContextCompat.getColor(itemView.getContext(), colorRes));
                chipCategory.setChipBackgroundColorResource(containerColorRes);

                // Update category icon tint
                if (ivCategoryIcon != null) {
                    ivCategoryIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), colorRes));
                }
            }
        }
    }
}

