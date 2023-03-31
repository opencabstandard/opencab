package org.opencabstandard.provider;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import org.opencabstandard.provider.AbstractHOSProvider;
/**
 * Defines the contract for the OpenCab HOS Content provider.  An OpenCab HOS provider app should define
 * an Android {@link android.content.ContentProvider} class that follows this contract or should extend
 * the {@link AbstractHOSProvider} class and implement the abstract methods.
 */
public final class HOSContract {

    /**
     * This is the current version of the HOSContract for the Open Cab Standard.  The version will
     * be passed as an argument to all method calls to the provider. The provider may reject or handle
     * appropriately if the VERSION does not match the expected value when passed to the method calls.
     */
    public static final String VERSION = "0.3";

    /**
     * This authority is used for querying the HOS provider.  This should be declared in the manifest
     * as the authority for the HOS provider.
     */
    public static final String AUTHORITY = "org.opencabstandard.hos";

    /**
     * Provider method name for retrieving the current HOS.  The returned object contains a list of clocks.
     * The clocks can be displayed in the OpenCab HOS consumer app to provide the driver update to date
     * information about his current HOS status. This method can take some time to execute, so the consumer
     * app should avoid making this call on the main thread as it could cause the app to become unresponsive.
     *
     * <p>
     * Example:
     * <pre>
     * <code class="language-java">
     *
     *     {@link android.content.ContentResolver} resolver = getApplicationContext().getContentResolver();
     *     {@link Bundle} result = resolver.call(Uri.parse("content://" + {@link HOSContract}.AUTHORITY),
     *                                  {@link HOSContract}.METHOD_GET_HOS,
     *                                  {@link HOSContract}.VERSION,
     *                                  null);
     *     {@link HOSContract}.HOSStatus status = result.getParcelableArrayList({@link HOSContract}.KEY_HOS);
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
     *       A-&gt;&gt;B: contentResolver.call(Uri.parse("content://org.opencabstandard.hos"), "getHOS", "0.2", null)
     *       B-&gt;&gt;A: android.os.Bundle
     * </div>
     */
    public static final String METHOD_GET_HOS = "getHOS";

    /**
     * Provider method name indicating that the OpenCab consumer app has started navigation.  The OpenCab
     * provider app can use this as an indicator that it is not necessary to lock the screen due to vehicle
     * motion.
     *
     * <p>
     * Example:
     * <pre>
     * <code class="language-java">
     *     {@link android.content.ContentResolver} resolver = getApplicationContext().getContentResolver();
     *     {@link Bundle} result = resolver.call(Uri.parse("content://" + {@link HOSContract}.AUTHORITY),
     *                                  {@link HOSContract}.METHOD_START_NAVIGATION,
     *                                  {@link HOSContract}.VERSION,
     *                                  null);
     *     Boolean status = result.getBoolean({@link HOSContract}.KEY_NAVIGATION_RESULT);
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
     *       A-&gt;&gt;B: contentResolver.call(Uri.parse("content://org.opencabstandard.hos"), "startNavigation", "0.2", null)
     *       B-&gt;&gt;A: android.os.Bundle
     * </div>
     */
    public static final String METHOD_START_NAVIGATION = "startNavigation";

    /**
     * Provider method name indicating that the OpenCab consumer app has ended navigation. The OpenCab
     * provider app can use this as an indicator that it can lock the screen due to vehicle motion.
     *
     * <p>
     * Example:
     * <pre>
     * <code class="language-java">
     *     {@link android.content.ContentResolver} resolver = getApplicationContext().getContentResolver();
     *     {@link Bundle} result = resolver.call(Uri.parse("content://" + {@link HOSContract}.AUTHORITY),
     *                                  {@link HOSContract}.METHOD_END_NAVIGATION,
     *                                  {@link HOSContract}.VERSION,
     *                                  null);
     *     Boolean status = result.getBoolean({@link HOSContract}.KEY_NAVIGATION_RESULT);
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
     *       A-&gt;&gt;B: contentResolver.call(Uri.parse("content://org.opencabstandard.hos"), "endNavigation", "0.2", null)
     *       B-&gt;&gt;A: android.os.Bundle
     * </div>
     */
    public static final String METHOD_END_NAVIGATION = "endNavigation";

