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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TransactionsSentAdapter extends RecyclerView.Adapter<TransactionsSentAdapter.TransactionsSentViewHolder> {

    private List<Transaction> mTransactionList;

    public TransactionsSentAdapter(List<Transaction> transactionList) {
        this.mTransactionList = transactionList;
    }

    @Override
    public TransactionsSentAdapter.TransactionsSentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_list_item, parent, false);

        return new TransactionsSentAdapter.TransactionsSentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsSentViewHolder holder, int position) {
        holder.message.setText("Propozycja wysłana do użytkownika " + mTransactionList.get(position).getOwnerLogin());
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

    public class TransactionsSentViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public TextView date;
        public int position;

        public TransactionsSentViewHolder(final View view) {

            super(view);
            message = (TextView)view.findViewById(R.id.message_text_view);
            date = (TextView)view.findViewById(R.id.creation_date_text_view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), TransactionSentActivity.class);
                    intent.putExtra(TransactionSentActivity.CANCEL_TRANSACTION, 1);
                    TransactionTransfer.setTransaction(mTransactionList.get(position));
                    ((Activity)v.getContext()).startActivityForResult(intent, UserTransactionsActivity.REQUEST_CODE_CANCEL_TRANSACTION);
                }
            });
        }
    }

}

