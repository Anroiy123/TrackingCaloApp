package com.example.trackingcaloapp.data.remote;

import android.util.Log;

import com.example.trackingcaloapp.data.local.entity.Food;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser cho USDA FoodData Central API responses.
 * Chuyển đổi JSON response thành Food objects.
 *
 * Response format:
 * {
 *   "totalHits": 26818,
 *   "foods": [
 *     {
 *       "fdcId": 454004,
 *       "description": "APPLE",
 *       "foodNutrients": [
 *         {"nutrientName": "Energy", "unitName": "KCAL", "value": 52.0},
 *         {"nutrientName": "Protein", "unitName": "G", "value": 0.0},
 *         {"nutrientName": "Total lipid (fat)", "unitName": "G", "value": 0.65},
 *         {"nutrientName": "Carbohydrate, by difference", "unitName": "G", "value": 14.3}
 *       ]
 *     }
 *   ]
 * }
 */
public class UsdaParser {
    private static final String TAG = "UsdaParser";

    // Nutrient IDs từ USDA API
    private static final int NUTRIENT_ID_ENERGY = 1008;      // Energy (kcal)
    private static final int NUTRIENT_ID_PROTEIN = 1003;     // Protein
    private static final int NUTRIENT_ID_FAT = 1004;         // Total lipid (fat)
    private static final int NUTRIENT_ID_CARBS = 1005;       // Carbohydrate, by difference

    /**
     * Parse response từ /foods/search endpoint
     */
    public static List<Food> parseSearchResponse(JSONObject response) {
        List<Food> foods = new ArrayList<>();

        try {
            // Check if foods array exists
            if (!response.has("foods")) {
                Log.w(TAG, "No 'foods' array in response");
                return foods;
            }

            JSONArray foodsArray = response.getJSONArray("foods");
            Log.d(TAG, "Parsing " + foodsArray.length() + " foods");

            for (int i = 0; i < foodsArray.length(); i++) {
                Food food = parseFoodObject(foodsArray.getJSONObject(i));
                if (food != null) {
                    foods.add(food);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON parse error: " + e.getMessage());
        }

        return foods;
    }

    /**
     * Parse single food object từ JSON
     */
    private static Food parseFoodObject(JSONObject obj) {
        try {
            // Lấy thông tin cơ bản
            long fdcId = obj.getLong("fdcId");
            String description = obj.optString("description", "Unknown");

            // Format tên food (USDA thường viết hoa hết)
            String name = formatFoodName(description);

            // Parse nutrients
            float calories = 0, protein = 0, carbs = 0, fat = 0;

            if (obj.has("foodNutrients")) {
                JSONArray nutrients = obj.getJSONArray("foodNutrients");

                for (int i = 0; i < nutrients.length(); i++) {
                    JSONObject nutrient = nutrients.getJSONObject(i);

                    int nutrientId = nutrient.optInt("nutrientId", 0);
                    float value = (float) nutrient.optDouble("value", 0);

                    switch (nutrientId) {
                        case NUTRIENT_ID_ENERGY:
                            calories = value;
                            break;
                        case NUTRIENT_ID_PROTEIN:
                            protein = value;
                            break;
                        case NUTRIENT_ID_FAT:
                            fat = value;
                            break;
                        case NUTRIENT_ID_CARBS:
                            carbs = value;
                            break;
                    }
                }
            }

            // Tạo Food object
            Food food = new Food(
                name,       // name
                calories,   // calories per 100g (USDA đã normalize)
                protein,    // protein
                carbs,      // carbs
                fat,        // fat
                "api",      // category
                false       // isCustom
            );

            // Set API fields
            food.setApiId(fdcId);
            food.setApiSource("usda");
            food.setCachedAt(System.currentTimeMillis());

            Log.d(TAG, "Parsed food: " + food.getName() +
                  " (cal=" + food.getCalories() +
                  ", p=" + food.getProtein() +
                  ", c=" + food.getCarbs() +
                  ", f=" + food.getFat() + ")");

            return food;
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing food object: " + e.getMessage());
            return null;
        }
    }

    /**
     * Format food name từ USDA (thường viết hoa hết)
     * "APPLE, RAW" -> "Apple, Raw"
     */
    private static String formatFoodName(String name) {
        if (name == null || name.isEmpty()) {
            return "Unknown";
        }

        // Title case
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;

        for (char c : name.toLowerCase().toCharArray()) {
            if (Character.isWhitespace(c) || c == ',' || c == '-') {
                capitalizeNext = true;
                result.append(c);
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
}
