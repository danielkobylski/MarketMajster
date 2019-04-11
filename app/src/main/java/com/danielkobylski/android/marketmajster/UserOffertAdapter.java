package com.danielkobylski.android.marketmajster;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gc.materialdesign.views.ButtonFlat;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserOffertAdapter extends RecyclerView.Adapter<UserOffertAdapter.UserOffertViewHolder> {

    private List<Product> mProductList;

    public UserOffertAdapter(List<Product> productList) {
        this.mProductList = productList;
    }

    @Override
    public UserOffertAdapter.UserOffertViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_offerts_list_item, parent, false);

        return new UserOffertAdapter.UserOffertViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserOffertViewHolder holder, int position) {
        if (mProductList.get(position).getImageList().size() != 0) {
            byte[] bmpArr = mProductList.get(position).getImageList().get(0).getContent();
            Bitmap bmp = BitmapFactory.decodeByteArray(bmpArr, 0, bmpArr.length);
            holder.imageView.setImageBitmap(bmp);
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        holder.position = position;
        holder.titleTextView.setText(mProductList.get(position).getName());
        holder.categoryTextView.setText(mProductList.get(position).getCategory());
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

    public class UserOffertViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView titleTextView;
        public TextView categoryTextView;
        public ButtonFlat editButton;
        public ButtonFlat deleteButton;

        public int position;

        public UserOffertViewHolder(final View view) {

            super(view);

            imageView = (ImageView)view.findViewById(R.id.offert_item_preview);
            titleTextView = (TextView)view.findViewById(R.id.offert_name_text_view);
            categoryTextView = (TextView)view.findViewById(R.id.offert_category_text_view);
            editButton = (ButtonFlat)view.findViewById(R.id.edit_offert_button);
            deleteButton = (ButtonFlat)view.findViewById(R.id.delete_offert_button);

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), NewOffertActivity.class);
                    intent.putExtra(NewOffertActivity.EDIT_OFFERT, 1);
                    ProductTransfer.setProduct(mProductList.get(position));
                    ((Activity)v.getContext()).startActivityForResult(intent, UserOffertsActivity.REQUEST_CODE_EDIT_PRODUCT);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    removeUserProduct(mProductList.get(position).getId(), position);
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.DialogStyle);
                    builder.setMessage("Jesteś pewien?").setPositiveButton("Tak", dialogClickListener)
                            .setNegativeButton("Nie", dialogClickListener).show();

                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProductTransfer.setProduct(mProductList.get(position));
                    Intent intent = new Intent(v.getContext(), ProductActivity.class);
                    intent.putExtra("HideButtons", 1);
                    v.getContext().startActivity(intent);
                }
            });

        }
    }

    private void removeUserProduct(long productId, final int position) {
        RequestQueue requestQueue = Volley.newRequestQueue(BarterApp.getAppContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.deleteProduct(productId), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProductList.remove(position);
                notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + UserTransfer.getToken());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

}

