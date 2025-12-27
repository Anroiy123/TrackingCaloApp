package com.example.trackingcaloapp.ui.add;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddFoodFragment extends Fragment implements FoodAdapter.OnFoodClickListener {

    private static final String TAG = "AddFoodFragment";
    private static final long DEBOUNCE_MS = 500; // Debounce search 500ms

    private TextInputEditText etSearch;
    private ChipGroup chipGroupMealType;
    private RecyclerView rvFoods;
    private TextView tvEmpty;
    private ProgressBar progressBar;
    private FloatingActionButton fabAddCustomFood;

    private FoodRepository foodRepository;
    private FoodEntryRepository foodEntryRepository;
    private FoodAdapter foodAdapter;

    private int selectedMealType = Constants.MEAL_BREAKFAST;

    // Hybrid search
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private List<Food> currentFoods = new ArrayList<>();
    private LiveData<List<Food>> currentLocalLiveData;

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
        // Use constructor with Context to enable API support
        foodRepository = new FoodRepository(db.foodDao(), requireContext());
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
        progressBar = view.findViewById(R.id.progressBar);
        fabAddCustomFood = view.findViewById(R.id.fabAddCustomFood);

        foodAdapter = new FoodAdapter(this);
        rvFoods.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvFoods.setAdapter(foodAdapter);

        // Setup FAB click listener
        fabAddCustomFood.setOnClickListener(v -> showCreateFoodDialog());
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

                // Cancel previous search
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                if (query.isEmpty()) {
                    loadFoodsByMealType();
                    return;
                }

                // Debounce: Wait 500ms before searching
                searchRunnable = () -> searchHybrid(query);
                searchHandler.postDelayed(searchRunnable, DEBOUNCE_MS);
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
        // Remove previous observer
        if (currentLocalLiveData != null) {
            currentLocalLiveData.removeObservers(getViewLifecycleOwner());
        }

        currentLocalLiveData = foodRepository.getFoodsByMealType(selectedMealType);
        currentLocalLiveData.observe(getViewLifecycleOwner(), foods -> {
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

    /**
     * Hybrid search: Local Room DB + FatSecret API
     * - Local results appear immediately via LiveData
     * - API results appended after network response
     */
    private void searchHybrid(String query) {
        Log.d(TAG, "Hybrid search: " + query);
        progressBar.setVisibility(View.VISIBLE);
        currentFoods.clear();

        // Remove previous observer to prevent memory leak
        if (currentLocalLiveData != null) {
            currentLocalLiveData.removeObservers(getViewLifecycleOwner());
        }

        // Step 1: Observe local results (immediate)
        currentLocalLiveData = foodRepository.searchFoodsLocal(query);
        currentLocalLiveData.observe(getViewLifecycleOwner(), foods -> {
            currentFoods.clear();
            if (foods != null) {
                currentFoods.addAll(foods);
            }
            foodAdapter.setFoods(new ArrayList<>(currentFoods));
            updateEmptyState();
        });

        // Step 2: Fetch API results (async)
        foodRepository.searchHybrid(query, new FoodRepository.HybridSearchCallback() {
            @Override
            public void onApiResults(List<Food> apiFoods) {
                if (!isAdded()) return; // Fragment not attached

                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);

                    // Append API results (avoid duplicates by apiId)
                    for (Food apiFood : apiFoods) {
                        boolean isDuplicate = false;
                        for (Food existing : currentFoods) {
                            // Check by apiId for API foods, or by name for local foods
                            if (existing.getApiId() != null && apiFood.getApiId() != null &&
                                existing.getApiId().equals(apiFood.getApiId())) {
                                isDuplicate = true;
                                break;
                            }
                        }
                        if (!isDuplicate) {
                            currentFoods.add(apiFood);
                        }
                    }

                    foodAdapter.setFoods(new ArrayList<>(currentFoods));
                    updateEmptyState();
                    Log.d(TAG, "Total foods after API: " + currentFoods.size());
                });
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "API search error: " + error);
                    // Local results still shown, just log the error
                });
            }
        });
    }

    private void updateEmptyState() {
        if (currentFoods.isEmpty()) {
            rvFoods.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvFoods.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Remove pending callbacks to prevent memory leak
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }

        // Remove LiveData observer
        if (currentLocalLiveData != null) {
            currentLocalLiveData.removeObservers(getViewLifecycleOwner());
        }
    }

    @Override
    public void onFoodClick(Food food) {
        showAddFoodDialog(food);
    }

    @Override
    public void onFoodLongClick(Food food) {
        new AlertDialog.Builder(requireContext(), R.style.Theme_App_Dialog)
                .setTitle(food.getName())
                .setItems(new String[]{"Chỉnh sửa", "Xóa"}, (dialog, which) -> {
                    if (which == 0) {
                        showEditFoodDialog(food);
                    } else {
                        showDeleteFoodConfirmation(food);
                    }
                })
                .show();
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

        new AlertDialog.Builder(requireContext(), R.style.Theme_App_Dialog)
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

    /**
     * Show dialog to create custom food
     */
    private void showCreateFoodDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_create_custom_food, null);

        TextInputEditText etName = dialogView.findViewById(R.id.etFoodName);
        TextInputEditText etCalories = dialogView.findViewById(R.id.etCalories);
        TextInputEditText etProtein = dialogView.findViewById(R.id.etProtein);
        TextInputEditText etCarbs = dialogView.findViewById(R.id.etCarbs);
        TextInputEditText etFat = dialogView.findViewById(R.id.etFat);
        AutoCompleteTextView spinnerCategory = dialogView.findViewById(R.id.spinnerCategory);

        // Setup category spinner
        String[] categoryNames = {"Cơm", "Phở", "Bún", "Thịt", "Hải sản", "Rau", "Đồ uống", "Ăn vặt", "Trái cây"};
        String[] categoryValues = {"com", "pho", "bun", "thit", "hai_san", "rau", "do_uong", "an_vat", "trai_cay"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, categoryNames);
        spinnerCategory.setAdapter(adapter);
        spinnerCategory.setText(categoryNames[7], false); // Default: "Ăn vặt"

        new AlertDialog.Builder(requireContext(), R.style.Theme_App_Dialog)
                .setTitle(R.string.create_custom_food)
                .setView(dialogView)
                .setPositiveButton("Tạo", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(requireContext(), R.string.error_name_required, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    float calories = parseFloat(etCalories, 0);
                    float protein = parseFloat(etProtein, 0);
                    float carbs = parseFloat(etCarbs, 0);
                    float fat = parseFloat(etFat, 0);

                    // Validate calories > 0
                    if (calories <= 0) {
                        Toast.makeText(requireContext(), R.string.error_calories_required, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Get selected category
                    int categoryIndex = java.util.Arrays.asList(categoryNames)
                            .indexOf(spinnerCategory.getText().toString());
                    String category = categoryIndex >= 0 ? categoryValues[categoryIndex] : "an_vat";

                    // Create custom food with isCustom = true
                    Food food = new Food(name, calories, protein, carbs, fat, category, true);
                    foodRepository.insert(food);
                    Toast.makeText(requireContext(), R.string.food_created, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    /**
     * Parse float from TextInputEditText with default value
     */
    private float parseFloat(TextInputEditText et, float defaultValue) {
        try {
            String text = et.getText().toString().trim();
            return text.isEmpty() ? defaultValue : Float.parseFloat(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Show dialog to edit custom food
     */
    private void showEditFoodDialog(Food food) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_create_custom_food, null);

        TextInputEditText etName = dialogView.findViewById(R.id.etFoodName);
        TextInputEditText etCalories = dialogView.findViewById(R.id.etCalories);
        TextInputEditText etProtein = dialogView.findViewById(R.id.etProtein);
        TextInputEditText etCarbs = dialogView.findViewById(R.id.etCarbs);
        TextInputEditText etFat = dialogView.findViewById(R.id.etFat);
        AutoCompleteTextView spinnerCategory = dialogView.findViewById(R.id.spinnerCategory);

        // Pre-fill values
        etName.setText(food.getName());
        etCalories.setText(String.valueOf(food.getCalories()));
        etProtein.setText(String.valueOf(food.getProtein()));
        etCarbs.setText(String.valueOf(food.getCarbs()));
        etFat.setText(String.valueOf(food.getFat()));

        // Setup category spinner
        String[] categoryNames = {"Cơm", "Phở", "Bún", "Thịt", "Hải sản", "Rau", "Đồ uống", "Ăn vặt", "Trái cây"};
        String[] categoryValues = {"com", "pho", "bun", "thit", "hai_san", "rau", "do_uong", "an_vat", "trai_cay"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, categoryNames);
        spinnerCategory.setAdapter(adapter);

        // Set current category
        int currentCategoryIndex = java.util.Arrays.asList(categoryValues).indexOf(food.getCategory());
        if (currentCategoryIndex >= 0) {
            spinnerCategory.setText(categoryNames[currentCategoryIndex], false);
        }

        new AlertDialog.Builder(requireContext(), R.style.Theme_App_Dialog)
                .setTitle(R.string.edit_food)
                .setView(dialogView)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(requireContext(), R.string.error_name_required, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    float calories = parseFloat(etCalories, 0);
                    float protein = parseFloat(etProtein, 0);
                    float carbs = parseFloat(etCarbs, 0);
                    float fat = parseFloat(etFat, 0);

                    if (calories <= 0) {
                        Toast.makeText(requireContext(), R.string.error_calories_required, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int categoryIndex = java.util.Arrays.asList(categoryNames)
                            .indexOf(spinnerCategory.getText().toString());
                    String category = categoryIndex >= 0 ? categoryValues[categoryIndex] : "an_vat";

                    // Update food object
                    food.setName(name);
                    food.setCalories(calories);
                    food.setProtein(protein);
                    food.setCarbs(carbs);
                    food.setFat(fat);
                    food.setCategory(category);

                    foodRepository.update(food);
                    Toast.makeText(requireContext(), "Đã cập nhật", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    /**
     * Show confirmation dialog before deleting food
     */
    private void showDeleteFoodConfirmation(Food food) {
        new AlertDialog.Builder(requireContext(), R.style.Theme_App_Dialog)
                .setTitle("Xóa món ăn?")
                .setMessage("Bạn có chắc muốn xóa \"" + food.getName() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    foodRepository.delete(food);
                    Toast.makeText(requireContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
