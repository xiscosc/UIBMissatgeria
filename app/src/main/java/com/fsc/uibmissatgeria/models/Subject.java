package com.fsc.uibmissatgeria.models;

import com.fsc.uibmissatgeria.Constants;
import com.orm.SugarRecord;

import java.util.List;

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

    public List<SubjectGroup> getGroups() {
        return SubjectGroup.find(
                SubjectGroup.class,
                "SUBJECT = ? AND ID_API != ?",
                Long.toString(this.getId()),
                Integer.toString(Constants.DEFAULT_GROUP_ID)
        );
    }

    public SubjectGroup getDefaultGroup() {
        List<SubjectGroup> groups = SubjectGroup.find(
                SubjectGroup.class,
                "SUBJECT = ? AND ID_API = ?",
                Long.toString(this.getId()),
                Integer.toString(Constants.DEFAULT_GROUP_ID)
        );
        if (groups.isEmpty()) {
            return null;
        } else {
            return groups.get(0);
        }

    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Subject)) return false;
        Subject otherMyClass = (Subject) other;
        return (this.idApi == otherMyClass.idApi);
    }

    public Boolean hasUnreadGroups() {
        Boolean result = false;
        List<SubjectGroup> groups = SubjectGroup.find(
                SubjectGroup.class,
                "SUBJECT = ?",
                Long.toString(this.getId())
        );
        for (SubjectGroup g: groups) {
            result = result || !g.isRead();
        }
        return result;
    }
}