package com.hcmut.admin.utrafficsystem.ui.healthfacility.find;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.adapter.healthfacility.HealthFacilitiesViewCommentAdapter;
import com.hcmut.admin.utrafficsystem.model.HealthFacility;
import com.hcmut.admin.utrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.utrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.request.healthFacilities.CommentRequest;
import com.hcmut.admin.utrafficsystem.ui.direction.DirectionFragment;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.utrafficsystem.util.GsonUtil;
import com.hcmut.admin.utrafficsystem.util.LocationCollectionManager;
import com.hcmut.admin.utrafficsystem.util.MapUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HealthFacilityInforFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HealthFacilityInforFragment extends Fragment implements HealthFacilitiesViewCommentAdapter.ViewCommentAdapterOnClickHandler, MapActivity.OnBackPressCallback{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static final String HF_ADDRESS = "HF_address";
    public static final String HF_LATLNG = "HF_latlng";
    public static final String CURRENT_ADDRESS = "current_address";
    public static final String CURRENT_LATLNG = "current_latlng";

    private TextView nameHF;
    private TextView addressHF;
    private TextView work_timeHF;
    private TextView specialisationHF;
    private TextView serviceHF;
    private TextView phone_numberHF;
    private TextView rating;
    private RatingBar ratingBar;
    private TextView tvNumberOfRating;
    private TextView tvRate;

    private Button btnDirectHF;

    private AutoCompleteTextView actComment;
    private ImageView imageSendComment;

    private static final int MAKE_CALL_PERMISSION_REQUEST_CODE = 1;

    private ImageView imgReportHF;
    private Button btnShareHealthFacilities;

    private RecyclerView recListComment;
    private List<HealthFacility.Comment> comments = new ArrayList<>();;

    HealthFacilitiesViewCommentAdapter healthFacilitiesViewCommentAdapterAdapter;
    ArrayList<HealthFacility.Comment> listComment;

    Toolbar toolbar;

    private HealthFacility infor;


    public HealthFacilityInforFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HealthFacilityInforFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HealthFacilityInforFragment newInstance(String param1, String param2) {
        HealthFacilityInforFragment fragment = new HealthFacilityInforFragment();
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
        return inflater.inflate(R.layout.fragment_health_facility_infor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nameHF = view.findViewById(R.id.nameHF);
        addressHF = view.findViewById(R.id.addressHF);
        work_timeHF = view.findViewById(R.id.work_timeHF);
        specialisationHF = view.findViewById(R.id.specialisationHF);
        serviceHF = view.findViewById(R.id.serviceHF);
        phone_numberHF = view.findViewById(R.id.phone_numberHF);
        rating = view.findViewById(R.id.tv_rating);
        ratingBar = view.findViewById(R.id.ratingBar);;
        tvNumberOfRating = view.findViewById(R.id.tvNumberOfRating);
        tvRate = view.findViewById(R.id.tvRate);

        btnDirectHF = view.findViewById(R.id.btnDirectHF);

        actComment = view.findViewById(R.id.actComment);
        imageSendComment = view.findViewById(R.id.imageSendComment);
        recListComment = view.findViewById(R.id.recListComment);

        toolbar = (Toolbar) view.findViewById(R.id.toolBarHealthFacilitiesInfo);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Thông tin cơ sở y tế");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPress();
            }
        });

        imgReportHF = view.findViewById(R.id.imgReportHF);
        btnShareHealthFacilities = view.findViewById(R.id.btnShareHealthFacilities);

        mGetArguments();

        String json = GsonUtil.objectToJsonString(infor.getComment());
        if(!Strings.isNullOrEmpty(json)) {
            Type type = new TypeToken<List<HealthFacility.Comment>>(){}.getType();
            comments = new Gson().fromJson(json, type);
        }

        addEvent();
        setUpRecycleView();
        getComment();
    }

    private void setUpRecycleView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recListComment.setLayoutManager(layoutManager);
        recListComment.setHasFixedSize(true);
    }

    private void getComment() {
        listComment = new ArrayList<>();
        healthFacilitiesViewCommentAdapterAdapter = new HealthFacilitiesViewCommentAdapter(getContext(), listComment, infor.getId(), this, this);
        recListComment.setAdapter(healthFacilitiesViewCommentAdapterAdapter);

        listComment.clear();
        if(comments != null){
            listComment.addAll(comments);
        }
    }

    private void mGetArguments() {
        Bundle bundle = getArguments();
        if(bundle != null){
            infor = bundle.getParcelable("infor");
            nameHF.setText(infor.getName());
            addressHF.setText(infor.getAddress());

            if (infor.getWork_time().equals("")){
                work_timeHF.setText("Đang cập nhật");
            }else{
                work_timeHF.setText(infor.getWork_time());
            }

            if(Arrays.toString(infor.getSpecialisation()).equals("")){
                specialisationHF.setText("Đang cập nhật");
            }else{
                String specialisation = Arrays.toString(infor.getSpecialisation()).replace("[","").replace("]","");
                handleViewMore(specialisationHF, specialisation);
            }

            if (infor.getService().equals("")){
                serviceHF.setText("Đang cập nhật");
            } else{
                handleViewMore(serviceHF, infor.getService());
            }

            if (infor.getPhone_number().equals("")){
                phone_numberHF.setText("Đang cập nhật");
            }else{
                phone_numberHF.setText(infor.getPhone_number());
            }
        }

        actComment.setEnabled(true);

        rating.setText(String.valueOf(infor.getRate()));
        tvNumberOfRating.setText(infor.getNumberOfRate() + " đánh giá");
        ratingBar.setRating(Float.parseFloat(String.valueOf(infor.getRate())));
    }

    private void addEvent() {

        btnDirectHF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bundle bundle = new Bundle();
                if (MapUtil.checkGPSTurnOn(getActivity(), MapActivity.androidExt)) {
                    LocationCollectionManager.getInstance(getContext())
                            .getCurrentLocation(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        bundle.putString(DirectionFragment.CURRENT_ADDRESS,"Vị trí của bạn");
                                        bundle.putParcelable(DirectionFragment.CURRENT_LATLNG, new LatLng(location.getLatitude(), location.getLongitude()));

                                        bundle.putString(DirectionFragment.DESTINATION_ADDRESS,infor.getAddress());
                                        bundle.putParcelable(DirectionFragment.DESTINATION_LATLNG, new LatLng(infor.getLatitude(),infor.getLongitude()));

                                        NavHostFragment.findNavController(HealthFacilityInforFragment.this).navigate(R.id.action_healthFacilityInforFragment_to_directionFragment,bundle);

                                    } else {
                                        Toast.makeText(getContext(),
                                                "Không thể lấy vị trí, vui lòng thử lại",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        imgReportHF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HealthFacilityInforFragment.this)
                        .navigate(R.id.action_healthFacilityInforFragment_to_userFeedback);
            }
        });

        tvRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(BottomSheetRatingFragment.ID_HF, infor.getId());

                BottomSheetRatingFragment bts = new BottomSheetRatingFragment(bundle, BottomSheetRatingFragment.HF_OBJECT);
                bts.show(getFragmentManager(), bts.getTag());
            }
        });

        imageSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment();
            }
        });

        btnShareHealthFacilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = getArguments();
                if(bundle != null){
                    infor = bundle.getParcelable("infor");
                    String[] textToShare = new String[]{infor.getName(), infor.getPhone_number().replace("\n",""), infor.getAddress().replace(" ", "+").replace("/","+").replace("-","+")};
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Tên: "+ textToShare[0] + "\n" + "Số điện thoại: " + textToShare[1] +  "\n" + "Địa chỉ: " + "https://www.google.com/maps/place/" + textToShare[2]);
                    sendIntent.setType("text/plain");
                    sendIntent.setPackage("com.facebook.orca");
                    try {
                        startActivity(Intent.createChooser(sendIntent,"Chia sẽ"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        imageSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment();
            }
        });

        phone_numberHF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission(Manifest.permission.CALL_PHONE)) {
                    phone_numberHF.setEnabled(true);
                } else {
                    phone_numberHF.setEnabled(false);
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MAKE_CALL_PERMISSION_REQUEST_CODE);
                }

                Bundle bundle = getArguments();
                if(bundle != null){
                    infor = bundle.getParcelable("infor");

                    String phoneNumber = infor.getPhone_number().replace(".","").replace("\n","");
                    if (phoneNumber.length() > 13){
                        ArrayList<String> listPhoneNumber = new ArrayList<String>(Arrays.asList(phoneNumber.split(" - ")));
                        bundle.putStringArrayList("phoneNumber", listPhoneNumber);

                        BottomSheetPhoneNumberFragment btsPhoneNumber = new BottomSheetPhoneNumberFragment(HealthFacilityInforFragment.this, getContext(), listPhoneNumber);
                        btsPhoneNumber.show(getFragmentManager(), btsPhoneNumber.getTag());
                    }else{
                        if (!TextUtils.isEmpty(phoneNumber)) {
                            if (checkPermission(Manifest.permission.CALL_PHONE)) {
                                String dial = "tel:" + phoneNumber;
                                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                            } else {
                                Toast.makeText(getContext(), "Quyền cuộc gọi điện thoại bị từ chối", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Nhập số điện thoại", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    private void handleViewMore(final TextView textView, final String string){

        if (string.length() > 80) {
            textView.setText(Html.fromHtml(string.substring(0, 80)+"..."+"<font color='blue'> <u>Xem thêm</u></font>"));
        }
        else textView.setText(string);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textView.getText().toString().endsWith("Xem thêm")) {
                    textView.setText(Html.fromHtml(string +"<font color='blue'> <u>Thu gọn</u></font>"));
                }
                else {
                    if (string.length() > 80) {
                        textView.setText(Html.fromHtml(string.substring(0, 80)+"..."+"<font color='blue'> <u>Xem thêm</u></font>"));
                    }
                    else textView.setText(string);
                }
            }
        });
    }

    private void sendComment(){
        try {
            String message = actComment.getText().toString().trim();
            if (message.equals("")) {
                MapActivity.androidExt.showMessageNoAction(
                        getContext(),
                        "Thông báo",
                        "Vui lòng nhập nội dung bình luận!");
            } else {
                final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "", "Đang xử lý..!", true);
                final HealthFacility.Comment comment = new HealthFacility.Comment(MapActivity.currentUser.getUserId(), MapActivity.currentUser.getUserName(), message);
                RetrofitClient.getAPIHealthFacilities().postUserCommentHealthFacilities(new CommentRequest(infor.getId(), comment))
                        .enqueue(new Callback<BaseResponse<Object>>() {
                            @Override
                            public void onResponse(Call<BaseResponse<Object>> call, Response<BaseResponse<Object>> response) {
                                progressDialog.dismiss();
                                try {
                                    if (response.body() != null) {
                                        listComment.add(comment);
                                        healthFacilitiesViewCommentAdapterAdapter.notifyDataSetChanged();
                                        MapActivity.androidExt.showMessageNoAction(
                                                getContext(),
                                                "Thông báo",
                                                "Bình luận của bạn đã được hệ thống ghi nhận!");
                                    } else {
                                        MapActivity.androidExt.showMessageNoAction(
                                                getContext(),
                                                "Thông báo",
                                                "Không thể gửi bình luận, vui lòng kiểm tra lại đường truyền");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {
                                progressDialog.dismiss();
                                try {
                                    MapActivity.androidExt.showMessageNoAction(
                                            getContext(), "Thông báo", "Không thể gửi bình luận, vui lòng kiểm tra lại đường truyền");
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

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case MAKE_CALL_PERMISSION_REQUEST_CODE :
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    phone_numberHF.setEnabled(true);
                    Toast.makeText(getContext(), "Bạn có thể gọi đến số bằng cách nhấp vào nút", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(HealthFacilityInforFragment.this).popBackStack();
    }
}