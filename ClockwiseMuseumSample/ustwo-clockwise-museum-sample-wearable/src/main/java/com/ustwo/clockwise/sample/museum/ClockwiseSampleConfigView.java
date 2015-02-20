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

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ustwo.clockwise.sample.config.WearableConfigListener;
import com.ustwo.clockwise.sample.museum.R;


/**
 * Wearable configuration for the configurable sample watch face.
 */
public class ClockwiseSampleConfigView extends LinearLayout {

    private WearableConfigListener mListener;

    public ClockwiseSampleConfigView(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            mListener = (WearableConfigListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement WatchFaceConfigListener");
        }
    }

    @Override
    protected void onFinishInflate() {
        Context context = getContext();

        ((TextView) findViewById(R.id.sample_config_textview_title)).setText(getContext().getString(R.string.clockwise_sample_config_title));

        CheckBox themeCheckBox = (CheckBox) findViewById(R.id.sample_config_checkbox);
        TextView themeTextView = (TextView)findViewById(R.id.sample_config_checkboxname);
        themeTextView.setText(context.getString(R.string.clockwise_sample_toggle_name));

        // Set the current state.
        boolean lightThemeActive = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(ClockwiseSampleMuseumWatchFace.PREF_CLOCKWISE_SAMPLE_LIGHT_THEME, false);
        themeCheckBox.setChecked(lightThemeActive);

        // Listen for toggles.
        themeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mListener.onConfigCompleted(ClockwiseSampleMuseumWatchFace.PREF_CLOCKWISE_SAMPLE_LIGHT_THEME, isChecked, false);
            }
        });

    }
}
