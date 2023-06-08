package com.hcmut.admin.utraffictest.business;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.hcmut.admin.utraffictest.BuildConfig;
import com.hcmut.admin.utraffictest.repository.remote.RetrofitClient;
import com.hcmut.admin.utraffictest.repository.remote.model.BaseResponse;
import com.hcmut.admin.utraffictest.repository.remote.model.response.AppVersionResponse;
import com.hcmut.admin.utraffictest.ui.map.MapActivity;
import com.hcmut.admin.utraffictest.util.ClickDialogListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VersionUpdater {
    public static final String APP_VERSION_CLIENT_ID = "1";

    public static void checkNewVersion(final Activity activity) {
        RetrofitClient.getApiService().getCurrentAppVersionInfo(APP_VERSION_CLIENT_ID)
                .enqueue(new Callback<BaseResponse<AppVersionResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<AppVersionResponse>> call, Response<BaseResponse<AppVersionResponse>> response) {
                        try {
                            int appVersionCode = BuildConfig.VERSION_CODE;
                            int remoteVersionCode = response.body().getData().versionCode;
                            if (isOldVersionCode(appVersionCode, remoteVersionCode)) {
                                MapActivity.androidExt.showDialog(
                                        activity,
                                        "Cập nhật phần mềm",
                                        "Phiên bản mới đã có sẵn, bạn có muốn cập nhật?",
                                        new ClickDialogListener.Yes() {
                                            @Override
                                            public void onCLickYes() {
                                                directToAppInCHPlay(activity);
                                            }
                                        });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<AppVersionResponse>> call, Throwable t) {

                    }
                });
    }

    private static boolean isOldVersionCode(int appVersionCode, int remoteVersionCode) {
        return appVersionCode < remoteVersionCode;
    }

    private static void directToAppInCHPlay(Activity activity) {
        final String appPackageName = activity.getPackageName();
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
}
