package com.fsc.uibmissatgeria.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.models.Subject;
import com.fsc.uibmissatgeria.models.User;

import java.util.List;

/**
 * Created by Xisco on 04/03/2015.
 */

public class PeerAdapter extends RecyclerView.Adapter<PeerAdapter.PeerViewHolder>
        implements View.OnClickListener {

    public static class PeerViewHolder extends RecyclerView.ViewHolder {

        private TextView userName;

        public PeerViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            userName = (TextView) itemView.findViewById(R.id.peer_user_name);

        }

        public void bindSubject(User u) {
            userName.setText(u.getName());
        }
    }

    private List<User> peers;
    private View.OnClickListener listener;

    public PeerAdapter(List<User> peers) {
        this.peers = peers;
    }

    @Override
    public PeerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.listitem_peer, viewGroup, false);

        itemView.setOnClickListener(this);

        return new PeerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PeerViewHolder viewHolder, int pos) {
        User item = peers.get(pos);
        viewHolder.bindSubject(item);
    }

    @Override
    public int getItemCount() {
        return peers.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(listener != null)
            listener.onClick(view);
    }
}