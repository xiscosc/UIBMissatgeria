package com.fsc.uibmissatgeria.api;

import android.content.Context;
import android.content.res.Resources;

import com.fsc.uibmissatgeria.R;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class Server {

    private final String SERVER_URL = "https://rhodes.joan-font.com/";
    private Context c;

    public Server(Context c) {
        this.c = c;

    }


    public ArrayList<Subject> getSubjects() {

        JSONObject reader = getFromServer(SERVER_URL+"user/subjects/");
        if (reader != null) {
            try {
                int total = reader.getInt("total");
                if (total > 0) {
                    JSONArray subjectJsonArray = reader.getJSONArray("results");
                    ArrayList<Subject> subjects = new ArrayList<>();
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

                        subjects.add(
                                new Subject(
                                subjectJson.getString("name"),
                                groups,
                                subjectJson.getInt("code"),
                                subjectJson.getInt("id")
                              )
                        );
                    }
                    return subjects;
                }
            } catch (Exception e) {
                System.out.printf("" + e);
                return new ArrayList<>();
            }
        }
        return new ArrayList<>();
    }


    public ArrayList<Message> getMessages(Subject s, Group g) {

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
                    ArrayList<Message> messages = new ArrayList<>();
                    User us3 = new User(3, "Usuario de prueba");
                    for (int x = 0; x < total; x++) {
                        JSONObject messageJson = messageJsonArray.getJSONObject(x);
                        messages.add( new Message(
                                messageJson.getInt("id"),
                                messageJson.getString("body"),
                                us3,
                                messageJson.getString("created_at")
                        ));
                    }
                    return messages;
                }
            } catch (Exception e) {
                System.out.printf(e.toString());
                return new ArrayList<>();
            }
        }
        return new ArrayList<>();


    }


    private JSONObject getFromServer(String url) {

        JSONObject obj = null;
        String strJson = null;
        HttpsURLConnection urlConnection = null;
        BufferedReader reader = null;
        URL urlObj;

        try {
            urlObj = new URL(url);
        } catch (MalformedURLException e) {
            return obj;
        }

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = c.getResources().openRawResource(R.raw.rhodes);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

            urlConnection = (HttpsURLConnection) urlObj.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Authorization", "cP2AlEWpd9PjcsOf7qKm1/AB6CPf0dD0LI5GK3DZ1c8=");
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


        } catch (Exception e) {
            strJson = null;
            System.out.println(e.toString());
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
