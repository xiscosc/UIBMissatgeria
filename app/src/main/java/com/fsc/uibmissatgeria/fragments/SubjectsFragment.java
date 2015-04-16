package com.fsc.uibmissatgeria.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.activities.SubjectActivity;
import com.fsc.uibmissatgeria.adapters.SubjectAdapter;
import com.fsc.uibmissatgeria.models.ModelsManager;
import com.fsc.uibmissatgeria.models.Subject;
import com.fsc.uibmissatgeria.api.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SubjectsFragment extends Fragment {


    private RecyclerView recView;
    private ArrayList<Subject> subjects;
    private SubjectAdapter subjectAdapter;
    private ProgressBar loadingBar;



    public SubjectsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_subject, container, false);

        recView = (RecyclerView) rootView.findViewById(R.id.list_subjects);
        recView.setLayoutManager(
                new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        loadingBar = (ProgressBar) rootView.findViewById(R.id.subjects_loading);
        loadSubjects();
        return rootView;
    }

    public void loadSubjects() {
        ObtainSubjectsTask task = new ObtainSubjectsTask(SubjectsFragment.this);
        task.execute();
    }

    private void createAdapter() {
        subjectAdapter = new SubjectAdapter(subjects);
        subjectAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Subject s = subjects.get(recView.getChildAdapterPosition(v));
                startSubjectActivity(s);
            }
        });
        recView.setAdapter(subjectAdapter);
    }

    public void startSubjectActivity(Subject s) {
        Intent intent = new Intent(getActivity(), SubjectActivity.class);
        intent.putExtra(Constants.SUBJECT_OBJ, s.getId());
        startActivity(intent);
    }

    private class ObtainSubjectsTask extends AsyncTask<Void, Void, ArrayList<Subject>> {

        private SubjectsFragment ctx;

        public ObtainSubjectsTask(SubjectsFragment c) {
            super();
            ctx = c;
        }

        @Override
        protected ArrayList<Subject> doInBackground(Void... params) {
            ModelsManager mm = new ModelsManager(ctx.getActivity());
            return mm.getSubjects();
        }

        @Override
        protected void onPostExecute(ArrayList<Subject> subjects) {
            ctx.subjects =  subjects;
            ctx.createAdapter();
            loadingBar.setVisibility(View.GONE);
        }

    }

}