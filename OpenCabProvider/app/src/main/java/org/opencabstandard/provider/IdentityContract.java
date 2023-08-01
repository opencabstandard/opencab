package org.opencabstandard.provider;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Defines the contract for the OpenCab Identity Content provider. An OpenCab Identity provider app should
 * define an Android {@link android.content.ContentProvider} class
 * that follows this contract or should subclass the {@link AbstractIdentityProvider} class and
 * implement the abstract methods.
 * <h3>Single-sign on for one driver</h3>
 * <p>
 * An example sequence where an Identity consumer detects a login and performs single-sign on might be the following:
 * </p>
 * <div class="mermaid">
 *     sequenceDiagram
 *         participant A as Identity Consumer
 *         participant B as Identity Provider
 *         Note over B: Driver A logs in
 *         B-&gt;&gt;A: Broadcast: ACTION_DRIVER_LOGIN
 *         Note over A: App enumerates available Identity<br>providers and selects one.
 *         Note over A: App queries provider for list of active drivers.
 *         A-&gt;&gt;B: Call: METHOD_GET_ACTIVE_DRIVERS, version: "0.2"
 *         B-&gt;&gt;A: Return: [Driver{username="A"}]
 *         Note over A: App currently has no logged in user,<br>and notices that it now needs to have<br>user A logged in to match<br>the identity provider state.
 *         Note over A: The consumer makes a call to the Identity<br>provider and signals that it understands<br>version 0.3 of the contract and that it wants session<br> credentials for all active drivers. This is a<br>separate call from METHOD_GET_ACTIVE_DRIVERS<br>since it may be expensive for a provider to fetch<br>or generate credentials, and it should only be<br>called when needed, not simply to monitor<br>the list of active drivers.
 *         A-&gt;&gt;B: Call: METHOD_GET_LOGIN_CREDENTIALS, version: "0.3"
 *         Note over B: The provider app sees the "0.3" version and knows the calling<br>application supports team drivers via OpenCab. <br>If the version is "0.2" or lower, the provider knows the<br>calling application does not support team drivers via OpenCab.
 *         B-&gt;&gt;A: Return: <br>KEY_VERSION="0.3"<br>KEY_ALL_LOGIN_CREDENTIALS=[<br>  IdentityContract.DriverSession{<br>    username="A",<br>    loginCredentials=LoginCredentials{<br>      authority="example"<br>      provider="com.eleostech.example"<br>      token="kf40m1fpl…d28zckhuf6"<br>    }<br>  }<br>]<br>
 * </div>
 * <h3>Single sign-on for a second (team) driver</h3>
 * <div class="mermaid">
 *     sequenceDiagram
 *         participant B as Identity Consumer
 *         participant A as Identity Provider
 *         Note over A: Driver B taps the option to log in and enters their<br>credentials for the Identity provider app.
 *         A-&gt;&gt;B: Broadcast: IdentityContract.ACTION_DRIVER_LOGIN
 *         Note over B: App queries provider for list of active drivers.
 *         B-&gt;&gt;A: Call<br>IdentityContract.METHOD_GET_ACTIVE_DRIVERS("0.2")
 *         A-&gt;&gt;B: Return<br>[Driver{username="A"}, Driver{username="B"}]
 *         Note over B: App currently has only driver A logged in, so it knows that it<br>now needs to have user B logged in as well to match the <br>identity provider state.
 *         Note over B: The app makes a call to the Identity provider and signals<br>that it understands version 0.3 of the contract and that it<br>wants session credentials for all active drivers. This is a<br>separate call from METHOD_GET_ACTIVE_DRIVERS<br>since it may be expensive for a provider to fetch or generate<br>credentials, and it should only be called when needed, not<br>simply to monitor the list of active drivers.
 *         B-&gt;&gt;A: Call<br>IdentityContract.METHOD_GET_LOGIN_CREDENTIALS("0.3")
 *         A-&gt;&gt;B: Return<br>KEY_VERSION="0.3"<br>KEY_ALL_LOGIN_CREDENTIALS=[<br>  IdentityContract.DriverSession{<br>    username="A",<br>    loginCredentials=LoginCredentials{<br>      authority="example"<br>      provider="com.eleostech.example"<br>      token="kf40m1fpl…d28zckhuf6"<br>    }<br>  },<br>  IdentityContract.DriverSession{<br>    username="B",<br>    loginCredentials=LoginCredentials{<br>      authority="example"<br>      provider="com.eleostech.example"<br>      token="p0LLdm3Ma…KEAd8vMN12d"<br>    }<br>  }<br>]
 *         Note over B: App adjusts session state to be have driver A and B both logged in.
 * </div>
 */
