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
public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>{

    private static final String CATEGORY_CHOSEN = "com.ciastkaipiwo.android.scrummajster.category_chosen";
    private List<Category> mCatList;
    public CategoriesAdapter(List<Category> catList) {
        this.mCatList = catList;
    }

    @Override
    public CategoriesAdapter.CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_list_item, parent, false);

        return new CategoriesAdapter.CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CategoriesAdapter.CategoryViewHolder holder, int position) {

        holder.category.setText(mCatList.get(position).getName());
        holder.position = position;

    }


    @Override
    public int getItemCount() {
        return mCatList.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView category;
        public int position;

        public CategoryViewHolder(final View view) {

            super(view);
            view.setOnClickListener(this);

            category = (TextView) view.findViewById(R.id.category);

        }

        @Override
        public void onClick(View v) {
            if ( ((Activity)v.getContext()).getIntent().getIntExtra("Caller", -1 ) == 1) {
                Intent data = new Intent();
                data.putExtra(CATEGORY_CHOSEN, mCatList.get(position));
                ((Activity)v.getContext()).setResult(RESULT_OK, data);
                ((Activity)v.getContext()).finish();
            }

            else {
                Intent intent = new Intent(v.getContext(), SearchActivity.class);
                intent.putExtra(SearchActivity.FILTER_NAME, ((Activity)v.getContext()).getIntent().getStringExtra(SearchActivity.FILTER_NAME));
                intent.putExtra(SearchActivity.FILTER_CATEGORY, mCatList.get(position));
                v.getContext().startActivity(intent);
            }
        }
    }

}
