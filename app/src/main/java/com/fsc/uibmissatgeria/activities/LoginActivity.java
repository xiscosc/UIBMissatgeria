package com.fsc.uibmissatgeria.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.api.AccountUIB;


public class LoginActivity extends ActionBarActivity {

    private ProgressDialog pDialog;
    private EditText user;
    private EditText password;
    private String userString;
    private String passwordString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        setContentView(R.layout.activity_login);
        user = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_password);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logIn(View view) {
        userString = user.getText().toString().toUpperCase().replaceAll("\\s+","");
        passwordString = password.getText().toString().replaceAll("\\s+","");
        if ((userString!=null && !userString.isEmpty()) && (passwordString!=null && !passwordString.isEmpty())) {
            pDialog = new ProgressDialog(this);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Please Wait..."); //TODO: TRANSLATE
            pDialog.setCancelable(false);
            pDialog.setMax(100);
            LogInTask lt = new LogInTask(this);
            lt.execute();
        } else {
            Constants.showToast(this, "There is an empty field"); //TODO: TRANSLATE
        }
    }

    private void startPrincipalActivity(){
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
            AccountUIB auib = new AccountUIB(ctx);
            return auib.initalLogin(ctx.userString, ctx.passwordString);
        }

        @Override
        protected void onPostExecute(Boolean logged) {
            super.onPostExecute(logged);
            pDialog.dismiss();
            if (logged) {
                ctx.startPrincipalActivity();
            } else {
                Constants.showToast(ctx, "Auth Error"); //TODO: TRANSLATE
            }

        }

        @Override
        protected void onPreExecute() {
            pDialog.setProgress(0);
            pDialog.show();
        }
    }

}
