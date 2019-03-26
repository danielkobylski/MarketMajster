package com.danielkobylski.android.marketmajster;

import android.util.Base64;

import com.danielkobylski.android.marketmajster.Image;
import com.danielkobylski.android.marketmajster.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class User {
    private Long mId;
    private String mName;
    private String mLastName;
    private String mLogin;
    private String mPassword;
    private String mEmail;
    private String mPhone;
    private String mAddress;
    private String mCity;
    private String mZipcode;
    private String mBirthDate;
    private Image mAvatarImage;
    private List<Product> mFavourites;


    public User(Long id, String name, String lastName, String login, String password, String email, String phone, String address, String city, String zipcode, String birthDate, Image avatarImage, List<Product> favourites) {
        mId = id;
        mName = name;
        mLastName = lastName;
        mLogin = login;
        mPassword = password;
        mEmail = email;
        mPhone = phone;
        mAddress = address;
        mCity = city;
        mZipcode = zipcode;
        mBirthDate = birthDate;
        mAvatarImage = avatarImage;
        mFavourites = favourites;
    }

    User(JSONObject user) {
        try {
            mId = Long.valueOf(user.getString("id"));
            mName = user.getString("forename");
            mLastName = user.getString("surname");
            mLogin = user.getString("login");
            mPassword = ""; //user.getString("password");
            mEmail = user.getString("email");
            mPhone = user.getString("phoneNumber");
            mAddress = user.getString("address");
            mCity = user.getString("city");
            mZipcode = user.getString("zipCode");
            mBirthDate = user.getString("birthDate");
            mAvatarImage = new Image((long)-1,(long)-1,Base64.decode(user.getString("img"), Base64.DEFAULT));
            mFavourites = new ArrayList<>();
            JSONArray favs = user.getJSONArray("fav");
            if (favs.length() != 0) {
                for(int i = 0; i < favs.length(); i++) {
                    mFavourites.add(new Product(favs.getJSONObject(i)));
                }
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }

    }

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

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getLogin() {
        return mLogin;
    }

    public void setLogin(String login) {
        mLogin = login;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getZipcode() {
        return mZipcode;
    }

    public void setZipcode(String zipcode) {
        mZipcode = zipcode;
    }

    public String getBirthDate() {
        return mBirthDate;
    }

    public void setBirthDate(String birthDate) {
        mBirthDate = birthDate;
    }

    public Image getAvatarImage() {
        return mAvatarImage;
    }

    public void setAvatarImage(Image avatarImage) {
        mAvatarImage = avatarImage;
    }

    public List<Product> getFavourites() {
        return mFavourites;
    }

    public void setFavourites(List<Product> favourites) {
        mFavourites = favourites;
    }
}

