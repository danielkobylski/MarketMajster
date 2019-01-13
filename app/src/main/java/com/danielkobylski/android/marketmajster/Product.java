package com.danielkobylski.android.marketmajster;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Product implements Parcelable{
    private Long mId;
    private String mName;
    private String mCategory;
    private List<Image> mImageList;
    private String mCreationDate;
    private String mDescription;
    private String mCity;
    private Long mOwnerId;

    Product(Long id, String name, String category, List<Image> imageList, String creationDate, String description, String city, Long ownerId) {
        mId = id;
        mName = name;
        mCategory = category;
        mImageList = imageList;
        mCreationDate = creationDate;
        mDescription = description;
        mCity = city;
        mOwnerId = ownerId;
    }

    Product(JSONObject product) {
        try {
            mId = Long.valueOf(product.getString("id"));
            mName = product.getString("name");
            mCategory =  product.getString("categoryName");
            mImageList = new ArrayList<>();
            mCreationDate = product.getString("creationDate");
            mDescription = product.getString("description");
            mCity = product.getString("cityName");
            mOwnerId = Long.valueOf(product.getString("ownerId"));
            JSONArray images = product.getJSONArray("offerImagesList");
            if (images.length() != 0) {
                for (int i = 0; i < images.length(); i++) {
                    mImageList.add(new Image(images.getJSONObject(i)));
                }
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }

    }

    public Long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getCategory() {
        return mCategory;
    }

    public List<Image> getImageList() { return mImageList; }

    public void setId(Long id) {
        mId = id;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public String getCreationDate() {
        return mCreationDate;
    }

    public void setCreationDate(String creationDate) {
        mCreationDate = creationDate;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public Long getOwnerId() {
        return mOwnerId;
    }

    public void setOwnerId(Long ownerId) {
        mOwnerId = ownerId;
    }

    protected Product(Parcel in) {
        mId = in.readByte() == 0x00 ? null : in.readLong();
        mName = in.readString();
        mCategory = in.readString();
        mCreationDate = in.readString();
        mDescription = in.readString();
        mCity = in.readString();
        mOwnerId = in.readLong();
        if (in.readByte() == 0x01) {
            mImageList = new ArrayList<Image>();
            in.readList(mImageList, Image.class.getClassLoader());
        } else {
            mImageList = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mId);
        }
        dest.writeString(mName);
        dest.writeString(mCategory);
        dest.writeString(mCreationDate);
        dest.writeString(mDescription);
        dest.writeString(mCity);
        dest.writeLong(mOwnerId);
        if (mImageList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mImageList);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}

   /* protected Product(Parcel in) {
        mId = in.readLong();
        mName = in.readString();
        mCategory = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mName);
        dest.writeString(mCategory);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
*/