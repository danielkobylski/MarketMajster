package com.danielkobylski.android.marketmajster;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class TransactionsNewActiveAdapter extends RecyclerView.Adapter<TransactionsNewActiveAdapter.TransactionsNewActiveViewHolder> {

    private List<Transaction> mTransactionList;

    public TransactionsNewActiveAdapter(List<Transaction> transactionList) {
        this.mTransactionList = transactionList;
    }

    @Override
    public TransactionsNewActiveAdapter.TransactionsNewActiveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_list_item, parent, false);

        return new TransactionsNewActiveAdapter.TransactionsNewActiveViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsNewActiveViewHolder holder, int position) {
        holder.message.setText("Wiadomość dotycząca transakcji: " + mTransactionList.get(position).getOfferName());
        holder.date.setText( mTransactionList.get(position).getTransactionStateMaxStep().get(0).getDate());
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
        return mTransactionList.size();
    }

    public class TransactionsNewActiveViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public TextView date;
        public int position;

        public TransactionsNewActiveViewHolder(final View view) {

            super(view);
            message = (TextView)view.findViewById(R.id.message_text_view);
            date = (TextView)view.findViewById(R.id.creation_date_text_view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TransactionTransfer.setTransaction(mTransactionList.get(position));
                    setSessionAndActivity(v);
                }
            });
        }
    }

    private void setSessionAndActivity(final View v) {
        RequestQueue requestQueue = Volley.newRequestQueue(BarterApp.getAppContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.TRANSACTION_SESSION_CLEAR, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Intent intent = new Intent(BarterApp.getAppContext(), TransactionNewActivity.class);
                intent.putExtra(TransactionNewActivity.WORK_TRANSACTION, 1);
                intent.putExtra(TransactionNewActivity.ACTIVE_TRANSACTION, 1);
                ((Activity)v.getContext()).startActivityForResult(intent, UserTransactionsActivity.REQUEST_CODE_WORK_TRANSACTION);
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

