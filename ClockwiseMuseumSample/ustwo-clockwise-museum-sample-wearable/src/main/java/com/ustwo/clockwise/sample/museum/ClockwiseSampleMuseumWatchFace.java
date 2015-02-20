/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 ustwo studio inc (www.ustwo.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.ustwo.clockwise.sample.museum;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.WindowInsets;

import com.ustwo.clockwise.WatchFaceTime;
import com.ustwo.clockwise.WatchMode;
import com.ustwo.clockwise.WatchShape;
import com.ustwo.clockwise.sample.common.ConfigurableConnectedWatchFace;
import com.ustwo.clockwise.sample.museum.R;
import com.ustwo.clockwise.util.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Configurable sample watch face for wearable devices
 *
 * @author ustwo
 */
public class ClockwiseSampleMuseumWatchFace extends ConfigurableConnectedWatchFace {

    /**
     * Container class for design specifications
     */
    public static class Spec {
        /**
         * The design display size, in reference to which specs are created
         */
        public static final float SPEC_SIZE = 320.0f;

        /**
         * The diameter of the center circle
         */
        public static final float SPEC_mCircleDiameter = 200.0f;

        // Hand specs
        /**
         * Distance from the center of the hands to the display center (radius at which hands travel)
         */
        public static final float SPEC_mHandDotDistanceFromCenter = 130.0f;

        /**
         * Diameter of the hand circles
         */
        public static final float SPEC_mHandDotDiameter = 20.0f;

        /**
         * Stroke thickness of the minute hand dot
         */
        public static final float SPEC_mMinuteHandDotStroke = 2.0f;

        /**
         * Stroke thickness of the minute hand
         */
        public static final float SPEC_mSecondHandStroke = 2.0f;

        // Center circle elements specs
        /**
         * Position of the time text, in relation to the top-left corner of the inner circle
         */
        public static final PointF SPEC_mTimeTextPositionInCircle = new PointF(100f, 64f);

        /**
         * Position of the date text, in relation to the top-left corner of the inner circle
         */
        public static final PointF SPEC_mDateTextPositionInCircle = new PointF(100f, 140f);

        /**
         * Font size of the time text
         */
        public static final float SPEC_mTimeTextSize = 36.0f;

        /**
         * Font size of the date text
         */
        public static final float SPEC_mDateTextSize = 18.0f;

        public static final int SPEC_COLOR_DARK_MINUTES = 0xFFFFFFFF;
        public static final int SPEC_COLOR_DARK_HOURS = 0xFFFFFFFF;
        public static final int SPEC_COLOR_DARK_SECONDS = 0xFFe51c23;
        public static final int SPEC_COLOR_DARK_BACKGROUND = 0xFF212121;
        public static final int SPEC_COLOR_DARK_TIME_TEXT = 0xFFFFFFFF;
        public static final int SPEC_COLOR_DARK_DATE_TEXT = 0x42FFFFFF;  // 26% alpha

        public static final int SPEC_COLOR_LIGHT_MINUTES = 0xFF212121;
        public static final int SPEC_COLOR_LIGHT_HOURS = 0xFF212121;
        public static final int SPEC_COLOR_LIGHT_SECONDS = 0xFFe51c23;
        public static final int SPEC_COLOR_LIGHT_BACKGROUND = 0xFFfafafa;
        public static final int SPEC_COLOR_LIGHT_TIME_TEXT = 0xFF212121;
        public static final int SPEC_COLOR_LIGHT_DATE_TEXT = 0x8A000000;  // 54% alpha

        public static final int SPEC_COLOR_LOWBIT_BACKGROUND = 0xFF000000;
        public static final int SPEC_COLOR_LOWBIT_FOREGROUND = 0xFFFFFFFF;
    }

    /**
     * Theme preference name
     */
    public static final String PREF_CLOCKWISE_SAMPLE_LIGHT_THEME = "pref_clockwise_sample_light_theme";

    /**
     * Whether the currently selected theme is light
     */
    private boolean mIsCurrentThemeLight = false;

    // Bitmaps for center circles
    private Bitmap mBackgroundBitmapDark;
    private Bitmap mBackgroundBitmapLight;
    private Bitmap mBackgroundBitmapLowbit;

    /**
     * The bitmap which will be drawn on next draw cycle
     */
    private Bitmap mCurrentBackgroundBitmap;

    /**
     * The background color which will be drawn on the next draw cycle
     */
    private int mCurrentBackgroundColor = Spec.SPEC_COLOR_DARK_BACKGROUND;

    /**
     * Paint used for all bitmaps
     */
    private Paint mBitmapPaint = new Paint();

    private Paint mTimeTextPaint = new Paint();
    private Paint mDateTextPaint = new Paint();

    private Paint mMinuteHandDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mHourHandDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSecondHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * Position of the center of the watch face, in pixels
     */
    private PointF mWatchFaceCenter = new PointF(0f, 0f);

    /**
     * Current time text (will be drawn on next draw cycle)
     */
    private String mTimeText = "00:00";

    /**
     * Current date text (will be drawn on next draw cycle)
     */
    private String mDateText = "";

    /**
     * Current date used to to display text.
     */
    private Date mDate = new Date();

    /**
     * Date format to use to display current date.
     */
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("MMM dd");

    /**
     * Format 12 hour time (e.g. 3:45)
     */
    private SimpleDateFormat mTimeFormat12 = new SimpleDateFormat("h:mm");

    /**
     * Format 24 hour time (e.g. 15:45)
     */
    private SimpleDateFormat mTimeFormat24 = new SimpleDateFormat("HH:mm");

    // Specs scaled to current device dimensions
    private float mCircleDiameter;

    private float mHandDotDistanceFromCenter;
    private float mHandDotDiameter;

    private PointF mTimeTextPositionInCircle = new PointF(0f, 0f);
    private PointF mDateTextPositionInCircle = new PointF(0f, 0f);

    private float mMinuteHandDotStroke;
    private float mSecondHandStroke;

    /**
     * Maximum radius of the second hand
     */
    private float mMaxSecondHandRadius = 0.0f;

    // Degrees at which hands will be drawn on the next draw cycle
    private float mCurrentDegreesHour = 0.0f;
    private float mCurrentDegreesMinute = 0.0f;
    private float mCurrentDegreesSecond = 0.0f;

    /**
     * Scales a float dimension from a spec value to the specified screen size
     * @param specValue The spec value to scale
     * @param currentRenderSize The screen size
     * @return The scaled dimension
     */
    public static float getFloatValueFromSpec(float specValue, float currentRenderSize) {
        return (specValue / Spec.SPEC_SIZE) * currentRenderSize;
    }

    /**
     * Scales a point value from a spec value to the specified screen size, and returns result in output
     * @param output The point which will be set to the output of the operation
     * @param specValue The spec point to be scaled
     * @param currentRenderSize The screen size
     */
    public static void applyPointValueFromSpec(PointF output, PointF specValue, float currentRenderSize) {
        if(output == null || specValue == null) {
            return;
        }
        output.set(getFloatValueFromSpec(specValue.x, currentRenderSize), getFloatValueFromSpec(specValue.y, currentRenderSize));
    }

    /**
     * Sets the current theme
     * @param isCurrentThemeLight If true, the theme will be light. Otherwise, dark.
     */
    private void setCurrentThemeLight(boolean isCurrentThemeLight) {
        if(mIsCurrentThemeLight == isCurrentThemeLight) {
            return;
        }

        mIsCurrentThemeLight = isCurrentThemeLight;
        refreshCurrentState();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        updateConfiguration(PreferenceManager.getDefaultSharedPreferences(this));

        mTimeTextPaint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        mTimeTextPaint.setTextAlign(Paint.Align.CENTER);

        mDateTextPaint.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        mDateTextPaint.setTextAlign(Paint.Align.CENTER);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setFilterBitmap(true);
        mBitmapPaint.setDither(true);
    }

    @Override
    protected void onLayout(WatchShape watchShape, Rect rect, WindowInsets windowInsets) {
        // Convert spec dimensions to current screen size
        float renderSize = Math.min(getWidth(), getHeight());

        mWatchFaceCenter.set(getWidth() * 0.5f, getHeight() * 0.5f);

        mCircleDiameter = getFloatValueFromSpec(Spec.SPEC_mCircleDiameter, renderSize);

        mHandDotDistanceFromCenter = getFloatValueFromSpec(Spec.SPEC_mHandDotDistanceFromCenter, renderSize);
        mHandDotDiameter = getFloatValueFromSpec(Spec.SPEC_mHandDotDiameter, renderSize);

        applyPointValueFromSpec(mTimeTextPositionInCircle, Spec.SPEC_mTimeTextPositionInCircle, renderSize);
        applyPointValueFromSpec(mDateTextPositionInCircle, Spec.SPEC_mDateTextPositionInCircle, renderSize);

        float timeTextSize = getFloatValueFromSpec(Spec.SPEC_mTimeTextSize, renderSize);
        float dateTextSize = getFloatValueFromSpec(Spec.SPEC_mDateTextSize, renderSize);

        mTimeTextPaint.setTextSize(timeTextSize);
        mDateTextPaint.setTextSize(dateTextSize);

        mMinuteHandDotStroke = getFloatValueFromSpec(Spec.SPEC_mMinuteHandDotStroke, renderSize);
        mSecondHandStroke = getFloatValueFromSpec(Spec.SPEC_mSecondHandStroke, renderSize);

        mMinuteHandDotPaint.setStyle(Paint.Style.STROKE);
        mMinuteHandDotPaint.setStrokeWidth(mMinuteHandDotStroke);

        mSecondHandPaint.setStyle(Paint.Style.STROKE);
        mSecondHandPaint.setStrokeWidth(mSecondHandStroke);

        mBackgroundBitmapLowbit = BitmapFactory.decodeResource(getResources(), R.drawable.sample_bg_1bit);
        mBackgroundBitmapLowbit = Bitmap.createScaledBitmap(mBackgroundBitmapLowbit, (int)mCircleDiameter, (int)mCircleDiameter, true);

        float halfWidth = getWidth() * 0.5f;
        float halfHeight = getHeight() * 0.5f;
        mMaxSecondHandRadius = (float)Math.sqrt(halfWidth * halfWidth + halfHeight * halfHeight);

        refreshCurrentState();

        WatchFaceTime time = getTime();
        updateDateAndTimeText(time);
        updateHandPositions(time);
    }

    @Override
    protected void onWatchFaceConfigChanged(SharedPreferences sharedPreferences, String key) {
        updateConfiguration(sharedPreferences);
    }

    private void updateConfiguration(SharedPreferences preferences) {
        boolean isLightTheme = preferences.getBoolean(PREF_CLOCKWISE_SAMPLE_LIGHT_THEME, false);
        setCurrentThemeLight(isLightTheme);
    }

    @Override
    protected WatchFaceStyle getWatchFaceStyle() {
        WatchFaceStyle.Builder builder =
                new WatchFaceStyle.Builder(this)
                        .setCardPeekMode(WatchFaceStyle.PEEK_MODE_VARIABLE)
                        .setAmbientPeekMode(WatchFaceStyle.AMBIENT_PEEK_MODE_VISIBLE)
                        .setPeekOpacityMode(WatchFaceStyle.PEEK_OPACITY_MODE_TRANSLUCENT)
                        .setCardProgressMode(WatchFaceStyle.PROGRESS_MODE_NONE)
                        .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                        .setViewProtection(WatchFaceStyle.PROTECT_HOTWORD_INDICATOR | WatchFaceStyle.PROTECT_STATUS_BAR)
                        .setHotwordIndicatorGravity(Gravity.TOP | Gravity.LEFT)
                        .setStatusBarGravity(Gravity.TOP | Gravity.LEFT)
                        .setShowSystemUiTime(false);
        return builder.build();
    }

    @Override
    protected long getInteractiveModeUpdateRate() {
        return DateUtils.SECOND_IN_MILLIS;
    }

    @Override
    public void onWatchModeChanged(WatchMode watchMode) {
        refreshCurrentState();
    }


    @Override
    protected void on24HourFormatChanged(boolean is24HourFormat) {
        // Handle 24-hour format setting changes (if using digital time display)

        mTimeText = is24HourFormat() ? mTimeFormat24.format(mDate) :
                mTimeFormat12.format(mDate);
    }

    /*
    // React to peek card position/size changes

    @Override
    protected void onCardPeek(Rect peekCardRect) {
        // get height of visible area (not including card)
        float visibleHeight = getHeight() - peekCardRect.height();
    }
    */

    @Override
    protected void onTimeChanged(WatchFaceTime oldTime, WatchFaceTime newTime) {
        if(newTime.hasTimeZoneChanged(oldTime)) {
            TimeZone timeZone = TimeZone.getTimeZone(newTime.timezone);
            mTimeFormat12.setTimeZone(timeZone);
            mTimeFormat24.setTimeZone(timeZone);
            mDateFormat.setTimeZone(timeZone);
        }

        if(newTime.hasMinuteChanged(oldTime) || newTime.hasHourChanged(oldTime) || newTime.hasDateChanged(oldTime)) {
            updateDateAndTimeText(newTime);
        }
        updateHandPositions(newTime);
    }

    private void updateHandPositions(WatchFaceTime timeStamp) {
        mCurrentDegreesHour = TimeUtil.getHourDegrees(timeStamp);
        mCurrentDegreesMinute = TimeUtil.getMinuteDegrees(timeStamp);
        mCurrentDegreesSecond = TimeUtil.getSecondDegrees(timeStamp);
    }

    private void updateDateAndTimeText(WatchFaceTime timeStamp) {
        mDate.setTime(timeStamp.toMillis(false));

        mDateText = mDateFormat.format(mDate);
        mTimeText = is24HourFormat() ? mTimeFormat24.format(mDate) :
                mTimeFormat12.format(mDate);
    }

    private void refreshCurrentState() {
        WatchMode currentWatchMode = getCurrentWatchMode();

        switch(currentWatchMode) {
            case INTERACTIVE:
                applyInteractiveState();
                break;
            case AMBIENT:
                // Non-low bit ambient mode
                applyAmbientState();
                break;
            default:
                // Other ambient modes (LOW_BIT, BURN_IN, LOW_BIT_BURN_IN)
                applyLowBitState();
                break;
        }
    }


    /**
     * Apply interactive-mode paint colors and background images
     */
    private void applyInteractiveState() {
        if((int)mCircleDiameter <= 0) {
            return;
        }

        if(mIsCurrentThemeLight) {
            if(mBackgroundBitmapLight == null || mBackgroundBitmapLight.getWidth() != (int)mCircleDiameter) {
                mBackgroundBitmapLight = BitmapFactory.decodeResource(getResources(), R.drawable.sample_bg_light);
                mBackgroundBitmapLight = Bitmap.createScaledBitmap(mBackgroundBitmapLight, (int) mCircleDiameter, (int) mCircleDiameter, true);
            }

            mCurrentBackgroundColor = Spec.SPEC_COLOR_LIGHT_BACKGROUND;
            mCurrentBackgroundBitmap = mBackgroundBitmapLight;
            mTimeTextPaint.setColor(Spec.SPEC_COLOR_LIGHT_TIME_TEXT);
            mDateTextPaint.setColor(Spec.SPEC_COLOR_LIGHT_DATE_TEXT);
            mSecondHandPaint.setColor(Spec.SPEC_COLOR_LIGHT_SECONDS);
            mMinuteHandDotPaint.setColor(Spec.SPEC_COLOR_LIGHT_MINUTES);
            mHourHandDotPaint.setColor(Spec.SPEC_COLOR_LIGHT_HOURS);
        }
        else {
            if(mBackgroundBitmapDark == null || mBackgroundBitmapDark.getWidth() != (int)mCircleDiameter) {
                mBackgroundBitmapDark = BitmapFactory.decodeResource(getResources(), R.drawable.sample_bg_dark);
                mBackgroundBitmapDark = Bitmap.createScaledBitmap(mBackgroundBitmapDark, (int) mCircleDiameter, (int) mCircleDiameter, true);
            }

            mCurrentBackgroundColor = Spec.SPEC_COLOR_DARK_BACKGROUND;
            mCurrentBackgroundBitmap = mBackgroundBitmapDark;
            mTimeTextPaint.setColor(Spec.SPEC_COLOR_DARK_TIME_TEXT);
            mDateTextPaint.setColor(Spec.SPEC_COLOR_DARK_DATE_TEXT);
            mSecondHandPaint.setColor(Spec.SPEC_COLOR_DARK_SECONDS);
            mMinuteHandDotPaint.setColor(Spec.SPEC_COLOR_DARK_MINUTES);
            mHourHandDotPaint.setColor(Spec.SPEC_COLOR_DARK_HOURS);
        }
    }

    /**
     * Apply low-bit ambient mode paint colors and background images
     */
    private void applyLowBitState() {
        mCurrentBackgroundColor = Spec.SPEC_COLOR_LOWBIT_BACKGROUND;
        mCurrentBackgroundBitmap = mBackgroundBitmapLowbit;
        mTimeTextPaint.setColor(Spec.SPEC_COLOR_LOWBIT_FOREGROUND);
        mDateTextPaint.setColor(Spec.SPEC_COLOR_LOWBIT_FOREGROUND);
        mMinuteHandDotPaint.setColor(Spec.SPEC_COLOR_LOWBIT_FOREGROUND);
        mHourHandDotPaint.setColor(Spec.SPEC_COLOR_LOWBIT_FOREGROUND);
    }

    /**
     * Apply ambient-mode (non-low bit) paint colors and background images.
     * This mode is similar to the dark interactive mode, but doesn't show the second hand.
     */
    private void applyAmbientState() {
        if((int)mCircleDiameter <= 0) {
            return;
        }

        if(mBackgroundBitmapDark == null || mBackgroundBitmapDark.getWidth() != (int)mCircleDiameter) {
            mBackgroundBitmapDark = BitmapFactory.decodeResource(getResources(), R.drawable.sample_bg_dark);
            mBackgroundBitmapDark = Bitmap.createScaledBitmap(mBackgroundBitmapDark, (int) mCircleDiameter, (int) mCircleDiameter, true);
        }

        mCurrentBackgroundColor = Spec.SPEC_COLOR_DARK_BACKGROUND;
        mCurrentBackgroundBitmap = mBackgroundBitmapDark;
        mTimeTextPaint.setColor(Spec.SPEC_COLOR_DARK_TIME_TEXT);
        mDateTextPaint.setColor(Spec.SPEC_COLOR_DARK_DATE_TEXT);
        mMinuteHandDotPaint.setColor(Spec.SPEC_COLOR_DARK_MINUTES);
        mHourHandDotPaint.setColor(Spec.SPEC_COLOR_DARK_HOURS);
    }

    @Override
    public void onDraw(Canvas canvas) {
        int backgroundBitmapPositionX = -mCurrentBackgroundBitmap.getWidth() / 2;
        int backgroundBitmapPositionY = -mCurrentBackgroundBitmap.getHeight() / 2;

        canvas.save();
        // To simplify drawing, we apply a translation to the canvas,
        // so all render operations can be in reference to the center of the face
        canvas.translate(mWatchFaceCenter.x, mWatchFaceCenter.y);
        canvas.drawColor(mCurrentBackgroundColor);
        canvas.drawBitmap(mCurrentBackgroundBitmap, backgroundBitmapPositionX, backgroundBitmapPositionY, mBitmapPaint);
        canvas.drawText(mTimeText, backgroundBitmapPositionX + mTimeTextPositionInCircle.x, backgroundBitmapPositionY + mTimeTextPositionInCircle.y, mTimeTextPaint);
        canvas.drawText(mDateText, backgroundBitmapPositionX + mDateTextPositionInCircle.x, backgroundBitmapPositionY + mDateTextPositionInCircle.y, mDateTextPaint);
        canvas.restore();

        // Draw the second hand in interactive mode only, because other modes don't
        // update/redraw each second.
        if(getCurrentWatchMode() == WatchMode.INTERACTIVE) {
            drawSecondHand(canvas);
        }

        drawMinuteHand(canvas);
        drawHourHand(canvas);
    }

    private void drawSecondHand(Canvas canvas) {
        canvas.save();
        canvas.rotate(mCurrentDegreesSecond, mWatchFaceCenter.x, mWatchFaceCenter.y);
        float origin = mCircleDiameter * 0.5f;
        canvas.drawLine(mWatchFaceCenter.x, mWatchFaceCenter.y - origin,
                mWatchFaceCenter.x, mWatchFaceCenter.y - mMaxSecondHandRadius, mSecondHandPaint);
        canvas.restore();
    }

    private void drawMinuteHand(Canvas canvas) {
        canvas.save();
        canvas.rotate(mCurrentDegreesMinute, mWatchFaceCenter.x, mWatchFaceCenter.y);
        canvas.drawCircle(mWatchFaceCenter.x, mWatchFaceCenter.y - mHandDotDistanceFromCenter,
                mHandDotDiameter * 0.5f, mMinuteHandDotPaint);
        canvas.restore();
    }

    private void drawHourHand(Canvas canvas) {
        canvas.save();
        canvas.rotate(mCurrentDegreesHour, mWatchFaceCenter.x, mWatchFaceCenter.y);
        canvas.drawCircle(mWatchFaceCenter.x, mWatchFaceCenter.y - mHandDotDistanceFromCenter,
                mHandDotDiameter * 0.5f, mHourHandDotPaint);
        canvas.restore();
    }

}
