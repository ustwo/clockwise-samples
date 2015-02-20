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

import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.ustwo.clockwise.sample.museum.R;


/**
 * Preference UI for the configurable watch face for selecting a theme (light/dark).
 * We're using a custom preference because we're displaying a preview image
 */
public class ClockwiseSampleWatchFacePreference extends Preference {

    private static final String TAG = ClockwiseSampleWatchFacePreference.class.getSimpleName();

    private ImageView mPreviewView;
    private boolean mLightThemeActive;

    public ClockwiseSampleWatchFacePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.configurable_watchface_theme_preference_layout);

        ((Activity) context).setTitle(R.string.sample_config_title);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        mLightThemeActive = getPersistedBoolean(false);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        mPreviewView = (ImageView) view.findViewById(R.id.museum_config_imageview_preview);
        Switch lightThemeSwitch = (Switch) view.findViewById(R.id.museum_config_switch);
        lightThemeSwitch.setChecked(mLightThemeActive);
        updateView();

        lightThemeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mLightThemeActive = isChecked;
                persistBoolean(mLightThemeActive);
                updateView();
            }
        });
    }

    private void updateView() {
        mPreviewView.setImageResource(mLightThemeActive ? R.drawable.sample_round_light : R.drawable.sample_round_dark);
    }
}