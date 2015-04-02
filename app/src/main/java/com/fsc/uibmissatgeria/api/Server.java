package com.fsc.uibmissatgeria.api;

import com.fsc.uibmissatgeria.objects.Subject;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private final String SERVER_URL = "http://rhodes.joan-font.com/";

    public Server() {
    }


    public Subject[] getSubjects() {

        JSONObject reader = getFromServer(SERVER_URL+"user/subjects/");
        if (reader != null) {
            try {
                int total = reader.getInt("total");
                if (total > 0) {
                    JSONArray subjectJsonArray = reader.getJSONArray("results");
                    Subject[] subjects = new Subject[subjectJsonArray.length()];
                    for (int x = 0; x < subjectJsonArray.length(); x++) {
                        ArrayList<Group> groups = new ArrayList<>();
                        JSONObject subjectJson = subjectJsonArray.getJSONObject(x);

                        JSONArray groupJsonArray = subjectJson.getJSONArray("groups");
                        for (int y=0; y<groupJsonArray.length(); y++) {
                            JSONObject groupJson = groupJsonArray.getJSONObject(y);
                            groups.add(
                                    new Group(
                                            groupJson.getInt("id"),
                                            groupJson.getString("name")
                                    )
                            );
                        }

                        subjects[x] = new Subject(
                                subjectJson.getString("name"),
                                groups,
                                subjectJson.getInt("code"),
                                subjectJson.getInt("id")
                        );
                    }
                    return subjects;
                }
            } catch (Exception e) {
                System.out.printf("" + e);
                return new Subject[0];
            }
        }
        return new Subject[0];
    }


    public Message[] getMessages(Subject s, Group g) {

        JSONObject reader;

        if (g==null) {
            reader = getFromServer(SERVER_URL+"user/subjects/"+s.getId()+"/messages/");
        } else {
            reader = getFromServer(SERVER_URL+"user/subjects/"+s.getId()+"/groups/"+g.getId()+"/messages/");
        }


        if (reader != null) {
            try {
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
                System.out.printf(e.toString());
                return new Message[0];
            }
        }
        return new Message[0];


    }


    private JSONObject getFromServer(String url) {

        JSONObject obj = null;
        String strJson = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        URL urlObj;

        try {
            urlObj = new URL(url);
        } catch (MalformedURLException e) {
            return obj;
        }

        try {
            urlConnection = (HttpURLConnection) urlObj.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Authorization", "4");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                strJson = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                strJson = null;
            } else {
                strJson = buffer.toString();
            }


        } catch (IOException e) {
            strJson = null;
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

        if (strJson != null) {
            try {
                obj = new JSONObject(strJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return obj;
    }


    public void sendMessageToGroup(Subject s, Group g, String body) {
        String url;

        if (g==null) {
            url = SERVER_URL+"user/subjects/"+s.getId()+"/messages/";
        } else {
            url = SERVER_URL+"user/subjects/"+s.getId()+"/groups/"+g.getId()+"/messages/";
        }


        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            post.addHeader("Authorization", "4");
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            pairs.add(new BasicNameValuePair("body", body));
            post.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
            HttpResponse response = client.execute(post);
        } catch (Exception e) {

        }


    }

}
