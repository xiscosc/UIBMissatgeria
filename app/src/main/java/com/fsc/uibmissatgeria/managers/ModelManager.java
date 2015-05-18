package com.fsc.uibmissatgeria.managers;

import android.content.Context;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.api.AccountUIB;
import com.fsc.uibmissatgeria.api.Server;
import com.fsc.uibmissatgeria.models.Avatar;
import com.fsc.uibmissatgeria.models.Conversation;
import com.fsc.uibmissatgeria.models.Message;
import com.fsc.uibmissatgeria.models.MessageConversation;
import com.fsc.uibmissatgeria.models.Subject;
import com.fsc.uibmissatgeria.models.SubjectGroup;
import com.fsc.uibmissatgeria.models.User;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiscosastre on 11/04/15.
 */
public class ModelManager {

    private Context ctx;
    private Server server;
    private String error_message;

    public ModelManager(Context c) {
        ctx = c;

}

    public List<Message> getMessages(Subject s, SubjectGroup g) {
        Long lastID = g.getLastMessageId();
        List<Message> messages = Select.from(Message.class)
                .where("SUBJECT_GROUP = "+g.getId())
                .orderBy("ID_API DESC")
                .limit(Integer.toString(Constants.MAX_LIST_SIZE))
                .list();
        if (messages.isEmpty()) {
            messages = initMessages(s, g);
            if (!messages.isEmpty()) {
                g.setLastMessageId(messages.get(0).getIdApi());
                g.save();
            }
            return messages;
        } else {
            Message m = messages.get(0);
            if (!m.isToday() || !m.getIdApi().equals(lastID)) {
                List<Message> new_messages = getNewMessages(s, g, m);
                for(Message me : new_messages) {
                    messages.add(0, me);
                }
                if (messages.size()> Constants.MAX_LIST_SIZE) {
                    messages = messages.subList(0, Constants.MAX_LIST_SIZE-1);
                 }
            }
            return  messages;
        }
    }


    public List<Message> getOlderMessages(Subject s, SubjectGroup g, Message m) {
        List<Message> messages = Select.from(Message.class)
                .where("SUBJECT_GROUP = "+g.getId()+" AND ID_API < "+m.getIdApi())
                .orderBy("ID_API DESC")
                .limit(Integer.toString(Constants.MAX_LIST_OLDER_SIZE))
                .list();

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
        String errorMessage = server.getMessages(s, g);
        if (errorMessage != null)  this.error_message = errorMessage;
        return Select.from(Message.class)
                .where("SUBJECT_GROUP = "+g.getId())
                .orderBy("ID_API DESC").list();
    }

     /*
    Returns new messages ordered ASC for better insertion in top-RecyclerView
     */
    public List<Message> getNewMessages(Subject s, SubjectGroup g, Message m) {
        server = new Server(ctx);
        String errorMessage = server.getNewMessages(s, g, m);
        if (errorMessage != null)  this.error_message = errorMessage;
        List<Message> nMessages = Select.from(Message.class)
                .where("SUBJECT_GROUP = "+g.getId()+" AND ID_API > "+m.getIdApi())
                .orderBy("ID_API ASC")
                .list();

        if (!nMessages.isEmpty()) {
            g.setLastMessageId(nMessages.get(nMessages.size()-1).getIdApi());
            g.save();
        }
        return nMessages;
    }

    /*
    Returns older messages ordered DESC for better insertion in bottom-RecyclerView
    */
    private List<Message> retrieveOlderMessages(Subject s, SubjectGroup g, Message m) {
        server = new Server(ctx);
        String errorMessage = server.getOlderMessages(s, g, m);
        if (errorMessage != null)  this.error_message = errorMessage;
        return Select.from(Message.class)
                .where("SUBJECT_GROUP = "+g.getId()+" AND ID_API < "+m.getIdApi())
                .orderBy("ID_API DESC")
                .list();
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
        String errorMessage = server.getSubjects();
        if (errorMessage != null)  this.error_message = errorMessage;
        return Subject.listAll(Subject.class);
    }

