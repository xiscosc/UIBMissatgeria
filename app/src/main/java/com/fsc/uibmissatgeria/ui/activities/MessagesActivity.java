package com.fsc.uibmissatgeria.ui.activities;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.ui.fragments.MessagesFragment;
import com.fsc.uibmissatgeria.models.SubjectGroup;
import com.fsc.uibmissatgeria.models.Subject;


public class MessagesActivity extends AppCompatActivity {
    public Subject sbj;
    public SubjectGroup gr;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);



        Intent i = getIntent();

        Long idSubject = i.getLongExtra(Constants.SUBJECT_OBJ, 0);
        sbj = Subject.findById(Subject.class, idSubject);
        Long idSubjectGroup = i.getLongExtra(Constants.GROUP_OBJ, 0);
        gr = SubjectGroup.findById(SubjectGroup.class, idSubjectGroup);

        String subname = getString(R.string.group)+" ";
        if (gr.getIdApi()!=Constants.DEFAULT_GROUP_ID) {
          subname +=gr.getName();
        } else {
            subname +=getString(R.string.general);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_messages, new MessagesFragment())
                    .commit();
        }

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(sbj.getName());
        toolbar.setSubtitle(subname);


        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        setSupportActionBar(toolbar);

        gr.setRead(true);
        gr.save();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                upIntent.putExtra(Constants.SUBJECT_OBJ, sbj.getId());
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                                    // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void newMessageAction() {
        Intent intent = new Intent(this, NewMessageActivity.class);
        intent.putExtra(Constants.SUBJECT_OBJ, sbj.getId());
        intent.putExtra(Constants.GROUP_OBJ, gr.getId());
        startActivity(intent);
    }


}
