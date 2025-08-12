package com.eleostech.exampleprovider;

import android.content.Context;
import android.util.Log;

import org.opencabstandard.provider.HOSContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HOSUtil {


    private static final String HOS_DURATION_CLOCK_LABEL = "HOS Test Time";

    private static final String LOG_TAG = HOSUtil.class.getCanonicalName();

    /**
     * If "Add team driver(s) is checked, this will retrieve number of drivers requested and return
     * an array of {@link HOSContract.HOSStatusV2}
     * @param context
     * @return
     */
    public static ArrayList<HOSContract.HOSStatusV2> getHOSStatusTeamV2(Context context) {
        ArrayList<HOSContract.HOSStatusV2> response = new ArrayList<>();
        if (Preferences.isIdentityProviderTeamDriverEnabled(context)) {
            for (int i = 0; i < Preferences.getTeamsDriversNumber(context); i++) {
                //this will return a Team driver with unique name
                response.add(getHOSStatusV2(context, true, "OPENCAB_TEAM_DRIVER_" + (i + 1)));
            }
        }
        return response;
    }

    /**
     * If "Add team driver(s) is checked, this will retrieve number of drivers requested and return
     * an array of {@link HOSContract.HOSTeamData}
     * @param context
     * @return
     */
    public static HOSContract.HOSTeamData getTeamHOSData(Context context) {
        ArrayList<HOSContract.HOSData> response = new ArrayList<>();
        if (Preferences.isIdentityProviderTeamDriverEnabled(context)) {
            for (int i = 0; i < Preferences.getTeamsDriversNumber(context); i++) {
                response.add(getHOSData(context, true, "OPENCAB_TEAM_DRIVER_" + (i + 1)));
            }
        }
        HOSContract.HOSTeamData data = new HOSContract.HOSTeamData();
        data.setTeamHosData(response);
        return data;
    }

    public static HOSContract.HOSData getHOSData(Context context) {
        return getHOSData(context, false, null);
    }


    public static HOSContract.HOSData getHOSData(Context context, boolean isTeamDriver, String userName) {
        long currentMillis = System.currentTimeMillis();
        Date currentMillisDate = new Date(currentMillis);

        String username = isTeamDriver ? userName : Preferences.getUsername(context);
        String duty = null;
        Date date = new Date();
        String label = null;
        boolean useDurationSeconds = false;
        boolean limit = false;
        switch (Preferences.getDutyStatus(context)) {
            case "d":
                duty = "D";
                date = addHoursToDate(currentMillisDate, 11);
                label = "Drive Time Remaining";
                limit = true;
                break;
            case "on":
                duty = "ON";
                date = addHoursToDate(currentMillisDate, 7);
                label = "On Duty Time Remaining";
                useDurationSeconds = true;
                break;
            case "off":
                duty = "OFF";
                date = addHoursToDate(currentMillisDate, 9);
                label = "Rest Time Remaining";
                break;
        }
        ArrayList<HOSContract.ClockData> clocks = new ArrayList<>();
        HOSContract.ClockData item1 = new HOSContract.ClockData();
        item1.setLabel("Duty Status");
        item1.setValueType(HOSContract.ClockData.ValueType.STRING);
        item1.setValue(duty);
        clocks.add(item1);
        HOSContract.ClockData item2 = new HOSContract.ClockData();
        item2.setLabel(label);
        item2.setValueType(HOSContract.ClockData.ValueType.COUNTDOWN);
        item2.setLimitsDrivingRange(limit);

        SimpleDateFormat format = new SimpleDateFormat(HOSProvider.DATE_FORMAT);
        item2.setValue(format.format(date));
        item2.setImportant(true);
        clocks.add(item2);

        HOSContract.ClockData item3 = new HOSContract.ClockData();
        item3.setLabel("User");
        item3.setValueType(HOSContract.ClockData.ValueType.STRING);
        item3.setValue(username);
        clocks.add(item3);

        HOSContract.ClockData item4 = new HOSContract.ClockData();
        item4.setLabel("Time since Rest");
        item4.setValueType(HOSContract.ClockData.ValueType.COUNTUP);
        Long time = new Date().getTime();
        Date midnight = new Date(time - time % (24 * 60 * 60 * 1000));
        item4.setValue(format.format(midnight));
        clocks.add(item4);

        HOSContract.ClockData item5 = new HOSContract.ClockData();
        item5.setLabel(HOS_DURATION_CLOCK_LABEL);
        item5.setValueType(HOSContract.ClockData.ValueType.STRING);
        long dateMillis = (date.getTime() - new Date().getTime()) / 1000;
        item5.setValue(String.format("%02d:%02d", dateMillis / 60 / 60, dateMillis / 60 % 60));
        item5.setDurationSeconds(new Double(dateMillis));
        item5.setLimitsDrivingRange(useDurationSeconds);
        clocks.add(item5);

        HOSContract.ClockData item6 = new HOSContract.ClockData();
        item6.setLabel("Today's Date");
        item6.setValueType(HOSContract.ClockData.ValueType.DATE);
        String strDate = format.format(new Date());
        item6.setValue(strDate);
        clocks.add(item6);

        HOSContract.HOSData hosData = new HOSContract.HOSData();
        hosData.setUsername(username);
        hosData.setClocks(clocks);
        if (Preferences.isManageAction(context)) {
            String logoutAction = Preferences.getToggleLogoutAction(context) ? "googlechrome://navigate?url=google.com" : "hos://com.eleostech.opencabprovider/hos";
            hosData.setManageAction("hos://com.eleostech.opencabprovider/hos");
            hosData.setLogoutAction(logoutAction);
        }
        if (Preferences.getToggleDelayHosResponse(context)) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }
        return hosData;
    }

    public static HOSContract.HOSStatusV2 getHOSStatusV2(Context context) {
        return getHOSStatusV2(context, false, null);
    }

    private static HOSContract.HOSStatusV2 getHOSStatusV2(Context context, boolean isTeamDriver, String userName) {
        long currentMillis = System.currentTimeMillis();
        Date currentMillisDate = new Date(currentMillis);

        String username = isTeamDriver ? userName : Preferences.getUsername(context);
        String duty = null;
        Date date = new Date();
        String label = null;
        boolean useDurationSeconds = false;
        boolean limit = false;
        switch (Preferences.getDutyStatus(context)) {
            case "d":
                duty = "D";
                date = addHoursToDate(currentMillisDate, 11);
                label = "Drive Time Remaining";
                limit = true;
                break;
            case "on":
                duty = "ON";
                date = addHoursToDate(currentMillisDate, 7);
                label = "On Duty Time Remaining";
                useDurationSeconds = true;
                break;
            case "off":
                duty = "OFF";
                date = addHoursToDate(currentMillisDate, 9);
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
        item2.setImportant(true);
        clocks.add(item2);

        HOSContract.ClockV2 item3 = new HOSContract.ClockV2();
        item3.setLabel("User");
        item3.setValueType(HOSContract.ClockV2.ValueType.STRING);
        item3.setValue(username);
        clocks.add(item3);

        HOSContract.ClockV2 item4 = new HOSContract.ClockV2();
        item4.setLabel("Time since Rest");
        item4.setValueType(HOSContract.ClockV2.ValueType.COUNTUP);
        Long time = new Date().getTime();
        Date midnight = new Date(time - time % (24 * 60 * 60 * 1000));
        item4.setValue(format.format(midnight));
        clocks.add(item4);

        HOSContract.ClockV2 item5 = new HOSContract.ClockV2();
        item5.setLabel(HOS_DURATION_CLOCK_LABEL);
        item5.setValueType(HOSContract.ClockV2.ValueType.STRING);
        long dateMillis = (date.getTime() - new Date().getTime()) / 1000;
        item5.setValue(String.format("%02d:%02d", dateMillis / 60 / 60, dateMillis / 60 % 60));
        item5.setDurationSeconds(new Double(dateMillis));
        item5.setLimitsDrivingRange(useDurationSeconds);
        clocks.add(item5);

        HOSContract.ClockV2 item6 = new HOSContract.ClockV2();
        item6.setLabel("Today's Date");
        item6.setValueType(HOSContract.ClockV2.ValueType.DATE);
        String strDate = format.format(new Date());
        item6.setValue(strDate);
        clocks.add(item6);

        HOSContract.HOSStatusV2 hosStatusV2 = new HOSContract.HOSStatusV2();
        hosStatusV2.setClocks(clocks);
        if (Preferences.isManageAction(context)) {
            String logoutAction = Preferences.getToggleLogoutAction(context) ? "googlechrome://navigate?url=google.com" : "hos://com.eleostech.opencabprovider/hos";
            hosStatusV2.setManageAction("hos://com.eleostech.opencabprovider/hos");
            hosStatusV2.setLogoutAction(logoutAction);
        }
        if (Preferences.getToggleDelayHosResponse(context)) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }
        return hosStatusV2;
    }

    public static HOSContract.HOSStatus getHOSStatus(Context context, boolean isTeamDriver) {
        long currentMillis = System.currentTimeMillis();
        Date currentMillisDate = new Date(currentMillis);

        String username = isTeamDriver ? "OPENCAB_TEAM_DRIVER" : Preferences.getUsername(context);
        String duty = null;
        Date date = new Date();
        String label = null;
        boolean limit = false;
        if (Preferences.getDutyStatus(context) != null) {
            switch (Preferences.getDutyStatus(context)) {
                case "d":
                    duty = "D";
                    date = addHoursToDate(currentMillisDate, 11);
                    label = "Drive Time Remaining";
                    limit = true;
                    break;
                case "on":
                    duty = "ON";
                    date = addHoursToDate(currentMillisDate, 7);
                    label = "On Duty Time Remaining";
                    break;
                case "off":
                    duty = "OFF";
                    date = addHoursToDate(currentMillisDate, 9);
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
        Long time = new Date().getTime();
        Date midnight = new Date(time - time % (24 * 60 * 60 * 1000));
        item4.setValue(format.format(midnight));
        clocks.add(item4);

        HOSContract.Clock item5 = new HOSContract.Clock();
        item5.setLabel("Today's Date");
        item5.setValueType(HOSContract.Clock.ValueType.DATE);
        String strDate = format.format(new Date());
        item5.setValue(strDate);
        clocks.add(item5);

        HOSContract.HOSStatus hosStatus = new HOSContract.HOSStatus();
        hosStatus.setClocks(clocks);
        if (Preferences.isManageAction(context)) {
            hosStatus.setManageAction("hos://com.eleostech.opencabprovider/hos");
        }
        if (Preferences.getToggleDelayHosResponse(context)) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }
        return hosStatus;
    }

    private static Date addHoursToDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int minutes = calendar.get(Calendar.MINUTE);
        calendar.add(Calendar.MINUTE, 60 - minutes);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }
}
