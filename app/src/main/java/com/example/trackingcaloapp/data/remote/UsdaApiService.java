package com.example.trackingcaloapp.data.remote;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.trackingcaloapp.data.local.entity.Food;

import org.json.JSONObject;

import java.util.List;

/**
 * Service class cho USDA FoodData Central API.
 * API miễn phí, không cần IP whitelist.
 * Rate limit: 1000 requests/hour
 *
 * Docs: https://fdc.nal.usda.gov/api-guide/
 */
public class UsdaApiService {
    private static final String TAG = "UsdaApiService";
    private static final String BASE_URL = "https://api.nal.usda.gov/fdc/v1";

    // DEMO_KEY có thể dùng để test, rate limit thấp hơn
    // Đăng ký key miễn phí tại: https://api.data.gov/signup/
    private static final String API_KEY = "DEMO_KEY";

    // Retry policy constants
    private static final int TIMEOUT_MS = 15000;  // 15 seconds
    private static final int MAX_RETRIES = 1;
    private static final float BACKOFF_MULT = 1.0f;

    private final Context context;
    private final RequestQueue requestQueue;

    public UsdaApiService(Context context) {
        this.context = context.getApplicationContext();
        this.requestQueue = Volley.newRequestQueue(this.context);
    }

    /**
     * Callback interface cho search results
     */
    public interface SearchCallback {
        void onSuccess(List<Food> foods);
        void onError(String error);
    }

    /**
     * Kiểm tra kết nối mạng
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    /**
     * Search foods từ USDA FoodData Central API
     *
     * @param query      Từ khóa tìm kiếm (ví dụ: "chicken", "rice", "apple")
     * @param maxResults Số kết quả tối đa (1-50)
     * @param callback   Callback để nhận kết quả
     */
    public void searchFoods(String query, int maxResults, SearchCallback callback) {
        // Kiểm tra network
        if (!isNetworkAvailable()) {
            callback.onError("Không có kết nối mạng");
            return;
        }

        // Validate input
        if (query == null || query.trim().isEmpty()) {
            callback.onError("Từ khóa tìm kiếm không hợp lệ");
            return;
        }

        // Limit max results
        maxResults = Math.max(1, Math.min(maxResults, 50));

        Log.d(TAG, "Searching for: " + query + " (max=" + maxResults + ")");

        // Build URL với query parameters
        String url = BASE_URL + "/foods/search" +
            "?api_key=" + API_KEY +
            "&query=" + Uri.encode(query) +
            "&pageSize=" + maxResults;

        Log.d(TAG, "API URL: " + url);

        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            response -> {
                Log.d(TAG, "Response received");
                List<Food> foods = UsdaParser.parseSearchResponse(response);
                Log.d(TAG, "Parsed " + foods.size() + " foods");
                callback.onSuccess(foods);
            },
            error -> {
                String errorMsg = "Lỗi kết nối API";
                if (error.networkResponse != null) {
                    int statusCode = error.networkResponse.statusCode;
                    errorMsg = "Lỗi API: " + statusCode;
                    if (statusCode == 403) {
                        errorMsg = "API key không hợp lệ";
                    } else if (statusCode == 429) {
                        errorMsg = "Quá nhiều request, vui lòng đợi";
                    }
                } else if (error.getMessage() != null) {
                    errorMsg = error.getMessage();
                }
                Log.e(TAG, "API error: " + errorMsg);
                callback.onError(errorMsg);
            }
        );

        // Set retry policy
        request.setRetryPolicy(new DefaultRetryPolicy(
            TIMEOUT_MS, MAX_RETRIES, BACKOFF_MULT));

        // Add to queue
        requestQueue.add(request);
    }

    /**
     * Cancel tất cả pending requests
     */
    public void cancelAllRequests() {
        requestQueue.cancelAll(request -> true);
    }
}
