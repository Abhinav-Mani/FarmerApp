package com.androlord.farmerapp.LanguageHelper;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.androlord.farmerapp.R;

public class SettingsFragment extends PreferenceFragment implements
        Preference.OnPreferenceClickListener {
    public static final String LANGUAGE_SETTING = "lang_setting";
    public static final int LANGUAGE_CHANGED = 1000;

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(R.string.settings);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String preferenceKey = preference != null ? preference.getKey() : "";

        if (preferenceKey.equals(getString(R.string.pref_key_language))) {
            return handleLanguagePreferenceClick();
        }

        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        findPreference(getString(R.string.pref_key_language))
                .setOnPreferenceClickListener(this);
    }

    private boolean handleLanguagePreferenceClick() {
        LanguageDialog languagesDialog = new LanguageDialog();
        languagesDialog.show(getFragmentManager(), "LanguagesDialogFragment");
        return true;
    }

}