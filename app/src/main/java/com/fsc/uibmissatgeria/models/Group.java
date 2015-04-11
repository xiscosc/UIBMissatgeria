package com.fsc.uibmissatgeria.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

/**
 * Created by xiscosastrecabot on 20/3/15.
 */
public class Group extends SugarRecord<Group> implements Parcelable {
    private int idApi;
    private String name;

    public Group(int id, String name) {

        this.idApi = id;
        this.name = name;
    }

    public Group(Parcel in) {
        this.idApi = in.readInt();
        this.name = in.readString();
    }

    public int getIdApi() {
        return idApi;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(idApi);
            dest.writeString(name);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        public Group[] newArray(int size) {
            return new Group[size];
        }
    };
}
