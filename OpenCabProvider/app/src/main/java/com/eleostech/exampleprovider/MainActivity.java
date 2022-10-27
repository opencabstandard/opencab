package com.eleostech.exampleprovider;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.eleostech.opencabprovider.R;
import com.eleostech.opencabprovider.databinding.ActivityMainBinding;

import org.opencabstandard.provider.HOSContract;
import org.opencabstandard.provider.IdentityContract;
import org.opencabstandard.provider.VehicleInformationContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getCanonicalName();

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        binding.statusDriving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDutyStatus("driving");
            }
        });

        binding.statusOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDutyStatus("on");
            }
        });

        binding.statusOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDutyStatus("off");
            }
        });

        if (Preferences.getHOS(this) != null) {
            String status = null;
            String json = Preferences.getHOS(this);

            HOSContract.HOSStatus hstatus = HOSProvider.createGson().fromJson(json, HOSContract.HOSStatus.class);
            List<HOSContract.Clock> clocks = hstatus.getClocks();

            for (HOSContract.Clock cl : clocks) {
                if (cl.getLabel().equals("Duty Status")) {
                    status = cl.getValue();
                    break;
                }
            }

            Log.d(LOG_TAG, "Found status: " + status);
            switch (status) {
                case "D":
                    binding.statusDriving.setBackgroundColor(getColor(R.color.status_active));
                    break;
                case "ON":
                    binding.statusOn.setBackgroundColor(getColor(R.color.status_active));
                    break;
                case "OFF":
                    binding.statusOff.setBackgroundColor(getColor(R.color.status_active));
                    break;

            }
        } else {
            setDutyStatus("off");
        }

        Spinner spinner = (Spinner) findViewById(R.id.broadcast_event_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.broadcast_events, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        binding.identityProviderTokenTypeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateIdentityProviderTokenType();
            }
        });
    }

    private void updateIdentityProviderTokenType() {
        Preferences.setIdentityResponseJWT(this, binding.identityProviderTokenTypeSwitch.isChecked());
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
    }

    private void login(final String username) {
        Log.d(LOG_TAG, "login()");

        Preferences.setUsername(MainActivity.this, username);
        binding.welcomeUser.setText("Welcome " + username);
        binding.loginContainer.setVisibility(View.GONE);
        binding.logoutContainer.setVisibility(View.VISIBLE);
        setDutyStatus("off");
        addUser(username, false);

    }

    private void switchDriver() {
        logout();
        login("OPENCAB-CO");
    }

    private void logout() {
        Preferences.clear(this);

        binding.loginContainer.setVisibility(View.VISIBLE);
        binding.logoutContainer.setVisibility(View.GONE);
        Intent intent = new Intent();
        intent.setAction(IdentityContract.ACTION_DRIVER_LOGOUT);
        sendBroadcast(intent);
    }

    private void setDutyStatus(String status) {
        Log.d(LOG_TAG, "setDutyStatus(): " + status);
        binding.statusDriving.setBackgroundColor(getColor(R.color.status_inactive));
        binding.statusOn.setBackgroundColor(getColor(R.color.status_inactive));
        binding.statusOff.setBackgroundColor(getColor(R.color.status_inactive));

        String duty = null;
        Date date = null;
        String label = null;
        boolean limit = false;
        switch (status) {
            case "driving":
                binding.statusDriving.setBackgroundColor(getColor(R.color.status_active));
                duty = "D";
                date = addHoursToDate(new Date(), 12);
                label = "Drive Time Remaining";
                limit = true;
                break;
            case "on":
                binding.statusOn.setBackgroundColor(getColor(R.color.status_active));
                duty = "ON";
                date = addHoursToDate(new Date(), 8);
                label = "On Duty Time Remaining";
                break;
            case "off":
                binding.statusOff.setBackgroundColor(getColor(R.color.status_active));
                duty = "OFF";
                date = new Date();
                label = "Rest Time Remaining";
                break;
        }

        ArrayList<HOSContract.Clock> clocks = new ArrayList<>();
        HOSContract.Clock item1 = new HOSContract.Clock();
        item1.setLabel("Duty Status");
        item1.setValueType(HOSContract.Clock.ValueType.STRING);
        item1.setValue(duty);
        clocks.add(item1);
        HOSContract.Clock item2 = new HOSContract.Clock();
        item2.setLabel(label);
        item2.setValueType(HOSContract.Clock.ValueType.COUNTDOWN);
        item2.setLimitsDrivingRange(limit);

        SimpleDateFormat format = new SimpleDateFormat(HOSProvider.DATE_FORMAT);
        item2.setValue(format.format(date));
        clocks.add(item2);

        String username = Preferences.getUsername(this);
        HOSContract.Clock item3 = new HOSContract.Clock();
        item3.setLabel("User");
        item3.setValueType(HOSContract.Clock.ValueType.STRING);
        item3.setValue(username);
        clocks.add(item3);

        HOSContract.Clock item4 = new HOSContract.Clock();
        item4.setLabel("Time since Rest");
        item4.setValueType(HOSContract.Clock.ValueType.COUNTUP);
        item4.setValue(format.format(new Date()));
        clocks.add(item4);

        HOSContract.HOSStatus hosStatus = new HOSContract.HOSStatus();
        hosStatus.setClocks(clocks);
        hosStatus.setManageAction("hos://com.eleostech.opencabprovider/hos");

        String json = HOSProvider.createGson().toJson(hosStatus);
        Preferences.setHOS(this, json);
    }

    private Date addHoursToDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
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
        Intent intent = new Intent();
        String packageName = "com.eleostech.exampleconsumer";
        String className = null;
        String event = binding.broadcastEventSpinner.getSelectedItem().toString();
        Log.d(LOG_TAG, "Broadcasting " + event + " event");
        String action = null;
        switch (event) {
            case "ACTION_DRIVER_LOGOUT":
                className = "com.eleostech.exampleconsumer.IdentityChangedReceiver";
                action = IdentityContract.ACTION_DRIVER_LOGOUT;
                break;
            case "ACTION_DRIVER_LOGIN":
                className = "com.eleostech.exampleconsumer.IdentityChangedReceiver";
                action = IdentityContract.ACTION_DRIVER_LOGIN;
                break;
            case "ACTION_IDENTITY_INFORMATION_CHANGED":
                className = "com.eleostech.exampleconsumer.IdentityChangedReceiver";
                action = IdentityContract.ACTION_IDENTITY_INFORMATION_CHANGED;
                break;
            case "ACTION_VEHICLE_INFORMATION_CHANGED":
                className = "com.eleostech.exampleconsumer.VehicleInformationChangedReceiver";
                action = VehicleInformationContract.ACTION_VEHICLE_INFORMATION_CHANGED;
                break;
            default:
                // code block
        }
        intent.setComponent(new ComponentName(packageName, className));
        intent.setAction(action);
        getApplication().sendBroadcast(intent);
    }
}