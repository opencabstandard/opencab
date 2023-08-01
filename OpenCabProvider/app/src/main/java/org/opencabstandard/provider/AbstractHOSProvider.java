package org.opencabstandard.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * An abstract ContentProvider that implements the {@link HOSContract}.  The provider app can choose
 * to implement the full ContentProvider or to extend this class.  If extending this class it only needs
 * to implement the abstract methods.
 */
public abstract class AbstractHOSProvider extends ContentProvider {
    private static final String LOG_TAG = AbstractHOSProvider.class.getName();

    /**
     * Initialize the provider.
     *
     * @return Indicates successful initialization.
     */
    @Override
    public boolean onCreate() {
        Log.d(LOG_TAG, "onCreate()");
        return true;
    }

    /**
     * Not used.
     *
     * @param uri
     * @param strings
     * @param s
     * @param strings1
     * @param s1
     * @return
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    /**
     * Not used.
     *
     * @param uri
     * @return
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * Not used.
     *
     * @param uri
     * @param contentValues
     * @return
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    /**
     * Not used.
     *
     * @param uri
     * @param s
     * @param strings
     * @return
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    /**
     * Not used.
     *
     * @param uri
     * @param contentValues
     * @param s
     * @param strings
     * @return
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    /**
     * This method will be called for all interactions with the ContentProvider based on the method argument
     * passed in.  The appropriate abstract method will be called based on the method argument.
     *
     * @param method  The desired method to call.
     * @param version The {@link HOSContract}.VERSION
     * @param extras  Additional data if needed by the method.
     * @return {@link Bundle} with results.
     */
    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String version, @Nullable Bundle extras) {
        Bundle result = new Bundle();
        Log.i(LOG_TAG, "Method name: " + method + ", version: " + version);
        switch (method) {
            case HOSContract.METHOD_GET_HOS:
                result.putString(HOSContract.KEY_VERSION, getHosVersion());
                Version requestedVersion = new Version(getHosVersion() != null ? getHosVersion() : "0.2");
                Version supportedVersionV2 = new Version("0.2");
                Version supportedVersionV3 = new Version("0.3");
                Log.i(LOG_TAG, "requested version: " + requestedVersion + ", supported version: " + supportedVersionV2);
                if (requestedVersion.compareTo(supportedVersionV2) == 0) {
                    HOSContract.HOSStatus status = getHOS();
                    if (status != null) {
                        result.putParcelable(HOSContract.KEY_HOS, status);
                        result.putString(HOSContract.KEY_VERSION, "0.2");
                    } else {
                        result.putString(HOSContract.KEY_ERROR, "Sorry, we are unable to fetch the current HOS.");
                    }
                } else if (requestedVersion.compareTo(supportedVersionV3) >= 0) {
                    HOSContract.HOSStatusV2 status = getHOSV2();
                    if (status != null) {
                        result.putParcelable(HOSContract.KEY_HOS, status);
                        if (isTeamDriverEnabled()) {
                            result.putParcelableArrayList(HOSContract.KEY_TEAM_HOS, getTeamHOSV2());
                        }
                        result.putString(HOSContract.KEY_VERSION, "0.3");
                    } else {
                        result.putString(HOSContract.KEY_ERROR, "Sorry, we are unable to fetch the current HOS.");
                    }
                }
                break;
            case HOSContract.METHOD_START_NAVIGATION:
                boolean startStatus = startNavigation(version);
                result.putBoolean(HOSContract.KEY_NAVIGATION_RESULT, startStatus);
                break;
            case HOSContract.METHOD_END_NAVIGATION:
                boolean endStatus = endNavigation(version);
                result.putBoolean(HOSContract.KEY_NAVIGATION_RESULT, endStatus);
                break;
            default:
                Log.w(LOG_TAG, "Unrecognized method name: " + method);
                result.putString(HOSContract.KEY_ERROR, "The provided method was not recognized: " + method);
        }

        return result;
    }

    /**
     * Implement this method to return the current HOS status v2.
     *
     * @return The current HOS
     */
    protected abstract HOSContract.HOSStatus getHOS();

    /**
     * Implement this method to return the current HOS status v2.
     *
     * @return The current HOS
     */
    protected abstract HOSContract.HOSStatusV2 getHOSV2();

    /**
     * Implement this method to return the current team HOS status v2.
     *
     * @return The current HOS
     */
    protected abstract ArrayList<HOSContract.HOSStatusV2> getTeamHOSV2();

    /**
     * Implement this method to indicate when the app started navigation.
     *
     * @param version The {@link HOSContract}.VERSION
     * @return Indicator if the method was successful.
     */
    protected abstract Boolean startNavigation(String version);

    /**
     * Implement this method to indicate when the app ended navigation.
     *
     * @param version The {@link HOSContract}.VERSION
     * @return Indicator if the method was successful.
     */
    protected abstract Boolean endNavigation(String version);

    /**
     * Implement this to enable team driver functionality.
     *
     * @return
     */
    protected abstract Boolean isTeamDriverEnabled();

    /**
     * Implement this to force a specific {@link HOSContract}.VERSION.
     *
     * @return
     */
    protected abstract String getHosVersion();

}
