# Phase 3: UI Integration - AddFoodActivity Filter

## Context
- **Parent Plan:** [plan.md](./plan.md)
- **Dependencies:** [Phase 1](./phase-01-mapping-logic.md), [Phase 2](./phase-02-data-layer.md)

## Overview
| Field | Value |
|-------|-------|
| Date | 2025-12-27 |
| Description | Tích hợp filter vào AddFoodActivity và AddFoodFragment khi user chọn chip bữa ăn |
| Priority | High |
| Implementation Status | Pending |
| Review Status | Pending |

## Key Insights

1. **AddFoodActivity.setupMealTypeChips()** - listener chỉ set `selectedMealType`, không reload data
2. **AddWorkoutActivity** - có pattern đúng: khi chọn chip → gọi `loadWorkouts()`
3. **Cần áp dụng pattern tương tự:** khi chọn chip → gọi `loadFoodsByMealType()`

## Requirements

1. Thêm method `loadFoodsByMealType()` để filter theo mealType
2. Cập nhật `setupMealTypeChips()` để gọi load khi chọn chip
3. Cập nhật search để kết hợp với mealType filter
4. Áp dụng cho cả AddFoodActivity và AddFoodFragment

## Architecture

```
AddFoodActivity.java
├── loadFoods()                    [MODIFY - rename to loadFoodsByMealType()]
├── setupMealTypeChips()           [MODIFY - add loadFoodsByMealType() call]
├── searchFoods(query)             [KEEP - but consider combining with mealType]
└── loadFoodsByMealType()          [NEW - use foodRepository.getFoodsByMealType()]
```

## Related Code Files

| File | Changes |
|------|---------|
| `app/src/main/java/.../ui/addfood/AddFoodActivity.java` | Modify setupMealTypeChips(), add loadFoodsByMealType() |
| `app/src/main/java/.../ui/add/AddFoodFragment.java` | Same changes |

## Implementation Steps

### Step 3.1: Modify loadFoods() method in AddFoodActivity
**Location:** `AddFoodActivity.java:120-132`

**Before:**
```java
private void loadFoods() {
    LiveData<List<Food>> foodsLiveData = foodRepository.getAllFoods();
    foodsLiveData.observe(this, foods -> {
        // ...
    });
}
```

**After:**
```java
private void loadFoodsByMealType() {
    LiveData<List<Food>> foodsLiveData = foodRepository.getFoodsByMealType(selectedMealType);
    foodsLiveData.observe(this, foods -> {
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
```

### Step 3.2: Update setupMealTypeChips() to call loadFoodsByMealType()
**Location:** `AddFoodActivity.java:98-118`

**Add at end of listener:**
```java
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
    loadFoodsByMealType();  // <-- ADD THIS
});
```

### Step 3.3: Update onCreate() to call loadFoodsByMealType()
**Location:** `AddFoodActivity.java:58`

**Change:**
```java
// Before
loadFoods();

// After
loadFoodsByMealType();
```

### Step 3.4: Update afterTextChanged in search
**Location:** `AddFoodActivity.java:87-94`

**Change:**
```java
@Override
public void afterTextChanged(Editable s) {
    String query = s.toString().trim();
    if (query.isEmpty()) {
        loadFoodsByMealType();  // <-- CHANGE from loadFoods()
    } else {
        searchFoods(query);
    }
}
```

### Step 3.5: Apply same changes to AddFoodFragment
Repeat steps 3.1-3.4 for `AddFoodFragment.java`

## Todo List

- [ ] Rename loadFoods() → loadFoodsByMealType() in AddFoodActivity
- [ ] Update setupMealTypeChips() to call loadFoodsByMealType() after setting mealType
- [ ] Update search afterTextChanged to call loadFoodsByMealType()
- [ ] Apply same changes to AddFoodFragment

## Success Criteria

1. ✅ Selecting "Bữa sáng" chip shows: phở, bún, bánh mì, xôi, cà phê, trứng
2. ✅ Selecting "Bữa trưa" chip shows: cơm, thịt, hải sản, rau, trứng
3. ✅ Selecting "Bữa tối" chip shows: cơm, phở, bún, thịt, hải sản, rau, trứng
4. ✅ Selecting "Bữa phụ" chip shows: ăn vặt, trái cây, đồ uống
5. ✅ Search still works and clears when switching tabs
6. ✅ Adding food entry uses correct selectedMealType

## Risk Assessment

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| LiveData observer leak | Low | Medium | Activity handles lifecycle correctly |
| Search + mealType conflict | Low | Low | Clear search when changing mealType |

## Security Considerations

- User input (search query) already escaped by Room LIKE query → Safe

## Next Steps

→ Build and test app to verify filter behavior
