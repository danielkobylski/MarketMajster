package com.danielkobylski.android.marketmajster;

public class API {

    public static final String URL = "http://s12.mydevil.net:8080/barter/";
    public static final String URL_LOCAL = "http://192.168.0.17:8080/";
    public static final String TOKEN = URL+"api/auth/signin";
    public static final String CURRENT_USER = URL+"users/current";
    public static final String CHANGE_PASSWORD = URL+"users/update/password";
    public static final String CHANGE_SETTINGS = URL+"users/update";
    public static final String AVATAR = "http://192.168.0.17:8080/"+"image/upload/string";
    public static final String SESSION_CLEAR = URL_LOCAL+"clear/session/img";
    public static final String NEW_OFFERT_IMAGES = URL_LOCAL + "auth/products/image/upload/string";
    public static final String NEW_OFFERT = URL_LOCAL+"auth/products/add";

    public static final String VOIVODESHIPS = URL+"voivo/all";
    public static final String CITIES = URL+"city/all";
    public static String productData(int pageToLoad,int pageSize) {
        return URL_LOCAL+"products/latest/all?page=" + pageToLoad + "&size=" + pageSize;
    }

    public static String userData(long userId) {
        return URL + "users/product?ownerId=" + userId;
    }

    public static String userProducts(long userId) {
        return URL_LOCAL + "auth/products/owner?ownerId="+userId+"&active=true";
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

}
