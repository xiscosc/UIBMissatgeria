package com.fsc.uibmissatgeria.objects;

import java.util.Date;

/**
 * Created by xiscosastrecabot on 7/3/15.
 */
public class Message {
    private String body;
    private User user;
    private Date date;

    public Message(String body, User user, Date date) {
        this.body = body;
        this.user = user;
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public User getUser() {
        return user;
    }

    public Date getDate() {
        return date;
    }
}
