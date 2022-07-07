package com.eleostech.exampleconsumer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import de.greenrobot.event.EventBus;

public class VehicleInformationChangedReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = VehicleInformationChangedReceiver.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG, "onReceiver()");

        EventBus.getDefault().post(new VehicleInformationChangedEvent());
    }
}
