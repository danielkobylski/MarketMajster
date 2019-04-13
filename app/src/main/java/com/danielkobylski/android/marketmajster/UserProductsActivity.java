package com.danielkobylski.android.marketmajster;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProductsActivity extends AppCompatActivity {

    private List<Product> mProductList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ProductsAdapter mProductsAdapter;
    private GridLayoutManager mLayoutManager;
    private TextView mOwnerTextView;
    private User mOwner;
    private CircleImageView mUserAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_products);

        mOwner = UserTransfer.getOffertOwner();
        mOwnerTextView = (TextView)findViewById(R.id.user_offers_text_view);
        mOwnerTextView.setText("Oferty użytkownika " + mOwner.getName());

        mUserAvatar = (CircleImageView)findViewById(R.id.user_products_avatar);
        byte[] avatarContent = mOwner.getAvatarImage().getContent();
        Bitmap bmp = BitmapFactory.decodeByteArray(avatarContent, 0, avatarContent.length);
        mUserAvatar.setImageBitmap(bmp);

        mRecyclerView = (RecyclerView) findViewById(R.id.products_user_recycler_view);
        mProductsAdapter = new ProductsAdapter(mProductList);
        mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mProductsAdapter);
        initUserProductData();
    }

    public void initUserProductData() {
        RequestQueue requestQueue = Volley.newRequestQueue(UserProductsActivity.this);
        mProductList.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                API.otherProducts(UserTransfer.getOffertOwner().getId(), ProductTransfer.getProduct().getId()),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            //JSONArray products = response.getJSONArray("content");
                            for (int i = 0; i < response.length(); i++) {
                                mProductList.add(new Product(response.getJSONObject(i)));
                                mProductsAdapter.notifyDataSetChanged();
                            }
                            mRecyclerView.setAdapter(mProductsAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+UserTransfer.getToken());
                params.put("Cookie", UserTransfer.getJSessionID());
                return params;
            }};
        requestQueue.add(jsonArrayRequest);
    }
}
