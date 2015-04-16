package com.fsc.uibmissatgeria.models;

import com.orm.SugarRecord;

import java.util.ArrayList;

/**
 * Created by Xisco on 04/03/2015.
 */
public class Subject extends SugarRecord<Subject> {
    private int idApi;
    private String name;
    private int code;

    public Subject(String name, int code, int id) {
        this.name = name;
        this.code = code;
        this.idApi = id;
    }

    public Subject() {

    }

    public int getIdApi() {
        return idApi;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public ArrayList<SubjectGroup> getGroups() {
        return (ArrayList<SubjectGroup>) SubjectGroup.find(
                SubjectGroup.class,
                "SUBJECT = ?",
                Long.toString(this.getId())
        );
    }
}