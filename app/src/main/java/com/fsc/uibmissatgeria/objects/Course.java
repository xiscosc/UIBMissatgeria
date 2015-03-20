package com.fsc.uibmissatgeria.objects;

import java.util.ArrayList;

/**
 * Created by Xisco on 04/03/2015.
 */
public class Course {
    private String name;
    private ArrayList<Group> groups;
    private int code;
    private int id;


    public Course(String name, ArrayList<Group> groups, int code, int id) {
        this.name = name;
        this.groups = groups;
        this.code = code;
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public int getCode() {
        return code;
    }

    public int getId() {
        return id;
    }

    public Group getFirstGroup() {
        return groups.get(0);
    }

    public String getGroupName() {
        int size = groups.size();
        String name;

        if (size>1) {
            name = "VARIOUS"; //TODO: TRANSLATE
        } else {
            name = getFirstGroup().getName();
        }
        return name;
    }

    public Boolean hasOnlyOneGroup() {
        return (groups.size()==1);
    }
}