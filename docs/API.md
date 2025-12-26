# 📚 API Documentation

## Entities

### Food Entity

```java
@Entity(tableName = "foods")
public class Food {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String name;           // Tên thực phẩm
    private float calories;        // Calo/100g
    private float protein;         // Protein/100g
    private float carbs;           // Carbs/100g
    private float fat;             // Fat/100g
    private String category;       // Danh mục
    private boolean isCustom;      // User tạo?
}
```

**Categories:**
- `Cơm & Bún & Phở` - Các món chính
- `Bánh` - Bánh mì, bánh ngọt
- `Thịt` - Các loại thịt
- `Hải sản` - Cá, tôm, cua
- `Rau củ` - Rau xanh, củ quả
- `Trái cây` - Các loại trái cây
- `Đồ uống` - Nước, trà, cà phê
- `Snack` - Đồ ăn vặt

---

### FoodEntry Entity

```java
@Entity(tableName = "food_entries")
public class FoodEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private int foodId;            // FK → Foods
    private float quantity;        // Khối lượng (gram)
    private int mealType;          // 0-3 (bữa ăn)
    private long date;             // Timestamp (ms)
    private float totalCalories;   // Calo đã tính
    private float totalProtein;    // Protein đã tính
    private float totalCarbs;      // Carbs đã tính
    private float totalFat;        // Fat đã tính
}
```

**Meal Types:**
| Value | Constant | Meaning |
|-------|----------|---------|
| 0 | `MEAL_BREAKFAST` | Bữa sáng |
| 1 | `MEAL_LUNCH` | Bữa trưa |
| 2 | `MEAL_DINNER` | Bữa tối |
| 3 | `MEAL_SNACK` | Ăn vặt |

---

### Workout Entity

```java
@Entity(tableName = "workouts")
public class Workout {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String name;           // Tên bài tập
    private float caloriesPerUnit; // Calo/đơn vị
    private String unit;           // Đơn vị (phút/km/lần)
    private String category;       // Loại bài tập
    private boolean isCustom;      // User tạo?
}
```

**Categories:**
- `Cardio` - Chạy bộ, đạp xe, bơi
- `Sức mạnh` - Gym, tập tạ
- `Linh hoạt` - Yoga, stretching

**Units:**
- `phút` - Thời gian tập
- `km` - Khoảng cách
- `lần` - Số lần thực hiện

---

### WorkoutEntry Entity

```java
@Entity(tableName = "workout_entries")
public class WorkoutEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private int workoutId;         // FK → Workouts
    private float quantity;        // Số lượng
    private int duration;          // Thời gian (phút)
    private long date;             // Timestamp (ms)
    private float caloriesBurned;  // Calo đốt cháy
    private String note;           // Ghi chú
}
```

---

## DAOs

### FoodDao

```java
@Dao
public interface FoodDao {
    // Lấy tất cả foods
    @Query("SELECT * FROM foods ORDER BY name")
    LiveData<List<Food>> getAllFoods();
    
    // Lấy food theo ID
    @Query("SELECT * FROM foods WHERE id = :foodId")
    Food getFoodById(int foodId);
    
    // Tìm kiếm theo tên
    @Query("SELECT * FROM foods WHERE name LIKE '%' || :query || '%'")
    LiveData<List<Food>> searchFoods(String query);
    
    // Lấy theo category
    @Query("SELECT * FROM foods WHERE category = :category")
    LiveData<List<Food>> getFoodsByCategory(String category);
    
    // Lấy danh sách categories
    @Query("SELECT DISTINCT category FROM foods ORDER BY category")
    LiveData<List<String>> getAllCategories();
    
    // Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Food food);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Food> foods);
    
    // Update
    @Update
    void update(Food food);
    
    // Delete
    @Delete
    void delete(Food food);
    
    @Query("DELETE FROM foods WHERE id = :foodId")
    void deleteById(int foodId);
}
```

### FoodEntryDao

