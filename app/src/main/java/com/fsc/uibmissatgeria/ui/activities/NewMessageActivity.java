package com.fsc.uibmissatgeria.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.api.Server;
import com.fsc.uibmissatgeria.models.SubjectGroup;
import com.fsc.uibmissatgeria.models.Subject;


public class NewMessageActivity extends ActionBarActivity {

    Subject sbj;
    SubjectGroup gr;
    TextView subject;
    TextView group;
    EditText body;
    TextView numChar;
    Button button;
    int defaultColor;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();

        Long idSubject = i.getLongExtra(Constants.SUBJECT_OBJ, 0);
        System.out.println("test1");
        sbj = Subject.findById(Subject.class, idSubject);
        Long idSubjectGroup = i.getLongExtra(Constants.GROUP_OBJ, 0);
        System.out.println("test2");
        gr = SubjectGroup.findById(SubjectGroup.class, idSubjectGroup);

        setContentView(R.layout.activity_new_message);

        subject =(TextView)findViewById(R.id.new_message_subject);
        group =(TextView)findViewById(R.id.new_message_group);
        body =  (EditText)findViewById(R.id.new_message_text);
        numChar = (TextView)findViewById(R.id.new_message_chars);
        button = (Button) findViewById(R.id.new_message_button);

        numChar.setText(Constants.MAX_CHAR+"");
        defaultColor = numChar.getCurrentTextColor();

        subject.setText(sbj.getName());
        if (gr.getIdApi() != Constants.DEFAULT_GROUP_ID) {
            group.setText(gr.getName());
        } else {
            group.setText("GENERAL"); // TODO: TRANSLATE
        }

        body.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                int total = Constants.MAX_CHAR - s.toString().length();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_log_out) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void sendMessage(View view) {
        String bodyString = body.getText().toString();

        if (!bodyString.replaceAll("\\s+","").isEmpty()
                && bodyString.replaceAll("\\s+","")!=null
                && bodyString.length()<=Constants.MAX_CHAR) {
            pDialog = new ProgressDialog(this);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Sending message..."); //TODO: TRANSLATE
            pDialog.setCancelable(false);
            pDialog.setMax(100);
            SendMessageTask task = new SendMessageTask(
                    this,
                    sbj,
                    gr,
                    bodyString);
            task.execute();
        } else if (bodyString.length()>Constants.MAX_CHAR) {
            Constants.showToast(this, "MAX CHAR ERROR"); //TODO: TRANSLATE
        } else {
            Constants.showToast(this, "The message can't be empty"); //TODO: TRANSLATE
        }


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
