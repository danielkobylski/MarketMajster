package com.danielkobylski.android.marketmajster;

public class TransactionTransfer {

    public static Transaction mTransaction;

    public static Transaction getTransaction() {
        return mTransaction;
    }

    public static void setTransaction(Transaction transaction) {
        TransactionTransfer.mTransaction = transaction;
    }

}
