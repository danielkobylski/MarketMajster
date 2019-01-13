package com.danielkobylski.android.marketmajster;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class CategoryChoiceActivity extends AppCompatActivity {

    private static final String CATEGORY_CHOSEN = "com.ciastkaipiwo.android.scrummajster.category_chosen";
    private List<Category> mCatList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private CategoriesAdapter mCatAdapter;
    private LinearLayoutManager mLayoutManager;
    private final String URL = "http://192.168.0.17:8080/categories/all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_choice);

        mCatList.add(new Category((long)-1, "Wszystkie"));

        mRecyclerView = (RecyclerView) findViewById(R.id.category_choice_recycler_view);
        mCatAdapter = new CategoriesAdapter(mCatList);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mCatAdapter);

        initCategoryData();
    }

    private void initCategoryData() {

        RequestQueue requestQueue = Volley.newRequestQueue(CategoryChoiceActivity.this);
        //mCatList.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            for(int i=0;i<response.length();i++){
                                mCatList.add(new Category(response.getJSONObject(i)));
                                mCatAdapter.notifyDataSetChanged();
                            }
                            mRecyclerView.setAdapter(mCatAdapter);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        //Log.d("Response error",error.getMessage());
                        Toast.makeText(
                                CategoryChoiceActivity.this,
                                "Error while getting categories data",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);

    }

    public static Category getNewCategory(Intent result) {
        return (Category) result.getParcelableExtra(CATEGORY_CHOSEN);
    }

}
