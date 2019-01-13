package com.danielkobylski.android.marketmajster;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TransactionProposalActivity extends AppCompatActivity {

    private TextView mOffertTitle;
    private TextView mOffertOwner;
    private CircleImageView mOffertPreview;
    private Product mProduct;
    private User mOwner;
    private List<Product> mProductList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ProductProposalAdapter mProductsAdapter;
    private LinearLayoutManager mLayoutManager;
    private TextInputEditText mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_proposal);

        mProduct = ProductTransfer.getOffert();
        mOwner = UserTransfer.getOffertOwner();

        mOffertTitle = (TextView) findViewById(R.id.proposal_item_title_text_view);
        mOffertTitle.setText(mProduct.getName());

        mOffertOwner = (TextView)findViewById(R.id.proposal_item_owner_text_view);
        mOffertOwner.setText(mOwner.getName());

        mOffertPreview = (CircleImageView)findViewById(R.id.proposal_item_preview_circle_image_view);
        byte[] avatarContent = mProduct.getImageList().get(0).getContent();
        Bitmap bmp = BitmapFactory.decodeByteArray(avatarContent, 0, avatarContent.length);
        mOffertPreview.setImageBitmap(bmp);

        mMessage = (TextInputEditText)findViewById(R.id.transaction_proposal_message_input_text);
        mMessage.clearFocus();

        mRecyclerView = (RecyclerView) findViewById(R.id.transaction_proposal_user_item_container);
        mProductsAdapter = new ProductProposalAdapter(mProductList);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mProductsAdapter);

        //IF THE USER IS NOT LOGGED, PROMPT LOGINACTIVITY

        initUserProductData();

    }

    public void initUserProductData() {
        RequestQueue requestQueue = Volley.newRequestQueue(TransactionProposalActivity.this);
        mProductList.clear();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                "http://192.168.0.17:8080/products/owner?ownerId=" + UserTransfer.mLoggedUser.getId() + "&active=true",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray products = response.getJSONArray("content");
                            for (int i = 0; i < products.length(); i++) {
                                mProductList.add(new Product(products.getJSONObject(i)));
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
                        //Log.d("Response error",error.getMessage());
                        Toast.makeText(
                                TransactionProposalActivity.this,
                                "http://192.168.0.17:8080/products/owner?ownerId=" + UserTransfer.mLoggedUser.getId() + "&active=true",
                                Toast.LENGTH_LONG
                        ).show();
                        Log.d("Volley error: ",error.getMessage());
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

}
