package com.fbartnitzek.tasteemall;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefsFragment())
                .commit();
    }


    public static class PrefsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

        private static final String LOG_TAG = PrefsFragment.class.getName();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.v(LOG_TAG, "onCreate, hashCode=" + this.hashCode() + ", " + "savedInstanceState = [" + savedInstanceState + "]");

            // make sure default values are applied (shared function to retrieve)

            addPreferencesFromResource(R.xml.pref_general);
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_type)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_user_name)));
        }

        /**
         * Attaches a listener so the summary is always updated with the preference value.
         * Also fires the listener once, to initialize the summary (so it shows up before the value
         * is changed.)
         */
        private void bindPreferenceSummaryToValue(Preference preference) {
//            Log.v(LOG_TAG, "bindPreferenceSummaryToValue, hashCode=" + this.hashCode() + ", " + "preference = [" + preference + "]");
            // Set the listener to watch for value changes.
            preference.setOnPreferenceChangeListener(this);

            // Trigger the listener immediately with the preference's
            // current value.
            setPreferenceSummary(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }


        private void setPreferenceSummary(Preference preference, Object value) {
//            Log.v(LOG_TAG, "setPreferenceSummary, hashCode=" + this.hashCode() + ", " + "preference = [" + preference + "], value = [" + value + "]");
            String stringValue = value.toString();
//            String key = preference.getKey();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list (since they have separate labels/values).
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            } else {
                // For other preferences, set the summary to the value's simple string representation.
                preference.setSummary(stringValue);
            }
//        SunshineSyncAdapter.syncImmediately(this);
        }

        @Override
        public void onResume() {
            Log.v(LOG_TAG, "onResume, hashCode=" + this.hashCode() + ", " + "");
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            sp.registerOnSharedPreferenceChangeListener(this);
            super.onResume();
        }

        @Override
        public void onPause() {
            Log.v(LOG_TAG, "onPause, hashCode=" + this.hashCode() + ", " + "");
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            sp.unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
//            return false;
            Log.v(LOG_TAG, "onPreferenceChange, hashCode=" + this.hashCode() + ", " + "preference = [" + preference + "], newValue = [" + newValue + "]");
            setPreferenceSummary(preference, newValue);
            return true;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // optionally init / reset stuff...
            Log.v(LOG_TAG, "onSharedPreferenceChanged, hashCode=" + this.hashCode() + ", " + "sharedPreferences = [" + sharedPreferences + "], key = [" + key + "]");
        }
    }




}
