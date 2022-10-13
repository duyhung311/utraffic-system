package com.hcmut.admin.utrafficsystem.ui.userfeedback;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hcmut.admin.utrafficsystem.BuildConfig;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.utrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.request.FeedbackRequest;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.FeedbackResponse;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFeedbackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFeedbackFragment extends Fragment implements MapActivity.OnBackPressCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView imgBack;
    private TextView txtAppVersion;
    private TextView txtSendFeedback;
    private TextView txtFeedbackMessage;

    public UserFeedbackFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFeedback.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFeedbackFragment newInstance(String param1, String param2) {
        UserFeedbackFragment fragment = new UserFeedbackFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_feedback, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addControls(view);
        addEvents(view);
    }

    private void addControls(View view) {
        imgBack = view.findViewById(R.id.imgBack);
        txtAppVersion = view.findViewById(R.id.txtAppVersion);
        txtAppVersion.setText(BuildConfig.VERSION_NAME);
        txtSendFeedback = view.findViewById(R.id.txtSendFeedback);
        txtFeedbackMessage = view.findViewById(R.id.txtFeedbackMessage);
    }

    private void addEvents(View view) {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(UserFeedbackFragment.this).popBackStack();
            }
        });
        txtSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String message = txtFeedbackMessage.getText().toString();
                    if (message.equals("")) {
                        MapActivity.androidExt.showMessageNoAction(
                                getContext(),
                                "Thông báo",
                                "Vui lòng nhập nội dung góp ý!");
                    } else {
                        String accessAuth = MapActivity.currentUser.getAccessToken();
                        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "", "Đang xử lý..!", true);
                        RetrofitClient.getApiService().postUserFeedback(accessAuth, new FeedbackRequest(message))
                                .enqueue(new Callback<BaseResponse<FeedbackResponse>>() {
                                    @Override
                                    public void onResponse(Call<BaseResponse<FeedbackResponse>> call, Response<BaseResponse<FeedbackResponse>> response) {
                                        progressDialog.dismiss();
                                        try {
                                            if (response.body() != null &&
                                                    response.body().getData() != null &&
                                                    response.body().getData().user != null) {
                                                MapActivity.androidExt.showMessageNoAction(
                                                        getContext(),
                                                        "Thông báo",
                                                        "Góp ý của bạn đã được hệ thống ghi nhận!");
                                            } else {
                                                MapActivity.androidExt.showMessageNoAction(
                                                        getContext(),
                                                        "Thông báo",
                                                        "Không thể gửi góp ý, vui lòng kiểm tra lại đường truyền");
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<BaseResponse<FeedbackResponse>> call, Throwable t) {
                                        progressDialog.dismiss();
                                        try {
                                            MapActivity.androidExt.showMessageNoAction(
                                                    getContext(),
                                                    "Thông báo",
                                                    "Không thể gửi góp ý, vui lòng kiểm tra lại đường truyền");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(UserFeedbackFragment.this).popBackStack();
    }
}