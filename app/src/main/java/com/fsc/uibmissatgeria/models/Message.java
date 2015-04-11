package com.fsc.uibmissatgeria.models;

import com.orm.SugarRecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by xiscosastrecabot on 7/3/15.
 */
public class Message extends SugarRecord<Message>{
    private String body;
    private User user;
    private Date date;
    private int idApi;
    private Subject subject;
    private Group group;

    public Message(int id, String body, User user, Date date, Subject s, Group g) {
        this.body = body;
        this.user = user;
        this.date = date;
        this.idApi = id;
        this.subject = s;
        this.group = g;
    }

    public Message(int id, String body, User user, String date, Subject s, Group g) {
        this.body = body;
        this.user = user;
        this.idApi = id;
        this.subject = s;
        this.group = g;
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
