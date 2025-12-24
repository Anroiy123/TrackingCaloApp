package com.example.trackingcaloapp.utils;

/**
 * Class chứa các hằng số sử dụng trong ứng dụng.
 */
public class Constants {
    
    // ==================== MEAL TYPES ====================
    public static final int MEAL_BREAKFAST = 0;
    public static final int MEAL_LUNCH = 1;
    public static final int MEAL_DINNER = 2;
    public static final int MEAL_SNACK = 3;
    
    // ==================== WORKOUT CATEGORIES ====================
    public static final String WORKOUT_CARDIO = "cardio";
    public static final String WORKOUT_STRENGTH = "strength";
    public static final String WORKOUT_FLEXIBILITY = "flexibility";
    
    // ==================== FOOD CATEGORIES ====================
    public static final String FOOD_COM = "com";
    public static final String FOOD_PHO = "pho";
    public static final String FOOD_BUN = "bun";
    public static final String FOOD_BANH = "banh";
    public static final String FOOD_XOI = "xoi";
    public static final String FOOD_THIT = "thit";
    public static final String FOOD_HAI_SAN = "hai_san";
    public static final String FOOD_RAU = "rau";
    public static final String FOOD_TRUNG = "trung";
    public static final String FOOD_DO_UONG = "do_uong";
    public static final String FOOD_AN_VAT = "an_vat";
    public static final String FOOD_TRAI_CAY = "trai_cay";
    
    // ==================== INTENT EXTRAS ====================
    public static final String EXTRA_FOOD_ID = "extra_food_id";
    public static final String EXTRA_WORKOUT_ID = "extra_workout_id";
    public static final String EXTRA_ENTRY_ID = "extra_entry_id";
    public static final String EXTRA_MEAL_TYPE = "extra_meal_type";
    public static final String EXTRA_DATE = "extra_date";
    
    // ==================== REQUEST CODES ====================
    public static final int REQUEST_ADD_FOOD = 1001;
    public static final int REQUEST_ADD_WORKOUT = 1002;
    public static final int REQUEST_EDIT_FOOD = 1003;
    public static final int REQUEST_EDIT_WORKOUT = 1004;
    
    // ==================== DEFAULT VALUES ====================
    public static final int DEFAULT_CALORIE_GOAL = 2000;
    public static final float DEFAULT_WEIGHT = 65f;
    public static final float DEFAULT_HEIGHT = 170f;
    public static final int DEFAULT_AGE = 25;
    
    // ==================== AUTO MEAL SUGGESTION HOURS ====================
    public static final int BREAKFAST_START_HOUR = 6;
    public static final int BREAKFAST_END_HOUR = 10;
    public static final int LUNCH_START_HOUR = 10;
    public static final int LUNCH_END_HOUR = 14;
    public static final int DINNER_START_HOUR = 18;
    public static final int DINNER_END_HOUR = 22;

    // ==================== MACRO RATIOS (30/40/30) ====================
    public static final float PROTEIN_RATIO = 0.30f;  // 30% calories from protein
    public static final float CARBS_RATIO = 0.40f;    // 40% calories from carbs
    public static final float FAT_RATIO = 0.30f;      // 30% calories from fat

    // Calories per gram
    public static final float PROTEIN_CAL_PER_GRAM = 4f;
    public static final float CARBS_CAL_PER_GRAM = 4f;
    public static final float FAT_CAL_PER_GRAM = 9f;

    // ==================== LIMITS ====================
    public static final int MIN_AGE = 10;
    public static final int MAX_AGE = 120;
    public static final float MIN_WEIGHT = 20f;
    public static final float MAX_WEIGHT = 300f;
    public static final float MIN_HEIGHT = 100f;
    public static final float MAX_HEIGHT = 250f;
    public static final int MIN_CALORIE_GOAL = 1000;
    public static final int MAX_CALORIE_GOAL = 5000;
    
    // ==================== DISPLAY NAMES ====================

    /**
     * Lấy tên hiển thị của loại bữa ăn (int version)
     */
    public static String getMealTypeName(int mealType) {
        switch (mealType) {
            case MEAL_BREAKFAST:
                return "Bữa sáng";
            case MEAL_LUNCH:
                return "Bữa trưa";
            case MEAL_DINNER:
                return "Bữa tối";
            case MEAL_SNACK:
                return "Bữa phụ";
            default:
                return "Khác";
        }
    }

    /**
     * Lấy tên hiển thị của loại bài tập (String version)
     */
    public static String getWorkoutCategoryName(String category) {
        switch (category) {
            case WORKOUT_CARDIO:
                return "Cardio";
            case WORKOUT_STRENGTH:
                return "Sức mạnh";
            case WORKOUT_FLEXIBILITY:
                return "Linh hoạt";
            default:
                return category;
        }
    }
    
    /**
     * Lấy tên hiển thị của loại thực phẩm
     */
    public static String getFoodCategoryDisplayName(String category) {
        switch (category) {
            case FOOD_COM:
                return "Cơm";
            case FOOD_PHO:
                return "Phở";
            case FOOD_BUN:
                return "Bún";
            case FOOD_BANH:
                return "Bánh";
            case FOOD_XOI:
                return "Xôi";
            case FOOD_THIT:
                return "Thịt";
            case FOOD_HAI_SAN:
                return "Hải sản";
            case FOOD_RAU:
                return "Rau củ";
            case FOOD_TRUNG:
                return "Trứng";
            case FOOD_DO_UONG:
                return "Đồ uống";
            case FOOD_AN_VAT:
                return "Ăn vặt";
            case FOOD_TRAI_CAY:
                return "Trái cây";
            default:
                return category;
        }
    }
    
    /**
     * Lấy icon resource name cho loại bữa ăn
     */
    public static String getMealTypeIcon(int mealType) {
        switch (mealType) {
            case MEAL_BREAKFAST:
                return "ic_breakfast";
            case MEAL_LUNCH:
                return "ic_lunch";
            case MEAL_DINNER:
                return "ic_dinner";
            case MEAL_SNACK:
                return "ic_snack";
            default:
                return "ic_food";
        }
    }
}

