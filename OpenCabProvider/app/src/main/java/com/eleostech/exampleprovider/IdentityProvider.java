package com.eleostech.exampleprovider;

import android.util.Log;

import org.opencabstandard.provider.AbstractIdentityProvider;
import org.opencabstandard.provider.IdentityContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class IdentityProvider extends AbstractIdentityProvider {
    private static final String LOG_TAG = IdentityProvider.class.getName();

    @Override
    public IdentityContract.LoginCredentials getLoginCredentials(String version) {
        Log.d(LOG_TAG, "getLoginCredentials()");

        IdentityContract.LoginCredentials creds = null;
        String username = Preferences.getUsername((getContext()));

        if (username != null && username.length() > 0) {
            creds = new IdentityContract.LoginCredentials();
            if (Preferences.getIdentityResponseAsJWT(getContext())) {
                creds.setToken(createJwt(username));
            } else {
                creds.setToken(Preferences.getIdentityResponseToken(getContext()));
            }

            creds.setProvider(getContext().getPackageName());
            creds.setAuthority(IdentityContract.AUTHORITY);
        }

        return creds;
    }

    @Override
    public ArrayList<IdentityContract.Driver> getActiveDrivers(String version) {
        Log.d(LOG_TAG, "getActiveDrivers()");
        return Preferences.getActiveDrivers(getContext());
    }

    private String createJwt(String username) {
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
