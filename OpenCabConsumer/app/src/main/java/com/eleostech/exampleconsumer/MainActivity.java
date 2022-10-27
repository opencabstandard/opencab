package com.eleostech.exampleconsumer;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.eleostech.exampleconsumer.databinding.ActivityMainBinding;

import org.opencabstandard.provider.IdentityContract;
import org.opencabstandard.provider.VehicleInformationContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getCanonicalName();

    private ActivityMainBinding binding;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate()");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.vehicleInformationButton.setOnClickListener(v -> callVehicleInformationProvider());
        binding.identityProviderLoginCredentialsButton.setOnClickListener(v -> callIdentityProviderGetLoginCredentials());
        binding.identityActiveDriverButton.setOnClickListener(v -> callIdentityProviderGetActiveDrivers());
        EventBus.getDefault().register(this);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        binding.broadcastListView.setAdapter(adapter);
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
        logEvent(event.action);
    }

    public void onEvent(IdentityChangedEvent event) {
        Log.d(LOG_TAG, "IdentityChangedEvent()");
        clear();
        logEvent(event.action);
    }

    private void logEvent(String event) {
        SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String dateTime = s.format(new Date());
        adapter.add(dateTime + " : " + event);
    }

    private void clear() {
        binding.vin.setText("");
        binding.authority.setText("");
        binding.provider.setText("");
        binding.error.setText("");
        binding.gear.setText("");
        binding.token.setText("");
        binding.vehicleId.setText("");
        binding.identityActiveDriverName.setText("");
        binding.identityActiveDriverDriving.setText("");
        binding.errorActiveDrivers.setText("");
        binding.errorLoginCredentials.setText("");
    }

    private Date addHoursToDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    private void callVehicleInformationProvider() {
        Log.d(LOG_TAG, "callVehicleInformationProvider()");
        clear();
        ContentResolver resolver = getApplicationContext().getContentResolver();
        Uri authority = Uri.parse("content://" + VehicleInformationContract.AUTHORITY);
        Bundle result;
        try {
            result = resolver.call(authority, "getVehicleInformation", "0.2", null);
        } catch (Exception ex) {
            Log.i(LOG_TAG, "Error calling provider: ", ex);
            binding.error.setText(ex.getMessage());
            return;
        }

        if (result != null) {
            Log.d(LOG_TAG, "Got result!");
            result.setClassLoader(VehicleInformationContract.class.getClassLoader());
            if (result.containsKey(VehicleInformationContract.KEY_VEHICLE_INFORMATION)) {
                VehicleInformationContract.VehicleInformation vinfo = result.getParcelable(VehicleInformationContract.KEY_VEHICLE_INFORMATION);
                Log.d(LOG_TAG, "Got VI: " + vinfo.getVin());
                String vin = vinfo.getVin();
                binding.vin.setText("VIN: " + vin);
                binding.vehicleId.setText("Vehicle ID: " + vinfo.getVehicleId());
                binding.gear.setText("InGear: " + vinfo.isInGear());
            } else if (result.containsKey(VehicleInformationContract.KEY_ERROR)) {
                String error = result.getString(VehicleInformationContract.KEY_ERROR);
                Log.d(LOG_TAG, "Error: " + error);
                binding.error.setText(error);
            }
        } else {
            Log.d(LOG_TAG, "Result from provider is null.");
            binding.error.setText("Result from provider is null.");
        }
    }

    private void callIdentityProviderGetLoginCredentials() {
        Log.d(LOG_TAG, "callIdentityProviderGetLoginCredentials()");
        clear();
        ContentResolver resolver = getApplicationContext().getContentResolver();
        Uri authority = Uri.parse("content://" + IdentityContract.AUTHORITY);
        Bundle result;
        try {
            result = resolver.call(authority, "getLoginCredentials", "0.2", null);
        } catch (Exception ex) {
            Log.i(LOG_TAG, "Error calling provider: ", ex);
            binding.error.setText(ex.getMessage());
            return;
        }

        if (result != null) {
            Log.d(LOG_TAG, "Got result!");
            result.setClassLoader(IdentityContract.class.getClassLoader());
            if (result.containsKey(IdentityContract.KEY_LOGIN_CREDENTIALS)) {
                IdentityContract.LoginCredentials loginCredentials = result.getParcelable(IdentityContract.KEY_LOGIN_CREDENTIALS);
                Log.d(LOG_TAG, "Got token: " + loginCredentials);
                if (loginCredentials != null) {
                    String token = loginCredentials.getToken();
                    binding.token.setText("Token: " + token);
                    String provider = loginCredentials.getProvider();
                    binding.provider.setText("Provider: " + provider);
                    String authorityData = loginCredentials.getAuthority();
                    binding.authority.setText("Authority: " + authorityData);
                } else {
                    binding.errorLoginCredentials.setText("No login credentials found");
                }
            } else if (result.containsKey(IdentityContract.KEY_ERROR)) {
                String error = result.getString(IdentityContract.KEY_ERROR);
                Log.d(LOG_TAG, "Error: " + error);
                binding.errorLoginCredentials.setText(error);
            }
        } else {
            Log.d(LOG_TAG, "Result from provider is null.");
            binding.errorLoginCredentials.setText("Result from provider is null.");
        }
    }

    private void callIdentityProviderGetActiveDrivers() {
        Log.d(LOG_TAG, "callIdentityProviderGetActiveDrivers()");
        clear();
        ContentResolver resolver = getApplicationContext().getContentResolver();
        Uri authority = Uri.parse("content://" + IdentityContract.AUTHORITY);
        Bundle result;
        try {
            result = resolver.call(authority, "getActiveDrivers", "0.2", null);
        } catch (Exception ex) {
            Log.i(LOG_TAG, "Error calling provider: ", ex);
            binding.error.setText(ex.getMessage());
            return;
        }

        if (result != null) {
            Log.d(LOG_TAG, "Got result!");
            result.setClassLoader(IdentityContract.class.getClassLoader());
            if (result.containsKey(IdentityContract.KEY_ACTIVE_DRIVERS)) {
                List<IdentityContract.Driver> drivers = result.getParcelableArrayList(IdentityContract.KEY_ACTIVE_DRIVERS);
                Log.d(LOG_TAG, "Got drivers: " + drivers);
                if (drivers != null && drivers.size() > 0) {
                    IdentityContract.Driver driver = drivers.get(0);
                    binding.identityActiveDriverName.setText("Driver Name: " + driver.getUsername());
                    binding.identityActiveDriverDriving.setText("Is Driving: " + driver.isDriving());
                } else {
                    binding.errorActiveDrivers.setText("No active drivers found");
                }
            } else if (result.containsKey(IdentityContract.KEY_ERROR)) {
                String error = result.getString(IdentityContract.KEY_ERROR);
                Log.d(LOG_TAG, "Error: " + error);
                binding.error.setText(error);
            }
        } else {
            Log.d(LOG_TAG, "Result from provider is null.");
            binding.error.setText("Result from provider is null.");
        }
    }
}