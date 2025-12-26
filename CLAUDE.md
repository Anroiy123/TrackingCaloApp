# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

TrackingCaloApp is a Vietnamese calorie tracking Android application built with Java 11. The app helps users track daily food intake and workout activities, calculate calorie goals based on personal metrics (BMR/TDEE using Mifflin-St Jeor formula), and maintain a food/exercise diary.

## Build & Development Commands

```bash
# Build the project
./gradlew build

# Run debug build
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Run unit tests
./gradlew test

# Run single test class
./gradlew test --tests "com.example.trackingcaloapp.ExampleUnitTest"

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Clean build
./gradlew clean

# Sync Gradle (after changing dependencies)
./gradlew --refresh-dependencies
```

## Architecture

**MVVM + Repository Pattern** with the following layers:

### Data Layer (`app/src/main/java/.../data/`)

**Entities** (`local/entity/`):
- `Food` - Food items with nutritional info per 100g (name, calories, protein, carbs, fat, category, isCustom)
- `FoodEntry` - User's food diary entries with foreign key to Food, stores calculated totals
- `Workout` - Exercise types with caloriesPerUnit, unit (phút/km/lần), category (cardio/strength/flexibility)
- `WorkoutEntry` - Workout diary entries with foreign key to Workout, stores caloriesBurned

**DAOs** (`local/dao/`): Room interfaces with LiveData returns for reactive UI updates
- Support both sync methods (for background) and LiveData methods (for UI observation)
- Date-based queries use `BETWEEN :startOfDay AND :endOfDay` pattern
- Aggregation queries: `getTotalCaloriesByDate()`, `getTotalCaloriesBurnedByDate()`

**Database** (`local/database/AppDatabase.java`):
- Room singleton with 4-thread executor for write operations
- Auto-populates 50+ Vietnamese foods and 25+ workouts on first creation via `RoomDatabase.Callback`
- Database name: `calorie_tracker_db`

**Repositories** (`repository/`):
- `FoodRepository`, `FoodEntryRepository`, `WorkoutRepository`, `WorkoutEntryRepository`
- All write operations wrapped in `AppDatabase.databaseWriteExecutor.execute()`
- Support both Application and direct DAO injection constructors

**Preferences** (`preferences/UserPreferences.java`):
- SharedPreferences wrapper for user profile (name, age, height, weight, gender)
- Activity level/weight goal stored as strings, with int overloads for spinner positions
- Key method: `isOnboardingComplete()` - controls app flow

### UI Layer (`app/src/main/java/.../ui/`)

**App Flow:**
1. `MainActivity` checks `userPreferences.isOnboardingComplete()`
2. If false → redirects to `OnboardingActivity`
3. If true → shows dashboard with today's calorie summary

**Activities:**
- `MainActivity` - Dashboard with ProgressBar, calorie stats, RecentActivityAdapter (shows last 5 activities)
- `OnboardingActivity` - Collects user info, calculates calorie goal via `CalorieCalculator`, saves to `UserPreferences`
- `AddFoodActivity` - RecyclerView with search/ChipGroup filter, click shows `AlertDialog` for quantity input
- `AddWorkoutActivity` - Similar pattern with category filter chips
- `DiaryActivity` - ViewPager2 + TabLayout with `FoodEntriesFragment` and `WorkoutEntriesFragment`
- `ProfileActivity` - Edit user profile, recalculate calorie goal, shows BMI

**Adapters Pattern:**
- `FoodAdapter`, `WorkoutAdapter` - Display lists with click listeners via interface callbacks
- `FoodEntryAdapter`, `WorkoutEntryAdapter` - Display diary entries with meal/category color indicators
- `RecentActivityAdapter` - Combines food + workout entries, sorts by date, limits to 5
- `DiaryPagerAdapter` - FragmentStateAdapter for ViewPager2

