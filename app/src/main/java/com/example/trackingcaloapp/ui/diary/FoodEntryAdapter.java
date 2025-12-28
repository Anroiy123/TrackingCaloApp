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
import com.example.trackingcaloapp.model.FoodEntryWithFood;
import com.example.trackingcaloapp.model.FoodWithEntry;
import com.example.trackingcaloapp.utils.Constants;
import com.example.trackingcaloapp.utils.DateUtils;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class FoodEntryAdapter extends RecyclerView.Adapter<FoodEntryAdapter.ViewHolder> {

    private List<FoodWithEntry> entries = new ArrayList<>();
    private OnFoodEntryClickListener listener;

    public interface OnFoodEntryClickListener {
        void onFoodEntryClick(FoodWithEntry entry);
        void onFoodEntryLongClick(FoodWithEntry entry);
    }

    public FoodEntryAdapter() {
        this.listener = null;
    }

    public FoodEntryAdapter(OnFoodEntryClickListener listener) {
        this.listener = listener;
    }

    public void setListener(OnFoodEntryClickListener listener) {
        this.listener = listener;
    }

    public void setEntries(List<FoodEntryWithFood> entriesWithFood) {
        this.entries.clear();
        if (entriesWithFood != null) {
            for (FoodEntryWithFood item : entriesWithFood) {
                this.entries.add(item.toFoodWithEntry());
            }
        }
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
        FoodWithEntry entry = entries.get(position);
        holder.bind(entry, listener);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public FoodWithEntry getEntryAt(int position) {
        if (position >= 0 && position < entries.size()) {
            return entries.get(position);
        }
        return null;
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

        void bind(FoodWithEntry entry, OnFoodEntryClickListener listener) {
            // Hiển thị tên thực phẩm thay vì ID
            tvFoodName.setText(entry.getFoodName());
            tvQuantity.setText(String.format("%.0fg", entry.getQuantity()));
            tvCalories.setText(String.valueOf((int) entry.getTotalCalories()));
            tvTime.setText(DateUtils.formatTime(entry.getDate()));

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFoodEntryClick(entry);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onFoodEntryLongClick(entry);
                    return true;
                }
                return false;
            });

            // Set meal type chip text and colors
            int mealType = entry.getMealType();
            String mealName = Constants.getMealTypeName(mealType);
            if (chipMealType != null) {
                chipMealType.setText(mealName);

                int colorRes;
                int containerColorRes;
                switch (mealType) {
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

