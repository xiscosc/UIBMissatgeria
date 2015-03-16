package com.fsc.uibmissatgeria.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.adapters.Course;
import com.fsc.uibmissatgeria.adapters.CourseAdapter;


public class CoursesFragment extends Fragment {

    CourseAdapter adapterCourse;

    public CoursesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listchats, container, false);

        Course[] courses = new Course[] {
                new Course("Estructura de dades", "MATI", 5654343),
                new Course("Gestió de projectes", "MATI", 1234343),
                new Course("Sistemes operatius 2", "TARDA", 23213),
                new Course("Programació 2", "MATI", 32221),
                new Course("Xarxes Avançades", "TARDA", 576723),
        };

        /**
         * ASYNC TASK

        String stringUrl = "rhodes.joan-font.com/subjects";
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {
            textView.setText("No network connection available.");
        }

         */

        adapterCourse = new CourseAdapter(getActivity(), courses);

        ListView listView = (ListView) rootView.findViewById(R.id.list_item_c);
        listView.setAdapter(adapterCourse);

        return rootView;
    }

}