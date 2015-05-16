package com.fsc.uibmissatgeria.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.models.Conversation;
import com.fsc.uibmissatgeria.models.MessageConversation;

import java.util.List;


public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationsViewHolder>
        implements View.OnClickListener {

    public static class ConversationsViewHolder extends RecyclerView.ViewHolder {

        private TextView userName;
        private TextView date;
        private TextView body;
        private View circleUnread;

        public ConversationsViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            userName = (TextView) itemView.findViewById(R.id.conversation_user);
            date = (TextView) itemView.findViewById(R.id.conversation_date);
            body = (TextView) itemView.findViewById(R.id.conversation_body);
            circleUnread = (View) itemView.findViewById(R.id.conversation_circle_unread);

        }

        public void bindConversation(Conversation c) {
            userName.setText(c.getPeerName());
            MessageConversation m = c.getLastMessage();
            if (m != null) {
                date.setText(m.getStringDate());
                body.setText(m.getBodyForList());
                if (!m.isRead()) circleUnread.setVisibility(View.VISIBLE);
            } else {
                date.setText("");
                body.setText("");
                circleUnread.setVisibility(View.VISIBLE);
            }

        }
    }

    private List<Conversation> conversations;
    private View.OnClickListener listener;

    public ConversationAdapter(List<Conversation> conversations) {
        this.conversations = conversations;
    }

    @Override
    public ConversationsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.listitem_conversation, viewGroup, false);

        itemView.setOnClickListener(this);

        return new ConversationsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ConversationsViewHolder viewHolder, int pos) {
        Conversation item = conversations.get(pos);
        viewHolder.bindConversation(item);
    }

    @Override
    public int getItemCount() {
        return conversations.size();
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