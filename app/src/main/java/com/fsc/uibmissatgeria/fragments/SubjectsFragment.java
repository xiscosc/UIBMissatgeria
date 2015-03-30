package com.fsc.uibmissatgeria.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.activities.MessagesActivity;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.adapters.SubjectAdapter;
import com.fsc.uibmissatgeria.objects.Subject;
import com.fsc.uibmissatgeria.api.Server;


public class SubjectsFragment extends Fragment {

    SubjectAdapter adapterSubject;
    ProgressDialog pDialog;
    ListView listView;


    public SubjectsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listchats, container, false);
        listView = (ListView) rootView.findViewById(R.id.list_item_c);
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

    private void createAdapter(final Subject[] subjects) {
        adapterSubject = new SubjectAdapter(getActivity(), subjects);
        listView.setAdapter(adapterSubject);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startMessages(subjects[position]);
            }
        });

    }

    public void startMessages(Subject c) {
        Intent intent = new Intent(getActivity(), MessagesActivity.class);
        intent.putExtra(Constants.SUBJECT_NAME, c.getName());
        intent.putExtra(Constants.SUBJECT_ID, c.getId());
        intent.putExtra(Constants.GROUP_ID, c.getFirstGroup().getId()); // TODO: MULTIGROUP
        intent.putExtra(Constants.GROUP_NAME, c.getFirstGroup().getName());
        intent.putExtra(Constants.SUBJECT_OBJ, c);
        startActivity(intent);
    }

    private class ObtainSubjectsTask extends AsyncTask<Void, Void, Subject[]> {

        private SubjectsFragment ctx;

        public ObtainSubjectsTask(SubjectsFragment c) {
            super();
            ctx = c;
        }

        @Override
        protected Subject[] doInBackground(Void... params) {
            Server s = new Server();
            return s.getSubjects();
        }

        @Override
        protected void onPostExecute(Subject[] subjects) {
            ctx.createAdapter(subjects);
            pDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            pDialog.setProgress(0);
            pDialog.show();
        }
    }

}