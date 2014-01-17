package com.cardosos.flextimemonitor;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity 
	implements OnSharedPreferenceChangeListener {

	public static final String KEY_PREF_FLEX_MODE = "pref_flexMode";
	public static final String KEY_PREF_ABSENCE_FIX = "pref_absenceFix";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		// Setting the summary for ListPreference
		Preference flexModePref = findPreference(KEY_PREF_FLEX_MODE);
		// Set summary to be the user-description for the selected value
		flexModePref.setSummary(getPreferenceManager().getSharedPreferences().getString(KEY_PREF_FLEX_MODE, ""));
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
			.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
			.unregisterOnSharedPreferenceChangeListener(this);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(KEY_PREF_FLEX_MODE)){
			Preference flexModePref = findPreference(key);
			// Set summary to be the user-description for the selected value
			flexModePref.setSummary(sharedPreferences.getString(key, ""));
		}
	}
}
