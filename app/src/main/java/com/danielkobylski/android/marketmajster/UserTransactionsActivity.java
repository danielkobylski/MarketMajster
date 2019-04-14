package com.danielkobylski.android.marketmajster;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class UserTransactionsActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_CANCEL_TRANSACTION = 3;
    public static final int REQUEST_CODE_WORK_TRANSACTION = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_transactions);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        UserTransactionsNewFragment userTransactionsNewFragment = new UserTransactionsNewFragment();
        UserTransactionsSentFragment userTransactionsSentFragment = new UserTransactionsSentFragment();
        UserTransactionsActiveNewFragment userTransactionsActiveNewFragment = new UserTransactionsActiveNewFragment();
        UserTransactionsActiveSentFragment userTransactionsActiveSentFragment = new UserTransactionsActiveSentFragment();

        adapter.addFragment(userTransactionsNewFragment, "Nowe");
        adapter.addFragment(userTransactionsSentFragment, "Wysłane");
        adapter.addFragment(userTransactionsActiveNewFragment, "Aktywne oczekujące");
        adapter.addFragment(userTransactionsActiveSentFragment, "Aktywne wysłane");

        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.user_transactions_tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CANCEL_TRANSACTION || requestCode == REQUEST_CODE_WORK_TRANSACTION) {
                recreate();
            }
        }
    }
}
