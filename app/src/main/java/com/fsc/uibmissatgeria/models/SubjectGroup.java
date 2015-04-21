package com.fsc.uibmissatgeria.models;
import com.orm.SugarRecord;


public class SubjectGroup extends SugarRecord<SubjectGroup>{
    private String name;
    private int idApi;
    private Subject subject;

    public SubjectGroup() {
    }

    public SubjectGroup(int id, String name, Subject s) {
        this.name = name;
        this.idApi = id;
        this.subject = s;
    }

    public String getName() {
        return name;
    }

    public int getIdApi() {
        return idApi;
    }

    public Subject getSubject() {
        return subject;
    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof SubjectGroup)) return false;
        SubjectGroup otherSG = (SubjectGroup) other;
        return (idApi == otherSG.idApi && subject.equals(otherSG.subject));
    }
}
