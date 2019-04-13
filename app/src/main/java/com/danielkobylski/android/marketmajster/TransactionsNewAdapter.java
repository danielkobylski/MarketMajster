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

public class TransactionsNewAdapter extends RecyclerView.Adapter<TransactionsNewAdapter.TransactionsNewViewHolder> {

    private List<Transaction> mTransactionList;

    public TransactionsNewAdapter(List<Transaction> transactionList) {
        this.mTransactionList = transactionList;
    }

    @Override
    public TransactionsNewAdapter.TransactionsNewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_list_item, parent, false);

        return new TransactionsNewAdapter.TransactionsNewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsNewViewHolder holder, int position) {
       holder.message.setText("Propozycja użytkownika " + mTransactionList.get(position).getClientLogin());
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

    public class TransactionsNewViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public TextView date;
        public int position;

        public TransactionsNewViewHolder(final View view) {

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

