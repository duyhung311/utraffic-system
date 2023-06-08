package com.hcmut.admin.utraffictest.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Voucher implements Parcelable{
    @SerializedName("id")
    private int voucherId;
    @SerializedName("voucherName")
    private String voucherName;
    @SerializedName("value")
    private double voucherValue;
    @SerializedName("quantity")
    private int voucherQuantity;
    @SerializedName("supplier")
    private String voucherSupplier;
    @SerializedName("category")
    private String voucherCategory;
    @SerializedName("image")
    private String voucherImage;
    @SerializedName("isFavourite")
    private int isFavourite;
    @SerializedName("isInUser")
    private int isInUser;
    private Voucher mInfo;



    public Voucher(int id, String voucherName, double voucherValue, int voucherQuantity, String voucherSupplier, String voucherCategory,String voucherImage) {
        this.voucherId=id;
        this.voucherName = voucherName;
        this.voucherValue = voucherValue;
        this.voucherQuantity = voucherQuantity;
        this.voucherSupplier = voucherSupplier;
        this.voucherCategory = voucherCategory;
        this.voucherImage = voucherImage;
    }

    public Voucher() {

    }

    public int getVoucherId() {
        return voucherId;
    }

    public String getVoucherName() {
        return voucherName;
    }

    public double getVoucherValue() {
        return voucherValue;
    }

    public int getVoucherQuantity() {
        return voucherQuantity;
    }

    public String getVoucherSupplier() {
        return voucherSupplier;
    }

    public String getVoucherCategory() {
        return voucherCategory;
    }

    public String getVoucherImage() {
        return voucherImage;
    }


    public int isFavourite() {
        return isFavourite;
    }

    public int isInCart() {
        return isInUser;
    }

    public void setIsFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite ? 1 : 0;
    }

    public void setIsInCart(boolean isInCart) {
        this.isInUser = isInCart ? 1 : 0;
    }

    // Write the values to be saved to the `Parcel`.
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(voucherId);
        out.writeString(voucherName);
        out.writeDouble(voucherValue);
        out.writeInt(voucherQuantity);
        out.writeString(voucherSupplier);
        out.writeString(voucherCategory);
        out.writeString(voucherImage);
        out.writeInt(isFavourite);
        out.writeInt(isInUser);
        out.writeParcelable(mInfo, flags);
    }

    // Retrieve the values written into the `Parcel`.
    private Voucher(Parcel in) {
        voucherId = in.readInt();
        voucherName = in.readString();
        voucherValue = in.readDouble();
        voucherQuantity = in.readInt();
        voucherSupplier = in.readString();
        voucherCategory = in.readString();
        voucherImage = in.readString();
        isFavourite = in.readInt();
        isInUser = in.readInt();
        mInfo = in.readParcelable(Voucher.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Create the Parcelable.Creator<Product> CREATOR` constant for our class;
    public static final Parcelable.Creator<Voucher> CREATOR
            = new Parcelable.Creator<Voucher>() {

        // This simply calls our new constructor and
        // passes along `Parcel`, and then returns the new object!
        @Override
        public Voucher createFromParcel(Parcel in) {
            return new Voucher(in);
        }

        @Override
        public Voucher[] newArray(int size) {
            return new Voucher[size];
        }
    };
}
