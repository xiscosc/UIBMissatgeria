package com.fsc.uibmissatgeria.ui.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.models.Conversation;
import com.fsc.uibmissatgeria.models.ModelsManager;
import com.fsc.uibmissatgeria.models.User;
import com.fsc.uibmissatgeria.ui.adapters.PeerAdapter;

import java.util.List;

public class PeerSelectionActivity extends ActionBarActivity {

    private RecyclerView recView;
    private List<User> peers;
    private PeerAdapter peersAdapter;
    private ProgressBar loadingBar;
    private ModelsManager mm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peer_selection);
        recView = (RecyclerView) findViewById(R.id.peers_list);
        recView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        loadingBar = (ProgressBar) findViewById(R.id.peers_loading);
        mm = new ModelsManager(this);
        loadPeers();
    }

    private void loadPeers() {
        ObtainPeersTask task = new ObtainPeersTask(this);
        task.execute();
    }

    private void createAdapter() {
        peersAdapter = new PeerAdapter(peers);
        peersAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User peer = peers.get(recView.getChildAdapterPosition(v));
                Conversation c = mm.getConversation(peer);
                //TODO: ACTIVITY CONVERSATION
            }
        });
        recView.setAdapter(peersAdapter);
    }



    private class ObtainPeersTask extends AsyncTask<Void, Void, List<User>> {

        private PeerSelectionActivity ctx;

        public ObtainPeersTask(PeerSelectionActivity c) {
            super();
            ctx = c;
        }

        @Override
        protected List<User> doInBackground(Void... params) {
            return ctx.mm.getPeers();
        }

        @Override
        protected void onPostExecute(List<User> peers) {
            ctx.mm.showError();
            ctx.peers =  peers;
            ctx.createAdapter();
            loadingBar.setVisibility(View.GONE);
        }

    }

}