    /**
     * Key for retrieving the HOS status from the returned {@link android.os.Bundle} object.  If the value is null,
     * an error occurred and you can then retrieve the error from the {@link android.os.Bundle} using the key
     * {@link HOSContract}.KEY_ERROR.
     *
     * <p>
     * Example:
     * <pre>
     * <code class="language-java">
     *      {@link android.content.ContentResolver} resolver = getApplicationContext().getContentResolver();
     *      {@link Bundle} result = resolver.call(Uri.parse("content://" + {@link HOSContract}.AUTHORITY),
     *                                  {@link HOSContract}.METHOD_GET_HOS,
     *                                  {@link HOSContract}.VERSION,
     *                                  null);
     *     {@link HOSContract.HOSStatus} status = result.getParelableArrayList({@link HOSContract}.KEY_HOS);
     * </code>
     * </pre>
     */
    public static final String KEY_HOS = "hos";

    /**
     * If an error has occurred in one of the provider method calls, use this key to retrieve
     * the error from the Bundle object returned from the provider call method.
     *
     * <p>
     * Example:
     * <pre>
     * <code class="language-java">
     *     {@link Bundle} result = provider.call(Uri.parse("content://" + {@link HOSContract}.AUTHORITY),
     *                                  "ANY METHOD",
     *                                  {@link HOSContract}.VERSION,
     *                                  null);
     *     String error = result.getString({@link HOSContract}.KEY_ERROR);
     * </code>
     * </pre>
     *
     */
    public static final String KEY_ERROR = "error";

    /**
     * For the methods {@link HOSContract}.METHOD_START_NAVIGATION and {@link HOSContract}.METHOD_END_NAVIGATION,
     * the returned {@link android.os.Bundle} object will contain this key which maps to a Boolean indicating
     * success or failure.
     *
     * <p>
     * Example:
     * <pre>
     * <code class="language-java">
     *     {@link Bundle} result = provider.call(Uri.parse("content://" + {@link HOSContract}.AUTHORITY),
     *                                  {@link HOSContract}.METHOD_START_NAVIGATION,
     *                                  {@link HOSContract}.VERSION,
     *                                  null);
     *     Boolean status = result.getBoolean({@link HOSContract}.KEY_NAVIGATION_RESULT);
     * </code>
     * </pre>
     */
    public static final String KEY_NAVIGATION_RESULT = "navigation_result";

    public HOSContract() {

    }

    /**
     * An object representing the HOS status.
     */
    public static class HOSStatus implements Parcelable {
        private List<Clock> clocks;
        private String manageAction;

        public HOSStatus() {

        }

        /**
         * The list of current HOS clocks for the driver.
         *
         * @return The HOS clocks
         */
        public List<Clock> getClocks() {
            return clocks;
        }

        /**
         * The list of current HOS clocks for the driver.
         * @param clocks The HOS clocks
         */
        public void setClocks(List<Clock> clocks) {
            this.clocks = clocks;
        }

        /**
         * An intent string to launch the OpenCab HOS provider app.
         *
         * @param manageAction The intent string
         */
        public void setManageAction(String manageAction) {
            this.manageAction = manageAction;
        }

        /**
         * An intent string to launch the OpenCab HOS provider app.
         *
         * @return The intent string
         */
        public String getManageAction() {
            return manageAction;
        }

        protected HOSStatus(Parcel in) {
            clocks = in.createTypedArrayList(Clock.CREATOR);
            manageAction = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(clocks);
            dest.writeString(manageAction);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<HOSStatus> CREATOR = new Creator<HOSStatus>() {
            @Override
            public HOSStatus createFromParcel(Parcel in) {
                return new HOSStatus(in);
            }

            @Override
            public HOSStatus[] newArray(int size) {
                return new HOSStatus[size];
            }
        };
    }

    /**
     * Object representing an HOS clock.  A clock contains a descriptive label and the value.  The
     * value can be one of the types defined in the {@link ValueType} enum.
     *
     * <p>
     * An example of the different types of clocks is shown in the image below:
     * </p>
     *
     * <img src="../../../images/clocks-example.png" alt="Hours of service screenshot from the mobile application.">
     */
    public static class Clock implements Parcelable {
        private String label;
        private String value;
        private ValueType valueType;
        private boolean important;
        private boolean limitsDrivingRange;
        private Double durationSeconds;

        /**
         * Allowed types for valueType field.
         */
        public enum ValueType {

            /**
             * The value field will contain a simple string. It can be informational information
             * such as the driver username or something like "Adverse Weather Conditions".
             *
             * You can also use a string for an ELD clock duration (like 4:32) if you want to ensure
             * your own specific rounding and formatting logic is used. In this case, providers SHOULD
             * provide the actual duration in seconds using the {@link durationSeconds} property. Consumers MUST
             * display the <code>value</code>, but MAY use {@link durationSeconds} for business logic other than
             * pure information display.
             */
            STRING("string"),

