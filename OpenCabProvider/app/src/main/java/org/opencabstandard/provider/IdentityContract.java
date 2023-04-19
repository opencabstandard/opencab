package org.opencabstandard.provider;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Defines the contract for the OpenCab Identity Content provider. An OpenCab Identity provider app should
 * define an Android {@link android.content.ContentProvider} class
 * that follows this contract or should subclass the {@link AbstractIdentityProvider} class and
 * implement the abstract methods.
 */
public final class IdentityContract {

    /**
     * This is the current version of the {@link IdentityContract} for the Open Cab Standard.  The version will
     * be passed as an argument to all method calls to the provider.  The provider may reject or handle
     * appropriately if the VERSION does not match the expected value.  An OpenCab Identity provider allows access
     * to authentication related information that can be used by an OpenCab Identity consumer app to enable
     * SSO.
     */
    public static final String VERSION = "0.2";

    /**
     * This authority is declared in the manifest for the Identity Content Provider.  It is then used
     * by the consumer app to identify any providers installed on the device.
     */
    public static final String AUTHORITY = "org.opencabstandard.identity";

    /**
     * This Action is broadcast when the driver logs out of the OpenCab provider app. Providers MUST publish this
     * event when a user (either a team driver or the last primary user) logs out of the app. This event
     * SHOULD correspond to a change in the result of <code>METHOD_GET_ACTIVE_DRIVERS</code>
     *
     * <p>The OpenCab consumer app MAY listen to this broadcast and perform a logout of the consumer app.</p>
     *
     * <p>Note: The underlying string value of this constant erroneously begins with <code>com.opencabstandard</code>
     * rather than <code>org.opencabstandard</code>. This error is preserved in order to maintain backwards compatibility.</p>
     */
    public static final String ACTION_DRIVER_LOGOUT = "com.opencabstandard.ACTION_DRIVER_LOGOUT";

    /**
     * This Action is broadcast when the identity information changes.
     * <p>
     * You can subscribe to this event to receive updates indicating the active driver, or information
     * about the active driver (such as their LoginCredentials token) has been updated. To get the updated value,
     * call METHOD_GET_LOGIN_CREDENTIALS after receiving this event.
     * </p>
     *
     * <p>Providers MUST publish this event when the values returned by <code>METHOD_GET_LOGIN_CREDENTIALS</code> change.<p>
     */
    public static final String ACTION_IDENTITY_INFORMATION_CHANGED = "org.opencabstandard.ACTION_IDENTITY_INFORMATION_CHANGED";

    /**
     * This Action is broadcast when the user logs in to the application. Providers MUST publish this event
     * when a user authenticates with the app, such as by entering their credentials.
     */
    public static final String ACTION_DRIVER_LOGIN = "org.opencabstandard.ACTION_DRIVER_LOGIN";

    /**
     * Provider method for retrieving the login credentials.  The credentials include a token that uniquely
     * identifies the driver and can be used to authenticate the driver.
     *
     * <p>
     * Example:
     * <pre>
     * <code class="language-java">
     *     {@link android.content.ContentResolver} resolver = getApplicationContext().getContentResolver();
     *     {@link android.os.Bundle} result = resolver.call(Uri.parse("content://" + {@link IdentityContract}.AUTHORITY),
     *
     *                                  {@link IdentityContract}.METHOD_GET_LOGIN_CREDENTIALS,
     *                                  {@link IdentityContract}.VERSION,
     *                                  null);
     *
     *     LoginCredentials credentials = result.getParcelable({@link IdentityContract}.KEY_LOGIN_CREDENTIALS);
     * </code>
     * </pre>
     *
     * <p>
     * Diagram:
     * </p>
     *
     * <div class="mermaid">
     *     sequenceDiagram
     *       participant A as OpenCab Consumer
     *       participant B as OpenCab Provider
     *       A-&gt;&gt;B: contentResolver.call(Uri.parse("content://org.opencabstandard.identity"), "getLoginCredentials", "0.2", null)
     *       B-&gt;&gt;A: android.os.Bundle
     * </div>
     */
    public static final String METHOD_GET_LOGIN_CREDENTIALS = "getLoginCredentials";

