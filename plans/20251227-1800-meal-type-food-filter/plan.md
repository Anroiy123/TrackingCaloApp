# Plan: Filter thực phẩm theo bữa ăn

**Created:** 2025-12-27
**Status:** ✅ Completed

## Problem Statement

Hiện tại trong AddFoodActivity, các chip "Bữa sáng", "Bữa trưa", "Bữa tối", "Bữa phụ" chỉ được dùng để chọn `mealType` khi tạo FoodEntry, **KHÔNG** filter danh sách thực phẩm hiển thị. User mong đợi khi chọn tab sẽ thấy thực phẩm phù hợp với bữa đó.

## Root Cause Analysis

1. **Food entity** chỉ có field `category` (loại thực phẩm: com, pho, thit...)
2. **Không có field `mealTypes`** để xác định thực phẩm phù hợp với bữa nào
3. **ChipGroup listener** chỉ cập nhật `selectedMealType`, không gọi filter/reload

## Proposed Solution

**Approach B: Mapping dựa trên category** (Không cần thay đổi DB schema)

Tạo mapping trong Constants.java: `category → List<mealType>` dựa trên thói quen ăn uống người Việt.

| Bữa ăn | Categories phù hợp |
|--------|-------------------|
| Bữa sáng | pho, bun, banh, xoi, do_uong, trung |
| Bữa trưa/tối | com, thit, hai_san, rau, trung |
| Bữa phụ | an_vat, trai_cay, do_uong |

## Implementation Phases

| Phase | Description | Status | Progress |
|-------|-------------|--------|----------|
| [Phase 1](./phase-01-mapping-logic.md) | Tạo mapping category → mealTypes trong Constants | ✅ Done | 100% |
| [Phase 2](./phase-02-data-layer.md) | Thêm query filter trong DAO & Repository | ✅ Done | 100% |
| [Phase 3](./phase-03-ui-integration.md) | Tích hợp filter vào AddFoodActivity & Fragment | ✅ Done | 100% |

## Files Modified

- `app/src/main/java/.../utils/Constants.java` ✅
- `app/src/main/java/.../data/local/dao/FoodDao.java` ✅
- `app/src/main/java/.../data/repository/FoodRepository.java` ✅
- `app/src/main/java/.../ui/addfood/AddFoodActivity.java` ✅
- `app/src/main/java/.../ui/add/AddFoodFragment.java` ✅

## Risks

- **Low:** Một số thực phẩm có thể xuất hiện ở nhiều bữa (phở có thể ăn sáng hoặc tối)
- **Mitigation:** Cho phép một category thuộc nhiều mealTypes

## Estimated Effort

- Phase 1: Simple ✅
- Phase 2: Simple ✅
- Phase 3: Medium ✅

## Dependencies

- None (không cần database migration)
