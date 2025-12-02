# 🗄️ Database Documentation

## Tổng Quan

TrackingCaloApp sử dụng **Room Database** (SQLite wrapper) để lưu trữ dữ liệu local.

## Entity Relationship Diagram

```
┌─────────────────┐         ┌─────────────────────┐
│     FOODS       │         │    FOOD_ENTRIES     │
├─────────────────┤         ├─────────────────────┤
│ id (PK)         │◀────────│ foodId (FK)         │
│ name            │    1:N  │ id (PK)             │
│ calories        │         │ quantity            │
│ protein         │         │ mealType            │
│ carbs           │         │ date                │
│ fat             │         │ totalCalories       │
│ category        │         │ totalProtein        │
│ isCustom        │         │ totalCarbs          │
└─────────────────┘         │ totalFat            │
                            └─────────────────────┘

┌─────────────────┐         ┌─────────────────────┐
│    WORKOUTS     │         │   WORKOUT_ENTRIES   │
├─────────────────┤         ├─────────────────────┤
│ id (PK)         │◀────────│ workoutId (FK)      │
│ name            │    1:N  │ id (PK)             │
│ caloriesPerUnit │         │ quantity            │
│ unit            │         │ duration            │
│ category        │         │ date                │
│ isCustom        │         │ caloriesBurned      │
└─────────────────┘         │ note                │
                            └─────────────────────┘
```

## Tables Schema

### 1. Foods Table

```sql
CREATE TABLE foods (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    calories REAL NOT NULL,
    protein REAL NOT NULL,
    carbs REAL NOT NULL,
    fat REAL NOT NULL,
    category TEXT,
    isCustom INTEGER NOT NULL DEFAULT 0
);
```

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| id | INTEGER | NO | AUTO | Primary key |
| name | TEXT | NO | - | Tên thực phẩm |
| calories | REAL | NO | - | Calo/100g |
| protein | REAL | NO | - | Protein/100g |
| carbs | REAL | NO | - | Carbs/100g |
| fat | REAL | NO | - | Fat/100g |
| category | TEXT | YES | NULL | Danh mục |
| isCustom | INTEGER | NO | 0 | 1 nếu user tạo |

### 2. Food Entries Table

```sql
CREATE TABLE food_entries (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    foodId INTEGER NOT NULL,
    quantity REAL NOT NULL,
    mealType INTEGER NOT NULL,
    date INTEGER NOT NULL,
    totalCalories REAL NOT NULL,
    totalProtein REAL NOT NULL,
    totalCarbs REAL NOT NULL,
    totalFat REAL NOT NULL,
    FOREIGN KEY (foodId) REFERENCES foods(id) ON DELETE CASCADE
);

CREATE INDEX index_food_entries_foodId ON food_entries(foodId);
CREATE INDEX index_food_entries_date ON food_entries(date);
```

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| id | INTEGER | NO | Primary key |
| foodId | INTEGER | NO | FK → foods.id |
| quantity | REAL | NO | Khối lượng (gram) |
| mealType | INTEGER | NO | 0=sáng, 1=trưa, 2=tối, 3=snack |
| date | INTEGER | NO | Timestamp (milliseconds) |
| totalCalories | REAL | NO | = (food.calories × quantity) / 100 |
| totalProtein | REAL | NO | = (food.protein × quantity) / 100 |
| totalCarbs | REAL | NO | = (food.carbs × quantity) / 100 |
| totalFat | REAL | NO | = (food.fat × quantity) / 100 |

### 3. Workouts Table

```sql
CREATE TABLE workouts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    caloriesPerUnit REAL NOT NULL,
    unit TEXT NOT NULL,
    category TEXT,
    isCustom INTEGER NOT NULL DEFAULT 0
);
```

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| id | INTEGER | NO | Primary key |
| name | TEXT | NO | Tên bài tập |
| caloriesPerUnit | REAL | NO | Calo/đơn vị |
| unit | TEXT | NO | "phút", "km", "lần" |
| category | TEXT | YES | "Cardio", "Sức mạnh", "Linh hoạt" |
| isCustom | INTEGER | NO | 1 nếu user tạo |

### 4. Workout Entries Table

```sql
CREATE TABLE workout_entries (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    workoutId INTEGER NOT NULL,
    quantity REAL NOT NULL,
    duration INTEGER NOT NULL,
    date INTEGER NOT NULL,
    caloriesBurned REAL NOT NULL,
    note TEXT,
    FOREIGN KEY (workoutId) REFERENCES workouts(id) ON DELETE CASCADE
);

CREATE INDEX index_workout_entries_workoutId ON workout_entries(workoutId);
CREATE INDEX index_workout_entries_date ON workout_entries(date);
```

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| id | INTEGER | NO | Primary key |
| workoutId | INTEGER | NO | FK → workouts.id |
| quantity | REAL | NO | Số lượng (phút/km/lần) |
| duration | INTEGER | NO | Thời gian tập (phút) |
| date | INTEGER | NO | Timestamp (milliseconds) |
| caloriesBurned | REAL | NO | = workout.caloriesPerUnit × quantity |
| note | TEXT | YES | Ghi chú |

## Pre-populated Data

### Vietnamese Foods (~50 items)

