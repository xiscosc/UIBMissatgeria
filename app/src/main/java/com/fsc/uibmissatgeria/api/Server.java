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
import com.orm.query.Select;

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
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class Server {

    private final String SERVER_URL = "https://rhodes.joan-font.com/";
    private Context c;
    private String netError;
    private String token;

    public Server(Context c) {
        this.c = c;
        netError = c.getResources().getString(R.string.network_error);
    }

    public String getSubjects() {

        JSONArray subjectJsonArray = readArrayFromServer(SERVER_URL + "user/subjects/");
        String result;

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
                return null;
            } catch (Exception e) {
              e.printStackTrace();
            }
        }

        result =  netError;  
        return result;
    }



    public String getMessages(Subject s, SubjectGroup g) {

        String result = null;
        List<User> usersDbList = User.listAll(User.class);
        List<Message> messagesDbList = Message.find(Message.class,
                "SUBJECT_GROUP = ?",
                Long.toString(g.getId())
        );
        JSONObject reader = readObjectFromServer(makeMessagesUrl(s, g, null, null));
        if (reader != null) {
            try {
                manageMessages(reader, messagesDbList, usersDbList, g);
            } catch (Exception e) {
                try {
                    result = reader.getString("message");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    result = netError;  
                }
            }
        } else {
            result = netError;  
        }

        return result;
    }



    public String getNewMessages(Subject s, SubjectGroup g, Message last) {

        String result;
        List<User> usersDbList = User.listAll(User.class);
        List<Message> messagesDbList = Message.find(Message.class,
                "SUBJECT_GROUP = ? AND ID_API > ? ORDER BY ID_API ASC",
                Long.toString(g.getId()),
                Long.toString(last.getIdApi())
        );
        JSONObject reader = readObjectFromServer(makeMessagesUrl(s, g, last, "next"));
        if (reader != null) {
            try {
                manageMessages(reader, messagesDbList, usersDbList, g);
                Boolean x = reader.getBoolean("more");
                while (x) {
                    Message middle = messagesDbList.get(messagesDbList.size()-1);
                    reader = readObjectFromServer(makeMessagesUrl(s, g, middle, "next"));
                    if (reader != null) {
                        x = reader.getBoolean("more");
                        manageMessages(reader, messagesDbList, usersDbList, g);
                    } else {
                        x = false;
                    }
                }
                return null;
            } catch (Exception e) {
                try {
                    result =  reader.getString("message");
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        result =   netError;  
        return result;
    }



    public String getOlderMessages(Subject s, SubjectGroup g, Message first) {

        String result;
        List<User> usersDbList = User.listAll(User.class);
        List<Message> messagesDbList = Message.find(Message.class,
                "SUBJECT_GROUP = ? AND ID_API < ? ORDER BY ID_API DESC",
                Long.toString(g.getId()),
                Long.toString(first.getIdApi())
        );
        JSONObject reader = readObjectFromServer(makeMessagesUrl(s, g, first, "previous"));
        if (reader != null) {
            try {
                manageMessages(reader, messagesDbList, usersDbList, g);
                return null;
            } catch (Exception e) {
                try {
                    result = reader.getString("message");
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        result = netError;  
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
        if (g.getIdApi()==Constants.DEFAULT_GROUP_ID) {
            url = SERVER_URL+"user/subjects/"+s.getIdApi()+"/messages/";
        } else {
            url = SERVER_URL+"user/subjects/"+s.getIdApi()+"/groups/"
                    +g.getIdApi()+"/messages/";
        }
        sendMessage(url, body);
    }



    public void sendMessageToConversation(Conversation c, String body) {
        String url;
        url = SERVER_URL+"user/chats/"+c.getPeer().getIdApi()+"/messages/";
        sendMessage(url, body);
    }


    private void sendMessage(String url, String body) {
        HttpsURLConnection urlConnection = null;
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

    private String makeMessagesUrl(Subject s, SubjectGroup g, Message m, String direction) {
        String result;
        if (g.getIdApi()==Constants.DEFAULT_GROUP_ID) {
            result = SERVER_URL + "user/subjects/" + s.getIdApi() + "/messages/";
        } else {
            result = SERVER_URL + "user/subjects/" + s.getIdApi() + "/groups/"+ g.getIdApi() + "/messages/";
        }
        if (m != null && direction != null) {
            result += m.getIdApi()+"/";
            result += direction+"/";
        }
        return result;
    }

    private String makeConversationsUrl(Conversation c, MessageConversation m, String direction) {
        String result;
        result = SERVER_URL + "user/chats/" + c.getPeer().getIdApi() + "/messages/";
        if (m != null && direction != null) {
            result += m.getIdApi()+"/";
            result += direction+"/";
        }
        return result;
    }

    public String getConversations() {
        String result = null;
        AccountUIB accountUIB = new AccountUIB(c);
        List<Conversation> converDB = Conversation.listAll(Conversation.class);
        List<User> peers = User.listAll(User.class);
        User usr = accountUIB.getUser();
        JSONArray reader;

        try {
            reader = readArrayFromServer(SERVER_URL + "user/chats/");
            if (reader != null) {
                manageConversations(reader, converDB, peers, usr);
            } else {
                result =  c.getResources().getString(R.string.error_conversations);  
            }
        } catch (JSONException e) {
            e.printStackTrace();
            result =  netError;  
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
                List<MessageConversation> mcl = MessageConversation.find(MessageConversation.class, "ID_API = "+mc.getIdApi());
                if (mcl.isEmpty()) {
                    mc.save();
                }
                c.setLastMessageId(mc.getIdApi());
                c.save();
            }

        }
    }

    public String getPeers() {
        String result = null;
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
                result = c.getResources().getString(R.string.error_peers);  
            }
        } catch (JSONException e) {
            e.printStackTrace();
            result = netError;  
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

    public String getNewMessagesConversation(Conversation c, MessageConversation last) {

        String result;
        String where;

        if (last!=null) {
            where = "CONVERSATION = "+c.getId()+" AND ID_API > "+last.getIdApi();
        } else {
            where = "CONVERSATION = "+c.getId()+" AND ID_API > 0";
        }

        List<MessageConversation> messagesDbList = Select
                .from(MessageConversation.class)
                .where(where)
                .list();


        JSONObject reader = readObjectFromServer(makeConversationsUrl(c, last, "next"));
        if (reader != null) {
            try {
                manageMessagesConversation(reader, messagesDbList, c);
                Boolean x = reader.getBoolean("more");
                while (x) {
                    MessageConversation middle = messagesDbList.get(messagesDbList.size()-1);
                    reader = readObjectFromServer(makeConversationsUrl(c, middle, "next"));
                    if (reader != null) {
                        x = reader.getBoolean("more");
                        manageMessagesConversation(reader, messagesDbList, c);
                    } else {
                        x = false;
                    }
                }
                return null;
            } catch (Exception e) {
                try {
                    result =  reader.getString("message");
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        result =   netError;  
        return result;
    }

    public String getOlderMessagesConversation(Conversation c, MessageConversation last) {

        String result;
        List<MessageConversation> messagesDbList = Select
                .from(MessageConversation.class)
                .where("CONVERSATION = "+c.getId()+" AND ID_API < "+last.getIdApi())
                .list();


        JSONObject reader = readObjectFromServer(makeConversationsUrl(c, last, "previous"));
        if (reader != null) {
            try {
                manageMessagesConversation(reader, messagesDbList, c);
                return null;
            } catch (Exception e) {
                try {
                    result =  reader.getString("message");
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        result =   netError;  
        return result;
    }

    public String getMessagesConversation(Conversation c) {

        String result;
        List<MessageConversation> messagesDbList = Select
                .from(MessageConversation.class)
                .where("CONVERSATION = "+c.getId())
                .list();


        JSONObject reader = readObjectFromServer(makeConversationsUrl(c, null, null));
        if (reader != null) {
            try {
                manageMessagesConversation(reader, messagesDbList, c);
                return null;
            } catch (Exception e) {
                try {
                    result =  reader.getString("message");
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        result =   netError;  
        return result;
    }

    private void manageMessagesConversation(JSONObject reader, List<MessageConversation> messagesDbList, Conversation c) throws JSONException {
        JSONArray messagesArray = reader.getJSONArray("messages");
        AccountUIB aUIB = new AccountUIB(this.c);
        User usr = aUIB.getUser();
        for (int x =0; x<messagesArray.length(); x++) {
            JSONObject messageJson = messagesArray.getJSONObject(x);

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

            if (!messagesDbList.contains(mc)) {
                mc.save();
                messagesDbList.add(mc);
            }

        }

    }

    public Map<String, Object> getSettings() {
        Map<String, Object> result = new HashMap<>();
        JSONObject reader = readObjectFromServer(SERVER_URL + "config/");
        if (reader != null) {
            try {
                result.put("message_max_length", reader.getInt("message_max_length"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
