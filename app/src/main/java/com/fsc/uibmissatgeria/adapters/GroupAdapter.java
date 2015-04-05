package com.fsc.uibmissatgeria.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.objects.Group;

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
            groupTitle = (TextView) itemView.findViewById(R.id.group_title);
        }

        public void bindGroup(Group g) {
            groupTitle.setText("Group "+g.getName()); //TODO: TRANSLATE
        }
    }

    private ArrayList<Group> groups;
    private View.OnClickListener listener;

    public GroupAdapter(ArrayList<Group> groups) {
        this.groups = groups;
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
        Group item = groups.get(pos);
        viewHolder.bindGroup(item);
    }

    @Override
    public int getItemCount() {
        return groups.size();
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