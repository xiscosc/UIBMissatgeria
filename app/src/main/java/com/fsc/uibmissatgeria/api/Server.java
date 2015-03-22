package com.fsc.uibmissatgeria.api;


import com.fsc.uibmissatgeria.objects.Course;
import com.fsc.uibmissatgeria.objects.Group;
import com.fsc.uibmissatgeria.objects.Message;
import com.fsc.uibmissatgeria.objects.User;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private URL urlObj;
    private HttpURLConnection urlConnection;
    private BufferedReader reader;

    private final String SERVER_URL = "http://rhodes.joan-font.com/";

    public Server() {
    }


    public Course[] getCourses() {

        try {
            urlObj = new URL(SERVER_URL+"user/subjects/");
        } catch (MalformedURLException e) {
            return new Course[0];
        }

        String ret = getFromServer();
        if (ret != null) {
            try {
                JSONObject reader = new JSONObject(ret);
                int total = reader.getInt("total");
                if (total > 0) {
                    JSONArray courseJsonArray = reader.getJSONArray("results");
                    Course[] courses = new Course[courseJsonArray.length()];
                    for (int x = 0; x < courseJsonArray.length(); x++) {
                        ArrayList<Group> groups = new ArrayList<>();
                        JSONObject courseJson = courseJsonArray.getJSONObject(x);

                        JSONArray groupJsonArray = courseJson.getJSONArray("groups");
                        for (int y=0; y<groupJsonArray.length(); y++) {
                            JSONObject groupJson = groupJsonArray.getJSONObject(y);
                            groups.add(
                                    new Group(
                                            groupJson.getInt("id"),
                                            groupJson.getString("name")
                                    )
                            );
                        }

                        /*JSONObject groupJson = courseJson.getJSONObject("group");
                        groups.add(
                                new Group(
                                        groupJson.getInt("id"),
                                        groupJson.getString("name")
                                )
                        );*/

                        courses[x] = new Course(
                                courseJson.getString("name"),
                                groups,
                                courseJson.getInt("code"),
                                courseJson.getInt("id")
                        );
                    }
                    return courses;
                }
            } catch (Exception e) {
                System.out.printf("" + e);
                return new Course[0];
            }
        }
        return new Course[0];
    }


    public Message[] getMessages(int idGroup, int idCourse) {
        try {
            urlObj = new URL(SERVER_URL+"user/subjects/"+idCourse+"/groups/"+idGroup+"/messages/");
        } catch (MalformedURLException e) {
            return new Message[0];
        }

        String ret = getFromServer();

        if (ret != null) {
            try {
                JSONObject reader = new JSONObject(ret);
                int total = reader.getInt("total");
                if (total > 0) {
                    JSONArray messageJsonArray = reader.getJSONArray("results");
                    Message[] messages = new Message[total];
                    User us3 = new User(3, "Usuario de prueba");
                    for (int x = 0; x < total; x++) {
                        JSONObject messageJson = messageJsonArray.getJSONObject(x);
                        messages[x] = new Message(
                                messageJson.getInt("id"),
                                messageJson.getString("body"),
                                us3,
                                messageJson.getString("created_at")
                        );
                    }
                    return messages;
                }
            } catch (Exception e) {
                System.out.printf("" + e);
                return new Message[0];
            }
        }
        return new Message[0];


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


    public void sendMessage(int idGroup, int idCourse, String body) {
        try {
            HttpClient client = new DefaultHttpClient();

            HttpPost post = new HttpPost(SERVER_URL+"user/subjects/"+idCourse+"/groups/"+idGroup+"/messages/");
            post.addHeader("Authorization", "1");
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            pairs.add(new BasicNameValuePair("body", body));
            post.setEntity(new UrlEncodedFormEntity(pairs));
            HttpResponse response = client.execute(post);
        } catch (Exception e) {

        }


    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
