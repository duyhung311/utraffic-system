package com.hcmut.admin.utraffictest.ui.signup;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hcmut.admin.utraffictest.R;
import com.hcmut.admin.utraffictest.model.AndroidExt;
import com.hcmut.admin.utraffictest.repository.remote.model.BaseResponse;
import com.hcmut.admin.utraffictest.repository.remote.model.response.LoginResponse;
import com.hcmut.admin.utraffictest.repository.remote.RetrofitClient;
import com.hcmut.admin.utraffictest.util.ClickDialogListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    EditText username, password, confirmPassword, email, phoneNumber, name;
    Button btnSignUp;
    AndroidExt androidExt = new AndroidExt();

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        init();
    }

    private void init() {
        username = findViewById(R.id.edt_username);
        password = findViewById(R.id.edt_password);
        confirmPassword = findViewById(R.id.edt_check_password);
        email = findViewById(R.id.edt_email);
        name = findViewById(R.id.name);
        phoneNumber = findViewById(R.id.edt_phone);
        btnSignUp = findViewById(R.id.btn_sign_up);
        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_up: {
                signUp();
                break;
            }
        }
    }

    private void signUp() {
        if (username.getText().toString().isEmpty()) {
            androidExt.showErrorDialog(this, "Vui lòng nhập tên đăng nhập");
            username.requestFocus();
        } else if (password.getText().toString().length() < 6) {
            androidExt.showErrorDialog(this, "Vui lòng nhập mật khẩu ít nhất 6 ký tự");
            password.requestFocus();
        } else if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
            androidExt.showErrorDialog(this, "Mật khẩu xác nhận không chính xác");
            confirmPassword.requestFocus();
        } else {
            progressDialog = ProgressDialog.show(SignUpActivity.this, "", getString(R.string.loading), true);
            RetrofitClient.getApiService().register(username.getText().toString(), password.getText().toString(),
                    name.getText().toString(), email.getText().toString(), phoneNumber.getText().toString())
                    .enqueue(new Callback<BaseResponse<LoginResponse>>() {
                        @Override
                        public void onResponse(Call<BaseResponse<LoginResponse>> call, Response<BaseResponse<LoginResponse>> response) {
                            if (response.body() != null) {
                                if (response.body().getData() != null) {
                                    androidExt.showSuccessDialog(SignUpActivity.this, "Đăng ký thành công", new ClickDialogListener.OK() {
                                        @Override
                                        public void onCLickOK() {
                                            onBackPressed();
                                        }
                                    });
                                } else {
                                    androidExt.showErrorDialog(SignUpActivity.this, "Tên đăng nhập đã tồn tại");
                                }
                            } else {
                                androidExt.showErrorDialog(SignUpActivity.this, "Tên đăng nhập đã tồn tại");
                            }
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onFailure(Call<BaseResponse<LoginResponse>> call, Throwable t) {
                            androidExt.showErrorDialog(SignUpActivity.this, "Đăng ký thất bại, vui lòng liên hệ admin");
                            progressDialog.dismiss();
                        }
                    });
        }
    }
}
