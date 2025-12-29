# 🏗️ Kiến Trúc Ứng Dụng TrackingCaloApp

## Tổng Quan

TrackingCaloApp được xây dựng theo mô hình **MVVM (Model-View-ViewModel)** kết hợp với **Repository Pattern**, đảm bảo separation of concerns và dễ dàng maintain, test.

## 📊 Sơ Đồ Kiến Trúc

```
┌─────────────────────────────────────────────────────────────────┐
│                         UI LAYER                                 │
│  ┌─────────────┐  ┌─────────────────────────────┐               │
│  │  Activity   │  │         Fragments           │               │
│  │             │  │                             │               │
│  │ LoginActivity│  │ HomeFragment    AddFragment │               │
│  │ RegisterAct │  │ DiaryFragment   ProfileFrag │               │
│  │ MainActivity│  │ AddFoodFragment             │               │
│  │ (Container) │  │ AddWorkoutFragment          │               │
│  │ Onboarding  │  │ FoodEntriesFragment         │               │
│  │  Activity   │  │ WorkoutEntriesFragment      │               │
│  └──────┬──────┘  └──────────┬──────────────────┘               │
│         │                    │                                   │
│         └─────────┬──────────┘                                   │
│                   │ observes LiveData                            │
│                   ▼                                              │
│  ┌─────────────────────────────────────────────┐                │
│  │              ViewModels                      │                │
│  │  ProfileViewModel                            │                │
│  └─────────────────────────────────────────────┘                │
│  ┌─────────────────────────────────────────────┐                │
│  │              Adapters                        │                │
│  │  FoodAdapter, WorkoutAdapter, EntryAdapters │                │
│  │  DiaryPagerAdapter, AddPagerAdapter         │                │
│  │  RecentActivityAdapter                      │                │
│  └─────────────────────────────────────────────┘                │
├─────────────────────────────────────────────────────────────────┤
│                      DATA LAYER                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                    REPOSITORY                            │    │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │    │
│  │  │FoodRepository│  │WorkoutRepo   │  │UserRepository│   │    │
│  │  │FoodEntryRepo │  │WorkoutEntryRepo│ │WeightLogRepo│   │    │
│  │  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘   │    │
│  │         │                 │                 │            │    │
│  │         └────────┬────────┴─────────────────┘            │    │
│  │                  │                                       │    │
│  └──────────────────┼───────────────────────────────────────┘    │
│                     │                                            │
│         ┌───────────┼───────────┬───────────────┐               │
│         ▼           ▼           ▼               ▼               │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐               │
│  │Room Database│ │SharedPrefs  │ │  API Layer  │               │
│  │  (v4)       │ │             │ │             │               │
│  │ ┌─────────┐ │ │UserPrefs    │ │FatSecretAPI │               │
│  │ │  DAOs   │ │ │             │ │UsdaAPI      │               │
│  │ │FoodDao  │ │ │- userName   │ │             │               │
│  │ │EntryDao │ │ │- calorieGoal│ └─────────────┘               │
│  │ │WorkoutDao│ │ │- weightGoal│                               │
│  │ │UserDao  │ │ │- targetWeight                               │
│  │ │WeightLog│ │ │             │                               │
│  │ └─────────┘ │ └─────────────┘                               │
│  │ ┌─────────┐ │                                               │
│  │ │ Entity  │ │                                               │
│  │ │Food     │ │                                               │
│  │ │FoodEntry│ │                                               │
│  │ │Workout  │ │                                               │
│  │ │WorkoutEntry│                                              │
│  │ │WeightLog│ │                                               │
│  │ │User     │ │                                               │
│  │ └─────────┘ │                                               │
│  └─────────────┘                                               │
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

**Kiến trúc Single Activity + Fragments**: App sử dụng một MainActivity làm container chính, các màn hình được triển khai dưới dạng Fragments và chuyển đổi qua Bottom Navigation.

#### Activities

| Activity | Chức năng |
|----------|-----------|
| `LoginActivity` | Màn hình đăng nhập |
| `RegisterActivity` | Màn hình đăng ký tài khoản |
| `MainActivity` | Container chính, quản lý Bottom Navigation và Fragments |
| `OnboardingActivity` | Thiết lập ban đầu cho user mới (+ target weight) |

#### Main Fragments (Bottom Navigation)

| Fragment | Chức năng |
|----------|-----------|
| `HomeFragment` | Dashboard - hiển thị tổng quan calo, progress, charts, hoạt động gần đây |
| `DiaryFragment` | Nhật ký - ViewPager2 với tabs Food/Workout entries + charts |
| `AddFragment` | Container - ViewPager2 với tabs Thêm Food/Workout |
| `ProfileFragment` | Hồ sơ - quản lý thông tin cá nhân, weight tracking, BMI |

#### Child Fragments

| Fragment | Parent | Chức năng |
|----------|--------|-----------|
| `AddFoodFragment` | AddFragment | Thêm thực phẩm vào nhật ký |
| `AddWorkoutFragment` | AddFragment | Thêm bài tập vào nhật ký |
| `FoodEntriesFragment` | DiaryFragment | Hiển thị danh sách food entries |
| `WorkoutEntriesFragment` | DiaryFragment | Hiển thị danh sách workout entries |

#### Adapters

| Adapter | Chức năng |
|---------|-----------|
| `FoodAdapter` | RecyclerView adapter cho danh sách foods |
| `WorkoutAdapter` | RecyclerView adapter cho danh sách workouts |
| `FoodEntryAdapter` | Adapter cho food entries |
| `WorkoutEntryAdapter` | Adapter cho workout entries |
| `RecentActivityAdapter` | Adapter cho hoạt động gần đây |
| `DiaryFragmentPagerAdapter` | ViewPager2 adapter cho diary tabs (Food/Workout entries) |
| `AddPagerAdapter` | ViewPager2 adapter cho add tabs (Food/Workout) |

#### ViewModels

| ViewModel | Chức năng |
|-----------|-----------|
| `ProfileViewModel` | Quản lý state và logic cho ProfileFragment, weight logging |

### 2. Data Layer (`data/`)

#### 2.1 Local Database (`local/`)

**AppDatabase.java** - Room Database Singleton (Version 4)

```java
@Database(
    entities = {Food.class, FoodEntry.class, Workout.class, WorkoutEntry.class, WeightLog.class, User.class},
    version = 4,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    // DAOs
    public abstract FoodDao foodDao();
    public abstract FoodEntryDao foodEntryDao();
    public abstract WorkoutDao workoutDao();
    public abstract WorkoutEntryDao workoutEntryDao();
    public abstract WeightLogDao weightLogDao();
    public abstract UserDao userDao();
    
    // Singleton pattern
    // Migration callbacks (v2→v3: WeightLog, v3→v4: User)
    // ExecutorService for background operations
}
```

**Entities**

| Entity | Mô tả |
|--------|-------|
| `Food` | Thông tin thực phẩm (tên, calo, protein, carbs, fat, apiId, apiSource) |
| `FoodEntry` | Nhật ký ăn uống (foodId, quantity, mealType, date) |
| `Workout` | Thông tin bài tập (tên, calo/unit, unit, category) |
| `WorkoutEntry` | Nhật ký tập luyện (workoutId, quantity, duration, date) |
| `WeightLog` | Lịch sử cân nặng (weight, timestamp, note) |
| `User` | Tài khoản người dùng (username, passwordHash, createdAt) |

**DAOs (Data Access Objects)**

```java
@Dao
public interface FoodDao {
    @Query("SELECT * FROM foods ORDER BY name")
    LiveData<List<Food>> getAllFoods();
    
