# 🏗️ Kiến Trúc Ứng Dụng TrackingCaloApp

## Tổng Quan

TrackingCaloApp được xây dựng theo mô hình **MVVM (Model-View-ViewModel)** kết hợp với **Repository Pattern**, đảm bảo separation of concerns và dễ dàng maintain, test.

## 📊 Sơ Đồ Kiến Trúc

```
┌─────────────────────────────────────────────────────────────────┐
│                         UI LAYER                                 │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐              │
│  │  Activity   │  │  Fragment   │  │   Adapter   │              │
│  │             │  │             │  │             │              │
│  │ MainActivity│  │FoodEntries  │  │ FoodAdapter │              │
│  │ AddFood     │  │WorkoutEntries│ │WorkoutAdapter│             │
│  │ AddWorkout  │  │             │  │             │              │
│  │ Diary       │  │             │  │             │              │
│  │ Profile     │  │             │  │             │              │
│  │ Onboarding  │  │             │  │             │              │
│  └──────┬──────┘  └──────┬──────┘  └─────────────┘              │
│         │                │                                       │
│         └────────┬───────┘                                       │
│                  │ observes LiveData                             │
│                  ▼                                               │
├─────────────────────────────────────────────────────────────────┤
│                      DATA LAYER                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                    REPOSITORY                            │    │
│  │  ┌──────────────┐  ┌──────────────┐                     │    │
│  │  │FoodRepository│  │WorkoutRepo   │                     │    │
│  │  │FoodEntryRepo │  │WorkoutEntryRepo│                   │    │
│  │  └──────┬───────┘  └──────┬───────┘                     │    │
│  │         │                 │                              │    │
│  │         └────────┬────────┘                              │    │
│  │                  │                                       │    │
│  └──────────────────┼───────────────────────────────────────┘    │
│                     │                                            │
│         ┌───────────┴───────────┐                               │
│         ▼                       ▼                               │
│  ┌─────────────────┐    ┌─────────────────┐                     │
│  │  Room Database  │    │SharedPreferences│                     │
│  │                 │    │                 │                     │
│  │  ┌───────────┐  │    │ UserPreferences │                     │
│  │  │    DAO    │  │    │                 │                     │
│  │  │ FoodDao   │  │    │ - userName      │                     │
│  │  │ EntryDao  │  │    │ - calorieGoal   │                     │
│  │  │ WorkoutDao│  │    │ - activityLevel │                     │
│  │  └───────────┘  │    │ - weightGoal    │                     │
│  │                 │    │                 │                     │
│  │  ┌───────────┐  │    └─────────────────┘                     │
│  │  │  Entity   │  │                                            │
│  │  │ Food      │  │                                            │
│  │  │ FoodEntry │  │                                            │
│  │  │ Workout   │  │                                            │
│  │  │WorkoutEntry│ │                                            │
│  │  └───────────┘  │                                            │
│  └─────────────────┘                                            │
└─────────────────────────────────────────────────────────────────┘
```

## 🔄 Data Flow

```
┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐
│   User   │────▶│    UI    │────▶│Repository│────▶│ Database │
│  Action  │     │ (Activity)│     │          │     │  (Room)  │
└──────────┘     └──────────┘     └──────────┘     └──────────┘
                       ▲                                 │
                       │         LiveData                │
                       └─────────────────────────────────┘
```

### Flow Chi Tiết

1. **User Action**: Người dùng tương tác với UI (click button, nhập text)
2. **UI Layer**: Activity/Fragment xử lý event và gọi Repository
3. **Repository**: Thực hiện business logic, gọi DAO
4. **Database**: Room thực hiện query, trả về LiveData
5. **LiveData**: Tự động notify UI khi data thay đổi
6. **UI Update**: UI tự động cập nhật với data mới

## 📦 Các Layer Chi Tiết

### 1. UI Layer (`ui/`)

Chịu trách nhiệm hiển thị data và xử lý user interactions.

#### Activities

