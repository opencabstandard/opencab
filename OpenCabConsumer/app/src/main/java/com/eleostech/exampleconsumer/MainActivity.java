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

import org.opencabstandard.provider.HOSContract;
import org.opencabstandard.provider.IdentityContract;
import org.opencabstandard.provider.VehicleInformationContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
        search:
        for (PackageInfo pkg : packages) {
            if (pkg.providers != null) {
                for (ProviderInfo provider : pkg.providers) {
                    if (provider.authority != null) {
                        if (provider.authority.endsWith(".org.opencabstandard.hos")) {
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
                                if (result.containsKey(HOSContract.KEY_HOS)) {
                                    HOSContract.HOSStatusV2 hosStatus = result.getParcelable(HOSContract.KEY_HOS);
                                    if (hosStatus != null) {
                                        adapterHos.insert(dateTime + " : " + "Package: " + provider.packageName + ", Manage Action: " + hosStatus.getManageAction() + ", Logout Action: " + hosStatus.getLogoutAction(), 0);
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
                                    HOSContract.HOSStatusV2 hosStatus = result.getParcelable(HOSContract.KEY_TEAM_HOS);
                                    if (hosStatus != null && hosStatus.getClocks() != null) {
                                        for (HOSContract.ClockV2 clock : hosStatus.getClocks()) {
                                            adapterHos.insert(dateTime + " : " + "Package: " + provider.packageName + ", Label: " + clock.getLabel() + ", Value: " + clock.getValue() + ", Duration: " + clock.getDurationSeconds(), 0);
                                        }
                                    }
                                } else if (result.containsKey(VehicleInformationContract.KEY_ERROR)) {
                                    String error = result.getString(VehicleInformationContract.KEY_ERROR);
                                    Log.d(LOG_TAG, "Error: " + error);
                                    adapterHos.insert(dateTime + " : " + "Package: " + provider.packageName + ", Error: " + error, 0);
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
                                result = resolver.call(authority, "getVehicleInformation", "0.2", null);
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
                                    adapterVehicleInformation.insert(dateTime + " : " + "Package: " + provider.packageName + ", VIN: " + vinfo.getVin() + ", Vehicle ID: " + vinfo.getVehicleId() + ", InGear: " + vinfo.isInGear(), 0);
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
                                result = resolver.call(authority, "getLoginCredentials", "0.2", null);
                            } catch (Exception ex) {
                                Log.i(LOG_TAG, "Error calling provider: ", ex);
                                adapterLoginCredentials.insert(dateTime + " : " + "Package: " + provider.packageName + ", Error: " + ex.getMessage(), 0);
                                return;
                            }

                            if (result != null) {
                                Log.d(LOG_TAG, "Got result!");
                                result.setClassLoader(IdentityContract.class.getClassLoader());
                                if (result.containsKey(IdentityContract.KEY_LOGIN_CREDENTIALS)) {
                                    IdentityContract.LoginCredentials loginCredentials = result.getParcelable(IdentityContract.KEY_LOGIN_CREDENTIALS);
                                    Log.d(LOG_TAG, "Got token: " + loginCredentials);
                                    if (loginCredentials != null) {
                                        adapterLoginCredentials.insert(dateTime + " : " + "Package: " + provider.packageName  + ", Token: " + loginCredentials.getToken() + ", Provider: " + loginCredentials.getProvider(), 0);
                                    } else {
                                        adapterLoginCredentials.insert(dateTime + " : " + "Package: " + provider.packageName + ", Error: No login credentials found", 0);
                                    }
                                } else if (result.containsKey(IdentityContract.KEY_ERROR)) {
                                    String error = result.getString(IdentityContract.KEY_ERROR);
                                    Log.d(LOG_TAG, "Error: " + error);
                                    adapterLoginCredentials.insert(dateTime + " : " + "Package: " + provider.packageName + ", Error: " + error, 0);
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
                                result = resolver.call(authority, "getActiveDrivers", "0.2", null);
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
                                        adapterActiveDrivers.insert(dateTime + " : " + "Package: " + provider.packageName + ", Driver Name: " + drivers.get(0).getUsername() + ", Is Driving: " + drivers.get(0).isDriving(), 0);
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