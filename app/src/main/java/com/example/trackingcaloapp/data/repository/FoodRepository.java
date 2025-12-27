package com.example.trackingcaloapp.data.repository;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.trackingcaloapp.data.local.dao.FoodDao;
import com.example.trackingcaloapp.data.local.database.AppDatabase;
import com.example.trackingcaloapp.data.local.entity.Food;
import com.example.trackingcaloapp.data.remote.UsdaApiService;
import com.example.trackingcaloapp.utils.Constants;

import java.util.List;

/**
 * Repository cho Food entity.
 * Cung cấp abstraction layer giữa ViewModel và data sources.
 * Hỗ trợ cả local Room database và USDA FoodData Central API.
 */
public class FoodRepository {

    private static final String TAG = "FoodRepository";

    private final FoodDao foodDao;
    private final LiveData<List<Food>> allFoods;
    private UsdaApiService apiService;

    /**
     * Constructor cho Application context (ViewModel usage)
     */
    public FoodRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        foodDao = db.foodDao();
        allFoods = foodDao.getAllFoods();
        this.apiService = new UsdaApiService(application);
    }

    /**
     * Constructor cho FoodDao only (existing - backward compatible)
     * Không có API support
     */
    public FoodRepository(FoodDao foodDao) {
        this.foodDao = foodDao;
        this.allFoods = foodDao.getAllFoods();
        this.apiService = null; // No API support
    }

    /**
     * Constructor cho FoodDao + Context (Fragment usage with API)
     */
    public FoodRepository(FoodDao foodDao, Context context) {
        this.foodDao = foodDao;
        this.allFoods = foodDao.getAllFoods();
        this.apiService = new UsdaApiService(context.getApplicationContext());
    }

    /**
     * Initialize API service lazily nếu chưa có
     */
    public void initApiService(Context context) {
        if (this.apiService == null) {
            this.apiService = new UsdaApiService(context.getApplicationContext());
        }
    }

    // ==================== HYBRID SEARCH ====================

    /**
     * Callback interface cho hybrid search (API results)
     */
    public interface HybridSearchCallback {
        void onApiResults(List<Food> foods);
        void onError(String error);
    }

    /**
     * Hybrid search: Room DB + USDA API
     * Local results được trả về qua LiveData
     * API results được trả về qua callback
     *
     * @param query    Từ khóa tìm kiếm
     * @param callback Callback để nhận API results
     */
    public void searchHybrid(String query, HybridSearchCallback callback) {
        // Check if API service is available
        if (apiService == null) {
            Log.w(TAG, "API service not initialized");
            callback.onError("API service not initialized");
            return;
        }

        // Search API if query >= 3 chars
        if (query != null && query.length() >= 3) {
            Log.d(TAG, "Searching USDA API for: " + query);
            apiService.searchFoods(query, 20, new UsdaApiService.SearchCallback() {
                @Override
                public void onSuccess(List<Food> apiFoods) {
                    Log.d(TAG, "USDA API returned " + apiFoods.size() + " foods");

                    // Cache API foods to Room in background
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        for (Food apiFood : apiFoods) {
                            Food existing = foodDao.getFoodByApiId(apiFood.getApiId());
                            if (existing == null) {
                                foodDao.insert(apiFood);
                                Log.d(TAG, "Cached: " + apiFood.getName());
                            }
                        }
                    });

                    callback.onApiResults(apiFoods);
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "USDA API error: " + error);
                    callback.onError(error);
                }
            });
        } else {
            // Query too short, don't call API
            Log.d(TAG, "Query too short for API search");
        }
    }

    /**
     * Search local foods only (cho LiveData observation)
     * Sắp xếp: Local foods trước, API foods sau
     */
    public LiveData<List<Food>> searchFoodsLocal(String query) {
        return foodDao.searchAllFoods(query);
    }

    /**
     * Get food by API ID (check cache)
     */
    public Food getFoodByApiId(long apiId) {
        return foodDao.getFoodByApiId(apiId);
    }

    /**
     * Xóa cached foods cũ hơn X ngày
     * @param days Số ngày
     */
    public void deleteOldCachedFoods(int days) {
        long timestamp = System.currentTimeMillis() - (days * 24L * 60 * 60 * 1000);
        AppDatabase.databaseWriteExecutor.execute(() -> {
            foodDao.deleteOldCachedFoods(timestamp);
        });
    }

    // ==================== GETTERS ====================
    
    /**
     * Lấy tất cả thực phẩm
     */
    public LiveData<List<Food>> getAllFoods() {
        return allFoods;
    }
    
    /**
     * Lấy thực phẩm theo ID
     */
    public Food getFoodById(int foodId) {
        return foodDao.getFoodById(foodId);
    }
    
    /**
     * Lấy thực phẩm theo ID (LiveData)
     */
    public LiveData<Food> getFoodByIdLive(int foodId) {
        return foodDao.getFoodByIdLive(foodId);
    }
    
    /**
     * Tìm kiếm thực phẩm theo tên
     */
    public LiveData<List<Food>> searchFoods(String query) {
        return foodDao.searchFoods(query);
    }
    
    /**
     * Lấy thực phẩm theo category
     */
    public LiveData<List<Food>> getFoodsByCategory(String category) {
        return foodDao.getFoodsByCategory(category);
    }

    /**
     * Lấy thực phẩm phù hợp với loại bữa ăn
     * @param mealType 0=breakfast, 1=lunch, 2=dinner, 3=snack
     */
    public LiveData<List<Food>> getFoodsByMealType(int mealType) {
        List<String> categories = Constants.getCategoriesForMealType(mealType);
        return foodDao.getFoodsByCategories(categories);
    }

    /**
     * Lấy thực phẩm do user tự tạo
     */
    public LiveData<List<Food>> getCustomFoods() {
        return foodDao.getCustomFoods();
    }
    
    /**
     * Lấy danh sách categories
     */
    public LiveData<List<String>> getAllCategories() {
        return foodDao.getAllCategories();
    }
    
    // ==================== INSERT ====================
    
    /**
     * Thêm thực phẩm mới
     */
    public void insert(Food food) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            foodDao.insert(food);
        });
    }
    
    // ==================== UPDATE ====================
    
    /**
     * Cập nhật thực phẩm
     */
    public void update(Food food) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            foodDao.update(food);
        });
    }
    
    // ==================== DELETE ====================
    
    /**
     * Xóa thực phẩm
     */
    public void delete(Food food) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            foodDao.delete(food);
        });
    }
    
    /**
     * Xóa thực phẩm theo ID
     */
    public void deleteById(int foodId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            foodDao.deleteById(foodId);
        });
    }
}

