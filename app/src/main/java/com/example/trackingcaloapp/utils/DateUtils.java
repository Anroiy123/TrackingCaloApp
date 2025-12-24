package com.example.trackingcaloapp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class để xử lý các thao tác liên quan đến ngày tháng.
 */
public class DateUtils {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private static final SimpleDateFormat DATE_FORMAT_FULL = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    
    /**
     * Lấy timestamp đầu ngày (00:00:00) của một ngày
     */
    public static long getStartOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
    
    /**
     * Lấy timestamp cuối ngày (23:59:59) của một ngày
     */
    public static long getEndOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }
    
    /**
     * Lấy timestamp đầu ngày hôm nay
     */
    public static long getStartOfToday() {
        return getStartOfDay(System.currentTimeMillis());
    }
    
    /**
     * Lấy timestamp cuối ngày hôm nay
     */
    public static long getEndOfToday() {
        return getEndOfDay(System.currentTimeMillis());
    }
    
    /**
     * Lấy timestamp đầu ngày hôm qua
     */
    public static long getStartOfYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return getStartOfDay(calendar.getTimeInMillis());
    }
    
    /**
     * Lấy timestamp cuối ngày hôm qua
     */
    public static long getEndOfYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return getEndOfDay(calendar.getTimeInMillis());
    }
    
    /**
     * Format timestamp thành chuỗi ngày (dd/MM/yyyy)
     */
    public static String formatDate(long timestamp) {
        return DATE_FORMAT.format(new Date(timestamp));
    }
    
    /**
     * Format timestamp thành chuỗi ngày đầy đủ (Thứ, dd/MM/yyyy)
     */
    public static String formatDateFull(long timestamp) {
        return DATE_FORMAT_FULL.format(new Date(timestamp));
    }
    
    /**
     * Format timestamp thành chuỗi giờ (HH:mm)
     */
    public static String formatTime(long timestamp) {
        return TIME_FORMAT.format(new Date(timestamp));
    }
    
    /**
     * Format timestamp thành chuỗi ngày giờ (dd/MM/yyyy HH:mm)
     */
    public static String formatDateTime(long timestamp) {
        return DATE_TIME_FORMAT.format(new Date(timestamp));
    }
    
    /**
     * Kiểm tra xem timestamp có phải là hôm nay không
     */
    public static boolean isToday(long timestamp) {
        long startOfToday = getStartOfToday();
        long endOfToday = getEndOfToday();
        return timestamp >= startOfToday && timestamp <= endOfToday;
    }
    
    /**
     * Kiểm tra xem timestamp có phải là hôm qua không
     */
    public static boolean isYesterday(long timestamp) {
        long startOfYesterday = getStartOfYesterday();
        long endOfYesterday = getEndOfYesterday();
        return timestamp >= startOfYesterday && timestamp <= endOfYesterday;
    }
    
    /**
     * Lấy chuỗi hiển thị thân thiện cho ngày
     * VD: "Hôm nay", "Hôm qua", "Thứ Hai, 01/12/2024"
     */
    public static String getDisplayDate(long timestamp) {
        if (isToday(timestamp)) {
            return "Hôm nay";
        } else if (isYesterday(timestamp)) {
            return "Hôm qua";
        } else {
            return formatDateFull(timestamp);
        }
    }
    
    /**
     * Lấy timestamp của n ngày trước
     */
    public static long getDaysAgo(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -days);
        return calendar.getTimeInMillis();
    }
    
    /**
     * Lấy timestamp đầu tuần (Thứ Hai)
     */
    public static long getStartOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return getStartOfDay(calendar.getTimeInMillis());
    }
    
    /**
     * Lấy timestamp cuối tuần (Chủ Nhật)
     */
    public static long getEndOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.add(Calendar.WEEK_OF_YEAR, 1); // Chủ Nhật tuần này
        return getEndOfDay(calendar.getTimeInMillis());
    }
    
    /**
     * Lấy timestamp đầu tháng
     */
    public static long getStartOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return getStartOfDay(calendar.getTimeInMillis());
    }
    
    /**
     * Lấy timestamp cuối tháng
     */
    public static long getEndOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return getEndOfDay(calendar.getTimeInMillis());
    }
    
    /**
     * Tính số ngày giữa 2 timestamp
     */
    public static int getDaysBetween(long start, long end) {
        long diff = end - start;
        return (int) (diff / (24 * 60 * 60 * 1000));
    }

    /**
     * Auto-suggest meal type dựa trên giờ hiện tại
     * 06:00-10:00 → Sáng
     * 10:00-14:00 → Trưa
     * 18:00-22:00 → Tối
     * Còn lại → Ăn vặt
     */
    public static int getMealTypeByTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= Constants.BREAKFAST_START_HOUR && hour < Constants.BREAKFAST_END_HOUR) {
            return Constants.MEAL_BREAKFAST;
        } else if (hour >= Constants.LUNCH_START_HOUR && hour < Constants.LUNCH_END_HOUR) {
            return Constants.MEAL_LUNCH;
        } else if (hour >= Constants.DINNER_START_HOUR && hour < Constants.DINNER_END_HOUR) {
            return Constants.MEAL_DINNER;
        } else {
            return Constants.MEAL_SNACK;
        }
    }

    /**
     * Auto-suggest meal type dựa trên timestamp
     */
    public static int getMealTypeByTime(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= Constants.BREAKFAST_START_HOUR && hour < Constants.BREAKFAST_END_HOUR) {
            return Constants.MEAL_BREAKFAST;
        } else if (hour >= Constants.LUNCH_START_HOUR && hour < Constants.LUNCH_END_HOUR) {
            return Constants.MEAL_LUNCH;
        } else if (hour >= Constants.DINNER_START_HOUR && hour < Constants.DINNER_END_HOUR) {
            return Constants.MEAL_DINNER;
        } else {
            return Constants.MEAL_SNACK;
        }
    }

    /**
     * Format ngày theo tiếng Việt đầy đủ
     * VD: "Thứ Hai, 10/12/2025"
     */
    public static String formatDateVietnamese(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        String dayOfWeek;
        int dow = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dow) {
            case Calendar.MONDAY: dayOfWeek = "Thứ Hai"; break;
            case Calendar.TUESDAY: dayOfWeek = "Thứ Ba"; break;
            case Calendar.WEDNESDAY: dayOfWeek = "Thứ Tư"; break;
            case Calendar.THURSDAY: dayOfWeek = "Thứ Năm"; break;
            case Calendar.FRIDAY: dayOfWeek = "Thứ Sáu"; break;
            case Calendar.SATURDAY: dayOfWeek = "Thứ Bảy"; break;
            case Calendar.SUNDAY: dayOfWeek = "Chủ Nhật"; break;
            default: dayOfWeek = "";
        }

        return dayOfWeek + ", " + formatDate(timestamp);
    }
}

