package com.fsc.uibmissatgeria.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.fragments.MessagesFragment;


public class MessagesActivity extends ActionBarActivity {




    public int idCourse;
    public int idGroup;
    private String courseName;
    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Intent i = getIntent();
        courseName = i.getStringExtra(Constants.COURSE_NAME);
        idCourse = i.getIntExtra(Constants.COURSE_ID, 0);
        idGroup = i.getIntExtra(Constants.GROUP_ID, 0);
        groupName = i.getStringExtra(Constants.GROUP_NAME);

        setTitle(courseName);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_messages, new MessagesFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_messages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_messages_new) {
            newMessageAction();
            return true;
        }

        if (id == R.id.menu_messages_reload) {
            reloadMessages();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void newMessageAction() {
        Intent intent = new Intent(this, NewMessageActivity.class);
        intent.putExtra(Constants.GROUP_ID, idGroup);
        intent.putExtra(Constants.COURSE_NAME, courseName);
        intent.putExtra(Constants.GROUP_NAME, groupName);
        intent.putExtra(Constants.COURSE_ID, idCourse);
        startActivity(intent);
    }

    public void reloadMessages() {
        MessagesFragment fragment = (MessagesFragment) getSupportFragmentManager().findFragmentById(R.id.container_messages);
        fragment.loadMessages();
    }

}
