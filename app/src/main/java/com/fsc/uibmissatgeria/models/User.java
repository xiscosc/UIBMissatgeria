package com.fsc.uibmissatgeria.models;

import com.orm.SugarRecord;

/**
 * Created by xiscosastrecabot on 7/3/15.
 */
public class User extends SugarRecord<User> {
    private String firstName;
    private String lastName;
    private String uibDigitalUser;
    private String type;
    private int idApi;


    public User(int id, String firstName, String lastName, String uibDigitalUser, String type) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.uibDigitalUser = uibDigitalUser;
        this.type = type;
        this.idApi = id;
    }

    public User() {

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

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof User)) return false;
        User otherMyClass = (User) other;
        return (this.idApi == otherMyClass.idApi);
    }
}
