package com.fsc.uibmissatgeria.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.fsc.uibmissatgeria.R;

/**
 * Created by Xisco on 04/03/2015.
 */
public class Course {
    private String name;
    private String group;
    private int code;

    public Course(String name, String group, int code) {
        this.name = name;
        this.group = group;
        this.code = code;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
