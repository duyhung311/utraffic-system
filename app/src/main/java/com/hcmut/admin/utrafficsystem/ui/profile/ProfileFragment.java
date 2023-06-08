package com.hcmut.admin.utrafficsystem.ui.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.business.glide.GlideApp;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements MapActivity.OnBackPressCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView txtEditProfile;
    private TextView txtDeleteAccount;
    private TextView txtName;
    private TextView txtEmail;
    private TextView txtPhone;
    private RoundedImageView imgAvatar;
    private AppCompatRatingBar ratingBar;
    private ImageView imgBack;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        addControls(view);
        addEvents();
    }

    private void addControls(View view) {
        try {
            ((MapActivity) view.getContext()).hideBottomNav();
        } catch (Exception e) {
            e.printStackTrace();
        }

        txtEditProfile = view.findViewById(R.id.txtEditProfile);
        txtDeleteAccount = view.findViewById(R.id.txtDeleteAccount);
        txtName = view.findViewById(R.id.txtName);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtPhone = view.findViewById(R.id.txtPhone);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        ratingBar = view.findViewById(R.id.ratingBar);
        imgBack = view.findViewById(R.id.imgBack);
    }

    private void addEvents() {
        txtEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ProfileFragment.this)
                        .navigate(R.id.action_profileFragment_to_editProfileFragment);
            }
        });
        txtDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: DELETE ACCOUNT
                new AlertDialog.Builder(view.getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("XOÁ TÀI KHOẢN").setMessage("Bạn có chắc chắn muốn xoá tài khoản?")
                        .setPositiveButton("Tôi muốn xoá tài khoản", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MapActivity.currentUser.deleteAccount(
                                        getActivity(),
                                        ProfileFragment.this,
                                        MapActivity.currentUser.getUserName());
                                Toast.makeText(view.getContext(), "Activity closed",Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("Không", null).show();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ProfileFragment.this).popBackStack();
            }
        });
    }

    public void updateView() {
        txtName.setText(MapActivity.currentUser.getUserName());
        txtEmail.setText(MapActivity.currentUser.getUserEmail());
        txtPhone.setText(MapActivity.currentUser.getPhoneNumber() == null ?
                "9999999999" : MapActivity.currentUser.getPhoneNumber());
        ratingBar.setRating(MapActivity.currentUser.getEvaluation_score() * 10 / 2.0f);

        try {
            GlideApp.with(txtName.getContext())
                    .load(MapActivity.currentUser.getImgUrl())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(imgAvatar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(ProfileFragment.this).popBackStack();
    }
}
