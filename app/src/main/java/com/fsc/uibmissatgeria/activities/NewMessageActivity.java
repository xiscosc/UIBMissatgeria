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
import com.fsc.uibmissatgeria.objects.Group;
import com.fsc.uibmissatgeria.objects.Subject;


public class NewMessageActivity extends ActionBarActivity {

    Subject sbj;
    Group gr;
    TextView subject;
    TextView group;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();

        sbj = i.getParcelableExtra(Constants.SUBJECT_OBJ);
        gr = i.getParcelableExtra(Constants.GROUP_OBJ);

        setContentView(R.layout.activity_new_message);

        subject =(TextView)findViewById(R.id.new_message_subject);
        group =(TextView)findViewById(R.id.new_message_group);

        subject.setText(sbj.getName());
        if (gr!=null) {
            group.setText(gr.getName());
        } else {
            group.setText("GENERAL"); // TODO: TRANSLATE
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
                sbj,
                gr,
                body.getText().toString());
        task.execute();
    }

    private class SendMessageTask extends AsyncTask<Void, Void, Void> {

        String body;
        Subject subject;
        Group group;
        NewMessageActivity ctx;

        public SendMessageTask(NewMessageActivity ctx, Subject s, Group g, String body) {
            super();
            this.subject = s;
            this.group = g;
            this.body = body;
            this.ctx = ctx;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Server s = new Server(ctx);
            s.sendMessageToGroup(subject, group, body);
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
