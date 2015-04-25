package com.fsc.uibmissatgeria.models;

import android.content.Context;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.api.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xiscosastre on 11/04/15.
 */
public class ModelsManager {

    private Context ctx;
    private Server server;

    public ModelsManager(Context c) {
        ctx = c;

}

    public List<Message> getMessages(Subject s, SubjectGroup g) {
        List<Message> messages = Message.find(Message.class,
                "SUBJECT_GROUP = ? ORDER BY ID_API DESC LIMIT ?",
                Long.toString(g.getId()),
                Integer.toString(Constants.MAX_LIST_SIZE));
        if (messages.isEmpty()) {
            return initMessages(s, g);
        } else {
            Message m = messages.get(0);
            if (!m.isToday()) {
                List<Message> new_messages = getNewMessages(s, g, m);
                for(Message me : new_messages) {
                    messages.add(0, me);
                }
                if (messages.size()> Constants.MAX_LIST_SIZE) {
                    return messages.subList(0, Constants.MAX_LIST_SIZE-1);
                 }
            }
            return  messages;
        }
    }

    public List<Message> getOlderMessages(Subject s, SubjectGroup g, Message m) {
        List<Message> messages = Message.find(Message.class,
                "SUBJECT_GROUP = ? AND ID_API < ? ORDER BY ID_API DESC LIMIT ?",
                Long.toString(g.getId()),
                Integer.toString(m.getIdApi()),
                Integer.toString(Constants.MAX_LIST_OLDER_SIZE));

        if (messages.isEmpty()) {
            return retrieveOlderMessages(s, g, m);
        } else {
            if (messages.size()<Constants.MAX_LIST_OLDER_SIZE) {
                Message me = messages.get(messages.size()-1);
                List<Message> new_messages = retrieveOlderMessages(s, g, me);
                for(Message mes : new_messages) {
                    messages.add(mes);
                }
            }
            return  messages;
        }
    }

    private List<Message> initMessages(Subject s, SubjectGroup g) {
        server = new Server(ctx);
        Map<String, Object> messagesData = server.getMessages(s, g);
        boolean existsError = messagesData.containsKey(Constants.RESULT_ERROR);
        if (existsError) {
            String error_message = (String) messagesData.get(Constants.RESULT_ERROR);
            Constants.showToast(ctx, error_message);
            return new ArrayList<>();
        }
        return (List<Message>) messagesData.get(Constants.RESULT_MESSAGES);
    }

    public List<Message> getNewMessages(Subject s, SubjectGroup g, Message m) {
        server = new Server(ctx);
        Map<String, Object> messagesData = server.getNewMessages(s, g, m);
        boolean existsError = messagesData.containsKey(Constants.RESULT_ERROR);
        if (existsError) {
            String error_message = (String) messagesData.get(Constants.RESULT_ERROR);
            Constants.showToast(ctx, error_message);
            return new ArrayList<>();
        }
        return (List<Message>) messagesData.get(Constants.RESULT_MESSAGES);
    }

    private List<Message> retrieveOlderMessages(Subject s, SubjectGroup g, Message m) {
        server = new Server(ctx);
        Map<String, Object> messagesData = server.getOlderMessages(s, g, m);
        boolean existsError = messagesData.containsKey(Constants.RESULT_ERROR);
        if (existsError) {
            String error_message = (String) messagesData.get(Constants.RESULT_ERROR);
            Constants.showToast(ctx, error_message);
            return new ArrayList<>();
        }
        return (List<Message>) messagesData.get(Constants.RESULT_MESSAGES);
    }

    public List<Subject> getSubjects() {
        List<Subject> subjects = Subject.listAll(Subject.class);
        if (subjects.isEmpty()) {
            return initSubjects();
        } else {
            return  subjects;
        }
    }

    private List<Subject> initSubjects() {
        server = new Server(ctx);
        Map<String, Object> subjectsData = server.getSubjects();
        boolean existsError = subjectsData.containsKey(Constants.RESULT_ERROR);
        if (existsError) {
            String error_message = (String) subjectsData.get(Constants.RESULT_ERROR);
            Constants.showToast(ctx, error_message);
            return new ArrayList<>();
        }
        return (List<Subject>) subjectsData.get(Constants.RESULT_SUBJECTS);
    }

    public static void resetDB() {
        User.deleteAll(User.class);
        Message.deleteAll(Message.class);
        Subject.deleteAll(Subject.class);
        SubjectGroup.deleteAll(SubjectGroup.class);
    }

    public static SubjectGroup generateSGDefault(Subject s) {
        SubjectGroup sg = new SubjectGroup(Constants.DEFAULT_GROUP_ID, "Subject Forum", s);
        sg.save();
        return sg;
    }



}