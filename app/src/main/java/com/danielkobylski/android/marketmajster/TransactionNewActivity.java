package com.danielkobylski.android.marketmajster;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class TransactionNewActivity extends AppCompatActivity {

    private Transaction mTransaction;
    private RecyclerView mRecyclerView;
    private TransactionStateAdapter mAdapter;
    private TextView mOwnerMsgHeader;
    private TextView mOwnerMsg;
    private TextView mProductName;
    private TextView mOwnerName;
    private CircleImageView mProductImage;
    private Button mOtherProductsButton;
    private Button mRejectButton;
    private Button mAcceptButton;
    private Button mAnswerButton;
    private Product mProduct;
    private int mAcceptedItemsCount;
    private TextInputEditText mMessage;
    private List<TransactionState> mTransactionStates;
    public static final String WORK_TRANSACTION = "com.danielkobylski.marketmajster.work_transaction";
    public static final String ACTIVE_TRANSACTION = "com.danielkobylski.marketmajster.active_transaction";
    public static final int REQUEST_CODE_OTHER_PRODUCTS = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_new);

        mTransaction = TransactionTransfer.getTransaction();
        mTransactionStates = new ArrayList<>();
        mOwnerMsgHeader = (TextView) findViewById(R.id.header);
        mOwnerMsg = (TextView) findViewById(R.id.owner_msg_text_view);
        mProductName = (TextView) findViewById(R.id.proposal_item_title_text_view);
        mOwnerName = (TextView) findViewById(R.id.proposal_item_owner_text_view);
        mProductImage = (CircleImageView) findViewById(R.id.proposal_item_preview_circle_image_view);
        mOtherProductsButton =  (Button) findViewById(R.id.other_products_button);
        mRejectButton = (Button) findViewById(R.id.cancel_transaction_button);
        mAcceptButton = (Button) findViewById(R.id.accept_transaction_button);
        mAnswerButton = (Button) findViewById(R.id.send_transaction_button);

        mMessage = (TextInputEditText) findViewById(R.id.transaction_proposal_message_input_text);
        mMessage.clearFocus();
        mMessage.setEnabled(true);

        mProductName.setText(mTransaction.getOfferName());
        mOwnerName.setText(mTransaction.getOwnerLogin());

        getProduct();

        String otherMsg;

        if(mTransaction.getClientId() == UserTransfer.mLoggedUser.getId()) {
           otherMsg = mTransaction.getTransactionStateMaxStep().get(0).getMessageOwner();
        }
        else {
            otherMsg = mTransaction.getTransactionStateMaxStep().get(0).getMessageClient();
        }

        mOwnerMsg.setText((otherMsg).equals("null") ? "" : otherMsg);

        mRecyclerView = (RecyclerView) findViewById(R.id.transaction_state_container);
        mAdapter = new TransactionStateAdapter(mTransactionStates, mTransaction.getClientId());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(TransactionNewActivity.this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        mOwnerMsgHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOwnerMsg.getVisibility() == View.GONE ){
                    mOwnerMsg.setVisibility(View.VISIBLE);
                    mOwnerMsgHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_up_black_24dp, 0);
                }
                else {
                    mOwnerMsg.setVisibility(View.GONE);
                    mOwnerMsgHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down_black_24dp, 0);
                }

            }
        });

        mOtherProductsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransactionNewActivity.this, ChooseOtherActivity.class);
                TransactionTransfer.setUserMsg(mMessage.getText().toString());
                TransactionTransfer.setOtherMsg(mOwnerMsg.getText().toString());
                startActivityForResult(intent, REQUEST_CODE_OTHER_PRODUCTS);
            }
        });

        mRejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                rejectTransaction();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.DialogStyle);
                builder.setMessage("Jesteś pewien?").setPositiveButton("Tak", dialogClickListener)
                        .setNegativeButton("Nie", dialogClickListener).show();
            }
        });

        final ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{android.R.attr.state_checked} , // checked
                },
                new int[]{
                        Color.parseColor("#FF0000"),
                        Color.parseColor("#00631c"),
                }
        );

        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean error = false;
                for (int i=0; i < mRecyclerView.getChildCount(); i++) {
                    CheckBox checkBox1 = mRecyclerView.getChildAt(i).findViewById(R.id.transaction_state_user_accept_check_box);
                    CheckBox checkBox2 = mRecyclerView.getChildAt(i).findViewById(R.id.transaction_state_other_accept_check_box);
                    if (checkBox1.isChecked() != true) {
                        error = true;
                        CompoundButtonCompat.setButtonTintList(checkBox1,colorStateList);
                        //checkBox1.setButtonTintList(ContextCompat.getColor(TransactionNewActivity.this, R.color.red));
                        mRecyclerView.getChildAt(i).findViewById(R.id.options_container).setVisibility(View.VISIBLE);
                        ((ImageView)mRecyclerView.getChildAt(i).findViewById(R.id.direction_arrow_image_view)).setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    }

                    if (checkBox2.isChecked() != true) {
                        error = true;
                        CompoundButtonCompat.setButtonTintList(checkBox2,colorStateList);
                        //checkBox2.setBackgroundColor(ContextCompat.getColor(TransactionNewActivity.this, R.color.red));
                        mRecyclerView.getChildAt(i).findViewById(R.id.options_container).setVisibility(View.VISIBLE);
                        ((ImageView)mRecyclerView.getChildAt(i).findViewById(R.id.direction_arrow_image_view)).setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    }
                }

                if(error == true) {
                    Toast.makeText(TransactionNewActivity.this, "Aby zakończyć transakcję, wszystkie produkty muszą zostać zaakceptowane przez obie strony!", Toast.LENGTH_LONG).show();
                }
                else{
                    mAcceptedItemsCount = 0;
                    for (int i=0; i < mTransactionStates.size(); i++) {
                       JSONObject params = new JSONObject();
                       String userSide = UserTransfer.mLoggedUser.getId() == mTransaction.getClientId() ? "\"client\"" : "\"owner\"";
                       acceptOffert(JSONifyTransactionState(mTransactionStates.get(i)), userSide);
                    }
                }

            }
        });

        mAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean error = false;
                for (int i=0; i < mRecyclerView.getChildCount(); i++) {
                    CheckBox checkBox  = mRecyclerView.getChildAt(i).findViewById(R.id.transaction_state_user_accept_check_box);
                    if (checkBox.isChecked() != true) {
                        error = true;
                        CompoundButtonCompat.setButtonTintList(checkBox,colorStateList);
                        mRecyclerView.getChildAt(i).findViewById(R.id.options_container).setVisibility(View.VISIBLE);
                        ((ImageView)mRecyclerView.getChildAt(i).findViewById(R.id.direction_arrow_image_view)).setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    }
                }
                if (error == true) {
                    Toast.makeText(TransactionNewActivity.this, "Aby wysłać, musisz zaakceptować wszystkie Twoje propozycje!", Toast.LENGTH_LONG).show();
                }
                else {
                    mAcceptedItemsCount = 0;
                    for (int i=0; i < mTransactionStates.size(); i++) {
                        JSONObject params = new JSONObject();
                        sendReply(JSONifyTransactionState(mTransactionStates.get(i)));
                    }
                }
            }
        });

        if (getIntent().getIntExtra(ACTIVE_TRANSACTION, 0) == 0) {
            setSessionAndActivity(API.transactionNewOfferList(mTransaction.getId()));
        }
        else {
            setSessionAndActivity(API.transactionActiveOfferList(mTransaction.getId()));
        }
    }

    private JSONObject JSONifyTransactionState(TransactionState transactionState) {
        JSONObject params = new JSONObject();
        try {
            params.put("buyerAccept", transactionState.isBuyerAccept());
            params.put("date", getCurrentDate());
            params.put("messageClient", UserTransfer.mLoggedUser.getId() == mTransaction.getClientId() ? mMessage.getText().toString() : mOwnerMsg.getText().toString());
            params.put("messageOwner", UserTransfer.mLoggedUser.getId() == mTransaction.getClientId() ? mOwnerMsg.getText().toString() : mMessage.getText().toString());
            params.put("offerId", transactionState.getOfferId());
            params.put("sellerAccept", transactionState.isSellerAccept());
            params.put("sideFlag", transactionState.getSideFlag());
            params.put("step", transactionState.getStep());
            params.put("transactionId", mTransaction.getId());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
    }

    private void acceptOffert(JSONObject params, String userSide) {
        RequestQueue requestQueue = Volley.newRequestQueue(TransactionNewActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,API.acceptTransaction(mTransaction.getId(), userSide),params,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mAcceptedItemsCount++;
                if (mAcceptedItemsCount == mTransactionStates.size()) {
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
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
    }

    private void sendReply(JSONObject params) {
        RequestQueue requestQueue = Volley.newRequestQueue(TransactionNewActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,API.TRANSACTION_REPLY,params,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mAcceptedItemsCount++;
                if (mAcceptedItemsCount == mTransactionStates.size()) {
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
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
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
                                mProductImage.setImageBitmap(bmp);
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
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_OTHER_PRODUCTS) {
            recreate();
        }
    }

    private String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        String day = "0"+String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String month =  "0"+String.valueOf(cal.get(Calendar.MONTH)+1);
        String year =  String.valueOf(cal.get(Calendar.YEAR));
        return year + "-" + month.substring(month.length()-2) + "-" + day.substring(day.length()-2);
    }

    private void setSessionAndActivity(String URL) {
        RequestQueue requestQueue = Volley.newRequestQueue(BarterApp.getAppContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        mTransactionStates.add(new TransactionState(response.getJSONObject(i)));
                        mAdapter.notifyDataSetChanged();
                    }
                }
                catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
            ;})
        {
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

    private void rejectTransaction() {
        RequestQueue requestQueue = Volley.newRequestQueue(BarterApp.getAppContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.rejectActiveTransaction(mTransaction.getId()), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                setResult(RESULT_OK);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TransactionNewActivity.this, "Wystąpił błąd", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+UserTransfer.getToken());
                params.put("Cookie", UserTransfer.getJSessionID());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

}
