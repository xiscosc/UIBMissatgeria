package com.fsc.uibmissatgeria.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.models.SubjectGroup;

import java.util.ArrayList;

/**
 * Created by Xisco on 04/03/2015.
 */
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupsViewHolder>
        implements View.OnClickListener {

    public static class GroupsViewHolder extends RecyclerView.ViewHolder {

        private TextView groupTitle;

        public GroupsViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            groupTitle = (TextView) itemView.findViewById(R.id.group_title);
        }

        public void bindGroup(SubjectGroup g) {
            groupTitle.setText("Group "+g.getName()); //TODO: TRANSLATE
        }
    }

    private ArrayList<SubjectGroup> subjectGroups;
    private View.OnClickListener listener;

    public GroupAdapter(ArrayList<SubjectGroup> subjectGroups) {
        this.subjectGroups = subjectGroups;
    }

    @Override
    public GroupsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.listitem_group, viewGroup, false);

        itemView.setOnClickListener(this);

        return new GroupsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GroupsViewHolder viewHolder, int pos) {
        SubjectGroup item = subjectGroups.get(pos);
        viewHolder.bindGroup(item);

    }

    @Override
    public int getItemCount() {
        return subjectGroups.size();
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