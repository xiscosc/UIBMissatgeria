package com.fsc.uibmissatgeria.ui.activities;

import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.api.Server;
import com.fsc.uibmissatgeria.api.ServerSettings;
import com.fsc.uibmissatgeria.models.SubjectGroup;
import com.fsc.uibmissatgeria.models.Subject;


public class NewMessageActivity extends ActionBarActivity {

    private Subject sbj;
    private SubjectGroup gr;
    private TextView subject;
    private TextView group;
    private EditText body;
    private TextView numChar;
    private Button button;
    private int defaultColor;
    private int max_char;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getAttrFromIntent();
        setContentView(R.layout.activity_new_message);

        subject =(TextView)findViewById(R.id.new_message_subject);
        group =(TextView)findViewById(R.id.new_message_group);
        body =  (EditText)findViewById(R.id.new_message_text);
        numChar = (TextView)findViewById(R.id.new_message_chars);
        button = (Button) findViewById(R.id.new_message_button);
        max_char = (new ServerSettings(this)).getMaxChar();

        numChar.setText(Integer.toString(max_char));
        defaultColor = numChar.getCurrentTextColor();

        subject.setText(sbj.getName());
        if (gr.getIdApi() != Constants.DEFAULT_GROUP_ID) {
            group.setText(gr.getName());
        } else {
            group.setText(getResources().getString(R.string.general));
        }

        body.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                int total = max_char - s.toString().length();
                if (total < 0 ) {
                    button.setEnabled(false);
                    numChar.setTextColor(Color.RED);
                } else {
                    numChar.setTextColor(defaultColor);
                    button.setEnabled(true);
                }
                numChar.setText(total+"");
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

    }


    private void getAttrFromIntent() {
        Intent i = getIntent();

        Long idSubject = i.getLongExtra(Constants.SUBJECT_OBJ, 0);
        System.out.println("test1");
        sbj = Subject.findById(Subject.class, idSubject);
        Long idSubjectGroup = i.getLongExtra(Constants.GROUP_OBJ, 0);
        System.out.println("test2");
        gr = SubjectGroup.findById(SubjectGroup.class, idSubjectGroup);
    }


    public void sendMessage(View view) {
        String bodyString = body.getText().toString();

        if (!bodyString.replaceAll("\\s+","").isEmpty()
                && bodyString.replaceAll("\\s+","")!=null
                && bodyString.length() <= max_char) {
            pDialog = new ProgressDialog(this);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage(getResources().getString(R.string.sending_message));
            pDialog.setCancelable(false);
            pDialog.setMax(100);
            SendMessageTask task = new SendMessageTask(
                    this,
                    sbj,
                    gr,
                    bodyString);
            task.execute();
        } else if (bodyString.length() > max_char) {
            Constants.showToast(this, getResources().getString(R.string.error_max_char));
        } else {
            Constants.showToast(this, getResources().getString(R.string.error_empty));
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                upIntent.putExtra(Constants.SUBJECT_OBJ, sbj.getId());
                upIntent.putExtra(Constants.GROUP_OBJ, gr.getId());
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

    private class SendMessageTask extends AsyncTask<Void, Void, Void> {

        String body;
        Subject subject;
        SubjectGroup subjectGroup;
        NewMessageActivity ctx;

        public SendMessageTask(NewMessageActivity ctx, Subject s, SubjectGroup g, String body) {
            super();
            this.subject = s;
            this.subjectGroup = g;
            this.body = body;
            this.ctx = ctx;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Server s = new Server(ctx);
            s.sendMessageToGroup(subject, subjectGroup, body);
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
