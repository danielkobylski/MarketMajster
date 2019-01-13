package com.danielkobylski.android.marketmajster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gc.materialdesign.views.ButtonFlat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    public static final String FILTER_NAME = "com.danielkobylski.android.marketmajster.name";
    public static final String FILTER_CATEGORY = "com.danielkobylski.android.marketmajster.category";
    public static final String FILTER_LOC = "com.danielkobylski.android.marketmajster.loc";
    private static final int REQUEST_CODE_CATEGORY = 1;
    public static final int REQUEST_CODE_NEW_CITY = 2;
    private static final int PAGE_SIZE = 6;
    private final String URL = "http://192.168.0.17:8080/";
    private List<Product> mProductList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ProductsAdapter mProductsAdapter;
    private GridLayoutManager mLayoutManager;
    private String filterName;
    private SearchView mSearchView;
    private ButtonFlat mCategoryButton;
    private ButtonFlat mLocButton;
    private TextView mNotFoundTextView;
    private int pageToLoad = 0;
    private Category category;
    private City city;
    private boolean loading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mNotFoundTextView = (TextView) findViewById(R.id.search_msg_not_found);

        mRecyclerView = (RecyclerView) findViewById(R.id.products_searched_recycler_view);
        mProductsAdapter = new ProductsAdapter(mProductList);
        mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mProductsAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0)
                {
                    if (loading)
                    {
                        if (mLayoutManager.findLastCompletelyVisibleItemPosition() == mLayoutManager.getItemCount()-1)
                        {
                            loading = false;
                            Log.d(".a.", "Last Item Wow !");
                            Toast.makeText(getApplicationContext(), "Last item, wow!", Toast.LENGTH_SHORT).show();
                            loadProductData();
                            loading = true;
                        }
                    }
                }
            }
        });

        mSearchView = (SearchView) findViewById(R.id.search_search_bar);
        mSearchView.setIconified(false);
        mSearchView.clearFocus();
        filterName = getIntent().getStringExtra(FILTER_NAME);
        if (filterName!=null) {
            mSearchView.setQuery(filterName, false);
            mSearchView.clearFocus();
        }

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mProductList.clear();
                pageToLoad=0;
                loadProductData();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    this.onQueryTextSubmit("");
                }
                return false;
            }
        });

        mCategoryButton = (ButtonFlat) findViewById(R.id.search_product_category_button);
        mCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, CategoryChoiceActivity.class);
                intent.putExtra("Caller", 1);
                startActivityForResult(intent, REQUEST_CODE_CATEGORY);
            }
        });
        category = getIntent().getParcelableExtra(FILTER_CATEGORY);
        if (category!=null) {mCategoryButton.setText(category.getName());} else {category = new Category((long)-1,"");};

        mLocButton = (ButtonFlat) findViewById(R.id.search_product_loc_button);
        mLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, LocalisationChoiceActivity.class);
                startActivityForResult(intent, REQUEST_CODE_NEW_CITY);
            }
        });
        city = getIntent().getParcelableExtra(FILTER_LOC);
        if (city!= null) {mLocButton.setText(city.getName());} else {city = new City((long)-1, (long)-1, "");}



        loadProductData();

    }

    public void onResume() {
        super.onResume();

        if (getIntent().getIntExtra("refresher", -1) == 1) {
            finish();
        }

        //mSearchView.setIconified(false);
        //mRecyclerView.setAdapter(mProductsAdapter);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CATEGORY) {
            if (data == null) {
                return;
            }
            category = CategoryChoiceActivity.getNewCategory(data);
            if (category==null) {category = new Category((long)-1,"");}
            pageToLoad = 0;
            mProductList.clear();
            mProductsAdapter.notifyDataSetChanged();
            mCategoryButton.setText(category.getName());
            loadProductData();
        }
        else if (requestCode == REQUEST_CODE_NEW_CITY) {
            city = data.getParcelableExtra(CitiesAdapter.CITY_CHOSEN);
            pageToLoad = 0;
            mProductList.clear();
            mProductsAdapter.notifyDataSetChanged();
            mLocButton.setText(city.getName());
            loadProductData();
        }
    }

    public void loadProductData() {
        RequestQueue requestQueue = Volley.newRequestQueue(SearchActivity.this);
        Log.d("url@@@", URL+"products/search?param="+mSearchView.getQuery()+"&categoryId="+category.getId()+"&cityId="+city.getId()+"&voivoId="+city.getVoivoId()+"&page=" + pageToLoad + "&size=" + PAGE_SIZE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL+"products/search/and?param="+mSearchView.getQuery()+"&categoryId="+category.getId()+"&cityId="+city.getId()+"&voivoId="+city.getVoivoId()+"&page=" + pageToLoad + "&size=" + PAGE_SIZE,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONArray products = response.getJSONArray("content");
                            if (products.length() > 0) {
                                for (int i = 0; i < products.length(); i++) {
                                    mProductList.add(new Product(products.getJSONObject(i)));
                                    mProductsAdapter.notifyDataSetChanged();
                                }
                                pageToLoad++;
                            }
                            if (mProductList.size() == 0) {
                                mNotFoundTextView.setText("Nie znaleziono ofert.");
                            }
                            else {
                                mNotFoundTextView.setText("");
                            }
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
                                SearchActivity.this,
                                "Error while getting products data, ",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
    

}
