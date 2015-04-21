package com.fsc.uibmissatgeria.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.models.Message;

import java.util.List;

/**
 * Created by Xisco on 04/03/2015.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessagesViewHolder>
        implements View.OnClickListener {

    public static class MessagesViewHolder extends RecyclerView.ViewHolder {

        private TextView messageUser;
        private TextView messageDate;
        private TextView messageBody;


        public MessagesViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            messageUser = (TextView)itemView.findViewById(R.id.message_user);
            messageDate = (TextView)itemView.findViewById(R.id.message_date);
            messageBody = (TextView)itemView.findViewById(R.id.message_body);
        }

        public void bindGroup(Message m) {
            messageUser.setText(m.getUser().getName());
            messageDate.setText(m.getStringDate());
            messageBody.setText(m.getBody());
        }
    }

    private List<Message> messages;
    private View.OnClickListener listener;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public MessagesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.listitem_message, viewGroup, false);

        itemView.setOnClickListener(this);

        return new MessagesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MessagesViewHolder viewHolder, int pos) {
        Message item = messages.get(pos);
        viewHolder.bindGroup(item);
    }

    @Override
    public int getItemCount() {
        return messages.size();
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