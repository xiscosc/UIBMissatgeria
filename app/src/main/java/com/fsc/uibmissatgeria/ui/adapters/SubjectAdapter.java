package com.fsc.uibmissatgeria.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.models.Subject;

import java.util.List;

/**
 * Created by Xisco on 04/03/2015.
 */

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectsViewHolder>
        implements View.OnClickListener {

    public static class SubjectsViewHolder extends RecyclerView.ViewHolder {

        private TextView subjectTitle;
        private TextView subjectSubTitle;

        public SubjectsViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            subjectTitle = (TextView) itemView.findViewById(R.id.subject_title);
            subjectSubTitle = (TextView) itemView.findViewById(R.id.subject_subtitle);
        }

        public void bindSubject(Subject s) {
            subjectTitle.setText(s.getName());
            subjectSubTitle.setText(Integer.toString(s.getCode()));
        }
    }

    private List<Subject> subjects;
    private View.OnClickListener listener;

    public SubjectAdapter(List<Subject> subjects) {
        this.subjects = subjects;
    }

    @Override
    public SubjectsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.listitem_subject, viewGroup, false);

        itemView.setOnClickListener(this);

        return new SubjectsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SubjectsViewHolder viewHolder, int pos) {
        Subject item = subjects.get(pos);
        viewHolder.bindSubject(item);
    }

    @Override
    public int getItemCount() {
        return subjects.size();
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