package com.fsc.uibmissatgeria.adapters;

/**
 * Created by Xisco on 04/03/2015.
 */
public class Conversation {
    private Message[] messages = null;
    private int user;


    public Conversation(Message[] messages, int user) {
        this.messages = messages;
        this.user = user;
    }

    public Conversation(int user) {
        this.user = user;
    }

    public Message[] getMessages() {
        return messages;
    }

    public void setMessages(Message[] messages) {
        this.messages = messages;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }
}

