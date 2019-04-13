package com.danielkobylski.android.marketmajster;

import android.app.Activity;
import android.content.Intent;
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
import com.android.volley.toolbox.Volley;
import com.gc.materialdesign.views.ButtonFlat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ButtonFlat mSubmitButton;

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
        if (mProduct.getImageList().size() > 0) {
            byte[] avatarContent = mProduct.getImageList().get(0).getContent();
            Bitmap bmp = BitmapFactory.decodeByteArray(avatarContent, 0, avatarContent.length);
            mOffertPreview.setImageBitmap(bmp);
        }
        mMessage = (TextInputEditText)findViewById(R.id.transaction_proposal_message_input_text);
        mMessage.clearFocus();

        mSubmitButton = (ButtonFlat) findViewById(R.id.transaction_proposal_submit_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitTransactionProposal();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.transaction_proposal_user_item_container);
        mProductsAdapter = new ProductProposalAdapter(mProductList, true);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mProductsAdapter);

        if (UserTransfer.mLoggedUser == null) {
            Intent intent = new Intent(TransactionProposalActivity.this, LoginActivity.class);
            startActivityForResult(intent, MainActivity.REQUEST_CODE_LOGIN);
        }
        else { initUserProductData(); }
    }
//UserTransfer.mLoggedUser.getId())
    public void initUserProductData() {
        RequestQueue requestQueue = Volley.newRequestQueue(TransactionProposalActivity.this);
        mProductList.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                API.userProducts(UserTransfer.mLoggedUser.getId()),//"http://192.168.0.17:8080/products/owner?ownerId=" + UserTransfer.mLoggedUser.getId() + "&active=true",
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
                        //Log.d("Response error",error.getMessage());
                        Toast.makeText(
                                TransactionProposalActivity.this,
                                "http://192.168.0.17:8080/products/owner?ownerId=" + UserTransfer.mLoggedUser.getId() + "&active=true",
                                Toast.LENGTH_LONG
                        ).show();
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
        requestQueue.add(jsonArrayRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        initUserProductData();
    }

    private void submitTransactionProposal() {
        JSONObject offertData = new JSONObject();
        List<Long> checked = new ArrayList<>();
        long[] checkedArr;
        try {
            offertData.put("ownerId", mOwner.getId());
            offertData.put("clientId",UserTransfer.mLoggedUser.getId() );
            offertData.put("offerId", mProduct.getId());
            offertData.put("messageClient", mMessage.getText().toString());
            offertData.put("status", 1);
            for (int i=0; i < mRecyclerView.getChildCount(); i++) {
                CheckBox checkBox = mRecyclerView.getChildAt(i).findViewById(R.id.transaction_proposal_item_check_box);
                if (checkBox.isChecked()) {
                    checked.add(mProductList.get(i).getId());
                }
            }
            checkedArr = new long[checked.size()];
            for (int i = 0; i < checked.size(); i++) {
                checkedArr[i] = checked.get(i);
            }
            offertData.put("ids",new JSONArray(checked)); //Arrays.toString(checkedArr)
        }

        catch(JSONException e) {e.printStackTrace();}

        RequestQueue requestQueue = Volley.newRequestQueue(BarterApp.getAppContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,API.SUBMIT_PROPOSAL,offertData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            setResult(RESULT_OK);
                            finish();
                        }
                        catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TransactionProposalActivity.this, "Wystąpił błąd", Toast.LENGTH_LONG).show();
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
