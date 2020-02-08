package com.androlord.farmerapp.LanguageHelper;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.androlord.farmerapp.MainActivity;

public class MainActivityLauncher extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateLocaleIfNeeded();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void updateLocaleIfNeeded() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        if (sharedPreferences.contains(SettingsFragment.LANGUAGE_SETTING)) {
            String locale = sharedPreferences.getString(
                    SettingsFragment.LANGUAGE_SETTING, "");
            Locale localeSetting = new Locale(locale);

            if (!localeSetting.equals(Locale.getDefault())) {
                Resources resources = getResources();
                Configuration conf = resources.getConfiguration();
                conf.locale = localeSetting;
                resources.updateConfiguration(conf,
                        resources.getDisplayMetrics());

                Intent refresh = new Intent(this, MainActivityLauncher.class);
                startActivity(refresh);
                finish();
            }
        }
    }
}