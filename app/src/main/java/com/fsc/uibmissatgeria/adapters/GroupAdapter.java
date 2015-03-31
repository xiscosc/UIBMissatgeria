package com.fsc.uibmissatgeria.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.objects.Group;

/**
 * Created by Xisco on 04/03/2015.
 */
public class GroupAdapter extends ArrayAdapter<Group> {

    public GroupAdapter(Context context, Group[] data) {
        super(context, R.layout.listitem_group, data);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View item = inflater.inflate(R.layout.listitem_group, null);

        Group c = (Group) getItem(position);

        TextView CTitle = (TextView)item.findViewById(R.id.group_title);
        CTitle.setText("Group "+c.getName()); //TODO: TRANSLATE


        return(item);
    }
}