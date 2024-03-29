package com.eleostech.exampleconsumer;

import android.content.ContentResolver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.eleostech.exampleconsumer.databinding.ActivityMainBinding;
import com.google.gson.Gson;

import org.opencabstandard.provider.HOSContract;
import org.opencabstandard.provider.IdentityContract;
import org.opencabstandard.provider.VehicleInformationContract;
import org.opencabstandard.provider.Version;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getCanonicalName();

    private ActivityMainBinding binding;
    private ArrayAdapter<String> adapterHos;
    private ArrayAdapter<String> adapterBroadcastedEvents;
    private ArrayAdapter<String> adapterVehicleInformation;
    private ArrayAdapter<String> adapterLoginCredentials;
    private ArrayAdapter<String> adapterActiveDrivers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate()");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.hosButton.setOnClickListener(v -> callHOSProvider());
        binding.vehicleInformationButton.setOnClickListener(v -> callVehicleInformationProvider());
        binding.identityProviderLoginCredentialsButton.setOnClickListener(v -> callIdentityProviderGetLoginCredentials());
        binding.identityActiveDriverButton.setOnClickListener(v -> callIdentityProviderGetActiveDrivers());
        EventBus.getDefault().register(this);

        adapterHos = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        binding.hosListView.setAdapter(adapterHos);

        adapterBroadcastedEvents = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        binding.broadcastListView.setAdapter(adapterBroadcastedEvents);

        adapterVehicleInformation = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        binding.vehicleInformationListView.setAdapter(adapterVehicleInformation);

        adapterLoginCredentials = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        binding.loginCredentialsListView.setAdapter(adapterLoginCredentials);

        adapterActiveDrivers = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        binding.activeDriversListView.setAdapter(adapterActiveDrivers);
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
        logEvent(event.action);
    }

    public void onEvent(IdentityChangedEvent event) {
        Log.d(LOG_TAG, "IdentityChangedEvent()");
        logEvent(event.action);
    }

    private void logEvent(String event) {
        SimpleDateFormat s = new SimpleDateFormat("MM/dd hh:mm:ss");
        String dateTime = s.format(new Date());
        adapterBroadcastedEvents.insert(dateTime + " : " + event, 0);
    }


    private void callHOSProvider() {
        Log.d(LOG_TAG, "callHOSProvider()");
        // See section 4.3 of the specification, "Selecting and calling providers,"
        // for details about this enumeration process.
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
        adapterHos.clear();
        search:
        for (PackageInfo pkg : packages) {
            if (pkg.providers != null) {
                for (ProviderInfo provider : pkg.providers) {
                    if (provider.authority != null) {
                        if (provider.authority.endsWith(".org.opencabstandard.hos")) {
                            Gson gson = new Gson();
                            ContentResolver resolver = getApplicationContext().getContentResolver();
                            Uri authority = Uri.parse("content://" + provider.authority);
                            Bundle result;
                            SimpleDateFormat s = new SimpleDateFormat("MM/dd hh:mm:ss");
                            String dateTime = s.format(new Date());
                            try {
                                result = resolver.call(authority, HOSContract.METHOD_GET_HOS, HOSContract.VERSION, null);
                            } catch (Exception ex) {
                                Log.i(LOG_TAG, "Error calling provider: ", ex);
                                adapterHos.insert(dateTime + " : " + "Package: " + provider.packageName + ", Error: " + ex.getMessage(), 0);
                                return;
                            }

                            if (result != null) {
                                Log.d(LOG_TAG, "Got result!");
                                result.setClassLoader(HOSContract.class.getClassLoader());

                                Version supportedVersionV4 = new Version("0.4");
                                Version supportedVersionV3 = new Version("0.3");
                                Version supportedVersionV2 = new Version("0.2");
                                Version resultVersion = null;
                                if (result.containsKey(HOSContract.KEY_VERSION) && result.get(HOSContract.KEY_VERSION) != null) {
                                    resultVersion = new Version(result.getString(HOSContract.KEY_VERSION));
                                } else {
                                    resultVersion = supportedVersionV2;
                                }
                                //Support for version 0.2(before versioning was introduced)
                                if (resultVersion.compareTo(supportedVersionV2) == 0) {
                                    if (result.containsKey(HOSContract.KEY_HOS)) {
                                        HOSContract.HOSStatus hosStatus = result.getParcelable(HOSContract.KEY_HOS);
                                        if (hosStatus != null) {
                                            adapterHos.insert(dateTime + " : " + "Package: " + provider.packageName + ", Manage Action: " + hosStatus.getManageAction(), 0);
                                            for (HOSContract.Clock clock : hosStatus.getClocks()) {
                                                adapterHos.insert(dateTime + " : " + "Package: " + provider.packageName + ", Label: " + clock.getLabel() + ", Value: " + clock.getValue(), 0);
                                            }
                                        }
                                    } else if (result.containsKey(VehicleInformationContract.KEY_ERROR)) {
                                        String error = result.getString(VehicleInformationContract.KEY_ERROR);
                                        Log.d(LOG_TAG, "Error: " + error);
                                        adapterHos.insert(dateTime + " : " + "Package: " + provider.packageName + ", Error: " + error, 0);
                                    }
                                //Support for Version 0.3
                                } else if (resultVersion.compareTo(supportedVersionV3) == 0) {
                                    if (result.containsKey(HOSContract.KEY_HOS)) {
                                        HOSContract.HOSStatusV2 hosStatus = result.getParcelable(HOSContract.KEY_HOS);
                                        if (hosStatus != null) {
                                            adapterHos.insert(dateTime + " : " + "Package: " + provider.packageName + ", Manage Action: " + hosStatus.getManageAction() + ", Logout Action: " + hosStatus.getLogoutAction() + ", KEY_VERSION: " + (result.containsKey(HOSContract.KEY_VERSION) ? result.getString(HOSContract.KEY_VERSION) : "null"), 0);
                                            for (HOSContract.ClockV2 clock : hosStatus.getClocks()) {
                                                adapterHos.insert(dateTime + " : " + "Package: " + provider.packageName + ", Label: " + clock.getLabel() + ", Value: " + clock.getValue() + ", Duration: " + clock.getDurationSeconds(), 0);
                                            }
                                        }
                                    } else if (result.containsKey(VehicleInformationContract.KEY_ERROR)) {
                                        String error = result.getString(VehicleInformationContract.KEY_ERROR);
                                        Log.d(LOG_TAG, "Error: " + error);
                                        adapterHos.insert(dateTime + " : " + "Package: " + provider.packageName + ", Error: " + error, 0);
                                    }
                                    if (result.containsKey(HOSContract.KEY_TEAM_HOS)) {
                                        ArrayList<HOSContract.HOSStatusV2> hosStatusList = result.getParcelableArrayList(HOSContract.KEY_TEAM_HOS);
                                        if (hosStatusList != null && hosStatusList.size() > 0) {
                                            for (HOSContract.HOSStatusV2 item : hosStatusList) {
                                                if (item.getClocks() != null) {
                                                    for (HOSContract.ClockV2 clock : item.getClocks()) {
                                                        adapterHos.insert(dateTime + " : " + "Package: " + provider.packageName + ", Label: " + clock.getLabel() + ", Value: " + clock.getValue() + ", Duration: " + clock.getDurationSeconds(), 0);
                                                    }
                                                }
                                            }
                                        }
                                    } else if (result.containsKey(VehicleInformationContract.KEY_ERROR)) {
                                        String error = result.getString(VehicleInformationContract.KEY_ERROR);
                                        Log.d(LOG_TAG, "Error: " + error);
                                        adapterHos.insert(dateTime + " : " + "Package: " + provider.packageName + ", Error: " + error, 0);
                                    }
                                //Support for Version 0.4
                                } else if (resultVersion.compareTo(supportedVersionV4) == 0) {
                                    if (result.containsKey(HOSContract.KEY_HOS)) {
                                        HOSContract.HOSData hosStatus = gson.fromJson(result.getString(HOSContract.KEY_HOS), HOSContract.HOSData.class);
                                        if (hosStatus != null) {
                                            adapterHos.insert(dateTime + " : " + "Package: " + provider.packageName + ", Username: " + hosStatus.getUsername() +", Manage Action: " + hosStatus.getManageAction() + ", Logout Action: " + hosStatus.getLogoutAction() + ", KEY_VERSION: " + (result.containsKey(HOSContract.KEY_VERSION) ? result.getString(HOSContract.KEY_VERSION) : "null"), 0);
                                            for (HOSContract.ClockData clock : hosStatus.getClocks()) {
                                                adapterHos.insert(dateTime + " : " + "Package: " + provider.packageName + ", Label: " + clock.getLabel() + ", Value: " + clock.getValue() + ", Duration: " + clock.getDurationSeconds(), 0);
                                            }
                                        }
                                    } else if (result.containsKey(VehicleInformationContract.KEY_ERROR)) {
                                        String error = result.getString(VehicleInformationContract.KEY_ERROR);
                                        Log.d(LOG_TAG, "Error: " + error);
                                        adapterHos.insert(dateTime + " : " + "Package: " + provider.packageName + ", Error: " + error, 0);
                                    }
                                    if (result.containsKey(HOSContract.KEY_TEAM_HOS)) {
                                        HOSContract.HOSTeamData teamHOSData = gson.fromJson((result.getString(HOSContract.KEY_TEAM_HOS)), HOSContract.HOSTeamData.class);
                                        ArrayList<HOSContract.HOSData> hosStatusList = teamHOSData.getTeamHosData();
                                        if (hosStatusList != null && hosStatusList.size() > 0) {
                                            for (HOSContract.HOSData item : hosStatusList) {
                                                if (item.getClocks() != null) {
                                                    for (HOSContract.ClockData clock : item.getClocks()) {
                                                        adapterHos.insert(dateTime + " : " + "Package: " + provider.packageName + ", Label: " + clock.getLabel() + ", Value: " + clock.getValue() + ", Duration: " + clock.getDurationSeconds(), 0);
                                                    }
                                                }
                                            }
                                        }
                                    } else if (result.containsKey(VehicleInformationContract.KEY_ERROR)) {
                                        String error = result.getString(VehicleInformationContract.KEY_ERROR);
                                        Log.d(LOG_TAG, "Error: " + error);
                                        adapterHos.insert(dateTime + " : " + "Package: " + provider.packageName + ", Error: " + error, 0);
                                    }
                                }
                            } else {
                                Log.d(LOG_TAG, "Result from provider is null.");
                                adapterHos.insert(dateTime + " : " + "Package: " + provider.packageName + ", Error: Result from provider is null.", 0);
                            }
                        }
                    }
                }
            }
        }
    }

    private void callVehicleInformationProvider() {
        Log.d(LOG_TAG, "callVehicleInformationProvider()");
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
        search:
        for (PackageInfo pkg : packages) {
            if (pkg.providers != null) {
                for (ProviderInfo provider : pkg.providers) {
                    if (provider.authority != null) {
                        if (provider.authority.endsWith(".org.opencabstandard.vehicleinformation")) {
                            ContentResolver resolver = getApplicationContext().getContentResolver();
                            Uri authority = Uri.parse("content://" + provider.authority);
                            Bundle result;
                            SimpleDateFormat s = new SimpleDateFormat("MM/dd hh:mm:ss");
                            String dateTime = s.format(new Date());
                            try {
                                result = resolver.call(authority, VehicleInformationContract.METHOD_GET_VEHICLE_INFORMATION, VehicleInformationContract.VERSION, null);
                            } catch (Exception ex) {
                                Log.i(LOG_TAG, "Error calling provider: ", ex);
                                adapterVehicleInformation.insert(dateTime + " : " + "Package: " + provider.packageName + ", Error: " + ex.getMessage(), 0);
                                return;
                            }

                            if (result != null) {
                                Log.d(LOG_TAG, "Got result!");
                                result.setClassLoader(VehicleInformationContract.class.getClassLoader());
                                if (result.containsKey(VehicleInformationContract.KEY_VEHICLE_INFORMATION)) {
                                    VehicleInformationContract.VehicleInformation vinfo = result.getParcelable(VehicleInformationContract.KEY_VEHICLE_INFORMATION);
                                    adapterVehicleInformation.insert(dateTime + " : " + "Package: " + provider.packageName + ", VIN: " + vinfo.getVin() + ", Vehicle ID: " + vinfo.getVehicleId() + ", InGear: " + vinfo.isInGear() + ", KEY_VERSION: " + (result.containsKey(VehicleInformationContract.KEY_VERSION) ? result.getString(VehicleInformationContract.KEY_VERSION) : "null"), 0);
                                } else if (result.containsKey(VehicleInformationContract.KEY_ERROR)) {
                                    String error = result.getString(VehicleInformationContract.KEY_ERROR);
                                    Log.d(LOG_TAG, "Error: " + error);
                                    adapterVehicleInformation.insert(dateTime + " : " + "Package: " + provider.packageName + ", Error: " + error, 0);
                                }
                            } else {
                                Log.d(LOG_TAG, "Result from provider is null.");
                                adapterVehicleInformation.insert(dateTime + " : " + "Package: " + provider.packageName + ", Error: Result from provider is null.", 0);
                            }
                        }
                    }
                }
            }
        }
    }

    private void callIdentityProviderGetLoginCredentials() {
        Log.d(LOG_TAG, "callIdentityProviderGetLoginCredentials()");
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
        search:
        for (PackageInfo pkg : packages) {
            Log.d(LOG_TAG, pkg.packageName);
            if (pkg.providers != null) {
                for (ProviderInfo provider : pkg.providers) {
                    if (provider.authority != null) {
                        if (provider.authority.endsWith(".org.opencabstandard.identity")) {
                            ContentResolver resolver = getApplicationContext().getContentResolver();
                            Uri authority = Uri.parse("content://" + provider.authority);
                            Bundle result;
                            SimpleDateFormat s = new SimpleDateFormat("MM/dd hh:mm:ss");
                            String dateTime = s.format(new Date());
                            try {
                                result = resolver.call(authority, IdentityContract.METHOD_GET_LOGIN_CREDENTIALS, IdentityContract.VERSION, null);
                            } catch (Exception ex) {
                                Log.i(LOG_TAG, "Error calling provider: ", ex);
                                adapterLoginCredentials.insert(dateTime + " : " + "Package: " + provider.packageName + ", Error: " + ex.getMessage(), 0);
                                return;
                            }

                            if (result != null) {
                                Log.d(LOG_TAG, "Got result!");
                                result.setClassLoader(IdentityContract.class.getClassLoader());
                                if (result.containsKey(IdentityContract.KEY_LOGIN_CREDENTIALS) || result.containsKey(IdentityContract.KEY_ALL_LOGIN_CREDENTIALS)) {
                                    if (result.containsKey(IdentityContract.KEY_LOGIN_CREDENTIALS)) {
                                        IdentityContract.LoginCredentials loginCredentials = result.getParcelable(IdentityContract.KEY_LOGIN_CREDENTIALS);
                                        Log.d(LOG_TAG, "Found login credentials: " + loginCredentials);
                                        adapterLoginCredentials.insert(dateTime + " : " + "KEY_LOGIN_CREDENTIALS | Package: " + provider.packageName + ", Token: " + (loginCredentials != null ? loginCredentials.getToken() : "null") + ", Authority: " + (loginCredentials != null ? loginCredentials.getAuthority() : "null") + ", Provider: " + (loginCredentials != null ? loginCredentials.getProvider() : "null") + ", KEY_VERSION: " + (result.containsKey(IdentityContract.KEY_VERSION) ? result.getString(IdentityContract.KEY_VERSION) : "null"), 0);
                                    }
                                    if (result.containsKey(IdentityContract.KEY_ALL_LOGIN_CREDENTIALS)) {
                                        ArrayList<IdentityContract.DriverSession> driverSessionArrayList = result.getParcelableArrayList(IdentityContract.KEY_ALL_LOGIN_CREDENTIALS);
                                        Log.d(LOG_TAG, "Found driver sessions: " + driverSessionArrayList);
                                        if (driverSessionArrayList != null && driverSessionArrayList.size() > 0) {
                                            for (IdentityContract.DriverSession driverSession : driverSessionArrayList) {
                                                adapterLoginCredentials.insert(dateTime + " : " + "KEY_ALL_LOGIN_CREDENTIALS | Package: " + provider.packageName + ", Username: " + (driverSession != null ? driverSession.getUsername() : "null") + ", Token: " + (driverSession != null ? driverSession.getLoginCredentials().getToken() : "null") + ", Provider: " + ((driverSession != null && driverSession.getLoginCredentials() != null) ? driverSession.getLoginCredentials().getProvider() : "null"), 0);
                                            }
                                        } else {
                                            adapterLoginCredentials.insert(dateTime + " : " + "KEY_ALL_LOGIN_CREDENTIALS | Package: " + provider.packageName + ", Error: No login credentials found", 0);
                                        }
                                    }
                                } else if (result.containsKey(IdentityContract.KEY_ERROR)) {
                                    String error = result.getString(IdentityContract.KEY_ERROR);
                                    Log.d(LOG_TAG, "Error: " + error);
                                    adapterLoginCredentials.insert(dateTime + " : " + "KEY_ALL_LOGIN_CREDENTIALS | Package: " + provider.packageName + ", Error: " + error, 0);
                                }
                            } else {
                                Log.d(LOG_TAG, "Result from provider is null.");
                                adapterLoginCredentials.insert(dateTime + " : " + "Package: " + provider.packageName + ", Error: Result from provider is null", 0);
                            }
                        }
                    }
                }
            }
        }
    }

    private void callIdentityProviderGetActiveDrivers() {
        Log.d(LOG_TAG, "callIdentityProviderGetActiveDrivers()");
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
        search:
        for (PackageInfo pkg : packages) {
            if (pkg.providers != null) {
                for (ProviderInfo provider : pkg.providers) {
                    if (provider.authority != null) {
                        if (provider.authority.endsWith(".org.opencabstandard.identity")) {
                            ContentResolver resolver = getApplicationContext().getContentResolver();
                            Uri authority = Uri.parse("content://" + provider.authority);
                            Bundle result;
                            SimpleDateFormat s = new SimpleDateFormat("MM/dd hh:mm:ss");
                            String dateTime = s.format(new Date());
                            try {
                                result = resolver.call(authority, IdentityContract.METHOD_GET_ACTIVE_DRIVERS, IdentityContract.VERSION, null);
                            } catch (Exception ex) {
                                Log.i(LOG_TAG, "Error calling provider: ", ex);
                                adapterActiveDrivers.insert(dateTime + " : " + "Package: " + provider.packageName + ", Error: " + ex.getMessage(), 0);
                                return;
                            }

                            if (result != null) {
                                Log.d(LOG_TAG, "Got result!");
                                result.setClassLoader(IdentityContract.class.getClassLoader());
                                if (result.containsKey(IdentityContract.KEY_ACTIVE_DRIVERS)) {
                                    List<IdentityContract.Driver> drivers = result.getParcelableArrayList(IdentityContract.KEY_ACTIVE_DRIVERS);
                                    Log.d(LOG_TAG, "Got drivers: " + drivers);
                                    if (drivers != null && drivers.size() > 0) {
                                        for (IdentityContract.Driver driver : drivers) {
                                            adapterActiveDrivers.insert(dateTime + " : " + "Package: " + provider.packageName + ", Driver Name: " + driver.getUsername() + ", Is Driving: " + driver.isDriving(), 0);
                                        }
                                    } else {
                                        adapterActiveDrivers.insert(dateTime + " : " + "Package: " + provider.packageName + ", Error: No active drivers found", 0);

                                    }
                                } else if (result.containsKey(IdentityContract.KEY_ERROR)) {
                                    String error = result.getString(IdentityContract.KEY_ERROR);
                                    Log.d(LOG_TAG, "Error: " + error);
                                    adapterActiveDrivers.insert(dateTime + " : " + "Package: " + provider.packageName + ", Error: " + error, 0);
                                }
                            } else {
                                Log.d(LOG_TAG, "Result from provider is null.");
                                adapterActiveDrivers.insert(dateTime + " : " + "Package: " + provider.packageName + ", Error: Result from provider is null.", 0);
                            }
                        }
                    }
                }
            }
        }
    }
}
