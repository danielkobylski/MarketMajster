package com.danielkobylski.android.marketmajster;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class UserFavsFragment extends Fragment {

    private List<Product> mProductList;
    private RecyclerView mRecyclerView;
    private UserFavsAdapter mOffertAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_user_favs, container, false);

        mProductList = UserTransfer.mLoggedUser.getFavourites();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.user_favs_recycler_view);
        mOffertAdapter = new UserFavsAdapter(mProductList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mOffertAdapter);
        mOffertAdapter.notifyDataSetChanged();
        return v;
    }



}