package com.danielkobylski.android.marketmajster;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gc.materialdesign.views.Button;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;

import org.json.JSONObject;
import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    private ButtonFlat mRegisterButton;
    private ButtonRectangle mLoginButton;
    private TextInputEditText mLogin;
    private TextInputEditText mPassword;
    private CheckBox mRememberMe;

    public static final String PREFS_NAME = "barter_login";
    public static final String PREFS_ID = "16";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLogin = (TextInputEditText) findViewById(R.id.input_login_edit_text);
        mPassword = (TextInputEditText) findViewById(R.id.input_password_edit_text);
        mRememberMe = (CheckBox) findViewById(R.id.remember_me_check_box);

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
                getUserData();
            }
        });


    }

    public void getUserData() {
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                "http://192.168.0.17:8080/users/find/"+mLogin.getText(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            UserTransfer.mLoggedUser = new User(response);
                            if(mRememberMe.isChecked()==true) {
                                getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
                                        .edit()
                                        .putString(PREFS_ID, mLogin.getText().toString())
                                        .commit();
                            }
                            setResult(RESULT_OK);
                            finish();
                        } catch(Exception e) {

                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
}
