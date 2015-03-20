package com.fsc.uibmissatgeria.objects;

/**
 * Created by xiscosastrecabot on 7/3/15.
 */
public class User {
    private String name;
    private int id;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
