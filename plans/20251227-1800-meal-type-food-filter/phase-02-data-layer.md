# Phase 2: Data Layer - Filter Foods by MealType

## Context
- **Parent Plan:** [plan.md](./plan.md)
- **Dependencies:** [Phase 1](./phase-01-mapping-logic.md)

## Overview
| Field | Value |
|-------|-------|
| Date | 2025-12-27 |
| Description | Thêm query filter theo mealType trong FoodDao và FoodRepository |
| Priority | High |
| Implementation Status | Pending |
| Review Status | Pending |

## Key Insights

1. **FoodDao đã có** `getFoodsByCategory(String category)` - filter theo 1 category
2. **Cần thêm:** `getFoodsByCategories(List<String> categories)` - filter theo nhiều categories
3. **Room query:** Sử dụng `IN (:categories)` syntax

## Requirements

1. Thêm method `getFoodsByCategories(List<String>)` vào FoodDao
2. Thêm method `getFoodsByMealType(int mealType)` vào FoodRepository
3. Giữ nguyên backward compatibility với các method hiện có

## Architecture

```
FoodDao.java
└── getFoodsByCategories(List<String> categories) -> LiveData<List<Food>>  [NEW]

FoodRepository.java
└── getFoodsByMealType(int mealType) -> LiveData<List<Food>>  [NEW]
    └── Uses Constants.getCategoriesForMealType() + foodDao.getFoodsByCategories()
```

## Related Code Files

| File | Changes |
|------|---------|
| `app/src/main/java/.../data/local/dao/FoodDao.java` | Add getFoodsByCategories() |
| `app/src/main/java/.../data/repository/FoodRepository.java` | Add getFoodsByMealType() |

## Implementation Steps

### Step 2.1: Add getFoodsByCategories to FoodDao
**Location:** `FoodDao.java:89` (after getFoodsByCategory)

```java
/**
 * Lấy thực phẩm theo nhiều categories
 */
@Query("SELECT * FROM foods WHERE category IN (:categories) ORDER BY name ASC")
LiveData<List<Food>> getFoodsByCategories(List<String> categories);
```

### Step 2.2: Add getFoodsByMealType to FoodRepository
**Location:** `FoodRepository.java:69` (after getFoodsByCategory)

```java
/**
 * Lấy thực phẩm phù hợp với loại bữa ăn
 * @param mealType 0=breakfast, 1=lunch, 2=dinner, 3=snack
 */
public LiveData<List<Food>> getFoodsByMealType(int mealType) {
    List<String> categories = Constants.getCategoriesForMealType(mealType);
    return foodDao.getFoodsByCategories(categories);
}
```

### Step 2.3: Add import to FoodRepository
**Location:** Top of FoodRepository.java

```java
import com.example.trackingcaloapp.utils.Constants;
import java.util.List;
```

## Todo List

- [ ] Add getFoodsByCategories() to FoodDao.java
- [ ] Add getFoodsByMealType() to FoodRepository.java
- [ ] Add import statements

## Success Criteria

1. ✅ `foodRepository.getFoodsByMealType(MEAL_BREAKFAST)` returns LiveData with breakfast foods
2. ✅ Query filters correctly based on categories
3. ✅ Results sorted by name ASC

## Risk Assessment

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| Empty list if no foods match | Low | Low | Database prepopulated with foods in all categories |
| Room query syntax error | Low | High | Test query in isolation first |

## Security Considerations

- Categories list comes from Constants (not user input) → No SQL injection risk

## Next Steps

→ Proceed to [Phase 3: UI Integration](./phase-03-ui-integration.md)
