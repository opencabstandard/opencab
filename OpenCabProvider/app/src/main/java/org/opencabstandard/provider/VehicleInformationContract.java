package org.opencabstandard.provider;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;



/**
 * Defines the contract for the OpenCab Vehicle Information provider. An OpenCab Vehicle Information provider app should
 * define an Android {@link android.content.ContentProvider} class
 * that follows this contract or should subclass the {@link AbstractVehicleInformationProvider} class and
 * implement the abstract methods.
 * <div class="mermaid">
 *     sequenceDiagram
        participant A as OpenCab Consumer
        participant B as OpenCab Provider
        Note over A,B: consumer app launches, no driver logged in
        A-&gt;&gt;+B: provider.call(METHOD_GET_VEHICLE_INFORMATION, version: 1)
        B-&gt;&gt;A: {VEHICLE_INFORMATION: null}
        Note over A,B: driver logs in or connects to a vehicle mount
        B-&gt;&gt;A: ACTION_VEHICLE_INFORMATION_CHANGED
        A-&gt;&gt;+B: provider.call(METHOD_GET_VEHICLE_INFORMATION, version: 1)
        B-&gt;&gt;A: {VEHICLE_INFORMATION: {VIN: "JH4NA1150RT000268", MOVING: false}}
        Note over A,B: driver switches to D status or puts vehicle in gear
        B-&gt;&gt;A: ACTION_VEHICLE_INFORMATION_CHANGED
        A-&gt;&gt;+B: provider.call(METHOD_GET_VEHICLE_INFORMATION, version: 1)
        B-&gt;&gt;A: {VEHICLE_INFORMATION: {VIN: "JH4NA1150RT000268", MOVING: true}}
        Note over A,B: driver logs out or detaches tablet
        B-&gt;&gt;A: ACTION_VEHICLE_INFORMATION_CHANGED
        A-&gt;&gt;+B: provider.call(METHOD_GET_VEHICLE_INFORMATION, version: 1)
        B-&gt;&gt;A: {VEHICLE_INFORMATION: null}
 * </div>
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
    // TODO: can we use dashes in java package names? No dashes but we can use underscores
    public static final String AUTHORITY = "org.opencabstandard.vehicleinformation";

    /**
     * This Action is broadcast when the vehicle information changes, perhaps because the driver
     * chooses to associate with another vehicle, or if the device removed from a vehicle in a
     * slip-seat scenario.
     */
    public static final String ACTION_VEHICLE_INFORMATION_CHANGED = "com.opencabstandard.VEHICLE_INFORMATION_CHANGED";

    /**
     * Provider method for retrieving the vehicle information.
     * 
     * <p>
     * Example:
     * <pre>
     * <code class="language-java">
     *
     *     {@link android.content.ContentResolver} resolver = getApplicationContext().getContentResolver();
     *     {@link Bundle} result = resolver.call(Uri.parse("content://" + {@link VehicleInformationContract}.AUTHORITY),
     *                                  {@link VehicleInformationContract}.METHOD_GET_VEHICLE_INFORMATION,
     *                                  {@link VehicleInformationContract}.VERSION,
     *                                  null);
     *     {@link VehicleInformationContract}.VehicleInformation info = result.getParcelableArrayList({@link VehicleInformationContract}.KEY_VEHICLE_INFORMATION);
     * </code>
     * </pre>
     *
     *
     */
    public static final String METHOD_GET_VEHICLE_INFORMATION = "getVehicleInformation";

    /**
     * Use this key to retrieve the VIN from the {@link android.os.Bundle} object that is returned
     * from the {@link VehicleInformationContract}.METHOD_GET_VEHICLE_INFORMATION method call. If the value is null,
     * an error occurred and you can then retrieve the error from the {@link android.os.Bundle} using the key
     * {@link VehicleInformationContract}.KEY_ERROR.
     *
     * <p>
     * Example:
     * <pre>
     * <code class="language-java">
     *
     *     {@link android.content.ContentResolver} resolver = getApplicationContext().getContentResolver();
     *     {@link Bundle} result = resolver.call(Uri.parse("content://" + {@link VehicleInformationContract}.AUTHORITY),
     *                                  {@link VehicleInformationContract}.METHOD_GET_VEHICLE_INFORMATION,
     *                                  {@link VehicleInformationContract}.VERSION,
     *                                  null);
     *     {@link VehicleInformationContract}.VehicleInformation info = result.getParcelableArrayList({@link VehicleInformationContract}.KEY_VEHICLE_INFORMATION);
     * </code>
     * </pre>
     *
     */
    public static final String KEY_VEHICLE_INFORMATION = "vehicle_info";

    /**
     * If an error has occurred in one of the provider method calls, use this key to retrieve
     * the error from the Bundle object returned from the provider call method.
     *
     * <p>
     * Example:
     * <pre>
     * <code class="language-java">
     *     {@link Bundle} result = provider.call(Uri.parse("content://" + {@link VehicleInformationContract}.AUTHORITY),
     *                                  "ANY METHOD",
     *                                  {@link VehicleInformationContract}.VERSION,
     *                                  null);
     *     String error = result.getString({@link VehicleInformationContract}.KEY_ERROR);
     * </code>
     * </pre>
     */
    public static final String KEY_ERROR = "error";


    public VehicleInformationContract() {

    }

    /**
     * Object containing the vehicle information.
     */
    public static class VehicleInformation implements Parcelable {

        private String vin;
        private boolean moving;

        public VehicleInformation() {

        }

        /**
         * A string to identify the vehicle vin number.
         *
         * @param vin The vehicle information number (VIN) as a string, e.g. "1M2AX07Y79M006004"
         */
        public void setVin(String vin) {
            this.vin = vin;
        }

        /**
         * A string to identify the vehicle vin number.
         *
         * @return vin The vehicle information number (VIN) as a string, e.g. "1M2AX07Y79M006004"
         */
        public String getVin() {
            return vin;
        }
        
        /**
         * Is the vehicle currently moving?
         * @return Boolean indicating whether the vehicle is currently moving.
         */
        public boolean isMoving() {
            return moving;
        }

        /**
         * Indicate that the vehicle is currently moving.  If false, the vehicle is not
         * and does not intend on moving.
         * @param status Boolean indicating if the vehicle is currently moving.
         */
        public void setMoving(boolean status) {
            moving = status;
        }        

        protected VehicleInformation(Parcel in) {
            this.vin = in.readString();
            this.moving = (Boolean)in.readValue(Boolean.class.getClassLoader());
        }        

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(vin);
            dest.writeValue(moving);
        }      

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<VehicleInformation> CREATOR = new Creator<VehicleInformation>() {
            @Override
            public VehicleInformation createFromParcel(Parcel in) {
                return new VehicleInformation(in);
            }

            @Override
            public VehicleInformation[] newArray(int size) {
                return new VehicleInformation[size];
            }
        };
    }        
}
