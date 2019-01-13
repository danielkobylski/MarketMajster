package com.danielkobylski.android.marketmajster;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CityChoiceActivity extends AppCompatActivity {

    public static final String CITY_NAME = "com.ciastkaipiwo.android.scrummajster.city_name";
    private List<City> mCityList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private CitiesAdapter mCitiesAdapter;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_choice);

        Voivodeship voivodeship = getIntent().getParcelableExtra(CITY_NAME);
        mCityList = voivodeship.getCities();
        mCityList.add(0, new City((long)-1, voivodeship.getId(), voivodeship.getName()));

        mRecyclerView = (RecyclerView) findViewById(R.id.city_choice_recycler_view);
        mCitiesAdapter = new CitiesAdapter(mCityList);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mCitiesAdapter);

    }
}
