package com.fsc.uibmissatgeria.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.fsc.uibmissatgeria.activities.MessagesActivity;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.adapters.MessageAdapter;
import com.fsc.uibmissatgeria.api.Server;
import com.fsc.uibmissatgeria.objects.Group;
import com.fsc.uibmissatgeria.objects.Message;
import com.fsc.uibmissatgeria.objects.Subject;


public class MessagesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    MessageAdapter adapterMessage;
    ListView listView;
    SwipeRefreshLayout swipeLayout;


    public MessagesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messages, container, false);
        listView = (ListView) rootView.findViewById(R.id.list_item_messages);

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_messages);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeLayout.post(new Runnable() {
            @Override public void run() {
                swipeLayout.setRefreshing(true);
                loadMessages();
            }
        });

        ImageButton fabImageButton = (ImageButton) rootView.findViewById(R.id.fab_image_button);

        fabImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessagesActivity ma = (MessagesActivity) getActivity();
                ma.newMessageAction();
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if(listView != null && listView.getChildCount() > 0){
                    // check if the first item of the list is visible
                    boolean firstItemVisible = listView.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = listView.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeLayout.setEnabled(enable);
            }
        });

        return rootView;
    }

    @Override public void onRefresh() {
        loadMessages();
    }



    public void loadMessages() {
        MessagesActivity ma = (MessagesActivity) getActivity();
        ObtainMessagesTask task = new ObtainMessagesTask(
                MessagesFragment.this,
                ma.sbj,
                ma.gr);
        task.execute();
    }

    private void createAdapter(final Message[] messages) {
        adapterMessage = new MessageAdapter(getActivity(), messages);
        listView.setAdapter(adapterMessage);

    }

    @Override
    public void onResume() {
        super.onResume();
        swipeLayout.setRefreshing(true);
        loadMessages();
    }

    private class ObtainMessagesTask extends AsyncTask<Void, Void, Message[]> {

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
        protected Message[] doInBackground(Void... params) {
            Server s = new Server();
            return s.getMessages(subject, group);
        }

        @Override
        protected void onPostExecute(Message[] messages) {
            ctx.createAdapter(messages);
            ctx.swipeLayout.setRefreshing(false);
        }

        @Override
        protected void onPreExecute() {
            //swipeLayout.setRefreshing(true);
        }
    }

}