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
import com.example.trackingcaloapp.data.local.entity.FoodEntry;
import com.example.trackingcaloapp.utils.Constants;
import com.example.trackingcaloapp.utils.DateUtils;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class FoodEntryAdapter extends RecyclerView.Adapter<FoodEntryAdapter.ViewHolder> {

    private List<FoodEntry> entries = new ArrayList<>();

    public void setEntries(List<FoodEntry> entries) {
        this.entries = entries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodEntry entry = entries.get(position);
        holder.bind(entry);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout layoutMealIcon;
        private final ImageView ivMealIcon;
        private final TextView tvFoodName;
        private final Chip chipMealType;
        private final TextView tvQuantity;
        private final TextView tvCalories;
        private final TextView tvTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutMealIcon = itemView.findViewById(R.id.layoutMealIcon);
            ivMealIcon = itemView.findViewById(R.id.ivMealIcon);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            chipMealType = itemView.findViewById(R.id.chipMealType);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvCalories = itemView.findViewById(R.id.tvCalories);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        void bind(FoodEntry entry) {
            tvFoodName.setText("Thực phẩm #" + entry.getFoodId());
            tvQuantity.setText(String.format("%.0fg", entry.getQuantity()));
            tvCalories.setText(String.valueOf((int) entry.getTotalCalories()));
            tvTime.setText(DateUtils.formatTime(entry.getDate()));

            // Set meal type chip text and colors
            String mealName = Constants.getMealTypeName(entry.getMealType());
            if (chipMealType != null) {
                chipMealType.setText(mealName);

                int colorRes;
                int containerColorRes;
                switch (entry.getMealType()) {
                    case Constants.MEAL_BREAKFAST:
                        colorRes = R.color.breakfast;
                        containerColorRes = R.color.breakfast_container;
                        break;
                    case Constants.MEAL_LUNCH:
                        colorRes = R.color.lunch;
                        containerColorRes = R.color.lunch_container;
                        break;
                    case Constants.MEAL_DINNER:
                        colorRes = R.color.dinner;
                        containerColorRes = R.color.dinner_container;
                        break;
                    case Constants.MEAL_SNACK:
                        colorRes = R.color.snack;
                        containerColorRes = R.color.snack_container;
                        break;
                    default:
                        colorRes = R.color.primary;
                        containerColorRes = R.color.primary_container;
                }

                chipMealType.setTextColor(ContextCompat.getColor(itemView.getContext(), colorRes));
                chipMealType.setChipBackgroundColorResource(containerColorRes);

                // Update meal icon tint
                if (ivMealIcon != null) {
                    ivMealIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), colorRes));
                }
            }
        }
    }
}

