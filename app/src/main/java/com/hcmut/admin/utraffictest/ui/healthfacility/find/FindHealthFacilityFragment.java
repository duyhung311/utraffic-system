package com.hcmut.admin.utraffictest.ui.healthfacility.find;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.hcmut.admin.utraffictest.R;
import com.hcmut.admin.utraffictest.model.DiagnosisInfo;
import com.hcmut.admin.utraffictest.repository.remote.RetrofitClient;
import com.hcmut.admin.utraffictest.ui.map.MapActivity;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FindHealthFacilityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindHealthFacilityFragment extends Fragment implements MapActivity.OnBackPressCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static final String SPECIALISATION_IDS = "specialisationIds";
    public static final String SPECIALISATION = "specialisation";

    private TextView txtSearchHF;
    private Switch swSymptom;
    private RelativeLayout rltlSymptom;
    private MultiAutoCompleteTextView mactvSymptoms;
    private TextView tvSymptom;
    private RelativeLayout rltlYearOfBirth;
    private EditText edtYearOfBirth;
    private TextView tvYearOfBirth;
    private RelativeLayout rltlGender;
    private RadioGroup rdgGender;
    private TextView tvGender;
    private Switch swSpecialisation;
    private RelativeLayout rltlSpecialisation;
    private AutoCompleteTextView actvSpecialisations;
    private TextView tvSpecialisation;
    private AppCompatButton btnFind;
    private AppCompatImageButton btnBack;

    private List<String> listAllSymptoms;
    private List<String> listAllSpecialisations;

    Toolbar toolbar;

    public FindHealthFacilityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FindHealthFacilityFragment newInstance(String param1, String param2) {
        FindHealthFacilityFragment fragment = new FindHealthFacilityFragment();
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
        return inflater.inflate(R.layout.fragment_find_health_facility, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtSearchHF = (TextView) view.findViewById(R.id.txtSearchHF);
        swSymptom = (Switch) view.findViewById(R.id.swSymptom);
        rltlSymptom = (RelativeLayout) view.findViewById(R.id.rltlSymptom);
        mactvSymptoms = (MultiAutoCompleteTextView) view.findViewById(R.id.mactvSymptoms);
        tvSymptom = (TextView) view.findViewById(R.id.tvSymptom);
        rltlYearOfBirth = (RelativeLayout) view.findViewById(R.id.rltlYearOfBirth);
        edtYearOfBirth = (EditText) view.findViewById(R.id.edtYearOfBirth);
        tvYearOfBirth = (TextView) view.findViewById(R.id.tvYearOfBirth);
        rltlGender = (RelativeLayout) view.findViewById(R.id.rltlGender);
        rdgGender = (RadioGroup) view.findViewById(R.id.rdgGender);
        tvGender = (TextView) view.findViewById(R.id.tvGender);

        swSpecialisation = (Switch) view.findViewById(R.id.swSpecialisation);
        rltlSpecialisation = (RelativeLayout) view.findViewById(R.id.rltlSpecialisation);
        actvSpecialisations = (AutoCompleteTextView) view.findViewById(R.id.actvSpecialisations);
        tvSpecialisation = (TextView) view.findViewById(R.id.tvSpecialisation);

        btnFind = (AppCompatButton) view.findViewById(R.id.btnFind);
//        btnBack = (AppCompatImageButton) view.findViewById(R.id.btnBack);

        toolbar = (Toolbar) view.findViewById(R.id.toolBarFindHealthFacilities);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Tìm kiếm cơ sở y tế");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPress();
            }
        });

        addEvents();
    }

    private void addEvents(){

        txtSearchHF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(FindHealthFacilityFragment.this)
                        .navigate(R.id.action_findHealthFacilityFragment_to_searchHFFragment);
            }
        });

        swSymptom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    toggleSwitchs(true,false);
                } else {
                    toggleSwitchs(false,true);
                }
            }
        });

        swSpecialisation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    toggleSwitchs(false,true);
                } else {
                    toggleSwitchs(true,false);
                }
            }
        });

        mactvSymptoms.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getAllSymptoms();
                mactvSymptoms.showDropDown();
                return false;
            }
        });

        actvSpecialisations.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getAllSpecialisations();
                actvSpecialisations.showDropDown();
                return false;
            }
        });

