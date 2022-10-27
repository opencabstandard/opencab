package com.eleostech.exampleconsumer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import de.greenrobot.event.EventBus;

public class IdentityChangedReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = IdentityChangedReceiver.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG, "onReceiver()");

        EventBus.getDefault().post(new IdentityChangedEvent(intent.getAction()));
    }
}
