package com.eleostech.exampleconsumer;

public class VehicleInformationChangedEvent {

    public String action;

    public VehicleInformationChangedEvent(String eventAction){
        action = eventAction;
    }
}