**Dialog Pattern for Adding Entries:**
```java
// Inflate custom dialog layout
View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_food_entry, null);
// Setup TextWatcher for live calculation preview
new AlertDialog.Builder(this)
    .setView(dialogView)
    .setPositiveButton("Thêm", (dialog, which) -> addEntry())
    .setNegativeButton("Hủy", null)
    .show();
```

### Model Layer (`app/src/main/java/.../model/`)
- `DailySummary` - Aggregates daily data with calculated properties (netCalories, remainingCalories, progressPercentage)
- `FoodWithEntry`, `WorkoutWithEntry` - Join wrappers for displaying entry with parent entity details

### Utils (`app/src/main/java/.../utils/`)
- `CalorieCalculator` - Mifflin-St Jeor BMR, TDEE with activity multiplier, BMI
- `DateUtils` - `getStartOfDay()`/`getEndOfDay()` for timestamp ranges, Vietnamese date formatting
- `Constants` - Meal type ints (0-3), category strings, intent extra keys

## Key Patterns

### Database Queries by Date Range
All diary queries use timestamp ranges:
```java
long startOfDay = DateUtils.getStartOfDay(timestamp);
long endOfDay = DateUtils.getEndOfDay(timestamp);
repository.getEntriesByDate(startOfDay, endOfDay);
```

### LiveData Observation
UI automatically updates when data changes:
```java
foodEntryRepository.getTotalCaloriesByDate(start, end).observe(this, consumed -> {
    // Update UI - runs on main thread
});
```

### Background Database Operations
All write operations use the executor:
```java
AppDatabase.databaseWriteExecutor.execute(() -> {
    dao.insert(entity);
});
```

### Adding Food/Workout Entry Flow
1. User clicks item in list → `onFoodClick(Food food)` callback
2. Show dialog with quantity input and live calculation preview
3. Create entry with calculated totals: `entry.setTotalCalories(food.calculateCalories(quantity))`
4. Insert via repository → `finish()` activity
5. MainActivity's `onResume()` reloads data via LiveData observers

### Fragment Date Updates (DiaryActivity)
```java
// DiaryPagerAdapter holds fragment references
public void updateDate(long newDate) {
    this.selectedDate = newDate;
    if (foodFragment != null) foodFragment.updateDate(newDate);
    if (workoutFragment != null) workoutFragment.updateDate(newDate);
}
```

## Data Model Notes

- **Food nutritional values**: Stored per 100g, calculated on insertion: `(value * quantity) / 100`
- **Meal types**: 0=breakfast, 1=lunch, 2=dinner, 3=snack (`Constants.MEAL_*`)
- **Workout units**: "phút" (minutes), "km", "lần" (reps)
- **Workout categories**: "cardio", "strength", "flexibility"
- **Food categories**: "com", "pho", "bun", "thit", "hai_san", "rau", "do_uong", "an_vat", "trai_cay"
- **Activity levels**: sedentary(1.2), light(1.375), moderate(1.55), active(1.725), very_active(1.9)
- **Weight goals**: lose (-500 cal), maintain (0), gain (+500 cal) from TDEE

## UI/Color Conventions

- **Primary**: Green (#4CAF50) - Health theme
- **Secondary**: Orange (#FF9800) - Energy/Calories
- **Meal colors**: breakfast(#FFB74D), lunch(#4FC3F7), dinner(#7986CB), snack(#AED581)
- **Workout colors**: cardio(#EF5350), strength(#5C6BC0), flexibility(#26A69A)
- **Calories**: consumed(#FF5722), burned(#4CAF50), remaining(#2196F3)

## Language

- All UI strings are in Vietnamese (see `res/values/strings.xml`)
- Database is pre-populated with 50+ Vietnamese foods and 25+ workouts
- Comments in code are primarily in Vietnamese

## Dependencies

Key libraries (see `gradle/libs.versions.toml`):
- Room Database 2.6.1
- Lifecycle (ViewModel, LiveData) 2.7.0
- Material Design 3 (1.13.0)
- ViewPager2 1.1.0
- Target SDK 36, Min SDK 27
