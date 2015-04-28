package com.fsc.uibmissatgeria.models;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by Xisco on 04/03/2015.
 */
public class Conversation extends SugarRecord<Conversation> {
    private User peer;
    private Long lastMessageId;

    @Ignore
    private MessageConversation lastMessage;


    public Conversation(User peer) {
        this.peer = peer;
        this.lastMessageId = Long.valueOf(0);
    }

    public Conversation() {
    }

    public User getPeer() {
        return peer;
    }

    public String getPeerName() {
        return peer.getName();
    }

    public MessageConversation getLastMessage() {
        if (lastMessage != null && lastMessage.getIdApi().equals(lastMessageId)) {
            return lastMessage;
        } else {
            if (!lastMessageId.equals(Long.valueOf(0))) {
                List<MessageConversation> messages = MessageConversation.find(MessageConversation.class, "ID_API = ?", Long.toString(lastMessageId));
                if (!messages.isEmpty()) {
                    lastMessage = messages.get(0);
                    return lastMessage;
                }
            }
        }
        return null;
    }

    public Long getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(Long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Conversation)) return false;
        Conversation otherMyClass = (Conversation) other;
        return (this.peer.getIdApi() == otherMyClass.peer.getIdApi());
    }
}

