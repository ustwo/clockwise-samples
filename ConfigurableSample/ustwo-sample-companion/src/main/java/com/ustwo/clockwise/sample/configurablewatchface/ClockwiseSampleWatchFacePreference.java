package com.ustwo.clockwise.sample.configurablewatchface;

import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;


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