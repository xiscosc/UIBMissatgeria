package com.fsc.uibmissatgeria.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fsc.uibmissatgeria.activities.MessagesActivity;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.adapters.MessageAdapter;
import com.fsc.uibmissatgeria.api.Server;
import com.fsc.uibmissatgeria.objects.Message;


public class MessagesFragment extends Fragment {

    MessageAdapter adapterMessage;
    ProgressDialog pDialog;
    ListView listView;


    public MessagesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messages, container, false);
        listView = (ListView) rootView.findViewById(R.id.list_item_messages);
        loadMessages();
        return rootView;
    }

    public void loadMessages() {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Loading messages..."); //TODO: TRANSLATE
        pDialog.setCancelable(false);
        pDialog.setMax(100);
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
            pDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            pDialog.setProgress(0);
            pDialog.show();
        }
    }

}