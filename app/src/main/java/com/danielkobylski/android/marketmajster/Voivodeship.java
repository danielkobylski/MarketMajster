package com.danielkobylski.android.marketmajster;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Voivodeship implements Parcelable {

    private long mId;
    private String mName;
    private ArrayList<City> mCities;

    public Voivodeship(long id, String name) {
        mId = id;
        mName = name;
        mCities = new ArrayList<>();
    }

    public Voivodeship(JSONObject voivodeship) {

        try {
            mId = Long.valueOf(voivodeship.getString("id"));
            mName = voivodeship.getString("name");
            JSONArray cities = voivodeship.getJSONArray("items");
            mCities = new ArrayList<>();
            for (int i = 0; i<cities.length();i++) {
                mCities.add(new City(cities.getJSONObject(i)));
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }

    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public ArrayList<City> getCities() {
        return mCities;
    }

    public void setCities(ArrayList<City> cities) {
        mCities = cities;
    }

    public Voivodeship addCity(City city) {
        mCities.add(city);
        return this;
    }

    protected Voivodeship(Parcel in) {
        mId = in.readLong();
        mName = in.readString();
        if (in.readByte() == 0x01) {
            mCities = new ArrayList<City>();
            in.readList(mCities, City.class.getClassLoader());
        } else {
            mCities = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mName);
        if (mCities == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mCities);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Voivodeship> CREATOR = new Parcelable.Creator<Voivodeship>() {
        @Override
        public Voivodeship createFromParcel(Parcel in) {
            return new Voivodeship(in);
        }

        @Override
        public Voivodeship[] newArray(int size) {
            return new Voivodeship[size];
        }
    };
}