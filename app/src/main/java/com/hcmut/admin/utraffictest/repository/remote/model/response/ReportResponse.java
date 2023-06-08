package com.hcmut.admin.utraffictest.repository.remote.model.response;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;
import com.hcmut.admin.utraffictest.R;
import com.hcmut.admin.utraffictest.repository.remote.model.BaseResponse;
import com.hcmut.admin.utraffictest.repository.remote.model.request.RatingBody;
import com.hcmut.admin.utraffictest.repository.remote.RetrofitClient;
import com.hcmut.admin.utraffictest.ui.map.MapActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportResponse {
    @SerializedName("_id")
    private String id;
    @SerializedName("velocity")
    private int velocity;
    @SerializedName("description")
    private String description;
    @SerializedName("images")
    private ArrayList<String> images;
    @SerializedName("causes")
    private ArrayList<String> causes;
    @SerializedName("userId")
    private int userId;
    @SerializedName("user")
    private ReportUser user;
    @SerializedName("name")
    private String name;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("reputation")
    private float reputation;

    public ReportResponse(){}

    public ReportResponse(int id, ReportUser reportUser, int velocity, ArrayList<String> images, ArrayList<String> causes, String name, float reputation) {
        this.velocity = velocity;
        this.images = images;
        this.causes = causes;
        this.name = name;
        this.reputation = reputation;
        user = reportUser;
        //this.id = id;
    }

    public void performRating(final Context context, int rate) {
        if (context != null) {
            final ProgressDialog progressDialog = ProgressDialog.show(
                    context,
                    "",
                    context.getString(R.string.loading),
                    true);
            RetrofitClient.getApiService().postRating(MapActivity.currentUser.getAccessToken(), new RatingBody(this.id, (float) rate / 5))
                    .enqueue(new Callback<BaseResponse<PostRatingResponse>>() {
                        @Override
                        public void onResponse(Call<BaseResponse<PostRatingResponse>> call, Response<BaseResponse<PostRatingResponse>> response) {
                            progressDialog.dismiss();
                            if (response.body() != null && response.body().getCode() == 200) {
                                Toast.makeText(context, "Gửi thành công", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Gửi đánh giá thất bại, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseResponse<PostRatingResponse>> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Gửi đánh giá thất bại, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public String getId() {
        return id;
    }

    public int getVelocity() {
        return velocity;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public List<String> getCauses() {
        return causes;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public float getReputation() {
        return reputation;
    }

    public ReportUser getUser() {
        return user;
    }
}
