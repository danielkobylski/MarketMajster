package com.danielkobylski.android.marketmajster;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gc.materialdesign.views.ButtonFlat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class TransactionSentActivity extends AppCompatActivity {

    private TextView mOffertTitle;
    private TextView mOffertOwner;
    private CircleImageView mOffertPreview;
    private Product mProduct;
    private User mOwner;
    private Transaction mTransaction;
    private List<Product> mProductList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ProductProposalAdapter mProductsAdapter;
    private LinearLayoutManager mLayoutManager;
    private TextInputEditText mMessage;
    private ButtonFlat mSubmitButton;
    public static final String CANCEL_TRANSACTION = "com.danielkobylski.marketmajster.cancel_transaction";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_sent);

        mTransaction = TransactionTransfer.getTransaction();

        //mProduct = ProductTransfer.getOffert();

        mOffertTitle = (TextView) findViewById(R.id.proposal_item_title_text_view);
        mOffertTitle.setText(mTransaction.getOfferName());

        mOffertOwner = (TextView)findViewById(R.id.proposal_item_owner_text_view);
        mOffertOwner.setText(mTransaction.getOwnerLogin());

        mOffertPreview = (CircleImageView)findViewById(R.id.proposal_item_preview_circle_image_view);

        mMessage = (TextInputEditText)findViewById(R.id.transaction_proposal_message_input_text);
        mMessage.setText((mTransaction.getMessageClient().equals("null") ? "": mTransaction.getMessageClient()));
        mMessage.clearFocus();

        mSubmitButton = (ButtonFlat) findViewById(R.id.transaction_proposal_submit_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                cancelTransactionProposal();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.DialogStyle);
                builder.setMessage("Jesteś pewien?").setPositiveButton("Tak", dialogClickListener)
                        .setNegativeButton("Nie", dialogClickListener).show();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.transaction_proposal_user_item_container);
        mProductsAdapter = new ProductProposalAdapter(mProductList,false);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mProductsAdapter);

        initProposedProductData();
        getProduct();

    }

    private void cancelTransactionProposal() {
        RequestQueue requestQueue = Volley.newRequestQueue(BarterApp.getAppContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.cancelTransaction(mTransaction.getId()), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                setResult(RESULT_OK);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + UserTransfer.getToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void initProposedProductData() {
        List<Long> productIDs = new ArrayList<>();
        for (TransactionState ts: mTransaction.getTransactionStateMaxStep()) {
            productIDs.add(ts.getOfferId());
        }
        String arrayStr = productIDs.toString().substring(1, productIDs.toString().length()-1).replace(" ","");
        RequestQueue requestQueue = Volley.newRequestQueue(TransactionSentActivity.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                API.productsList(arrayStr),
                new JSONArray(productIDs),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            if (response.length() > 0) {
                                for (int i = 0; i < response.length(); i++) {
                                    mProductList.add(new Product(response.getJSONObject(i)));
                                    mProductsAdapter.notifyDataSetChanged();
                                }
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        error.printStackTrace();
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);

    }

    private void getProduct() {
        RequestQueue requestQueue = Volley.newRequestQueue(BarterApp.getAppContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,API.getProduct(mTransaction.getOfferId()),null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            mProduct = new Product(response);
                            if (mProduct.getImageList().size() > 0) {
                                byte[] avatarContent = mProduct.getImageList().get(0).getContent();
                                Bitmap bmp = BitmapFactory.decodeByteArray(avatarContent, 0, avatarContent.length);
                                mOffertPreview.setImageBitmap(bmp);
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

}
