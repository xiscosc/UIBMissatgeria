package com.fsc.uibmissatgeria.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.activities.SubjectActivity;
import com.fsc.uibmissatgeria.adapters.SubjectAdapter;
import com.fsc.uibmissatgeria.objects.Subject;
import com.fsc.uibmissatgeria.api.Server;

import java.util.ArrayList;


public class SubjectsFragment extends Fragment {


    ProgressDialog pDialog;
    private RecyclerView recView;
    private ArrayList<Subject> subjects;
    private SubjectAdapter subjectAdapter;



    public SubjectsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_subject, container, false);

        recView = (RecyclerView) rootView.findViewById(R.id.list_subjects);


        loadSubjects();
        return rootView;
    }

    public void loadSubjects() {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Loading subjects..."); //TODO: TRANSLATE
        pDialog.setCancelable(false);
        pDialog.setMax(100);
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
        recView.setLayoutManager(
                new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));

    }

    public void startSubjectActivity(Subject s) {
        Intent intent = new Intent(getActivity(), SubjectActivity.class);
        intent.putExtra(Constants.SUBJECT_OBJ, s);
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
            Server s = new Server();
            return s.getSubjects();
        }

        @Override
        protected void onPostExecute(ArrayList<Subject> subjects) {
            ctx.subjects = subjects;
            ctx.createAdapter();
            pDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            pDialog.setProgress(0);
            pDialog.show();
        }
    }

}