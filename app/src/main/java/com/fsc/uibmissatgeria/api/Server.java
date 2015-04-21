package com.fsc.uibmissatgeria.api;

import android.content.Context;
import android.graphics.AvoidXfermode;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.models.ModelsManager;
import com.fsc.uibmissatgeria.models.Subject;
import com.fsc.uibmissatgeria.models.SubjectGroup;
import com.fsc.uibmissatgeria.models.Message;
import com.fsc.uibmissatgeria.models.User;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class Server {

    private final String SERVER_URL = "https://rhodes.joan-font.com/";
    private Context c;
    private String token;

    public Server(Context c) {
        this.c = c;

    }

    public Map<String, Object> getSubjects() {

        JSONObject reader = readFromServer(SERVER_URL + "user/subjects/");
        Map<String, Object> result = new HashMap<>();

        if (reader != null) {
            try {
                JSONArray subjectJsonArray = reader.getJSONArray("results");
                List<Subject> subjectDbList = Subject.listAll(Subject.class);
                List<SubjectGroup> subjectGroupDbList = SubjectGroup.listAll(SubjectGroup.class);
                for (int x = 0; x < subjectJsonArray.length(); x++) {
                    JSONObject subjectJson = subjectJsonArray.getJSONObject(x);
                    JSONArray groupJsonArray = subjectJson.getJSONArray("groups");
                    Subject sbj;
                    sbj = new Subject(
                            subjectJson.getString("name"),
                            subjectJson.getInt("code"),
                            subjectJson.getInt("id")
                    );
                    if (!subjectDbList.contains(sbj)) {
                        sbj.save();
                        ModelsManager.generateSGDefault(sbj);
                        subjectDbList.add(sbj);
                    } else {
                        int index = subjectDbList.indexOf(sbj);
                        sbj = subjectDbList.get(index);
                    }

                    for (int y=0; y<groupJsonArray.length(); y++) {
                        JSONObject groupJson = groupJsonArray.getJSONObject(y);
                        SubjectGroup sbjg = new SubjectGroup(
                                groupJson.getInt("id"),
                                groupJson.getString("name"),
                                sbj
                        );
                        if (!subjectGroupDbList.contains(sbjg)) {
                            sbjg.save();
                            subjectGroupDbList.add(sbjg);
                        }
                    }
                }
                result.put(Constants.RESULT_SUBJECTS, subjectDbList);
                return result;
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


    public Map<String, Object> getMessages(Subject s, SubjectGroup g) {

        Map<String, Object> result = new HashMap<>();
        List<User> usersDbList = User.listAll(User.class);
        List<Message> messagesDbList = Message.find(Message.class,
                "SUBJECT_GROUP = ?",
                Long.toString(g.getId())
        );
        JSONObject reader;
        if (g.getIdApi()==Constants.DEFAULT_GROUP_ID) {
            reader = readFromServer(SERVER_URL + "user/subjects/" + s.getIdApi() + "/messages/");
        } else {
            reader = readFromServer(SERVER_URL + "user/subjects/" + s.getIdApi() + "/groups/"
                    + g.getIdApi() + "/messages/");
        }
        if (reader != null) {
            try {
                manageMessages(reader, messagesDbList, usersDbList, g);
                result.put(Constants.RESULT_MESSAGES, messagesDbList);
                return result;
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


    private List<Message> manageMessages(JSONObject reader, List<Message> messagesDbList, List<User> usersDbList, SubjectGroup g) throws JSONException {
        JSONArray messageJsonArray = reader.getJSONArray("results");
        List<Message> result = new ArrayList<>();
        for (int x = 0; x < messageJsonArray.length(); x++) {
            JSONObject messageJson = messageJsonArray.getJSONObject(x);
            JSONObject userJson = messageJson.getJSONObject("sender");

            User sender = new User(
                    userJson.getInt("id"),
                    userJson.getString("first_name"),
                    userJson.getString("last_name"),
                    userJson.getString("user"),
                    userJson.getString("type")
            );

            if (!usersDbList.contains(sender)) {
                sender.save();
                usersDbList.add(sender);
            } else {
                int index = usersDbList.indexOf(sender);
                sender = usersDbList.get(index);
            }

            Message msg  = new Message(
                    messageJson.getInt("id"),
                    messageJson.getString("body"),
                    sender,
                    messageJson.getString("created_at"),
                    g
            );

            if (!messagesDbList.contains(msg)) {
                msg.save();
                messagesDbList.add(msg);
                result.add(msg);
            }
        }
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


    public void sendMessageToGroup(Subject s, SubjectGroup g, String body) {
        String url;
        HttpsURLConnection urlConnection = null;

        if (g.getIdApi()==Constants.DEFAULT_GROUP_ID) {
            url = SERVER_URL+"user/subjects/"+s.getIdApi()+"/messages/";
        } else {
            url = SERVER_URL+"user/subjects/"+s.getIdApi()+"/groups/"
                    +g.getIdApi()+"/messages/";
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
        AccountUIB auib;

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

            auib = new AccountUIB(this.c);

            urlConnection = (HttpsURLConnection) urlObj.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setRequestMethod(method);
            urlConnection.setRequestProperty("Authorization", auib.getToken());

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
            this.token = reader.getString("token");
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getTokenRaw() {
        return this.token;
    }

    public User getUserByToken() {
        JSONObject reader = readFromServer(SERVER_URL + "user/");
        if (reader != null) {
            try {
                return new User(
                        reader.getInt("id"),
                        reader.getString("first_name"),
                        reader.getString("last_name"),
                        reader.getString("user"),
                        reader.getString("type")
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
