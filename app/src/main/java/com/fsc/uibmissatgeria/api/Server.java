package com.fsc.uibmissatgeria.api;


import com.fsc.uibmissatgeria.adapters.Course;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Server {

    private URL urlObj;
    private HttpURLConnection urlConnection;
    private BufferedReader reader;

    public Server() {}


    public Course[] getCourses() {

        try {
            urlObj = new URL("http://rhodes.joan-font.com/subjects/");
        }catch (MalformedURLException e) {
            return new Course[0];
        }

        String ret = getFromServer();
        if (ret!=null) {
            try {
                JSONObject reader = new JSONObject(ret);
                int total = reader.getInt("total");
                if (total>0) {
                    JSONArray ja = reader.getJSONArray("results");
                    ArrayList<Course> courses = new ArrayList<Course>();
                    for (int x=0; x<ja.length(); x++) {
                        JSONObject obj = (JSONObject) ja.get(x);
                        Course c = new Course(obj.getString("name"), "GRUP: "+obj.getInt("id"), obj.getInt("code"));
                        courses.add(c);
                    }
                    return (Course[]) courses.toArray();
                }
            } catch (Exception e){
                return new Course[0];
            }

        }
        return new Course[0];
    }


    private String getFromServer() {
        String ret;
        try {
            urlConnection = (HttpURLConnection) urlObj.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Authorization", "1");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                ret = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                ret = null;
            } else {
                ret = buffer.toString();
            }


        } catch (IOException e) {
            ret = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                }
            }
        }

        return ret;
    }


}
