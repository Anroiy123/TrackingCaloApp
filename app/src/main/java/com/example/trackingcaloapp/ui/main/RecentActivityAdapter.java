package com.example.trackingcaloapp.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.data.local.entity.FoodEntry;
import com.example.trackingcaloapp.data.local.entity.WorkoutEntry;
import com.example.trackingcaloapp.utils.Constants;
import com.example.trackingcaloapp.utils.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecentActivityAdapter extends RecyclerView.Adapter<RecentActivityAdapter.ViewHolder> {

    private List<ActivityItem> items = new ArrayList<>();

    public void setData(List<FoodEntry> foodEntries, List<WorkoutEntry> workoutEntries) {
        items.clear();
        
        if (foodEntries != null) {
            for (FoodEntry entry : foodEntries) {
                items.add(new ActivityItem(entry));
            }
        }
        
        if (workoutEntries != null) {
            for (WorkoutEntry entry : workoutEntries) {
                items.add(new ActivityItem(entry));
            }
        }
        
        // Sort by date descending (most recent first)
        Collections.sort(items, (a, b) -> Long.compare(b.getDate(), a.getDate()));
        
        // Limit to 5 most recent
        if (items.size() > 5) {
            items = new ArrayList<>(items.subList(0, 5));
        }
        
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActivityItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final View viewTypeIndicator;
        private final TextView tvActivityName;
        private final TextView tvActivityInfo;
        private final TextView tvCalories;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewTypeIndicator = itemView.findViewById(R.id.viewTypeIndicator);
            tvActivityName = itemView.findViewById(R.id.tvActivityName);
            tvActivityInfo = itemView.findViewById(R.id.tvActivityInfo);
            tvCalories = itemView.findViewById(R.id.tvCalories);
        }

        void bind(ActivityItem item) {
            if (item.isFood()) {
                FoodEntry entry = item.getFoodEntry();
                tvActivityName.setText("Thực phẩm #" + entry.getFoodId());
                String mealName = Constants.getMealTypeName(entry.getMealType());
                String time = DateUtils.formatTime(entry.getDate());
                tvActivityInfo.setText(mealName + " • " + time);
                tvCalories.setText("+" + (int) entry.getTotalCalories() + " cal");
                tvCalories.setTextColor(itemView.getContext().getColor(R.color.calories_consumed));
                viewTypeIndicator.setBackgroundColor(itemView.getContext().getColor(R.color.calories_consumed));
            } else {
                WorkoutEntry entry = item.getWorkoutEntry();
                tvActivityName.setText("Bài tập #" + entry.getWorkoutId());
                String time = DateUtils.formatTime(entry.getDate());
                tvActivityInfo.setText(time);
                tvCalories.setText("-" + (int) entry.getCaloriesBurned() + " cal");
                tvCalories.setTextColor(itemView.getContext().getColor(R.color.calories_burned));
                viewTypeIndicator.setBackgroundColor(itemView.getContext().getColor(R.color.calories_burned));
            }
        }
    }

    // Helper class to combine food and workout entries
    static class ActivityItem {
        private FoodEntry foodEntry;
        private WorkoutEntry workoutEntry;

        ActivityItem(FoodEntry foodEntry) {
            this.foodEntry = foodEntry;
        }

        ActivityItem(WorkoutEntry workoutEntry) {
            this.workoutEntry = workoutEntry;
        }

        boolean isFood() {
            return foodEntry != null;
        }

        FoodEntry getFoodEntry() {
            return foodEntry;
        }

        WorkoutEntry getWorkoutEntry() {
            return workoutEntry;
        }

        long getDate() {
            return isFood() ? foodEntry.getDate() : workoutEntry.getDate();
        }
    }
}

