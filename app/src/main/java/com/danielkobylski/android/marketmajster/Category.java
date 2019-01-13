package com.danielkobylski.android.marketmajster;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Category implements Parcelable {

    private Long mId;
    private String mName;

    public Category(Long id, String name) {
        mId = id;
        mName = name;
    }

    public Category(JSONObject category) {

        try {
            mId = Long.valueOf(category.getString("id"));
            mName = category.getString("name");
        } catch(JSONException e) {
            e.printStackTrace();
        }

    }

    protected Category(Parcel in) {
        mId = in.readLong();
        mName = in.readString();
    }


    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mName);
    }

}
