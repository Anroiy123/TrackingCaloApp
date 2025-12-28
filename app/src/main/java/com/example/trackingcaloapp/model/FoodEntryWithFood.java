package com.example.trackingcaloapp.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.trackingcaloapp.data.local.entity.Food;
import com.example.trackingcaloapp.data.local.entity.FoodEntry;

/**
 * POJO class để Room map kết quả JOIN giữa FoodEntry và Food.
 * Sử dụng @Embedded và @Relation để Room tự động JOIN.
 */
public class FoodEntryWithFood {

    @Embedded
    private FoodEntry foodEntry;

    @Relation(
        parentColumn = "foodId",
        entityColumn = "id"
    )
    private Food food;

    public FoodEntryWithFood() {}

    public FoodEntry getFoodEntry() {
        return foodEntry;
    }

    public void setFoodEntry(FoodEntry foodEntry) {
        this.foodEntry = foodEntry;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    /**
     * Chuyển đổi sang FoodWithEntry để sử dụng trong UI
     */
    public FoodWithEntry toFoodWithEntry() {
        return new FoodWithEntry(food, foodEntry);
    }
}

