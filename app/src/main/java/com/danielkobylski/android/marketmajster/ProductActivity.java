package com.danielkobylski.android.marketmajster;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.Card;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductActivity extends AppCompatActivity {

    private Product mProduct;
    private TextView mCreationDate;
    private TextView mName;
    private TextView mDescription;
    private TextView mCategory;
    private TextView mCity;
    private Button mAddToFavourites;
    private Button mStartTransaction;
    private CircleImageView mUserAvatar;
    private TextView mOwnerName;
    private TextView mOwnerPhone;
    private TextView mOwnerEmail;
    private User mOwner;
    private ButtonFlat mMoreOffers;
    private boolean mAlreadyLiked;
    private static final String URL = "http://192.168.0.17:8080/users/find/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
       // mProduct = getIntent().getParcelableExtra("asd");
        mProduct = ProductTransfer.getProduct();
        ViewPager viewPager = findViewById(R.id.product_photo_view);
        ImageAdapter adapter = new ImageAdapter(this, mProduct.getImageList());
        viewPager.setAdapter(adapter);

        if (getIntent().getIntExtra("HideButtons", -1) == 1 ) {
            CardView cv = (CardView)findViewById(R.id.card_view2);
            cv.setVisibility(View.GONE);
        }

        getUser();

        if (UserTransfer.mLoggedUser != null) {
            for(Product p: UserTransfer.mLoggedUser.getFavourites()) {
                if (p.getId() == mProduct.getId()) {
                    mAlreadyLiked = true;
                    break;
                }
            }
        }

        mCreationDate = (TextView)findViewById(R.id.product_creation_date_text_view);
        mCreationDate.setText(mCreationDate.getText() + mProduct.getCreationDate());

        mName = (TextView)findViewById(R.id.product_view_title);
        mName.setText(mProduct.getName());

        mDescription = (TextView)findViewById(R.id.product_view_description);
        mDescription.setText(mProduct.getDescription());

        mCategory = (TextView)findViewById(R.id.category_text_view);
        mCategory.setText(mProduct.getCategory());

        mCity = (TextView)findViewById(R.id.loc_text_view);
        mCity.setText(mProduct.getCity());

        mMoreOffers = (ButtonFlat)findViewById(R.id.user_more_offers_button);
        mMoreOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserTransfer.setOffertOwner(mOwner);
                Intent intent = new Intent(ProductActivity.this, UserProductsActivity.class);
                startActivity(intent);
            }
        });

        mAddToFavourites = (Button) findViewById(R.id.favs_button);
        mAddToFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserTransfer.mLoggedUser == null) {
                    Intent intent = new Intent(ProductActivity.this, LoginActivity.class);
                    startActivityForResult(intent, MainActivity.REQUEST_CODE_LOGIN);
                }
                else {
                    if (mAlreadyLiked == true) {
                        Toast.makeText(ProductActivity.this, "Już śledzisz ten produkt!", Toast.LENGTH_LONG).show();
                    }
                    else if(UserTransfer.mLoggedUser.getId() == mOwner.getId()) {
                        Toast.makeText(ProductActivity.this, "To jest Twój produkt!", Toast.LENGTH_LONG).show();
                    }
                    else {
                        addToFavourites(mOwner, mProduct);
                        UserTransfer.mLoggedUser.addFavourite(mProduct);
                        mAlreadyLiked = true;
                    }
                }

            }
        });

        mStartTransaction = (Button) findViewById(R.id.start_transaction_button);
        mStartTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserTransfer.mLoggedUser == null) {
                    Intent intent = new Intent(ProductActivity.this, LoginActivity.class);
                    startActivityForResult(intent, MainActivity.REQUEST_CODE_LOGIN);
                }
                else if(UserTransfer.mLoggedUser.getId() == mOwner.getId()) {
                    Toast.makeText(ProductActivity.this, "To jest Twój produkt!", Toast.LENGTH_LONG).show();
                }
                else {
                    checkIfTransactionExists();
                }
            }
        });
    }

    private void checkIfTransactionExists() {
        RequestQueue requestQueue = Volley.newRequestQueue(BarterApp.getAppContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,API.transactionExists(UserTransfer.mLoggedUser.getId(), mProduct.getId()),null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("errorMsg")) {
                                Toast.makeText(ProductActivity.this, "Już wysłałeś zapytanie o ten produkt.", Toast.LENGTH_LONG).show();
                            }
                            else {
                                ProductTransfer.setOffert(mProduct);
                                UserTransfer.setOffertOwner(mOwner);
                                Intent intent = new Intent(ProductActivity.this, TransactionProposalActivity.class);
                                startActivity(intent);
                            }
                        }
                        catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProductActivity.this, "Wystąpił błąd", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+UserTransfer.getToken());
                //params.put("Cookie", UserTransfer.getJSessionID());
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void getUser() {
        RequestQueue requestQueue = Volley.newRequestQueue(ProductActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                API.userData(mProduct.getOwnerId()), //URL+mProduct.getOwnerId(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            mOwner = new User(response);
                            mOwnerName = (TextView)findViewById(R.id.user_name_text_view);
                            mOwnerName.setText(mOwner.getName()!=null?mOwner.getName() : mOwner.getLogin());

                            mOwnerPhone = (TextView)findViewById(R.id.user_phone_text_view);
                            mOwnerPhone.setText(mOwner.getPhone());

                            mOwnerEmail = (TextView)findViewById(R.id.user_email_text_view);
                            mOwnerEmail.setText(mOwner.getEmail());

                            mUserAvatar = (CircleImageView)findViewById(R.id.user_avatar_circle_image_view);
                            byte[] avatarContent = mOwner.getAvatarImage().getContent();
                            Bitmap bmp = BitmapFactory.decodeByteArray(avatarContent, 0, avatarContent.length);
                            mUserAvatar.setImageBitmap(bmp);
                        } catch(Exception e) {
                            Toast.makeText(ProductActivity.this, "Bad owner data", Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        error.printStackTrace();
                        Toast.makeText(
                                ProductActivity.this,
                                "Error while getting owner data, " + URL+"products/latest",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void addToFavourites(User user, Product product) {
        RequestQueue requestQueue = Volley.newRequestQueue(ProductActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                API.addToFavs(user.getId(), product.getId()),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(ProductActivity.this, "Dodano do ulubionych!", Toast.LENGTH_LONG).show();
                        } catch(Exception e) {
                            Toast.makeText(ProductActivity.this, "Error when addidng to favourites", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Toast.makeText(ProductActivity.this, "Error when trying to add project to favourites", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+UserTransfer.getToken());
                return params;
            }};
        requestQueue.add(jsonObjectRequest);
    }

}
