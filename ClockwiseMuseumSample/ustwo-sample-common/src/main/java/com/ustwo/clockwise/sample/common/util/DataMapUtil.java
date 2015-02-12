package com.ustwo.clockwise.sample.common.util;

import com.google.android.gms.wearable.DataMap;

/**
 * Utils for working with DataMaps.
 *
 * @author mark@ustwo.com
 */
public class DataMapUtil {

    /**
     * Put an object into a DataMap based on the object type.
     * <p/>
     * Currently supports String, int and boolean.
     *
     * @param dataMap
     * @param key
     * @param value
     * @throws IllegalArgumentException if the object type is not supported by this method.
     */
    public static void putObject(DataMap dataMap, String key, Object value) {
        if (value instanceof Integer) {
            int intValue = ((Integer) value).intValue();
            dataMap.putInt(key, intValue);
        } else if (value instanceof  String) {
            String stringValue = value.toString();
            dataMap.putString(key, stringValue);
        } else if (value instanceof  Boolean) {
            boolean booleanValue = ((Boolean) value).booleanValue();
            dataMap.putBoolean(key, booleanValue);
        } else {
            throw new IllegalArgumentException(
                    "Preference " + value + " of type " + value.getClass() + " is not supported");
        }
    }
}
