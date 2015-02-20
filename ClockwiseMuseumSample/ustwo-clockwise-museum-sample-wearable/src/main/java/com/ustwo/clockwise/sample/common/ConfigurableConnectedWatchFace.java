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
package com.ustwo.clockwise.sample.common;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ustwo.clockwise.ConnectedWatchFace;

/**
 * Base class for a watch face that can be configured by a companion.
 */
public abstract class ConfigurableConnectedWatchFace extends ConnectedWatchFace {

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(
                mOnSharedPreferenceChangeListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(
                mOnSharedPreferenceChangeListener);
    }

    /**
     * Called when a change to the SharedPreferences has been observed.
     * Overriding classes should detect if the SharedPreference change is part of their configuration and update
     * accordingly. They should call {@link com.ustwo.clockwise.WatchFace#invalidate()} to redraw if required.
     *
     * @param sharedPreferences SharedPreferences for watch face on wearable
     * @param key               Key for updated shared preference
     */
    protected abstract void onWatchFaceConfigChanged(SharedPreferences sharedPreferences, String key);

    private final SharedPreferences.OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    onWatchFaceConfigChanged(sharedPreferences, key);
                }
            };
}