    @Query("SELECT * FROM foods WHERE name LIKE '%' || :query || '%'")
    LiveData<List<Food>> searchFoods(String query);
    
    // API Integration
    @Query("SELECT * FROM foods WHERE apiId = :apiId AND apiSource = 'fatsecret' LIMIT 1")
    Food getFoodByApiId(long apiId);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Food food);
    
    // ... more queries
}

@Dao
public interface WeightLogDao {
    @Insert
    long insert(WeightLog log);
    
    @Query("SELECT * FROM weight_logs ORDER BY timestamp DESC")
    LiveData<List<WeightLog>> getAllLogs();
    
    @Query("SELECT * FROM weight_logs ORDER BY timestamp DESC LIMIT 1")
    LiveData<WeightLog> getLatestLog();
}

@Dao
public interface UserDao {
    @Insert
    long insert(User user);
    
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username AND passwordHash = :passwordHash)")
    boolean validateCredentials(String username, String passwordHash);
}
```

#### 2.2 API Layer (`api/`)

**FatSecretApiService.java** - FatSecret Platform API

```java
public class FatSecretApiService {
    // OAuth 2.0 authentication
    // Food search endpoint
    // Response parsing to Food entities
    // Caching strategy
}
```

**UsdaApiService.java** - USDA FoodData Central API

```java
public class UsdaApiService {
    // API key authentication
    // Food search endpoint
    // Response parsing to Food entities
}
```

#### 2.3 Preferences (`preferences/`)

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
    
    // Target Weight (Mới)
    - targetWeight
    - targetDate
    - weeklyRate
    
    // Login state (Mới)
    - isLoggedIn
    - currentUserId
    - loginUsername
    
    // App settings
    - isOnboardingComplete
    - themeMode
}
```

#### 2.4 Repository (`repository/`)

Repository pattern cung cấp clean API cho UI layer.

