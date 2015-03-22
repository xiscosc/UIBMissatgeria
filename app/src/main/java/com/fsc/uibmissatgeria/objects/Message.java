package com.fsc.uibmissatgeria.objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by xiscosastrecabot on 7/3/15.
 */
public class Message {
    private String body;
    private User user;
    private Date date;
    private int id;

    public Message(int id, String body, User user, Date date) {
        this.body = body;
        this.user = user;
        this.date = date;
        this.id = id;
    }

    public Message(int id, String body, User user, String date) {
        this.body = body;
        this.user = user;
        this.id = id;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            this.date = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

    public String getStringDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.GERMAN);
        return sdf.format(this.date);
    }
}
