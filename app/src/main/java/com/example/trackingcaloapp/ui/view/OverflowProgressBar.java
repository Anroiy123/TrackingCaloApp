package com.example.trackingcaloapp.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.trackingcaloapp.R;

/**
 * Custom View hiển thị thanh tiến độ calo với khả năng hiển thị overflow.
 * Khi progress > 100%, thanh sẽ hiển thị 2 segments:
 * - Normal segment (green): phần từ 0-100%
 * - Overflow segment (red): phần vượt quá 100%
 * 
 * Visual cap tại 200% để tránh overflow segment quá lớn.
 */
public class OverflowProgressBar extends View {

    // Default colors
    private static final int DEFAULT_NORMAL_COLOR = 0xFF10B981;    // Green
    private static final int DEFAULT_OVERFLOW_COLOR = 0xFFEF4444;  // Red
    private static final int DEFAULT_BACKGROUND_COLOR = 0xFFF3F4F6; // Light gray
    private static final int DEFAULT_MARKER_COLOR = 0xFF111827;    // Dark gray
    private static final float DEFAULT_CORNER_RADIUS = 9999f;      // Full rounded

    // Progress value (0-200+)
    private float progress = 0f;

    // Colors
    @ColorInt private int normalColor;
    @ColorInt private int overflowColor;
    @ColorInt private int backgroundColor;
    @ColorInt private int markerColor;

    // Corner radius
    private float cornerRadius;

    // Paint objects
    private Paint backgroundPaint;
    private Paint normalPaint;
    private Paint overflowPaint;
    private Paint markerPaint;

    // RectF objects for drawing
    private RectF backgroundRect;
    private RectF normalRect;
    private RectF overflowRect;

    // Marker width
    private static final float MARKER_WIDTH = 2f; // 2dp

    public OverflowProgressBar(Context context) {
        super(context);
        init(context, null);
    }

    public OverflowProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public OverflowProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        // Set default colors
        normalColor = DEFAULT_NORMAL_COLOR;
        overflowColor = DEFAULT_OVERFLOW_COLOR;
        backgroundColor = DEFAULT_BACKGROUND_COLOR;
        markerColor = DEFAULT_MARKER_COLOR;
        cornerRadius = DEFAULT_CORNER_RADIUS;

        // Parse custom attributes from XML
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.OverflowProgressBar);
            try {
                progress = typedArray.getFloat(R.styleable.OverflowProgressBar_opb_progress, 0f);
                normalColor = typedArray.getColor(R.styleable.OverflowProgressBar_opb_normalColor, 
                        ContextCompat.getColor(context, R.color.progress_normal));
                overflowColor = typedArray.getColor(R.styleable.OverflowProgressBar_opb_overflowColor, 
                        ContextCompat.getColor(context, R.color.progress_over));
                backgroundColor = typedArray.getColor(R.styleable.OverflowProgressBar_opb_backgroundColor, 
                        ContextCompat.getColor(context, R.color.progress_track));
                markerColor = typedArray.getColor(R.styleable.OverflowProgressBar_opb_markerColor, 
                        ContextCompat.getColor(context, R.color.text_primary));
                cornerRadius = typedArray.getDimension(R.styleable.OverflowProgressBar_opb_cornerRadius, 
                        context.getResources().getDimension(R.dimen.radius_full));
            } finally {
                typedArray.recycle();
            }
        }

        // Initialize Paint objects
        initPaints();

        // Initialize RectF objects
        backgroundRect = new RectF();
        normalRect = new RectF();
        overflowRect = new RectF();
    }

    private void initPaints() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(backgroundColor);

        normalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        normalPaint.setStyle(Paint.Style.FILL);
        normalPaint.setColor(normalColor);

        overflowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        overflowPaint.setStyle(Paint.Style.FILL);
        overflowPaint.setColor(overflowColor);

        markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markerPaint.setStyle(Paint.Style.FILL);
        markerPaint.setColor(markerColor);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float barWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        float barHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        // Skip drawing if dimensions are invalid
        if (barWidth <= 0 || barHeight <= 0) {
            return;
        }

        float left = getPaddingLeft();
        float top = getPaddingTop();

        // Draw background track
        backgroundRect.set(left, top, left + barWidth, top + barHeight);
        canvas.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, backgroundPaint);

        // Skip progress drawing if progress <= 0
        if (progress <= 0) {
            return;
        }

        if (progress <= 100) {
            // Chỉ vẽ normal segment
            float normalWidth = (progress / 100f) * barWidth;
            normalRect.set(left, top, left + normalWidth, top + barHeight);
            canvas.drawRoundRect(normalRect, cornerRadius, cornerRadius, normalPaint);
        } else {
            // Vẽ cả normal và overflow segments
            // Cap tại 200% để tránh overflow quá lớn
            float cappedProgress = Math.min(progress, 200f);

            // Tính tỷ lệ thực tế
            float normalRatio = 100f / cappedProgress;
            float overflowRatio = (cappedProgress - 100f) / cappedProgress;

            float normalWidth = normalRatio * barWidth;
            float overflowWidth = overflowRatio * barWidth;

            // Draw normal segment (green) - left side with rounded left corners
            normalRect.set(left, top, left + normalWidth, top + barHeight);
            canvas.drawRoundRect(normalRect, cornerRadius, cornerRadius, normalPaint);

            // Draw overflow segment (red) - right side with rounded right corners
            overflowRect.set(left + normalWidth, top, left + normalWidth + overflowWidth, top + barHeight);
            canvas.drawRoundRect(overflowRect, cornerRadius, cornerRadius, overflowPaint);
        }
    }

    // ==================== Public Methods ====================

    /**
     * Set progress value (0-200+, không cap)
     * @param progress Progress percentage (e.g., 50 for 50%, 150 for 150%)
     */
    public void setProgress(float progress) {
        // Ensure progress is not negative
        this.progress = Math.max(0, progress);
        invalidate();
    }

    /**
     * Get current progress value
     * @return Current progress percentage
     */
    public float getProgress() {
        return progress;
    }

    /**
     * Set color for normal segment (0-100%)
     * @param color Color int
     */
    public void setNormalColor(@ColorInt int color) {
        this.normalColor = color;
        normalPaint.setColor(color);
        invalidate();
    }

    /**
     * Set color for overflow segment (>100%)
     * @param color Color int
     */
    public void setOverflowColor(@ColorInt int color) {
        this.overflowColor = color;
        overflowPaint.setColor(color);
        invalidate();
    }

    /**
     * Set background track color
     * @param color Color int
     */
    @Override
    public void setBackgroundColor(@ColorInt int color) {
        this.backgroundColor = color;
        backgroundPaint.setColor(color);
        invalidate();
    }

    /**
     * Set goal marker color
     * @param color Color int
     */
    public void setMarkerColor(@ColorInt int color) {
        this.markerColor = color;
        markerPaint.setColor(color);
        invalidate();
    }

    /**
     * Set corner radius
     * @param radius Corner radius in pixels
     */
    public void setCornerRadius(float radius) {
        this.cornerRadius = radius;
        invalidate();
    }

    /**
     * Check if progress is in overflow state (>100%)
     * @return true if progress > 100
     */
    public boolean isOverflow() {
        return progress > 100;
    }
}
