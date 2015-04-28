package com.fsc.uibmissatgeria.models;

import com.orm.SugarRecord;

/**
 * Created by xiscosastrecabot on 7/3/15.
 */
public class User extends SugarRecord<User> {
    private String firstName;
    private String lastName;
    private String uibDigitalUser;
    private int type;
    private int idApi;
    private boolean peer;


    public User(int id, String firstName, String lastName, String uibDigitalUser, int type) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.uibDigitalUser = uibDigitalUser;
        this.type = type;
        this.idApi = id;
        this.peer = false;
    }

    public User(int id, String firstName, String lastName, String uibDigitalUser, int type, Boolean peer) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.uibDigitalUser = uibDigitalUser;
        this.type = type;
        this.idApi = id;
        this.peer = peer;
    }

    public User() {

    }

    public boolean isPeer() {
        return peer;
    }

    public void setPeer(boolean peer) {
        this.peer = peer;
    }

    public String getName() {
        return firstName+" "+lastName;
    }

    public int getIdApi() {
        return idApi;
    }

    public String getUibDigitalUser() {
        return uibDigitalUser;
    }

    public User cloneNoDb() {
        return new User(
                idApi, firstName, lastName, uibDigitalUser, type
        );
    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof User)) return false;
        User otherMyClass = (User) other;
        return (this.idApi == otherMyClass.idApi);
    }

    public int getType() {
        return type;
    }
}
