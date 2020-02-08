package com.androlord.farmerapp.LanguageHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.androlord.farmerapp.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LanguageDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        final Map<String, String> localeMap = new HashMap<String, String>();
        String[] availableLocales = getResources().getStringArray(
                R.array.languages);
        final String[] values = new String[availableLocales.length + 1];

        for (int i = 0; i < availableLocales.length; ++i) {
            String localString = availableLocales[i];
            if (localString.contains("-")) {
                localString = localString.substring(0,
                        localString.indexOf("-"));
            }
            Locale locale = new Locale(localString);
            values[i + 1]= locale.getDisplayLanguage() + " ("
                    + availableLocales[i]+ ")";
            localeMap.put(values[i + 1], availableLocales[i]);
        }
        values[0] = getActivity().getString(R.string.device) + " ("
                + Locale.getDefault().getLanguage() + ")";
        localeMap.put(values[0], Locale.getDefault().getLanguage());
        Arrays.sort(values, 1, values.length);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
                getActivity());
        dialogBuilder.setTitle(getString(R.string.language));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        ListView listView = new ListView(getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                String localString = localeMap.get(values[position]);
                if (localString.contains("-")) {
                    conf.locale = new Locale(localString.substring(0,
                            localString.indexOf("-")), localString.substring(
                            localString.indexOf("-") + 1, localString.length()));
                } else {
                    conf.locale = new Locale(localString);
                }
                res.updateConfiguration(conf, dm);
                settings.edit()
                        .putString(SettingsFragment.LANGUAGE_SETTING, localString).apply();

                // Refresh the app
                Intent refresh = new Intent(getActivity(), getActivity()
                        .getClass());
                startActivity(refresh);
                getActivity().setResult(SettingsFragment.LANGUAGE_CHANGED);
                getActivity().finish();

            }
        });

        dialogBuilder.setView(listView);
        dialogBuilder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return dialogBuilder.create();
    }

}
