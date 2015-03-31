package com.fsc.uibmissatgeria.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.fragments.MessagesFragment;
import com.fsc.uibmissatgeria.objects.Group;
import com.fsc.uibmissatgeria.objects.Subject;


public class MessagesActivity extends ActionBarActivity {




    public int idSubject;
    public int idGroup;
    private String subjectName;
    private String groupName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Intent i = getIntent();

        Subject sbj = (Subject) i.getParcelableExtra(Constants.SUBJECT_OBJ);
        Group gr = (Group) i.getParcelableExtra(Constants.GROUP_OBJ);



        subjectName = sbj.getName();
        idSubject = sbj.getId();
        idGroup = gr.getId();
        groupName = gr.getName();

        setTitle(subjectName);

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

        return super.onOptionsItemSelected(item);
    }


    public void newMessageAction() {
        Intent intent = new Intent(this, NewMessageActivity.class);
        intent.putExtra(Constants.GROUP_ID, idGroup);
        intent.putExtra(Constants.SUBJECT_NAME, subjectName);
        intent.putExtra(Constants.GROUP_NAME, groupName);
        intent.putExtra(Constants.SUBJECT_ID, idSubject);
        startActivity(intent);
    }


}
