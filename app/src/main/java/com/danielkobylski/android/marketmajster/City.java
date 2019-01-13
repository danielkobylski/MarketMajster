package com.danielkobylski.android.marketmajster;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class City implements Parcelable {

    private Long mId;
    private Long mVoivoId;
    private String mName;

    public City(Long id, long voivoId, String name) {
        mId = id;
        mVoivoId = voivoId;
        mName = name;
    }

    public City(JSONObject city) {
        try {
            mId = city.getLong("id");
            mVoivoId = city.getLong("voivoId");
            mName = city.getString("name");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public Long getVoivoId() {
        return mVoivoId;
    }

    public void setVoivoId(Long voivoId) {
        mVoivoId = voivoId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    protected City(Parcel in) {
        mId = in.readByte() == 0x00 ? null : in.readLong();
        mVoivoId = in.readByte() == 0x00 ? null : in.readLong();
        mName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mId);
        }
        if (mVoivoId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mVoivoId);
        }
        dest.writeString(mName);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<City> CREATOR = new Parcelable.Creator<City>() {
        @Override
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };
}