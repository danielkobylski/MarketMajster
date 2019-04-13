package com.danielkobylski.android.marketmajster;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserTransactionsSentFragment extends Fragment {

    private List<Transaction> mTransactionList;
    private RecyclerView mRecyclerView;
    private TransactionsSentAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_transactions, container, false);

        mTransactionList = new ArrayList<>();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mAdapter = new TransactionsSentAdapter(mTransactionList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        getSentTransactions();

        mAdapter.notifyDataSetChanged();
        return v;
    }

    public void getSentTransactions() {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
        mTransactionList.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                API.sentTransactions(UserTransfer.mLoggedUser.getId()),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                mTransactionList.add(new Transaction(response.getJSONObject(i)));
                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+UserTransfer.getToken());
                return params;
            }};
        requestQueue.add(jsonArrayRequest);
    }

}
