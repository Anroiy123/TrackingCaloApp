package com.example.trackingcaloapp.ui.addworkout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
        private final View viewCategoryIndicator;
        private final TextView tvWorkoutName;
        private final TextView tvWorkoutInfo;
        private final TextView tvCaloriesPerUnit;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewCategoryIndicator = itemView.findViewById(R.id.viewCategoryIndicator);
            tvWorkoutName = itemView.findViewById(R.id.tvWorkoutName);
            tvWorkoutInfo = itemView.findViewById(R.id.tvWorkoutInfo);
            tvCaloriesPerUnit = itemView.findViewById(R.id.tvCaloriesPerUnit);
        }

        void bind(Workout workout, OnWorkoutClickListener listener) {
            tvWorkoutName.setText(workout.getName());
            
            String categoryName = Constants.getWorkoutCategoryName(workout.getCategory());
            tvWorkoutInfo.setText(String.format("%.0f cal/%s • %s", 
                    workout.getCaloriesPerUnit(), workout.getUnit(), categoryName));
            
            tvCaloriesPerUnit.setText(String.format("%.0f cal/%s", 
                    workout.getCaloriesPerUnit(), workout.getUnit()));

            // Set category indicator color
            int colorRes;
            switch (workout.getCategory()) {
                case Constants.WORKOUT_CARDIO:
                    colorRes = R.color.workout_cardio;
                    break;
                case Constants.WORKOUT_STRENGTH:
                    colorRes = R.color.workout_strength;
                    break;
                case Constants.WORKOUT_FLEXIBILITY:
                    colorRes = R.color.workout_flexibility;
                    break;
                default:
                    colorRes = R.color.primary;
            }
            viewCategoryIndicator.setBackgroundColor(
                    itemView.getContext().getColor(colorRes));

            itemView.setOnClickListener(v -> listener.onWorkoutClick(workout));
        }
    }
}