```java
@Dao
public interface FoodEntryDao {
    // Lấy entries theo ngày
    @Query("SELECT * FROM food_entries WHERE date >= :startOfDay AND date <= :endOfDay")
    LiveData<List<FoodEntry>> getEntriesByDate(long startOfDay, long endOfDay);
    
    // Tổng calo theo ngày
    @Query("SELECT SUM(totalCalories) FROM food_entries WHERE date >= :startOfDay AND date <= :endOfDay")
    LiveData<Float> getTotalCaloriesByDate(long startOfDay, long endOfDay);
    
    // Tổng protein theo ngày
    @Query("SELECT SUM(totalProtein) FROM food_entries WHERE date >= :startOfDay AND date <= :endOfDay")
    LiveData<Float> getTotalProteinByDate(long startOfDay, long endOfDay);
    
    // Tổng carbs theo ngày
    @Query("SELECT SUM(totalCarbs) FROM food_entries WHERE date >= :startOfDay AND date <= :endOfDay")
    LiveData<Float> getTotalCarbsByDate(long startOfDay, long endOfDay);
    
    // Tổng fat theo ngày
    @Query("SELECT SUM(totalFat) FROM food_entries WHERE date >= :startOfDay AND date <= :endOfDay")
    LiveData<Float> getTotalFatByDate(long startOfDay, long endOfDay);
    
    // Insert/Update/Delete
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FoodEntry foodEntry);
    
    @Update
    void update(FoodEntry foodEntry);
    
    @Delete
    void delete(FoodEntry foodEntry);
}
```

### WorkoutDao & WorkoutEntryDao

Tương tự FoodDao và FoodEntryDao.

---

## Repositories

### FoodRepository

```java
public class FoodRepository {
    // Constructor
    public FoodRepository(Application application);
    public FoodRepository(FoodDao foodDao);  // For testing
    
    // Getters (return LiveData)
    public LiveData<List<Food>> getAllFoods();
    public Food getFoodById(int foodId);
    public LiveData<List<Food>> searchFoods(String query);
    public LiveData<List<Food>> getFoodsByCategory(String category);
    public LiveData<List<String>> getAllCategories();
    
    // Mutations (run on background thread)
    public void insert(Food food);
    public void update(Food food);
    public void delete(Food food);
    public void deleteById(int foodId);
}
```

---

## Utilities

### CalorieCalculator

```java
public class CalorieCalculator {
    /**
     * Tính BMR (Basal Metabolic Rate)
     * Công thức Mifflin-St Jeor
     */
    public static float calculateBMR(float weight, float height, int age, boolean isMale);
    
    /**
     * Tính TDEE (Total Daily Energy Expenditure)
     */
    public static float calculateTDEE(float bmr, float activityMultiplier);
    public static float calculateTDEE(float weight, float height, int age, 
                                       boolean isMale, String activityLevel);
    
    /**
     * Tính mục tiêu calo hàng ngày
     */
    public static int calculateDailyCalorieGoal(float tdee, String weightGoal);
    public static int calculateDailyCalorieGoal(float weight, float height, int age,
                                                 boolean isMale, String activityLevel, 
                                                 String weightGoal);
    public static int calculateDailyCalorieGoal(float weight, float height, int age,
                                                 boolean isMale, float activityMultiplier, 
                                                 int weightGoal);
    
    /**
     * Tính BMI (Body Mass Index)
     */
    public static float calculateBMI(float weight, float heightCm);
    
    /**
     * Lấy phân loại BMI
     */
    public static String getBMICategory(float bmi);
    
    /**
     * Lấy hệ số hoạt động
     */
    public static float getActivityMultiplier(String activityLevel);
}
```

### DateUtils

```java
public class DateUtils {
    /**
     * Lấy timestamp đầu ngày (00:00:00)
     */
    public static long getStartOfDay(long timestamp);
    
    /**
     * Lấy timestamp cuối ngày (23:59:59)
     */
    public static long getEndOfDay(long timestamp);
    
    /**
     * Format date thành string
     */
    public static String formatDate(long timestamp);
    public static String formatDate(long timestamp, String pattern);
    
    /**
     * Format thời gian
     */
    public static String formatTime(long timestamp);
    
    /**
     * Kiểm tra cùng ngày
     */
    public static boolean isSameDay(long timestamp1, long timestamp2);
    
    /**
     * Lấy ngày hôm qua/mai
     */
    public static long getPreviousDay(long timestamp);
    public static long getNextDay(long timestamp);
}
```

### Constants

```java
public class Constants {
    // Meal types
    public static final int MEAL_BREAKFAST = 0;
    public static final int MEAL_LUNCH = 1;
    public static final int MEAL_DINNER = 2;
    public static final int MEAL_SNACK = 3;
    
    // Food categories
    public static final String CATEGORY_RICE = "Cơm & Bún & Phở";
    public static final String CATEGORY_BREAD = "Bánh";
    public static final String CATEGORY_MEAT = "Thịt";
    public static final String CATEGORY_SEAFOOD = "Hải sản";
    public static final String CATEGORY_VEGETABLE = "Rau củ";
    public static final String CATEGORY_FRUIT = "Trái cây";
    public static final String CATEGORY_DRINK = "Đồ uống";
    public static final String CATEGORY_SNACK = "Snack";
    
    // Workout categories
    public static final String WORKOUT_CARDIO = "Cardio";
    public static final String WORKOUT_STRENGTH = "Sức mạnh";
    public static final String WORKOUT_FLEXIBILITY = "Linh hoạt";
    
    // Helper methods
    public static String getMealTypeName(int mealType);
    public static String getMealTypeIcon(int mealType);
}
```

