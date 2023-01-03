package com.eleostech.exampleconsumer;

public class IdentityChangedEvent {
    public String action;

    public IdentityChangedEvent(String eventAction){
        action = eventAction;
    }
}
