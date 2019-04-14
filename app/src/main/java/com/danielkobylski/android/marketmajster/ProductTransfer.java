package com.danielkobylski.android.marketmajster;

import com.danielkobylski.android.marketmajster.Product;

import java.util.List;

public class ProductTransfer {

    public static Product mProduct;
    public static Product mOffert;
    public static Product mProductPreview;
    public static List<Product> mOtherProducts;

    public static void setProduct (Product product) {
        mProduct = product;
    }

    public static Product getProduct () {
        return mProduct;
    }

    public static void setOffert (Product product) {
        mOffert = product;
    }

    public static Product getOffert () {
        return mOffert;
    }

    public static void setProductPreview (Product product) {
        mProductPreview = product;
    }

    public static Product getProductPreview () {
        return mProductPreview;
    }

    public static List<Product> getOtherProducts() {return mOtherProducts;}

    public static void setOtherProducts(List<Product> otherProducts) {
        mOtherProducts = otherProducts;
    }

}
