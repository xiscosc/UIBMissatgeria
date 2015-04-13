package com.fsc.uibmissatgeria.fragments;

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
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.activities.MessagesActivity;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.adapters.MessageAdapter;
import com.fsc.uibmissatgeria.api.Server;
import com.fsc.uibmissatgeria.models.Group;
import com.fsc.uibmissatgeria.models.Message;
import com.fsc.uibmissatgeria.models.Subject;

import java.util.ArrayList;
import java.util.Map;


public class MessagesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private MessageAdapter messageAdapter;
    private ArrayList<Message> messages;
    private RecyclerView recView;
    private SwipeRefreshLayout swipeLayout;
    private ImageButton fabImageButton;
    private ProgressBar loadingBar;


    public MessagesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messages, container, false);
        recView = (RecyclerView) rootView.findViewById(R.id.list_item_messages);
        recView.setLayoutManager(
                new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_messages);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        fabImageButton = (ImageButton) rootView.findViewById(R.id.fab_image_button);
        loadingBar = (ProgressBar) rootView.findViewById(R.id.loading_messages);

        setListeners();

        return rootView;
    }

    @Override public void onRefresh() {
        loadMessages();
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

        recView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = ((LinearLayoutManager)recView.getLayoutManager());
                if (layoutManager.findFirstVisibleItemPosition() == 0) {
                    swipeLayout.setEnabled(true);
                } else {
                    swipeLayout.setEnabled(false);
                }

            }
        });
    }



    public void loadMessages() {
        MessagesActivity ma = (MessagesActivity) getActivity();
        ObtainMessagesTask task = new ObtainMessagesTask(
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

    @Override
    public void onResume() {
        super.onResume();
        swipeLayout.setRefreshing(true);
        loadMessages();
    }

    private class ObtainMessagesTask extends AsyncTask<Void, Void, Map<String, Object>> {

        private MessagesFragment ctx;
        private Subject subject;
        private Group group;

        public ObtainMessagesTask(MessagesFragment c, Subject s, Group g) {
            super();
            ctx = c;
            subject = s;
            group = g;
        }

        @Override
        protected Map<String, Object> doInBackground(Void... params) {
            Server s = new Server(ctx.getActivity());
            return s.getMessages(subject, group);
        }

        @Override
        protected void onPostExecute(Map<String, Object> hm) {
                String error_message = (String) hm.get(Constants.RESULT_ERROR);
                if (error_message==null) {
                    int total = (int) hm.get(Constants.RESULT_TOTAL);
                    ArrayList<Message> messages = (ArrayList<Message>) hm.get(Constants.RESULT_MESSAGES);
                    if (total>0) {
                        ctx.messages = messages;
                        ctx.createAdapter();
                        ctx.loadingBar.setVisibility(View.GONE);
                    }
                } else {
                    Constants.showToast(ctx.getActivity(), error_message);
                }
                ctx.swipeLayout.setRefreshing(false);

        }

        @Override
        protected void onPreExecute() {
            //swipeLayout.setRefreshing(true);
        }
    }

}