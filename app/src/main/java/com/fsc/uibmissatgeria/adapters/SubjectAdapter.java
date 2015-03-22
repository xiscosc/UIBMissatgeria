package com.fsc.uibmissatgeria.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.objects.Subject;

/**
 * Created by Xisco on 04/03/2015.
 */
public class SubjectAdapter extends ArrayAdapter<Subject> {

    public SubjectAdapter(Context context, Subject[] data) {
        super(context, R.layout.listitem_subject, data);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View item = inflater.inflate(R.layout.listitem_subject, null);

        Subject c = (Subject) getItem(position);

        TextView CTitle = (TextView)item.findViewById(R.id.subject_title);
        CTitle.setText(c.getName());

        TextView CSTitle = (TextView)item.findViewById(R.id.subject_subtitle);
        CSTitle.setText(c.getCode()+" - GROUP: "+c.getGroupName()); //TODO: TRANSLATE

        return(item);
    }
}