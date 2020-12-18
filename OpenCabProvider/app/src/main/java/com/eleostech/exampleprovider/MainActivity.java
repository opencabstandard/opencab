package com.eleostech.exampleprovider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eleostech.opencabprovider.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.opencabstandard.provider.HOSContract;
import org.opencabstandard.provider.IdentityContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getCanonicalName();

    private ViewGroup loginContainer;
    private ViewGroup logoutContainer;
    private TextView welcomeText;
    private View statusDriving;
    private View statusOn;
    private View statusOff;
    private TextView navigationState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText usernameField = findViewById(R.id.username);
        welcomeText = findViewById(R.id.welcome_user);
        loginContainer = findViewById(R.id.login_container);
        logoutContainer = findViewById(R.id.logout_container);
        statusDriving = findViewById(R.id.status_driving);
        statusOn = findViewById(R.id.status_on);
        statusOff = findViewById(R.id.status_off);
        navigationState = findViewById(R.id.navigation_state);

        Button login = findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameField.getText().toString();
                if(username != null && username.length() > 0) {
                    login(username);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter username.", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button logout = findViewById(R.id.logout_button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        Button codriver = findViewById(R.id.switch_driver_button);
        codriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchDriver();
            }
        });

        String username = Preferences.getUsername(this);
        if(username != null) {
            logoutContainer.setVisibility(View.VISIBLE);
            loginContainer.setVisibility(View.GONE);
            welcomeText.setText("Welcome " + username);
        } else {
            loginContainer.setVisibility(View.VISIBLE);
            logoutContainer.setVisibility(View.GONE);
        }

        statusDriving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDutyStatus("driving");
            }
        });

        statusOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDutyStatus("on");
            }
        });

        statusOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDutyStatus("off");
            }
        });

        if(Preferences.getHOS(this) != null) {
            String status = null;
            String json = Preferences.getHOS(this);

            HOSContract.HOSStatus hstatus = HOSProvider.createGson().fromJson(json, HOSContract.HOSStatus.class);
            List<HOSContract.Clock> clocks = hstatus.getClocks();

            for(HOSContract.Clock cl: clocks) {
                if(cl.getLabel().equals("Duty Status")) {
                    status = cl.getValue();
                    break;
                }
            }

            Log.d(LOG_TAG, "Found status: " + status);
            switch(status) {
                case "D":
                    statusDriving.setBackgroundColor(getColor(R.color.status_active));
                    break;
                case "ON":
                    statusOn.setBackgroundColor(getColor(R.color.status_active));
                    break;
                case "OFF":
                    statusOff.setBackgroundColor(getColor(R.color.status_active));
                    break;

            }
        } else {
            setDutyStatus("off");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        boolean isNav = Preferences.getNavigationState(this);
        if(isNav) {
            navigationState.setText("NAVIGATING: TRUE");
        } else {
            navigationState.setText("NAVIGATING: FALSE");
        }
    }

    private void login(final String username) {
        Log.d(LOG_TAG, "login()");

        Preferences.setUsername(MainActivity.this, username);
        welcomeText.setText("Welcome " + username);
        loginContainer.setVisibility(View.GONE);
        logoutContainer.setVisibility(View.VISIBLE);
        setDutyStatus("off");
        addUser(username, true);

    }

    private void switchDriver() {
        logout();
        login("OPENCAB-CO");
    }

    private void logout() {
        Preferences.clear(this);

        loginContainer.setVisibility(View.VISIBLE);
        logoutContainer.setVisibility(View.GONE);
        Intent intent = new Intent();
        intent.setAction(IdentityContract.ACTION_DRIVER_LOGOUT);
        sendBroadcast(intent);
    }

    private void setDutyStatus(String status) {
        Log.d(LOG_TAG, "setDutyStatus(): " + status);
        statusDriving.setBackgroundColor(getColor(R.color.status_inactive));
        statusOn.setBackgroundColor(getColor(R.color.status_inactive));
        statusOff.setBackgroundColor(getColor(R.color.status_inactive));

        String duty = null;
        Date date = null;
        String label = null;
        boolean limit = false;
        switch(status) {
            case "driving":
                statusDriving.setBackgroundColor(getColor(R.color.status_active));
                duty = "D";
                date = addHoursToDate(new Date(), 12);
                label = "Drive Time Remaining";
                limit = true;
                break;
            case "on":
                statusOn.setBackgroundColor(getColor(R.color.status_active));
                duty = "ON";
                date = addHoursToDate(new Date(), 8);
                label = "On Duty Time Remaining";
                break;
            case "off":
                statusOff.setBackgroundColor(getColor(R.color.status_active));
                duty= "OFF";
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

    private  Date addHoursToDate(Date date, int hours) {
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
}