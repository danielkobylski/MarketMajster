package com.danielkobylski.android.marketmajster;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class TransactionNewActivity extends AppCompatActivity {

    private List<Integer> mTransactionList;
    private RecyclerView mRecyclerView;
    private TransactionStateAdapter mAdapter;
    private TextInputEditText mMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_new);

        final TextView title = (TextView) findViewById(R.id.header);
        final TextView msg = (TextView) findViewById(R.id.owner_msg_text_view);

        mMessage = (TextInputEditText) findViewById(R.id.transaction_proposal_message_input_text);
        mMessage.clearFocus();
        mMessage.setEnabled(true);

        mTransactionList = new ArrayList<>();
        mTransactionList.add(1);
        mTransactionList.add(2);
        mTransactionList.add(3);
        mTransactionList.add(4);
        mRecyclerView = (RecyclerView) findViewById(R.id.transaction_state_container);
        mAdapter = new TransactionStateAdapter(mTransactionList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(TransactionNewActivity.this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msg.getVisibility() == View.GONE ){
                    msg.setVisibility(View.VISIBLE);
                    title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_up_black_24dp, 0);
                }
                else {
                    msg.setVisibility(View.GONE);
                    title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down_black_24dp, 0);
                }

            }
        });

    }
}
