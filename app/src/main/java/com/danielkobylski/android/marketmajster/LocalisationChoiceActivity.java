package com.danielkobylski.android.marketmajster;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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

public class LocalisationChoiceActivity extends AppCompatActivity {

    private List<Voivodeship> mVoivodeshipList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private LocalisationAdapter mCatAdapter;
    private LinearLayoutManager mLayoutManager;
    private SearchView mSearchView;
    private final String URL = "http://192.168.0.17:8080/voivo/all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localisation_choice);

        mVoivodeshipList.add(new Voivodeship((long)-1,"Cała Polska").addCity(new City((long)-1,(long)-1,"Cała Polska")));

        mSearchView = (SearchView) findViewById(R.id.city_search_bar);
        mSearchView.setIconified(false);
        mSearchView.clearFocus();
        mSearchView.setQueryHint("Wpisz miasto");

        mRecyclerView = (RecyclerView) findViewById(R.id.voivode_choice_recycler_view);
        mCatAdapter = new LocalisationAdapter(mVoivodeshipList);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mCatAdapter);

        initVoivodeshipData();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == SearchActivity.REQUEST_CODE_NEW_CITY) {
            if (data == null) {
                return;
            }
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private void initVoivodeshipData() {
        RequestQueue requestQueue = Volley.newRequestQueue(LocalisationChoiceActivity.this);
        //mVoivodeshipList.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            for(int i=0;i<response.length();i++){
                                mVoivodeshipList.add(new Voivodeship(response.getJSONObject(i)));
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
                                LocalisationChoiceActivity.this,
                                "Error while getting categories data",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }
}
