package com.danielkobylski.android.marketmajster;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TransactionStateAdapter extends RecyclerView.Adapter<TransactionStateAdapter.TransactionsStateViewHolder> {

    private List<Integer> mTransactionList;

    public TransactionStateAdapter(List<Integer> transactionList) {
        this.mTransactionList = transactionList;
    }

    @Override
    public TransactionStateAdapter.TransactionsStateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_state_list_item, parent, false);

        return new TransactionStateAdapter.TransactionsStateViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsStateViewHolder holder, int position) {
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

    public class TransactionsStateViewHolder extends RecyclerView.ViewHolder {

        public ImageView directionalArrow;
        public ConstraintLayout optionContainer;
        public int position;

        public TransactionsStateViewHolder(final View view) {

            super(view);
            directionalArrow = (ImageView)view.findViewById(R.id.direction_arrow_image_view);
            optionContainer = (ConstraintLayout)view.findViewById(R.id.options_container);

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
        }
    }

}

