# Phase 1: Tạo Mapping Category → MealTypes

## Context
- **Parent Plan:** [plan.md](./plan.md)
- **Dependencies:** None

## Overview
| Field | Value |
|-------|-------|
| Date | 2025-12-27 |
| Description | Tạo mapping giữa food category và meal types trong Constants.java |
| Priority | High |
| Implementation Status | Pending |
| Review Status | Pending |

## Key Insights

1. **Thói quen ăn uống người Việt:**
   - Bữa sáng: phở, bún, bánh mì, xôi, cà phê sữa, trứng
   - Bữa trưa/tối: cơm + thịt/cá/hải sản + rau
   - Bữa phụ: ăn vặt, trái cây, đồ uống

2. **Food categories hiện có:**
   - com, pho, bun, banh, xoi, thit, hai_san, rau, trung, do_uong, an_vat, trai_cay

3. **Một số thực phẩm thuộc nhiều bữa:**
   - `trung` (trứng): sáng + trưa/tối
   - `do_uong`: sáng + phụ
   - `pho`, `bun`: sáng + tối

## Requirements

1. Tạo method `getCategoriesForMealType(int mealType)` trả về `List<String>` categories phù hợp
2. Mapping logic:
   - BREAKFAST: pho, bun, banh, xoi, do_uong, trung
   - LUNCH: com, thit, hai_san, rau, trung
   - DINNER: com, pho, bun, thit, hai_san, rau, trung (giống lunch + thêm phở/bún)
   - SNACK: an_vat, trai_cay, do_uong

## Architecture

```
Constants.java
├── MEAL_TYPE constants (existing)
├── FOOD_CATEGORY constants (existing)
└── getCategoriesForMealType(int mealType) -> List<String>  [NEW]
```

## Related Code Files

| File | Changes |
|------|---------|
| `app/src/main/java/.../utils/Constants.java` | Add `getCategoriesForMealType()` method |

## Implementation Steps

### Step 1.1: Add mapping method to Constants.java
**Location:** `Constants.java:130` (after `getFoodCategoryDisplayName`)

```java
/**
 * Lấy danh sách categories phù hợp cho loại bữa ăn
 * @param mealType 0=breakfast, 1=lunch, 2=dinner, 3=snack
 * @return Danh sách category strings
 */
public static List<String> getCategoriesForMealType(int mealType) {
    List<String> categories = new ArrayList<>();
    switch (mealType) {
        case MEAL_BREAKFAST:
            categories.add(FOOD_PHO);
            categories.add(FOOD_BUN);
            categories.add(FOOD_BANH);
            categories.add(FOOD_XOI);
            categories.add(FOOD_DO_UONG);
            categories.add(FOOD_TRUNG);
            break;
        case MEAL_LUNCH:
            categories.add(FOOD_COM);
            categories.add(FOOD_THIT);
            categories.add(FOOD_HAI_SAN);
            categories.add(FOOD_RAU);
            categories.add(FOOD_TRUNG);
            break;
        case MEAL_DINNER:
            categories.add(FOOD_COM);
            categories.add(FOOD_PHO);
            categories.add(FOOD_BUN);
            categories.add(FOOD_THIT);
            categories.add(FOOD_HAI_SAN);
            categories.add(FOOD_RAU);
            categories.add(FOOD_TRUNG);
            break;
        case MEAL_SNACK:
            categories.add(FOOD_AN_VAT);
            categories.add(FOOD_TRAI_CAY);
            categories.add(FOOD_DO_UONG);
            break;
    }
    return categories;
}
```

### Step 1.2: Add import for ArrayList
**Location:** Top of Constants.java

```java
import java.util.ArrayList;
import java.util.List;
```

## Todo List

- [ ] Add getCategoriesForMealType() method to Constants.java
- [ ] Add import statements

## Success Criteria

1. ✅ `Constants.getCategoriesForMealType(MEAL_BREAKFAST)` returns [pho, bun, banh, xoi, do_uong, trung]
2. ✅ `Constants.getCategoriesForMealType(MEAL_LUNCH)` returns [com, thit, hai_san, rau, trung]
3. ✅ `Constants.getCategoriesForMealType(MEAL_DINNER)` returns [com, pho, bun, thit, hai_san, rau, trung]
4. ✅ `Constants.getCategoriesForMealType(MEAL_SNACK)` returns [an_vat, trai_cay, do_uong]

## Risk Assessment

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| Mapping không phản ánh đúng thói quen user | Low | Medium | Có thể adjust sau khi nhận feedback |

## Security Considerations

- None (static mapping, no user input processing)

## Next Steps

→ Proceed to [Phase 2: Data Layer](./phase-02-data-layer.md)
