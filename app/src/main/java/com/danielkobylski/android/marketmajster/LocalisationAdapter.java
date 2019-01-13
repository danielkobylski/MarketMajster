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
public class LocalisationAdapter extends RecyclerView.Adapter<LocalisationAdapter.LocalisationViewHolder>{

    private static final String LOC_CHOSEN = "com.ciastkaipiwo.android.scrummajster.loc_chosen";
    private List<Voivodeship> mVoivodeshipList;
    public LocalisationAdapter(List<Voivodeship> voivodeshipList) {
        this.mVoivodeshipList = voivodeshipList;
    }

    @Override
    public LocalisationAdapter.LocalisationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_list_item, parent, false);

        return new LocalisationAdapter.LocalisationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LocalisationAdapter.LocalisationViewHolder holder, int position) {

        holder.text.setText(mVoivodeshipList.get(position).getName());
        holder.position = position;

    }

    @Override
    public int getItemCount() {
        return mVoivodeshipList.size();
    }

    public class LocalisationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView text;
        public int position;

        public LocalisationViewHolder(final View view) {

            super(view);
            view.setOnClickListener(this);
            text = (TextView) view.findViewById(R.id.category);

        }

        @Override
        public void onClick(View v) {
            Voivodeship voivodeship = mVoivodeshipList.get(position);
            if (voivodeship.getId()==-1) {
                Intent data = new Intent();
                data.putExtra(CitiesAdapter.CITY_CHOSEN, new City((long)-1, voivodeship.getId(),voivodeship.getName()));
                ((Activity) v.getContext()).setResult(RESULT_OK,data);
                ((Activity) v.getContext()).finish();
            }
            else {
                Intent intent = new Intent(v.getContext(), CityChoiceActivity.class);
                intent.putExtra(CityChoiceActivity.CITY_NAME, mVoivodeshipList.get(position));
                //v.getContext().startActivity(intent);
                ((Activity) v.getContext()).startActivityForResult(intent, SearchActivity.REQUEST_CODE_NEW_CITY);
            }
        }

    }

}
