package com.fsc.uibmissatgeria.activities;


import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.api.AccountUIB;
import com.fsc.uibmissatgeria.models.User;


public class ProfileActivity extends ActionBarActivity {
    private TextView name;
    private TextView uibdigital;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        name = (TextView) findViewById(R.id.user_name_profile);
        uibdigital = (TextView) findViewById(R.id.uibdigital_profile);

        setProfile();
    }

    private void setProfile() {
        AccountUIB accountUIB = new AccountUIB(this);
        User usr = accountUIB.getUser();
        if (usr!=null) {
            name.setText(usr.getName());
            uibdigital.setText(usr.getUibDigitalUser());
        }

    }

}
