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
    
    // API Integration fields
    private Long apiId;            // FatSecret food_id (null = local food)
    private String apiSource;      // "fatsecret" hoặc null
    private long cachedAt;         // Timestamp khi cache từ API
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
- `api` - Thực phẩm từ API (FatSecret/USDA)

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

### WeightLog Entity (Mới)

```java
@Entity(tableName = "weight_logs")
public class WeightLog {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private float weight;          // Cân nặng (kg)
    private long timestamp;        // Thời điểm ghi nhận (ms)
    private String note;           // Ghi chú (optional)
}
```

---

### User Entity (Mới)

```java
@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String username;       // Tên đăng nhập (unique)
    private String passwordHash;   // Mật khẩu đã hash (SHA-256)
    private long createdAt;        // Thời điểm tạo tài khoản (ms)
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

### WeightLogDao (Mới)

```java
@Dao
public interface WeightLogDao {
    @Insert
    long insert(WeightLog log);
    
    @Query("SELECT * FROM weight_logs ORDER BY timestamp DESC")
    LiveData<List<WeightLog>> getAllLogs();
    
    @Query("SELECT * FROM weight_logs WHERE timestamp >= :startTime ORDER BY timestamp ASC")
    LiveData<List<WeightLog>> getLogsSince(long startTime);
    
    @Query("SELECT * FROM weight_logs ORDER BY timestamp DESC LIMIT 1")
    LiveData<WeightLog> getLatestLog();
    
    @Query("SELECT * FROM weight_logs ORDER BY timestamp DESC LIMIT 1")
    WeightLog getLatestLogSync();
    
    @Delete
    void delete(WeightLog log);
    
    @Query("SELECT COUNT(*) FROM weight_logs")
    int getLogCount();
}
```

### UserDao (Mới)

```java
@Dao
public interface UserDao {
    @Insert
    long insert(User user);
    
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);
    
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    User getUserById(int userId);
    
    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    int countByUsername(String username);
    
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username AND passwordHash = :passwordHash)")
    boolean validateCredentials(String username, String passwordHash);
}
```

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
    public LiveData<List<Food>> getFoodsByMealType(int mealType);
    public LiveData<List<Food>> getCustomFoods();
    
    // Hybrid Search (Local + API)
    public interface SearchCallback {
        void onApiResults(List<Food> apiFoods);
        void onError(String error);
    }
    public void searchHybrid(String query, SearchCallback callback);
    
    // Mutations (run on background thread)
    public void insert(Food food);
    public void update(Food food);
    public void delete(Food food);
    public void deleteById(int foodId);
}
```

### WeightLogRepository (Mới)

```java
public class WeightLogRepository {
    public WeightLogRepository(Application application);
    
    public void insert(WeightLog log);
    public LiveData<List<WeightLog>> getAllLogs();
    public LiveData<List<WeightLog>> getLogsSince(long startTime);
    public LiveData<WeightLog> getLatestLog();
}
```

### UserRepository (Mới)

```java
public class UserRepository {
    public UserRepository(Application application);
    
    // Đăng ký - return userId nếu thành công, -1 nếu username đã tồn tại
    public Future<Long> register(String username, String password);
    
    // Đăng nhập - return User nếu thành công, null nếu thất bại
    public Future<User> login(String username, String password);
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
     * Tính mục tiêu calo với target weight (Mới)
     */
    public static int calculateDailyCalorieGoalWithTarget(float tdee, float currentWeight,
                                                           float targetWeight, int daysToGoal,
                                                           boolean isMale);
    
    /**
     * Tính số ngày để đạt mục tiêu (Mới)
     */
    public static int calculateDaysToGoal(float currentWeight, float targetWeight, float weeklyRate);
    
    /**
     * Tính BMI (Body Mass Index)
     */
    public static float calculateBMI(float weight, float heightCm);
    
    /**
     * Lấy phân loại BMI (theo chuẩn châu Á)
     */
    public static String getBMICategory(float bmi);
    
    /**
     * Lấy hệ số hoạt động
     */
    public static float getActivityMultiplier(String activityLevel);
}
```

### ChartHelper (Mới)

