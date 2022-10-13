package com.hcmut.admin.utrafficsystem.ui.signin;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.model.AndroidExt;
import com.hcmut.admin.utrafficsystem.repository.remote.model.request.LoginParam;
import com.hcmut.admin.utrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.LoginResponse;
import com.hcmut.admin.utrafficsystem.model.User;
import com.hcmut.admin.utrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.utrafficsystem.ui.signup.SignUpActivity;
import com.hcmut.admin.utrafficsystem.ui.SplashActivity;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.utrafficsystem.util.SharedPrefUtils;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final int MY_PERMISSIONS_RECORD_AUDIO = 5678;
    private static final int GOOGLE_LOGIN_TYPE = 1;
    private static final int FACEBOOK_LOGIN_TYPE = 2;

    private Boolean mPermissionsGranted = false;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    private static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String INTERNET = Manifest.permission.INTERNET;

    //Widgets
    Button fbLoginBtn, ggLoginBtn, loginBtn;
    EditText username, password;
    TextView tvSignUp;
    CallbackManager callbackManager;
    GoogleSignInClient googleSignInClient;
    GoogleApiClient googleApiClient;

    AndroidExt androidExt = new AndroidExt();

    private ProgressDialog progressDialog;

    //Google signin vars
    private static final int RC_SIGN_IN = 1;
    private static final int RC_PERM_GET_ACCOUNTS = 2;
    private long current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        User user = SharedPrefUtils.getUser(this);
        if (user != null) {
            Intent intent = new Intent(SignInActivity.this, MapActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        FacebookSdk.sdkInitialize(getApplicationContext());
        getPermissions();
        callbackManager = CallbackManager.Factory.create();
        init();
        Log.d("test Time", String.valueOf(Calendar.getInstance().getTimeInMillis() - SplashActivity.time));
    }
    private void init() {
        fbLoginBtn = findViewById(R.id.facebook_login_button);
        ggLoginBtn = findViewById(R.id.google_login_button);
        loginBtn = findViewById(R.id.btn_sign_in);
        username = findViewById(R.id.edt_username);
        password = findViewById(R.id.edt_password);
        tvSignUp = findViewById(R.id.tv_sign_up);
        loginBtn.setOnClickListener(this);
        fbLoginBtn.setOnClickListener(this);
        ggLoginBtn.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);

        //-------FACEBOOK LOGIN--------
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                updateUIFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
            }
        });

        //-------GOOGLE LOGIN--------
        final GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(SignInActivity.this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        googleApiClient.connect();

        // Build a GoogleSignInClient with the options specified by gso.


        googleSignInClient = GoogleSignIn.getClient(this, gso);

        if (SharedPrefUtils.getUser(this) == null) {
            googleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
            LoginManager.getInstance().logOut();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.facebook_login_button: {
                if (AccessToken.getCurrentAccessToken() != null)
                    LoginManager.getInstance().logOut();
                LoginManager.getInstance().logInWithReadPermissions(SignInActivity.this,
                        Collections.singletonList("public_profile"));
//                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
//                        new ResultCallback<BaseResponse<Status>() {
//                            @Override
//                            public void onResult(Status status) {
//                            }
//                        });
                break;
            }
            case R.id.google_login_button: {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
            }

            case R.id.btn_sign_in: {
                SplashActivity.time = Calendar.getInstance().getTimeInMillis();
                current = Calendar.getInstance().getTimeInMillis();
                signIn();
                break;
            }

            case R.id.tv_sign_up: {
                startActivity(new Intent(this, SignUpActivity.class));
                break;
            }
        }
    }

    private void signIn() {
        progressDialog = ProgressDialog.show(SignInActivity.this, "", getString(R.string.loading), true);
        RetrofitClient.getApiService().login(username.getText().toString(), password.getText().toString())
                .enqueue(new Callback<BaseResponse<LoginResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<LoginResponse>> call, Response<BaseResponse<LoginResponse>> response) {
                        progressDialog.dismiss();
                        if (response.body() != null) {
                            if (response.body().getCode() == 200) {
                                if (response.body().getData() != null) {

                                    LoginResponse loginResponse = response.body().getData();
                                    User user = new User();
                                    user.setAccessToken("Bearer " + response.body().getData().getAccessToken());
                                    user.setUserEmail(loginResponse.getEmail());
                                    user.setImgUrl(loginResponse.getAvatar());
                                    user.setUserName(loginResponse.getName());
                                    user.setAccountType("");
                                    user.setPhoneNumber(loginResponse.getPhoneNumber());
                                    user.setRole(loginResponse.getRole());
                                    user.setUserId(loginResponse.getId());
                                    user.setEvaluation_score(loginResponse.getEvaluation_score());
                                    user.setmLocationPermissionsGranted(mPermissionsGranted);
                                    SharedPrefUtils.saveUser(SignInActivity.this, user);
                                    Intent intent = new Intent(SignInActivity.this, MapActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    Log.d("Sign In", String.valueOf(Calendar.getInstance().getTimeInMillis() - current));
                                } else {
                                    androidExt.showErrorDialog(SignInActivity.this, "Có lỗi, vui lòng thông báo cho admin");
                                }
                            } else {
                                androidExt.showErrorDialog(SignInActivity.this, "Tài Khoản hoặc mật khẩu không đúng");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<LoginResponse>> call, Throwable t) {
                        progressDialog.dismiss();
                        androidExt.showErrorDialog(SignInActivity.this, "Kết nối thất bại, vui lòng kiểm tra lại");
                    }
                });
    }

    boolean isPermissionGranted(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }

    private void getPermissions() {
        String[] permissions = {FINE_LOCATION, COURSE_LOCATION, RECORD_AUDIO,
                WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, INTERNET};

        if (isPermissionGranted(FINE_LOCATION)) {
            if (isPermissionGranted(COURSE_LOCATION)) {
                if (isPermissionGranted(RECORD_AUDIO)) {
                    if (isPermissionGranted(WRITE_EXTERNAL_STORAGE)) {
                        if (isPermissionGranted(READ_EXTERNAL_STORAGE)) {
                            if (isPermissionGranted(INTERNET)) {
                                mPermissionsGranted = true;
                            } else {
                                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
                            }
                        } else {
                            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
                        }
                    } else {
                        ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
                    }
                } else {
                    ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
                }
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    // Google and Facebook login-support functions
    protected void updateUIGoogle(final GoogleSignInAccount account) {
        progressDialog = ProgressDialog.show(SignInActivity.this, "", getString(R.string.loading), true);
        //Get access token from server
        RetrofitClient.getApiService().loginWithGoogle(account.getId(), account.getIdToken()).enqueue(new Callback<BaseResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<LoginResponse>> call, Response<BaseResponse<LoginResponse>> response) {
//                User user = new User();
                if (response.body() != null && response.body().getData() != null) {

                    LoginResponse loginResponse = response.body().getData();
                    User user = new User();
                    user.setAccessToken("Bearer " + response.body().getData().getAccessToken());
                    user.setUserEmail(loginResponse.getEmail());
                    user.setImgUrl(loginResponse.getAvatar());
                    user.setUserName(loginResponse.getName());
                    user.setAccountType("google");
                    user.setPhoneNumber(loginResponse.getPhoneNumber());
                    user.setUserId(loginResponse.getId());
                    user.setRole(loginResponse.getRole());
                    user.setmLocationPermissionsGranted(mPermissionsGranted);
                    SharedPrefUtils.saveUser(SignInActivity.this, user);
                    Intent intent = new Intent(SignInActivity.this, MapActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Log.d("Sign In", String.valueOf(Calendar.getInstance().getTimeInMillis() - current));
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<BaseResponse<LoginResponse>> call, Throwable t) {
                progressDialog.dismiss();
//                Toast.makeText(LoginActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void updateUIFacebook(final AccessToken accessToken) {
        progressDialog = ProgressDialog.show(SignInActivity.this, "", getString(R.string.loading), true);
        GraphRequest request = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            final String name = object.getString("name");
                            final String id = object.getString("id");

                            LoginParam loginParam = new LoginParam(id, accessToken.getToken(), FACEBOOK_LOGIN_TYPE);
                            //Get access token from server
                            RetrofitClient.getApiService().loginWithFacebook(id, accessToken.getToken())
                                    .enqueue(new Callback<BaseResponse<LoginResponse>>() {
                                @Override
                                public void onResponse(Call<BaseResponse<LoginResponse>> call, Response<BaseResponse<LoginResponse>> response) {
                                    if (response.body() != null && response.body().getData() != null) {

                                        LoginResponse loginResponse = response.body().getData();
                                        User user = new User();
                                        user.setAccessToken("Bearer " + response.body().getData().getAccessToken());
                                        user.setUserEmail(loginResponse.getEmail());
                                        user.setImgUrl(loginResponse.getAvatar());
                                        user.setUserName(loginResponse.getName());
                                        user.setAccountType("facebook");
                                        user.setPhoneNumber(loginResponse.getPhoneNumber());
                                        user.setUserId(loginResponse.getId());
                                        user.setRole(loginResponse.getRole());
                                        user.setmLocationPermissionsGranted(mPermissionsGranted);
                                        SharedPrefUtils.saveUser(SignInActivity.this, user);
                                        Intent intent = new Intent(SignInActivity.this, MapActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        Log.d("Sign In", String.valueOf(Calendar.getInstance().getTimeInMillis() - current));
                                    }
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onFailure(Call<BaseResponse<LoginResponse>> call, Throwable t) {
                                    progressDialog.dismiss();
//                                    Toast.makeText(LoginActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            progressDialog.dismiss();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,email,id");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mPermissionsGranted = true;
                }
            }
        }
    }

    //Goolge handle sign in
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUIGoogle(account);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

