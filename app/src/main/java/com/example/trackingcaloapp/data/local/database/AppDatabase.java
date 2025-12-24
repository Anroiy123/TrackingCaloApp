package com.example.trackingcaloapp.data.local.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.trackingcaloapp.data.local.dao.FavoriteFoodDao;
import com.example.trackingcaloapp.data.local.dao.FoodDao;
import com.example.trackingcaloapp.data.local.dao.FoodEntryDao;
import com.example.trackingcaloapp.data.local.dao.WorkoutDao;
import com.example.trackingcaloapp.data.local.dao.WorkoutEntryDao;
import com.example.trackingcaloapp.data.local.entity.FavoriteFood;
import com.example.trackingcaloapp.data.local.entity.Food;
import com.example.trackingcaloapp.data.local.entity.FoodEntry;
import com.example.trackingcaloapp.data.local.entity.Workout;
import com.example.trackingcaloapp.data.local.entity.WorkoutEntry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Room Database chính của ứng dụng.
 * Singleton pattern để đảm bảo chỉ có một instance duy nhất.
 */
@Database(
    entities = {Food.class, FoodEntry.class, Workout.class, WorkoutEntry.class, FavoriteFood.class},
    version = 2,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    
    // DAOs
    public abstract FoodDao foodDao();
    public abstract FoodEntryDao foodEntryDao();
    public abstract WorkoutDao workoutDao();
    public abstract WorkoutEntryDao workoutEntryDao();
    public abstract FavoriteFoodDao favoriteFoodDao();

    // Singleton instance
    private static volatile AppDatabase INSTANCE;
    
    // Executor cho background operations
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    
    /**
     * Lấy instance của database (Singleton)
     */
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "calorie_tracker_db"
                    )
                    .fallbackToDestructiveMigration()
                    .addCallback(sRoomDatabaseCallback)
                    .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Alias for getDatabase() for compatibility
     */
    public static AppDatabase getInstance(final Context context) {
        return getDatabase(context);
    }
    
    /**
     * Callback để populate database khi tạo lần đầu
     */
    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            
            // Populate database với dữ liệu mẫu
            databaseWriteExecutor.execute(() -> {
                FoodDao foodDao = INSTANCE.foodDao();
                WorkoutDao workoutDao = INSTANCE.workoutDao();
                
                // Thêm thực phẩm mẫu
                populateFoods(foodDao);
                
                // Thêm bài tập mẫu
                populateWorkouts(workoutDao);
            });
        }
    };
    
    /**
     * Populate thực phẩm Việt Nam phổ biến
     * Format: new Food(name, calories, protein, carbs, fat, category, aliasVi, servingSize, servingUnit)
     */
    private static void populateFoods(FoodDao foodDao) {
        // ==================== CƠM & BÚN & PHỞ ====================
        foodDao.insert(new Food("Cơm trắng", 130, 2.7f, 28, 0.3f, "com", "com trang, com te, gao, rice", 150, "g"));
        foodDao.insert(new Food("Cơm rang", 180, 4, 25, 7, "com", "com chien, fried rice, com xao", 200, "g"));
        foodDao.insert(new Food("Phở bò", 450, 20, 60, 12, "pho", "pho bo, beef pho, pho tai, pho chin", 400, "tô"));
        foodDao.insert(new Food("Phở gà", 380, 18, 55, 8, "pho", "pho ga, chicken pho", 400, "tô"));
        foodDao.insert(new Food("Bún bò Huế", 480, 22, 58, 15, "bun", "bun bo hue, bun bo", 450, "tô"));
        foodDao.insert(new Food("Bún chả", 550, 25, 50, 25, "bun", "bun cha, bun cha ha noi", 400, "suất"));
        foodDao.insert(new Food("Bún riêu", 420, 18, 52, 14, "bun", "bun rieu, bun rieu cua", 400, "tô"));
        foodDao.insert(new Food("Bánh mì thịt", 350, 15, 40, 14, "banh", "banh mi, banh my, bread, sandwich", 150, "ổ"));
        foodDao.insert(new Food("Bánh cuốn", 180, 6, 28, 5, "banh", "banh cuon, banh cuon nong", 200, "đĩa"));
        foodDao.insert(new Food("Xôi xéo", 280, 6, 45, 8, "xoi", "xoi xeo, xoi dau xanh, sticky rice", 150, "gói"));

        // ==================== THỊT ====================
        foodDao.insert(new Food("Thịt heo luộc", 250, 27, 0, 15, "thit", "thit heo luoc, thit lon luoc, boiled pork", 100, "g"));
        foodDao.insert(new Food("Thịt heo kho", 300, 25, 5, 20, "thit", "thit heo kho, thit kho, thit lon kho", 100, "g"));
        foodDao.insert(new Food("Thịt bò xào", 280, 26, 3, 18, "thit", "thit bo xao, bo xao, stir fried beef", 100, "g"));
        foodDao.insert(new Food("Thịt gà luộc", 165, 31, 0, 3.6f, "thit", "thit ga luoc, ga luoc, boiled chicken", 100, "g"));
        foodDao.insert(new Food("Thịt gà rán", 260, 27, 8, 14, "thit", "thit ga ran, ga ran, fried chicken, ga chien", 100, "g"));
        foodDao.insert(new Food("Sườn nướng", 320, 22, 5, 24, "thit", "suon nuong, suon heo nuong, grilled ribs", 150, "g"));
        foodDao.insert(new Food("Thịt kho tàu", 350, 20, 8, 26, "thit", "thit kho tau, thit kho trung", 100, "g"));

        // ==================== HẢI SẢN ====================
        foodDao.insert(new Food("Cá kho", 180, 22, 3, 9, "hai_san", "ca kho, ca kho to, braised fish", 150, "g"));
        foodDao.insert(new Food("Cá chiên", 220, 20, 5, 13, "hai_san", "ca chien, ca ran, fried fish", 150, "g"));
        foodDao.insert(new Food("Tôm luộc", 99, 21, 0.2f, 1, "hai_san", "tom luoc, boiled shrimp, tom hap", 100, "g"));
        foodDao.insert(new Food("Tôm chiên", 150, 18, 8, 6, "hai_san", "tom chien, tom ran, fried shrimp", 100, "g"));
        foodDao.insert(new Food("Mực xào", 140, 18, 4, 5, "hai_san", "muc xao, squid, muc chien", 100, "g"));

        // ==================== RAU CỦ ====================
        foodDao.insert(new Food("Rau muống xào", 80, 3, 4, 6, "rau", "rau muong xao, rau muong, water spinach", 150, "đĩa"));
        foodDao.insert(new Food("Rau cải luộc", 25, 2, 3, 0.3f, "rau", "rau cai luoc, cai luoc, boiled vegetables", 100, "g"));
        foodDao.insert(new Food("Canh rau", 35, 2, 4, 1, "rau", "canh rau, canh, vegetable soup", 200, "bát"));
        foodDao.insert(new Food("Salad rau trộn", 50, 2, 6, 2, "rau", "salad, rau tron, vegetable salad", 150, "đĩa"));
        foodDao.insert(new Food("Đậu phụ chiên", 180, 12, 5, 13, "rau", "dau phu chien, dau hu chien, tofu, tau hu", 100, "g"));
        foodDao.insert(new Food("Đậu phụ sốt cà", 120, 10, 8, 6, "rau", "dau phu sot ca, dau hu sot ca", 150, "đĩa"));

        // ==================== TRỨNG ====================
        foodDao.insert(new Food("Trứng luộc", 155, 13, 1.1f, 11, "trung", "trung luoc, boiled egg, hot vit lon", 50, "quả"));
        foodDao.insert(new Food("Trứng chiên", 196, 14, 1, 15, "trung", "trung chien, fried egg, trung ran", 60, "quả"));
        foodDao.insert(new Food("Trứng ốp la", 180, 12, 1, 14, "trung", "trung op la, sunny side up", 60, "quả"));

        // ==================== ĐỒ UỐNG ====================
        foodDao.insert(new Food("Trà sữa trân châu", 300, 2, 50, 10, "do_uong", "tra sua, tra sua tran chau, milk tea, bubble tea", 500, "ml"));
        foodDao.insert(new Food("Cà phê sữa đá", 120, 2, 18, 4, "do_uong", "ca phe sua da, coffee, cafe sua", 200, "ml"));
        foodDao.insert(new Food("Cà phê đen", 5, 0.3f, 0, 0, "do_uong", "ca phe den, black coffee, cafe den", 150, "ml"));
        foodDao.insert(new Food("Nước cam", 45, 0.7f, 10, 0.2f, "do_uong", "nuoc cam, orange juice, cam vat", 250, "ml"));
        foodDao.insert(new Food("Sinh tố bơ", 250, 3, 20, 18, "do_uong", "sinh to bo, avocado smoothie, bo dam", 300, "ml"));
        foodDao.insert(new Food("Nước dừa", 45, 0.5f, 9, 0.5f, "do_uong", "nuoc dua, coconut water, dua tuoi", 300, "ml"));

        // ==================== ĂN VẶT ====================
        foodDao.insert(new Food("Bánh tráng trộn", 200, 4, 35, 5, "an_vat", "banh trang tron, rice paper salad", 150, "gói"));
        foodDao.insert(new Food("Gỏi cuốn (2 cuốn)", 150, 8, 20, 4, "an_vat", "goi cuon, fresh spring roll, cuon", 120, "cuốn"));
        foodDao.insert(new Food("Chả giò (2 cuốn)", 180, 6, 15, 11, "an_vat", "cha gio, nem ran, spring roll, nem", 100, "cuốn"));
        foodDao.insert(new Food("Khoai tây chiên", 312, 3.4f, 41, 15, "an_vat", "khoai tay chien, french fries, fries", 100, "g"));

        // ==================== TRÁI CÂY ====================
        foodDao.insert(new Food("Chuối", 89, 1.1f, 23, 0.3f, "trai_cay", "chuoi, banana", 120, "quả"));
        foodDao.insert(new Food("Táo", 52, 0.3f, 14, 0.2f, "trai_cay", "tao, apple", 150, "quả"));
        foodDao.insert(new Food("Cam", 47, 0.9f, 12, 0.1f, "trai_cay", "cam, orange", 150, "quả"));
        foodDao.insert(new Food("Xoài", 60, 0.8f, 15, 0.4f, "trai_cay", "xoai, mango", 200, "quả"));
        foodDao.insert(new Food("Dưa hấu", 30, 0.6f, 8, 0.2f, "trai_cay", "dua hau, watermelon", 200, "g"));
        foodDao.insert(new Food("Nho", 69, 0.7f, 18, 0.2f, "trai_cay", "nho, grape", 100, "g"));
    }
    
    /**
     * Populate bài tập phổ biến
     */
    private static void populateWorkouts(WorkoutDao workoutDao) {
        // ==================== CARDIO (theo phút) ====================
        workoutDao.insert(new Workout("Chạy bộ", 10, "phút", "cardio"));
        workoutDao.insert(new Workout("Đi bộ nhanh", 5, "phút", "cardio"));
        workoutDao.insert(new Workout("Đạp xe", 8, "phút", "cardio"));
        workoutDao.insert(new Workout("Bơi lội", 11, "phút", "cardio"));
        workoutDao.insert(new Workout("Nhảy dây", 12, "phút", "cardio"));
        workoutDao.insert(new Workout("Aerobic", 7, "phút", "cardio"));
        workoutDao.insert(new Workout("Zumba", 8, "phút", "cardio"));
        workoutDao.insert(new Workout("Leo cầu thang", 9, "phút", "cardio"));
        
        // ==================== CARDIO (theo km) ====================
        workoutDao.insert(new Workout("Chạy bộ (km)", 60, "km", "cardio"));
        workoutDao.insert(new Workout("Đi bộ (km)", 40, "km", "cardio"));
        workoutDao.insert(new Workout("Đạp xe (km)", 30, "km", "cardio"));
        
        // ==================== STRENGTH (theo phút) ====================
        workoutDao.insert(new Workout("Tập gym", 6, "phút", "strength"));
        workoutDao.insert(new Workout("Plank", 5, "phút", "strength"));
        workoutDao.insert(new Workout("Tập tạ", 5, "phút", "strength"));
        workoutDao.insert(new Workout("Crossfit", 10, "phút", "strength"));
        
        // ==================== STRENGTH (theo lần) ====================
        workoutDao.insert(new Workout("Hít đất", 0.5f, "lần", "strength"));
        workoutDao.insert(new Workout("Squat", 0.3f, "lần", "strength"));
        workoutDao.insert(new Workout("Gập bụng", 0.3f, "lần", "strength"));
        workoutDao.insert(new Workout("Burpee", 1, "lần", "strength"));
        
        // ==================== FLEXIBILITY ====================
        workoutDao.insert(new Workout("Yoga", 4, "phút", "flexibility"));
        workoutDao.insert(new Workout("Stretching", 3, "phút", "flexibility"));
        workoutDao.insert(new Workout("Pilates", 5, "phút", "flexibility"));
        
        // ==================== SPORTS ====================
        workoutDao.insert(new Workout("Bóng đá", 9, "phút", "cardio"));
        workoutDao.insert(new Workout("Bóng rổ", 8, "phút", "cardio"));
        workoutDao.insert(new Workout("Cầu lông", 7, "phút", "cardio"));
        workoutDao.insert(new Workout("Tennis", 8, "phút", "cardio"));
        workoutDao.insert(new Workout("Bóng chuyền", 5, "phút", "cardio"));
    }
}

