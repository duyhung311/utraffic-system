package com.hcmut.admin.utrafficsystem.util;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.model.AndroidExt;
import com.hcmut.admin.utrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.utrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.GiftResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.GiftStateResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GiftUtil {
    public static AndroidExt androidExt=new AndroidExt();;
    public void addGift (final Context context,final Context myapp, final GoogleMap mMap){
        final BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.giftbox);

        RetrofitClient.getApiService().getAllGift(SharedPrefUtils.getUser(myapp).getAccessToken())
                .enqueue(new Callback<BaseResponse<List<GiftResponse>>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<List<GiftResponse>>> call, Response<BaseResponse<List<GiftResponse>>> response) {
                        if (response.body() != null) {
                            if (response.body().getData() != null) {
                                try {
                                    List<GiftResponse> giftResponse = response.body().getData();
                                    for (GiftResponse gift : giftResponse) {
                                        Marker melbourne = mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(gift.getLatitude(),gift.getLongitude()))
                                                .icon(bitmap));
                                        melbourne.setTag("GIFT-"+gift.getId());


                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                androidExt.showErrorDialog(context, "C?? l???i, vui l??ng th??ng b??o cho admin");
                            }
                        } else {
                            androidExt.showErrorDialog(context, "C?? l???i, vui l??ng th??ng b??o cho admin");
                        }
                    }
                    @Override
                    public void onFailure(Call<BaseResponse<List<GiftResponse>>> call, Throwable t) {
                        androidExt.showErrorDialog(context, "K???t n???i th???t b???i, vui l??ng ki???m tra l???i");                    }
                });

    }
    public void checkGift(final Context context, final Context myapp, final Marker marker, final String id){
        LocationCollectionManager.getInstance(context)
                .getCurrentLocation(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Location giftLocation = new Location("");//provider name is unnecessary
                            giftLocation.setLatitude(marker.getPosition().latitude);
                            giftLocation.setLongitude(marker.getPosition().longitude);//your coords of course
                            if (location.distanceTo(giftLocation)<=50){

                                RetrofitClient.getApiService().checkGift(SharedPrefUtils.getUser(myapp).getAccessToken(),id)
                                        .enqueue(new Callback<BaseResponse<GiftStateResponse>>() {
                                            @Override
                                            public void onResponse(Call<BaseResponse<GiftStateResponse>> call, Response<BaseResponse<GiftStateResponse>> response) {
                                                if (response.body() != null) {
                                                    if (response.body().getData() != null) {
                                                        try {
                                                            GiftStateResponse responseState = response.body().getData();

                                                            if (responseState.getState() ==1){
                                                                androidExt.showGiftNotifyDialog(context, "Ch??c m???ng b???n ???? nh???n ???????c "+responseState.getPoint()+" ??i???m", new ClickDialogListener.OK() {
                                                                    @Override
                                                                    public void onCLickOK() {

                                                                    }
                                                                });
                                                            }
                                                            else{
                                                                androidExt.showGiftNotifyDialog(context, "B???n ???? nh???n qu?? t???i v??? tr?? n??y ho???c s??? l?????ng qu?? t???ng ???? h???t", new ClickDialogListener.OK() {
                                                                    @Override
                                                                    public void onCLickOK() {

                                                                    }
                                                                });

                                                            }

                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                    } else {
                                                        androidExt.showErrorDialog(context, "C?? l???i, vui l??ng th??ng b??o cho admin");
                                                    }
                                                } else {
                                                    androidExt.showErrorDialog(context, "C?? l???i, vui l??ng th??ng b??o cho admin");
                                                }
                                            }
                                            @Override
                                            public void onFailure(Call<BaseResponse<GiftStateResponse>> call, Throwable t) {
                                                androidExt.showErrorDialog(context, "K???t n???i th???t b???i, vui l??ng ki???m tra l???i");                    }
                                        });

                            }
                            else{
                                Toast.makeText(context,
                                        "Vui l??ng ?????n v??? tr?? n??y ????? nh???n qu?? t???ng",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(context,
                                    "Kh??ng th??? l???y v??? tr??, vui l??ng th??? l???i",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
