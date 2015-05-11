package com.fsc.uibmissatgeria.ui.activities;

import android.content.Intent;
import android.os.AsyncTask;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.models.Conversation;
import com.fsc.uibmissatgeria.models.ModelsManager;
import com.fsc.uibmissatgeria.models.User;
import com.fsc.uibmissatgeria.ui.adapters.PeerAdapter;

import java.util.ArrayList;
import java.util.List;

public class PeerSelectionActivity extends AppCompatActivity {

    private RecyclerView recView;
    private List<User> peers;
    private List<User> peersOriginal;
    private PeerAdapter peersAdapter;
    private ProgressBar loadingBar;
    private ModelsManager mm;
    private EditText finder;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peer_selection);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        recView = (RecyclerView) findViewById(R.id.peers_list);
        recView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        loadingBar = (ProgressBar) findViewById(R.id.peers_loading);
        mm = new ModelsManager(this);
        finder = (EditText) findViewById(R.id.peers_search);
        finder.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                int total =  s.toString().length();
                if (total > 0) {
                    peers = getListFiltered(s.toString());
                    createAdapter();
                } else {
                   peers = peersOriginal;
                   createAdapter();
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        loadPeers();
    }

    private List<User> getListFiltered(String s) {
        List <User> listClone = new ArrayList<>();
        for (User p : peersOriginal) {
            String name = p.getName().toLowerCase();
            String comp = s.toLowerCase();
            if(name.contains(comp)){
                listClone.add(p);
            }
        }
        return listClone;
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
                startConversation(peer);
            }
        });
        recView.setAdapter(peersAdapter);
    }


    private void startConversation(User peer) {
        Conversation c = mm.getConversation(peer);
        Intent i = new Intent(this, ConversationActivity.class);
        i.putExtra(Constants.CONVERSATION_OBJ, c.getId());
        startActivity(i);
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
            ctx.peersOriginal = peers;
            ctx.createAdapter();
            loadingBar.setVisibility(View.GONE);
            finder.setVisibility(View.VISIBLE);
        }

    }

}
