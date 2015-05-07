package com.fsc.uibmissatgeria.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.models.Message;
import com.fsc.uibmissatgeria.models.MessageConversation;
import com.fsc.uibmissatgeria.models.User;

import java.util.List;

/**
 * Created by Xisco on 04/03/2015.
 */

public class MessageConversationAdapter extends RecyclerView.Adapter<MessageConversationAdapter.MessagesViewHolder>
        implements View.OnClickListener {

    public static class MessagesViewHolder extends RecyclerView.ViewHolder {

        private TextView messageUser;
        private TextView messageDate;
        private TextView messageBody;
        private ImageView avatar;

        public MessagesViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            messageUser = (TextView)itemView.findViewById(R.id.message_user);
            messageDate = (TextView)itemView.findViewById(R.id.message_date);
            messageBody = (TextView)itemView.findViewById(R.id.message_body);
            avatar = (ImageView)itemView.findViewById(R.id.user_avatar);
        }

        public void bindGroup(MessageConversation m, User u) {

            messageDate.setText(m.getStringDate());
            messageBody.setText(m.getBody());
            messageUser.setText(u.getName());
            avatar.setVisibility(View.INVISIBLE);

        }
    }

    private List<MessageConversation> messages;
    private View.OnClickListener listener;
    private Context c;


    public MessageConversationAdapter(List<MessageConversation> messages, Context ctx) {
        this.messages = messages;
        this.c = ctx;
    };

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).isFromOther()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public MessagesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int lay;
        switch (viewType){
            case 0:
               lay = R.layout.listitem_message_other;
               break;
            default:
               lay = R.layout.listitem_message;
               break;
        }

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(lay, viewGroup, false);

        itemView.setOnClickListener(this);

        return new MessagesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MessagesViewHolder viewHolder, int pos) {
        MessageConversation item = messages.get(pos);
        User from = item.getUser(c);
        viewHolder.bindGroup(item, from);
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