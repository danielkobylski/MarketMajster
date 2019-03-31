package com.danielkobylski.android.marketmajster;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class PasswordChangeActivity extends AppCompatActivity {

    private static ButtonRectangle mSubmitButton;
    private static TextInputEditText mOldPassword;
    private static TextInputEditText mNewPassowrd;
    private static TextInputEditText mConfirmPassword;
    private static ProgressBar mProgressBar;
    private static TextView mErrorTextView;
    private static String mErrMsg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        mErrorTextView = (TextView) findViewById(R.id.wrong_input) ;
        mOldPassword = (TextInputEditText) findViewById(R.id.input_current_password_edit_text);
        mNewPassowrd = (TextInputEditText) findViewById(R.id.input_new_password_edit_text);
        mConfirmPassword  = (TextInputEditText) findViewById(R.id.input_confirm_password_edit_text);
        mProgressBar = (ProgressBar) findViewById(R.id.loading);
        mSubmitButton = (ButtonRectangle) findViewById(R.id.submit_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = mOldPassword.getText().toString();
                String newPassword = mNewPassowrd.getText().toString();
                String confirmPassword = mConfirmPassword.getText().toString();
                if (!newPassword.equals(confirmPassword)) {
                    mErrMsg = "Podane hasła się różnią";
                }
                else if (newPassword.equals(oldPassword)) {
                    mErrMsg = "Nowe hasło nie może być takie same jak stare";
                }
                else if (newPassword.equals("")) {
                    mErrMsg = "Podaj nowe hasło";
                }
                else {mErrMsg = "";}
                if (!mErrMsg.equals("")) {
                    mErrorTextView.setText(mErrMsg);
                    mErrorTextView.setVisibility(View.VISIBLE);
                }
                else {
                    mErrorTextView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    changeUserPassword(oldPassword, newPassword);
                }
            }
        });
    }

    private void changeUserPassword(String oldPassword, String newPassword) {

        JSONObject passwordData = new JSONObject();
        try {
            passwordData.put("oldPassword", oldPassword);
            passwordData.put("newPassword", newPassword);
        }
        catch(JSONException e) {}
        RequestQueue requestQueue = Volley.newRequestQueue(BarterApp.getAppContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT,API.CHANGE_PASSWORD,passwordData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("errorMsg")) {
                                mErrorTextView.setText("Stare hasło jest niepoprawne");
                                mErrorTextView.setVisibility(View.VISIBLE);
                                mProgressBar.setVisibility(View.GONE);
                            }
                            else {
                                BarterApp.getSharedPrefs()
                                        .edit()
                                        .putString("password", mNewPassowrd.getText().toString())
                                        .commit();
                                Toast.makeText(PasswordChangeActivity.this, "Hasło zostało zmienione", Toast.LENGTH_LONG).show();
                                setResult(RESULT_OK);
                                finish();
                            }
                        }
                        catch(Exception e) {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mErrorTextView.setText("Wystąpił błąd");
                        mErrorTextView.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                        Log.d("asd","asd");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+UserTransfer.getToken());
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}
