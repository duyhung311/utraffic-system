package com.hcmut.admin.utrafficsystem.ui.atm;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.model.Atm;
import com.hcmut.admin.utrafficsystem.ui.direction.DirectionFragment;
import com.hcmut.admin.utrafficsystem.ui.healthfacility.find.BottomSheetPhoneNumberFragment;
import com.hcmut.admin.utrafficsystem.ui.healthfacility.find.BottomSheetRatingFragment;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.utrafficsystem.util.LocationCollectionManager;
import com.hcmut.admin.utrafficsystem.util.MapUtil;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AtmInforFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AtmInforFragment extends Fragment implements MapActivity.OnBackPressCallback{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView nameAtm;
    private TextView addressAtm;
    private TextView work_timeAtm;
    private TextView numberAtm;
    private TextView branchAtm;
    private TextView phone_numberAtm;

    private TextView rating;
    private RatingBar ratingAtm;
    private TextView numberOfRating;
    private TextView tvRate;
    private Button btnDirectAtm;

    private static final int MAKE_CALL_PERMISSION_REQUEST_CODE = 1;

    private ImageView imgReportAtm;
    private Button btnShareAtm;

    Toolbar toolbar;

    private Atm atm;

    public AtmInforFragment() {
    }

    public static AtmInforFragment newInstance(String param1, String param2) {
        AtmInforFragment fragment = new AtmInforFragment();
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
        return inflater.inflate(R.layout.fragment_atm_infor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nameAtm = view.findViewById(R.id.nameAtm);
        addressAtm = view.findViewById(R.id.addressAtm);
        work_timeAtm = view.findViewById(R.id.work_timeAtm);
        numberAtm = view.findViewById(R.id.numberAtm);
        branchAtm = view.findViewById(R.id.branchAtm);
        phone_numberAtm = view.findViewById(R.id.phone_numberAtm);

        rating = view.findViewById(R.id.tv_rating);
        ratingAtm = view.findViewById(R.id.ratingAtm);;
        numberOfRating = view.findViewById(R.id.tv_numberOfRating);
        tvRate = view.findViewById(R.id.tvRate);
        btnDirectAtm = view.findViewById(R.id.btnDirectAtm);

        toolbar = (Toolbar) view.findViewById(R.id.toolBarAtmInfo);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Thông tin điểm đặt ATM");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPress();
            }
        });

        imgReportAtm = view.findViewById(R.id.imgReportAtm);
        btnShareAtm = view.findViewById(R.id.btnShareAtm);

        mGetArguments();
        addEvent();

    }

    private void mGetArguments() {
        Bundle bundle = getArguments();
        if(bundle != null){
            atm = bundle.getParcelable("infoAtm");
            nameAtm.setText(atm.getName());
            addressAtm.setText(atm.getAddress());

            if (atm.getWork_time().equals("")){
                work_timeAtm.setText("Đang cập nhật");
            }else{
                work_timeAtm.setText(atm.getWork_time());
            }

            if (atm.getNumber_atm() == null){
                numberAtm.setText("Đang cập nhật");
            }else{
                numberAtm.setText(String.valueOf(atm.getNumber_atm()) + " máy");
            }

            if (atm.getBranch_atm().equals("")){
                branchAtm.setText("Đang cập nhật");
            }else{
                branchAtm.setText(atm.getBranch_atm());
            }

            if (atm.getPhone_number().equals("")){
                phone_numberAtm.setText("Đang cập nhật");
            }else{
                phone_numberAtm.setText(atm.getPhone_number().replace("và"," - "));
            }
        }
        rating.setText(String.valueOf(atm.getRate()));
        numberOfRating.setText(atm.getNumberOfRate() + " đánh giá");
        ratingAtm.setRating(Float.parseFloat(String.valueOf(atm.getRate())));

    }

    private void addEvent() {

        btnDirectAtm.setOnClickListener(new View.OnClickListener() {
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

                                        bundle.putString(DirectionFragment.DESTINATION_ADDRESS, atm.getAddress());
                                        bundle.putParcelable(DirectionFragment.DESTINATION_LATLNG, new LatLng(atm.getLatitude(), atm.getLongitude()));

                                        NavHostFragment.findNavController(AtmInforFragment.this).navigate(R.id.action_atmInforFragment_to_directionFragment, bundle);

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

        imgReportAtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(AtmInforFragment.this)
                        .navigate(R.id.action_atmInforFragment_to_userFeedback);
            }
        });

        tvRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(BottomSheetRatingFragment.ID_ATM, atm.getId());

                BottomSheetRatingFragment bts = new BottomSheetRatingFragment( bundle, BottomSheetRatingFragment.ATM_OBJECT);
                bts.show(getFragmentManager(), bts.getTag());
            }
        });

        btnShareAtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = getArguments();
                if(bundle != null){
                    atm = bundle.getParcelable("infoAtm");
                    String[] textToShare = new String[]{atm.getName(), atm.getPhone_number().replace("và"," - "), atm.getAddress().replace(" ", "+").replace("/","+").replace("-","+")};
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

        phone_numberAtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission(Manifest.permission.CALL_PHONE)) {
                    phone_numberAtm.setEnabled(true);
                } else {
                    phone_numberAtm.setEnabled(false);
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MAKE_CALL_PERMISSION_REQUEST_CODE);
                }

                Bundle bundle = getArguments();
                if(bundle != null){
                    atm = bundle.getParcelable("infoAtm");

                    String phoneNumber = atm.getPhone_number().replace(".","").replace("\n","").replace("và"," - ");
                    if (phoneNumber.length() > 13){
                        ArrayList<String> listPhoneNumber = new ArrayList<String>(Arrays.asList(phoneNumber.split(" - ")));
                        bundle.putStringArrayList("phoneNumber", listPhoneNumber);

                        BottomSheetPhoneNumberFragment btsPhoneNumber = new BottomSheetPhoneNumberFragment(AtmInforFragment.this, getContext(), listPhoneNumber);
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

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case MAKE_CALL_PERMISSION_REQUEST_CODE :
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    phone_numberAtm.setEnabled(true);
                    Toast.makeText(getContext(), "Bạn có thể gọi đến số bằng cách nhấp vào nút", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(AtmInforFragment.this).popBackStack();
    }
}