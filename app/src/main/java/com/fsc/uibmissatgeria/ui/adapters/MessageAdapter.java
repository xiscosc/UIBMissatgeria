package com.fsc.uibmissatgeria.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.models.Avatar;
import com.fsc.uibmissatgeria.models.Message;
import com.fsc.uibmissatgeria.ui.activities.MessageDetailActivity;
import com.gc.materialdesign.views.ButtonRectangle;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Xisco on 04/03/2015.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessagesViewHolder>
        implements View.OnClickListener {

    public static class MessagesViewHolder extends RecyclerView.ViewHolder {

        private TextView messageUser;
        private TextView messageDate;
        private TextView messageBody;
        private CircleImageView avatar;
        private Button buttonFiles;
        private View divider;


        public MessagesViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            messageUser = (TextView)itemView.findViewById(R.id.message_user);
            messageDate = (TextView)itemView.findViewById(R.id.message_date);
            messageBody = (TextView)itemView.findViewById(R.id.message_body);
            avatar = (CircleImageView)itemView.findViewById(R.id.user_avatar);
            buttonFiles = (Button)itemView.findViewById(R.id.button_files);
            divider = itemView.findViewById(R.id.message_divider);

        }

        public void bindGroup(Message m, Context c, boolean last) {
            messageDate.setText(m.getStringDate());
            messageBody.setText(m.getBody());
            messageUser.setText(m.getUser().getName());
            Avatar avatarObj = m.getUser().getAvatar();
            if (avatarObj!=null && avatarObj.hasFile()) {
                avatar.setImageBitmap(avatarObj.getBitmap(c));
            } else {
                avatar.setImageResource(R.drawable.user_avatar);
            }
            if (!(m.getFiles()).isEmpty()) {
                buttonFiles.setVisibility(View.VISIBLE);
            } else {
                buttonFiles.setVisibility(View.GONE);
            }
            if (!last) {
                divider.setVisibility(View.VISIBLE);
            } else {
                divider.setVisibility(View.GONE);
            }
        }
    }

    private List<Message> messages;
    private View.OnClickListener listener;
    private Context c;

    public MessageAdapter(List<Message> messages, Context c) {
        this.messages = messages;
        this.c = c;
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getUser().getType() == Constants.TYPE_TEACHER) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public MessagesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        int lay;
        switch (viewType){
            case 0:
                lay = R.layout.listitem_post_other;
                break;
            default:
                lay = R.layout.listitem_post;
                break;
        }

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(lay, viewGroup, false);

        itemView.setOnClickListener(this);

        return new MessagesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MessagesViewHolder viewHolder, int pos) {
        Message item = messages.get(pos);
        viewHolder.bindGroup(item, c, (pos == (messages.size()-1)));
        final Message msg = item;
        viewHolder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Avatar avatar = msg.getUser().getAvatar();
                if (avatar != null) avatar.startIntent(c);
            }
        });
        viewHolder.buttonFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(c, MessageDetailActivity.class);
                i.putExtra(Constants.MESSAGE_OBJ, msg.getId());
                c.startActivity(i);
            }
        });
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