package com.fsc.uibmissatgeria;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.fsc.uibmissatgeria.api.NotificationService;
import com.fsc.uibmissatgeria.ui.activities.LoginActivity;
import com.fsc.uibmissatgeria.ui.activities.PrincipalActivity;
import com.fsc.uibmissatgeria.api.AccountUIB;


public class MainActivity extends ActionBarActivity {

    AccountUIB accountUIB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountUIB = new AccountUIB(this);
        setTitle("");
        //setContentView(R.layout.activity_main);
        if (accountUIB.isLogged()) {
            startPrincipalActivity();
        } else {
            startLoginView();
        }

    }

    public void startLoginView() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        this.finish();
        startActivity(intent);
    }

    private void startPrincipalActivity(){
        accountUIB.startNotificationService();
        Intent intent = new Intent(this, PrincipalActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        this.finish();
        startActivity(intent);
    }

}