            /**
             *  Indicates that the value field will contain a date in
             *  <a href="https://tools.ietf.org/html/rfc3339">RFC3339</a>
             *  format.  The date will appear formatted as "MM/dd/yyyy".  An example for this type of clock
             *  could be the date of next required truck service.
             */
            DATE("date"),

            /**
             *  Indicates that the value field will contain a date in
             *  <a href="https://tools.ietf.org/html/rfc3339">RFC3339</a>
             *  format that will be shown as a clock counting up from the provided date.  An example for this
             *  type of clock could be the number of hours since last rest.
             */
            COUNTUP("countup"),

             /**
              *  Indicates that the value field will contain a date in
              *  <a href="https://tools.ietf.org/html/rfc3339">RFC3339</a>
              *  format that will be shown as a clock counting down to zero.  The counter will not go below zero.
              *  An example for this type of clock could be the remaining available drive time.
              */
            COUNTDOWN("countdown");

            private final String type;

            ValueType(String t) {
                type = t;
            }

            public String toString() {
                return this.type;
            }
        }

        public Clock() {

        }

        /**
         * Label for the clock.
         *
         * @param label The clock label.
         */
        public void setLabel(String label) {
            this.label = label;
        }

        /**
         * The value of the clock.  The format of this field will depend on the {@link Clock}.valueType field.
         *
         * @param value The clock value.
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * The value type of the clock.  See {@link ValueType} for the possible types.
         *
         * @param valueType The valueType for the clock.
         */
        public void setValueType(ValueType valueType) {
            this.valueType = valueType;
        }

        /**
         * Indicates the important clock. Consumers may interpret this flag in multiple ways,
         * but one possible use is to determine which clock to display in a compact view
         * layout that only permits a single clock to be shown.
         *
         * @param important Flag indicating which is the most important clock in the list.
         */
        public void setImportant(boolean important) {
            this.important = important;
        }

        /**
         * Indicates which clock most tightly limits the time a driver can spend driving.
         * Consumers may interpret this flag in multiple ways, but one possible use is to
         * indicate where a driver needs to plan to shut down when planning a route.
         *
         * @param limitsDrivingRange Flag indicating which clock limits the driving range.
         */
        public void setLimitsDrivingRange(boolean limitsDrivingRange) {
            this.limitsDrivingRange = limitsDrivingRange;
        }

        /**
         * Get the label for the clock.
         *
         * @return The label for the clock.
         */
        public String getLabel() {
            return label;
        }

        /**
         * Get the value for the clock.
         *
         * @return The value for the clock.
         */
        public String getValue() {
            return value;
        }

        /**
         * Get the value type
         * @return The value type for the clock.
         */
        public ValueType getValueType() {
            return valueType;
        }

        /**
         * Is the important flag set.
         *
         * @return Flag indicating this is the most important clock.
         */
        public boolean isImportant() {
            return important;
        }

        /**
         * Is the limitsDrivingRange flag set.
         *
         * @return Flag indicating this clock will limit the driving range.
         */
        public boolean isLimitsDrivingRange() {
            return limitsDrivingRange;
        }

        /**
         * Get the duration seconds
         *
         * @return The duration seconds for the clock.
         */
        public Double getDurationSeconds() {
            return durationSeconds;
        }

        /**
         * The duration seconds for the clock.
         *
         * @param durationSeconds The duration seconds for the clock.
         */
        public void setDurationSeconds(Double durationSeconds) {
            this.durationSeconds = durationSeconds;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(label);
            dest.writeString(value);
            dest.writeString(valueType.name());
            dest.writeValue(important);
            dest.writeValue(limitsDrivingRange);
            dest.writeValue(durationSeconds);
        }

        private Clock(Parcel in) {
            label = in.readString();
            value = in.readString();
            valueType = ValueType.valueOf(in.readString());
            important = (Boolean) in.readValue(Boolean.class.getClassLoader());
            limitsDrivingRange = (Boolean) in.readValue(Boolean.class.getClassLoader());
            durationSeconds = (Double) in.readValue(Double.class.getClassLoader());
        }

        public static Creator<Clock> CREATOR = new Creator<Clock>() {
            public Clock createFromParcel(Parcel source) {
                return new Clock(source);
            }

            public Clock[] newArray(int size) {
                return new Clock[size];
            }
        };
    }
}
