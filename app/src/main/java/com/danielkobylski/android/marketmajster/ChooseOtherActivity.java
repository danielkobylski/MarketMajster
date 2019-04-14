package com.danielkobylski.android.marketmajster;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChooseOtherActivity extends AppCompatActivity {

    private List<Product> mProductList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ProductProposalAdapter mProductsAdapter;
    private LinearLayoutManager mLayoutManager;
    private TextView mOwnerTextView;
    private Transaction mTransaction;
    private Button mSubmitButton;
    private List<Long> mChosenList;
    private int addedToSessionCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_other);

        addedToSessionCount = 0;

        mTransaction = TransactionTransfer.getTransaction();

        mChosenList = new ArrayList<>();

        mOwnerTextView = (TextView)findViewById(R.id.user_offers_text_view);
        mOwnerTextView.setText("Oferty użytkownika " + mTransaction.getOwnerLogin());

        if(mTransaction.getClientId() == UserTransfer.mLoggedUser.getId()) {
            mOwnerTextView.setText("Oferty użytkownika " + mTransaction.getOwnerLogin());
        }
        else {
            mOwnerTextView.setText("Oferty użytkownika " + mTransaction.getClientLogin());
        }

        mSubmitButton = (Button) findViewById(R.id.submit_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChosenList.clear();
                for (int i=0; i < mRecyclerView.getChildCount(); i++) {
                    CheckBox checkBox = mRecyclerView.getChildAt(i).findViewById(R.id.transaction_proposal_item_check_box);
                    if (checkBox.isChecked()) {
                        mChosenList.add(mProductList.get(i).getId());
                    }
                }

                for (int i = 0; i < mChosenList.size(); i++) {
                    addProductsToSession(mChosenList.get(i));
                }
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.products_user_recycler_view);
        mProductsAdapter = new ProductProposalAdapter(mProductList, true);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mProductsAdapter);
        initUserProductData();
    }

    public void initUserProductData() {
        RequestQueue requestQueue = Volley.newRequestQueue(ChooseOtherActivity.this);
        mProductList.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                API.transactionOtherProducts(mTransaction.getClientId() == UserTransfer.mLoggedUser.getId() ? mTransaction.getOwnerId() : mTransaction.getClientId()),
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

    private void addProductsToSession(final long productId) {
        JSONObject params = new JSONObject();
        try {
            params.put("offerId", productId);
            params.put("id", null);
            params.put("transactionId", mTransaction.getId());
            params.put("step", mTransaction.getTransactionStateMaxStep().get(0).getStep()+1);

            if(UserTransfer.mLoggedUser.getId() == mTransaction.getOwnerId()) {
                params.put("sellerAccept", true);
                params.put("buyerAccept", false);
                params.put("sideFlag", 0);
                params.put("messageOwner", TransactionTransfer.getUserMsg());
                params.put("messageClient", TransactionTransfer.getOtherMsg());
            }
            else {
                params.put("sellerAccept", false);
                params.put("buyerAccept", true);
                params.put("sideFlag", 1);
                params.put("messageOwner", TransactionTransfer.getOtherMsg());
                params.put("messageClient", TransactionTransfer.getUserMsg());
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(ChooseOtherActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,API.TRANSACTION_SAVE_ANOTHER,params,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                addedToSessionCount++;
                if (addedToSessionCount == mChosenList.size()) {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
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
        requestQueue.add(jsonObjectRequest);
    }
}
