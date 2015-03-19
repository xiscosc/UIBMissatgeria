package com.fsc.uibmissatgeria.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.adapters.Course;
import com.fsc.uibmissatgeria.adapters.CourseAdapter;

import com.fsc.uibmissatgeria.api.Server;


public class CoursesFragment extends Fragment {

    CourseAdapter adapterCourse;
    ProgressDialog pDialog;

    public CoursesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listchats, container, false);

        /*Course[] courses = new Course[] {
                new Course("Estructura de dades", "MATI", 5654343),
                new Course("Gestió de projectes", "MATI", 1234343),
                new Course("Sistemes operatius 2", "TARDA", 23213),
                new Course("Programació 2", "MATI", 32221),
                new Course("Xarxes Avançades", "TARDA", 576723),
        };*/

        Course[] courses = new Course[0];


        adapterCourse = new CourseAdapter(getActivity(), courses);

        ListView listView = (ListView) rootView.findViewById(R.id.list_item_c);
        listView.setAdapter(adapterCourse);

        loadCourses();

        return rootView;
    }

    public void loadCourses() {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Loading courses...");
        pDialog.setCancelable(false);
        pDialog.setMax(100);
        ObtainCoursesTask task = new ObtainCoursesTask();
        task.execute();
    }

    private class ObtainCoursesTask extends AsyncTask<Void, Void, Course[]> {

        @Override
        protected Course[] doInBackground(Void... params) {

            Server s = new Server();
            Course[] f = s.getCourses();

            Course[] courses = new Course[] {
                    new Course("Estructura de dades", "MATI", 5654343),
                    new Course("Gestió de projectes", "MATI", 1234343),
                    new Course("Sistemes operatius 2", "TARDA", 23213),
                    new Course("Programació 2", "MATI", 32221),
                    new Course("Xarxes Avançades", "TARDA", 576723),
            };

            return courses;
        }

        @Override
        protected void onPostExecute(Course[] courses) {
            //adapterCourse.clear();
            //adapterCourse.addAll(courses);
            pDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            pDialog.setProgress(0);
            pDialog.show();
        }
    }

}