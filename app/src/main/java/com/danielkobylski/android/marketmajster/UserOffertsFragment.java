package com.danielkobylski.android.marketmajster;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserOffertsFragment extends Fragment {

    private List<Product> mProductList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private UserOffertAdapter mOffertAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_user_offerts, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.user_offerts_recycler_view);
        mOffertAdapter = new UserOffertAdapter(mProductList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        initUserProductData();

        return v;
    }
    public void initUserProductData() {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
        mProductList.clear();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                API.userProducts(UserTransfer.mLoggedUser.getId()),//"http://192.168.0.17:8080/products/owner?ownerId=" + UserTransfer.mLoggedUser.getId() + "&active=true",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray products = response.getJSONArray("content");
                            for (int i = 0; i < products.length(); i++) {
                                mProductList.add(new Product(products.getJSONObject(i)));
                                mOffertAdapter.notifyDataSetChanged();
                            }
                            mRecyclerView.setAdapter(mOffertAdapter);
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
        requestQueue.add(jsonObjectRequest);
    }

}
