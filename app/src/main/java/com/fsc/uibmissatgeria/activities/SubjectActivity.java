package com.fsc.uibmissatgeria.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.adapters.GroupAdapter;
import com.fsc.uibmissatgeria.objects.Group;
import com.fsc.uibmissatgeria.objects.Subject;

import java.util.ArrayList;

public class SubjectActivity extends ActionBarActivity {

    private Subject sbj;
    private GroupAdapter groupAdapter;
    private ArrayList<Group> groups;
    private RecyclerView recView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        sbj = i.getParcelableExtra(Constants.SUBJECT_OBJ);
        groups = sbj.getGroups();
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_subject);
        TextView title = (TextView) findViewById(R.id.subject_name);
        recView = (RecyclerView) findViewById(R.id.list_groups_subject);
        title.setText(sbj.getName());
        createAdapter();

    }


    private void createAdapter() {
        groupAdapter = new GroupAdapter(groups);
        groupAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Group g = groups.get(recView.getChildAdapterPosition(v));
                startMessagesActivity(g);
            }
        });
        recView.setAdapter(groupAdapter);
        recView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }


    private void startMessagesActivity(Group g) {
        Intent intent = new Intent(this, MessagesActivity.class);
        intent.putExtra(Constants.SUBJECT_OBJ, sbj);
        intent.putExtra(Constants.GROUP_OBJ, g);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_subject, menu);
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

    public void startMessages(View view) {
        startMessagesActivity(null);
    }
}