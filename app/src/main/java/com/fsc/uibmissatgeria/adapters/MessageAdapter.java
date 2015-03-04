package com.fsc.uibmissatgeria.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fsc.uibmissatgeria.R;


public class MessageAdapter extends ArrayAdapter<Message> {

    public MessageAdapter(Context context, Message[] data) {
        super(context, R.layout.listitem_message, data);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View item = inflater.inflate(R.layout.listitem_message, null);

        Message m = (Message) getItem(position);

        TextView CTitle = (TextView)item.findViewById(R.id.MContent);
        CTitle.setText(m.getContent());

        return(item);
    }
}