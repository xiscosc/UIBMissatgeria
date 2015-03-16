package com.fsc.uibmissatgeria.adapters;

/**
 * Created by xiscosastrecabot on 7/3/15.
 */
public class Message {
    private String body;
    private int idUser;

    public Message(String body, int idUser) {
        this.body = body;
        this.idUser = idUser;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
}