public final class IdentityContract {

    /**
     * This is the current version of the {@link IdentityContract} for the Open Cab Standard.  The version will
     * be passed as an argument to all method calls to the provider.  The provider may reject or handle
     * appropriately if the VERSION does not match the expected value.  An OpenCab Identity provider allows access
     * to authentication related information that can be used by an OpenCab Identity consumer app to enable
     * SSO.
     */
    public static final String VERSION = "0.3";

    /**
     * This authority is declared in the manifest for the Identity Content Provider.  It is then used
     * by the consumer app to identify any providers installed on the device.
     */
    public static final String AUTHORITY = "org.opencabstandard.identity";

    /**
     * This is the name of the receiver class. Application will be looking for classes with this name when it tries to broadcast an event.
     */
    public static final String IDENTITY_CHANGED_RECEIVER = "IdentityChangedReceiver";

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
     * Use this key to retrieve the {@link DriverSession} from the {@link android.os.Bundle} object
     * that is returned from the {@link IdentityContract}.METHOD_GET_LOGIN_CREDENTIALS method call.
     *
     * <p>
     * Example:
     * <pre>
     * <code class="language-java">
     *     LoginCredentials credentials = result.getParcelable({@link IdentityContract}.KEY_ALL_LOGIN_CREDENTIALS);
     * </code>
     * </pre>
     */
    public static final String KEY_ALL_LOGIN_CREDENTIALS = "all_login_credentials";

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

    /**
     * For the methods {@link IdentityContract}.METHOD_GET_LOGIN_CREDENTIALS and {@link IdentityContract}.METHOD_GET_ACTIVE_DRIVERS,
     * the returned {@link android.os.Bundle} object will contain this key which maps to String indicating
     * contract version supported.
     *
     * <p>
     * Example:
     * <pre>
     * <code class="language-java">
     *     {@link android.content.ContentResolver} resolver = getApplicationContext().getContentResolver();
     *     {@link Bundle} result = resolver.call(Uri.parse("content://" + {@link HOSContract}.AUTHORITY),
     *                                  {@link IdentityContract}.METHOD_GET_LOGIN_CREDENTIALS,
     *                                  {@link IdentityContract}.VERSION,
     *                                  null);
     *     String version = result.getBoolean({@link IdentityContract}.KEY_VERSION);
     * </code>
     * </pre>
     */
    public static final String KEY_VERSION = "key_version";

    public IdentityContract() {

    }

    /**
     * Object containing the driver session.
     */
    public static class DriverSession implements Parcelable {

        private String username;

        /**
         * Retrieve driver user name
         *
         * @return username
         */
        public String getUsername() {
            return username;
        }

        /**
         * Set user name
         *
         * @param username
         */
        public void setUsername(String username) {
            this.username = username;
        }

        /**
         * Retrieve login credentials
         *
         * @return
         */
        public LoginCredentials getLoginCredentials() {
            return loginCredentials;
        }

        /**
         * Set user credentials
         *
         * @param loginCredentials
         */
        public void setLoginCredentials(LoginCredentials loginCredentials) {
            this.loginCredentials = loginCredentials;
        }

        private LoginCredentials loginCredentials;

        public DriverSession() {

        }

        protected DriverSession(Parcel in) {
            username = in.readString();
            loginCredentials = (LoginCredentials) in.readValue(LoginCredentials.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(username);
            dest.writeValue(loginCredentials);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<DriverSession> CREATOR = new Creator<DriverSession>() {
            @Override
            public DriverSession createFromParcel(Parcel in) {
                return new DriverSession(in);
            }

            @Override
            public DriverSession[] newArray(int size) {
                return new DriverSession[size];
            }
        };
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
         * Is this driver currently operating the vehicle?
         *
         * @return Boolean indicating whether this driver is operating the vehicle.
         */
        public boolean isDriving() {
            return driving;
        }

        /**
         * Indicate that this driver is currently operating the vehicle.  If false, the driver is
         * a co-driver.
         *
         * @param status Boolean indicating if this driver is operating the vehicle.
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
