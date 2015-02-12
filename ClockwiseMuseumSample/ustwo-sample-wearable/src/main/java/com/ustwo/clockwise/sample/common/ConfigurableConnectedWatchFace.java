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
     * @param sharedPreferences
     * @param key
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
