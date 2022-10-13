package com.hcmut.admin.utrafficsystem.ui.partner.healthfacility;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.model.LatLng;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.business.SearchPlaceHandler;
import com.hcmut.admin.utrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.utrafficsystem.util.ReadPathUtil;
import com.hcmut.admin.utrafficsystem.util.SharedPrefUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterHealthFacilityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterHealthFacilityFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private static final int IMAGE_GALLERY_CODE = 1002;
    private static final String PROCESSING = "processing";
    private static final String REJECTED = "rejected";
    private static final String ACCEPTED = "accepted";

    private String[] allSpecialisations;
    private boolean[] checkedSpecialisations;
    private ArrayList<Integer> chosenSpecialisations = new ArrayList<>();

    private EditText edtNameHF;
    private EditText edtAddressHF;
    private EditText edtWorkTimeHF;
    private TextView tvSpecialisationHF;
    private EditText edtServiceHF;
    private EditText edtPhoneNumberHF;
    private ImageView imvLicenseHF;
    private AppCompatImageButton btnBack;
    private AppCompatButton btnRegisterHF;

    private MultipartBody.Part licenseImage;
    private ProgressDialog progressDialog;

    public RegisterHealthFacilityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InforHealthFacilityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterHealthFacilityFragment newInstance(String param1, String param2) {
        RegisterHealthFacilityFragment fragment = new RegisterHealthFacilityFragment();
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
        return inflater.inflate(R.layout.fragment_register_health_facility, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtNameHF = view.findViewById(R.id.edtNameHF);
        edtAddressHF = view.findViewById(R.id.edtAddressHF);
        edtWorkTimeHF = view.findViewById(R.id.edtWorkTimeHF);
        tvSpecialisationHF = view.findViewById(R.id.tvSpecialisationHF);
        edtServiceHF = view.findViewById(R.id.edtServiceHF);
        edtPhoneNumberHF = view.findViewById(R.id.edtPhoneNumberHF);
        imvLicenseHF = view.findViewById(R.id.imvLicenseHF);
        btnBack =(AppCompatImageButton) view.findViewById(R.id.btnBack);
        btnRegisterHF = (AppCompatButton) view.findViewById(R.id.btnRegisterHF);

        addEvent();
    }

    private void addEvent() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(RegisterHealthFacilityFragment.this).popBackStack();
            }
        });

        imvLicenseHF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //askCameraPermissions();
                takeImage();
            }
        });

        tvSpecialisationHF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrofitClient.getAPIHealthFacilities().getAllSpecialisation().enqueue(new Callback<List<String>>() {
                    @Override
                    public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                        allSpecialisations = response.body().toArray(new String[0]);
                        checkedSpecialisations = new boolean[allSpecialisations.length];
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Chọn chuyên khoa");
                        builder.setCancelable(false);
                        builder.setMultiChoiceItems(allSpecialisations, checkedSpecialisations, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if(isChecked){
                                    chosenSpecialisations.add(which);
                                    //Collections.sort(chosenSpecialisations);
                                }else{
                                    chosenSpecialisations.remove(Integer.valueOf(which));
                                }
                            }
                        });
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StringBuilder stringBuilder = new StringBuilder();
                                for(int i=0 ; i<chosenSpecialisations.size() ; i++){
                                    stringBuilder.append(allSpecialisations[chosenSpecialisations.get(i)]);
                                    if(i != chosenSpecialisations.size()-1){
                                        stringBuilder.append(",");
                                    }
                                }
                                chosenSpecialisations.clear();
                                tvSpecialisationHF.setText(stringBuilder.toString());
                            }
                        });
                        builder.show();
                    }

                    @Override
                    public void onFailure(Call<List<String>> call, Throwable t) {
                        Toast.makeText(getContext(),"Kết nốt thất bại, vui lòng thử lại",Toast.LENGTH_LONG);
                    }
                });

            }
        });

        btnRegisterHF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInputEditext() == false){
                    return;
                }
                progressDialog = ProgressDialog.show(getContext(), "", "Vui lòng chờ", true);

                List<String> specialisationsList = new ArrayList<String>(Arrays.asList(String.valueOf(tvSpecialisationHF.getText()).split(",")));
                String address = edtAddressHF.getText().toString();

                RequestBody rbPartnerId = RequestBody.create(SharedPrefUtils.getUser(getContext()).getUserId(),MediaType.parse("multipart/form-data"));
                RequestBody rbName = RequestBody.create(edtNameHF.getText().toString(),MediaType.parse("multipart/form-data"));
                RequestBody rbAddress = RequestBody.create(address,MediaType.parse("multipart/form-data"));
                RequestBody rbWorkTime = RequestBody.create(edtWorkTimeHF.getText().toString() ,MediaType.parse("multipart/form-data"));
                List<RequestBody> rbSpecialisation = new ArrayList<>();
                for(int i = 0 ; i< specialisationsList.size(); i++){
                    rbSpecialisation.add(RequestBody.create(specialisationsList.get(i),MediaType.parse("multipart/form-data")));
                }
                RequestBody rbService = RequestBody.create(edtServiceHF.getText().toString(),MediaType.parse("multipart/form-data"));
                RequestBody rbPhoneNumber = RequestBody.create(edtPhoneNumberHF.getText().toString(),MediaType.parse("multipart/form-data"));
                LatLng latLng = SearchPlaceHandler.getLatLngFromAddressTextInput(getContext(), address);
                /*for(int i = 0 ; i<3 ; i++){
                    if(latLng == null){
                        latLng = SearchPlaceHandler.getLatLngFromAddressTextInput(getContext(), address);
                    }else{
                        break;
                    }
                }*/
                if (latLng != null) {
                    RequestBody rbLatitude = RequestBody.create(String.valueOf(latLng.latitude),MediaType.parse("multipart/form-data"));
                    RequestBody rbLongitude = RequestBody.create(String.valueOf(latLng.longitude),MediaType.parse("multipart/form-data"));
                    RequestBody rbState = RequestBody.create(PROCESSING,MediaType.parse("multipart/form-data"));

                    RetrofitClient.getAPIHealthFacilities().postHealthFacilityRegister(
                            rbPartnerId,
                            rbName,
                            rbAddress,
                            rbWorkTime,
                            rbSpecialisation,
                            rbService,
                            rbPhoneNumber,
                            rbLatitude,
                            rbLongitude,
                            licenseImage,
                            rbState
                    ).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(),"Thông tin đang chờ xử lý.",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(),"Kết nối thất bại, vui lòng thử lại",Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Không tìm được địa chỉ, vui lòng nhập lại.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkInputEditext(){
        if(edtNameHF.getText().toString().equals("")){
            Toast.makeText(getContext(), "Vui lòng nhập tên cơ sở y tế", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(edtAddressHF.getText().toString().equals("")){
            Toast.makeText(getContext(), "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(edtWorkTimeHF.getText().toString().equals("")){
            Toast.makeText(getContext(), "Vui lòng nhập thời gian làm việc", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(tvSpecialisationHF.getText().equals("")){
            Toast.makeText(getContext(), "Vui lòng chọn chuyên khoa", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(edtServiceHF.getText().toString().equals("")){
            Toast.makeText(getContext(), "Vui lòng nhập dịch vụ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(edtPhoneNumberHF.getText().toString().equals("")){
            Toast.makeText(getContext(), "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(licenseImage == null){
            Toast.makeText(getContext(), "Vui lòng chọn ảnh giấy phép hoạt động", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void takeImage(){
        final CharSequence[] options = {"Chụp ảnh từ camera","Chọn ảnh từ bộ sưu tập"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Chọn ảnh");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(options[which].equals("Chụp ảnh từ camera")){
                    askCameraPermissions();
                }else if(options[which].equals("Chọn ảnh từ bộ sưu tập")){
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_GALLERY_CODE);
                }
            }
        });
        builder.show();
    }

    private void askCameraPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                    || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                String[] permission = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission,PERMISSION_CODE);
            }else{
                openCamera();
            }
        }else{
            openCamera();
        }
    }

    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera,IMAGE_CAPTURE_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }else{
                    Toast.makeText(getContext(),"Permission denied",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == IMAGE_CAPTURE_CODE){
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imvLicenseHF.setImageBitmap(bitmap);
                File file = savebitmap(bitmap);
                RequestBody requestBody = RequestBody.create(file,MediaType.parse("image/png"));
                licenseImage = MultipartBody.Part.createFormData("license_image", file.getName(), requestBody);

            }else if(requestCode == IMAGE_GALLERY_CODE){
                Uri imageUri = data.getData();
                String path = ReadPathUtil.getPath(getContext(), data.getData());
                File file = new File(path);
                imvLicenseHF.setImageURI(imageUri);
                RequestBody requestBody = RequestBody.create(file,MediaType.parse(getContext().getContentResolver().getType(imageUri)));
                licenseImage = MultipartBody.Part.createFormData("license_image", file.getName(), requestBody);
            }
        }
    }

    private File savebitmap(Bitmap bmp) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        // String temp = null;
        File file = new File(extStorageDirectory, "temp.png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, "temp.png");

        }

        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }
}