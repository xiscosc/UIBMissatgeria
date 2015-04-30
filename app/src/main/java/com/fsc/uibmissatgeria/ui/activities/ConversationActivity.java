package com.fsc.uibmissatgeria.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.api.Server;
import com.fsc.uibmissatgeria.models.Conversation;
import com.fsc.uibmissatgeria.models.MessageConversation;
import com.fsc.uibmissatgeria.models.ModelsManager;
import com.fsc.uibmissatgeria.ui.adapters.MessageConversationAdapter;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ConversationActivity extends ActionBarActivity {

    private Conversation conversation;
    private MessageConversationAdapter messageAdapter;
    private List<MessageConversation> messages;
    private RecyclerView recView;
    private EditText editText;
    private ProgressBar loadingBar_new;
    private ProgressBar loadingBar_older;
    private ModelsManager mm;
    private Boolean olderAvaiable;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        Long id = i.getLongExtra(Constants.CONVERSATION_OBJ, 0);
        conversation = Conversation.findById(Conversation.class, id);
        setTitle(conversation.getPeerName());
        setContentView(R.layout.activity_conversation);
        editText = (EditText) findViewById(R.id.conversation_text);
        recView = (RecyclerView) findViewById(R.id.conversation_list);

        recView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

        recView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = ((LinearLayoutManager)recView.getLayoutManager());
                if (olderAvaiable && layoutManager.findFirstVisibleItemPosition() == 0) {
                    olderAvaiable = false;
                    getOlderMessages();
                }

            }
        });

        mm = new ModelsManager(this);
        loadingBar_new = (ProgressBar) findViewById(R.id.conversation_loading_new);
        loadingBar_older = (ProgressBar) findViewById(R.id.conversation_loading_older);
        olderAvaiable = false;
        loadMessages();
    }

    private void createAdapter() {
        messageAdapter = new MessageConversationAdapter(messages, this);
        recView.setAdapter(messageAdapter);
    }

    private void loadMessages() {
        ConversationTask task = new ConversationTask(this);
        task.execute();
    }

    private void updateMessages() {
        NewMessagesTask task = new NewMessagesTask(this);
        task.execute();
    }

    private void getOlderMessages() {
        OlderMessagesTask task = new OlderMessagesTask(this);
        task.execute();
    }

    public void sendMessage(View view) {
        String bodyString = editText.getText().toString();

        if (!bodyString.replaceAll("\\s+","").isEmpty()
                && bodyString.replaceAll("\\s+","")!=null
                && bodyString.length()<=Constants.MAX_CHAR) {
            SendMessageTask task = new SendMessageTask(
                    this,
                    bodyString);
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            task.execute();
        } else if (bodyString.length()>Constants.MAX_CHAR) {
            Constants.showToast(this, "MAX CHAR ERROR"); //TODO: TRANSLATE
        } else {
            Constants.showToast(this, "The message can't be empty"); //TODO: TRANSLATE
        }


    }

    private void startTimeReload() {
        final ConversationActivity self = this;
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        new NewMessagesTask(self).execute();
                    }
                });
            }
        };
        timer.schedule(task, 0, 25000);
    }

    private class ConversationTask extends AsyncTask<Void, Void, List<MessageConversation>> {

        private ConversationActivity ctx;


        public ConversationTask(ConversationActivity c) {
            super();
            ctx = c;

        }

        @Override
        protected List<MessageConversation> doInBackground(Void... params) {
            return ctx.mm.getMessagesConversation(ctx.conversation);
        }

        @Override
        protected void onPostExecute(List<MessageConversation> messages) {
            ctx.mm.showError();
            ctx.messages = messages;
            ctx.createAdapter();
            if (!messages.isEmpty()) {
                recView.scrollToPosition(messages.size() - 1);
            }
            ctx.loadingBar_new.setVisibility(View.GONE);
            ctx.startTimeReload();

        }

        @Override
        protected void onPreExecute() {

        }
    }

    private class NewMessagesTask extends AsyncTask<Void, Void, List<MessageConversation>> {

        private ConversationActivity ctx;


        public NewMessagesTask(ConversationActivity c) {
            super();
            ctx = c;

        }

        @Override
        protected List<MessageConversation> doInBackground(Void... params) {
            if (messages == null || messages.isEmpty()) {
                return ctx.mm.getMessagesConversation(ctx.conversation);
            } else {
                return ctx.mm.getNewMessagesConversation(ctx.conversation, messages.get(messages.size()-1));
            }
        }

        @Override
        protected void onPostExecute(List<MessageConversation> msgs) {
            ctx.mm.showError();
            if (messages == null || messages.isEmpty()) {
                ctx.messages = msgs;
                ctx.createAdapter();
            } else {
                for(MessageConversation me : msgs) {
                    messages.add(me);
                }
                messageAdapter.notifyDataSetChanged();

            }
            if (!msgs.isEmpty()) {
                recView.scrollToPosition(messages.size() - 1);
            }
            olderAvaiable = messages.size() >= 15; //TODO: MOVE TO CONSTANTS
            ctx.loadingBar_new.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            ctx.loadingBar_new.setVisibility(View.VISIBLE);
        }
    }

    private class OlderMessagesTask extends AsyncTask<Void, Void, List<MessageConversation>> {

        private ConversationActivity ctx;


        public OlderMessagesTask(ConversationActivity c) {
            super();
            ctx = c;

        }

        @Override
        protected List<MessageConversation> doInBackground(Void... params) {
            if (messages == null || messages.isEmpty()) {
                return ctx.mm.getMessagesConversation(ctx.conversation);
            } else {
                return ctx.mm.getOlderMessagesConversation(ctx.conversation, messages.get(0));
            }
        }

        @Override
        protected void onPostExecute(List<MessageConversation> msgs) {
            ctx.mm.showError();
            if (messages == null || messages.isEmpty()) {
                ctx.messages = msgs;
                ctx.createAdapter();
                if (!messages.isEmpty()) {
                    recView.scrollToPosition(messages.size()-1);
                }
            } else {
                if (!msgs.isEmpty()) {
                    for(MessageConversation me : msgs) {
                        messages.add(0,me);
                    }
                    messageAdapter.notifyDataSetChanged();
                    recView.scrollToPosition(0);
                }
            }
            olderAvaiable = !(msgs.size()<Constants.MAX_LIST_OLDER_SIZE);
            ctx.loadingBar_older.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            ctx.loadingBar_older.setVisibility(View.VISIBLE);
        }
    }

    private class SendMessageTask extends AsyncTask<Void, Void, Void> {

        String body;
        ConversationActivity ctx;

        public SendMessageTask(ConversationActivity ctx, String body) {
            super();
            this.body = body;
            this.ctx = ctx;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Server s = new Server(ctx);
            s.sendMessageToConversation(ctx.conversation, body);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ctx.editText.setText("");
            pDialog.dismiss();
            ctx.loadMessages();
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ctx);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Sending message..."); //TODO: TRANSLATE
            pDialog.setCancelable(false);
            pDialog.setMax(100);
            pDialog.setProgress(0);
            pDialog.show();
        }
    }





}
