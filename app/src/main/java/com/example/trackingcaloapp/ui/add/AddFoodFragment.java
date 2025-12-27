package com.example.trackingcaloapp.ui.add;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.data.local.database.AppDatabase;
import com.example.trackingcaloapp.data.local.entity.Food;
import com.example.trackingcaloapp.data.local.entity.FoodEntry;
import com.example.trackingcaloapp.data.repository.FoodEntryRepository;
import com.example.trackingcaloapp.data.repository.FoodRepository;
import com.example.trackingcaloapp.ui.addfood.FoodAdapter;
import com.example.trackingcaloapp.utils.Constants;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class AddFoodFragment extends Fragment implements FoodAdapter.OnFoodClickListener {

    private TextInputEditText etSearch;
    private ChipGroup chipGroupMealType;
    private RecyclerView rvFoods;
    private TextView tvEmpty;

    private FoodRepository foodRepository;
    private FoodEntryRepository foodEntryRepository;
    private FoodAdapter foodAdapter;

    private int selectedMealType = Constants.MEAL_BREAKFAST;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_food, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppDatabase db = AppDatabase.getInstance(requireContext());
        foodRepository = new FoodRepository(db.foodDao());
        foodEntryRepository = new FoodEntryRepository(db.foodEntryDao());

        initViews(view);
        setupSearch();
        setupMealTypeChips(view);
        loadFoodsByMealType();
    }


    private void initViews(View view) {
        etSearch = view.findViewById(R.id.etSearch);
        chipGroupMealType = view.findViewById(R.id.chipGroupMealType);
        rvFoods = view.findViewById(R.id.rvFoods);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        foodAdapter = new FoodAdapter(this);
        rvFoods.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvFoods.setAdapter(foodAdapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    loadFoodsByMealType();
                } else {
                    searchFoods(query);
                }
            }
        });
    }

    private void setupMealTypeChips(View view) {
        Chip chipBreakfast = view.findViewById(R.id.chipBreakfast);
        Chip chipLunch = view.findViewById(R.id.chipLunch);
        Chip chipDinner = view.findViewById(R.id.chipDinner);
        Chip chipSnack = view.findViewById(R.id.chipSnack);

        chipGroupMealType.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;

            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chipBreakfast) {
                selectedMealType = Constants.MEAL_BREAKFAST;
            } else if (checkedId == R.id.chipLunch) {
                selectedMealType = Constants.MEAL_LUNCH;
            } else if (checkedId == R.id.chipDinner) {
                selectedMealType = Constants.MEAL_DINNER;
            } else if (checkedId == R.id.chipSnack) {
                selectedMealType = Constants.MEAL_SNACK;
            }

            // Clear search and reload foods for selected meal type
            etSearch.setText("");
            loadFoodsByMealType();
        });
    }

    private void loadFoodsByMealType() {
        LiveData<List<Food>> foodsLiveData = foodRepository.getFoodsByMealType(selectedMealType);
        foodsLiveData.observe(getViewLifecycleOwner(), foods -> {
            if (foods != null && !foods.isEmpty()) {
                foodAdapter.setFoods(foods);
                rvFoods.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);
            } else {
                rvFoods.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void searchFoods(String query) {
        LiveData<List<Food>> searchLiveData = foodRepository.searchFoods(query);
        searchLiveData.observe(getViewLifecycleOwner(), foods -> {
            if (foods != null && !foods.isEmpty()) {
                foodAdapter.setFoods(foods);
                rvFoods.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);
            } else {
                rvFoods.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onFoodClick(Food food) {
        showAddFoodDialog(food);
    }

    private void showAddFoodDialog(Food food) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_food_entry, null);

        TextView tvFoodName = dialogView.findViewById(R.id.tvFoodName);
        TextView tvFoodInfo = dialogView.findViewById(R.id.tvFoodInfo);
        TextInputEditText etQuantity = dialogView.findViewById(R.id.etQuantity);
        TextView tvTotalCalories = dialogView.findViewById(R.id.tvTotalCalories);
        TextView tvProtein = dialogView.findViewById(R.id.tvProtein);
        TextView tvCarbs = dialogView.findViewById(R.id.tvCarbs);
        TextView tvFat = dialogView.findViewById(R.id.tvFat);

        // Setup meal type spinner
        AutoCompleteTextView spinnerMealType = dialogView.findViewById(R.id.spinnerMealType);
        String[] mealTypes = getResources().getStringArray(R.array.meal_types);
        ArrayAdapter<String> mealAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                mealTypes
        );
        spinnerMealType.setAdapter(mealAdapter);
        // Pre-select based on current tab (smart default)
        spinnerMealType.setText(mealTypes[selectedMealType], false);

        tvFoodName.setText(food.getName());
        tvFoodInfo.setText(String.format("%.0f cal / 100g", food.getCalories()));

        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    float quantity = s.toString().isEmpty() ? 0 : Float.parseFloat(s.toString());
                    tvTotalCalories.setText(String.valueOf((int) food.calculateCalories(quantity)));
                    tvProtein.setText(String.format("%.1fg", food.calculateProtein(quantity)));
                    tvCarbs.setText(String.format("%.1fg", food.calculateCarbs(quantity)));
                    tvFat.setText(String.format("%.1fg", food.calculateFat(quantity)));
                } catch (NumberFormatException e) {
                    tvTotalCalories.setText("0");
                }
            }
        });

        etQuantity.setText("100");

        new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    try {
                        float quantity = Float.parseFloat(etQuantity.getText().toString());
                        // Get selected meal type from spinner
                        int dialogMealType = getMealTypeFromSelection(spinnerMealType.getText().toString());
                        addFoodEntry(food, quantity, dialogMealType);
                    } catch (NumberFormatException e) {
                        // Ignore
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    /**
     * Convert meal type selection string to int constant
     */
    private int getMealTypeFromSelection(String selection) {
        String[] mealTypes = getResources().getStringArray(R.array.meal_types);
        for (int i = 0; i < mealTypes.length; i++) {
            if (mealTypes[i].equals(selection)) {
                return i;
            }
        }
        return selectedMealType; // fallback to tab selection
    }

    private void addFoodEntry(Food food, float quantity, int mealType) {
        FoodEntry entry = new FoodEntry();
        entry.setFoodId(food.getId());
        entry.setQuantity(quantity);
        entry.setMealType(mealType);
        entry.setDate(System.currentTimeMillis());
        entry.setTotalCalories(food.calculateCalories(quantity));
        entry.setTotalProtein(food.calculateProtein(quantity));
        entry.setTotalCarbs(food.calculateCarbs(quantity));
        entry.setTotalFat(food.calculateFat(quantity));

        foodEntryRepository.insert(entry);
        Toast.makeText(requireContext(), "Đã thêm " + food.getName(), Toast.LENGTH_SHORT).show();
    }
}
