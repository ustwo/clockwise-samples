package com.ustwo.clockwise.sample;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;
import com.ustwo.clockwise.sample.common.util.SharedPreferencesUtil;

import java.util.List;

/**
 * Handles configuration changed events from the wearable and updates the local SharedPreferences.
 */
public class WearableConfigListenerService extends WearableListenerService  {

    /**
     * Copy of the shared preferences set on the wearable. This represents the latest preferences set.
     * These preferences are updated when a change is made on the wearable.
     */
    protected static final String PREFS_WEARABLE_CONFIG = "wearable_config";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        String supportedPath = SharedPreferencesUtil.DATA_PATH_CONFIG_UPDATE_WEARABLE;

        for (DataEvent event : events) {
            String path = event.getDataItem().getUri().getPath();
            if (supportedPath.equals(path)) {
                DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                updateSharedPreferences(dataMap);
            }
        }
    }

    /**
     * Update the phone's copy of the wearable's shared preferences. These will be synced to companion preference set by
     * the {@link com.ustwo.clockwise.sample.CompanionConfigActivity} when it is created.
     *
     * @param dataMap
     */
    private void updateSharedPreferences(DataMap dataMap) {
        DataMap prefsDataMap = dataMap.getDataMap(SharedPreferencesUtil.DATA_KEY_CONFIG_PREFS);
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_WEARABLE_CONFIG, Context.MODE_PRIVATE).edit();

        for (String key : prefsDataMap.keySet()) {
            SharedPreferencesUtil.putObject(editor, key, prefsDataMap.get(key));
        }

        editor.commit();
    }
}
