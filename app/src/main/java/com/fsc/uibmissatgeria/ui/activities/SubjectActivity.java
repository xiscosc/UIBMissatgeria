package com.fsc.uibmissatgeria.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
        TextView title = (TextView) findViewById(R.id.subject_name);
        recView = (RecyclerView) findViewById(R.id.list_groups_subject);
        title.setText(sbj.getName());
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
}
