package com.fsc.uibmissatgeria.ui.fragments;

import android.content.Intent;
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

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.api.AccountUIB;
import com.fsc.uibmissatgeria.models.Conversation;
import com.fsc.uibmissatgeria.managers.ModelManager;
import com.fsc.uibmissatgeria.ui.activities.ConversationActivity;
import com.fsc.uibmissatgeria.ui.activities.PeerSelectionActivity;
import com.fsc.uibmissatgeria.ui.adapters.ConversationAdapter;

import java.util.List;


public class ConversationsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ConversationAdapter conversationAdapter;
    private List<Conversation> conversations;
    private RecyclerView recView;
    private SwipeRefreshLayout swipeLayout;
    private ImageButton fabImageButton;
    private ProgressBar loadingBar;
    private ModelManager mm;
    private AccountUIB accountUIB;

    public ConversationsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_conversations, container, false);

        recView = (RecyclerView) rootView.findViewById(R.id.conversations_list);
        recView.setLayoutManager(
                new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.conversations_swipe);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        fabImageButton = (ImageButton) rootView.findViewById(R.id.conversationsfab_image_button);
        loadingBar = (ProgressBar) rootView.findViewById(R.id.conversations_loading);

        mm = new ModelManager(getActivity());
        accountUIB = new AccountUIB(getActivity());
        setListeners();
        return rootView;
    }

    @Override public void onRefresh() {
        updateConversations();
    }


    private void setListeners() {
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.post(new Runnable() {
            @Override public void run() {
                swipeLayout.setRefreshing(true);
                loadConversations();
            }
        });

        fabImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPeerSelection();
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

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accountUIB.isLogged()) {
            swipeLayout.setRefreshing(true);
            updateConversations();
        }
    }

    private void loadConversations() {
        ConversationsTask task = new ConversationsTask(this);
        task.execute();
    }

    private void updateConversations() {
        swipeLayout.setRefreshing(true);
        UpdateConversationsTask task = new UpdateConversationsTask(this);
        task.execute();
    }

    private void createAdapter() {
        recView.setEnabled(true);
        conversationAdapter = new ConversationAdapter(conversations);
        conversationAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Conversation c = conversations.get(recView.getChildAdapterPosition(v));
                startConversation(c);
            }
        });
        recView.setAdapter(conversationAdapter);

    }

    private void startPeerSelection() {
        Intent i = new Intent(getActivity(), PeerSelectionActivity.class);
        startActivity(i);
    }

    private void startConversation(Conversation c) {
        Intent i = new Intent(getActivity(), ConversationActivity.class);
        i.putExtra(Constants.CONVERSATION_OBJ, c.getId());
        startActivity(i);
    }


    private class ConversationsTask extends AsyncTask<Void, Void, List<Conversation>> {

        private ConversationsFragment ctx;


        public ConversationsTask(ConversationsFragment c) {
            super();
            ctx = c;

        }

        @Override
        protected List<Conversation> doInBackground(Void... params) {
            return ctx.mm.getConversations();
        }

        @Override
        protected void onPostExecute(List<Conversation> conversations) {
            ctx.mm.showError();
            ctx.conversations = conversations;
            ctx.createAdapter();
            ctx.loadingBar.setVisibility(View.GONE);
            ctx.swipeLayout.setRefreshing(false);
            ctx.updateConversations();
        }

        @Override
        protected void onPreExecute() {
            //swipeLayout.setRefreshing(true);
        }
    }

    private class UpdateConversationsTask extends AsyncTask<Void, Void, List<Conversation>> {

        private ConversationsFragment ctx;


        public UpdateConversationsTask(ConversationsFragment c) {
            super();
            ctx = c;

        }

        @Override
        protected List<Conversation> doInBackground(Void... params) {
            return ctx.mm.updateConversations();
        }

        @Override
        protected void onPostExecute(List<Conversation> conversations) {
            ctx.mm.showError();
            ctx.conversations = conversations;
            ctx.createAdapter();
            ctx.loadingBar.setVisibility(View.GONE);
            ctx.swipeLayout.setRefreshing(false);
        }

        @Override
        protected void onPreExecute() {
            //swipeLayout.setRefreshing(true);
        }
    }
}