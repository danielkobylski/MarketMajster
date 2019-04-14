package com.danielkobylski.android.marketmajster;

public class API {

    public static final String URL = "http://s12.mydevil.net:8080/barter/";
    public static final String URL_LOCAL = "http://192.168.0.17:8080/";
    public static final String TOKEN = URL+"api/auth/signin";
    public static final String CURRENT_USER = URL_LOCAL+"users/current";
    public static final String CHANGE_PASSWORD = URL+"users/update/password";
    public static final String CHANGE_SETTINGS = URL+"users/update";
    public static final String AVATAR = "http://192.168.0.17:8080/"+"image/upload/string";
    public static final String SESSION_CLEAR = URL_LOCAL+"clear/session/img";
    public static final String NEW_OFFERT_IMAGES = URL_LOCAL + "auth/products/image/upload/string";
    public static final String NEW_OFFERT = URL_LOCAL+"auth/products/add";
    public static final String SUBMIT_PROPOSAL = URL_LOCAL + "transaction/save";
    public static final String PRODUCT_LIST = URL_LOCAL + "products/list";
    public static final String VOIVODESHIPS = URL+"voivo/all";
    public static final String CITIES = URL+"city/all";
    public static final String TRANSACTION_REPLY = URL_LOCAL + "send/message";
    public static final String TRANSACTION_SESSION_CLEAR = URL_LOCAL + "transaction/clear/session";
    public static final String TRANSACTION_SAVE_ANOTHER = URL_LOCAL + "transaction/save/offer/another";
    public static String productData(int pageToLoad,int pageSize) {
        return URL_LOCAL+"products/latest/all?page=" + pageToLoad + "&size=" + pageSize;
    }

    public static String userData(long userId) {
        return URL_LOCAL + "users/product?ownerId=" + userId;
    }

    public static String userProducts(long userId) {
        return URL_LOCAL + "/auth/products/owner/list?ownerId="+ userId +"&active=true";
    }

    public static String otherProducts(long userId, long productId) {
        return URL_LOCAL + "products/owner/another/one?ownerId="+userId+"&productId="+productId;
    }

    public static String addToFavs(Long userId, Long productId) {
        return URL_LOCAL + "users/add/fav?userId=" + userId + "&productId=" + productId;
    }

    public static String removeFav(Long userId, Long productId) {
        return URL_LOCAL + "users/delete/fav?productId=" + productId +"&userId="+userId;
    }

    public static String changeAvatar(Long userId) {
        return URL + "image/upload?id="+userId;
    }

    public static String deleteProduct(Long productId) {
        return URL_LOCAL + "auth/products/delete?id="+productId;
    }

    public static String editProduct(Long productId) {
        return URL_LOCAL + "auth/products/" + productId;
    }

    public static String newTransactions(Long userId) {
        return URL_LOCAL + "transaction/new/proposal?ownerId=" + userId;
    }

    public static String sentTransactions(Long userId) {
        return URL_LOCAL + "transaction/send/proposal?userId=" + userId;
    }

    public static String activeNewTransactions(Long userId) {
        return URL_LOCAL + "transaction/active/wait/proposal?userId=" + userId;
    }

    public static String activeSentTransactions(Long userId) {
        return URL_LOCAL + "transaction/active/sent/owner?ownerId=" + userId + "&active=true";
    }

    public static String transactionExists(Long userId, Long offerId) {
        return URL_LOCAL + "transaction/exist?userId=" + userId + "&offerId=" + offerId;
    }

    public static String getProduct(Long productId) {
        return URL_LOCAL + "products/" + productId;
    }

    public static String cancelTransaction(Long transactionId) {
        return URL_LOCAL + "transaction/client/reject?id=" + transactionId;
    }

    public static String productsList(String productList) {
        return URL_LOCAL + "products/list?ids=" + productList;
    }

    public static String rejectActiveTransaction(long offerId) {
        return URL_LOCAL + "transaction/reject?id="+offerId;
    }

    public static String acceptTransaction(long transactionId, String userSide) {
        //additionaly pass JSON with transactionState
        return URL_LOCAL + "transaction/success/end?id=" + transactionId + "&userSide=" + userSide;
    }

    public static String userItemAccept(long offerId, String userSide) {
        return URL_LOCAL + "accept/owner/side/offer?offerId="+offerId+"&side=" + userSide;
    }

    public static String transactionOtherProducts(long ownerId) {
        return URL_LOCAL + "auth/products/owner/another?ownerId=" + ownerId;
    }

    public static String transactionNewOfferList(long transactionId) {
        return URL_LOCAL + "transaction/offer/list?transactionId="+transactionId;
    }

    public static String transactionActiveOfferList(long transactionId) {
        return URL_LOCAL + "transaction/active/offer/list?transactionId="+transactionId;
    }

    public static String removeTransactionItem(long offerId) {
        return URL_LOCAL + "transaction/delete/offer?offerId=" + offerId;
    }

}
