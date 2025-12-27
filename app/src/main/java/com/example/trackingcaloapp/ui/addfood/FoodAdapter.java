package com.example.trackingcaloapp.ui.addfood;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.data.local.entity.Food;

import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private List<Food> foods = new ArrayList<>();
    private final OnFoodClickListener listener;

    public interface OnFoodClickListener {
        void onFoodClick(Food food);
        void onFoodLongClick(Food food);
    }

    public FoodAdapter(OnFoodClickListener listener) {
        this.listener = listener;
    }

    public void setFoods(List<Food> foods) {
        this.foods = foods;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = foods.get(position);
        holder.bind(food, listener);
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvFoodName;
        private final TextView tvFoodInfo;
        private final TextView tvCalories;
        private final TextView tvCustomBadge;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvFoodInfo = itemView.findViewById(R.id.tvFoodInfo);
            tvCalories = itemView.findViewById(R.id.tvCalories);
            tvCustomBadge = itemView.findViewById(R.id.tvCustomBadge);
        }

        void bind(Food food, OnFoodClickListener listener) {
            tvFoodName.setText(food.getName());
            tvFoodInfo.setText(String.format("%.0f cal / 100g", food.getCalories()));
            tvCalories.setText(String.valueOf((int) food.getCalories()));

            // Show custom badge if food is custom
            tvCustomBadge.setVisibility(food.isCustom() ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(v -> listener.onFoodClick(food));

            // Long-press for custom foods only
            itemView.setOnLongClickListener(v -> {
                if (food.isCustom() && listener != null) {
                    listener.onFoodLongClick(food);
                    return true;
                }
                return false;
            });
        }
    }
}