    /**
     * Provider method for retrieving the current active drivers.  Active drivers can include the vehicle
     * operator as well as any co-drivers in the vehicle.
     *
     * <p>
     * Example:
     * <pre>
     * <code class="language-java">
     *     {@link android.content.ContentResolver} resolver = getApplicationContext().getContentResolver();
     *     {@link android.os.Bundle} result = resolver.call(Uri.parse("content://" + {@link IdentityContract}.AUTHORITY),
     *                                  {@link IdentityContract}.METHOD_GET_ACTIVE_DRIVERS,
     *                                  {@link IdentityContract}.VERSION,
     *                                  null);
     *
     *     {@link java.util.ArrayList}&lt;IdentityContract.Driver&gt; drivers =
     *              result.getParcelableArrayList({@link IdentityContract}.KEY_ACTIVE_DRIVERS);
     * </code>
     * </pre>
     *
     * <p>
     * Diagram:
     * </p>
     *
     * <div class="mermaid">
     *     sequenceDiagram
     *       participant A as OpenCab Consumer
     *       participant B as OpenCab Provider
     *       A-&gt;&gt;B: contentResolver.call(Uri.parse("content://org.opencabstandard.identity"), "getActiveDrivers", "0.2", null)
     *       B-&gt;&gt;A: android.os.Bundle
     * </div>
     */
    public static final String METHOD_GET_ACTIVE_DRIVERS = "getActiveDrivers";

    /**
     * Use this key to retrieve the active drivers from the {@link android.os.Bundle} object that is returned
     * from the {@link IdentityContract}.METHOD_GET_ACTIVE_DRIVERS method call.
     *
     * <p>
     * Example:
     * <pre>
     * <code class="language-java">
     *     {@link java.util.ArrayList}&lt;Driver&gt; drivers =
     *              result.getParcelableArrayList({@link IdentityContract}.KEY_ACTIVE_DRIVERS);
     * </code>
     * </pre>
     */
    public static final String KEY_ACTIVE_DRIVERS = "activeDrivers";

    /**
     * Use this key to retrieve the {@link LoginCredentials} from the {@link android.os.Bundle} object
     * that is returned from the {@link IdentityContract}.METHOD_GET_LOGIN_CREDENTIALS method call.
     *
     * <p>
     * Example:
     * <pre>
     * <code class="language-java">
     *     LoginCredentials credentials = result.getParcelable({@link IdentityContract}.KEY_LOGIN_CREDENTIALS);
     * </code>
     * </pre>
     */
    public static final String KEY_LOGIN_CREDENTIALS = "login_credentials";

    /**
     * If an error has occurred in one of the provider method calls, use this key to retrieve
     * the error from the Bundle object returned from the provider call method.
     *
     * <p>
     * Example:
     * <pre>
     * <code class="language-java">
     *     String error = result.getString({@link IdentityContract}.KEY_ERROR);
     * </code>
     * </pre>
     */
    public static final String KEY_ERROR = "error";


    public IdentityContract() {

    }

    /**
     * Object containing the login credentials.
     */
    public static class LoginCredentials implements Parcelable {

        private String token;
        private String provider;
        private String authority;

        public LoginCredentials() {

        }

        /**
         * An authentication token that can be used to uniquely and securely identify the driver.
         * For more details about possible types of tokens, see {@link IdentityContract}.
         *
         * @param token The login token
         */
        public void setToken(String token) {
            this.token = token;
        }

        /**
         * The package name of the OpenCab identity provider.
         *
         * @param provider The provider package name.
         */
        public void setProvider(String provider) {
            this.provider = provider;
        }

