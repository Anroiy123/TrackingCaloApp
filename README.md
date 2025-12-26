`# 🍎 TrackingCaloApp`

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green.svg" alt="Platform">
  <img src="https://img.shields.io/badge/Language-Java-orange.svg" alt="Language">
  <img src="https://img.shields.io/badge/Min%20SDK-24-blue.svg" alt="Min SDK">
  <img src="https://img.shields.io/badge/Target%20SDK-36-blue.svg" alt="Target SDK">
  <img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License">
</p>

**TrackingCaloApp** là ứng dụng Android giúp theo dõi lượng calo tiêu thụ và đốt cháy hàng ngày. Ứng dụng được thiết kế đặc biệt cho người dùng Việt Nam với cơ sở dữ liệu thực phẩm và bài tập phổ biến.

## 📱 Screenshots

| Onboarding | Trang chủ | Thêm thực phẩm | Nhật ký |
|:----------:|:---------:|:--------------:|:-------:|
| ![Onboarding](docs/screenshots/onboarding.png) | ![Home](docs/screenshots/home.png) | ![Add Food](docs/screenshots/add_food.png) | ![Diary](docs/screenshots/diary.png) |

## ✨ Tính năng

### 🍽️ Theo dõi thực phẩm
- Thêm thực phẩm vào nhật ký theo bữa ăn (sáng, trưa, tối, snack)
- Cơ sở dữ liệu **50+ món ăn Việt Nam** phổ biến
- Tìm kiếm và lọc theo danh mục
- Tự động tính calo, protein, carbs, fat

### 🏃 Theo dõi bài tập
- Ghi lại các hoạt động thể dục
- Cơ sở dữ liệu **27+ bài tập** phổ biến
- Tính calo đốt cháy tự động
- Phân loại: Cardio, Sức mạnh, Linh hoạt

### 📊 Tính toán thông minh
- Tính **BMR** (Basal Metabolic Rate) theo công thức Mifflin-St Jeor
- Tính **TDEE** (Total Daily Energy Expenditure)
- Tính **BMI** (Body Mass Index)
- Mục tiêu calo tùy chỉnh theo mục đích (giảm/giữ/tăng cân)

### 📅 Nhật ký chi tiết
- Xem lịch sử theo ngày
- Tổng hợp calo tiêu thụ và đốt cháy
- Theo dõi tiến độ với progress bar
- Phân tích dinh dưỡng (protein, carbs, fat)

## 🛠️ Công nghệ sử dụng

| Công nghệ | Mô tả |
|-----------|-------|
| **Java 11** | Ngôn ngữ lập trình chính |
| **Room Database** | Local database với SQLite |
| **LiveData** | Reactive data holder |
| **ViewModel** | UI-related data holder |
| **Material Design 3** | Modern UI components |
| **ViewPager2** | Swipeable views |
| **RecyclerView** | Efficient list display |

## 📁 Cấu trúc Project

```
app/src/main/java/com/example/trackingcaloapp/
│
├── 📁 data/                          # Data layer
│   ├── 📁 local/
│   │   ├── 📁 database/
│   │   │   └── AppDatabase.java      # Room database singleton
│   │   ├── 📁 dao/
│   │   │   ├── FoodDao.java          # Food data access
│   │   │   ├── FoodEntryDao.java     # Food entry data access
│   │   │   ├── WorkoutDao.java       # Workout data access
│   │   │   └── WorkoutEntryDao.java  # Workout entry data access
│   │   └── 📁 entity/
│   │       ├── Food.java             # Food entity
│   │       ├── FoodEntry.java        # Food diary entry
│   │       ├── Workout.java          # Workout entity
│   │       └── WorkoutEntry.java     # Workout diary entry
│   ├── 📁 preferences/
│   │   └── UserPreferences.java      # SharedPreferences wrapper
│   └── 📁 repository/
│       ├── FoodRepository.java       # Food data repository
│       ├── FoodEntryRepository.java  # Food entry repository
│       ├── WorkoutRepository.java    # Workout data repository
│       └── WorkoutEntryRepository.java
│
├── 📁 model/                         # Data models
│   ├── DailySummary.java             # Daily summary model
│   ├── FoodWithEntry.java            # Food with entry wrapper
│   └── WorkoutWithEntry.java         # Workout with entry wrapper
│
├── 📁 ui/                            # UI layer (Single Activity + Fragments)
│   ├── 📁 main/
│   │   └── MainActivity.java         # Container chính + Bottom Navigation
│   ├── 📁 onboarding/
│   │   └── OnboardingActivity.java   # First-time setup
│   ├── 📁 home/
│   │   ├── HomeFragment.java         # Dashboard fragment
│   │   └── RecentActivityAdapter.java
│   ├── 📁 add/
│   │   ├── AddFragment.java          # Container với ViewPager2
│   │   ├── AddPagerAdapter.java      # Adapter cho tabs Food/Workout
│   │   ├── AddFoodFragment.java      # Thêm thực phẩm
│   │   ├── AddWorkoutFragment.java   # Thêm bài tập
│   │   ├── FoodAdapter.java
│   │   └── WorkoutAdapter.java
│   ├── 📁 diary/
│   │   ├── DiaryFragment.java        # Nhật ký với ViewPager2
│   │   ├── DiaryFragmentPagerAdapter.java
│   │   ├── FoodEntriesFragment.java
│   │   ├── FoodEntryAdapter.java
│   │   ├── WorkoutEntriesFragment.java
│   │   └── WorkoutEntryAdapter.java
│   └── 📁 profile/
│       └── ProfileFragment.java      # Profile settings
│
└── 📁 utils/                         # Utilities
    ├── CalorieCalculator.java        # Calorie calculations
    ├── Constants.java                # App constants
    └── DateUtils.java                # Date utilities
```

