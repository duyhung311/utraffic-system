package com.hcmut.admin.utrafficsystem.ui.voucher.transferpoint;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.utrafficsystem.util.SharedPrefUtils;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransferPointFragment extends Fragment implements View.OnClickListener, MapActivity.OnBackPressCallback {
    Button btnConfirm;
    EditText numberPoint;
    Toolbar toolbar;
    CircleImageView avatar;
    TextView name;
    TextView phone;
    EditText point;
    EditText content;
    Bundle bundle = new Bundle();

    AndroidExt androidExt = new AndroidExt();
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                if (getArguments().getString("phone")==null || getArguments().getString("phone")=="") {
                    androidExt.showErrorDialog(getContext(), "Vui lòng cập nhật số điện thoại để thực hiện giao dịch");
                }
                else {
                    hideSoftKeyboard(getActivity());
                    getMessageAuthentication();
                }
                break;
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return inflater.inflate(R.layout.fragment_transfer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(this);
        numberPoint = view.findViewById(R.id.numberPoint);
        avatar = view.findViewById(R.id.avatarTransfer);
        name = view.findViewById(R.id.nameUser);
        phone = view.findViewById(R.id.phoneUser);
        point = view.findViewById(R.id.numberPoint);
        content = view.findViewById(R.id.contentTransfer);
        name.setText(getArguments().getString("name"));
        phone.setText(getArguments().getString("phone"));
        point.setHint("Số dư có thể chuyển: "+getArguments().getInt("pointUser")+ " điểm");
        Picasso.get().load(getArguments().getString("avatar")).noFade().fit().into(avatar);

        btnConfirm.setEnabled(false);
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
        NavHostFragment.findNavController(TransferPointFragment.this).popBackStack();
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
        numberPoint.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()>0 && Integer.parseInt(editable.toString())<=getArguments().getInt("pointUser") && Integer.parseInt(editable.toString())>0){

                    btnConfirm.setEnabled(true);
                }
                else{
                    btnConfirm.setEnabled(false);
                }

            }
        });
    }
    private void getMessageAuthentication(){
        RetrofitClient.getApiService().getMessageAuthentication(SharedPrefUtils.getUser(getContext()).getAccessToken())
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getData() != null) {
                                bundle.putString("point",point.getText().toString());
                                bundle.putString("message",content.getText().toString());
                                bundle.putString("receive",getArguments().getString("id"));
                                NavHostFragment.findNavController(TransferPointFragment.this)
                                        .navigate(R.id.action_transferPointFragment_to_transferOTPFragment,bundle);

                            } else {
                                androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                            }
                        } else {
                            androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                        }
                    }
                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");                    }
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
