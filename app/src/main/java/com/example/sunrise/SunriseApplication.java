package com.example.sunrise;

import android.app.Application;

import com.google.android.material.color.DynamicColors;
import com.google.firebase.database.FirebaseDatabase;

/**
 * SunriseApplication sets up global stuff for the Sunrise app.
 * Mainly, it enables Firebase offline mode so the app works without internet.
 */
public class SunriseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (true) {
            DynamicColors.applyToActivitiesIfAvailable(this);

            setTheme(R.style.Theme_Sunrise_Dynamic);
        } else {
            setTheme(R.style.Theme_Sunrise);
        }
        /* Enable disk persistence, so app can be used offline  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
