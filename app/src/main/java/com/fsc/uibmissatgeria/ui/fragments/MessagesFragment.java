package com.fsc.uibmissatgeria.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.fsc.uibmissatgeria.ui.activities.MessagesActivity;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.ui.adapters.MessageAdapter;
import com.fsc.uibmissatgeria.models.ModelsManager;
import com.fsc.uibmissatgeria.models.SubjectGroup;
import com.fsc.uibmissatgeria.models.Message;
import com.fsc.uibmissatgeria.models.Subject;

import java.util.List;


public class MessagesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private MessageAdapter messageAdapter;
    private List<Message> messages;
    private RecyclerView recView;
    private SwipeRefreshLayout swipeLayout;
    private ImageButton fabImageButton;
    private ProgressBar loadingBar;
    private ModelsManager mm;
    private Boolean olderAvaiable;



    public MessagesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messages, container, false);
        recView = (RecyclerView) rootView.findViewById(R.id.list_messages);
        recView.setLayoutManager(
                new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_messages);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        fabImageButton = (ImageButton) rootView.findViewById(R.id.fab_image_button_messages);
        loadingBar = (ProgressBar) rootView.findViewById(R.id.loading_messages);

        mm = new ModelsManager(getActivity());
        setListeners();
        olderAvaiable = true;


        return rootView;
    }

    @Override public void onRefresh() {
        updateMessages();
    }


    private void setListeners() {
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.post(new Runnable() {
            @Override public void run() {
                swipeLayout.setRefreshing(true);
                loadMessages();
            }
        });

        fabImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessagesActivity ma = (MessagesActivity) getActivity();
                ma.newMessageAction();
            }
        });

        /*
        Disables SwipeRefresh if the list is not at top
         */
        recView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = ((LinearLayoutManager)recView.getLayoutManager());
                if (layoutManager.findFirstVisibleItemPosition() == 0) {
                    swipeLayout.setEnabled(true);
                } else {
                    swipeLayout.setEnabled(false);
                }
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                if (olderAvaiable && (visibleItemCount+pastVisiblesItems) >= totalItemCount) {
                    olderAvaiable = false;
                    getOlder();
                }

            }
        });
    }



    private void loadMessages() {
        MessagesActivity ma = (MessagesActivity) getActivity();
        InitMessagesTask task = new InitMessagesTask(
                MessagesFragment.this,
                ma.sbj,
                ma.gr);
        task.execute();
    }

    private void updateMessages() {
        MessagesActivity ma = (MessagesActivity) getActivity();
        UpdateMessagesTask task = new UpdateMessagesTask(
                MessagesFragment.this,
                ma.sbj,
                ma.gr);
        task.execute();
    }

    private void getOlder() {
        MessagesActivity ma = (MessagesActivity) getActivity();
        OlderMessagesTask task = new OlderMessagesTask(
                MessagesFragment.this,
                ma.sbj,
                ma.gr);
        task.execute();
    }

    private void createAdapter() {
        recView.setEnabled(true);
        messageAdapter = new MessageAdapter(messages);
        recView.setAdapter(messageAdapter);

    }

    private void checkOlder(){
        if (messages.size() < 15) { //TODO: ADD TO CONFIG
            olderAvaiable = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        swipeLayout.setRefreshing(true);
        updateMessages();

    }

    private class InitMessagesTask extends AsyncTask<Void, Void, List<Message>> {

        private MessagesFragment ctx;
        private Subject subject;
        private SubjectGroup subjectGroup;

        public InitMessagesTask(MessagesFragment c, Subject s, SubjectGroup g) {
            super();
            ctx = c;
            subject = s;
            subjectGroup = g;
        }

        @Override
        protected List<Message> doInBackground(Void... params) {
            return ctx.mm.getMessages(subject, subjectGroup);
        }

        @Override
        protected void onPostExecute(List<Message> messages) {
                ctx.messages = messages;
                ctx.createAdapter();
                ctx.loadingBar.setVisibility(View.GONE);
                ctx.swipeLayout.setRefreshing(false);
                ctx.checkOlder();
        }

        @Override
        protected void onPreExecute() {
            //swipeLayout.setRefreshing(true);
        }
    }

    private class UpdateMessagesTask extends AsyncTask<Void, Void, List<Message>> {

        private MessagesFragment ctx;
        private Subject subject;
        private SubjectGroup subjectGroup;

        public UpdateMessagesTask(MessagesFragment c, Subject s, SubjectGroup g) {
            super();
            ctx = c;
            subject = s;
            subjectGroup = g;
        }

        @Override
        protected List<Message> doInBackground(Void... params) {
            if (!ctx.messages.isEmpty()) {
                Message last = ctx.messages.get(0);
                return ctx.mm.getNewMessages(subject, subjectGroup, last);
            } else {
                return ctx.mm.getMessages(subject, subjectGroup);
            }

        }

        @Override
        protected void onPostExecute(List<Message> messages) {
            if (!ctx.messages.isEmpty()) {
                for (Message me: messages) {
                    ctx.messages.add(0, me);
                }
                ctx.messageAdapter.notifyDataSetChanged();
            } else {
                ctx.messages = messages;
                ctx.createAdapter();
            }
            ctx.checkOlder();
            ctx.swipeLayout.setRefreshing(false);
        }

        @Override
        protected void onPreExecute() {
            //swipeLayout.setRefreshing(true);
        }
    }

    private class OlderMessagesTask extends AsyncTask<Void, Void, List<Message>> {

        private MessagesFragment ctx;
        private Subject subject;
        private SubjectGroup subjectGroup;

        public OlderMessagesTask(MessagesFragment c, Subject s, SubjectGroup g) {
            super();
            ctx = c;
            subject = s;
            subjectGroup = g;
        }

        @Override
        protected List<Message> doInBackground(Void... params) {
            if (!ctx.messages.isEmpty()) {
                Message last = ctx.messages.get(messages.size()-1);
                return ctx.mm.getNewMessages(subject, subjectGroup, last);
            } else {
                return ctx.mm.getMessages(subject, subjectGroup);
            }

        }

        @Override
        protected void onPostExecute(List<Message> messages) {
            if (!messages.isEmpty()) {
                if (!ctx.messages.isEmpty()) {
                    for (Message me: messages) {
                        ctx.messages.add(me);
                    }
                    ctx.messageAdapter.notifyDataSetChanged();
                } else {
                    ctx.messages = messages;
                    ctx.createAdapter();
                }
                ctx.olderAvaiable = true;
            } else {
                ctx.olderAvaiable = false;
            }

            ctx.swipeLayout.setRefreshing(false);
        }

        @Override
        protected void onPreExecute() {
            //swipeLayout.setRefreshing(true);
        }
    }

}