package com.fsc.uibmissatgeria.ui.activities;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.ui.adapters.GroupAdapter;
import com.fsc.uibmissatgeria.models.SubjectGroup;
import com.fsc.uibmissatgeria.models.Subject;

import java.util.List;

public class SubjectActivity extends AppCompatActivity {

    private Subject sbj;
    private GroupAdapter groupAdapter;
    private List<SubjectGroup> subjectGroups;
    private RecyclerView recView;
    private SubjectGroup defaultGroup;
    private View unRead;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        Long idSubject = i.getLongExtra(Constants.SUBJECT_OBJ, 0);
        sbj = Subject.findById(Subject.class, idSubject);
        subjectGroups = sbj.getGroups();
        setContentView(R.layout.activity_subject);

        defaultGroup = sbj.getDefaultGroup();
        unRead = findViewById(R.id.forum_circle_unread);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        recView = (RecyclerView) findViewById(R.id.list_groups_subject);

        toolbar.setTitle(sbj.getName());
        toolbar.setSubtitle(R.string.subject_subtitle);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        if (!defaultGroup.isRead()) unRead.setVisibility(View.VISIBLE);
        createAdapter();

    }


    private void createAdapter() {
        groupAdapter = new GroupAdapter(subjectGroups);
        groupAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubjectGroup g = subjectGroups.get(recView.getChildAdapterPosition(v));
                startMessagesActivity(g);
            }
        });
        recView.setAdapter(groupAdapter);
        recView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }


    private void startMessagesActivity(SubjectGroup g) {
        Intent intent = new Intent(this, MessagesActivity.class);
        intent.putExtra(Constants.SUBJECT_OBJ, sbj.getId());
        intent.putExtra(Constants.GROUP_OBJ, g.getId());
        startActivity(intent);
    }


    public void startMessages(View view) {
        startMessagesActivity(defaultGroup);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent i = getIntent();
        Long idSubject = i.getLongExtra(Constants.SUBJECT_OBJ, 0);
        sbj = Subject.findById(Subject.class, idSubject);
        subjectGroups = sbj.getGroups();
        defaultGroup = sbj.getDefaultGroup();
        if (!defaultGroup.isRead()) {
            unRead.setVisibility(View.VISIBLE);
        } else {
            unRead.setVisibility(View.GONE);
        }
        createAdapter();
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
}
