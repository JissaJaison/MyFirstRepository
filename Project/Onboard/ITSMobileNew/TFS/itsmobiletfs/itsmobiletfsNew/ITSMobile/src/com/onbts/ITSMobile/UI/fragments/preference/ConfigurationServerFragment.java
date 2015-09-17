package com.onbts.ITSMobile.UI.fragments.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;

import com.onbts.ITSMobile.R;

public class ConfigurationServerFragment extends BaseSettingsPreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.configuration_server);
	getPreferenceManager().setSharedPreferencesMode(Context.MODE_MULTI_PROCESS);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	super.onSharedPreferenceChanged(sharedPreferences, key);
    }

    public void preferenceChanged(Preference pref) {

        if (pref == null)
            return;
        if (pref instanceof EditTextPreference) {

            EditTextPreference editTextPref = (EditTextPreference) pref;
            String text = editTextPref.getText();
            if (pref.getKey().equals("application_password")) {
                text="***";
            }
            pref.setSummary(text);

        }
    }
/*
    <?xml version="1.0" encoding="UTF-8"?>
    <PreferenceScreen xmlns:android="http://schemas.android.com/apk/res/android" >

    <EditTextPreference
    android:key="urlServer"
    android:defaultValue="http://67.215.180.182/ITSMobile/ITSMobileSyncService.svc"
    android:summary="@string/edittext1_summary"
    android:title="@string/url_server_title" >
    </EditTextPreference>

    </PreferenceScreen>
    */
}
