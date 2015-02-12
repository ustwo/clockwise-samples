package com.ustwo.clockwise.sample.config;

import android.app.Activity;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.ustwo.clockwise.sample.common.WearableAPIHelper;
import com.ustwo.clockwise.sample.common.util.DataMapUtil;
import com.ustwo.clockwise.sample.common.util.SharedPreferencesUtil;

/**
 * Activity which handles config requests on the wearable.
 */
public class WearableConfigActivity extends Activity implements WearableConfigListener {

    private static final String TAG = WearableConfigActivity.class.getSimpleName();

    private WearableAPIHelper mWearableAPIHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note that it is possible to load different resources by examining or using the
        // ComponentName -- it is the name of the watch face Service the user has requested to configure.
        ComponentName componentName = getIntent().getParcelableExtra(
                "android.support.wearable.watchface.extra.WATCH_FACE_COMPONENT");

        String resourceName = "clockwise_sample_config";
        final int layoutId = getResources().getIdentifier(resourceName, "layout", componentName.getPackageName());
        if (layoutId <= 0) {
            Log.e(TAG, "Could not find resource id for: " + resourceName);
            finish();
            return;
        }

        setContentView(layoutId);

        mWearableAPIHelper = new WearableAPIHelper(this, new WearableAPIHelper.WearableAPIHelperListener() {
            @Override
            public void onWearableAPIConnected(GoogleApiClient apiClient) {
            }

            @Override
            public void onWearableAPIConnectionSuspended(int cause) {
            }

            @Override
            public void onWearableAPIConnectionFailed(ConnectionResult result) {
            }
        });
    }


    @Override
    public void onConfigCompleted(String key, Object value, boolean finish) {
        // Store the value locally.
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        SharedPreferencesUtil.putObject(editor, key, value);
        editor.commit();

        // Send the changed preference to the companion. The path indicates the source of the change.
        DataMap prefsDataMap = new DataMap();
        DataMapUtil.putObject(prefsDataMap, key, value);

        // We have to make the data map unique to ensure Wear API sends it to the wearable. This is required because
        // it is valid for the companion app to send the same config change multiple times if the wearable was
        // alternately changing the config value to something else.
        DataMap dataMap = new DataMap();
        dataMap.putDataMap(SharedPreferencesUtil.DATA_KEY_CONFIG_PREFS, prefsDataMap);
        dataMap.putLong(SharedPreferencesUtil.DATA_KEY_CONFIG_TIMESTAMP, System.currentTimeMillis());

        mWearableAPIHelper.putDataMap(SharedPreferencesUtil.DATA_PATH_CONFIG_UPDATE_WEARABLE, dataMap, null);

        if (finish) {
            finish();
        }
    }
}
