package com.danielkobylski.android.marketmajster;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserTransfer {

    public static User mLoggedUser;
    public static User mUser;
    public static User mOffertOwner;
    public static String mToken;

    public static void setUser(User user) {mUser = user;}
    public static User getUser() {return mUser;}

    public static void setOffertOwner(User user) {mOffertOwner = user;}
    public static User getOffertOwner() {return mOffertOwner;}

    public static void setToken(String token){mToken = token;}
    public static String getToken(){return mToken;}
}