```java
public class FoodRepository {
    private final FoodDao foodDao;
    private UsdaApiService apiService;
    
    // Hybrid search: Local + API
    public void searchHybrid(String query, SearchCallback callback) {
        // 1. Return local results immediately via LiveData
        // 2. Search API if query >= 3 chars
        // 3. Cache API results to Room
        // 4. Callback with API results
    }
}

public class UserRepository {
    public Future<Long> register(String username, String password);
    public Future<User> login(String username, String password);
}

public class WeightLogRepository {
    public void insert(WeightLog log);
    public LiveData<List<WeightLog>> getAllLogs();
    public LiveData<WeightLog> getLatestLog();
}
```

### 3. Model Layer (`model/`)

Data classes cho UI consumption.

| Model | Mô tả |
|-------|-------|
| `DailySummary` | Tổng hợp calo ngày (consumed, burned, net, remaining) |
| `DailyCalorieSum` | Tổng calo theo ngày (cho LineChart) |
| `MacroSum` | Tổng macro nutrients (cho PieChart) |
| `MealTypeCalories` | Calo theo loại bữa ăn (cho BarChart) |
| `HourlyCalorieSum` | Calo theo giờ trong ngày |
| `FoodEntryWithFood` | Wrapper kết hợp Food + FoodEntry |
| `WorkoutEntryWithWorkout` | Wrapper kết hợp Workout + WorkoutEntry |
| `UserInfo` | Thông tin user cho Profile (+ BMI, target weight) |
| `ValidationResult` | Kết quả validate form |

### 4. Utils Layer (`utils/`)

Utility classes và helper functions.

| Class | Chức năng |
|-------|-----------|
| `CalorieCalculator` | Tính BMR, TDEE, daily goal, target weight calculations |
| `ChartHelper` | Helper cho MPAndroidChart (Line, Bar, Pie) |
| `Constants` | App constants, meal types, categories |
| `DateUtils` | Date formatting, start/end of day, period calculations |
| `PasswordUtils` | Password hashing với SHA-256 |

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
┌──────────────┐     No      ┌──────────────────┐
│   isLoggedIn │────────────▶│  LoginActivity   │
│      ?       │             │                  │
└──────┬───────┘             │  ┌────────────┐  │
       │ Yes                 │  │ Register   │  │
       ▼                     │  │ Activity   │  │
┌──────────────┐             └──────────────────┘
│  Onboarding  │     No              │
│   Complete?  │◀────────────────────┘
└──────┬───────┘
       │ Yes
       ▼
┌──────────────────────────────────────────────────────┐
│                    MainActivity                       │
│              (Single Activity Container)              │
│  ┌────────────────────────────────────────────────┐  │
│  │              Fragment Container                 │  │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐       │  │
│  │  │  Home    │ │  Diary   │ │   Add    │       │  │
│  │  │ Fragment │ │ Fragment │ │ Fragment │       │  │
│  │  │ +Charts  │ │ +Charts  │ │          │       │  │
│  │  └──────────┘ └────┬─────┘ └────┬─────┘       │  │
│  │                    │            │              │  │
│  │              ┌─────┴─────┐ ┌────┴────┐        │  │
│  │              │ViewPager2 │ │ViewPager2│        │  │
│  │              │Food|Workout│ │Food|Workout│     │  │
│  │              │ Entries   │ │  Add     │        │  │
│  │              └───────────┘ └──────────┘        │  │
│  │                                                │  │
│  │  ┌──────────┐                                  │  │
│  │  │ Profile  │ ← Weight Chart, BMI, Logout     │  │
│  │  │ Fragment │                                  │  │
│  │  └──────────┘                                  │  │
│  └────────────────────────────────────────────────┘  │
│                                                      │
│  ┌────────────────────────────────────────────────┐  │
│  │           Bottom Navigation Bar                │  │
│  │   [Home]    [Diary]    [Add]    [Profile]     │  │
│  └────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────┘
```

**Fragment Navigation**: Sử dụng FragmentManager để replace fragments trong container khi user chọn tab từ Bottom Navigation.

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
    - Home (HomeFragment)
    - Diary (DiaryFragment)
    - Add (AddFragment → AddFoodFragment/AddWorkoutFragment)
    - Profile (ProfileFragment)
</com.google.android.material.bottomnavigation.BottomNavigationView>
```

**Fragment Transaction**: MainActivity sử dụng `FragmentManager.beginTransaction().replace()` để chuyển đổi giữa các fragments.

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
    implementation("com.google.android.material:material:1.13.0")
    
    // Charts
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    
    // HTTP Client
    implementation("com.android.volley:volley:1.2.1")
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
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
- [FatSecret Platform API](https://platform.fatsecret.com/api/)
- [USDA FoodData Central API](https://fdc.nal.usda.gov/api-guide.html)

## 🔗 Kết nối Emulator MuMu
```powershell
& "D:\Program Files\Netease\MuMuPlayer\nx_main\adb.exe" connect 127.0.0.1:7555
```