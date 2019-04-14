package com.danielkobylski.android.marketmajster;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class TransactionStateAdapter extends RecyclerView.Adapter<TransactionStateAdapter.TransactionsStateViewHolder> {

    private List<TransactionState> mTransactionStateList;
    private long mClientID;

    public TransactionStateAdapter(List<TransactionState> transactionStateList, long clientID) {
        this.mTransactionStateList = transactionStateList;
        this.mClientID = clientID;
    }

    @Override
    public TransactionStateAdapter.TransactionsStateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_state_list_item, parent, false);

        return new TransactionStateAdapter.TransactionsStateViewHolder(itemView);
    }

    public List<TransactionState> getTransactionStateList() {return mTransactionStateList;}

    @Override
    public void onBindViewHolder(@NonNull TransactionsStateViewHolder holder, int position) {
        holder.productName.setText(mTransactionStateList.get(position).getOfferName());

        if(mClientID == UserTransfer.mLoggedUser.getId()) {
            holder.userAcceptCheckBox.setChecked(mTransactionStateList.get(position).isBuyerAccept());
            holder.otherAcceptCheckBox.setChecked(mTransactionStateList.get(position).isSellerAccept());
        }
        else {
            holder.userAcceptCheckBox.setChecked(mTransactionStateList.get(position).isSellerAccept());
            holder.otherAcceptCheckBox.setChecked(mTransactionStateList.get(position).isBuyerAccept());
        }

        getProductImage(holder, mTransactionStateList.get(position).getOfferId());

        holder.position = position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mTransactionStateList.size();
    }

    public class TransactionsStateViewHolder extends RecyclerView.ViewHolder {

        public ImageView directionalArrow;
        public ConstraintLayout optionContainer;
        public CircleImageView productImage;
        public TextView productName;
        public Button showOffertButton;
        public Button acceptButton;
        public Button deleteButton;
        public CheckBox userAcceptCheckBox;
        public CheckBox otherAcceptCheckBox;
        public int position;

        public TransactionsStateViewHolder(final View view) {

            super(view);
            directionalArrow = (ImageView)view.findViewById(R.id.direction_arrow_image_view);
            optionContainer = (ConstraintLayout)view.findViewById(R.id.options_container);
            productImage = (CircleImageView) view.findViewById(R.id.transaction_proposal_item_preview);
            showOffertButton = (Button) view.findViewById(R.id.transaction_proposal_view_product_button);
            productName = (TextView) view.findViewById(R.id.transaction_proposal_item_name_text_view);
            acceptButton = (Button)view.findViewById(R.id.transaction_product_accept_button);
            deleteButton = (Button)view.findViewById(R.id.transaction_product_delete_button);
            userAcceptCheckBox = (CheckBox)view.findViewById(R.id.transaction_state_user_accept_check_box) ;
            otherAcceptCheckBox = (CheckBox)view.findViewById(R.id.transaction_state_other_accept_check_box);

            directionalArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(optionContainer.getVisibility()==View.GONE) {
                        directionalArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                        optionContainer.setVisibility(View.VISIBLE);
                    }
                    else {
                        directionalArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                        optionContainer.setVisibility(View.GONE);
                    }
                }
            });

            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userAcceptCheckBox.isChecked() == true) {
                        Toast.makeText(v.getContext(), "Już zaakceptowałeś ten produkt!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (mClientID == UserTransfer.mLoggedUser.getId()) {
                            acceptItem(mTransactionStateList.get(position).getOfferId(), userAcceptCheckBox, "\"client\"");
                        }
                        else {
                            acceptItem(mTransactionStateList.get(position).getOfferId(), userAcceptCheckBox, "\"owner\"");
                        }

                    }
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    removeTransactionItem(mTransactionStateList.get(position).getOfferId(), position, v);
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
        }
    }

    private void getProductImage(@NonNull final TransactionsStateViewHolder holder, Long productId) {
        RequestQueue requestQueue = Volley.newRequestQueue(BarterApp.getAppContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,API.getProduct(productId),null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Product product = new Product(response);
                            if (product.getImageList().size() > 0) {
                                byte[] avatarContent = product.getImageList().get(0).getContent();
                                Bitmap bmp = BitmapFactory.decodeByteArray(avatarContent, 0, avatarContent.length);
                                holder.productImage.setImageBitmap(bmp);
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

    private void removeTransactionItem(final long offerId, final int position, final View v) {
        RequestQueue requestQueue = Volley.newRequestQueue(BarterApp.getAppContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.removeTransactionItem(offerId), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ((Activity)v.getContext()).recreate();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
        requestQueue.add(stringRequest);
    }

    private void acceptItem(long offerId, final CheckBox userAcceptCheckBox, String userSide) {
        RequestQueue requestQueue = Volley.newRequestQueue(BarterApp.getAppContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.userItemAccept(offerId, userSide), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                userAcceptCheckBox.setChecked(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
        requestQueue.add(stringRequest);
    }

}

