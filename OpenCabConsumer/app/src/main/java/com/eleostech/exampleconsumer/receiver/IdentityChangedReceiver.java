package com.eleostech.exampleconsumer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.eleostech.exampleconsumer.IdentityChangedEvent;

import de.greenrobot.event.EventBus;

public class IdentityChangedReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = IdentityChangedReceiver.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG, "onReceiver()");
        EventBus.getDefault().post(new IdentityChangedEvent(intent.getAction()));
    }
}
