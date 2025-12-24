package com.example.trackingcaloapp.ui.addfood;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.data.local.database.AppDatabase;
import com.example.trackingcaloapp.data.local.entity.Food;
import com.example.trackingcaloapp.data.local.entity.FoodEntry;
import com.example.trackingcaloapp.data.repository.FoodEntryRepository;
import com.example.trackingcaloapp.data.repository.FoodRepository;
import com.example.trackingcaloapp.model.FoodWithDetails;
import com.example.trackingcaloapp.utils.Constants;
import com.example.trackingcaloapp.utils.DateUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddFoodActivity extends AppCompatActivity implements FoodAdapter.OnFoodClickListener {

    private MaterialToolbar toolbar;
    private TextInputEditText etSearch;
    private ChipGroup chipGroupMealType;
    private TabLayout tabLayout;
    private RecyclerView rvFoods;
    private TextView tvEmpty;

    private FoodRepository foodRepository;
    private FoodEntryRepository foodEntryRepository;
    private FoodAdapter foodAdapter;

    private int selectedMealType = Constants.MEAL_BREAKFAST;
    private int currentTab = 0; // 0 = All Foods, 1 = Favorites
    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        // Initialize repositories
        AppDatabase db = AppDatabase.getInstance(this);
        foodRepository = new FoodRepository(db.foodDao(), db.favoriteFoodDao());
        foodEntryRepository = new FoodEntryRepository(db.foodEntryDao());

        initViews();
        setupToolbar();
        setupSearch();
        setupMealTypeChips();

        // Auto-suggest meal type based on current time
        autoSelectMealType();

        loadFoods();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etSearch = findViewById(R.id.etSearch);
        chipGroupMealType = findViewById(R.id.chipGroupMealType);
        tabLayout = findViewById(R.id.tabLayout);
        rvFoods = findViewById(R.id.rvFoods);
        tvEmpty = findViewById(R.id.tvEmpty);

        // Setup RecyclerView
        foodAdapter = new FoodAdapter(this);
        foodAdapter.setFavoriteClickListener((food, isCurrentlyFavorite) -> toggleFavorite(food));
        rvFoods.setLayoutManager(new LinearLayoutManager(this));
        rvFoods.setAdapter(foodAdapter);

        // Setup TabLayout
        setupTabLayout();
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                if (currentSearchQuery.isEmpty()) {
                    if (currentTab == 0) {
                        loadFoods();
                    } else {
                        loadFavorites();
                    }
                } else {
                    searchFoods(currentSearchQuery);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                currentSearchQuery = s.toString().trim();
                if (currentSearchQuery.isEmpty()) {
                    if (currentTab == 0) {
                        loadFoods();
                    } else {
                        loadFavorites();
                    }
                } else {
                    searchFoods(currentSearchQuery);
                }
            }
        });
    }

    private void setupMealTypeChips() {
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
        });
    }

    private void loadFoods() {
        LiveData<List<Food>> foodsLiveData = foodRepository.getAllFoods();
        foodsLiveData.observe(this, foods -> {
            if (foods != null && !foods.isEmpty()) {
                foodAdapter.setFoods(foods);
                loadFavoriteIds();
                rvFoods.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);
            } else {
                rvFoods.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadFavorites() {
        LiveData<List<FoodWithDetails>> favoritesLiveData = foodRepository.getAllFavoriteFoodsWithDetails();
        if (favoritesLiveData == null) {
            tvEmpty.setText(R.string.no_favorites);
            rvFoods.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            return;
        }

        favoritesLiveData.observe(this, favorites -> {
            if (favorites != null && !favorites.isEmpty()) {
                // Convert FoodWithDetails to Food list
                List<Food> foods = new ArrayList<>();
                Set<Integer> favIds = new HashSet<>();
                for (FoodWithDetails fwd : favorites) {
                    foods.add(fwd.getFood());
                    favIds.add(fwd.getFoodId());
                }
                foodAdapter.setFoods(foods);
                foodAdapter.setFavoriteIds(favIds);
                rvFoods.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);
            } else {
                tvEmpty.setText(R.string.no_favorites);
                rvFoods.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadFavoriteIds() {
        // Load all favorite IDs to show stars
        LiveData<List<FoodWithDetails>> favoritesLiveData = foodRepository.getAllFavoriteFoodsWithDetails();
        if (favoritesLiveData != null) {
            favoritesLiveData.observe(this, favorites -> {
                if (favorites != null) {
                    Set<Integer> favIds = new HashSet<>();
                    for (FoodWithDetails fwd : favorites) {
                        favIds.add(fwd.getFoodId());
                    }
                    foodAdapter.setFavoriteIds(favIds);
                }
            });
        }
    }

    private void searchFoods(String query) {
        LiveData<List<Food>> searchLiveData = foodRepository.searchFoods(query);
        searchLiveData.observe(this, foods -> {
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
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_food_entry, null);
        
        TextView tvFoodName = dialogView.findViewById(R.id.tvFoodName);
        TextView tvFoodInfo = dialogView.findViewById(R.id.tvFoodInfo);
        TextInputEditText etQuantity = dialogView.findViewById(R.id.etQuantity);
        TextView tvTotalCalories = dialogView.findViewById(R.id.tvTotalCalories);
        TextView tvProtein = dialogView.findViewById(R.id.tvProtein);
        TextView tvCarbs = dialogView.findViewById(R.id.tvCarbs);
        TextView tvFat = dialogView.findViewById(R.id.tvFat);

        tvFoodName.setText(food.getName());
        tvFoodInfo.setText(String.format("%.0f cal / 100g", food.getCalories()));

        // Update nutrition when quantity changes
        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    float quantity = s.toString().isEmpty() ? 0 : Float.parseFloat(s.toString());
                    float calories = food.calculateCalories(quantity);
                    float protein = food.calculateProtein(quantity);
                    float carbs = food.calculateCarbs(quantity);
                    float fat = food.calculateFat(quantity);

                    tvTotalCalories.setText(String.valueOf((int) calories));
                    tvProtein.setText(String.format("%.1fg", protein));
                    tvCarbs.setText(String.format("%.1fg", carbs));
                    tvFat.setText(String.format("%.1fg", fat));
                } catch (NumberFormatException e) {
                    tvTotalCalories.setText("0");
                }
            }
        });

        // Trigger initial calculation
        etQuantity.setText("100");

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    try {
                        float quantity = Float.parseFloat(etQuantity.getText().toString());
                        addFoodEntry(food, quantity);
                    } catch (NumberFormatException e) {
                        // Ignore
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void addFoodEntry(Food food, float quantity) {
        FoodEntry entry = new FoodEntry();
        entry.setFoodId(food.getId());
        entry.setQuantity(quantity);
        entry.setMealType(selectedMealType);
        entry.setDate(System.currentTimeMillis());
        entry.setTotalCalories(food.calculateCalories(quantity));
        entry.setTotalProtein(food.calculateProtein(quantity));
        entry.setTotalCarbs(food.calculateCarbs(quantity));
        entry.setTotalFat(food.calculateFat(quantity));

        foodEntryRepository.insert(entry);

        // Increment favorite use count if this food is a favorite
        foodRepository.incrementFavoriteUseCount(food.getId());

        Toast.makeText(this, R.string.entry_added, Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * Auto-select meal type chip based on current time
     */
    private void autoSelectMealType() {
        selectedMealType = DateUtils.getMealTypeByTime();

        int chipIdToCheck;
        switch (selectedMealType) {
            case Constants.MEAL_BREAKFAST:
                chipIdToCheck = R.id.chipBreakfast;
                break;
            case Constants.MEAL_LUNCH:
                chipIdToCheck = R.id.chipLunch;
                break;
            case Constants.MEAL_DINNER:
                chipIdToCheck = R.id.chipDinner;
                break;
            case Constants.MEAL_SNACK:
            default:
                chipIdToCheck = R.id.chipSnack;
                break;
        }

        chipGroupMealType.check(chipIdToCheck);
    }

    /**
     * Toggle favorite status of a food
     */
    private void toggleFavorite(Food food) {
        foodRepository.toggleFavorite(food.getId(), food.getServingSize(), isNowFavorite -> {
            runOnUiThread(() -> {
                if (isNowFavorite) {
                    Toast.makeText(this, R.string.added_to_favorites, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.removed_from_favorites, Toast.LENGTH_SHORT).show();
                }
                // Update the specific item
                foodAdapter.updateFavorite(food.getId(), isNowFavorite);

                // If on favorites tab and removed, reload
                if (currentTab == 1 && !isNowFavorite) {
                    loadFavorites();
                }
            });
        });
    }
}

