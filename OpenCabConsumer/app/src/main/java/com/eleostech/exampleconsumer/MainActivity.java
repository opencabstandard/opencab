package com.eleostech.exampleconsumer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eleostech.exampleconsumer.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.opencabstandard.provider.VehicleInformationContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getCanonicalName();

    private ViewGroup loginContainer;
    private ViewGroup logoutContainer;
    private TextView vinView;
    private TextView idView;
    private TextView gearView;
    private TextView errorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate()");
        setContentView(R.layout.activity_main);

        loginContainer = findViewById(R.id.login_container);
        vinView = findViewById(R.id.vin);
        idView = findViewById(R.id.vehicleId);
        gearView = findViewById(R.id.gear);
        errorView = findViewById(R.id.error);

        Button login = findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         callProvider();
                                     }
                                 });

        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onEvent(VehicleInformationChangedEvent event) {
        Log.d(LOG_TAG, "onVehicleInformationChanged()");
        clear();
    }

    private void clear() {
        gearView.setText("");
        vinView.setText("");
        idView.setText("");
        errorView.setText("");
    }

    private  Date addHoursToDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    private void callProvider() {
        Log.d(LOG_TAG, "callProvider()");
        clear();
        ContentResolver resolver = getApplicationContext().getContentResolver();
        Uri authority = Uri.parse("content://" + VehicleInformationContract.AUTHORITY);
        Bundle result = null;
        try {
            result = resolver.call(authority, "getVehicleInformation", "0.2", null);
        } catch (Exception ex) {
            Log.i(LOG_TAG, "Error calling provider: ", ex);
            errorView.setText(ex.getMessage());
            return;
        }

        if (result != null) {
            Log.d(LOG_TAG, "Got result!");
            result.setClassLoader(VehicleInformationContract.class.getClassLoader());
            if (result.containsKey(VehicleInformationContract.KEY_VEHICLE_INFORMATION)) {
                VehicleInformationContract.VehicleInformation vinfo = result.getParcelable(VehicleInformationContract.KEY_VEHICLE_INFORMATION);
                Log.d(LOG_TAG, "Got VI");
                String vin = vinfo.getVin();
                vinView.setText("VIN: " + vin);
                idView.setText("Vehicle ID: " + vinfo.getVehicleId());
                gearView.setText("InGear: " + vinfo.isInGear());
            } else if (result.containsKey(VehicleInformationContract.KEY_ERROR)) {
                String error = result.getString(VehicleInformationContract.KEY_ERROR);
                Log.d(LOG_TAG, "Error: " + error);
                errorView.setText(error);
            }
        } else {
            Log.d(LOG_TAG, "Result from provider is null.");
            errorView.setText("Result from provider is null.");
        }
    }

}