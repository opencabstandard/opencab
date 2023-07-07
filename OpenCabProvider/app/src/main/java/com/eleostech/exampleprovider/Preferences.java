package com.eleostech.exampleprovider;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.opencabstandard.provider.IdentityContract;

import java.util.ArrayList;

public class Preferences {
    private static final String LOG_TAG = Preferences.class.getCanonicalName();
    public static final String PREFS_NAME = "opencab";

    private static final String PREFS_USERNAME = "PREFS_USERNAME";

    private static final String PREFS_DUTY_STATUS = "PREFS_DUTY_STATUS";

    private static final String PREFS_ACTIVE_DRIVERS = "PREFS_ACTIVE_DRIVERS";
    private static final String PREFS_NAVIGATION_STATE = "PREFS_NAVIGATION_STATE";
    private static final String PREFS_IDENTITY_PROVIDER_SEND_JWT = "PREFS_IDENTITY_PROVIDER_SEND_JWT";
    private static final String PREFS_IDENTITY_PROVIDER_TOKEN = "PREFS_IDENTITY_PROVIDER_TOKEN";

    private static final String PREFS_HOS_VERSION = "PREFS_HOS_VERSION";

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getPreferencesEditor(Context context) {
        return getPreferences(context).edit();
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(Context context, String key, String defValue) {
        return getPreferences(context).getString(key, defValue);
    }

    public static void remove(Context context, String key) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.remove(key);
        editor.commit();
    }

    public static void clear(Context context) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.clear();
        editor.commit();
    }

    public static void setUsername(Context context, String username) {
        putString(context, PREFS_USERNAME, username);
    }

    public static String getUsername(Context context) {
        return getString(context, PREFS_USERNAME, null);
    }

    public static void setDutyStatus(Context context, String dutyStatus) {
        putString(context, PREFS_DUTY_STATUS, dutyStatus);
    }


    public static String getDutyStatus(Context context) {
        return getString(context, PREFS_DUTY_STATUS, null);
    }

    public static void setActiveDrivers(Context context, ArrayList<IdentityContract.Driver> activeDrivers) {
        String drivers = new Gson().toJson(activeDrivers);
        Log.d(LOG_TAG, "Saving drivers: " + drivers);
        putString(context, PREFS_ACTIVE_DRIVERS, drivers);
    }

    public static ArrayList<IdentityContract.Driver> getActiveDrivers(Context context) {
        String drivers = getString(context, PREFS_ACTIVE_DRIVERS, null);
        ArrayList<IdentityContract.Driver> activeDrivers = null;
        if (drivers != null) {
            java.lang.reflect.Type listType = new TypeToken<ArrayList<IdentityContract.Driver>>() {
            }.getType();
            activeDrivers = new Gson().fromJson(drivers, listType);
        }
        return activeDrivers;
    }

    public static void setNavigationState(Context context, boolean isNavigating) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.putBoolean(PREFS_NAVIGATION_STATE, isNavigating);
        editor.commit();
    }

    public static boolean getNavigationState(Context context) {
        return getPreferences(context).getBoolean(PREFS_NAVIGATION_STATE, false);
    }

    public static void setIdentityResponseJWT(Context context, boolean isJWT) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.putBoolean(PREFS_IDENTITY_PROVIDER_SEND_JWT, isJWT);
        editor.commit();
    }


    public static boolean getIdentityResponseAsJWT(Context context) {
        return getPreferences(context).getBoolean(PREFS_IDENTITY_PROVIDER_SEND_JWT, true);
    }

    public static void setIdentityResponseToken(Context context, String token) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.putString(PREFS_IDENTITY_PROVIDER_TOKEN, token);
        editor.commit();
    }

    public static String getIdentityResponseToken(Context context) {
        return getPreferences(context).getString(PREFS_IDENTITY_PROVIDER_TOKEN, "");
    }

    public static void setHosVersion(Context context, String version) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.putString(PREFS_HOS_VERSION, version);
        editor.commit();
    }

    public static String getHosVersion(Context context) {
        return getPreferences(context).getString(PREFS_HOS_VERSION, "");
    }
}
