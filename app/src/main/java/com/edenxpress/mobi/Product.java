package com.edenxpress.mobi;

import android.graphics.drawable.ColorDrawable;

/**
 * Created by HP 15-P038 on Jan 7 17.
 */

public class Product {
    public String btnTitle;
    public String discount;
    public String formatedSpecialPrice;
    public String hasOptions;
    private String id;
    private ColorDrawable img_bitmap;
    public String img_url;
    public String inStock;
    private String linksPurchasedSeparately;
    public String max;
    public String min;
    public String model;
    public String price;
    public String priceView;
    private String productId;
    private String productName;
    public String range;
    public String rating;
    private String sellerString = "";
    public String shortDescription;
    private String typeId;
    public String wishlist;

    public String getSellerString() {
        return this.sellerString;
    }

    public void setSellerString(String sellerString) {
        this.sellerString = sellerString;
    }

    public String getLinksPurchasedSeparately() {
        return this.linksPurchasedSeparately;
    }

    public void setLinksPurchasedSeparately(String linksPurchasedSeparately) {
        this.linksPurchasedSeparately = linksPurchasedSeparately;
    }

    public ColorDrawable getImg_bitmap() {
        return this.img_bitmap;
    }

    public void setImg_bitmap(ColorDrawable img_bitmap) {
        this.img_bitmap = img_bitmap;
    }

    public Product(String p_productName, String p_img_url, String p_price, String p_shortDescription, String p_rating, String p_btnTitle, String p_wishlist, String p_discount, String p_min, String p_max, String p_priceView, String p_formatedSpecialPrice, String p_range, String p_model, String p_productId, String p_hasOptions, String p_isStock) {
        this.productName = p_productName;
        this.img_url = p_img_url;
        this.price = p_price;
        this.shortDescription = p_shortDescription;
        this.rating = p_rating;
        this.btnTitle = p_btnTitle;
        this.wishlist = p_wishlist;
        this.discount = p_discount;
        this.min = p_min;
        this.max = p_max;
        this.priceView = p_priceView;
        this.formatedSpecialPrice = p_formatedSpecialPrice;
        this.range = p_range;
        this.model = p_model;
        this.productId = p_productId;
        this.hasOptions = p_hasOptions;
        this.inStock = p_isStock;
    }

    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getTypeId() {
        return this.typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getProductId() {
        return this.productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
