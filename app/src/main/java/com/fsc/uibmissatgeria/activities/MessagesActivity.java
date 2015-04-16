package com.fsc.uibmissatgeria.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.fragments.MessagesFragment;
import com.fsc.uibmissatgeria.models.SubjectGroup;
import com.fsc.uibmissatgeria.models.Subject;


public class MessagesActivity extends ActionBarActivity {
    public Subject sbj;
    public SubjectGroup gr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Intent i = getIntent();

        Long idSubject = i.getLongExtra(Constants.SUBJECT_OBJ, 0);
        sbj = Subject.findById(Subject.class, idSubject);
        Long idSubjectGroup = i.getLongExtra(Constants.GROUP_OBJ, 0);
        if (idSubjectGroup != 0) gr = SubjectGroup.findById(SubjectGroup.class, idSubjectGroup);

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
        intent.putExtra(Constants.SUBJECT_OBJ, sbj.getId());
        if(gr!=null) intent.putExtra(Constants.GROUP_OBJ, gr.getId());
        startActivity(intent);
    }


}
