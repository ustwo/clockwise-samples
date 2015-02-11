package com.ustwo.clockwise.sample.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;
import com.ustwo.clockwise.sample.common.util.SharedPreferencesUtil;

import java.util.List;

/**
 * Handles configuration changed events from the companion and updates the local SharedPreferences.
 */
public class CompanionConfigListenerService extends WearableListenerService {

    private void updateSharedPreferences(DataMap dataMap) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        DataMap prefsDataMap = dataMap.getDataMap(SharedPreferencesUtil.DATA_KEY_CONFIG_PREFS);

        for (String key : prefsDataMap.keySet()) {
            SharedPreferencesUtil.putObject(editor, key, prefsDataMap.get(key));
        }

        editor.commit();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        String supportedPath = SharedPreferencesUtil.DATA_PATH_CONFIG_UPDATE_COMPANION;

        for (DataEvent event : events) {
            String path = event.getDataItem().getUri().getPath();

            if (supportedPath.equals(path)) {
                updateSharedPreferences(DataMapItem.fromDataItem(event.getDataItem()).getDataMap());
            }
        }
    }
}
