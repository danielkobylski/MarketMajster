package com.danielkobylski.android.marketmajster;

import android.accounts.Account;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.design.button.MaterialButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.SearchView;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private static DrawerLayout mDrawerLayout;
    private static ImageView mLogo;
    private static SearchView mSearchView;
    private static List<Product> mProductList = new ArrayList<>();
    private static RecyclerView mRecyclerView;
    private static ProductsAdapter mProductsAdapter;
    private static ButtonFlat mCategoryButton;
    private static ButtonFlat mLocButton;
    private static final String URL = "http://s12.mydevil.net:8080/barter/"; //"http://192.168.0.17:8080/";
    private static final int PAGE_SIZE = 6;
    private static GridLayoutManager mLayoutManager;
    private static boolean loading = true;
    private static Typeface typeface;
    private static int pageToLoad = 0;
    private static final int REQUEST_CODE_NEW_LOC = 3;
    public static final int REQUEST_CODE_LOGIN = 4;
    private static final int REQUEST_CODE_CHANGE_PASS = 5;
    private static TextView mLogoutTextView;
    private static TextView mLoginTextView;
    private static CircleImageView mLoginAvatar;
    private static NavigationView mNavigationView;
    private static Intent mIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        mNavigationView = findViewById(R.id.nav_view);
        mLogo = mNavigationView.getHeaderView(0).findViewById(R.id.logo);
        mLogoutTextView = mNavigationView.getHeaderView(0).findViewById(R.id.drawer_text_logout);
        mLoginTextView = mNavigationView.getHeaderView(0).findViewById(R.id.drawer_text_login);
        mLoginAvatar = mNavigationView.getHeaderView(0).findViewById(R.id.login_avatar);

        changeDrawerData(null);

        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch(menuItem.getItemId()) {
                            case R.id.login:
                                mIntent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivityForResult(mIntent, REQUEST_CODE_LOGIN);
                                break;
                            case R.id.logout:
                                UserTransfer.mLoggedUser = null;
                                SharedPreferences preferences = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                                preferences.edit().remove("login").commit();
                                preferences.edit().remove("password").commit();
                                changeDrawerData(null);
                                break;
                            case R.id.add_offer:
                                mIntent = new Intent(MainActivity.this, NewOffertActivity.class);
                                startActivity(mIntent);
                                break;
                            case R.id.transactions:
                                Toast.makeText(MainActivity.this, "Transactions", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.account_settings:
                                mIntent = new Intent(MainActivity.this, AccountConfigActivity.class);
                                startActivity(mIntent);
                                break;
                            case R.id.change_password:
                                mIntent = new Intent(MainActivity.this, PasswordChangeActivity.class);
                                startActivityForResult(mIntent, REQUEST_CODE_CHANGE_PASS);
                                break;
                            case R.id.offerts:
                                mIntent = new Intent(MainActivity.this, UserOffertsActivity.class);
                                startActivity(mIntent);
                                break;
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });

        mSearchView = (SearchView) findViewById(R.id.main_search_bar);

        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.setIconified(false);
            }
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra(SearchActivity.FILTER_NAME, query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.products_latest_recycler_view);
        mProductsAdapter = new ProductsAdapter(mProductList);
        mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mProductsAdapter);

        LoginActivity.logRememberedUser(MainActivity.this);
        loadProductData();

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

        mCategoryButton = (ButtonFlat) findViewById(R.id.main_product_category_button);
        mCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoryChoiceActivity.class);
                intent.putExtra(SearchActivity.FILTER_NAME, mSearchView.getQuery().toString());
                startActivity(intent);
            }
        });

        mLocButton = (ButtonFlat) findViewById(R.id.main_product_loc_button);
        mLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LocalisationChoiceActivity.class);
                startActivityForResult(intent, REQUEST_CODE_NEW_LOC);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getIntent().getIntExtra("refresher", -1) == 1) {
            finish();
        }
        mSearchView.clearFocus();
        //loadProductData();
        mProductsAdapter.notifyDataSetChanged();
        changeDrawerData(UserTransfer.mLoggedUser);
    }

    @Override
    public void onRestart() {
        super.onRestart();

        if (getIntent().getIntExtra("refresher", -1) == 1) {
            finish();
        }

        mSearchView.clearFocus();
        //loadProductData();
        mProductsAdapter.notifyDataSetChanged();
        //mRecyclerView.setAdapter(mProductsAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_NEW_LOC) {
            if (data == null) {
                return;
            }
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            intent.putExtra(SearchActivity.FILTER_LOC, data.getParcelableExtra(CitiesAdapter.CITY_CHOSEN));
            intent.putExtra(SearchActivity.FILTER_NAME, mSearchView.getQuery().toString());
            startActivity(intent);
        }
        else if(requestCode == REQUEST_CODE_LOGIN) {
            changeDrawerData(UserTransfer.mLoggedUser);
        }
        else if(requestCode == REQUEST_CODE_CHANGE_PASS) {
        }
    }

    public void initProductsData() {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        mProductList.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL+"products/latest",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            Log.d("Response", String.valueOf(response.length()));
                            for(int i=0;i<response.length();i++){
                                mProductList.add(new Product(response.getJSONObject(i)));
                                mProductsAdapter.notifyDataSetChanged();
                            }
                            mRecyclerView.setAdapter(mProductsAdapter);
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
                                MainActivity.this,
                                "Error while getting products data, " + URL+"products/latest",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }


    public void loadProductData() {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                API.productData(pageToLoad,PAGE_SIZE),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            if (response.length() > 0) {
                                for (int i = 0; i < response.length(); i++) {
                                    mProductList.add(new Product(response.getJSONObject(i)));
                                    mProductsAdapter.notifyDataSetChanged();
                                }
                                pageToLoad++;
                            }
                            //mRecyclerView.setAdapter(mProductsAdapter);
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
                                MainActivity.this,
                                "Error while getting products data, ",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    public void getLoggedUser(String id) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                "http://192.168.0.17:8080/users/find/"+id,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            UserTransfer.mLoggedUser = new User(response);
                            changeDrawerData(UserTransfer.mLoggedUser);
                            Toast.makeText(MainActivity.this, UserTransfer.mLoggedUser.getName() + " logged", Toast.LENGTH_SHORT).show();
                        } catch(Exception e) {
                            Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        //Log.d("Response error",error.getMessage());
                        Toast.makeText(
                                MainActivity.this,
                                "Login error",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }


    public static void changeDrawerData(User user) {

        if (user != null) {
            mLogoutTextView.setVisibility(View.GONE);
            mLoginAvatar.setVisibility(View.VISIBLE);
            mLoginTextView.setVisibility(View.VISIBLE);

            byte[] avatarContent = UserTransfer.mLoggedUser.getAvatarImage().getContent();
            Bitmap bmp = BitmapFactory.decodeByteArray(avatarContent, 0, avatarContent.length);
            mLoginAvatar.setImageBitmap(bmp);
            mLogo.setVisibility(View.GONE);
            mLoginTextView.setText("Witaj, " + user.getName());
            mNavigationView.getMenu().setGroupVisible(R.id.user_not_logged, false);
            mNavigationView.getMenu().setGroupVisible(R.id.group_home, true);
            mNavigationView.getMenu().setGroupVisible(R.id.group_offers, true);
            mNavigationView.getMenu().setGroupVisible(R.id.group_account, true);
        } else {
            mLogoutTextView.setVisibility(View.VISIBLE);
            mLoginAvatar.setVisibility(View.GONE);
            mLoginTextView.setVisibility(View.GONE);
            mLogo.setVisibility(View.VISIBLE);
            mNavigationView.getMenu().setGroupVisible(R.id.user_not_logged, true);
            mNavigationView.getMenu().setGroupVisible(R.id.group_home, false);
            mNavigationView.getMenu().setGroupVisible(R.id.group_offers, false);
            mNavigationView.getMenu().setGroupVisible(R.id.group_account, false);

        }
    }

}