    public static void resetDB() {
        MessageConversation.deleteAll(MessageConversation.class);
        Conversation.deleteAll(Conversation.class);
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

    public void reloadData(){
        AccountUIB a = new AccountUIB(ctx);
        User usr = a.getUser().cloneNoDb();
        resetDB();
        usr.save();
    }

    public Conversation getConversation(User peer) {
        List<Conversation> conversations = Conversation.find(Conversation.class, "PEER = "+peer.getId());
        if (conversations.isEmpty()) {
            Conversation c = new Conversation(peer);
            c.save();
            return c;
        } else {
            return conversations.get(0);
        }
    }

    public List<Conversation> getConversations() {
        return Select.from(Conversation.class).where("LAST_MESSAGE_ID != 0").orderBy("LAST_MESSAGE_ID DESC").list();
    }


    public List<Conversation> updateConversations() {
        server = new Server(ctx);
        String errorMessage = server.getConversations();
        if (errorMessage != null)  this.error_message = errorMessage;
        return Select.from(Conversation.class).where("LAST_MESSAGE_ID != 0").orderBy("LAST_MESSAGE_ID DESC").list();
    }


    public List<User> getPeers() {
        server = new Server(ctx);
        String errorMessage = server.getPeers();
        if (errorMessage != null)  this.error_message = errorMessage;
        return User.find(User.class, "PEER = 1");
    }

    public static boolean thereAreSubjects() {
        List<Subject> subjects = Subject.listAll(Subject.class);
        return !subjects.isEmpty();
    }

    public void showError()
    {
        if (error_message != null) Constants.showToast(ctx, error_message);
        error_message = null;
    }


    public List<MessageConversation> getMessagesConversation(Conversation  c) {
        server = new Server(ctx);
        String errorMessage = server.getMessagesConversation(c);
        if (errorMessage != null)  this.error_message = errorMessage;
        List<MessageConversation> msgdb = Select.from(MessageConversation.class)
                .where("CONVERSATION = "+c.getId())
                .orderBy("ID_API DESC")
                .limit(Integer.toString(Constants.MAX_LIST_SIZE_CONVERSATION))
                .list();

        List<MessageConversation> messages = new ArrayList<>();

        for (MessageConversation mc : msgdb) { //LIST INVERTER
            messages.add(0, mc);
        }

        if (!messages.isEmpty()) {
            MessageConversation ms = messages.get(messages.size() - 1);
            if (!ms.isRead()) {
                ms.setRead(true);
                ms.save();
            }
            c.setLastMessageId(ms.getIdApi());
            c.save();
        }
        return messages;


    }

    public List<MessageConversation> getNewMessagesConversation(Conversation  c, MessageConversation m) {
        server = new Server(ctx);
        String errorMessage = server.getNewMessagesConversation(c, m);
        if (errorMessage != null)  this.error_message = errorMessage;
        List<MessageConversation> messages =  Select.from(MessageConversation.class)
                .where("CONVERSATION = "+c.getId()+" AND ID_API > "+m.getIdApi())
                .orderBy("ID_API ASC")
                .list();
        if (!messages.isEmpty()) {
            MessageConversation ms = messages.get(messages.size() - 1);
            if (!ms.isRead()) {
                ms.setRead(true);
                ms.save();
            }
            c.setLastMessageId(ms.getIdApi());
            c.save();
        }
        return messages;

    }

    private List<MessageConversation> retrieveOlderMessagesConversation(Conversation  c, MessageConversation m) {
        server = new Server(ctx);
        String errorMessage = server.getOlderMessagesConversation(c, m);
        if (errorMessage != null)  this.error_message = errorMessage;
        return Select.from(MessageConversation.class)
                .where("CONVERSATION = "+c.getId()+" AND ID_API < "+m.getIdApi())
                .orderBy("ID_API DESC")
                .list();

    }

    public List<MessageConversation> getOlderMessagesConversation(Conversation c, MessageConversation m) {
        List<MessageConversation> messages = Select.from(MessageConversation.class)
                .where("CONVERSATION = " + c.getId() + " AND ID_API < " + m.getIdApi())
                .orderBy("ID_API DESC")
                .limit(Integer.toString(Constants.MAX_LIST_OLDER_SIZE_CONVERSATION))
                .list();

        if (messages.isEmpty()) {
            return retrieveOlderMessagesConversation(c, m);
        } else {
            if (messages.size()<Constants.MAX_LIST_OLDER_SIZE_CONVERSATION) {
                MessageConversation me = messages.get(messages.size()-1);
                List<MessageConversation> new_messages = retrieveOlderMessagesConversation(c, me);
                for(MessageConversation mes : new_messages) {
                    messages.add(mes);
                }
            }
            return  messages;
        }
    }

    public Avatar updateAvatar( User user, Avatar localAvatar) {
        server = new Server(ctx);
        Avatar avatar = server.uploadAvatar(localAvatar.getLocal_path(), user);
        if (avatar == null) return null;
        localAvatar.mergeFromServerAvatar(avatar);
        return localAvatar;
    }
}
