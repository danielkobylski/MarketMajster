package com.danielkobylski.android.marketmajster;

import org.json.JSONException;
import org.json.JSONObject;

public class TransactionState {

    private long mId;
    private long mOfferId;
    private long mTransactionId;
    private boolean mSellerAccept;
    private boolean mBuyerAccept;
    private int mSideFlag;
    private int mStep;
    private String mMessageClient;
    private String mMessageOwner;
    private String mDate;
    private String mOfferName;
    private String mCategoryName;
    private String mCityName;
    private String mSellerAcceptStatus;
    private String mBuyerAcceptStatus;
    private boolean mDelete;
    private boolean mOfferActive;

    TransactionState(JSONObject transactionState) {
        try {
            //mId = transactionState.getLong("id");
            mOfferId = transactionState.getLong("offerId");
            mTransactionId = transactionState.getLong("transactionId");
            mSellerAccept = transactionState.getBoolean("sellerAccept");
            mBuyerAccept = transactionState.getBoolean("buyerAccept");
            mSideFlag = transactionState.getInt("sideFlag");
            mStep = transactionState.getInt("step");
            mMessageClient = transactionState.getString("messageClient");
            mMessageOwner = transactionState.getString("messageOwner");
            mDate = transactionState.getString("date");
            mOfferName = transactionState.getString("offerName");
            mCategoryName = transactionState.getString("categoryName");
            mCityName = transactionState.getString("cityName");
            mSellerAcceptStatus = transactionState.getString("sellerAcceptStatus");
            mBuyerAcceptStatus = transactionState.getString("buyerAcceptStatus");
            mDelete = transactionState.getBoolean("delete");
            mOfferActive = transactionState.getBoolean("offerActive");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getOfferId() {
        return mOfferId;
    }

    public void setOfferId(long offerId) {
        mOfferId = offerId;
    }

    public long getTransactionId() {
        return mTransactionId;
    }

    public void setTransactionId(long transactionId) {
        mTransactionId = transactionId;
    }

    public boolean isSellerAccept() {
        return mSellerAccept;
    }

    public void setSellerAccept(boolean sellerAccept) {
        mSellerAccept = sellerAccept;
    }

    public boolean isBuyerAccept() {
        return mBuyerAccept;
    }

    public void setBuyerAccept(boolean buyerAccept) {
        mBuyerAccept = buyerAccept;
    }

    public int getSideFlag() {
        return mSideFlag;
    }

    public void setSideFlag(int sideFlag) {
        mSideFlag = sideFlag;
    }

    public int getStep() {
        return mStep;
    }

    public void setStep(int step) {
        mStep = step;
    }

    public String getMessageClient() {
        return mMessageClient;
    }

    public void setMessageClient(String messageClient) {
        mMessageClient = messageClient;
    }

    public String getMessageOwner() {
        return mMessageOwner;
    }

    public void setMessageOwner(String messageOwner) {
        mMessageOwner = messageOwner;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getOfferName() {
        return mOfferName;
    }

    public void setOfferName(String offerName) {
        mOfferName = offerName;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public void setCategoryName(String categoryName) {
        mCategoryName = categoryName;
    }

    public String getCityName() {
        return mCityName;
    }

    public void setCityName(String cityName) {
        mCityName = cityName;
    }

    public String getSellerAcceptStatus() {
        return mSellerAcceptStatus;
    }

    public void setSellerAcceptStatus(String sellerAcceptStatus) {
        mSellerAcceptStatus = sellerAcceptStatus;
    }

    public String getBuyerAcceptStatus() {
        return mBuyerAcceptStatus;
    }

    public void setBuyerAcceptStatus(String buyerAcceptStatus) {
        mBuyerAcceptStatus = buyerAcceptStatus;
    }

    public boolean isDelete() {
        return mDelete;
    }

    public void setDelete(boolean delete) {
        mDelete = delete;
    }

    public boolean isOfferActive() {
        return mOfferActive;
    }

    public void setOfferActive(boolean offerActive) {
        mOfferActive = offerActive;
    }
}
