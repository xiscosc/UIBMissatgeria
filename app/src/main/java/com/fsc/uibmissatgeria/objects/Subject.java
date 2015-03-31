package com.fsc.uibmissatgeria.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Xisco on 04/03/2015.
 */
public class Subject implements Parcelable {
    private String name;
    private ArrayList<Group> groups;
    private int code;
    private int id;


    public Subject(String name, ArrayList<Group> groups, int code, int id) {
        this.name = name;
        this.groups = groups;
        this.code = code;
        this.id = id;
    }

    public Subject(Parcel in) {
        this.name = in.readString();
        Object[] gr =  in.readArray(Group.class.getClassLoader());
        this.code = in.readInt();
        this.id= in.readInt();
        groups = new ArrayList<Group>();
        for(Object g : gr){
            Group ng = (Group) g;
            groups.add(ng);
        }

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

    public Group[] getArrayGroups() {
        return groups.toArray(new Group[groups.size()]);
    }

    public Boolean hasOnlyOneGroup() {
        return (groups.size()==1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeArray(groups.toArray());
        dest.writeInt(code);
        dest.writeInt(id);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Subject createFromParcel(Parcel in) {
            return new Subject(in);
        }

        public Subject[] newArray(int size) {
            return new Subject[size];
        }
    };
}