package com.danielkobylski.android.marketmajster;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.TextView;

public class test_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_activity);

        final CardView title = (CardView) findViewById(R.id.card);
        final TextView msg = (TextView) findViewById(R.id.msg);

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msg.getVisibility() == View.GONE ){
                    TransitionManager.beginDelayedTransition(title);
                    msg.setVisibility(View.VISIBLE);
                }
                else {
                    TransitionManager.beginDelayedTransition(title);
                    msg.setVisibility(View.GONE);
                }

            }
        });

    }


}
