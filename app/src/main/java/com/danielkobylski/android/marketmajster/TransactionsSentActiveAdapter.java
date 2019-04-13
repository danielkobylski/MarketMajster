package com.danielkobylski.android.marketmajster;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TransactionsSentActiveAdapter extends RecyclerView.Adapter<TransactionsSentActiveAdapter.TransactionsSentActiveViewHolder> {

    private List<Transaction> mTransactionList;

    public TransactionsSentActiveAdapter(List<Transaction> transactionList) {
        this.mTransactionList = transactionList;
    }

    @Override
    public TransactionsSentActiveAdapter.TransactionsSentActiveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_list_item, parent, false);

        return new TransactionsSentActiveAdapter.TransactionsSentActiveViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsSentActiveViewHolder holder, int position) {
        holder.message.setText("Odpowiedź do transakcji: " + mTransactionList.get(position).getOfferName());
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

    public class TransactionsSentActiveViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public TextView date;
        public int position;

        public TransactionsSentActiveViewHolder(final View view) {

            super(view);
            message = (TextView)view.findViewById(R.id.message_text_view);
            date = (TextView)view.findViewById(R.id.creation_date_text_view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

}

