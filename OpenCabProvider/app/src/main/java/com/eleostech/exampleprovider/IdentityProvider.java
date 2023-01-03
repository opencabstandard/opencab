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
    public ArrayList<IdentityContract.Driver> getActiveDrivers(String version) {
        Log.d(LOG_TAG, "getActiveDrivers()");
        return Preferences.getActiveDrivers(getContext());
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
