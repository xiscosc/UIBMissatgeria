package com.fsc.uibmissatgeria.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.api.AccountUIB;
import com.fsc.uibmissatgeria.api.NotificationService;
import com.fsc.uibmissatgeria.api.ServerSettings;
import com.fsc.uibmissatgeria.managers.FileManager;
import com.fsc.uibmissatgeria.managers.ModelManager;
import com.fsc.uibmissatgeria.models.Avatar;

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
       ReloadTask reloadTask = new ReloadTask(this);
       reloadTask.execute();

    }

    public void LogOut() {
        NotificationService.cancelNotifications(this);
        FileManager.deleteAllFiles();
        AccountUIB auib = new AccountUIB(this);
        auib.logOut();
        this.finish();
    }

    private class ReloadTask extends AsyncTask<Void, Void, Void> {

        Context ctx;
        ProgressDialog pDialog;

        public ReloadTask(Context ctx) {
            super();
            this.ctx = ctx;
        }

        @Override
        protected Void doInBackground(Void... params) {
            NotificationService.cancelNotifications(ctx);
            FileManager.deleteAllFiles();
            ModelManager m = new ModelManager(ctx);
            m.reloadData();

            ServerSettings serverSettings = new ServerSettings(ctx);
            serverSettings.loadSettings();

            Avatar avatar = (new AccountUIB(ctx)).getUser().getAvatar();
            if (avatar!= null){
                if (avatar.downloadFromServer(ctx)) avatar.save();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void response) {

            pDialog.hide();
            Constants.showToast(getApplicationContext(), getResources().getString(R.string.data_cleaned));

        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ctx);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage(getResources().getString(R.string.please_wait));
            pDialog.setCancelable(false);
            pDialog.setMax(100);
            pDialog.setProgress(0);
            pDialog.show();
        }
    }
}