package com.fsc.uibmissatgeria.models;

import android.content.Context;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.api.AccountUIB;
import com.orm.SugarRecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MessageConversation extends SugarRecord<MessageConversation>{
    private String body;
    private Date date;
    private Long idApi;
    private Conversation conversation;
    private Boolean fromOther;
    private Boolean read;


    public MessageConversation(Long id, String body, Date date, Conversation conversation, Boolean fromOther) {
        this.body = body;
        this.date = date;
        this.idApi = id;
        this.conversation = conversation;
        this.fromOther = fromOther;
        this.read = !fromOther;
    }

    public MessageConversation(Long id, String body, String date, Conversation conversation, Boolean fromOther) {
        this.body = body;
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT_SERVER);
        try {
            this.date = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.idApi=id;
        this.conversation = conversation;
        this.fromOther = fromOther;
        this.read = !fromOther;
    }

    public MessageConversation(){

    }

    public String getBody() {
        return body;
    }

    public User getUser(Context ctx) {
        if (fromOther) {
            return conversation.getPeer();
        } else {
            AccountUIB au = new AccountUIB(ctx);
            return  au.getUser();
        }
    }

    public Boolean isToday() {
        Date today = new Date();
        int result = today.compareTo(date);
        return result == 0;
    }

    public Boolean isRead() {
        return read;
    }

    public void setReaded(Boolean read) {
        this.read = read;
    }

    public Long getIdApi() {
        return idApi;
    }

    public Boolean isFromOther() {
        return fromOther;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public String getStringDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.GERMAN);
        return sdf.format(this.date);
    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof MessageConversation)) return false;
        MessageConversation otherMyClass = (MessageConversation) other;
        return (this.idApi.equals(otherMyClass.idApi));
    }
}
