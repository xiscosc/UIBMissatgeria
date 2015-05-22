package com.fsc.uibmissatgeria.api;

import android.content.Context;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.managers.FileManager;
import com.fsc.uibmissatgeria.models.Avatar;
import com.fsc.uibmissatgeria.models.Conversation;
import com.fsc.uibmissatgeria.models.FileMessage;
import com.fsc.uibmissatgeria.models.FileMessageConversation;
import com.fsc.uibmissatgeria.models.MessageConversation;
import com.fsc.uibmissatgeria.managers.ModelManager;
import com.fsc.uibmissatgeria.models.Subject;
import com.fsc.uibmissatgeria.models.SubjectGroup;
import com.fsc.uibmissatgeria.models.Message;
import com.fsc.uibmissatgeria.models.User;
import com.orm.query.Select;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
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
                        ModelManager.generateSGDefault(sbj);
                        subjectDbList.add(sbj);
                    } else {
                        int index = subjectDbList.indexOf(sbj);
                        sbj = subjectDbList.get(index);
                    }

                    for (int y = 0; y < groupJsonArray.length(); y++) {
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

        result = netError;
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
                    Message middle = messagesDbList.get(messagesDbList.size() - 1);
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
                    userJson.getInt("type"),
                    userJson.getInt("type") == Constants.TYPE_TEACHER
            );

            if (!usersDbList.contains(sender)) {
                sender.save();
                usersDbList.add(sender);
            } else {
                int index = usersDbList.indexOf(sender);
                sender = usersDbList.get(index);
            }

            if (!userJson.isNull("avatar")) manageAvatar(sender, userJson.getJSONObject("avatar"));

            Message msg = new Message(
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
                
                manageFiles(msg, messageJson.getJSONArray("media"));
            }
        }
        return result;
    }

    private void manageFiles(Message msg, JSONArray media) {
        try {
            for (int x=0; x<media.length(); x++) {
                JSONObject fileJson = media.getJSONObject(x);
                FileMessage fm = new FileMessage(fileJson.getLong("id"), fileJson.getString("mime"), msg);
                fm.save();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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


    private JSONArray readArrayFromServer(String url) {
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


    public int sendMessageToGroup(Subject s, SubjectGroup g, String body, List<FileMessage> files, User user) {
        int response = Constants.ALL_OK;
        String url;
        if (g.getIdApi() == Constants.DEFAULT_GROUP_ID) {
            url = "user/subjects/" + s.getIdApi() + "/messages/";
        } else {
            url = "user/subjects/" + s.getIdApi() + "/groups/"
                    + g.getIdApi() + "/messages/";
        }
        JSONObject messageJson = sendMessage(url, body);
        if (messageJson != null) {
            try {
                Message msg = new Message(
                        messageJson.getLong("id"),
                        messageJson.getString("body"),
                        user,
                        messageJson.getString("created_at"),
                        g
                );
                msg.save();
                if (!files.isEmpty()) {
                    for (FileMessage f : files) {
                        long idApi = sendFileToMessageGroup(f, msg);
                        if (idApi > 0) {
                            f.setIdApi(idApi);
                            f.setMessage(msg);
                            f.save();
                        } else {
                            FileManager.deleteFile(f.getLocalPath());
                            response = Constants.SOME_FILES_NOT_SEND;
                        }
                    }
                }
            }catch (JSONException e) {
                response = Constants.MESSSAGE_NOT_SEND;
                e.printStackTrace();
            }
        } else {
            response = Constants.MESSSAGE_NOT_SEND;
        }

        return response;
    }

    private long sendFileToMessageGroup(FileMessage f, Message msg) {
        SubjectGroup g = msg.getSubjectGroup();
        Subject s = g.getSubject();
        String url;

        if (g.getIdApi() == Constants.DEFAULT_GROUP_ID) {
            url = "user/subjects/" + s.getIdApi() + "/messages/";
        } else {
            url = "user/subjects/" + s.getIdApi() + "/groups/"
                    + g.getIdApi() + "/messages/";
        }
        url += msg.getIdApi()+"/media/";

        JSONObject response = uploadFile(f.getLocalPath(), "file", f.getMimeType(), url, "PATCH");
        if (response==null) return 0;
        try {
            return response.getLong("id");
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }


    public int sendMessageToConversation(Conversation c, String body, List<FileMessageConversation> files) {
        int response = Constants.ALL_OK;
        String url;
        url = "user/chats/" + c.getPeer().getIdApi() + "/messages/";
        JSONObject messageJson = sendMessage(url, body);
        if (messageJson != null) {
            try {
                MessageConversation mc = new MessageConversation(
                        messageJson.getLong("id"),
                        messageJson.getString("body"),
                        messageJson.getString("created_at"),
                        c,
                        false
                );
                mc.save();
                if (!files.isEmpty()) {
                    for (FileMessageConversation f : files) {
                        long idApi = sendFileToConversation(f, mc);
                        if (idApi > 0) {
                            f.setIdApi(idApi);
                            f.setMessage(mc);
                            f.save();
                        } else {
                            response = Constants.SOME_FILES_NOT_SEND;
                            FileManager.deleteFile(f.getLocalPath());
                        }
                    }
                }
            }catch (JSONException e) {
                response = Constants.MESSSAGE_NOT_SEND;
                e.printStackTrace();
            }
        } else {
            response = Constants.MESSSAGE_NOT_SEND;
        }

        return response;
    }


    private JSONObject sendMessage(String url, String body) {
        String result = "";
        HttpsURLConnection urlConnection;
        try {
            urlConnection = setUpConnection(SERVER_URL +url, "POST");
            urlConnection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write("body=" + body);
            writer.flush();
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                result += line;
            }
            writer.close();
            reader.close();
            return new JSONObject(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public long sendFileToConversation(FileMessageConversation f, MessageConversation mc) {
        String url = "user/chats/"+mc.getConversation().getPeer().getIdApi()+"/messages/"+mc.getIdApi()+"/media/";
        JSONObject response = uploadFile(f.getLocalPath(), "file", f.getMimeType(), url, "PATCH");
        if (response==null) return 0;
        try {
            return response.getLong("id");
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }

    }

    private HttpsURLConnection setUpConnection(String url, String method) {
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
        return urlConnection;
    }

    public Map<String, Object> doLogin(String user, String password) {

        Map<String, Object> result = new HashMap<>();
        try {
            String url = SERVER_URL + "login/?user=" + user + "&password=" + password;
            JSONObject reader = readObjectFromServer(url);
            this.token = reader.getString("token");
            JSONObject usr = reader.getJSONObject("user");
            User userObject = new User(
                    usr.getInt("id"),
                    usr.getString("first_name"),
                    usr.getString("last_name"),
                    usr.getString("user"),
                    usr.getInt("type")
            );
            result.put("user", userObject);
            if (!usr.isNull("avatar")) result.put("avatar", usr.getJSONObject("avatar"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getTokenRaw() {
        return this.token;
    }

        /*
            PARAMS PAGINATION
            message_id
            order (asc, desc)
            direction (next, previous)
     */

    private String makeMessagesUrl(Subject s, SubjectGroup g, Message m, String direction) {
        String result;
        if (g.getIdApi() == Constants.DEFAULT_GROUP_ID) {
            result = SERVER_URL + "user/subjects/" + s.getIdApi() + "/messages/";
        } else {
            result = SERVER_URL + "user/subjects/" + s.getIdApi() + "/groups/" + g.getIdApi() + "/messages/";
        }
        if (m != null && direction != null) {
            result += m.getIdApi() + "/";
            result += direction + "/";
        }
        return result;
    }

    private String makeConversationsUrl(Conversation c, MessageConversation m, String direction) {
        String result;
        result = SERVER_URL + "user/chats/" + c.getPeer().getIdApi() + "/messages/";
        if (m != null && direction != null) {
            result += m.getIdApi() + "/";
            result += direction + "/";
        }
        return result;
    }

    public String getConversations() {
        String result = null;
        List<Conversation> converDB = Conversation.listAll(Conversation.class);
        List<User> peers = User.listAll(User.class);
        JSONArray reader;

        try {
            reader = readArrayFromServer(SERVER_URL + "user/chats/");
            if (reader != null) {
                manageConversations(reader, converDB, peers);
            } else {
                result = c.getResources().getString(R.string.error_conversations);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            result = netError;
        }

        return result;
    }

    private void manageConversations(JSONArray reader, List<Conversation> converDB, List<User> peers) throws JSONException {
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

            if (peers.contains(peer)) {
                int index = peers.indexOf(peer);
                peer = peers.get(index);
                peer.setPeer(true);
                peer.save();
            } else {
                peer.save();
                peers.add(peer);
            }

            if (!userJson.isNull("avatar")) manageAvatar(peer, userJson.getJSONObject("avatar"));

            Conversation c = new Conversation(peer);

            if (!converDB.contains(c)) {
                c.setLastMessageId(Long.valueOf(-1));
                c.save();
                converDB.add(c);
            } else {
                int index = converDB.indexOf(c);
                c = converDB.get(index);
                if (c.getLastMessageId().equals(Long.valueOf(0))) {
                    c.setLastMessageId(Long.valueOf(-1));
                    c.save();
                }
            }

        }
    }


    private void manageAvatar(User user, JSONObject avatarData) {
        if (avatarData != null) {
            try {
                Avatar avatarRemote = new Avatar(avatarData.getLong("id"), user, avatarData.getString("mime"));
                Avatar avatarLocal = user.getAvatar();
                if (avatarLocal == null) {
                    boolean result = avatarRemote.downloadFromServer(c);
                    if (result) avatarRemote.save();
                } else if (!avatarLocal.equals(avatarRemote)){
                    boolean result = avatarRemote.downloadFromServer(c);
                    if (result) {
                        FileManager.deleteFile(avatarLocal.getLocalPath());
                        avatarLocal.delete();
                        avatarRemote.save();
                    }
                }
            }catch (JSONException e) {
                e.printStackTrace();
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
                if (reader != null) {
                    managePeers(reader, usersDB, peers);
                }
            }
            reader = readArrayFromServer(SERVER_URL + "user/teachers/");
            if (reader != null) {
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

    private void managePeers(JSONArray reader, List<User> usersDB, List<User> peers) throws JSONException {
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

            if (!userJson.isNull("avatar")) manageAvatar(peer, userJson.getJSONObject("avatar"));
        }

    }

    public String getNewMessagesConversation(Conversation c, MessageConversation last) {

        String result;
        String where;

        if (last != null) {
            where = "CONVERSATION = " + c.getId() + " AND ID_API > " + last.getIdApi();
        } else {
            where = "CONVERSATION = " + c.getId() + " AND ID_API > 0";
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
                    MessageConversation middle = messagesDbList.get(messagesDbList.size() - 1);
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

    public String getOlderMessagesConversation(Conversation c, MessageConversation last) {

        String result;
        List<MessageConversation> messagesDbList = Select
                .from(MessageConversation.class)
                .where("CONVERSATION = " + c.getId() + " AND ID_API < " + last.getIdApi())
                .list();


        JSONObject reader = readObjectFromServer(makeConversationsUrl(c, last, "previous"));
        if (reader != null) {
            try {
                manageMessagesConversation(reader, messagesDbList, c);
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

    public String getMessagesConversation(Conversation c) {

        String result;
        List<MessageConversation> messagesDbList = Select
                .from(MessageConversation.class)
                .where("CONVERSATION = " + c.getId())
                .list();


        JSONObject reader = readObjectFromServer(makeConversationsUrl(c, null, null));
        if (reader != null) {
            try {
                manageMessagesConversation(reader, messagesDbList, c);
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

    private void manageMessagesConversation(JSONObject reader, List<MessageConversation> messagesDbList, Conversation c) throws JSONException {
        JSONArray messagesArray = reader.getJSONArray("messages");
        AccountUIB aUIB = new AccountUIB(this.c);
        User usr = aUIB.getUser();
        for (int x = 0; x < messagesArray.length(); x++) {
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
                manageFilesConversation(mc, messageJson.getJSONArray("media"));
            }

        }

    }

    private void manageFilesConversation(MessageConversation msg, JSONArray media) {
        try {
            for (int x=0; x<media.length(); x++) {
                JSONObject fileJson = media.getJSONObject(x);
                FileMessageConversation fm = new FileMessageConversation(fileJson.getLong("id"), fileJson.getString("mime"), msg);
                fm.save();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> getSettings() {
        Map<String, Object> result = new HashMap<>();
        JSONObject reader = readObjectFromServer(SERVER_URL + "config/");
        if (reader != null) {
            try {
                result.put("message_max_length", reader.getInt("message_max_length"));
                result.put("max_file_size", reader.getInt("max_file_size"));
                result.put("max_message_files", reader.getInt("max_message_files"));

                JSONArray mimeJSON = reader.getJSONArray("allowed_mime_types");
                List<String> mime = new ArrayList<>();
                for (int x = 0; x < mimeJSON.length(); x++) {
                    mime.add(mimeJSON.getString(x));
                }
                result.put("allowed_mime_types", mime);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public Map<String, Object> getNotifications() {
        Map<String, Object> result = new HashMap<>();
        JSONObject reader = readObjectFromServer(SERVER_URL + "user/notifications/");
        if (reader != null) {
            try {
                JSONArray chats = reader.getJSONArray("chats");
                JSONArray subjects = reader.getJSONArray("subjects");
                JSONArray groups = reader.getJSONArray("groups");

                List<Conversation> resultConversations = manageConversationsNotifications(chats);
                List<SubjectGroup> resultSubjects = manageSubjectsNotifications(subjects);
                List<SubjectGroup> resultSubjectGroups = manageSubjectGroupsNotifications(groups);

                result.put(Constants.RESULT_CONVERSATIONS, resultConversations);
                result.put(Constants.RESULT_SUBJECTS, resultSubjects);
                result.put(Constants.RESULT_GROUPS, resultSubjectGroups);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private List<SubjectGroup> manageSubjectGroupsNotifications(JSONArray groups) throws JSONException {
        AccountUIB accountUIB = new AccountUIB(c);
        User user = accountUIB.getUser();
        Boolean onlyTeacher = accountUIB.OnlyTeacherNotifications();
        List<SubjectGroup> result = new ArrayList<>();
        for (int x = 0; x < groups.length(); x++) {
            JSONObject message = groups.getJSONObject(x);
            int groupId = message.getInt("group_id");
            List<SubjectGroup> gs = SubjectGroup.find(SubjectGroup.class, "ID_API = " + groupId);
            if (!gs.isEmpty() && gs.size() == 1) {
                SubjectGroup g = gs.get(0);
                Long last = message.getLong("id");
                if (!last.equals(g.getLastMessageId())) {
                    JSONObject sender = message.getJSONObject("sender");
                    Boolean itsMe = (user.getIdApi() == sender.getInt("id"));
                    if (!g.hasMessages()) {
                        g.setLastMessageId(last);
                        g.setRead(false);
                        g.save();
                    } else if (onlyTeacher) {
                        if (sender.getInt("type") == Constants.TYPE_TEACHER) {
                            g.setLastMessageId(last);
                            if (!itsMe){
                                g.setRead(false);
                                result.add(g);
                            } else {
                                g.setRead(true);
                            }
                            g.save();
                        }
                    } else {
                        g.setLastMessageId(last);
                        if (!itsMe){
                            g.setRead(false);
                            result.add(g);
                        } else {
                            g.setRead(true);
                        }
                        g.save();
                    }

                }
            }
        }
        return result;
    }

    private List<SubjectGroup> manageSubjectsNotifications(JSONArray subjects) throws JSONException {
        AccountUIB accountUIB = new AccountUIB(c);
        User user = accountUIB.getUser();
        Boolean onlyTeacher = accountUIB.OnlyTeacherNotifications();
        List<SubjectGroup> result = new ArrayList<>();
        for (int x = 0; x < subjects.length(); x++) {
            JSONObject message = subjects.getJSONObject(x);
            int subjectID = message.getInt("subject_id");
            List<Subject> subject = Subject.find(Subject.class, "ID_API = " + subjectID);
            if (!subject.isEmpty() && subject.size() == 1) {
                SubjectGroup g = subject.get(0).getDefaultGroup();
                if (g != null) {
                    Long last = message.getLong("id");
                    if (!last.equals(g.getLastMessageId())) {
                        JSONObject sender = message.getJSONObject("sender");
                        Boolean itsMe = (user.getIdApi() == sender.getInt("id"));
                        if (!g.hasMessages()) {
                            g.setLastMessageId(last);
                            g.setRead(false);
                            g.save();
                        } else if (onlyTeacher) {
                            if (sender.getInt("type") == Constants.TYPE_TEACHER) {
                                g.setLastMessageId(last);
                                if (!itsMe){
                                    g.setRead(false);
                                    result.add(g);
                                } else {
                                    g.setRead(true);
                                }
                                g.save();
                            }
                        } else {
                            g.setLastMessageId(last);
                            if (!itsMe){
                                g.setRead(false);
                                result.add(g);
                            } else {
                                g.setRead(true);
                            }
                            g.save();
                        }

                    }
                }
            }
        }
        return result;
    }

    private List<Conversation> manageConversationsNotifications(JSONArray chats) throws JSONException {
        List<Conversation> result = new ArrayList<>();
        List<User> users = User.listAll(User.class);
        List<Conversation> conversations = Conversation.listAll(Conversation.class);
        AccountUIB aUIB = new AccountUIB(this.c);
        User usr = aUIB.getUser();
        for (int x = 0; x < chats.length(); x++) {
            JSONObject messageJson = chats.getJSONObject(x);
            JSONObject userJson2 = messageJson.getJSONObject("sender");
            JSONObject recipientJson = messageJson.getJSONObject("recipient");

            User sender = new User(
                    userJson2.getInt("id"),
                    userJson2.getString("first_name"),
                    userJson2.getString("last_name"),
                    userJson2.getString("user"),
                    userJson2.getInt("type")
            );

            User recipient = new User(
                    recipientJson.getInt("id"),
                    recipientJson.getString("first_name"),
                    recipientJson.getString("last_name"),
                    recipientJson.getString("user"),
                    recipientJson.getInt("type")
            );

            if (!sender.equals(usr)) {
                Conversation conver;
                if (users.contains(sender)) {
                    int i = users.indexOf(sender);
                    User u = users.get(i);
                    if (u.isPeer()) {
                        sender = u;
                        conver = new Conversation(sender);
                        if (conversations.contains(conver)) {
                            int index = conversations.indexOf(conver);
                            conver = conversations.get(index);
                        } else {
                            conver.save();
                            conversations.add(conver);
                        }
                    } else {
                        u.setPeer(true);
                        u.save();
                        sender = u;
                        conver = new Conversation(sender);
                        conver.save();
                        conversations.add(conver);
                    }

                } else {
                    sender.setPeer(true);
                    sender.save();
                    users.add(sender);
                    conver = new Conversation(sender);
                    conver.save();
                    conversations.add(conver);
                }


                if (!conver.getLastMessageId().equals(messageJson.getLong("id"))) {
                    MessageConversation mc = new MessageConversation(
                            messageJson.getLong("id"),
                            messageJson.getString("body"),
                            messageJson.getString("created_at"),
                            conver,
                            true
                    );

                    mc.save();
                    conver.setLastMessageId(mc.getIdApi());
                    conver.save();
                    result.add(conver);
                }

            } else {
                Conversation conver;
                if (users.contains(recipient)) {
                    int i = users.indexOf(recipient);
                    User u = users.get(i);
                    if (u.isPeer()) {
                        recipient = u;
                        conver = new Conversation(recipient);
                        if (conversations.contains(conver)) {
                            int index = conversations.indexOf(conver);
                            conver = conversations.get(index);
                        } else {
                            conver.save();
                            conversations.add(conver);
                        }
                    } else {
                        u.setPeer(true);
                        u.save();
                        sender = u;
                        conver = new Conversation(sender);
                        conver.save();
                        conversations.add(conver);
                    }

                } else {
                    recipient.setPeer(true);
                    recipient.save();
                    users.add(recipient);
                    conver = new Conversation(recipient);
                    conver.save();
                    conversations.add(conver);
                }

                if (!conver.getLastMessageId().equals(messageJson.getLong("id"))) {
                    MessageConversation mc = new MessageConversation(
                            messageJson.getLong("id"),
                            messageJson.getString("body"),
                            messageJson.getString("created_at"),
                            conver,
                            false
                    );

                    mc.save();
                    conver.setLastMessageId(mc.getIdApi());
                    conver.save();
                }
            }


        }

        return result;
    }


    private JSONObject uploadFile(String route, String filefield, String mime, String url, String method) {
        try {
            String charset = "UTF-8";
            File uploadFile1 = new File(route);

            try {
                MultipartUtility multipart = new MultipartUtility(setUpConnection(SERVER_URL + url, method), charset);

                multipart.addFormField("mime", mime);
                multipart.addFilePart(filefield, uploadFile1);
                return new JSONObject(multipart.finish());

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }



    public boolean downloadFile(String url, String target) {
        try {
            HttpsURLConnection conn = setUpConnection(SERVER_URL + url, "GET");
            //conn.setDoOutput(true);
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode>300) return false;

            File f = new File(target);
            FileOutputStream fileOutput = new FileOutputStream(f);
            InputStream inputStream = conn.getInputStream();

            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
            }
            fileOutput.close();
            return true;
        } catch (Exception e) {
            File f = new File(target);
            if (f.exists()) f.delete();
            return false;
        }
    }

    public Avatar uploadAvatar(String route, User user) {
        JSONObject response = uploadFile(route, "avatar", "image/jpeg", "user/avatar/", "PUT");
        if (response==null) return null;
        try {
            return new Avatar(response.getLong("id"), user, response.getString("mime"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }




    private class MultipartUtility {
        private final String boundary;
        private static final String LINE_FEED = "\r\n";
        private HttpsURLConnection httpConn;
        private String charset;
        private OutputStream outputStream;
        private PrintWriter writer;


        public MultipartUtility(HttpsURLConnection Conn, String charset)
                throws IOException {
            this.charset = charset;

            // creates a unique boundary based on time stamp
            boundary = "===" + System.currentTimeMillis() + "===";

            httpConn = Conn;
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true); // indicates POST method
            httpConn.setDoInput(true);
            httpConn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);
            httpConn.setRequestProperty("User-Agent", "CodeJava Agent");

            outputStream = httpConn.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                    true);
        }

        /**
         * Adds a form field to the request
         *
         * @param name  field name
         * @param value field value
         */
        public void addFormField(String name, String value) {
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                    .append(LINE_FEED);
            writer.append("Content-Type: text/plain; charset=" + charset).append(
                    LINE_FEED);
            writer.append(LINE_FEED);
            writer.append(value).append(LINE_FEED);
            writer.flush();
        }

        /**
         * Adds a upload file section to the request
         *
         * @param fieldName  name attribute in <input type="file" name="..." />
         * @param uploadFile a File to be uploaded
         * @throws IOException
         */
        public void addFilePart(String fieldName, File uploadFile)
                throws IOException {
            String fileName = uploadFile.getName();
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append(
                    "Content-Disposition: form-data; name=\"" + fieldName
                            + "\"; filename=\"" + fileName + "\"")
                    .append(LINE_FEED);
            writer.append(
                    "Content-Type: "
                            + URLConnection.guessContentTypeFromName(fileName))
                    .append(LINE_FEED);
            writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();

            FileInputStream inputStream = new FileInputStream(uploadFile);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();

            writer.append(LINE_FEED);
            writer.flush();
        }

        /**
         * Adds a header field to the request.
         *
         * @param name  - name of the header field
         * @param value - value of the header field
         */
        public void addHeaderField(String name, String value) {
            writer.append(name + ": " + value).append(LINE_FEED);
            writer.flush();
        }

        /**
         * Completes the request and receives response from the server.
         *
         * @return a list of Strings as response in case the server returned
         * status OK, otherwise an exception is thrown.
         * @throws IOException
         */
        public String finish() throws IOException {
            String response = "";

            writer.append(LINE_FEED).flush();
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();

            // checks server's status code first
            int status = httpConn.getResponseCode();
            BufferedReader reader;
            if (200 <= status && status < 300) {
                reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            } else if (400 <= status && status < 500) {
                reader = new BufferedReader(new InputStreamReader(httpConn.getErrorStream()));
            } else {
                return null;
            }

            String line = null;
            while ((line = reader.readLine()) != null) {
                response += line;
            }
            reader.close();

            httpConn.disconnect();

            return response;
        }

    }
}
