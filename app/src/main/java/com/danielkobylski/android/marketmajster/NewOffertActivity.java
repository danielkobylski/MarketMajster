package com.danielkobylski.android.marketmajster;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gc.materialdesign.views.Button;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewOffertActivity extends AppCompatActivity {

    private static ButtonFlat mPhotoChoiceButton;
    private static TextInputEditText mOffertName;
    private static TextInputEditText mCategory;
    private static AutoCompleteTextView mCity;
    private static TextInputEditText mDescription;
    private static List<Image> mImageList;
    private static ImageAdapter mImageAdapter;
    private static JSONArray mImageData;
    private static ViewPager mViewPager;
    private static ProgressBar mProgressBar;
    private static List<Image> mCachedImages;
    private static ButtonRectangle mSubmitButton;
    private Map<String, Long> mCategories;
    private Map<String, Long> mCities;
    private static ProgressBar mNewOffertLoading;
    private static TextView mErrMsg;
    private static Product mProductToEdit;
    public static final String EDIT_OFFERT = "com.danielkobylski.marketmajster.edit_offert";
    private static int mNewOrEdit;
    private static final int REQUEST_CODE_PHOTOS = 7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_offert);

        mImageData = new JSONArray();
        mImageList = new ArrayList<Image>();
        mCities = new HashMap<String, Long>();
        mCategories = new HashMap<String, Long>();
        mViewPager = findViewById(R.id.product_photo_view);
        mImageAdapter = new ImageAdapter(this, mImageList);
        mViewPager.setAdapter(mImageAdapter);

        mNewOffertLoading = (ProgressBar) findViewById(R.id.new_offert_loading);
        mProgressBar = (ProgressBar) findViewById(R.id.loading);
        mErrMsg = (TextView) findViewById(R.id.new_offert_err_msg);
        mOffertName = (TextInputEditText)findViewById(R.id.offert_name_edit_text);
        mCategory = (TextInputEditText)findViewById(R.id.category_edit_text);
        mCity = (AutoCompleteTextView)findViewById(R.id.city_edit_text);
        mDescription = (TextInputEditText)findViewById(R.id.description_edit_text);

        getCities();
        getCategories();
        mNewOrEdit = getIntent().getIntExtra(EDIT_OFFERT,0);
        if (mNewOrEdit == 1) {
            mProductToEdit = ProductTransfer.getProduct();
            mOffertName.setText( mProductToEdit.getName());
            mCategory.setText(mProductToEdit.getCategory());
            mCity.setText(mProductToEdit.getCity());
            mDescription.setText(mProductToEdit.getDescription());
        }

        mSubmitButton = (ButtonRectangle) findViewById(R.id.submit_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputLayout mCityBox = (TextInputLayout) findViewById(R.id.city_text_input);
                if (mCities.containsKey(mCity.getText().toString())) {
                    if (mNewOrEdit == 0) {
                        submitNewOffert(API.NEW_OFFERT);
                    }
                    else {
                        submitNewOffert(API.editProduct(mProductToEdit.getId()));
                    }
                    mErrMsg.setVisibility(View.GONE);
                    mNewOffertLoading.setVisibility(View.VISIBLE);
                    mCityBox.setError(null);
                }
                else {
                    mCityBox.setError("Wprowadzono złą miejscowość");
                }
            }
        });

        mCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCategories.size() != 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Wybierz kategorię");
                    final String[] categories = mCategories.keySet().toArray(new String[mCategories.keySet().size()]);
                    builder.setItems(categories, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mCategory.setText(categories[which]);
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        mPhotoChoiceButton = (ButtonFlat) findViewById(R.id.photo_choice_button);
        mPhotoChoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(pickPhoto , REQUEST_CODE_PHOTOS);//one can be replaced with any action code
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                }
                break;
            case REQUEST_CODE_PHOTOS:
                if(resultCode == RESULT_OK && data!=null){
                    mProgressBar.setVisibility(View.VISIBLE);
                    ClipData cD = data.getClipData();
                    ContentResolver cr = getContentResolver();
                    Uri selectedImage;
                    String imageType;
                    mImageList.clear();
                    if (cD != null) {
                        for (int i = 0;i < cD.getItemCount(); i++) {
                            selectedImage = cD.getItemAt(i).getUri();
                            imageType = cr.getType(selectedImage);
                            addImageToListAndJSON(selectedImage, imageType, cr);
                        }
                    } else {
                        selectedImage = data.getData();
                        imageType = cr.getType(selectedImage);
                        addImageToListAndJSON(selectedImage, imageType, cr);
                    }
                    mCachedImages = new ArrayList<>(mImageList);
                    mViewPager.setVisibility(View.GONE);
                    mViewPager.getAdapter().notifyDataSetChanged();
                    clearSessionImageData(data);
                }
                break;
        }
    }

    private void addImageToListAndJSON(Uri imageUri, String imageType, ContentResolver cr) {
        try {
            InputStream is = cr.openInputStream(imageUri);
            Bitmap bmp = BitmapFactory.decodeStream(is);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] bytes = baos.toByteArray();
            mImageList.add(new Image(bytes));

            JSONObject image = new JSONObject();
            try {
                image.put("image", Base64.encodeToString(bytes, Base64.NO_WRAP));
                image.put("type", imageType);
                //image.put(imageType, Base64.encodeToString(bytes, Base64.NO_WRAP));
                mImageData.put(image);
            }
            catch(JSONException e) {e.printStackTrace();}

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void clearSessionImageData(final Intent data) {
        RequestQueue requestQueue = Volley.newRequestQueue(NewOffertActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.SESSION_CLEAR, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                sendImagesToSession();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+UserTransfer.getToken());
                params.put("Cookie", UserTransfer.getJSessionID());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void sendImagesToSession() {
        RequestQueue requestQueue = Volley.newRequestQueue(BarterApp.getAppContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST,API.NEW_OFFERT_IMAGES,mImageData,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            mViewPager.getAdapter().notifyDataSetChanged();
                            mProgressBar.setVisibility(View.GONE);
                            mViewPager.setVisibility(View.VISIBLE);
                        }
                        catch(Exception e) {
                            e.printStackTrace();
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
        requestQueue.add(jsonArrayRequest);
    }

    private void submitNewOffert(String url) {
        JSONObject offertData = new JSONObject();
        try {
            offertData.put("name", mOffertName.getText().toString());
            offertData.put("description", mDescription.getText().toString());
            offertData.put("cityId", mCities.get(mCity.getText().toString()));
            offertData.put("categoryId", mCategories.get(mCategory.getText().toString()));
            if (mNewOrEdit == 0) {
                offertData.put("creationDate",getCurrentDate());
                offertData.put("ownerId", UserTransfer.mLoggedUser.getId());
                offertData.put("active", "true");
            }
        }

        catch(JSONException e) {}

        RequestQueue requestQueue = Volley.newRequestQueue(BarterApp.getAppContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(mNewOrEdit+1,url,offertData, //1 is POST, 2 is PUT => mNewOrEdit is 0 if new offert and 1 otherwise, therefore mNewOrEdit + 1
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            setResult(RESULT_OK);
                            finish();
                        }
                        catch(Exception e) {
                            mNewOffertLoading.setVisibility(View.GONE);
                            mErrMsg.setVisibility(View.VISIBLE);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mNewOffertLoading.setVisibility(View.GONE);
                        mErrMsg.setVisibility(View.VISIBLE);
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

    private String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        String day = "0"+String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String month =  "0"+String.valueOf(cal.get(Calendar.MONTH)+1);
        String year =  String.valueOf(cal.get(Calendar.YEAR));
        return year + "-" + month.substring(month.length()-2) + "-" + day.substring(day.length()-2);
    }

    private void getCities() {
        RequestQueue requestQueue = Volley.newRequestQueue(NewOffertActivity.this);
        //mVoivodeshipList.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                API.CITIES,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            for(int i=0;i<response.length();i++){
                                JSONObject city = response.getJSONObject(i);
                                mCities.put(city.getString("name"),
                                            city.getLong("id"));
                            }
                            mCity.setAdapter(new ArrayAdapter<>(NewOffertActivity.this, android.R.layout.simple_list_item_1, mCities.keySet().toArray()));

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        //Log.d("Response error",error.getMessage());
                        Toast.makeText(
                                NewOffertActivity.this,
                                "Error while getting cities data",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    private void getCategories() {

        RequestQueue requestQueue = Volley.newRequestQueue(NewOffertActivity.this);
        //mCatList.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "http://192.168.0.17:8080/categories/all",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            for(int i=0;i<response.length();i++){
                                JSONObject category = response.getJSONObject(i);
                                mCategories.put(category.getString("name"),
                                        category.getLong("id"));
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        //Log.d("Response error",error.getMessage());
                        Toast.makeText(
                                NewOffertActivity.this,
                                "Error while getting categories data",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);

    }

}
