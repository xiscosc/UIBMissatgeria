package com.fsc.uibmissatgeria.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.objects.Conversation;


public class ConversationAdapter extends ArrayAdapter<Conversation> {

    public ConversationAdapter(Context context, Conversation[] data) {
        super(context, R.layout.listitem_contact, data);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View item = inflater.inflate(R.layout.listitem_contact, null);

        Conversation m = (Conversation) getItem(position);

        TextView CTitle = (TextView)item.findViewById(R.id.MContent);
        CTitle.setText("User "+m.getUser());

        return(item);
    }
}