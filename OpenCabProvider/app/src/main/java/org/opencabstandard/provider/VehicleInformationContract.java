package org.opencabstandard.provider;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Defines the contract for the OpenCab Identity Content provider. An OpenCab Identity provider app should
 * define an Android {@link android.content.ContentProvider} class
 * that follows this contract or should subclass the {@link AbstractIdentityProvider} class and
 * implement the abstract methods.
 */
public final class VehicleInformationContract {

    /**
     * This is the current version of the {@link VehicleInformationContract} for the OpenCab Standard.  The version will
     * be passed as an argument to all method calls to the provider.  The provider may reject or handle
     * appropriately if the VERSION does not match the expected value.  An OpenCab vehicle information provider allows access
     * to details about the vehicle, if any, associated with the mobile device running the app or associated
     * with the driver who is logged into the mobile app.
     */
    public static final String VERSION = "0.2";

    /**
     * This authority is declared in the manifest for the app that acts as the vehicle information provider.
     * It is then used by the consumer app to identify any providers installed on the device.
     */
    // TODO: can we use dashes in java package names?
    public static final String AUTHORITY = "org.opencabstandard.vehicleinformation";

    /**
     * This Action is broadcast when the vehicle information changes, perhaps because the driver
     * chooses to associate with another vehicle, or if the device removed from a vehicle in a
     * slip-seat scenario.
     */
    public static final String ACTION_DRIVER_LOGOUT = "com.opencabstandard.VEHICLE_INFORMATION_CHANGED";

     /**
     * 
     */
    /**
     * Provider method for retrieving the vehicle information.
     *
     // TODO: Proper example
     */
    public static final String METHOD_GET_VEHICLE_INFORMATION = "getVehicleInformation";

    /**
     * Use this key to retrieve the VIN from the {@link android.os.Bundle} object that is returned
     * from the {@link VehicleInformationContract}.METHOD_GET_VEHICLE_INFORMATION method call.
     *
     * TODO: Example
     */
    public static final String KEY_VIN = "vin";

    /**
     * If an error has occurred in one of the provider method calls, use this key to retrieve
     * the error from the Bundle object returned from the provider call method.
     *
     * <p>
     * Example:
     * <pre>
     * <code class="language-java">
     *     String error = result.getString({@link VehicleInformationContract}.KEY_ERROR);
     * </code>
     * </pre>
     */
    public static final String KEY_ERROR = "error";


    public VehicleInformationContract() {

    }

    /**
     * Object containing the login credentials.
     */
    public static class VehicleInformation implements Parcelable {

        private String vin;

        public VehicleInformation() {

        }

        // todos: setters and getters and parcel stuff
    }
}
