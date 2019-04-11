package com.danielkobylski.android.marketmajster;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFlat;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductProposalAdapter extends RecyclerView.Adapter<ProductProposalAdapter.ProductProposalViewHolder> {

    private List<Product> mProductList;

    public ProductProposalAdapter(List<Product> productList) {
        this.mProductList = productList;
    }

    @Override
    public ProductProposalAdapter.ProductProposalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_offert_list_item, parent, false);

        return new ProductProposalAdapter.ProductProposalViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductProposalViewHolder holder, int position) {
        if (mProductList.get(position).getImageList().size() != 0) {
            byte[] bmpArr = mProductList.get(position).getImageList().get(0).getContent();
            Bitmap bmp = BitmapFactory.decodeByteArray(bmpArr, 0, bmpArr.length);
            Log.d("Somethings", "asdf");
            holder.imageView.setImageBitmap(bmp);
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        holder.position = position;
        holder.textView.setText(mProductList.get(position).getName());
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
    public int getItemCount() {
        return mProductList.size();
    }

    public class ProductProposalViewHolder extends RecyclerView.ViewHolder {

        public CheckBox checkBox;
        public CircleImageView imageView;
        public TextView textView;
        public ButtonFlat button;

        public int position;

        public ProductProposalViewHolder(final View view) {

            super(view);

            checkBox = (CheckBox)view.findViewById(R.id.transaction_proposal_item_check_box);
            imageView = (CircleImageView)view.findViewById(R.id.transaction_proposal_item_preview);
            textView = (TextView)view.findViewById(R.id.transaction_proposal_item_name_text_view);
            button = (ButtonFlat)view.findViewById(R.id.transaction_proposal_view_product_button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProductTransfer.setProduct(mProductList.get(position));
                    Intent intent = new Intent(v.getContext(), ProductActivity.class);
                    intent.putExtra("HideButtons", 1);
                    v.getContext().startActivity(intent);
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.setChecked(!checkBox.isChecked());
                }
            });

        }
    }

}