package com.danielkobylski.android.marketmajster;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.MessagePattern;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
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

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.HttpHeaders;
import de.hdodenhof.circleimageview.CircleImageView;

public class AccountConfigActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PHOTO = 5;

    private User mUser;
    private static TextInputEditText mFirstName;
    private static TextInputEditText mLastName;
    private static TextInputEditText mEmail;
    private static TextInputEditText mAddress;
    private static TextInputEditText mCity;
    private static TextInputEditText mZipcode;
    private static CircleImageView mAvatarPreview;
    private static ButtonFlat mAvatarButton;
    private static ButtonRectangle mSubmitButton;
    private static String mImageType;
    private static String mImageString = "";
    private static byte[] mImageByteArr;
    private static ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_config);

        mUser = UserTransfer.mLoggedUser;
        mProgressBar = (ProgressBar) findViewById(R.id.loading);
        mAvatarPreview = (CircleImageView) findViewById(R.id.user_avatar_edit);
        byte[] avatarContent = mUser.getAvatarImage().getContent();
        Bitmap bmp = BitmapFactory.decodeByteArray(avatarContent, 0, avatarContent.length);
        mAvatarPreview.setImageBitmap(bmp);

        mAvatarButton  = (ButtonFlat) findViewById(R.id.avatar_choice_button);
        mAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , REQUEST_CODE_PHOTO);//one can be replaced with any action code
            }
        });

        mFirstName = (TextInputEditText) findViewById(R.id.name_edit_text);
        mLastName = (TextInputEditText) findViewById(R.id.lastname_edit_text);
        mEmail = (TextInputEditText) findViewById(R.id.email_edit_text);
        mAddress = (TextInputEditText) findViewById(R.id.address_edit_text);
        mCity = (TextInputEditText) findViewById(R.id.city_edit_text);
        mZipcode = (TextInputEditText) findViewById(R.id.zipcode_edit_text);

        mFirstName.setText(mUser.getName());
        mLastName.setText(mUser.getLastName());
        mEmail.setText(mUser.getEmail());
        mAddress.setText(mUser.getAddress());
        mCity.setText(mUser.getCity());
        mZipcode.setText(mUser.getZipcode());

        mSubmitButton = (ButtonRectangle) findViewById(R.id.save_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUserData();
                if (!mImageString.equals("")) {
                    changeUserAvatar();
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    //mAvatarPreview.setImageURI(selectedImage);
                }

                break;
            case REQUEST_CODE_PHOTO:
                if(resultCode == RESULT_OK){
                    ContentResolver cr = getContentResolver();
                    Uri selectedImage = data.getData();
                    //mImagePath = selectedImage.getPath();
                    mImageType = cr.getType(selectedImage);
                    //String imageExt = mImagePath.substring(mImagePath.lastIndexOf(".")+1);
                    try {
                        InputStream is = cr.openInputStream(selectedImage);
                        Bitmap bmp = BitmapFactory.decodeStream(is);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        mImageByteArr = baos.toByteArray();
                        mImageString = Base64.encodeToString(mImageByteArr, Base64.NO_WRAP);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    mAvatarPreview.setImageURI(selectedImage);
                }
                break;
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void changeUserData() {
        JSONObject userData = new JSONObject();
        try {
            userData.put("forename", mFirstName.getText().toString());
            userData.put("surname", mLastName.getText().toString());
            userData.put("email", mEmail.getText().toString());
            userData.put("address", mAddress.getText().toString());
            userData.put("zipCode", mZipcode.getText().toString());
        }
        catch(JSONException e) {}
        RequestQueue requestQueue = Volley.newRequestQueue(BarterApp.getAppContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT,API.CHANGE_SETTINGS,userData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            UserTransfer.mLoggedUser.setName(mFirstName.getText().toString());
                            UserTransfer.mLoggedUser.setLastName(mLastName.getText().toString());
                            UserTransfer.mLoggedUser.setEmail(mEmail.getText().toString());
                            UserTransfer.mLoggedUser.setAddress(mAddress.getText().toString());
                            UserTransfer.mLoggedUser.setZipcode(mZipcode.getText().toString());
                            if (mImageString.equals("")) {
                                mProgressBar.setVisibility(View.GONE);
                                Toast.makeText(AccountConfigActivity.this, "Dane zostały zmienione", Toast.LENGTH_LONG).show();
                                setResult(RESULT_OK);
                                finish();
                            }
                            else {changeUserAvatar();}
                        }
                        catch(Exception e) {
                            mProgressBar.setVisibility(View.VISIBLE);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        error.printStackTrace();
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

    private void changeUserAvatar() {
        JSONObject avatarData = new JSONObject();
        try {
            avatarData.put("file", mImageString);
            avatarData.put("id", mUser.getId());
            avatarData.put("type", mImageType);

        }
        catch(JSONException e) {}
        RequestQueue requestQueue = Volley.newRequestQueue(BarterApp.getAppContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,API.AVATAR,avatarData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            UserTransfer.mLoggedUser.setAvatarImage(new Image(mImageByteArr));
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(AccountConfigActivity.this, "Dane zostały zmienione", Toast.LENGTH_LONG).show();
                            setResult(RESULT_OK);
                            finish();
                        }
                        catch(Exception e) {
                            mProgressBar.setVisibility(View.VISIBLE);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        error.printStackTrace();
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
