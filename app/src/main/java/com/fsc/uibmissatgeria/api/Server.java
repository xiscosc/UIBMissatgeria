package com.fsc.uibmissatgeria.api;

import android.content.Context;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.models.Conversation;
import com.fsc.uibmissatgeria.models.MessageConversation;
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

        JSONArray subjectJsonArray = readArrayFromServer(SERVER_URL + "user/subjects/");
        Map<String, Object> result = new HashMap<>();

        if (subjectJsonArray != null) {
            try {
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
              e.printStackTrace();
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
        JSONObject reader = readObjectFromServer(makeMessagesUrl(s, g, null, null, null));
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

    /*
    Returns new messages ordered ASC for better insertion in top-RecyclerView
     */

    public Map<String, Object> getNewMessages(Subject s, SubjectGroup g, Message last) {

        Map<String, Object> result = new HashMap<>();
        List<User> usersDbList = User.listAll(User.class);
        List<Message> messagesDbList = Message.find(Message.class,
                "SUBJECT_GROUP = ? AND ID_API > ? ORDER BY ID_API ASC",
                Long.toString(g.getId()),
                Long.toString(last.getIdApi())
        );
        JSONObject reader = readObjectFromServer(makeMessagesUrl(s, g, last, "asc", "next"));
        if (reader != null) {
            try {
                manageMessages(reader, messagesDbList, usersDbList, g);
                Boolean x = reader.getBoolean("more");
                while (x) {
                    Message middle = messagesDbList.get(messagesDbList.size()-1);
                    reader = readObjectFromServer(makeMessagesUrl(s, g, middle, "asc", "next"));
                    if (reader != null) {
                        x = reader.getBoolean("more");
                        manageMessages(reader, messagesDbList, usersDbList, g);
                    } else {
                        x = false;
                    }
                }
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


    /*
    Returns older messages ordered DESC for better insertion in bottom-RecyclerView
     */

    public Map<String, Object> getOlderMessages(Subject s, SubjectGroup g, Message first) {

        Map<String, Object> result = new HashMap<>();
        List<User> usersDbList = User.listAll(User.class);
        List<Message> messagesDbList = Message.find(Message.class,
                "SUBJECT_GROUP = ? AND ID_API < ? ORDER BY ID_API DESC",
                Long.toString(g.getId()),
                Long.toString(first.getIdApi())
        );
        JSONObject reader = readObjectFromServer(makeMessagesUrl(s, g, first, "desc", "previous"));
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
        JSONArray messageJsonArray = reader.getJSONArray("messages");
        List<Message> result = new ArrayList<>();
        for (int x = 0; x < messageJsonArray.length(); x++) {
            JSONObject messageJson = messageJsonArray.getJSONObject(x);
            JSONObject userJson = messageJson.getJSONObject("sender");

            User sender = new User(
                    userJson.getInt("id"),
                    userJson.getString("first_name"),
                    userJson.getString("last_name"),
                    userJson.getString("user"),
                    userJson.getInt("type")
            );

            if (!usersDbList.contains(sender)) {
                sender.save();
                usersDbList.add(sender);
            } else {
                int index = usersDbList.indexOf(sender);
                sender = usersDbList.get(index);
            }

            Message msg  = new Message(
                    messageJson.getLong("id"),
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


    private JSONObject readObjectFromServer(String url) {
        JSONObject result = null;
        try {
            String data = readFromServer(url);
            result = new JSONObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    private JSONArray readArrayFromServer(String url){
        JSONArray result = null;
        try {
            String data = readFromServer(url);
            result = new JSONArray(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    private String readFromServer(String url) {

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

        return strJson;
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
            JSONObject reader = readObjectFromServer(url);
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
        JSONObject reader = readObjectFromServer(SERVER_URL + "user/");
        if (reader != null) {
            try {
                return new User(
                        reader.getInt("id"),
                        reader.getString("first_name"),
                        reader.getString("last_name"),
                        reader.getString("user"),
                        reader.getInt("type")
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

        /*
            PARAMS PAGINATION
            message_id
            order (asc, desc)
            direction (next, previous)
     */

    private String makeMessagesUrl(Subject s, SubjectGroup g, Message m, String order, String direction) {
        String result;
        if (g.getIdApi()==Constants.DEFAULT_GROUP_ID) {
            result = SERVER_URL + "user/subjects/" + s.getIdApi() + "/messages/";
        } else {
            result = SERVER_URL + "user/subjects/" + s.getIdApi() + "/groups/"+ g.getIdApi() + "/messages/";
        }
        if (m != null) {
            result += m.getIdApi()+"/";

            if (direction != null) {
                result += direction+"/";
            }

            if (order != null) {
                result += "?order="+order;
            }

        }
        return result;
    }

    public Map<String, Object> getConversations() {
        Map<String, Object> result = new HashMap<>();
        AccountUIB accountUIB = new AccountUIB(c);
        List<Conversation> converDB = Conversation.listAll(Conversation.class);
        List<User> peers = User.listAll(User.class);
        User usr = accountUIB.getUser();
        JSONArray reader;

        try {
            reader = readArrayFromServer(SERVER_URL + "user/chats/");
            if (reader != null) {
                manageConversations(reader, converDB, peers, usr);
                if (converDB.isEmpty()) {
                    result.put(Constants.RESULT_ERROR, "Error getting conversations"); //TODO: TRANSLATE
                } else {
                    result.put(Constants.RESULT_CONVERSATIONS, converDB);
                }
            } else {
                result.put(Constants.RESULT_ERROR, "Error getting conversations"); //TODO: TRANSLATE
            }
        } catch (JSONException e) {
            e.printStackTrace();
            result.put(Constants.RESULT_ERROR, "Network Error"); //TODO: TRANSLATE
        }

        return result;
    }

    private void manageConversations(JSONArray reader, List<Conversation> converDB, List<User> peers, User usr) throws JSONException{
        for (int x = 0; x < reader.length(); x++) {
            JSONObject conversation = reader.getJSONObject(x);
            JSONObject userJson = conversation.getJSONObject("user");

            User peer = new User(
                    userJson.getInt("id"),
                    userJson.getString("first_name"),
                    userJson.getString("last_name"),
                    userJson.getString("user"),
                    userJson.getInt("type"),
                    true
            );

            if (peers.contains(peer)) {
                int index = peers.indexOf(peer);
                peer = peers.get(index);
                peer.setPeer(true);
                peer.save();
            } else {
                peer.save();
                peers.add(peer);
            }

            Conversation c = new Conversation(peer);

            if (converDB.contains(c)) {
                int index = converDB.indexOf(c);
                c = converDB.get(index);
            }  else {
                c.save();
            }
            JSONObject messageJson = conversation.getJSONObject("last_message");
            JSONObject userJson2 = messageJson.getJSONObject("recipient");

            User recipient = new User(
                    userJson2.getInt("id"),
                    userJson2.getString("first_name"),
                    userJson2.getString("last_name"),
                    userJson2.getString("user"),
                    userJson2.getInt("type")
            );

            MessageConversation mc = new MessageConversation(
                messageJson.getLong("id"),
                    messageJson.getString("body"),
                    messageJson.getString("created_at"),
                    c,
                    recipient.equals(usr)
            );

           MessageConversation lastc = c.getLastMessage();

            if (lastc == null || !mc.equals(lastc)) {
                mc.save();
                c.setLastMessageId(mc.getIdApi());
                c.save();
            }

        }
    }

    public Map<String, Object> getPeers() {
        Map<String, Object> result = new HashMap<>();
        AccountUIB accountUIB = new AccountUIB(c);
        List<User> usersDB = User.listAll(User.class);
        List<User> peers = new ArrayList<>();
        User usr = accountUIB.getUser();
        JSONArray reader;
        try {
            if (usr.getType() == Constants.TYPE_TEACHER) {
                reader = readArrayFromServer(SERVER_URL + "user/students/");
                if (reader!=null) {
                    managePeers(reader, usersDB, peers);
                }
            }
            reader = readArrayFromServer(SERVER_URL + "user/teachers/");
            if (reader!=null) {
                managePeers(reader, usersDB, peers);
            }
            if (peers.isEmpty()) {
                result.put(Constants.RESULT_ERROR, "Error getting peers"); //TODO: TRANSLATE
            } else {
                result.put(Constants.RESULT_PEERS, peers);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            result.put(Constants.RESULT_ERROR, "Network Error"); //TODO: TRANSLATE
        }

        return result;
    }

    private void managePeers(JSONArray reader, List<User> usersDB, List<User> peers) throws JSONException{
        for (int x = 0; x < reader.length(); x++) {
            JSONObject userJson = reader.getJSONObject(x);
            User peer = new User(
                    userJson.getInt("id"),
                    userJson.getString("first_name"),
                    userJson.getString("last_name"),
                    userJson.getString("user"),
                    userJson.getInt("type"),
                    true
            );

            if (usersDB.contains(peer)) {
                int index = usersDB.indexOf(peer);
                peer = usersDB.get(index);
                peer.setPeer(true);
                peer.save();
                peers.add(peer);
            } else {
                peer.save();
                usersDB.add(peer);
                peers.add(peer);
            }
        }

    }
}
