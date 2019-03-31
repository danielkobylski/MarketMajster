package com.danielkobylski.android.marketmajster;

public class API {

    public static final String URL = "http://s12.mydevil.net:8080/barter/";
    public static final String TOKEN = URL+"api/auth/signin";
    public static final String CURRENT_USER = URL+"users/current";
    public static final String CHANGE_PASSWORD = URL+"users/update/password";
    public static final String CHANGE_SETTINGS = URL+"users/update";
    public static final String AVATAR = "http://192.168.0.17:8080/"+"image/upload/string";

    public static String productData(int pageToLoad,int pageSize) {
        return URL+"products/latest/all?page=" + pageToLoad + "&size=" + pageSize;
    }

    public static String userData(long userId) {
        return URL + "users/product?ownerId=" + userId;
    }

    public static String userProducts(long userId) {
        return URL + "auth/products/owner?ownerId="+userId+"&active=true&page=0&size=1";
    }

    public static String addToFavs(Long userId, Long productId) {
        return URL + "users/add/fav?userId=" + userId + "&productId=" + productId;
    }

    public static String changeAvatar(Long userId) {
        return URL + "image/upload?id="+userId;
    }


}
