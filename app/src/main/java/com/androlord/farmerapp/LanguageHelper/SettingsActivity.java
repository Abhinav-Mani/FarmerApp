package com.androlord.farmerapp.LanguageHelper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentManager;
import android.os.Bundle;

import com.androlord.farmerapp.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        FragmentManager fragmentManager = getFragmentManager();
        if (savedInstanceState == null) {
            SettingsFragment settingsFragment = new SettingsFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, settingsFragment)
                    .commit();
        }
    }
}