---

## UserPreferences

```java
public class UserPreferences {
    // Constructor
    public UserPreferences(Context context);
    
    // User Info
    public void setUserName(String name);
    public String getUserName();
    
    public void setUserAge(int age);
    public int getUserAge();
    // Alias: setAge(), getAge()
    
    public void setUserGender(String gender);
    public String getUserGender();
    // Alias: setGender(), getGender()
    
    public void setUserHeight(float height);
    public float getUserHeight();
    // Alias: setHeight(), getHeight()
    
    public void setUserWeight(float weight);
    public float getUserWeight();
    // Alias: setWeight(), getWeight()
    
    // Goals
    public void setActivityLevel(String level);
    public void setActivityLevel(int position);  // 1-5
    public String getActivityLevel();
    public int getActivityLevelPosition();
    
    public void setWeightGoal(String goal);
    public void setWeightGoal(int position);  // 0-2
    public String getWeightGoal();
    public int getWeightGoalPosition();
    
    public void setDailyCalorieGoal(int calories);
    public int getDailyCalorieGoal();
    
    // App Settings
    public void setOnboardingComplete(boolean complete);
    public boolean isOnboardingComplete();
    
    public void setThemeMode(String mode);
    public String getThemeMode();
    
    // Helper Methods
    public String getActivityLevelDisplayName();
    public String getWeightGoalDisplayName();
    public float getActivityMultiplier();
    public boolean hasUserInfo();
    public void clearAll();
}
```

**Activity Levels:**
| Constant | Value | Multiplier |
|----------|-------|------------|
| `ACTIVITY_SEDENTARY` | "sedentary" | 1.2 |
| `ACTIVITY_LIGHT` | "light" | 1.375 |
| `ACTIVITY_MODERATE` | "moderate" | 1.55 |
| `ACTIVITY_ACTIVE` | "active" | 1.725 |
| `ACTIVITY_VERY_ACTIVE` | "very_active" | 1.9 |

**Weight Goals:**
| Constant | Value | Adjustment |
|----------|-------|------------|
| `GOAL_LOSE` | "lose" | -500 cal |
| `GOAL_MAINTAIN` | "maintain" | 0 |
| `GOAL_GAIN` | "gain" | +500 cal |

---

## UI Components

### MainActivity (Single Activity Container)

```java
public class MainActivity extends AppCompatActivity {
    // Fragment container và Bottom Navigation
    private FrameLayout fragmentContainer;
    private BottomNavigationView bottomNav;
    
    // Load fragment theo tab được chọn
    private void loadFragment(Fragment fragment);
    
    // Bottom Navigation listener
    // - R.id.nav_home → HomeFragment
    // - R.id.nav_diary → DiaryFragment
    // - R.id.nav_add → AddFragment
    // - R.id.nav_profile → ProfileFragment
}
```

### Main Fragments

| Fragment | Chức năng |
|----------|-----------|
| `HomeFragment` | Dashboard - hiển thị tổng quan calo, progress bars, hoạt động gần đây |
| `DiaryFragment` | Nhật ký - ViewPager2 với tabs FoodEntries/WorkoutEntries |
| `AddFragment` | Container - ViewPager2 với tabs AddFood/AddWorkout |
| `ProfileFragment` | Hồ sơ - quản lý thông tin cá nhân, mục tiêu, cài đặt |

### Child Fragments

| Fragment | Parent | Chức năng |
|----------|--------|-----------|
| `AddFoodFragment` | AddFragment | Tìm kiếm và thêm thực phẩm vào nhật ký |
| `AddWorkoutFragment` | AddFragment | Tìm kiếm và thêm bài tập vào nhật ký |
| `FoodEntriesFragment` | DiaryFragment | Hiển thị danh sách food entries theo ngày |
| `WorkoutEntriesFragment` | DiaryFragment | Hiển thị danh sách workout entries theo ngày |

### ViewPager Adapters

```java
// DiaryFragment ViewPager2
public class DiaryFragmentPagerAdapter extends FragmentStateAdapter {
    // Tab 0: FoodEntriesFragment
    // Tab 1: WorkoutEntriesFragment
}

// AddFragment ViewPager2
public class AddPagerAdapter extends FragmentStateAdapter {
    // Tab 0: AddFoodFragment
    // Tab 1: AddWorkoutFragment
}
```

