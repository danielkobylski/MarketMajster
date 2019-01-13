package com.danielkobylski.android.marketmajster;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>{

    private List<Product> mProductList;

    public ProductsAdapter(List<Product> productList) {
        this.mProductList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_list_item, parent, false);

        return new ProductViewHolder(itemView);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {

        holder.productTitle.setText(mProductList.get(position).getName());
        //holder.productCategory.setText(mProductList.get(position).getCategory());

        Log.d("image", mProductList.get(position).getName());
        if (mProductList.get(position).getImageList().size() != 0) {
            byte[] bmpArr = mProductList.get(position).getImageList().get(0).getContent();
            Bitmap bmp = BitmapFactory.decodeByteArray(bmpArr, 0, bmpArr.length);
            Log.d("Somethings", "asdf");
            holder.productImage.setImageBitmap(bmp);
            holder.productImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return mProductList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private static final int REQUEST_CODE_EDIT_PROJECT = 2;
        public TextView productTitle;
        //public TextView productCategory;
        public ImageView productImage;

        public int position;

        public ProductViewHolder(final View view) {

            super(view);
            view.setOnClickListener(this);

            productTitle = (TextView) view.findViewById(R.id.product_title);
            //productCategory = (TextView) view.findViewById(R.id.product_category);
            productImage = (ImageView) view.findViewById(R.id.product_picture);

        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), ProductActivity.class);
            //intent.putExtra("asd", mProductList.get(position));
            ProductTransfer.setProduct(mProductList.get(position));
            v.getContext().startActivity(intent);
        }
    }

}
