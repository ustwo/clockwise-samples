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
package com.ustwo.clockwise.sample.common.util;

import android.content.SharedPreferences;

import java.util.Map;

/**
 * Utils for working with SharedPreferences.
 *
 * @author mark@ustwo.com
 */
public class SharedPreferencesUtil {

    /**
     * Path endpoint used when a config change comes from the companion.
     * This is required to allow our onDataChanged methods to distinguish the origin of the config change.
     */
    public static final String DATA_PATH_CONFIG_UPDATE_COMPANION = "/config_update/companion";

    /**
     * Path endpoint used when a config change comes from the wearable.
     * This is required to allow our onDataChanged methods to distinguish the origin of the config change.
     */
    public static final String DATA_PATH_CONFIG_UPDATE_WEARABLE = "/config_update/wearable";

    /**
     * Path endpoint prefix for data requests by the wearable. Append identifiers to distinguish requests.
     * E.g. /data_update_request/weather
     */
    public static final String DATA_PATH_DATA_UPDATE_REQUEST = "/data_update_request/";

    /**
     * DataMap key for the preferences
     */
    public static final String DATA_KEY_CONFIG_PREFS = "prefs";

    /**
     * DataMap key for the unique timestamp
     */
    public static final String DATA_KEY_CONFIG_TIMESTAMP = "timestamp";


    /**
     * Gets a value mapped to the key from SharedPreferences as an object.
     * @param prefs
     * @param key
     * @return the value as an object or null if none could be found.
     */
    public static Object getObject(SharedPreferences prefs, String key) {
        Object value = null;

        for (Map.Entry<String, ?> entry : prefs.getAll().entrySet()) {
            if (entry.getKey().equals(key)) {
                value = entry.getValue();
                break;
            }
        }

        return value;
    }

    /**
     * Put an object into SharedPreferences based on the object type. Note, you must call commit/apply on the editor
     * manually.
     * <p/>
     * Currently supports String, int and boolean.
     *
     * @param editor
     * @param key
     * @param value
     * @throws IllegalArgumentException if the object type is not supported by this method.
     */
    public static void putObject(SharedPreferences.Editor editor, String key, Object value) {
        if (value instanceof Integer) {
            int intValue = ((Integer) value).intValue();
            editor.putInt(key, intValue);
        } else if (value instanceof  String) {
            String stringValue = value.toString();
            editor.putString(key, stringValue);
        } else if (value instanceof  Boolean) {
            boolean booleanValue = ((Boolean) value).booleanValue();
            editor.putBoolean(key, booleanValue);
        } else {
            throw new IllegalArgumentException(
                    "Preference " + value + " of type " + value.getClass() + " is not supported");
        }
    }
}
