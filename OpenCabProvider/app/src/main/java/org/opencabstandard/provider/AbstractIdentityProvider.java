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
import java.util.List;

/**
 * An abstract ContentProvider that implements the {@link IdentityContract}. The provider app can choose
 * to implement the full ContentProvider or to extend this class.  If extending this class it only needs
 * to implement the abstract methods.
 */
public abstract class AbstractIdentityProvider extends ContentProvider {
    private static final String LOG_TAG = AbstractIdentityProvider.class.getName();

    /**
     * Initialize the provider.
     *
     * @return Indicates successful initialization.
     */
    @Override
    public boolean onCreate() {
        Log.d(LOG_TAG, "Created");
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
     * @param version The {@link IdentityContract}.VERSION
     * @param extras  Additional data if needed by the method.
     * @return {@link Bundle} with results.
     */
    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String version, @Nullable Bundle extras) {
        Bundle result = new Bundle();
        Log.i(LOG_TAG, "Method name: " + method + ", version: " + version);

        switch (method) {
            case IdentityContract.METHOD_GET_ACTIVE_DRIVERS:
                ArrayList<IdentityContract.Driver> drivers = getActiveDrivers(version);
                int count = (drivers != null) ? drivers.size() : 0;
                Log.d(LOG_TAG, "Found active drivers: " + count);
                result.putParcelableArrayList(IdentityContract.KEY_ACTIVE_DRIVERS, drivers);
                break;
            case IdentityContract.METHOD_GET_LOGIN_CREDENTIALS:
                IdentityContract.LoginCredentials creds = getLoginCredentials(version);
                result.putParcelable(IdentityContract.KEY_LOGIN_CREDENTIALS, creds);
                ArrayList<IdentityContract.DriverSession> driverSessionList = getAllLoginCredentials(version);
                result.putParcelableArrayList(IdentityContract.KEY_ALL_LOGIN_CREDENTIALS, driverSessionList);
                break;
            default:
                Log.w(LOG_TAG, "Unrecognized method name: " + method);
                result.putString(IdentityContract.KEY_ERROR, "The provided method was not recognized: " + method);
        }

        return result;
    }

    /**
     * Implement this method to return the login credentials.
     *
     * @param version The {@link IdentityContract}.VERSION
     * @return The login credentials.
     */
    public abstract IdentityContract.LoginCredentials getLoginCredentials(String version);

    /**
     * Implement this method to return the login credentials for version 0.3+ .
     *
     * @param version The {@link IdentityContract}.VERSION
     * @return The login credentials.
     */
    public abstract ArrayList<IdentityContract.DriverSession> getAllLoginCredentials(String version);


    /**
     * Implement this method to return a {@link ArrayList} of the current active drivers.
     *
     * @param version The {@link IdentityContract}.VERSION
     * @return List of active drivers.
     */
    public abstract ArrayList<IdentityContract.Driver> getActiveDrivers(String version);

}
