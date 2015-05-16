package com.fsc.uibmissatgeria.ui.activities;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.api.AccountUIB;
import com.fsc.uibmissatgeria.managers.ModelManager;

/**
 * Created by xiscosastre on 28/4/15.
 */
public class OptionsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.app_settings);
        Preference reloadPref = findPreference("prefReset");
        Preference logoutPref = findPreference("prefLogout");
        ListPreference notificationPref = (ListPreference) findPreference("prefUpdateFrequency");

        notificationPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                AccountUIB accountUIB = new AccountUIB(getApplicationContext());
                accountUIB.startNotificationServiceFromSettings(Long.parseLong((String) newValue));
                return true;
            }

        });

        reloadPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                ResetData();
                return true;
            }
        });

        logoutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                LogOut();
                return true;
            }
        });
    }

    private void ResetData() {
        ModelManager m = new ModelManager(this);
        m.reloadData();
        Constants.showToast(getApplicationContext(), getResources().getString(R.string.data_cleaned));
    }

    public void LogOut() {
        AccountUIB auib = new AccountUIB(this);
        auib.logOut();
        this.finish();
    }
}