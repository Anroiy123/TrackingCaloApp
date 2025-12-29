package com.example.trackingcaloapp.utils;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.model.DailyCalorieSum;
import com.example.trackingcaloapp.model.HourlyCalorieSum;
import com.example.trackingcaloapp.model.MacroSum;
import com.example.trackingcaloapp.model.MealTypeCalories;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Utility class để cấu hình và cập nhật MPAndroidChart charts.
 * Cung cấp consistent styling theo theme của app.
 * Supports both light and dark mode through dynamic color resolution.
 */
public class ChartHelper {

    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("EEE", new Locale("vi", "VN"));

    // ==================== DYNAMIC COLOR GETTERS ====================

    /** Get primary color from resources (theme-aware) */
    public static int getColorPrimary(Context ctx) {
        return ContextCompat.getColor(ctx, R.color.primary);
    }

    /** Get primary color with alpha for fills */
    public static int getColorPrimaryLight(Context ctx) {
        int primary = getColorPrimary(ctx);
        return Color.argb(128, Color.red(primary), Color.green(primary), Color.blue(primary));
    }

    /** Get consumed calories color */
    public static int getColorConsumed(Context ctx) {
        return ContextCompat.getColor(ctx, R.color.calories_consumed);
    }

    /** Get burned calories color */
    public static int getColorBurned(Context ctx) {
        return ContextCompat.getColor(ctx, R.color.calories_burned);
    }

    /** Get breakfast color */
    public static int getColorBreakfast(Context ctx) {
        return ContextCompat.getColor(ctx, R.color.breakfast);
    }

    /** Get lunch color */
    public static int getColorLunch(Context ctx) {
        return ContextCompat.getColor(ctx, R.color.lunch);
    }

    /** Get dinner color */
    public static int getColorDinner(Context ctx) {
        return ContextCompat.getColor(ctx, R.color.dinner);
    }

    /** Get snack color */
    public static int getColorSnack(Context ctx) {
        return ContextCompat.getColor(ctx, R.color.snack);
    }

    /** Get protein color */
    public static int getColorProtein(Context ctx) {
        return ContextCompat.getColor(ctx, R.color.protein);
    }

    /** Get carbs color */
    public static int getColorCarbs(Context ctx) {
        return ContextCompat.getColor(ctx, R.color.carbs);
    }

    /** Get fat color */
    public static int getColorFat(Context ctx) {
        return ContextCompat.getColor(ctx, R.color.fat);
    }

    /** Get surface color for transparent circles in pie chart */
    public static int getColorSurface(Context ctx) {
        return ContextCompat.getColor(ctx, R.color.surface);
    }

    // ==================== LINE CHART ====================

    /**
     * Setup LineChart với styling cơ bản cho calorie trend
     */
    public static void setupLineChart(LineChart chart, Context ctx) {
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);
        chart.setBackgroundColor(Color.TRANSPARENT);
        chart.setExtraBottomOffset(10f);

        // X-axis configuration
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(ContextCompat.getColor(ctx, R.color.text_secondary));
        xAxis.setTextSize(10f);

        // Y-axis configuration
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(ContextCompat.getColor(ctx, R.color.outline));
        leftAxis.setTextColor(ContextCompat.getColor(ctx, R.color.text_secondary));
        leftAxis.setTextSize(10f);
        leftAxis.setAxisMinimum(0f);

        chart.getAxisRight().setEnabled(false);

        // Legend configuration
        Legend legend = chart.getLegend();
        legend.setEnabled(false);

