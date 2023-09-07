package org.opencabstandard.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * An abstract ContentProvider that implements the {@link VehicleInformationContract}. The provider app can choose
 * to implement the full ContentProvider or to extend this class.  If extending this class it only needs
 * to implement the abstract methods.
 */
public abstract class AbstractVehicleInformationProvider extends ContentProvider {
    private static final String LOG_TAG = AbstractVehicleInformationProvider.class.getName();

    /**
     * Initialize the provider.
     *
     * @return Indicates successful initialization.
     */
    @Override
    public boolean onCreate() {
        Log.d(LOG_TAG, "OnCreate()");
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
     * @param version The {@link VehicleInformationContract}.VERSION
     * @param extras  Additional data if needed by the method.
     * @return {@link Bundle} with results.
     */
    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String version, @Nullable Bundle extras) {
        Bundle result = new Bundle();
        Log.i(LOG_TAG, "Method name: " + method + ", version: " + version);

        switch (method) {
            case VehicleInformationContract.METHOD_GET_VEHICLE_INFORMATION:
                try {
                    VehicleInformationContract.VehicleInformation vehicle = getVehicleInformation(version);
                    if (vehicle != null) {
                        result.putParcelable(VehicleInformationContract.KEY_VEHICLE_INFORMATION, vehicle);
                        result.putString(VehicleInformationContract.KEY_VERSION, "0.2");
                    } else {
                        result.putString(VehicleInformationContract.KEY_ERROR, "Sorry, we are unable to fetch the current vehicle information");
                    }
                } catch (RuntimeException e) {
                    result.putString(VehicleInformationContract.KEY_ERROR, e.getMessage());
                }
                break;
            default:
                Log.w(LOG_TAG, "Unrecognized method name: " + method);
                result.putString(IdentityContract.KEY_ERROR, "The provided method was not recognized: " + method);
        }

        return result;
    }

    /**
     * Implement this method to return the vehicle information with VIN and other properties populated.
     *
     * @param version The {@link VehicleInformationContract}.VERSION
     * @return The vin number.
     */
    public abstract VehicleInformationContract.VehicleInformation getVehicleInformation(String version);
}