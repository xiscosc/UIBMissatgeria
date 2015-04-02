package com.fsc.uibmissatgeria.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.adapters.GroupAdapter;
import com.fsc.uibmissatgeria.objects.Group;
import com.fsc.uibmissatgeria.objects.Subject;

public class SubjectActivity extends ActionBarActivity {

    Subject sbj;
    ListView listView;
    GroupAdapter ga;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        sbj = (Subject) i.getParcelableExtra(Constants.SUBJECT_OBJ);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_subject);
        listView = (ListView) findViewById(R.id.list_groups_subject);
        TextView title = (TextView) findViewById(R.id.subject_name);
        title.setText(sbj.getName());
        createAdapter(sbj.getArrayGroups());

    }


    private void createAdapter(final Group[] groups) {
        ga = new GroupAdapter(this, groups);
        listView.setAdapter(ga);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startMessagesGroup(groups[position]);
            }
        });
    }


    private void startMessagesGroup(Group g) {
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
        Group g = null;
        Intent intent = new Intent(this, MessagesActivity.class);
        intent.putExtra(Constants.SUBJECT_OBJ, sbj);
        intent.putExtra(Constants.GROUP_OBJ, g);
        startActivity(intent);
    }
}
