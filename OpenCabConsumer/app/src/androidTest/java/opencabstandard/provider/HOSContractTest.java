package opencabstandard.provider;

import static org.junit.Assert.*;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.eleostech.exampleconsumer.MainActivity;

import org.junit.Test;
import org.opencabstandard.provider.HOSContract;

public class HOSContractTest {
    private static final String LOG_TAG = MainActivity.class.getCanonicalName();

    @Test
    public void testClockWithDurationConvertoClockWithNoDuration() {
        HOSContract.Clock clockWithDuration = new HOSContract.Clock();
        clockWithDuration.setLabel("LABEL");
        clockWithDuration.setValue("VALUE");
        clockWithDuration.setValueType(HOSContract.Clock.ValueType.STRING);
        clockWithDuration.setDurationSeconds(10.0);

        // Obtain a Parcel object and write the parcelable object to it:
        Parcel parcel = Parcel.obtain();
        clockWithDuration.writeToParcel(parcel, 0);

        // After you're done with writing, you need to reset the parcel for reading:
        parcel.setDataPosition(0);

        // Reconstruct object from parcel and asserts:
        ClockWithNoDuration clockWithNoDuration = ClockWithNoDuration.CREATOR.createFromParcel(parcel);
        Log.d(LOG_TAG, "clock with duration: " + clockWithDuration);
        Log.d(LOG_TAG, "clock with no duration: " + clockWithNoDuration);
        assertNotNull(clockWithDuration.getDurationSeconds());
        assertNotNull(clockWithNoDuration);
        assertEquals(clockWithNoDuration.getLabel(), clockWithDuration.getLabel());
    }

    @Test
    public void testClockWithNoDurationConvertoClockWithDuration() {
        ClockWithNoDuration clockWithNoDuration = new ClockWithNoDuration();
        clockWithNoDuration.setLabel("LABEL");
        clockWithNoDuration.setValue("VALUE");
        clockWithNoDuration.setValueType(HOSContract.Clock.ValueType.STRING);

        // Obtain a Parcel object and write the parcelable object to it:
        Parcel parcel = Parcel.obtain();
        clockWithNoDuration.writeToParcel(parcel, 0);

        // After you're done with writing, you need to reset the parcel for reading:
        parcel.setDataPosition(0);

        // Reconstruct object from parcel and asserts:
        HOSContract.Clock clockWithDuration = HOSContract.Clock.CREATOR.createFromParcel(parcel);
        Log.d(LOG_TAG, "clock with duration: " + clockWithDuration);
        Log.d(LOG_TAG, "clock with no duration: " + clockWithNoDuration);
        assertNull(clockWithDuration.getDurationSeconds());
        assertNotNull(clockWithNoDuration);
        assertEquals(clockWithNoDuration.getLabel(), clockWithDuration.getLabel());
    }

    public static class ClockWithNoDuration implements Parcelable {
        private String label;
        private String value;
        private HOSContract.Clock.ValueType valueType;
        private boolean important;
        private boolean limitsDrivingRange;

        /**
         * Allowed types for valueType field.
         */
        public enum ValueType {

            /**
             * The value field will contain a simple string. It can be informational information
             * such as the driver username or something like "Adverse Weather Conditions".
             * <p>
             * You can also use a string for an ELD clock duration (like 4:32) if you want to ensure
             * your own specific rounding and formatting logic is used. In this case, providers SHOULD
             * provide the actual duration in seconds using the {@link durationSeconds} property. Consumers MUST
             * display the <code>value</code>, but MAY use {@link durationSeconds} for business logic other than
             * pure information display.
             */
            STRING("string"),

            /**
             * Indicates that the value field will contain a date in
             * <a href="https://xml2rfc.tools.ietf.org/public/rfc/html/rfc3339.html#anchor14">RFC3339</a>
             * format.  The date will appear formatted as "MM/dd/yyyy".  An example for this type of clock
             * could be the date of next required truck service.
             */
            DATE("date"),

            /**
             * Indicates that the value field will contain a date in
             * <a href="https://xml2rfc.tools.ietf.org/public/rfc/html/rfc3339.html#anchor14">RFC3339</a>
             * format that will be shown as a clock counting down to zero.  The counter will not go below zero.
             * An example for this type of clock could be the remaining available drive time.
             */
            COUNTUP("countup"),

            /**
             * Indicates that the value field will contain a date in
             * <a href="https://xml2rfc.tools.ietf.org/public/rfc/html/rfc3339.html#anchor14">RFC3339</a>
             * format that will be shown as a clock counting up from the provided date.  An example for this
             * type of clock could be the number of hours since last rest.
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

        public ClockWithNoDuration() {

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
         * The value of the clock.  The format of this field will depend on the {@link HOSContract.Clock}.valueType field.
         *
         * @param value The clock value.
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * The value type of the clock.  See {@link HOSContract.Clock.ValueType} for the possible types.
         *
         * @param valueType The valueType for the clock.
         */
        public void setValueType(HOSContract.Clock.ValueType valueType) {
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
         *
         * @return The value type for the clock.
         */
        public HOSContract.Clock.ValueType getValueType() {
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
        }

        private ClockWithNoDuration(Parcel in) {
            label = in.readString();
            value = in.readString();
            valueType = HOSContract.Clock.ValueType.valueOf(in.readString());
            important = (Boolean) in.readValue(Boolean.class.getClassLoader());
            limitsDrivingRange = (Boolean) in.readValue(Boolean.class.getClassLoader());
        }

        @Override
        public String toString() {
            return "Clock{" + "label='" + label + '\'' + ", value='" + value + '\'' + ", valueType=" + valueType + ", important=" + important + ", limitsDrivingRange=" + limitsDrivingRange + '}';
        }

        public static Creator<ClockWithNoDuration> CREATOR = new Creator<ClockWithNoDuration>() {
            public ClockWithNoDuration createFromParcel(Parcel source) {
                return new ClockWithNoDuration(source);
            }

            public ClockWithNoDuration[] newArray(int size) {
                return new ClockWithNoDuration[size];
            }
        };
    }
}