```java
public class ChartHelper {
    /**
     * Lấy date range cho period (7, 30, 90 ngày)
     */
    public static long[] getDateRangeForPeriod(int days);
    
    /**
     * Setup và style cho LineChart
     */
    public static void setupLineChart(LineChart chart, Context context);
    public static void updateLineChartData(LineChart chart, List<DailyCalorieSum> data, Context context);
    
    /**
     * Setup và style cho BarChart
     */
    public static void setupBarChart(BarChart chart, Context context);
    public static void updateBarChartData(BarChart chart, List<MealTypeCalories> data, Context context);
    
    /**
     * Setup và style cho PieChart
     */
    public static void setupPieChart(PieChart chart, Context context);
    public static void updatePieChartData(PieChart chart, MacroSum data, Context context);
}
```

### PasswordUtils (Mới)

```java
public class PasswordUtils {
    /**
     * Hash password với SHA-256
     */
    public static String hashPassword(String password);
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
    public static String formatDateFull(long timestamp);  // Thứ, dd/MM/yyyy
    
    /**
     * Format thời gian
     */
    public static String formatTime(long timestamp);
    public static String formatDateTime(long timestamp);
    
    /**
     * Kiểm tra ngày
     */
    public static boolean isToday(long timestamp);
    public static boolean isYesterday(long timestamp);
    
    /**
     * Lấy display date thân thiện
     */
    public static String getDisplayDate(long timestamp);  // "Hôm nay", "Hôm qua", "Thứ X, dd/MM"
    
    /**
     * Lấy timestamp của n ngày trước
     */
    public static long getDaysAgo(int days);
    
    /**
     * Lấy timestamp đầu tuần/tháng
     */
    public static long getStartOfWeek();
    public static long getStartOfMonth();
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
    
    // Target Weight (Mới)
    public void setTargetWeight(float weight);
    public float getTargetWeight();
    
    public void setTargetDate(long timestamp);
    public long getTargetDate();
    
    public void setWeeklyRate(float rate);
    public float getWeeklyRate();
    
    public void clearWeightGoalTarget();
    public boolean hasTargetWeight();
    
    // Login State (Mới)
    public void setLoggedIn(boolean loggedIn);
    public boolean isLoggedIn();
    
    public void setCurrentUserId(int userId);
    public int getCurrentUserId();
    
    public void setLoginUsername(String username);
    public String getLoginUsername();
    
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

### Activities

| Activity | Chức năng |
|----------|-----------|
| `LoginActivity` | Màn hình đăng nhập với Remember Me |
| `RegisterActivity` | Màn hình đăng ký tài khoản mới |
| `MainActivity` | Container chính + Bottom Navigation |
| `OnboardingActivity` | Thiết lập ban đầu (+ target weight) |

### Main Fragments

| Fragment | Chức năng |
|----------|-----------|
| `HomeFragment` | Dashboard - tổng quan calo, progress bars, charts, hoạt động gần đây |
| `DiaryFragment` | Nhật ký - ViewPager2 với tabs FoodEntries/WorkoutEntries + charts |
| `AddFragment` | Container - ViewPager2 với tabs AddFood/AddWorkout |
| `ProfileFragment` | Hồ sơ - thông tin cá nhân, BMI, weight chart, logout |

### Child Fragments

| Fragment | Parent | Chức năng |
|----------|--------|-----------|
| `AddFoodFragment` | AddFragment | Tìm kiếm hybrid (local + API) và thêm thực phẩm |
| `AddWorkoutFragment` | AddFragment | Tìm kiếm và thêm bài tập vào nhật ký |
| `FoodEntriesFragment` | DiaryFragment | Hiển thị danh sách food entries theo ngày |
| `WorkoutEntriesFragment` | DiaryFragment | Hiển thị danh sách workout entries theo ngày |
| `QuickWeightLogDialogFragment` | ProfileFragment | Dialog ghi nhận cân nặng nhanh |

### ViewModels

| ViewModel | Chức năng |
|-----------|-----------|
| `ProfileViewModel` | Quản lý state cho ProfileFragment, weight logging, validation |

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

### Custom Views

| View | Chức năng |
|------|-----------|
| `OverflowProgressBar` | Progress bar hỗ trợ overflow (vượt quá 100%) |