```java
// Cơm & Bún & Phở
new Food("Phở bò", 450, 25, 50, 15, "Cơm & Bún & Phở"),
new Food("Cơm trắng", 130, 2.7f, 28, 0.3f, "Cơm & Bún & Phở"),
new Food("Bún chả", 350, 20, 40, 12, "Cơm & Bún & Phở"),
new Food("Cơm tấm sườn", 650, 30, 70, 25, "Cơm & Bún & Phở"),
// ... more

// Bánh
new Food("Bánh mì thịt", 350, 15, 45, 12, "Bánh"),
new Food("Bánh cuốn", 180, 8, 25, 5, "Bánh"),
// ... more

// Thịt
new Food("Thịt heo luộc", 250, 26, 0, 16, "Thịt"),
new Food("Thịt gà luộc", 165, 31, 0, 3.6f, "Thịt"),
// ... more

// Hải sản
new Food("Cá hồi nướng", 208, 20, 0, 13, "Hải sản"),
new Food("Tôm hấp", 99, 24, 0, 0.3f, "Hải sản"),
// ... more

// Rau củ
new Food("Rau muống xào", 50, 3, 4, 3, "Rau củ"),
new Food("Canh chua", 45, 5, 5, 1, "Rau củ"),
// ... more

// Trái cây
new Food("Chuối", 89, 1.1f, 23, 0.3f, "Trái cây"),
new Food("Xoài", 60, 0.8f, 15, 0.4f, "Trái cây"),
// ... more

// Đồ uống
new Food("Trà đá", 0, 0, 0, 0, "Đồ uống"),
new Food("Cà phê sữa đá", 120, 2, 20, 3, "Đồ uống"),
// ... more
```

### Vietnamese Workouts (~27 items)

```java
// Cardio
new Workout("Chạy bộ", 60, "phút", "Cardio"),
new Workout("Chạy bộ (km)", 60, "km", "Cardio"),
new Workout("Đạp xe", 40, "phút", "Cardio"),
new Workout("Bơi lội", 70, "phút", "Cardio"),
new Workout("Nhảy dây", 80, "phút", "Cardio"),
// ... more

// Sức mạnh
new Workout("Tập gym", 50, "phút", "Sức mạnh"),
new Workout("Chống đẩy", 7, "lần", "Sức mạnh"),
new Workout("Squat", 5, "lần", "Sức mạnh"),
new Workout("Plank", 5, "phút", "Sức mạnh"),
// ... more

// Linh hoạt
new Workout("Yoga", 25, "phút", "Linh hoạt"),
new Workout("Stretching", 20, "phút", "Linh hoạt"),
new Workout("Pilates", 30, "phút", "Linh hoạt"),
// ... more
```

## Common Queries

### Lấy tổng calo trong ngày

```java
@Query("SELECT SUM(totalCalories) FROM food_entries " +
       "WHERE date >= :startOfDay AND date <= :endOfDay")
LiveData<Float> getTotalCaloriesByDate(long startOfDay, long endOfDay);
```

### Lấy tổng calo đốt cháy trong ngày

```java
@Query("SELECT SUM(caloriesBurned) FROM workout_entries " +
       "WHERE date >= :startOfDay AND date <= :endOfDay")
LiveData<Float> getTotalCaloriesBurnedByDate(long startOfDay, long endOfDay);
```

### Tìm kiếm thực phẩm

```java
@Query("SELECT * FROM foods WHERE name LIKE '%' || :query || '%' ORDER BY name")
LiveData<List<Food>> searchFoods(String query);
```

### Lấy entries theo ngày và bữa ăn

```java
@Query("SELECT * FROM food_entries " +
       "WHERE date >= :startOfDay AND date <= :endOfDay AND mealType = :mealType")
LiveData<List<FoodEntry>> getEntriesByDateAndMealType(
    long startOfDay, long endOfDay, String mealType);
```

## Database Initialization

```java
@Database(
    entities = {Food.class, FoodEntry.class, Workout.class, WorkoutEntry.class},
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    
    private static volatile AppDatabase INSTANCE;
    
    public static final ExecutorService databaseWriteExecutor =
        Executors.newFixedThreadPool(4);
    
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "calorie_tracker_db"
                    )
                    .addCallback(sRoomDatabaseCallback)
                    .build();
                }
            }
        }
        return INSTANCE;
    }
    
    private static RoomDatabase.Callback sRoomDatabaseCallback = 
        new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                // Pre-populate data on first run
                databaseWriteExecutor.execute(() -> {
                    FoodDao foodDao = INSTANCE.foodDao();
                    WorkoutDao workoutDao = INSTANCE.workoutDao();
                    
                    // Insert Vietnamese foods
                    foodDao.insertAll(getVietnameseFoods());
                    
                    // Insert workouts
                    workoutDao.insertAll(getWorkouts());
                });
            }
        };
}
```

## Migration Strategy

Khi cần update database schema:

```java
static final Migration MIGRATION_1_2 = new Migration(1, 2) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
        // Add new column
        database.execSQL("ALTER TABLE foods ADD COLUMN fiber REAL DEFAULT 0");
    }
};

// Apply migration
Room.databaseBuilder(context, AppDatabase.class, "calorie_tracker_db")
    .addMigrations(MIGRATION_1_2)
    .build();
```

## Performance Tips

1. **Indices**: Đã tạo index trên `foodId`, `workoutId`, `date` để tăng tốc query
2. **LiveData**: Sử dụng LiveData để tự động update UI khi data thay đổi
3. **Background Thread**: Tất cả write operations chạy trên ExecutorService
4. **Singleton**: Chỉ một instance database trong toàn app
5. **Lazy Loading**: Data chỉ load khi cần thiết

