package com.ustwo.clockwise.sample.configurablewatchface;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ustwo.clockwise.sample.config.WearableConfigListener;


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
                .getBoolean(ClockwiseSampleConfigurableWatchFace.PREF_CLOCKWISE_SAMPLE_LIGHT_THEME, false);
        themeCheckBox.setChecked(lightThemeActive);

        // Listen for toggles.
        themeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mListener.onConfigCompleted(ClockwiseSampleConfigurableWatchFace.PREF_CLOCKWISE_SAMPLE_LIGHT_THEME, isChecked, false);
            }
        });

    }
}
