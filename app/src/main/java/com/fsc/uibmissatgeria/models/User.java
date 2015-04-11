package com.fsc.uibmissatgeria.models;

import com.orm.SugarRecord;

/**
 * Created by xiscosastrecabot on 7/3/15.
 */
public class User extends SugarRecord<User>{
    private String name;
    private int idApi;

    public User(int id, String name) {
        this.idApi = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
