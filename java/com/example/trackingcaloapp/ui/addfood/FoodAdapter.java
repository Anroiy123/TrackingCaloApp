package com.example.trackingcaloapp.ui.addfood;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.data.local.entity.Food;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private List<Food> foods = new ArrayList<>();
    private Set<Integer> favoriteIds = new HashSet<>();
    private final OnFoodClickListener listener;
    private OnFavoriteClickListener favoriteClickListener;

    public interface OnFoodClickListener {
        void onFoodClick(Food food);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Food food, boolean isCurrentlyFavorite);
    }

    public FoodAdapter(OnFoodClickListener listener) {
        this.listener = listener;
    }

    public void setFavoriteClickListener(OnFavoriteClickListener listener) {
        this.favoriteClickListener = listener;
    }

    public void setFoods(List<Food> foods) {
        this.foods = foods;
        notifyDataSetChanged();
    }

    public void setFavoriteIds(Set<Integer> favoriteIds) {
        this.favoriteIds = favoriteIds;
        notifyDataSetChanged();
    }

    public void updateFavorite(int foodId, boolean isFavorite) {
        if (isFavorite) {
            favoriteIds.add(foodId);
        } else {
            favoriteIds.remove(foodId);
        }
        // Find position and notify
        for (int i = 0; i < foods.size(); i++) {
            if (foods.get(i).getId() == foodId) {
                notifyItemChanged(i);
                break;
            }
        }
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
        boolean isFavorite = favoriteIds.contains(food.getId());
        holder.bind(food, isFavorite, listener, favoriteClickListener);
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivFavorite;
        private final TextView tvFoodName;
        private final TextView tvFoodInfo;
        private final TextView tvCalories;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvFoodInfo = itemView.findViewById(R.id.tvFoodInfo);
            tvCalories = itemView.findViewById(R.id.tvCalories);
        }

        void bind(Food food, boolean isFavorite, OnFoodClickListener listener, OnFavoriteClickListener favoriteListener) {
            tvFoodName.setText(food.getName());

            // Show serving info if available
            String servingUnit = food.getServingUnit();
            float servingSize = food.getServingSize();
            if (servingUnit != null && !servingUnit.equals("g") && servingSize > 0) {
                tvFoodInfo.setText(String.format("%.0f cal / %.0f%s", food.getCalories(), servingSize, servingUnit));
            } else {
                tvFoodInfo.setText(String.format("%.0f cal / 100g", food.getCalories()));
            }

            tvCalories.setText(String.valueOf((int) food.getCalories()));

            // Update star icon
            if (ivFavorite != null) {
                ivFavorite.setImageResource(isFavorite ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
                ivFavorite.setOnClickListener(v -> {
                    if (favoriteListener != null) {
                        favoriteListener.onFavoriteClick(food, isFavorite);
                    }
                });
            }

            itemView.setOnClickListener(v -> listener.onFoodClick(food));
        }
    }
}

