package com.fsc.uibmissatgeria.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fsc.uibmissatgeria.R;

/**
 * Created by Xisco on 04/03/2015.
 */
public class CourseAdapter extends ArrayAdapter<Course> {

    public CourseAdapter(Context context, Course[] data) {
        super(context, R.layout.listitem_course, data);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View item = inflater.inflate(R.layout.listitem_course, null);

        Course c = (Course) getItem(position);

        TextView CTitle = (TextView)item.findViewById(R.id.CTitle);
        CTitle.setText(c.getName());

        TextView CSTitle = (TextView)item.findViewById(R.id.CSTitle);
        CSTitle.setText(c.getCode()+" - GRUP "+c.getGroup());

        return(item);
    }
}