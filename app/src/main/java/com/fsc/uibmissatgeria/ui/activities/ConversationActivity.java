package com.fsc.uibmissatgeria.ui.activities;

import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.api.AccountUIB;
import com.fsc.uibmissatgeria.api.ServerSettings;
import com.fsc.uibmissatgeria.managers.FileManager;
import com.fsc.uibmissatgeria.managers.ImageManager;
import com.fsc.uibmissatgeria.models.Avatar;
import com.fsc.uibmissatgeria.models.Conversation;
import com.fsc.uibmissatgeria.models.FileMessageConversation;
import com.fsc.uibmissatgeria.models.MessageConversation;
import com.fsc.uibmissatgeria.managers.ModelManager;
import com.fsc.uibmissatgeria.models.User;
import com.fsc.uibmissatgeria.ui.adapters.MessageConversationAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationActivity extends AppCompatActivity {

    private Conversation conversation;
    private MessageConversationAdapter messageAdapter;
    private List<MessageConversation> messages;
    private RecyclerView recView;
    private EditText editText;
    private ProgressBar loadingBar_new;
    private ProgressBar loadingBar_older;
    private ModelManager mm;
    private Boolean olderAvaiable;
    private ProgressDialog pDialog;
    private Timer timer;
    private TimerTask ttask;
    private Boolean firstRun;
    private Toolbar toolbar;
    private FileMessageConversation file;


    /*FILES*/
    private LinearLayout fileView;
    private TextView fileName;
    private TextView fileSize;
    private CircleImageView fileImage;


    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        Long id = i.getLongExtra(Constants.CONVERSATION_OBJ, 0);
        conversation = Conversation.findById(Conversation.class, id);
        setContentView(R.layout.activity_conversation);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(conversation.getPeerName());
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        editText = (EditText) findViewById(R.id.conversation_text);
        recView = (RecyclerView) findViewById(R.id.conversation_list);

        fileView = (LinearLayout) findViewById(R.id.conversation_file_layout);
        fileName = (TextView) findViewById(R.id.conversation_file_name);
        fileSize = (TextView) findViewById(R.id.conversation_file_size);
        fileImage = (CircleImageView) findViewById(R.id.conversation_file_image);


        recView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

        recView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = ((LinearLayoutManager) recView.getLayoutManager());
                if (olderAvaiable && layoutManager.findFirstVisibleItemPosition() == 0) {
                    olderAvaiable = false;
                    getOlderMessages();
                }

            }
        });
        firstRun = true;

        mm = new ModelManager(this);
        loadingBar_new = (ProgressBar) findViewById(R.id.conversation_loading_new);
        loadingBar_older = (ProgressBar) findViewById(R.id.conversation_loading_older);
        olderAvaiable = false;
        loadMessages();
    }

    private void getFile() {
        if (file == null) {
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,
                    getString(R.string.select_file)), 1);
        } else {
            Constants.showToast(this, this.getString(R.string.error_file_max_number)+" 1");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                Uri route = data.getData();
                User user = (new AccountUIB(this)).getUser();
                if (FileManager.isImageFromUri(route, this)) {
                    ImageManager imageManager = new ImageManager(this);
                    file = imageManager.saveImageToStorageConversation(route, user);
                } else {
                    FileManager fileManager = new FileManager(this);
                    file = fileManager.savFileToStorageConversation(route);
                }
                if (file != null)  showFile();
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void showFile() {
        if (file.isImage()) {
            fileImage.setImageBitmap(file.getBitmap(this));
        } else {
            fileImage.setImageResource(R.drawable.file_icon);
        }
        fileSize.setText(file.getSizeMB() + " MB");
        fileName.setText(file.getName());
        fileView.setVisibility(View.VISIBLE);
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
        ServerSettings ss = new ServerSettings(this);
        String bodyString = editText.getText().toString();
        int max_char = ss.getMaxChar();

        if (!bodyString.replaceAll("\\s+","").isEmpty()
                && bodyString.replaceAll("\\s+","")!=null
                && bodyString.length() <= max_char) {
            SendMessageTask task = new SendMessageTask(
                    this,
                    bodyString);
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            task.execute();
        } else if (bodyString.length() > max_char) {
            Constants.showToast(this, getResources().getString(R.string.error_max_char));
        } else {
            Constants.showToast(this, getResources().getString(R.string.error_empty));
        }


    }

    private void startTimeReload() {
        final ConversationActivity self = this;
        final Handler handler = new Handler();
        timer = new Timer();
        ttask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        new NewMessagesTask(self).execute();
                    }
                });
            }
        };
        timer.schedule(ttask, 0, 25000);
    }

    private void cancelTimeReload() {
        if (timer!=null) {
            timer.cancel();
            timer.purge();
            ttask.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conversation, menu);
        this.menu = menu;
        Avatar avatar = conversation.getPeer().getAvatar();
        if (avatar != null && avatar.hasFile()) {
            MenuItem avatarMenu = menu.getItem(1);
            avatarMenu.setIcon(avatar.getCircleBitmap(this));

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                upIntent.putExtra(Constants.NOTIFICATION_CONVERSATIONS_INTENT, true);
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
            case R.id.menu_conversation_attach:
                getFile();
                return true;
            case R.id.menu_conversation_avatar:
                Avatar avatar = conversation.getPeer().getAvatar();
                if (avatar != null) avatar.startIntent(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelTimeReload();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (!firstRun) {
            startTimeReload();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelTimeReload();
    }

    public void deleteFile(View view) {
        if (file != null) FileManager.deleteFile(file.getLocalPath());
        hideFile();
    }

    private void hideFile() {
        file = null;
        fileView.setVisibility(View.GONE);
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
            if (firstRun) {
                ctx.startTimeReload();
                firstRun = false;
            } else {
                ctx.loadingBar_new.setVisibility(View.GONE);
            }

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
            ctx.loadingBar_new.setVisibility(View.GONE);
            if (!msgs.isEmpty()) recView.scrollToPosition(messages.size() - 1);
            olderAvaiable = messages.size() >= 15; //TODO: MOVE TO CONSTANTS

        }

        @Override
        protected void onPreExecute() {
            LinearLayoutManager layoutManager = ((LinearLayoutManager)recView.getLayoutManager());
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
            Boolean visible = ((visibleItemCount+pastVisiblesItems) >= totalItemCount);
            ctx.loadingBar_new.setVisibility(View.VISIBLE);
            if (visible) recView.scrollToPosition(messages.size() - 1);
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

    private class SendMessageTask extends AsyncTask<Void, Void, Boolean> {

        String body;
        ConversationActivity ctx;
        ModelManager modelManager;

        public SendMessageTask(ConversationActivity ctx, String body) {
            super();
            this.body = body;
            this.ctx = ctx;
            modelManager = new ModelManager(ctx);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            List<FileMessageConversation> files = new ArrayList<>();
            if (file!=null) files.add(file);
            return modelManager.sendMessageConversation(ctx.conversation, body, files);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            modelManager.showError();
            pDialog.dismiss();
            if (result) {
                ctx.editText.setText("");
                ctx.updateMessages();
            }
            ctx.hideFile();
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ctx);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage(getResources().getString(R.string.sending_message));
            pDialog.setCancelable(false);
            pDialog.setMax(100);
            pDialog.setProgress(0);
            pDialog.show();
        }
    }
}