//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(FindHealthFacilityFragment.this).popBackStack();
//            }
//        });

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swSymptom.isChecked()){
                    String symptoms = mactvSymptoms.getText().toString().trim().toLowerCase();
                    String yearBirth = edtYearOfBirth.getText().toString();
                    if(symptoms.equals("")){
                        toast("Vui lòng nhập triệu chứng");
                        return;
                    }
                    List<String> symptoms_list = Arrays.asList(symptoms.split("\\s*,\\s*"));

                    String gender = "";
                    switch(rdgGender.getCheckedRadioButtonId()){
                        case R.id.rbtMale:
                            gender = "male";
                            break;
                        case R.id.rbtFemale:
                            gender = "female";
                            break;
                    }
                    if(yearBirth.equals("")){
                        toast("Vui lòng nhập năm sinh");
                        return;
                    }
                    int year_of_birth = Integer.parseInt(yearBirth);
                    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                    if(year_of_birth <= currentYear && year_of_birth >= currentYear-150){
                        diagnosis(symptoms_list, gender, year_of_birth);
                    }else{
                        toast("Vui lòng nhập đúng năm sinh");
                    }
                }

                if(swSpecialisation.isChecked()){
                    String specialisation = actvSpecialisations.getText().toString().toLowerCase();
                    if (specialisation.equals("")){
                        toast("Vui lòng nhập chuyên khoa");
                        return;
                    }else{
                        Bundle bundle = new Bundle();
                        bundle.putString(SPECIALISATION,specialisation);
                        NavHostFragment.findNavController(FindHealthFacilityFragment.this)
                                .navigate(R.id.action_findHealthFacilityFragment_to_mapFeatureFragment,bundle);
                    }
                }
            }
        });
    }

    private void diagnosis(List<String> symptoms, String gender , int year_of_birth ){
        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "", "Đang chẩn đoán!", true);

        RetrofitClient.getAPIHealthFacilities().getHealthFacilitiesByDiagnosis(symptoms,gender,year_of_birth).enqueue(new Callback<List<DiagnosisInfo>>() {
            @Override
            public void onResponse(Call<List<DiagnosisInfo>> call, Response<List<DiagnosisInfo>> response) {
                progressDialog.dismiss();

                List<DiagnosisInfo> listDiagnosis = response.body();
                BottomSheetDiagnosisFragment btsDiagnosis = new BottomSheetDiagnosisFragment(FindHealthFacilityFragment.this,listDiagnosis);
                btsDiagnosis.show(getFragmentManager(),btsDiagnosis.getTag());
            }

            @Override
            public void onFailure(Call<List<DiagnosisInfo>> call, Throwable t) {
                progressDialog.dismiss();
                toast("Kết nối thất bại, vui lòng thử lại");
            }
        });
    }

    private void toggleSwitchs(boolean isCheckedSymptom, boolean isCheckedSpecialisation){
        int blue = ContextCompat.getColor(getActivity(), R.color.blue);
        int gray_dark = ContextCompat.getColor(getActivity(), R.color.gray_dark);

        int colorFindBySymptom = isCheckedSymptom?blue:gray_dark;
        int colorFindBySpecialisation = isCheckedSpecialisation?blue:gray_dark;

        //Drawable drawableFindBySymptom = ContextCompat.getDrawable(getContext(),isCheckedSymptom?R.drawable.bg_blue_border_radius:R.drawable.bg_gray_dark_border_radius);
        //Drawable drawableFindBySpecialisation = ContextCompat.getDrawable(getContext(),isCheckedSpecialisation?R.drawable.bg_blue_border_radius:R.drawable.bg_gray_dark_border_radius);

        int bgFindBySymptom = isCheckedSymptom? R.drawable.bg_blue_border_radius: R.drawable.bg_gray_dark_border_radius;
        int bgFindBySpecialisation = isCheckedSpecialisation? R.drawable.bg_blue_border_radius: R.drawable.bg_gray_dark_border_radius;

        swSymptom.setChecked(isCheckedSymptom);
        swSymptom.setTextColor(colorFindBySymptom);

        rltlSymptom.setBackgroundResource(bgFindBySymptom);
        mactvSymptoms.setEnabled(isCheckedSymptom);
        tvSymptom.setTextColor(colorFindBySymptom);

        rltlYearOfBirth.setBackgroundResource(bgFindBySymptom);
        edtYearOfBirth.setEnabled(isCheckedSymptom);
        tvYearOfBirth.setTextColor(colorFindBySymptom);

        rltlGender.setBackgroundResource(bgFindBySymptom);
        rdgGender.setEnabled(isCheckedSymptom);
        tvGender.setTextColor(colorFindBySymptom);
        for(int i=0 ; i<rdgGender.getChildCount(); i++){
            ((RadioButton)rdgGender.getChildAt(i)).setEnabled(isCheckedSymptom);
        }

        swSpecialisation.setChecked(isCheckedSpecialisation);
        swSpecialisation.setTextColor(colorFindBySpecialisation);

        rltlSpecialisation.setBackgroundResource(bgFindBySpecialisation);
        actvSpecialisations.setEnabled(isCheckedSpecialisation);
        tvSpecialisation.setTextColor(colorFindBySpecialisation);
    }

    private void toast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void getAllSymptoms(){
        RetrofitClient.getAPIHealthFacilities().getAllSymptoms().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                listAllSymptoms = response.body();
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,listAllSymptoms);
                mactvSymptoms.setAdapter(arrayAdapter);
                mactvSymptoms.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                toast("Kết nối thất bại, vui lòng thử lại");
            }
        });
    }

    private void getAllSpecialisations(){
        RetrofitClient.getAPIHealthFacilities().getAllSpecialisation().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                listAllSpecialisations = response.body();
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,listAllSpecialisations);
                actvSpecialisations.setAdapter(arrayAdapter);
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                toast("Kết nối thất bại, vui lòng thử lại");
            }
        });
    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(FindHealthFacilityFragment.this).popBackStack();
    }

}