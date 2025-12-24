package com.example.trackingcaloapp.ui.diary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.data.local.entity.FoodEntry;
import com.example.trackingcaloapp.utils.Constants;
import com.example.trackingcaloapp.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class FoodEntryAdapter extends RecyclerView.Adapter<FoodEntryAdapter.ViewHolder> {

    private List<FoodEntry> entries = new ArrayList<>();

    public void setEntries(List<FoodEntry> entries) {
        this.entries = entries;
        notifyDataSetChanged();
    }

    public FoodEntry getEntryAt(int position) {
        if (position >= 0 && position < entries.size()) {
            return entries.get(position);
        }
        return null;
    }

    public void removeEntry(int position) {
        if (position >= 0 && position < entries.size()) {
            entries.remove(position);
            notifyItemRemoved(position);
        }
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
        private final View viewMealIndicator;
        private final TextView tvFoodName;
        private final TextView tvMealType;
        private final TextView tvQuantity;
        private final TextView tvCalories;
        private final TextView tvTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewMealIndicator = itemView.findViewById(R.id.viewMealIndicator);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvMealType = itemView.findViewById(R.id.tvMealType);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvCalories = itemView.findViewById(R.id.tvCalories);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        void bind(FoodEntry entry) {
            tvFoodName.setText("Thực phẩm #" + entry.getFoodId());
            tvMealType.setText(Constants.getMealTypeName(entry.getMealType()));
            tvQuantity.setText(String.format("%.0fg", entry.getQuantity()));
            tvCalories.setText(String.valueOf((int) entry.getTotalCalories()));
            tvTime.setText(DateUtils.formatTime(entry.getDate()));

            // Set meal indicator color
            int colorRes;
            switch (entry.getMealType()) {
                case Constants.MEAL_BREAKFAST:
                    colorRes = R.color.meal_breakfast;
                    break;
                case Constants.MEAL_LUNCH:
                    colorRes = R.color.meal_lunch;
                    break;
                case Constants.MEAL_DINNER:
                    colorRes = R.color.meal_dinner;
                    break;
                case Constants.MEAL_SNACK:
                    colorRes = R.color.meal_snack;
                    break;
                default:
                    colorRes = R.color.primary;
            }
            viewMealIndicator.setBackgroundColor(
                    itemView.getContext().getColor(colorRes));
            tvMealType.setTextColor(itemView.getContext().getColor(colorRes));
        }
    }
}

