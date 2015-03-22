package com.fsc.uibmissatgeria.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.fsc.uibmissatgeria.activities.MessagesActivity;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.adapters.MessageAdapter;
import com.fsc.uibmissatgeria.api.Server;
import com.fsc.uibmissatgeria.objects.Message;


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

        return rootView;
    }

    @Override public void onRefresh() {
        loadMessages();
    }



    public void loadMessages() {
        MessagesActivity ma = (MessagesActivity) getActivity();
        ObtainMessagesTask task = new ObtainMessagesTask(
                MessagesFragment.this,
                ma.idCourse ,
                ma.idGroup);
        task.execute();
    }

    private void createAdapter(final Message[] messages) {
        adapterMessage = new MessageAdapter(getActivity(), messages);
        listView.setAdapter(adapterMessage);

    }



    private class ObtainMessagesTask extends AsyncTask<Void, Void, Message[]> {

        private MessagesFragment ctx;
        private int idCourse;
        private int idGroup;

        public ObtainMessagesTask(MessagesFragment c, int idC, int idG) {
            super();
            ctx = c;
            idCourse = idC;
            idGroup = idG;
        }

        @Override
        protected Message[] doInBackground(Void... params) {
            Server s = new Server();
            return s.getMessages(idGroup, idCourse);
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