        // Empty state
        chart.setNoDataText("Chưa có dữ liệu");
        chart.setNoDataTextColor(ContextCompat.getColor(ctx, R.color.text_hint));
    }

    /**
     * Update LineChart data với daily calorie summary
     */
    public static void updateLineChartData(LineChart chart, List<DailyCalorieSum> data, Context ctx) {
        if (data == null || data.isEmpty()) {
            chart.clear();
            chart.invalidate();
            return;
        }

        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            DailyCalorieSum item = data.get(i);
            entries.add(new Entry(i, item.getTotalCalories()));
            labels.add(DAY_FORMAT.format(new Date(item.getDayTimestamp())));
        }

        int primaryColor = getColorPrimary(ctx);
        int primaryLightColor = getColorPrimaryLight(ctx);

        LineDataSet dataSet = new LineDataSet(entries, "Calories");
        dataSet.setColor(primaryColor);
        dataSet.setCircleColor(primaryColor);
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(true);
        dataSet.setCircleHoleRadius(2f);
        dataSet.setValueTextSize(9f);
        dataSet.setValueTextColor(ContextCompat.getColor(ctx, R.color.text_secondary));
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(primaryLightColor);
        dataSet.setFillAlpha(50);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.2f);

        // Value formatter để hiển thị số nguyên
        dataSet.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        // Set x-axis labels
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.getXAxis().setLabelCount(labels.size());

        chart.animateX(500);
        chart.invalidate();
    }

    // ==================== BAR CHART ====================

    /**
     * Setup BarChart với styling cho meal type comparison
     */
    public static void setupBarChart(BarChart chart, Context ctx) {
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);
        chart.setBackgroundColor(Color.TRANSPARENT);
        chart.setExtraBottomOffset(10f);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        // X-axis configuration
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(ContextCompat.getColor(ctx, R.color.text_secondary));
        xAxis.setTextSize(11f);

        // Y-axis configuration
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(ContextCompat.getColor(ctx, R.color.outline));
        leftAxis.setTextColor(ContextCompat.getColor(ctx, R.color.text_secondary));
        leftAxis.setTextSize(10f);
        leftAxis.setAxisMinimum(0f);

        chart.getAxisRight().setEnabled(false);

        // Legend configuration
        Legend legend = chart.getLegend();
        legend.setEnabled(false);

        // Empty state
        chart.setNoDataText("Chưa có dữ liệu");
        chart.setNoDataTextColor(ContextCompat.getColor(ctx, R.color.text_hint));
    }

    /**
     * Update BarChart data với meal type calories
     */
    public static void updateBarChartData(BarChart chart, List<MealTypeCalories> data, Context ctx) {
        if (data == null || data.isEmpty()) {
            chart.clear();
            chart.invalidate();
            return;
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>(List.of("Sáng", "Trưa", "Tối", "Phụ"));
        int[] colors = new int[]{
            getColorBreakfast(ctx),
            getColorLunch(ctx),
            getColorDinner(ctx),
            getColorSnack(ctx)
        };

        // Initialize all 4 meal types with 0
        float[] mealCalories = new float[4];

        for (MealTypeCalories item : data) {
            int mealType = item.getMealType();
            if (mealType >= 0 && mealType < 4) {
                mealCalories[mealType] = item.getTotalCalories();
            }
        }

        List<Integer> barColors = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            entries.add(new BarEntry(i, mealCalories[i]));
            barColors.add(colors[i]);
        }

        BarDataSet dataSet = new BarDataSet(entries, "Bữa ăn");
        dataSet.setColors(barColors);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(ContextCompat.getColor(ctx, R.color.text_secondary));

        // Value formatter để hiển thị số nguyên
        dataSet.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0) return "";
                return String.valueOf((int) value);
            }
        });

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);
        chart.setData(barData);

        // Set x-axis labels
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.getXAxis().setLabelCount(4);

        chart.animateY(500);
        chart.invalidate();
    }

    // ==================== PIE CHART ====================

    /**
     * Setup PieChart với styling cho macro distribution
     */
    public static void setupPieChart(PieChart chart, Context ctx) {
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.TRANSPARENT);
        chart.setHoleRadius(50f);
        chart.setTransparentCircleRadius(55f);
        chart.setTransparentCircleColor(getColorSurface(ctx));
        chart.setTransparentCircleAlpha(100);
        chart.setDrawCenterText(true);
        chart.setCenterTextSize(14f);
        chart.setCenterTextColor(ContextCompat.getColor(ctx, R.color.text_primary));
        chart.setExtraOffsets(5f, 10f, 5f, 10f);

        // Legend configuration
        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextColor(ContextCompat.getColor(ctx, R.color.text_secondary));
        legend.setTextSize(11f);
        legend.setXEntrySpace(15f);

        // Empty state
        chart.setNoDataText("Chưa có dữ liệu");
        chart.setNoDataTextColor(ContextCompat.getColor(ctx, R.color.text_hint));
    }

    /**
     * Update PieChart data với macro summary.
     * Chỉ hiển thị số gam trong slice, không có label text (legend ở dưới đã có).
     */
    public static void updatePieChartData(PieChart chart, MacroSum data, Context ctx) {
        if (data == null || data.getTotal() == 0) {
            chart.clear();
            chart.setCenterText("0g");
            chart.invalidate();
            return;
        }

        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        int proteinColor = getColorProtein(ctx);
        int carbsColor = getColorCarbs(ctx);
        int fatColor = getColorFat(ctx);

        // Không truyền label vào PieEntry - chỉ hiển thị số gam
        // Legend ở dưới sẽ hiển thị tên macro với màu tương ứng
        if (data.getProtein() > 0) {
            entries.add(new PieEntry(data.getProtein()));
            colors.add(proteinColor);
        }
        if (data.getCarbs() > 0) {
            entries.add(new PieEntry(data.getCarbs()));
            colors.add(carbsColor);
        }
        if (data.getFat() > 0) {
            entries.add(new PieEntry(data.getFat()));
            colors.add(fatColor);
        }

        if (entries.isEmpty()) {
            chart.clear();
            chart.setCenterText("0g");
            chart.invalidate();
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(11f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(5f);

        // Value formatter để hiển thị gram (chỉ số + "g")
        dataSet.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int) value + "g";
            }
        });

        PieData pieData = new PieData(dataSet);
        chart.setData(pieData);

        // Set center text với tổng
        chart.setCenterText((int) data.getTotal() + "g");

        // Manually set legend entries để hiển thị tên macro
        Legend legend = chart.getLegend();
        List<LegendEntry> legendEntries = new ArrayList<>();
        if (data.getProtein() > 0) {
            legendEntries.add(new LegendEntry("Protein", Legend.LegendForm.SQUARE, 10f, 2f, null, proteinColor));
        }
        if (data.getCarbs() > 0) {
            legendEntries.add(new LegendEntry("Carbs", Legend.LegendForm.SQUARE, 10f, 2f, null, carbsColor));
        }
        if (data.getFat() > 0) {
            legendEntries.add(new LegendEntry("Fat", Legend.LegendForm.SQUARE, 10f, 2f, null, fatColor));
        }
        legend.setCustom(legendEntries);

        chart.animateY(500);
        chart.invalidate();
    }

    // ==================== HELPER METHODS ====================

    /**
     * Update LineChart data với hourly calorie summary (cho DiaryFragment)
     */
    public static void updateHourlyLineChartData(LineChart chart, List<HourlyCalorieSum> data, Context ctx) {
        if (data == null || data.isEmpty()) {
            chart.clear();
            chart.invalidate();
            return;
        }

        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            HourlyCalorieSum item = data.get(i);
            entries.add(new Entry(i, item.getTotalCalories()));
            labels.add(String.format(Locale.getDefault(), "%02d:00", item.getHour()));
        }

        int primaryColor = getColorPrimary(ctx);
        int primaryLightColor = getColorPrimaryLight(ctx);

        LineDataSet dataSet = new LineDataSet(entries, "Calories");
        dataSet.setColor(primaryColor);
        dataSet.setCircleColor(primaryColor);
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(true);
        dataSet.setCircleHoleRadius(2f);
        dataSet.setValueTextSize(9f);
        dataSet.setValueTextColor(ContextCompat.getColor(ctx, R.color.text_secondary));
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(primaryLightColor);
        dataSet.setFillAlpha(50);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.2f);

        // Value formatter để hiển thị số nguyên
        dataSet.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        // Set x-axis labels
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.getXAxis().setLabelCount(Math.min(labels.size(), 6));

        chart.animateX(500);
        chart.invalidate();
    }

    /**
     * Lấy timestamp 7 ngày trước (đầu ngày)
     */
    public static long getWeekStartTimestamp() {
        return DateUtils.getStartOfDay(DateUtils.getDaysAgo(6));
    }

    /**
     * Lấy timestamp hôm nay (cuối ngày)
     */
    public static long getWeekEndTimestamp() {
        return DateUtils.getEndOfToday();
    }

    /**
     * Calculate date range for given period.
     * @param days Number of days (1, 7, or 30)
     * @return long[] with [startDate, endDate]
     */
    public static long[] getDateRangeForPeriod(int days) {
        long endDate = DateUtils.getEndOfToday();
        long startDate;
        if (days <= 1) {
            startDate = DateUtils.getStartOfToday();
        } else {
            startDate = DateUtils.getStartOfDay(DateUtils.getDaysAgo(days - 1));
        }
        return new long[]{startDate, endDate};
    }
}
