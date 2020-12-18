package com.eleostech.exampleprovider;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.opencabstandard.provider.AbstractHOSProvider;
import org.opencabstandard.provider.HOSContract;

public class HOSProvider extends AbstractHOSProvider {
    private static final String LOG_TAG = HOSProvider.class.getName();

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSZ";

    public static Gson createGson() {
        GsonBuilder gsonb = new GsonBuilder();
        gsonb.setDateFormat(DATE_FORMAT);
        Gson gson = gsonb.create();

        return gson;
    }

    @Override
    protected HOSContract.HOSStatus getHOS(String version) {
        String json = Preferences.getHOS(getContext());
        Log.d(LOG_TAG, "HOS json: " + json);

        HOSContract.HOSStatus status = null;
        status = createGson().fromJson(json, HOSContract.HOSStatus.class);


        return status;
    }

    @Override
    protected Boolean startNavigation(String version) {
        Log.d(LOG_TAG, "startNavigation()");
        Preferences.setNavigationState(getContext(), true);
        return true;
    }

    @Override
    protected Boolean endNavigation(String version) {
        Log.d(LOG_TAG, "endNavigation()");
        Preferences.setNavigationState(getContext(), false);
        return true;
    }

}