**Kiến trúc**: Single Activity (MainActivity) + Multiple Fragments, điều hướng qua Bottom Navigation Bar.

## 🚀 Cài đặt

### Yêu cầu
- Android Studio Hedgehog (2023.1.1) trở lên
- JDK 11 trở lên
- Android SDK 24+ (Android 7.0)
- Gradle 8.0+

### Các bước cài đặt

1. **Clone repository**
```bash
git clone https://github.com/Anroiy123/TrackingCaloApp.git
cd TrackingCaloApp
```

2. **Mở project trong Android Studio**
```
File → Open → Chọn thư mục TrackingCaloApp
```

3. **Sync Gradle**
```
Android Studio sẽ tự động sync, hoặc click "Sync Now"
```

4. **Build và chạy**
```
Click Run (▶️) hoặc Shift+F10
```

## 📖 Hướng dẫn sử dụng

### 1. Thiết lập ban đầu (Onboarding)
- Nhập thông tin cá nhân: tên, tuổi, chiều cao, cân nặng
- Chọn giới tính
- Chọn mức độ hoạt động
- Chọn mục tiêu (giảm/giữ/tăng cân)
- App sẽ tự động tính mục tiêu calo hàng ngày

### 2. Thêm thực phẩm
- Từ trang chủ, nhấn tab "Add" ở bottom navigation
- Chọn tab "Thực phẩm" trong màn hình Add
- Tìm kiếm hoặc chọn từ danh sách
- Nhập khối lượng (gram)
- Chọn bữa ăn
- Nhấn "Thêm"

### 3. Thêm bài tập
- Từ trang chủ, nhấn tab "Add" ở bottom navigation
- Chọn tab "Bài tập" trong màn hình Add
- Chọn loại bài tập
- Nhập thời gian/khoảng cách
- Nhấn "Thêm"

### 4. Xem nhật ký
- Nhấn vào tab "Diary" ở bottom navigation
- Chuyển ngày bằng nút prev/next
- Chuyển tab Food/Workout để xem chi tiết
- Xem chi tiết thực phẩm và bài tập

## 🧮 Công thức tính toán

### BMR (Basal Metabolic Rate)
Sử dụng công thức **Mifflin-St Jeor**:

```
Nam:   BMR = (10 × weight) + (6.25 × height) - (5 × age) + 5
Nữ:    BMR = (10 × weight) + (6.25 × height) - (5 × age) - 161
```

### TDEE (Total Daily Energy Expenditure)
```
TDEE = BMR × Activity Multiplier

Activity Multipliers:
- Ít vận động:        1.2
- Vận động nhẹ:       1.375
- Vận động vừa:       1.55
- Vận động nhiều:     1.725
- Vận động rất nhiều: 1.9
```

### Mục tiêu calo
```
Giảm cân:  TDEE - 500 cal/ngày (giảm ~0.5kg/tuần)
Giữ cân:   TDEE
Tăng cân:  TDEE + 500 cal/ngày (tăng ~0.5kg/tuần)
```

### Calo NET
```
Calo NET = Calo tiêu thụ (ăn) - Calo đốt cháy (tập)
Còn lại = Mục tiêu - Calo NET
```

## 🗄️ Database Schema

### Foods Table
| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER | Primary key |
| name | TEXT | Tên thực phẩm |
| calories | REAL | Calo/100g |
| protein | REAL | Protein/100g |
| carbs | REAL | Carbs/100g |
| fat | REAL | Fat/100g |
| category | TEXT | Danh mục |
| isCustom | INTEGER | User tạo? |

### Food Entries Table
| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER | Primary key |
| foodId | INTEGER | FK → Foods |
| quantity | REAL | Khối lượng (g) |
| mealType | INTEGER | 0-3 (bữa ăn) |
| date | INTEGER | Timestamp |
| totalCalories | REAL | Calo đã tính |
| totalProtein | REAL | Protein đã tính |
| totalCarbs | REAL | Carbs đã tính |
| totalFat | REAL | Fat đã tính |

### Workouts Table
| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER | Primary key |
| name | TEXT | Tên bài tập |
| caloriesPerUnit | REAL | Calo/đơn vị |
| unit | TEXT | Đơn vị (phút/km) |
| category | TEXT | Loại bài tập |
| isCustom | INTEGER | User tạo? |

### Workout Entries Table
| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER | Primary key |
| workoutId | INTEGER | FK → Workouts |
| quantity | REAL | Số lượng |
| duration | INTEGER | Thời gian (phút) |
| date | INTEGER | Timestamp |
| caloriesBurned | REAL | Calo đốt cháy |
| note | TEXT | Ghi chú |

## 🤝 Đóng góp

Mọi đóng góp đều được chào đón! Vui lòng:

1. Fork repository
2. Tạo branch mới (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Mở Pull Request

## 📝 License

Distributed under the MIT License. See `LICENSE` for more information.

## 👨‍💻 Tác giả

**Anroiy123**
- GitHub: [@Anroiy123](https://github.com/Anroiy123)

## 🙏 Acknowledgments

- [Material Design](https://material.io/)
- [Android Jetpack](https://developer.android.com/jetpack)
- [Room Persistence Library](https://developer.android.com/training/data-storage/room)

## connect emulator MUMU 
& "D:\Program Files\Netease\MuMuPlayer\nx_main\adb.exe" connect 127.0.0.1:7555