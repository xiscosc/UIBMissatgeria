package com.fsc.uibmissatgeria.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.objects.Subject;
import com.fsc.uibmissatgeria.objects.Group;
import com.fsc.uibmissatgeria.objects.Message;
import com.fsc.uibmissatgeria.objects.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class Server {

    private final String SERVER_URL = "https://rhodes.joan-font.com/";
    private Context c;

    public Server(Context c) {
        this.c = c;

    }


    public Map<String, Object> getSubjects() {

        JSONObject reader = readFromServer(SERVER_URL + "user/subjects/");
        Map<String, Object> result = new HashMap<>();

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
                    result.put(Constants.RESULT_TOTAL, total);
                    result.put(Constants.RESULT_SUBJECTS, subjects);
                    return result;
                }
            } catch (Exception e) {
                try {
                    result.put(Constants.RESULT_ERROR, reader.getString("message"));
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        result.put(Constants.RESULT_ERROR, "Network Error"); //TODO: TRANSLATE
        return result;
    }


    public Map<String, Object> getMessages(Subject s, Group g) {

        JSONObject reader;
        Map<String, Object> result = new HashMap<>();

        if (g==null) {
            reader = readFromServer(SERVER_URL + "user/subjects/" + s.getId() + "/messages/");
        } else {
            reader = readFromServer(SERVER_URL + "user/subjects/" + s.getId() + "/groups/"
                    + g.getId() + "/messages/");
        }


        if (reader != null) {
            try {
                int total = reader.getInt("total");
                if (total > 0) {
                    JSONArray messageJsonArray = reader.getJSONArray("results");
                    ArrayList<Message> messages = new ArrayList<>();
                    for (int x = 0; x < total; x++) {
                        JSONObject messageJson = messageJsonArray.getJSONObject(x);
                        JSONObject userJson = messageJson.getJSONObject("sender");
                        messages.add( new Message(
                                messageJson.getInt("id"),
                                messageJson.getString("body"),
                                new User(userJson.getInt("id"), userJson.getString("first_name")
                                        + " " + userJson.getString("last_name")),
                                messageJson.getString("created_at")
                        ));
                    }
                    result.put(Constants.RESULT_TOTAL, total);
                    result.put(Constants.RESULT_MESSAGES, messages);
                    return result;
                }
            } catch (Exception e) {
                try {
                    result.put(Constants.RESULT_ERROR, reader.getString("message"));
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        result.put(Constants.RESULT_ERROR, "Network Error"); //TODO: TRANSLATE
        return result;
    }


    private JSONObject readFromServer(String url) {

        JSONObject obj = null;
        String strJson = null;
        HttpsURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            urlConnection = setUpConnection(url, "GET");
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
            e.printStackTrace();
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
        HttpsURLConnection urlConnection = null;

        if (g==null) {
            url = SERVER_URL+"user/subjects/"+s.getId()+"/messages/";
        } else {
            url = SERVER_URL+"user/subjects/"+s.getId()+"/groups/"+g.getId()+"/messages/";
        }

        try {
            urlConnection = setUpConnection(url, "POST");
            urlConnection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write("body="+body);
            writer.flush();
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            writer.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private HttpsURLConnection setUpConnection(String url, String method){
        HttpsURLConnection urlConnection = null;
        InputStream caInput = null;
        URL urlObj;

        try {
            urlObj = new URL(url);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            caInput = c.getResources().openRawResource(R.raw.rhodes);
            Certificate ca;
            ca = cf.generateCertificate(caInput);

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
            urlConnection.setRequestMethod(method);
            urlConnection.setRequestProperty("Authorization", getToken());

        } catch (MalformedURLException | CertificateException | NoSuchAlgorithmException |
                KeyStoreException | KeyManagementException | ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  urlConnection;
    }

    public boolean doLogin(String user, String password) {

        Boolean result = false;
        try {
            String url = SERVER_URL+"login/?user="+user+"&password="+password;
            JSONObject reader = readFromServer(url);
            String token = reader.getString("token");
            SharedPreferences settings = c.getSharedPreferences(Constants.SP_UIB, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("token", token);
            editor.commit();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getToken() {
        try {
            SharedPreferences settings = c.getSharedPreferences(Constants.SP_UIB, 0);
            return settings.getString("token", Constants.TK_FAIL);
        } catch (Exception e) {
            return Constants.TK_FAIL;
        }

    }

    public void removeToken() {
        try {
            SharedPreferences settings = c.getSharedPreferences(Constants.SP_UIB, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