| Activity | Chức năng |
|----------|-----------|
| `MainActivity` | Màn hình chính, hiển thị tổng quan calo |
| `OnboardingActivity` | Thiết lập ban đầu cho user mới |
| `AddFoodActivity` | Thêm thực phẩm vào nhật ký |
| `AddWorkoutActivity` | Thêm bài tập vào nhật ký |
| `DiaryActivity` | Xem nhật ký theo ngày |
| `ProfileActivity` | Quản lý thông tin cá nhân |

#### Fragments

| Fragment | Chức năng |
|----------|-----------|
| `FoodEntriesFragment` | Hiển thị danh sách food entries |
| `WorkoutEntriesFragment` | Hiển thị danh sách workout entries |

#### Adapters

| Adapter | Chức năng |
|---------|-----------|
| `FoodAdapter` | RecyclerView adapter cho danh sách foods |
| `WorkoutAdapter` | RecyclerView adapter cho danh sách workouts |
| `FoodEntryAdapter` | Adapter cho food entries |
| `WorkoutEntryAdapter` | Adapter cho workout entries |
| `RecentActivityAdapter` | Adapter cho hoạt động gần đây |
| `DiaryPagerAdapter` | ViewPager2 adapter cho diary tabs |

### 2. Data Layer (`data/`)

#### 2.1 Local Database (`local/`)

**AppDatabase.java** - Room Database Singleton

```java
@Database(
    entities = {Food.class, FoodEntry.class, Workout.class, WorkoutEntry.class},
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    // Singleton pattern
    // Pre-populated data callback
    // ExecutorService for background operations
}
```

**Entities**

| Entity | Mô tả |
|--------|-------|
| `Food` | Thông tin thực phẩm (tên, calo, protein, carbs, fat) |
| `FoodEntry` | Nhật ký ăn uống (foodId, quantity, mealType, date) |
| `Workout` | Thông tin bài tập (tên, calo/unit, unit, category) |
| `WorkoutEntry` | Nhật ký tập luyện (workoutId, quantity, duration, date) |

**DAOs (Data Access Objects)**

```java
@Dao
public interface FoodDao {
    @Query("SELECT * FROM foods ORDER BY name")
    LiveData<List<Food>> getAllFoods();
    
    @Query("SELECT * FROM foods WHERE name LIKE '%' || :query || '%'")
    LiveData<List<Food>> searchFoods(String query);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Food food);
    
    // ... more queries
}
```

#### 2.2 Preferences (`preferences/`)

**UserPreferences.java** - SharedPreferences Wrapper

```java
public class UserPreferences {
    // User info
    - userName, userAge, userGender
    - userHeight, userWeight
    
    // Goals
    - dailyCalorieGoal
    - activityLevel (sedentary/light/moderate/active/very_active)
    - weightGoal (lose/maintain/gain)
    
    // App settings
    - isOnboardingComplete
    - themeMode
}
```

#### 2.3 Repository (`repository/`)

Repository pattern cung cấp clean API cho UI layer.

```java
public class FoodRepository {
    private final FoodDao foodDao;
    
    public FoodRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        foodDao = db.foodDao();
    }
    
    // Expose LiveData to UI
    public LiveData<List<Food>> getAllFoods() {
        return foodDao.getAllFoods();
    }
    
    // Background operations
    public void insert(Food food) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            foodDao.insert(food);
        });
    }
}
```

### 3. Model Layer (`model/`)

Data classes cho UI consumption.

| Model | Mô tả |
|-------|-------|
| `DailySummary` | Tổng hợp calo ngày (consumed, burned, net, remaining) |
| `FoodWithEntry` | Wrapper kết hợp Food + FoodEntry |
| `WorkoutWithEntry` | Wrapper kết hợp Workout + WorkoutEntry |

### 4. Utils Layer (`utils/`)

Utility classes và helper functions.

| Class | Chức năng |
|-------|-----------|
| `CalorieCalculator` | Tính BMR, TDEE, daily goal |
| `Constants` | App constants, meal types, categories |
| `DateUtils` | Date formatting, start/end of day |

## 🔐 Threading Model

