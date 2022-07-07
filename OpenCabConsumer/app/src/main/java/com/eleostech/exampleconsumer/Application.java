package com.eleostech.exampleconsumer;


import android.content.IntentFilter;

import org.opencabstandard.provider.IdentityContract;
import org.opencabstandard.provider.VehicleInformationContract;

public class Application extends android.app.Application {

    private static final String LOG_TAG = Application.class.getCanonicalName();

    @Override
    public void onCreate() {
        super.onCreate();

        VehicleInformationChangedReceiver receiver = new VehicleInformationChangedReceiver();
        IntentFilter filter = new IntentFilter(VehicleInformationContract.ACTION_VEHICLE_INFORMATION_CHANGED);
        this.registerReceiver(receiver, filter);
    }
}
