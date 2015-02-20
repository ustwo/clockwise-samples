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
            if(event.getDataItem() != null && event.getDataItem().getUri() != null) {
                String path = event.getDataItem().getUri().getPath();
                if (supportedPath.equals(path)) {
                    updateSharedPreferences(DataMapItem.fromDataItem(event.getDataItem()).getDataMap());
                }
            }
        }
    }
}