        /**
         * A URL that can be used to authenticate the login token.
         * For more details about how OpenCab interacts with authentication systems, see {@link IdentityContract}.
         *
         * @param authority The authority URL.
         */
        public void setAuthority(String authority) {
            this.authority = authority;
        }

        /**
         * A token that can be used to uniquely identify the driver.
         * For more details about possible types of tokens, see {@link IdentityContract}.
         *
         * @return The login token
         */
        public String getToken() {
            return token;
        }

        /**
         * The package name of the OpenCab identity provider.
         *
         * @return The provider package name.
         */
        public String getProvider() {
            return provider;
        }

        /**
         * A URL that can be used to authenticate the login token.
         * For more details about how OpenCab interacts with authentication systems, see {@link IdentityContract}.
         *
         * @return The authority url.
         */
        public String getAuthority() {
            return authority;
        }

        protected LoginCredentials(Parcel in) {
            token = in.readString();
            provider = in.readString();
            authority = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(token);
            dest.writeString(provider);
            dest.writeString(authority);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<LoginCredentials> CREATOR = new Creator<LoginCredentials>() {
            @Override
            public LoginCredentials createFromParcel(Parcel in) {
                return new LoginCredentials(in);
            }

            @Override
            public LoginCredentials[] newArray(int size) {
                return new LoginCredentials[size];
            }
        };
    }

    /**
     * Object representing a Driver.
     */
    public static class Driver implements Parcelable {

        private String username;
        private boolean driving;

        /**
         * Set the driver username.
         *
         * @param user The username of the driver.
         */
        public void setUsername(String user) {
            this.username = user;
        }

        /**
         * Get the username of the driver.
         *
         * @return The username of the driver.
         */
        public String getUsername() {
            return this.username;
        }

        /**
         * Set to true for the driver who is currently active on the device as determined
         * by the authentication mechanism or UI, such as a dropdown for switching between
         * which user’s information is currently displayed. This does not indicate which
         * driver is marked as driving in the ELD, although for apps that act as providers
         * for both the Identity and HOS contracts, the value of this field MAY be driven
         * by ELD state. If the value of KEY_ACTIVE_DRIVERS is non-empty, exactly one driver
         * MUST be marked with isDriving set to true.
         *
         * <p>Consuming apps that only support a single user session at a time MUST track
         * which driver has this property set and perform a logout/login as necessary to
         * keep in sync with the identity provider’s active driver. Consuming apps that
         * support access by multiple users at once MAY use this property to sync which
         * user’s information is displayed with the identity provider’s active driver.
         *
         * @return Boolean indicating whether this driver is the primary or active user.
         */
        public boolean isDriving() {
            return driving;
        }

        /**
         * Set to true for the driver who is currently active on the device as determined
         * by the authentication mechanism or UI, such as a dropdown for switching between
         * which user’s information is currently displayed. This does not indicate which
         * driver is marked as driving in the ELD, although for apps that act as providers
         * for both the Identity and HOS contracts, the value of this field MAY be driven
         * by ELD state. If the value of KEY_ACTIVE_DRIVERS is non-empty, exactly one driver
         * MUST be marked with isDriving set to true.
         *
         * <p>Consuming apps that only support a single user session at a time MUST track
         * which driver has this property set and perform a logout/login as necessary to
         * keep in sync with the identity provider’s active driver. Consuming apps that
         * support access by multiple users at once MAY use this property to sync which
         * user’s information is displayed with the identity provider’s active driver.
         *
         * @param status Boolean indicating whether this driver is the primary or active user.
         */
        public void setDriving(boolean status) {
            driving = status;
        }

        public Driver() {

        }

        protected Driver(Parcel in) {
            this.username = in.readString();
            this.driving = (Boolean) in.readValue(Boolean.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(username);
            dest.writeValue(driving);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Driver> CREATOR = new Creator<Driver>() {
            @Override
            public Driver createFromParcel(Parcel in) {
                return new Driver(in);
            }

            @Override
            public Driver[] newArray(int size) {
                return new Driver[size];
            }
        };
    }
}
