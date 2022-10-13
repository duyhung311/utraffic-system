package com.hcmut.admin.utrafficsystem.ui.voucher.transferpoint;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.model.AndroidExt;
import com.hcmut.admin.utrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.utrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.UserResponse;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.utrafficsystem.util.SharedPrefUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseTransferFragment extends Fragment implements View.OnClickListener, MapActivity.OnBackPressCallback  {
    Button btnContinue;
    Toolbar toolbar;
    EditText txtChoose;
    Bundle bundle = new Bundle();
    String word;



    AndroidExt androidExt = new AndroidExt();
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_continue:
                hideSoftKeyboard(getActivity());
                findUser(word);

                break;
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choose_transfer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnContinue = view.findViewById(R.id.btn_continue);
        btnContinue.setEnabled(false);
        btnContinue.setOnClickListener(this);
        txtChoose = view.findViewById(R.id.txtChoose);


        toolbar = (androidx.appcompat.widget.Toolbar) view.findViewById(R.id.toolbar_voucher);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Chuyển điểm");
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPress();
            }
        });
        setButtonChange();

    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(ChooseTransferFragment.this).popBackStack();
    }
    @Override
    public void onResume() {
        super.onResume();
        try {
            ((MapActivity) getContext()).hideBottomNav();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setButtonChange(){
        txtChoose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()>0){


                    btnContinue.setEnabled(true);
                    word=editable.toString();
                }
                else{
                    btnContinue.setEnabled(false);
                }

            }
        });
    }
    private void findUser(String word) {

        RetrofitClient.getApiService().findUser(SharedPrefUtils.getUser(getContext()).getAccessToken(),word)
                .enqueue(new Callback<BaseResponse<UserResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<UserResponse>> call, Response<BaseResponse<UserResponse>> response) {

                        if (response.body() != null) {
                            System.out.println(response.body());
                            if (response.body().getCode() == 200) {

                                if (response.body().getData() != null) {
                                    UserResponse userResponse = response.body().getData();
                                    bundle.putString("id",userResponse.getId());
                                    bundle.putString("avatar",userResponse.getAvatar());
                                    bundle.putString("name",userResponse.getName());
                                    bundle.putString("phone",userResponse.getPhoneNumber());
                                    bundle.putInt("point",userResponse.getPoint());
                                    bundle.putInt("pointUser",getArguments().getInt("pointUser"));
                                    NavHostFragment.findNavController(ChooseTransferFragment.this)
                                            .navigate(R.id.action_chooseTransferFragment_to_transferPointFragment,bundle);


                                } else {
                                    androidExt.showErrorDialog(getContext(), "Người dùng không tồn tại");
                                }
                            } else {
                                androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<UserResponse>> call, Throwable t) {

                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");
                    }
                });
    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
