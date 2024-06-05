package com.example.sunrise.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public class LanguageManager {
    private static final String LANGUAGE_PREF_KEY = "language_pref";
    private static final String DEFAULT_LANGUAGE = "en"; // Default language if preference is not set

    // Method to set the app's language preference
    public static void setLanguage(Context context, String languageCode) {
        SharedPreferences preferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        preferences.edit().putString(LANGUAGE_PREF_KEY, languageCode).apply();
    }

    // Method to get the app's language preference
    public static String getLanguage(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return preferences.getString(LANGUAGE_PREF_KEY, DEFAULT_LANGUAGE);
    }

    // Method to apply the selected language to the app's configuration
    public static void applyLanguage(Context context) {
        String languageCode = getLanguage(context);
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();

        configuration.setLocale(locale);

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
}
