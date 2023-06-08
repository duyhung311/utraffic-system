package com.hcmut.admin.utraffictest.ui.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hcmut.admin.utraffictest.R;
import com.hcmut.admin.utraffictest.business.PhotoUploader;
import com.hcmut.admin.utraffictest.business.TrafficReportPhotoUploader;
import com.hcmut.admin.utraffictest.business.glide.GlideApp;
import com.hcmut.admin.utraffictest.ui.map.MapActivity;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment implements MapActivity.OnBackPressCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView imgBack;
    private RoundedImageView imgAvatar;
    private EditText txtName;
    private EditText txtPhone;
    private TextView txtSave;
    private TextView txtEmail;

    private PhotoUploader photoUploader;
    private ProgressDialog progressDialog;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
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
    public void onResume() {
        super.onResume();
        try {
            ((MapActivity) getActivity()).hideBottomNav();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addControls(view);
        addEvents();
    }

    private void addControls(View view) {
        imgBack = view.findViewById(R.id.imgBack);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        txtName = view.findViewById(R.id.txtName);
        txtPhone = view.findViewById(R.id.txtPhone);
        txtSave = view.findViewById(R.id.txtSave);
        txtEmail = view.findViewById(R.id.txtEmail);

        updateView(false);
    }

    private void addEvents() {
        photoUploader = new TrafficReportPhotoUploader(640, 720, new PhotoUploader.PhotoUploadCallback() {
            @Override
            public void onPreUpload() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                progressDialog = (getActivity() == null) ? null :
                        ProgressDialog.show(getActivity(), "", "Đang tải ảnh lên", true);
            }

            @Override
            public void onUpLoaded(Bitmap bitmap, String url) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                MapActivity.currentUser.updateUser(
                        getActivity(), EditProfileFragment.this, null, url, null);
            }

            @Override
            public void onUpLoadFail() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                try {
                    Toast.makeText(getContext(), "Không thể tải ảnh lên, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        final Context context = imgBack.getContext();
        if (context instanceof MapActivity) {
            MapActivity mapActivity = (MapActivity) context;
            mapActivity.registerCameraPhotoHandler(photoUploader);
        }
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(EditProfileFragment.this).popBackStack();
            }
        });
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoUploader.collectPhoto(getActivity());
            }
        });
        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapActivity.currentUser.updateUser(
                        getActivity(),
                        EditProfileFragment.this,
                        txtName.getText().toString(),
                        null,
                        txtPhone.getText().toString());
            }
        });
    }

    public void updateView(boolean isAvatarChange) {
        txtName.setText(MapActivity.currentUser.getUserName());
        txtEmail.setText(MapActivity.currentUser.getUserEmail());
        txtPhone.setText(MapActivity.currentUser.getPhoneNumber() == null ?
                "9999999999" : MapActivity.currentUser.getPhoneNumber());

        try {
            if (isAvatarChange) {   // clear old glide cache
                GlideApp.with(txtName.getContext()).clear(imgAvatar);
            }
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
        NavHostFragment.findNavController(EditProfileFragment.this).popBackStack();
    }
}
