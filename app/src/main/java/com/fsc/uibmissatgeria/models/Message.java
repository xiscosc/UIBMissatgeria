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
    private SubjectGroup subjectGroup;
    private int idApi;


    public Message(int id, String body, User user, Date date, SubjectGroup g) {
        this.body = body;
        this.user = user;
        this.date = date;
        this.subjectGroup = g;
        this.idApi = id;
    }

    public Message(int id, String body, User user, String date, SubjectGroup g) {
        this.body = body;
        this.user = user;
        this.subjectGroup = g;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            this.date = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.idApi=id;
    }

    public Message(){

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

    public int getIdApi() {
        return idApi;
    }

    public String getStringDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.GERMAN);
        return sdf.format(this.date);
    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Message)) return false;
        Message otherMyClass = (Message) other;
        return (this.idApi == otherMyClass.idApi);
    }
}
