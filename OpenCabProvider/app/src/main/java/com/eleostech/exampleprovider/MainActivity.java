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

        binding.vehicleInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.username.getText().toString();
                if (username != null && username.length() > 0) {
                    login(username);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter username.", Toast.LENGTH_LONG).show();
                }
            }
        });

        binding.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        binding.switchDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchDriver();
            }
        });

        binding.broadcastEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                broadCastEvent();
            }
        });

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

        Spinner spinner = (Spinner) findViewById(R.id.broadcast_event_spinner);
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

        binding.identityProviderTokenTypeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateIdentityProviderTokenType();
            }
        });

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

        Preferences.setIdentityResponseToken(this, null);
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

        List<PackageInfo> packages = getPackageManager().getInstalledPackages(PackageManager.GET_RECEIVERS);
        search:
        for (PackageInfo pkg : packages) {
            if (pkg.receivers != null) {
                for (ActivityInfo activityInfo : pkg.receivers) {
                    if (activityInfo.name.endsWith(".IdentityChangedReceiver")) {
                        String packageName = pkg.packageName;
                        String className = activityInfo.name;
                        Log.d(LOG_TAG, "Broadcasting event to packageName: " + packageName + ", className: "+ className);
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(packageName, className));
                        intent.setAction(IdentityContract.ACTION_DRIVER_LOGIN);
                        getApplication().sendBroadcast(intent);
                    }
                }
            }
        }
    }

    private void switchDriver() {
        logout();
        login("OPENCAB-CO");
    }

    private void logout() {
        Preferences.clear(this);
        binding.loginContainer.setVisibility(View.VISIBLE);
        binding.logoutContainer.setVisibility(View.GONE);
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(PackageManager.GET_RECEIVERS);
        search:
        for (PackageInfo pkg : packages) {
            if (pkg.receivers != null) {
                for (ActivityInfo activityInfo : pkg.receivers) {
                    if (activityInfo.name.endsWith(".IdentityChangedReceiver")) {
                        String packageName = pkg.packageName;
                        String className = activityInfo.name;
                        Log.d(LOG_TAG, "Broadcasting event to packageName: " + packageName + ", className: "+ className);
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(packageName, className));
                        intent.setAction(IdentityContract.ACTION_DRIVER_LOGOUT);
                        getApplication().sendBroadcast(intent);
                    }
                }
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
        String event = binding.broadcastEventSpinner.getSelectedItem().toString();
        Log.d(LOG_TAG, "Broadcasting " + event + " event");
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(PackageManager.GET_RECEIVERS);
        switch (event) {
            case "ACTION_DRIVER_LOGOUT":
                for (PackageInfo pkg : packages) {
                    if (pkg.receivers != null) {
                        for (ActivityInfo activityInfo : pkg.receivers) {
                            if (activityInfo.name.endsWith(".IdentityChangedReceiver")) {
                                String packageName = pkg.packageName;
                                String className = activityInfo.name;
                                Log.d(LOG_TAG, "Broadcasting event to packageName: " + packageName + ", className: "+ className);
                                Intent intent = new Intent();
                                intent.setComponent(new ComponentName(packageName, className));
                                intent.setAction(IdentityContract.ACTION_DRIVER_LOGOUT);
                                getApplication().sendBroadcast(intent);
                            }
                        }
                    }
                }
                break;
            case "ACTION_DRIVER_LOGIN":
                for (PackageInfo pkg : packages) {
                    if (pkg.receivers != null) {
                        for (ActivityInfo activityInfo : pkg.receivers) {
                            if (activityInfo.name.endsWith(".IdentityChangedReceiver")) {
                                String packageName = pkg.packageName;
                                String className = activityInfo.name;
                                Log.d(LOG_TAG, "Broadcasting event to packageName: " + packageName + ", className: "+ className);
                                Intent intent = new Intent();
                                intent.setComponent(new ComponentName(packageName, className));
                                intent.setAction(IdentityContract.ACTION_DRIVER_LOGIN);
                                getApplication().sendBroadcast(intent);
                            }
                        }
                    }
                }
                break;
            case "ACTION_IDENTITY_INFORMATION_CHANGED":
                for (PackageInfo pkg : packages) {
                    if (pkg.receivers != null) {
                        for (ActivityInfo activityInfo : pkg.receivers) {
                            if (activityInfo.name.endsWith(".IdentityChangedReceiver")) {
                                String packageName = pkg.packageName;
                                String className = activityInfo.name;
                                Log.d(LOG_TAG, "Broadcasting event to packageName: " + packageName + ", className: "+ className);
                                Intent intent = new Intent();
                                intent.setComponent(new ComponentName(packageName, className));
                                intent.setAction(IdentityContract.ACTION_IDENTITY_INFORMATION_CHANGED);
                                getApplication().sendBroadcast(intent);
                            }
                        }
                    }
                }
                break;
            case "ACTION_VEHICLE_INFORMATION_CHANGED":
                for (PackageInfo pkg : packages) {
                    if (pkg.receivers != null) {
                        for (ActivityInfo activityInfo : pkg.receivers) {
                            if (activityInfo.name.endsWith(".VehicleInformationChangedReceiver")) {
                                String packageName = pkg.packageName;
                                String className = activityInfo.name;
                                Log.d(LOG_TAG, "Broadcasting event to packageName: " + packageName + ", className: "+ className);
                                Intent intent = new Intent();
                                intent.setComponent(new ComponentName(packageName, className));
                                intent.setAction(VehicleInformationContract.ACTION_VEHICLE_INFORMATION_CHANGED);
                                getApplication().sendBroadcast(intent);
                            }
                        }
                    }
                }
                break;
            default:
                // code block
        }
    }
}