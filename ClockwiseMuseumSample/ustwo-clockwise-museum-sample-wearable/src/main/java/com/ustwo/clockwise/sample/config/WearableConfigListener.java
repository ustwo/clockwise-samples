package com.ustwo.clockwise.sample.config;

/**
 * Listener for configuration changes made by a config View.
 * Usually used to notify the parent activity/context of setting changes.
 */
public interface  WearableConfigListener {

    public void onConfigCompleted(String key, Object value, boolean finish);
}
