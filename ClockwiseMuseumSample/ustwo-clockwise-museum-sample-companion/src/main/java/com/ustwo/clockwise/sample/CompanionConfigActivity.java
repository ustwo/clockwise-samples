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
package com.ustwo.clockwise.sample;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.ustwo.clockwise.sample.common.WearableAPIHelper;
import com.ustwo.clockwise.sample.common.util.DataMapUtil;
import com.ustwo.clockwise.sample.common.util.SharedPreferencesUtil;

import java.util.Map;

/**
 * Activity which handles configuration requests and updates the companion preferences
 * ({@link #PREFS_COMPANION_CONFIG})
 * <p/>
 * In this example, we load a preference screen for a single watch face.
 * To provide configuration options for multiple watch faces, you could create a PreferenceScreen xml
 * file (in res/xml) for each watch face, and load the appropriate preference screen.
 * The manifest of the wearable app and this app then provide the necessary fields for Android Wear to link together
 * this Activity to the watch face when the user selects "Settings" for the watch face.
 */
public class CompanionConfigActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = CompanionConfigActivity.class.getSimpleName();

    /**
     * Copy of the shared preferences set on the companion. These must only be altered by Preference classes.
     * Any changes made to these will be sent over to the wearable. When this Activity is created we synchronize with
     * the wearable preferences to ensure the latest preferences are shown in the companion.
     */
    private static final String PREFS_COMPANION_CONFIG = "companion_config";
    private static final String COMPONENT_NAME = "android.support.wearable.watchface.extra.WATCH_FACE_COMPONENT";
    private static final String CONFIGURABLE_WATCHFACE_PREFERENCE_SCREEN = "configurable_watchface_preference_screen";

    private WearableAPIHelper mWearableAPIHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We must synchronise the wearable and companion preferences before the preference fragment is created.
        synchronizeWearablePreferences();

        if (!initPreferenceFragment()) {
            finish();
            return;
        }

        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
        }

        initialiseWearableAPI();
        getSharedPreferences(PREFS_COMPANION_CONFIG, MODE_PRIVATE).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        getSharedPreferences(PREFS_COMPANION_CONFIG, MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    private boolean initPreferenceFragment() {
        boolean initialised = false;

        ComponentName componentName = getIntent().getParcelableExtra(COMPONENT_NAME);

        // Note that we can also determine the watch face for which the preference screen is invoked
        // by examining the intent -- we can then load a specific preference screen.
        String resourceName = CONFIGURABLE_WATCHFACE_PREFERENCE_SCREEN;

        // The preference ID of the preference screen (in res/xml) we'd like to load
        final int prefId = getResources().getIdentifier(resourceName, "xml", componentName.getPackageName());

        if (prefId > 0) {
            PreferenceFragment fragment = new PreferenceFragment() {
                @Override
                public void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);

                    PreferenceManager preferenceManager = getPreferenceManager();
                    preferenceManager.setSharedPreferencesName(PREFS_COMPANION_CONFIG);
                    preferenceManager.setSharedPreferencesMode(MODE_PRIVATE);

                    addPreferencesFromResource(prefId);
                }
            };
            getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();

            initialised = true;
        } else {
            Log.e(TAG, "Could not find resource id for: " + resourceName);
        }

        return initialised;
    }

    private void initialiseWearableAPI() {
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


    /**
     * Copy over all the wearable shared preferences to the companion shared preferences.
     * <p/>
     * Note - this OnSharedPreferenceChangeListener must not be listening to changes to {@link #PREFS_COMPANION_CONFIG}
     * while we synchronise otherwise all the changes will be broadcast to the wearable. So, it is called before subscribing.
     */
    private void synchronizeWearablePreferences() {
        SharedPreferences wearablePrefs = getSharedPreferences(WearableConfigListenerService.PREFS_WEARABLE_CONFIG, MODE_PRIVATE);
        SharedPreferences.Editor companionPrefsEditor = getSharedPreferences(PREFS_COMPANION_CONFIG, MODE_PRIVATE).edit();

        for (Map.Entry<String, ?> entry : wearablePrefs.getAll().entrySet()) {
            SharedPreferencesUtil.putObject(companionPrefsEditor, entry.getKey(), entry.getValue());
        }

        companionPrefsEditor.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the user updates a preference in one of the configuration screens.
     *
     * @param companionPrefs    SharedPreferences for watch face on companion device
     * @param key               Key for updated shared preference
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences companionPrefs, String key) {
        // Retrieve the value of the SharedPreference that has changed.
        Object value = SharedPreferencesUtil.getObject(companionPrefs, key);

        if (value != null) {
            // Store the changed value in the local wearable prefs. It is already stored in the companion prefs.
            SharedPreferences.Editor wearablePrefsEditor = getSharedPreferences(
                    WearableConfigListenerService.PREFS_WEARABLE_CONFIG, MODE_PRIVATE).edit();
            SharedPreferencesUtil.putObject(wearablePrefsEditor, key, value);
            wearablePrefsEditor.commit();

            // Send the changed preference to the wearable. The path indicates the source of the change.
            DataMap prefsDataMap = new DataMap();
            DataMapUtil.putObject(prefsDataMap, key, value);

            // We have to make the data map unique to ensure Wear API sends it to the wearable. This is required because
            // it is valid for the companion app to send the same config change multiple times if the wearable was
            // alternately changing the config value to something else.
            DataMap dataMap = new DataMap();
            dataMap.putDataMap(SharedPreferencesUtil.DATA_KEY_CONFIG_PREFS, prefsDataMap);
            dataMap.putLong(SharedPreferencesUtil.DATA_KEY_CONFIG_TIMESTAMP, System.currentTimeMillis());

            mWearableAPIHelper.putDataMap(SharedPreferencesUtil.DATA_PATH_CONFIG_UPDATE_COMPANION, dataMap, null);
        }
    }
}
