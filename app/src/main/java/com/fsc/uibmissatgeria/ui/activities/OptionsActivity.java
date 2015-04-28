package com.fsc.uibmissatgeria.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.api.AccountUIB;
import com.fsc.uibmissatgeria.models.ModelsManager;

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
        ModelsManager m = new ModelsManager(this);
        m.reloadData();
        Constants.showToast(getApplicationContext(), "Data cleaned"); //TODO: TRANSLATE
    }

    public void LogOut() {
        AccountUIB auib = new AccountUIB(this);
        auib.logOut();
        this.finish();
    }
}