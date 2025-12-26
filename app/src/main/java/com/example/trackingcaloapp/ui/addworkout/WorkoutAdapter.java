package com.example.trackingcaloapp.ui.addworkout;

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
import com.example.trackingcaloapp.data.local.entity.Workout;
import com.example.trackingcaloapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {

    private List<Workout> workouts = new ArrayList<>();
    private final OnWorkoutClickListener listener;

    public interface OnWorkoutClickListener {
        void onWorkoutClick(Workout workout);
    }

    public WorkoutAdapter(OnWorkoutClickListener listener) {
        this.listener = listener;
    }

    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Workout workout = workouts.get(position);
        holder.bind(workout, listener);
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout layoutCategoryIcon;
        private final ImageView ivCategoryIcon;
        private final TextView tvWorkoutName;
        private final TextView tvWorkoutInfo;
        private final TextView tvCaloriesPerUnit;
        private final TextView tvUnit;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutCategoryIcon = itemView.findViewById(R.id.layoutCategoryIcon);
            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvWorkoutName = itemView.findViewById(R.id.tvWorkoutName);
            tvWorkoutInfo = itemView.findViewById(R.id.tvWorkoutInfo);
            tvCaloriesPerUnit = itemView.findViewById(R.id.tvCaloriesPerUnit);
            tvUnit = itemView.findViewById(R.id.tvUnit);
        }

        void bind(Workout workout, OnWorkoutClickListener listener) {
            tvWorkoutName.setText(workout.getName());

            String categoryName = Constants.getWorkoutCategoryName(workout.getCategory());
            tvWorkoutInfo.setText(String.format("%.0f cal/%s • %s",
                    workout.getCaloriesPerUnit(), workout.getUnit(), categoryName));

            tvCaloriesPerUnit.setText(String.format("%.0f", workout.getCaloriesPerUnit()));
            tvUnit.setText(String.format("cal/%s", workout.getUnit()));

            // Set category indicator color based on workout category
            int colorRes;
            int bgRes;
            switch (workout.getCategory()) {
                case Constants.WORKOUT_CARDIO:
                    colorRes = R.color.cardio;
                    bgRes = R.drawable.bg_stat_burned;
                    break;
                case Constants.WORKOUT_STRENGTH:
                    colorRes = R.color.strength;
                    bgRes = R.drawable.bg_stat_burned;
                    break;
                case Constants.WORKOUT_FLEXIBILITY:
                    colorRes = R.color.flexibility;
                    bgRes = R.drawable.bg_stat_burned;
                    break;
                default:
                    colorRes = R.color.calories_burned;
                    bgRes = R.drawable.bg_stat_burned;
            }

            if (ivCategoryIcon != null) {
                ivCategoryIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), colorRes));
            }

            itemView.setOnClickListener(v -> listener.onWorkoutClick(workout));
        }
    }
}

