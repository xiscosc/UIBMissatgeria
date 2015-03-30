package com.fsc.uibmissatgeria.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xiscosastrecabot on 20/3/15.
 */
public class Group implements Parcelable {
    private int id;
    private String name;

    public Group(int id, String name) {

        this.id = id;
        this.name = name;
    }

    public Group(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
    }

    public int getId() {
        return id;
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
            dest.writeInt(id);
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
