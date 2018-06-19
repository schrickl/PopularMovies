package com.bill.android.myapplication.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable {

    private String mName;
    private String mKey;

    public Trailer (String name, String key) {
        mName = name;
        mKey = key;
    }

    protected Trailer(Parcel in) {
        mName = in.readString();
        mKey = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mKey);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String mKey) {
        this.mKey = mKey;
    }
}
