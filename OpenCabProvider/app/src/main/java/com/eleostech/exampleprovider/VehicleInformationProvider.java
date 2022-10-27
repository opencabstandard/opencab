package com.eleostech.exampleprovider;

import android.util.Log;

import org.opencabstandard.provider.AbstractVehicleInformationProvider;
import org.opencabstandard.provider.VehicleInformationContract;

public class VehicleInformationProvider extends AbstractVehicleInformationProvider {
    private static final String LOG_TAG = VehicleInformationProvider.class.getName();

    @Override
    public VehicleInformationContract.VehicleInformation getVehicleInformation(String version) {
        Log.d(LOG_TAG, "getVehicleInformation()");

        VehicleInformationContract.VehicleInformation vehicleInformation = new VehicleInformationContract.VehicleInformation();
        vehicleInformation.setVehicleId("Great Vehicle ID 1");
        vehicleInformation.setVin("QWERRTYUIOP12345");
        vehicleInformation.setInGear(true);
        return vehicleInformation;
    }
}
