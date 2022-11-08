package com.eleostech.exampleprovider;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

public class OpenCabProviderService extends IntentService {

    public OpenCabProviderService() {
        super("OpenCabProviderService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // No-op
    }
}