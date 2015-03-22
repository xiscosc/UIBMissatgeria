package com.fsc.uibmissatgeria.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.api.Server;


public class NewMessageActivity extends ActionBarActivity {

    public int idCourse;
    public int idGroup;
    private String courseName;
    private String groupName;

    TextView course;
    TextView group;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        courseName = i.getStringExtra(Constants.COURSE_NAME);
        idCourse = i.getIntExtra(Constants.COURSE_ID, 0);
        idGroup = i.getIntExtra(Constants.GROUP_ID, 0);
        groupName = i.getStringExtra(Constants.GROUP_NAME);

        setContentView(R.layout.activity_new_message);

        course =(TextView)findViewById(R.id.new_message_course);
        group =(TextView)findViewById(R.id.new_message_group);

        course.setText(courseName);
        group.setText(groupName);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void sendMessage(View view) {

        EditText body =  (EditText)findViewById(R.id.new_message_text);

        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Sending message..."); //TODO: TRANSLATE
        pDialog.setCancelable(false);
        pDialog.setMax(100);
        SendMessageTask task = new SendMessageTask(
                this,
                idCourse ,
                idGroup,
                body.getText().toString());
        task.execute();
    }

    private class SendMessageTask extends AsyncTask<Void, Void, Void> {

        String body;
        int idCourse;
        int idGroup;
        NewMessageActivity ctx;

        public SendMessageTask(NewMessageActivity ctx, int idC, int idG, String body) {
            super();
            this.idCourse = idC;
            this.idGroup = idG;
            this.body = body;
            this.ctx = ctx;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Server s = new Server();
            s.sendMessage(idGroup, idCourse, body);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
            ctx.finish();


        }

        @Override
        protected void onPreExecute() {
            pDialog.setProgress(0);
            pDialog.show();
        }
    }
}
