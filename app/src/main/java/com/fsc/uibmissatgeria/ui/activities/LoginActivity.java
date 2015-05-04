package com.fsc.uibmissatgeria.ui.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.api.AccountUIB;
import com.fsc.uibmissatgeria.api.NotificationService;
import com.fsc.uibmissatgeria.api.ServerSettings;


public class LoginActivity extends ActionBarActivity {

    private EditText user;
    private EditText password;
    private String userString;
    private String passwordString;
    private LinearLayout loginLayout;
    private ProgressBar loadingBar;
    AccountUIB auib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        setContentView(R.layout.activity_login);
        user = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_password);
        loadingBar = (ProgressBar) findViewById(R.id.login_loading);
        loginLayout = (LinearLayout) findViewById(R.id.login_layout);
        auib = new AccountUIB(this);
    }



    public void logIn(View view) {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(this.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        userString = user.getText().toString().toUpperCase().replaceAll("\\s+","");
        passwordString = password.getText().toString().replaceAll("\\s+","");
        if ((userString!=null && !userString.isEmpty()) && (passwordString!=null && !passwordString.isEmpty())) {
            LogInTask lt = new LogInTask(this);
            lt.execute();
        } else {
            Constants.showToast(this, getResources().getString(R.string.error_empty));
        }
    }

    private void startPrincipalActivity(){
        auib.startNotificationService();
        Intent intent = new Intent(this, PrincipalActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        this.finish();
        startActivity(intent);
    }

    private class LogInTask extends AsyncTask<Void, Void, Boolean> {

        LoginActivity ctx;

        public LogInTask(LoginActivity ctx) {
            super();
            this.ctx = ctx;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = auib.initalLogin(ctx.userString, ctx.passwordString);
            if (result) (new ServerSettings(ctx)).loadSettings();
            return result;
        }

        @Override
        protected void onPostExecute(Boolean logged) {
            super.onPostExecute(logged);
            if (logged) {
                ctx.startPrincipalActivity();
            } else {
                loginLayout.setVisibility(View.VISIBLE);
                loadingBar.setVisibility(View.GONE);
                Constants.showToast(ctx, getResources().getString(R.string.auth_error));
            }

        }

        @Override
        protected void onPreExecute() {
            loginLayout.setVisibility(View.GONE);
            loadingBar.setVisibility(View.VISIBLE);
        }
    }

}