```
┌─────────────────────────────────────────────────────────────┐
│                      MAIN THREAD                             │
│  ┌─────────────┐                                            │
│  │     UI      │◀──── LiveData observes ────┐               │
│  │  Updates    │                            │               │
│  └─────────────┘                            │               │
└─────────────────────────────────────────────┼───────────────┘
                                              │
┌─────────────────────────────────────────────┼───────────────┐
│                 BACKGROUND THREADS                          │
│  ┌─────────────────────────────────────────┐│               │
│  │     ExecutorService (4 threads)         ││               │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐   ││               │
│  │  │ Insert  │ │ Update  │ │ Delete  │   ││               │
│  │  └─────────┘ └─────────┘ └─────────┘   ││               │
│  └─────────────────────────────────────────┘│               │
│                      │                      │               │
│                      ▼                      │               │
│  ┌─────────────────────────────────────────┐│               │
│  │           Room Database                  │───────────────┘
│  │    (SQLite with LiveData support)       │                │
│  └─────────────────────────────────────────┘                │
└─────────────────────────────────────────────────────────────┘
```

## 📱 Navigation Flow

```
┌──────────────┐
│   App Start  │
└──────┬───────┘
       │
       ▼
┌──────────────┐     No      ┌──────────────┐
│  Onboarding  │◀────────────│  isComplete? │
│   Complete?  │             └──────────────┘
└──────┬───────┘
       │ Yes
       ▼
┌──────────────┐
│ MainActivity │◀─────────────────────────────┐
│   (Home)     │                              │
└──────┬───────┘                              │
       │                                      │
       ├──────────────┬──────────────┐        │
       ▼              ▼              ▼        │
┌────────────┐ ┌────────────┐ ┌────────────┐  │
│  AddFood   │ │ AddWorkout │ │   Diary    │  │
│  Activity  │ │  Activity  │ │  Activity  │  │
└─────┬──────┘ └─────┬──────┘ └─────┬──────┘  │
      │              │              │         │
      └──────────────┴──────────────┴─────────┘
                     │
                     ▼
              ┌────────────┐
              │  Profile   │
              │  Activity  │
              └────────────┘
```

## 🎨 UI Components

### Material Design 3

- **Theme**: NoActionBar với custom colors
- **Primary Color**: Green (#4CAF50) - Health/Nature
- **Secondary Color**: Orange (#FF9800) - Energy/Activity
- **Cards**: MaterialCardView với elevation
- **Buttons**: MaterialButton với rounded corners
- **Input**: TextInputLayout với Material styling

### Bottom Navigation

```xml
<com.google.android.material.bottomnavigation.BottomNavigationView>
    - Home (MainActivity)
    - Add Food (AddFoodActivity)
    - Diary (DiaryActivity)
    - Profile (ProfileActivity)
</com.google.android.material.bottomnavigation.BottomNavigationView>
```

## 🔧 Configuration

### Build Configuration

```kotlin
// app/build.gradle.kts
android {
    compileSdk = 36
    
    defaultConfig {
        minSdk = 24
        targetSdk = 36
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
```

### Dependencies

```kotlin
dependencies {
    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")
    
    // UI Components
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("com.google.android.material:material:1.12.0")
}
```

## 📈 Performance Considerations

1. **Database Operations**: Tất cả write operations chạy trên background thread
2. **LiveData**: Tự động unsubscribe khi lifecycle ends
3. **RecyclerView**: ViewHolder pattern với DiffUtil
4. **Singleton Database**: Chỉ một instance database trong app
5. **Lazy Loading**: Data chỉ load khi cần thiết

## 🧪 Testing Strategy

### Unit Tests
- Repository tests
- CalorieCalculator tests
- DateUtils tests

### Instrumented Tests
- Database tests
- UI tests với Espresso

## 📚 Tài Liệu Tham Khảo

- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture)
- [Room Persistence Library](https://developer.android.com/training/data-storage/room)
- [LiveData Overview](https://developer.android.com/topic/libraries/architecture/livedata)
- [Material Design 3](https://m3.material.io/)

