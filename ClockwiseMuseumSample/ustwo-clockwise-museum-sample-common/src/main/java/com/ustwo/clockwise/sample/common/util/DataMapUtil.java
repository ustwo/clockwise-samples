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
