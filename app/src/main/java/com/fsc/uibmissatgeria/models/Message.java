package com.fsc.uibmissatgeria.models;

import com.fsc.uibmissatgeria.Constants;
import com.orm.SugarRecord;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by xiscosastrecabot on 7/3/15.
 */
public class Message extends SugarRecord<Message>{
    private String body;
    private User user;
    private Date date;
    private SubjectGroup subjectGroup;
    private Long idApi;


    public Message(Long id, String body, User user, Date date, SubjectGroup g) {
        this.body = body;
        this.user = user;
        this.date = date;
        this.subjectGroup = g;
        this.idApi = id;
    }

    public Message(Long id, String body, User user, String date, SubjectGroup g) {
        this.body = body;
        this.user = user;
        this.subjectGroup = g;
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT_SERVER);
        try {
            this.date = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.idApi=id;
    }

    public Message(){

    }


    public SubjectGroup getSubjectGroup() {
        return subjectGroup;
    }

    public String getBody() {
        return body;
    }

    public User getUser() {
        return user;
    }

    public Boolean isToday() {
        Date today = new Date();
        int result = today.compareTo(date);
        return result == 0;
    }

    public Long getIdApi() {
        return idApi;
    }


    public String getStringDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.GERMAN);
        return sdf.format(this.date);
    }

    public List<FileMessage> getFiles() {
        return FileMessage.find(FileMessage.class, "MESSAGE = "+getId());
    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Message)) return false;
        Message otherMyClass = (Message) other;
        return (idApi.equals(otherMyClass.idApi));
    }
}
