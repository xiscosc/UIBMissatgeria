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

import com.fsc.uibmissatgeria.MessagesActivity;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.adapters.CourseAdapter;
import com.fsc.uibmissatgeria.adapters.MessageAdapter;
import com.fsc.uibmissatgeria.api.Server;
import com.fsc.uibmissatgeria.objects.Course;
import com.fsc.uibmissatgeria.objects.Message;
import com.fsc.uibmissatgeria.objects.User;

import java.util.Date;


public class MessagesFragment extends Fragment {

    MessageAdapter adapterMessage;
    ProgressDialog pDialog;
    ListView listView;


    public MessagesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messages, container, false);
        listView = (ListView) rootView.findViewById(R.id.list_item_messages);

        User us1 = new User(1, "Francesc Sastre Cabot");
        User us2 = new User(2, "Joan Font Rosillo");
        User us3 = new User(3, "Marc Perelló Ferrer");
        Message[] messages = new Message[]{
                new Message("Mensaje de test 1", us3, new Date()),
                new Message("Mensaje de test 3\nMensaje de test 3\nMensaje de test 3", us2, new Date()),
                new Message("Mensaje de test 2", us1, new Date()),

        };

        adapterMessage = new MessageAdapter(getActivity(), messages);
        listView.setAdapter(adapterMessage);

        return rootView;
    }

    /*public void loadCourses() {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Loading courses..."); //TODO: TRANSLATE
        pDialog.setCancelable(false);
        pDialog.setMax(100);
        ObtainCoursesTask task = new ObtainCoursesTask(MessagesFragment.this);
        task.execute();
    }

    private void createAdapter(final Course[] courses) {
        adapterCourse = new CourseAdapter(getActivity(), courses);
        listView.setAdapter(adapterCourse);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startMessages(courses[position]);
            }
        });

    }

    public void startMessages(Course c) {
        Intent intent = new Intent(getActivity(), MessagesActivity.class);
        intent.putExtra("CNAME", c.getName());
        startActivity(intent);
    }

    private class ObtainCoursesTask extends AsyncTask<Void, Void, Course[]> {

        private MessagesFragment ctx;

        public ObtainCoursesTask(MessagesFragment c) {
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
    }*/

}