package com.eleostech.exampleprovider;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.eleostech.opencabprovider.R;
import com.eleostech.opencabprovider.databinding.ActivityMainBinding;

import org.opencabstandard.provider.IdentityContract;
import org.opencabstandard.provider.VehicleInformationContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getCanonicalName();

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Preferences.getDutyStatus(this) == null) {
            Preferences.setDutyStatus(this, "off");
        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.vehicleInformationButton.setOnClickListener(v -> {
            String username = binding.username.getText().toString();
            if (username != null && username.length() > 0) {
                login(username);
            } else {
                Toast.makeText(MainActivity.this, "Please enter username.", Toast.LENGTH_LONG).show();
            }
        });

        binding.logoutButton.setOnClickListener(v -> logout());

        binding.switchDriverButton.setOnClickListener(v -> switchDriver());

        binding.broadcastEventButton.setOnClickListener(view1 -> broadCastEvent());

        String username = Preferences.getUsername(this);
        if (username != null) {
            binding.logoutContainer.setVisibility(View.VISIBLE);
            binding.loginContainer.setVisibility(View.GONE);
            binding.welcomeUser.setText("Welcome " + username);
        } else {
            binding.loginContainer.setVisibility(View.VISIBLE);
            binding.logoutContainer.setVisibility(View.GONE);
        }

        binding.statusDriving.setOnClickListener(v -> setDutyStatus("d"));

        binding.statusOn.setOnClickListener(v -> setDutyStatus("on"));

        binding.statusOff.setOnClickListener(v -> setDutyStatus("off"));

        if (Preferences.getDutyStatus(this) != null) {
            String status;
            status = Preferences.getDutyStatus(this);

            setDutyStatus(status);

            Log.d(LOG_TAG, "Found status: " + status);
        } else {
            setDutyStatus("off");
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.broadcast_events, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.broadcastEventSpinner.setAdapter(adapter);


        ArrayAdapter<CharSequence> adapterHosVersion = ArrayAdapter.createFromResource(this, R.array.hos_versions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.hosClocksVersionSpinner.setAdapter(adapterHosVersion);

        binding.hosClocksVersionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Preferences.setHosVersion(getApplicationContext(), binding.hosClocksVersionSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.identityProviderTokenTypeSwitch.setOnClickListener(v -> updateIdentityProviderTokenType());

        binding.identityProviderTokenTextedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                saveToken(s.toString());
            }
        });

        binding.sendManageActionSwitch.setOnClickListener(v -> updateSendManageActionSwitch());

        binding.toggleLogoutActionSwitch.setOnClickListener(v -> updateToggleLogoutActionSwitch());

        binding.identityProviderTeamDriverSwitch.setOnClickListener(v -> updateIdentityProviderTeamDriver());
        Preferences.setIdentityResponseToken(this, null);
    }

    private void updateSendManageActionSwitch() {
        Preferences.setManageAction(this, binding.sendManageActionSwitch.isChecked());
    }

    private void updateToggleLogoutActionSwitch() {
        Preferences.setToggleLogoutAction(this, binding.toggleLogoutActionSwitch.isChecked());
    }

    private void saveToken(String text) {
        Preferences.setIdentityResponseToken(this, text);
    }

    private void updateIdentityProviderTokenType() {
        Preferences.setIdentityResponseJWT(this, binding.identityProviderTokenTypeSwitch.isChecked());
        if (binding.identityProviderTokenTypeSwitch.isChecked()) {
            binding.identityProviderTokenTextedit.setVisibility(View.GONE);
            Preferences.setIdentityResponseToken(this, "");
        } else {
            binding.identityProviderTokenTextedit.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        boolean isNav = Preferences.getNavigationState(this);
        if (isNav) {
            binding.navigationState.setText("NAVIGATING: TRUE");
        } else {
            binding.navigationState.setText("NAVIGATING: FALSE");
        }
        Preferences.setHosVersion(this, binding.hosClocksVersionSpinner.getSelectedItem().toString());
    }

    private void login(final String username) {
        Log.d(LOG_TAG, "login()");

        Preferences.setUsername(MainActivity.this, username);
        binding.welcomeUser.setText("Welcome " + username);
        binding.loginContainer.setVisibility(View.GONE);
        binding.logoutContainer.setVisibility(View.VISIBLE);
        setDutyStatus("off");
        addUser(username, false);

        HashMap<String, ActivityInfo> discoveredReceivers = getReceivers(IdentityContract.IDENTITY_CHANGED_RECEIVER);

        if (discoveredReceivers != null) {
            for (Map.Entry<String, ActivityInfo> entry : discoveredReceivers.entrySet()) {
                String key = entry.getKey();
                ActivityInfo value = entry.getValue();
                // If required by your use case or for security reasons, you can apply any
                // package name-based filtering by checking `value.packageName` against
                // a server-provided list. It is not recommended that you hard code any
                // package names in your mobile implementation directly.
                // See section 5, "Security," of the specification for more information.
                Intent intent1 = new Intent();
                intent1.setComponent(new ComponentName(value.packageName, key));
                intent1.setAction(IdentityContract.ACTION_IDENTITY_INFORMATION_CHANGED);
                getApplication().sendBroadcast(intent1);
                Intent intent2 = new Intent();
                intent2.setComponent(new ComponentName(value.packageName, key));
                intent2.setAction(IdentityContract.ACTION_DRIVER_LOGIN);
                getApplication().sendBroadcast(intent2);
            }
        }
    }

    private void switchDriver() {
        logout();
        login("OPENCAB-CO");
    }

    private void logout() {
        binding.identityProviderTeamDriverSwitch.setChecked(false);
        Preferences.clear(this);

        binding.loginContainer.setVisibility(View.VISIBLE);
        binding.logoutContainer.setVisibility(View.GONE);

        HashMap<String, ActivityInfo> discoveredReceivers = getReceivers(IdentityContract.IDENTITY_CHANGED_RECEIVER);

        if (discoveredReceivers != null) {
            for (Map.Entry<String, ActivityInfo> entry : discoveredReceivers.entrySet()) {
                String key = entry.getKey();
                ActivityInfo value = entry.getValue();
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(value.packageName, key));
                intent.setAction(IdentityContract.ACTION_DRIVER_LOGOUT);
                getApplication().sendBroadcast(intent);
            }
        }
    }

    private void setDutyStatus(String status) {
        Log.d(LOG_TAG, "setDutyStatus(): " + status);
        binding.statusDriving.setBackgroundColor(getColor(R.color.status_inactive));
        binding.statusOn.setBackgroundColor(getColor(R.color.status_inactive));
        binding.statusOff.setBackgroundColor(getColor(R.color.status_inactive));
        switch (status) {
            case "d":
                binding.statusDriving.setBackgroundColor(getColor(R.color.status_active));
                break;
            case "on":
                binding.statusOn.setBackgroundColor(getColor(R.color.status_active));
                break;
            case "off":
                binding.statusOff.setBackgroundColor(getColor(R.color.status_active));
                break;

        }
        Preferences.setDutyStatus(this, status);
    }

    private void addUser(String username, boolean driving) {
        IdentityContract.Driver driver = new IdentityContract.Driver();
        driver.setUsername(username);
        driver.setDriving(driving);

        ArrayList<IdentityContract.Driver> drivers = new ArrayList<>();
        drivers.add(driver);

        Preferences.setActiveDrivers(this, drivers);
    }

    private void broadCastEvent() {
        // See section 3.4, "Publishing broadcast intents," for specifics about
        // this process.
        String event = binding.broadcastEventSpinner.getSelectedItem().toString();
        Log.d(LOG_TAG, "Broadcasting " + event + " event");
        HashMap<String, ActivityInfo> discoveredReceivers;
        switch (event) {
            case "ACTION_DRIVER_LOGOUT":
                discoveredReceivers = getReceivers(IdentityContract.IDENTITY_CHANGED_RECEIVER);
                if (discoveredReceivers.size() > 0) {
                    for (Map.Entry<String, ActivityInfo> entry : discoveredReceivers.entrySet()) {
                        String key = entry.getKey();
                        ActivityInfo value = entry.getValue();
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(value.packageName, key));
                        intent.setAction(IdentityContract.ACTION_DRIVER_LOGOUT);
                        getApplication().sendBroadcast(intent);
                    }
                }
                break;
            case "ACTION_DRIVER_LOGIN":
                discoveredReceivers = getReceivers(IdentityContract.IDENTITY_CHANGED_RECEIVER);
                if (discoveredReceivers.size() > 0) {
                    for (Map.Entry<String, ActivityInfo> entry : discoveredReceivers.entrySet()) {
                        String key = entry.getKey();
                        ActivityInfo value = entry.getValue();
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(value.packageName, key));
                        intent.setAction(IdentityContract.ACTION_DRIVER_LOGIN);
                        getApplication().sendBroadcast(intent);
                    }
                }
                break;
            case "ACTION_IDENTITY_INFORMATION_CHANGED":
                discoveredReceivers = getReceivers(IdentityContract.IDENTITY_CHANGED_RECEIVER);
                if (discoveredReceivers.size() > 0) {
                    for (Map.Entry<String, ActivityInfo> entry : discoveredReceivers.entrySet()) {
                        String key = entry.getKey();
                        ActivityInfo value = entry.getValue();
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(value.packageName, key));
                        intent.setAction(IdentityContract.ACTION_IDENTITY_INFORMATION_CHANGED);
                        getApplication().sendBroadcast(intent);
                    }
                }
                break;
            case "ACTION_VEHICLE_INFORMATION_CHANGED":
                discoveredReceivers = getReceivers(VehicleInformationContract.VEHICLE_INFORMATION_CHANGED_RECEIVER);
                if (discoveredReceivers.size() > 0) {
                    for (Map.Entry<String, ActivityInfo> entry : discoveredReceivers.entrySet()) {
                        String key = entry.getKey();
                        ActivityInfo value = entry.getValue();
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(value.packageName, key));
                        intent.setAction(VehicleInformationContract.ACTION_VEHICLE_INFORMATION_CHANGED);
                        getApplication().sendBroadcast(intent);
                    }
                }
                break;
            default:
                // code block
        }
    }

    private HashMap<String, ActivityInfo> getReceivers(String type) {
        // See section 3.4, "Publishing broadcast intents", for specifics about this
        // enumeration process.
        List<PackageInfo> packages = getApplication().getPackageManager().getInstalledPackages(PackageManager.GET_RECEIVERS);
        HashMap<String, ActivityInfo> discoveredReceivers = new HashMap<>();
        if (packages != null) {
            for (PackageInfo packageInfo : packages) {
                if (packageInfo.receivers != null) {
                    for (ActivityInfo activityInfo : packageInfo.receivers) {
                        if (activityInfo.name.endsWith("." + type)) {
                            discoveredReceivers.put(activityInfo.name, activityInfo);
                        }

                    }
                }
            }
        }
        return discoveredReceivers;
    }

    private void updateIdentityProviderTeamDriver() {
        Preferences.setIdentityProviderTeamDriver(this, binding.identityProviderTeamDriverSwitch.isChecked());
    }
}
