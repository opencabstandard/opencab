package com.eleostech.exampleprovider;

import android.util.Log;

import org.opencabstandard.provider.AbstractIdentityProvider;
import org.opencabstandard.provider.IdentityContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class IdentityProvider extends AbstractIdentityProvider {
    private static final String LOG_TAG = IdentityProvider.class.getName();

    @Override
    public IdentityContract.LoginCredentials getLoginCredentials(String version) {
        Log.d(LOG_TAG, "getLoginCredentials()");

        IdentityContract.LoginCredentials creds = null;
        String username = Preferences.getUsername((getContext()));

        /* To make testing and development easy, the sample app can vend either a JWT
         * or any string entered into the UI as the identity token. However, keep in mind
         * that the OpenCab standard does not currently require any particular token format.
         *
         * It's the responsibility of providers to document the token format they use, how to
         * verify it, and the unique authority string that can be used to identify a specific format.
         */
        if (username != null && username.length() > 0) {
            creds = new IdentityContract.LoginCredentials();
            if (Preferences.getIdentityResponseAsJWT(getContext())) {
                creds.setToken(createJwt(username));
            } else {
                String response = null;
                String identityResponseToken = Preferences.getIdentityResponseToken(getContext());
                if (identityResponseToken != null && identityResponseToken.length() > 0) {
                    response = identityResponseToken;
                }
                creds.setToken(response);
            }

            creds.setProvider(getContext().getPackageName());
            creds.setAuthority(IdentityContract.AUTHORITY);
        }

        return creds;
    }

    @Override
    public ArrayList<IdentityContract.DriverSession> getAllLoginCredentials(String version) {
        Log.d(LOG_TAG, "getAllLoginCredentials()");

        ArrayList<IdentityContract.DriverSession> driverSessionList = new ArrayList<>();
        String username = Preferences.getUsername((getContext()));

        /* To make testing and development easy, the sample app can vend either a JWT
         * or any string entered into the UI as the identity token. However, keep in mind
         * that the OpenCab standard does not currently require any particular token format.
         *
         * It's the responsibility of providers to document the token format they use, how to
         * verify it, and the unique authority string that can be used to identify a specific format.
         */
        if (username != null && username.length() > 0) {
            IdentityContract.DriverSession driverSession = new IdentityContract.DriverSession();
            driverSession.setUsername(username);
            IdentityContract.LoginCredentials creds = new IdentityContract.LoginCredentials();
            if (Preferences.getIdentityResponseAsJWT(getContext())) {
                creds.setToken(createJwt(username));
            } else {
                String response = null;
                String identityResponseToken = Preferences.getIdentityResponseToken(getContext());
                if (identityResponseToken != null && identityResponseToken.length() > 0) {
                    response = identityResponseToken;
                }
                creds.setToken(response);
            }

            creds.setProvider(getContext().getPackageName());
            creds.setAuthority(IdentityContract.AUTHORITY);
            driverSession.setLoginCredentials(creds);
            driverSessionList.add(driverSession);
            if (Preferences.isIdentityProviderTeamDriverEnabled(getContext())) {
                IdentityContract.DriverSession driverSessionTeam = new IdentityContract.DriverSession();
                driverSessionTeam.setUsername("OPENCAB-TEAM-DRIVER");
                IdentityContract.LoginCredentials credsTeam = new IdentityContract.LoginCredentials();
                if (Preferences.getIdentityResponseAsJWT(getContext())) {
                    credsTeam.setToken(createJwt("OPENCAB-TEAM-DRIVER"));
                } else {
                    String response = null;
                    String identityResponseToken = Preferences.getIdentityResponseToken(getContext());
                    if (identityResponseToken != null && identityResponseToken.length() > 0) {
                        response = identityResponseToken;
                    }
                    creds.setToken(response);
                }

                credsTeam.setProvider(getContext().getPackageName());
                credsTeam.setAuthority(IdentityContract.AUTHORITY);
                driverSessionTeam.setLoginCredentials(credsTeam);
                driverSessionList.add(driverSessionTeam);
            }
        }

        return driverSessionList;
    }

    @Override
    public ArrayList<IdentityContract.Driver> getActiveDrivers(String version) {
        Log.d(LOG_TAG, "getActiveDrivers()");
        ArrayList<IdentityContract.Driver> activeDrivers = Preferences.getActiveDrivers(getContext());
        //Team driver support is only available on >= 0.3 versions
        if (version != null && Double.valueOf(version) > 0.2) {
            if (activeDrivers != null) {
                if (Preferences.isIdentityProviderTeamDriverEnabled(getContext())) {
                    //we should add team driver information here
                    IdentityContract.Driver teamDriver = new IdentityContract.Driver();
                    teamDriver.setDriving(false);
                    teamDriver.setUsername("OPENCAB-TEAM-DRIVER");
                    activeDrivers.add(teamDriver);
                }
            }
        }
        return activeDrivers;
    }

    private String createJwt(String username) {
        /*
        WARNING: This code generates an insecure, unsigned JWT for example purposes.

        OpenCab does not require the use of JWT for tokens. If you do choose to use JWT,
        you need to sign the token server-side, as a signing key cannot be safely
        distributed in a mobile app.
        */
        Claims claims = Jwts.claims();
        claims.put("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier", username);
        claims.put("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name", username);

        Date expire = addHoursToDate(new Date(), 730);
        String jwt = Jwts.builder().setClaims(claims).setExpiration(expire).compact();
        Log.d(LOG_TAG, "JWT: " + jwt);

        return jwt;
    }

    private Date addHoursToDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

}
