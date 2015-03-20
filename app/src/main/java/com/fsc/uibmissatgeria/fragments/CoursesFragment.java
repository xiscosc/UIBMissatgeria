package com.fsc.uibmissatgeria.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.adapters.Course;
import com.fsc.uibmissatgeria.adapters.CourseAdapter;

import com.fsc.uibmissatgeria.api.Server;

import java.util.ArrayList;
import java.util.List;


public class CoursesFragment extends Fragment {

    CourseAdapter adapterCourse;
    ProgressDialog pDialog;
    ListView listView;


    public CoursesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listchats, container, false);
        listView = (ListView) rootView.findViewById(R.id.list_item_c);
        loadCourses();

        return rootView;
    }

    public void loadCourses() {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Loading courses...");
        pDialog.setCancelable(false);
        pDialog.setMax(100);
        ObtainCoursesTask task = new ObtainCoursesTask(CoursesFragment.this);
        task.execute();
    }

    public void createAdapter(final Course[] courses) {
        adapterCourse = new CourseAdapter(getActivity(), courses);
        listView.setAdapter(adapterCourse);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   //TODO: CAMBIOS
            }
        });

    }

    private class ObtainCoursesTask extends AsyncTask<Void, Void, Course[]> {

        private CoursesFragment ctx;

        public ObtainCoursesTask(CoursesFragment c) {
            super();
            ctx = c;
        }

        @Override
        protected Course[] doInBackground(Void... params) {
            Server s = new Server();
            return s.getCourses();
        }

        @Override
        protected void onPostExecute(Course[] courses) {
            ctx.createAdapter(courses);
            pDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            pDialog.setProgress(0);
            pDialog.show();
        }
    }

}