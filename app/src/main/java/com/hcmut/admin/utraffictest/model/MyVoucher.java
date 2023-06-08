package com.hcmut.admin.utraffictest.model;

import com.google.gson.annotations.SerializedName;

public class MyVoucher {
    @SerializedName("id")
    private int productId;
    @SerializedName("product_name")
    private String productName;
    @SerializedName("order_number")
    private String orderNumber;
    @SerializedName("order_date")
    private String orderDate;
    @SerializedName("price")
    private double productPrice;
    @SerializedName("status")
    private String orderDateStatus;
    @SerializedName("name")
    private String userName;

    public MyVoucher(int productId,String productName,String orderNumber,String orderDate,double productPrice,String orderDateStatus,String userName){
        this.productId = productId;
        this.productName = productName;
        this.orderNumber= orderNumber;
        this.orderDate = orderDate;
        this.productPrice = productPrice;
        this.orderDateStatus = orderDateStatus;
        this.userName = userName;
    }

    public int getProductId() {
        return productId;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getOrderDate() {
        return orderDate;
    }


    public String getProductName() {
        return productName;
    }

    public String getOrderDateStatus() {
        return orderDateStatus;
    }

    public String getUserName() {
        return userName;
    }
}
