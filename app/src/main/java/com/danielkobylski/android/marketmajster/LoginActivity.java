package com.danielkobylski.android.marketmajster;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gc.materialdesign.views.Button;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private ButtonFlat mRegisterButton;
    private ButtonRectangle mLoginButton;
    private static ProgressBar mProgressBar;
    private static TextView mWrongInput;
    private static TextInputEditText mLogin;
    private static TextInputEditText mPassword;
    private static CheckBox mRememberMe;
    private static JSONObject mLoginData;
    public static final String PREFS_NAME = "barter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLogin = (TextInputEditText) findViewById(R.id.input_login_edit_text);
        mPassword = (TextInputEditText) findViewById(R.id.input_password_edit_text);
        mLogin.setText("daniel");
        mPassword.setText("123");
        mRememberMe = (CheckBox) findViewById(R.id.remember_me_check_box);

        mWrongInput = (TextView) findViewById(R.id.wrong_input);
        mProgressBar = (ProgressBar)findViewById(R.id.loading);

        mRegisterButton = (ButtonFlat)findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        mLoginButton = (ButtonRectangle) findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginData = new JSONObject();
                try {
                    mLoginData.put("username",mLogin.getText());
                    mLoginData.put("password", mPassword.getText());
                    mWrongInput.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    getAccessToken(mLoginData, mRememberMe.isChecked(), LoginActivity.this);
                }
                catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }
//"http://192.168.0.17:8080/users/find/"+mLogin.getText()


    public static void logRememberedUser(Activity requestingActivity) {

        SharedPreferences pref = BarterApp.getSharedPrefs();
        String login = pref.getString("login",null);
        String password = pref.getString("password", null);

        if (login != null && password != null) {
            mLoginData = new JSONObject();
            try {
                mLoginData.put("username",login);
                mLoginData.put("password", password);
                getAccessToken(mLoginData, false, requestingActivity);
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void getAccessToken(final JSONObject loginData, final boolean rememberMe, final Activity requestingActivity) {

                            RequestQueue requestQueue = Volley.newRequestQueue(BarterApp.getAppContext());
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,"http://192.168.0.17:8080/api/auth/signin",//API.TOKEN,
                                    loginData,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                            Log.d("Response",response.getString("accessToken"));
                            UserTransfer.setToken(response.getString("accessToken"));
                            if(rememberMe == true) {
                                BarterApp.getSharedPrefs()
                                        .edit()
                                        .putString("login", loginData.getString("username"))
                                        .putString("password", loginData.getString("password"))
                                        .commit();
                            }
                            getCurrentUser(requestingActivity);
                        }
                        catch(JSONException e) {
                           int i = 2;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        String activityName = requestingActivity.getLocalClassName();
                        if (activityName.equals("LoginActivity")) {
                            mProgressBar.setVisibility(View.GONE);
                            mWrongInput.setVisibility(View.VISIBLE);
                        }
                        else {
                            Toast.makeText(requestingActivity, "Could not login with current credintials", Toast.LENGTH_LONG);
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+UserTransfer.getToken());
                return params;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                // since we don't know which of the two underlying network vehicles
                // will Volley use, we have to handle and store session cookies manually
                Log.i("response",response.headers.toString());
                Map<String, String> responseHeaders = response.headers;
                String rawCookies = response.headers.get("Set-Cookie");
                String JSessionID = rawCookies.substring(0,rawCookies.indexOf(";")); //rawCookies.indexOf("=")+1
                UserTransfer.setJSessionID(rawCookies.substring(0,rawCookies.indexOf(";")));
                return super.parseNetworkResponse(response);
            }

        };
        requestQueue.add(jsonObjectRequest);
    }

    public static void getCurrentUser(final Activity requestingActivity) {
        final String activityName = requestingActivity.getLocalClassName();
        RequestQueue requestQueue = Volley.newRequestQueue(BarterApp.getAppContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,"http://192.168.0.17:8080/users/current",//API.CURRENT_USER,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            UserTransfer.mLoggedUser = new User(response);
                            if(activityName.equals("LoginActivity")) {
                                requestingActivity.setResult(RESULT_OK);
                                requestingActivity.finish();
                            }
                            else {
                                MainActivity.changeDrawerData(UserTransfer.mLoggedUser);
                                Toast.makeText(requestingActivity, "Zalogowano jako: " + UserTransfer.mLoggedUser.getName(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch(Exception e) {
                            e.printStackTrace();
                            Log.d("User Data Error",e.getMessage());
                            Toast.makeText(BarterApp.getAppContext(),"Error getting user data",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+UserTransfer.getToken());
                params.put("Cookie", UserTransfer.getJSessionID());
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
    }
}
