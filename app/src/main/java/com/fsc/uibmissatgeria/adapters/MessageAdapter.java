package com.fsc.uibmissatgeria.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.objects.Message;

/**
 * Created by Xisco on 04/03/2015.
 */
public class MessageAdapter extends ArrayAdapter<Message> {

    public MessageAdapter(Context context, Message[] data) {
        super(context, R.layout.listitem_course, data);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View item = inflater.inflate(R.layout.listitem_message, null);

        Message m = getItem(position);

        TextView messageUser = (TextView)item.findViewById(R.id.message_user);
        messageUser.setText(m.getUser().getName());

        TextView messageDate = (TextView)item.findViewById(R.id.message_date);
        messageDate.setText(m.getDate().toString());

        TextView messageBody = (TextView)item.findViewById(R.id.message_body);
        messageBody.setText(m.getBody());

        return(item);
    }
}