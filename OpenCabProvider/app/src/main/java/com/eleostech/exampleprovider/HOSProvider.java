package com.eleostech.exampleprovider;

import android.util.Log;

import org.opencabstandard.provider.AbstractHOSProvider;
import org.opencabstandard.provider.HOSContract;

import java.util.ArrayList;

public class HOSProvider extends AbstractHOSProvider {
    private static final String LOG_TAG = HOSProvider.class.getName();

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSZ";

    @Override
    protected HOSContract.HOSStatus getHOS() {
        return HOSUtil.getHOSStatus(getContext(), false);
    }

    @Override
    protected HOSContract.HOSStatusV2 getHOSV2() {
        return HOSUtil.getHOSStatusV2(getContext());
    }

    @Override
    protected HOSContract.HOSData getHOSData() {
        return HOSUtil.getHOSData(getContext());
    }

    @Override
    protected ArrayList<HOSContract.HOSStatusV2> getTeamHOSV2() {
        return HOSUtil.getHOSStatusTeamV2(getContext());
    }

    @Override
    protected HOSContract.HOSTeamData getHOSTeamData() {
        return HOSUtil.getTeamHOSData(getContext());
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

    @Override
    protected Boolean isTeamDriverEnabled() {
        return Preferences.isIdentityProviderTeamDriverEnabled(getContext());
    }

    @Override
    protected String getHosVersion() {
        return Preferences.getHosVersion(getContext());
    }

    @Override
    protected int getTeamsDriversNumber() {
        return Preferences.getTeamsDriversNumber(getContext());
    }
}
