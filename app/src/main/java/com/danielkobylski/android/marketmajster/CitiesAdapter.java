package com.danielkobylski.android.marketmajster;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import static android.app.Activity.RESULT_OK;
public class CitiesAdapter extends RecyclerView.Adapter<CitiesAdapter.CitiesViewHolder>{

    public static final String CITY_CHOSEN = "com.ciastkaipiwo.android.scrummajster.city_chosen";
    private List<City> mCityList;
    public CitiesAdapter(List<City> cityList) {
        this.mCityList = cityList;
    }

    @Override
    public CitiesAdapter.CitiesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_list_item, parent, false);

        return new CitiesAdapter.CitiesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CitiesAdapter.CitiesViewHolder holder, int position) {

        holder.text.setText(mCityList.get(position).getName());
        holder.position = position;

    }

    @Override
    public int getItemCount() {
        return mCityList.size();
    }

    public class CitiesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView text;
        public int position;

        public CitiesViewHolder(final View view) {

            super(view);
            view.setOnClickListener(this);

            text = (TextView) view.findViewById(R.id.category);

        }

        @Override
        public void onClick(View v) {
            {
                Intent data = new Intent();
                data.putExtra(CITY_CHOSEN, mCityList.get(position));
                ((Activity)v.getContext()).setResult(RESULT_OK, data);
                ((Activity)v.getContext()).finish();
            }
        }
    }

}
