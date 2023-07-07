package com.eleostech.exampleprovider;

import android.content.Context;

import org.opencabstandard.provider.HOSContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HOSUtil {

    private static final String HOS_DURATION_CLOCK_LABEL = "HOS Test Time";

    public static HOSContract.HOSStatusV2 getHOSStatusV2(Context context, boolean isTeamDriver) {
        String username = isTeamDriver ? "OPENCAB_TEAM_DRIVER" : Preferences.getUsername(context);
        String duty = null;
        Date date = new Date();
        String label = null;
        boolean limit = false;
        switch (Preferences.getDutyStatus(context)) {
            case "d":
                duty = "D";
                date = addHoursToDate(new Date(), 12);
                label = "Drive Time Remaining";
                limit = true;
                break;
            case "on":
                duty = "ON";
                date = addHoursToDate(new Date(), 8);
                label = "On Duty Time Remaining";
                break;
            case "off":
                duty = "OFF";
                date = new Date();
                label = "Rest Time Remaining";
                break;
        }
        ArrayList<HOSContract.ClockV2> clocks = new ArrayList<>();
        HOSContract.ClockV2 item1 = new HOSContract.ClockV2();
        item1.setLabel("Duty Status");
        item1.setValueType(HOSContract.ClockV2.ValueType.STRING);
        item1.setValue(duty);
        clocks.add(item1);
        HOSContract.ClockV2 item2 = new HOSContract.ClockV2();
        item2.setLabel(label);
        item2.setValueType(HOSContract.ClockV2.ValueType.COUNTDOWN);
        item2.setLimitsDrivingRange(limit);

        SimpleDateFormat format = new SimpleDateFormat(HOSProvider.DATE_FORMAT);
        item2.setValue(format.format(date));
        clocks.add(item2);

        HOSContract.ClockV2 item3 = new HOSContract.ClockV2();
        item3.setLabel("User");
        item3.setValueType(HOSContract.ClockV2.ValueType.STRING);
        item3.setValue(username);
        clocks.add(item3);

        HOSContract.ClockV2 item4 = new HOSContract.ClockV2();
        item4.setLabel("Time since Rest");
        item4.setValueType(HOSContract.ClockV2.ValueType.COUNTUP);
        item4.setValue(format.format(new Date()));
        clocks.add(item4);

        HOSContract.ClockV2 item5 = new HOSContract.ClockV2();
        item5.setLabel(HOS_DURATION_CLOCK_LABEL);
        item5.setValueType(HOSContract.ClockV2.ValueType.STRING);
        item5.setValue("0:04");
        item5.setDurationSeconds(240.0);
        item5.setLimitsDrivingRange(true);
        clocks.add(item5);

        HOSContract.HOSStatusV2 hosStatusV2 = new HOSContract.HOSStatusV2();
        hosStatusV2.setClocks(clocks);
        hosStatusV2.setManageAction("hos://com.eleostech.opencabprovider/hos");
        hosStatusV2.setLogoutAction("hos://com.eleostech.opencabprovider/hos");
        return hosStatusV2;
    }

    public static HOSContract.HOSStatus getHOSStatus(Context context, boolean isTeamDriver) {
        String username = isTeamDriver ? "OPENCAB_TEAM_DRIVER" : Preferences.getUsername(context);
        String duty = null;
        Date date = new Date();
        String label = null;
        boolean limit = false;
        if (Preferences.getDutyStatus(context) != null) {
            switch (Preferences.getDutyStatus(context)) {
                case "d":
                    duty = "D";
                    date = addHoursToDate(new Date(), 12);
                    label = "Drive Time Remaining";
                    limit = true;
                    break;
                case "on":
                    duty = "ON";
                    date = addHoursToDate(new Date(), 8);
                    label = "On Duty Time Remaining";
                    break;
                case "off":
                    duty = "OFF";
                    date = new Date();
                    label = "Rest Time Remaining";
                    break;
            }
        }
        ArrayList<HOSContract.Clock> clocks = new ArrayList<>();
        HOSContract.Clock item1 = new HOSContract.Clock();
        item1.setLabel("Duty Status");
        item1.setValueType(HOSContract.Clock.ValueType.STRING);
        item1.setValue(duty);
        clocks.add(item1);
        HOSContract.Clock item2 = new HOSContract.Clock();
        item2.setLabel(label);
        item2.setValueType(HOSContract.Clock.ValueType.COUNTDOWN);
        item2.setLimitsDrivingRange(limit);

        SimpleDateFormat format = new SimpleDateFormat(HOSProvider.DATE_FORMAT);
        item2.setValue(format.format(date));
        clocks.add(item2);

        HOSContract.Clock item3 = new HOSContract.Clock();
        item3.setLabel("User");
        item3.setValueType(HOSContract.Clock.ValueType.STRING);
        item3.setValue(username);
        clocks.add(item3);

        HOSContract.Clock item4 = new HOSContract.Clock();
        item4.setLabel("Time since Rest");
        item4.setValueType(HOSContract.Clock.ValueType.COUNTUP);
        item4.setValue(format.format(new Date()));
        clocks.add(item4);

        HOSContract.HOSStatus hosStatus = new HOSContract.HOSStatus();
        hosStatus.setClocks(clocks);
        hosStatus.setManageAction("hos://com.eleostech.opencabprovider/hos");
        return hosStatus;
    }

    private static Date addHoursToDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }
}
