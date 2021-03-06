package com.fsc.uibmissatgeria.ui.fragments;

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
import com.fsc.uibmissatgeria.ui.activities.PrincipalActivity;
import com.fsc.uibmissatgeria.ui.activities.SubjectActivity;
import com.fsc.uibmissatgeria.ui.adapters.SubjectAdapter;
import com.fsc.uibmissatgeria.managers.ModelManager;
import com.fsc.uibmissatgeria.models.Subject;

import java.util.List;


public class SubjectsFragment extends Fragment {


    private RecyclerView recView;
    private List<Subject> subjects;
    private SubjectAdapter subjectAdapter;
    private ProgressBar loadingBar;
    private ModelManager mm;



    public SubjectsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_subject, container, false);

        recView = (RecyclerView) rootView.findViewById(R.id.subjects_list);
        recView.setLayoutManager(
                new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        loadingBar = (ProgressBar) rootView.findViewById(R.id.subjects_loading);
        mm = new ModelManager(getActivity());
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

    @Override
    public void onResume() {
        super.onResume();
        PrincipalActivity ctx = (PrincipalActivity) getActivity();
        if (ctx.accountUIB.isLogged()) {
            loadingBar.setVisibility(View.VISIBLE);
            loadSubjects();
        }
    }


    private class ObtainSubjectsTask extends AsyncTask<Void, Void, List<Subject>> {

        private SubjectsFragment ctx;

        public ObtainSubjectsTask(SubjectsFragment c) {
            super();
            ctx = c;
        }

        @Override
        protected List<Subject> doInBackground(Void... params) {
            return ctx.mm.getSubjects();
        }

        @Override
        protected void onPostExecute(List<Subject> subjects) {
            ctx.mm.showError();
            ctx.subjects =  subjects;
            ctx.createAdapter();
            loadingBar.setVisibility(View.GONE);
        }

    }

}