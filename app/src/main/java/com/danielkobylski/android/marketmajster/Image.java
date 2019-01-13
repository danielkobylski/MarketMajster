package com.danielkobylski.android.marketmajster;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Image implements Parcelable {

    private Long mId;
    private Long mOffertId;
    private byte[] mContent;

    public Image(Long id, Long offertId, byte[] content) {
        mId = id;
        mOffertId = offertId;
        mContent = content;
    }

    public Image(JSONObject image) {

        try {
            mId = Long.valueOf(image.getString("id"));
            mOffertId = Long.valueOf(image.getString("offerId"));
            mContent = Base64.decode(image.getString("image"), Base64.DEFAULT);
        } catch(JSONException e) {
            e.printStackTrace();
        }

    }

    public Long getId() {
        return mId;
    }

    public Long getOffertId() {
        return mOffertId;
    }

    public byte[] getContent() {
        return mContent;
    }

    protected Image(Parcel in) {
        mId = in.readByte() == 0x00 ? null : in.readLong();
        mOffertId = in.readByte() == 0x00 ? null : in.readLong();
        mContent = new byte[in.readInt()];
        in.readByteArray(mContent);
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
        if (mOffertId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mOffertId);
        }
        if (mContent == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeInt(mContent.length);
            dest.writeByteArray(mContent);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}

/*
public class Image {

    private Long mId;
    private Long mOffertId;
    private byte[] mContent;

    public Image(Long id, Long offertId, byte[] content) {
        mId = id;
        mOffertId = offertId;
        mContent = content;
    }

    public Image(JSONObject image) {

        try {
            mId = Long.valueOf(image.getString("id"));
            mOffertId = Long.valueOf(image.getString("offerId"));
            mContent = Base64.decode(image.getString("image"), Base64.DEFAULT);
        } catch(JSONException e) {
            e.printStackTrace();
        }

    }

    public Long getId() {
        return mId;
    }

    public Long getOffertId() {
        return mOffertId;
    }

    public byte[] getContent() {
        return mContent;
    }
}
*/
