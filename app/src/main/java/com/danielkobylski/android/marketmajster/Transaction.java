package com.danielkobylski.android.marketmajster;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Transaction {

    private long mId;
    private long mOwnerId;
    private long mClientId;
    private long mOfferId;
    private int mStatus;
    private boolean mOwnerAccept;
    private boolean mClientAccept;
    private String mStatusName;
    private String mClientLogin;
    private String mOwnerLogin;
    private String mOfferName;
    private String mMessageClient;
    private List<TransactionState> mTransactionStates;
    private List<TransactionState> mTransactionStateMaxStep;

    Transaction(JSONObject transaction) {
        try {
            mId = transaction.getLong("id");
            mOwnerId = transaction.getLong("ownerId");
            mClientId = transaction.getLong("clientId");
            mOfferId = transaction.getLong("offerId");
            mStatus = transaction.getInt("status");
            mOwnerAccept = (transaction.getString("ownerAccept").equals("null")) ? false : transaction.getBoolean("ownerAccept");
            mClientAccept =(transaction.getString("clientAccept").equals("null")) ? false : transaction.getBoolean("clientAccept");
            mStatusName = transaction.getString("statusName");
            mClientLogin = transaction.getString("clientLogin");
            mOwnerLogin = transaction.getString("ownerLogin");
            mOfferName = transaction.getString("offerName");
            mMessageClient = transaction.getString("messageClient");
            mTransactionStates = new ArrayList<>();
            mTransactionStateMaxStep = new ArrayList<>();
            JSONArray transactionStates = transaction.getJSONArray("transactionState");
            for (int i = 0; i < transactionStates.length(); i++) {
                mTransactionStates.add(new TransactionState(transactionStates.getJSONObject(i)));
            }
            transactionStates = transaction.getJSONArray("transactionStateMaxStep");
            for (int i = 0; i < transactionStates.length(); i++) {
                mTransactionStateMaxStep.add(new TransactionState(transactionStates.getJSONObject(i)));
            }

        }
        catch(JSONException e) {
            e.printStackTrace();
        }
    }
    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getOwnerId() {
        return mOwnerId;
    }

    public void setOwnerId(long ownerId) {
        mOwnerId = ownerId;
    }

    public long getClientId() {
        return mClientId;
    }

    public void setClientId(long clientId) {
        mClientId = clientId;
    }

    public long getOfferId() {
        return mOfferId;
    }

    public void setOfferId(long offerId) {
        mOfferId = offerId;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public boolean isOwnerAccept() {
        return mOwnerAccept;
    }

    public void setOwnerAccept(boolean ownerAccept) {
        mOwnerAccept = ownerAccept;
    }

    public boolean isClientAccept() {
        return mClientAccept;
    }

    public void setClientAccept(boolean clientAccept) {
        mClientAccept = clientAccept;
    }

    public String getStatusName() {
        return mStatusName;
    }

    public void setStatusName(String statusName) {
        mStatusName = statusName;
    }

    public String getClientLogin() {
        return mClientLogin;
    }

    public void setClientLogin(String clientLogin) {
        mClientLogin = clientLogin;
    }

    public String getOwnerLogin() {
        return mOwnerLogin;
    }

    public void setOwnerLogin(String ownerLogin) {
        mOwnerLogin = ownerLogin;
    }

    public String getOfferName() {
        return mOfferName;
    }

    public void setOfferName(String offerName) {
        mOfferName = offerName;
    }

    public String getMessageClient() {
        return mMessageClient;
    }

    public void setMessageClient(String messageClient) {
        mMessageClient = messageClient;
    }

    public List<TransactionState> getTransactionStates() {
        return mTransactionStates;
    }

    public void setTransactionStates(List<TransactionState> transactionStates) {
        mTransactionStates = transactionStates;
    }

    public List<TransactionState> getTransactionStateMaxStep() {
        return mTransactionStateMaxStep;
    }

    public void setTransactionStateMaxStep(List<TransactionState> transactionStateMaxStep) {
        mTransactionStateMaxStep = transactionStateMaxStep;
    }


}
