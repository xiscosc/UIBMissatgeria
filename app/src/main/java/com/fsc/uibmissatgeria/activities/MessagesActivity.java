package com.fsc.uibmissatgeria.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.fragments.MessagesFragment;
import com.fsc.uibmissatgeria.models.Group;
import com.fsc.uibmissatgeria.models.Subject;


public class MessagesActivity extends ActionBarActivity {
    public Subject sbj;
    public Group gr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Intent i = getIntent();

        sbj = (Subject) i.getParcelableExtra(Constants.SUBJECT_OBJ);
        gr = (Group) i.getParcelableExtra(Constants.GROUP_OBJ);

        String name = sbj.getName();
        if (gr!=null) {
          name +=" "+gr.getName();
        }
        setTitle(name);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_messages, new MessagesFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }


    public void newMessageAction() {
        Intent intent = new Intent(this, NewMessageActivity.class);
        intent.putExtra(Constants.GROUP_OBJ, gr);
        intent.putExtra(Constants.SUBJECT_OBJ, sbj);
        startActivity(intent);
    }


}
