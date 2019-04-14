package com.danielkobylski.android.marketmajster;

public class TransactionTransfer {

    public static Transaction mTransaction;
    public static String mUserMsg;
    public static String mOtherMsg;

    public static Transaction getTransaction() {
        return mTransaction;
    }

    public static void setTransaction(Transaction transaction) {
        TransactionTransfer.mTransaction = transaction;
    }

    public static String getUserMsg() {
        return mUserMsg;
    }

    public static void setUserMsg(String userMsg) {
        TransactionTransfer.mUserMsg = userMsg;
    }

    public static String getOtherMsg() {
        return mOtherMsg;
    }

    public static void setOtherMsg(String otherMsg) {
        TransactionTransfer.mOtherMsg = otherMsg;
    }
}
