package com.hcmut.admin.utrafficsystem.ui.atm;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.adapter.atm.AtmAdapter;
import com.hcmut.admin.utrafficsystem.business.MarkerCreating;
import com.hcmut.admin.utrafficsystem.business.SearchDirectionHandler;
import com.hcmut.admin.utrafficsystem.model.AndroidExt;
import com.hcmut.admin.utrafficsystem.model.Atm;
import com.hcmut.admin.utrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.DirectRespose;
import com.hcmut.admin.utrafficsystem.ui.direction.DirectionFragment;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.utrafficsystem.util.LocationCollectionManager;
import com.hcmut.admin.utrafficsystem.util.MapUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeAtmFragment extends Fragment implements AtmAdapter.AtmAdapterOnClickHandler, MapActivity.OnBackPressCallback {

    private FloatingActionButton btnShowAtm;

    AtmAdapter atmAdapter;
    AtmAdapter atmTopNumberAdapter;
    ArrayList<Atm> listAtm;
    ArrayList<Atm> listTopNumberAtm;
    Button btnNearestByDistance;
    Button btnNearestByTime;
    RecyclerView recListAtm;
    RecyclerView recListTopNumberAtm;
    Toolbar toolbar;
    AndroidExt androidExt = new AndroidExt();
    TextView txtSearchAtm;

//    private Atm atm;
    private Double minCost = Double.MAX_VALUE;
    private Atm nearestAtm;
    private List<Atm> listAtmNearYourLocation;

    private Iterator itr;
    private VisibleRegion visibleRegion;
    private MarkerCreating atmMarkerCreating;
    private MarkerCreating searchMarkerCreating;

    private GoogleMap map;

    @Override
    public void onResume() {
        super.onResume();
        try {
            MapActivity mapActivity = ((MapActivity) getContext());
            mapActivity.hideBottomNav();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MapActivity) {
            MapActivity mapActivity = (MapActivity) context;
            mapActivity.addMapReadyCallback(new MapActivity.OnMapReadyListener() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    map = googleMap;
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_atm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (Toolbar) view.findViewById(R.id.toolBarAllAtm);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Điểm đặt cây ATM");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPress();
            }
        });


//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            atm = bundle.getParcelable("atmInfo");
//        }

        btnShowAtm = (FloatingActionButton) view.findViewById(R.id.btnShowAtm);
        recListAtm = view.findViewById(R.id.recListAtm);
        recListTopNumberAtm = view.findViewById(R.id.recListTopNumberAtm);
        btnNearestByDistance = view.findViewById(R.id.btnNearestByDistance);
        btnNearestByTime = view.findViewById(R.id.btnNearestByTime);
        txtSearchAtm = view.findViewById(R.id.txtSearchAtm);

        setUpRecycleView();
        getAtm();
        addEvents();
    }

    private void setUpRecycleView() {
        LinearLayoutManager layoutTopAtmManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recListTopNumberAtm.setLayoutManager(layoutTopAtmManager);
        recListTopNumberAtm.setHasFixedSize(true);

//        DividerItemDecoration dividerTopAtmItemDecoration = new DividerItemDecoration(getContext(), layoutTopAtmManager.getOrientation());
//        recListTopNumberAtm.addItemDecoration(dividerTopAtmItemDecoration);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recListAtm.setLayoutManager(layoutManager);
        recListAtm.setHasFixedSize(true);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), layoutManager.getOrientation());
//        recListAtm.addItemDecoration(dividerItemDecoration);
    }


    private void getAtm() {
        listAtm = new ArrayList<>();
        listTopNumberAtm = new ArrayList<>();
        atmAdapter = new AtmAdapter(getContext(), listAtm, this, this);
        atmTopNumberAdapter = new AtmAdapter(getContext(), listTopNumberAtm, this, this);
        recListAtm.setAdapter(atmAdapter);
        recListTopNumberAtm.setAdapter(atmTopNumberAdapter);
        getTopNumberAtmList();
        getAtmList();
    }

    private void getTopNumberAtmList() {
        RetrofitClient.getAPIAtm().getTopNumberAtmList()
                .enqueue(new Callback<List<Atm>>() {
                    @Override
                    public void onResponse(Call<List<Atm>> call, Response<List<Atm>> response) {
                        if (response.body() != null) {
                            if (response.body() != null) {
                                List<Atm> atmResponse = response.body();
                                listTopNumberAtm.clear();
                                listTopNumberAtm.addAll(atmResponse);
                                atmTopNumberAdapter.notifyDataSetChanged();
                            } else {
                                androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                            }
                        } else {
                            androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Atm>> call, Throwable t) {
                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");
                    }
                });
    }

    private void getAtmList() {
        RetrofitClient.getAPIAtm().getAtmList()
                .enqueue(new Callback<List<Atm>>() {
                    @Override
                    public void onResponse(Call<List<Atm>> call, Response<List<Atm>> response) {
                        if (response.body() != null) {
                            if (response.body() != null) {
                                List<Atm> atmResponse = response.body();
                                listAtm.clear();
                                listAtm.addAll(atmResponse);
                                atmAdapter.notifyDataSetChanged();
                            } else {
                                androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                            }
                        } else {
                            androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Atm>> call, Throwable t) {
                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");
                    }
                });
    }

    private void addEvents() {

        txtSearchAtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(HomeAtmFragment.this)
                        .navigate(R.id.action_homeAtmFragment_to_searchAtmFragment);
            }
        });

        btnNearestByDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nearestDirection(false);
            }
        });

        btnNearestByTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nearestDirection(true);
            }
        });

        btnShowAtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomeAtmFragment.this).navigate(R.id.action_homeAtmFragment_to_mapFeatureFragment);
                if (MapUtil.checkGPSTurnOn(getActivity(), MapActivity.androidExt)) {
                    LocationCollectionManager.getInstance(getContext())
                            .getCurrentLocation(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        if (map != null) {
                                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                                    new LatLng(location.getLatitude(), location.getLongitude()), 50));
                                            showAtmNearYourLocation(location.getLatitude(), location.getLongitude());
                                        } else {
                                            Toast.makeText(getContext(),
                                                    "Bản đồ chưa được tải lên, vui lòng thử lại",
                                                    Toast.LENGTH_SHORT).show();
                                        }
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

    }

    private void showAtmNearYourLocation(double latitude, double longitude) {
        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "", "Đang tìm ATM gần bạn!", true);
        RetrofitClient.getAPIAtm().getAtmNearYourLocation(longitude, latitude)
                .enqueue(new Callback<List<Atm>>() {
                    @Override
                    public void onResponse(Call<List<Atm>> call, Response<List<Atm>> response) {
                        progressDialog.dismiss();
                        listAtmNearYourLocation = response.body();
                        showListAtmNearYourLocation(listAtmNearYourLocation);
                        setupMapToShowAtm();
                    }

                    @Override
                    public void onFailure(Call<List<Atm>> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Kết nối thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupMapToShowAtm() {
        if (MapUtil.checkGPSTurnOn(getActivity(), MapActivity.androidExt)) {
            LocationCollectionManager.getInstance(getContext())
                    .getCurrentLocation(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                if (map != null) {
                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 50));
                                    if (listAtmNearYourLocation.isEmpty()) {
                                        Toast.makeText(getContext(), "Không có atm phù hợp", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                } else {
                                    Toast.makeText(getContext(),
                                            "Bản đồ chưa được tải lên, vui lòng thử lại",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getContext(),
                                        "Không thể lấy vị trí, vui lòng thử lại",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        if (searchMarkerCreating != null) {
            searchMarkerCreating.removeMarker();
        }

        if(listAtmNearYourLocation == null){
            Toast.makeText(getContext(),"Không có ATM nào phù hợp", Toast.LENGTH_LONG).show();
            return;
        }

        itr = listAtmNearYourLocation.iterator();
        showAtmInVision(itr);

        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                itr = listAtmNearYourLocation.iterator();
            }
        });

        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                showAtmInVision(itr);
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                Bundle bundle = new Bundle();
                Atm atm = ((Atm)marker.getTag());
                bundle.putParcelable("infoAtm", atm);
                removeAllAtmMarkers();
                NavHostFragment.findNavController(HomeAtmFragment.this)
                        .navigate(R.id.action_mapFeatureFragment_to_AtmInforFragment, bundle);


//                if (MapUtil.checkGPSTurnOn(getActivity(), MapActivity.androidExt)) {
//                    LocationCollectionManager.getInstance(getContext())
//                            .getCurrentLocation(new OnSuccessListener<Location>() {
//                                @Override
//                                public void onSuccess(Location location) {
//                                    if (location != null) {
//                                        if (map != null) {
//                                            Atm atm;
//                                            Bundle bundle = new Bundle();
//                                            atm = (Atm) marker.getTag();
//                                            bundle.putString(DirectionFragment.CURRENT_ADDRESS, "Vị trí của bạn");
//                                            bundle.putParcelable(DirectionFragment.CURRENT_LATLNG, new LatLng(location.getLatitude(), location.getLongitude()));
//                                            bundle.putString(DirectionFragment.DESTINATION_ADDRESS, atm.getAddress());
//                                            bundle.putParcelable(DirectionFragment.DESTINATION_LATLNG, new LatLng(atm.getLatitude(), atm.getLongitude()));
//
//                                            NavHostFragment.findNavController(HomeAtmFragment.this)
//                                                    .navigate(R.id.action_mapFeatureFragment_to_directionFragment, bundle);
//
//                                            removeAllAtmMarkers();
//                                        } else {
//                                            Toast.makeText(getContext(),
//                                                    "Bản đồ chưa được tải lên, vui lòng thử lại",
//                                                    Toast.LENGTH_SHORT).show();
//                                        }
//                                    } else {
//                                        Toast.makeText(getContext(),
//                                                "Không thể lấy vị trí, vui lòng thử lại",
//                                                Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                }

                return false;
            }
        });
    }

    private void removeAllAtmMarkers() {
        listAtmNearYourLocation.clear();
        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {

            }
        });
        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

            }
        });
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });
        map.clear();
    }

    private void showAtmInVision(Iterator itr) {
        visibleRegion = map.getProjection().getVisibleRegion();
        Atm atm;
        LatLng latLng;
        while (itr.hasNext()) {
            atm = (Atm) itr.next();
            latLng = atm.getLatLng();
            if (latLng != null) {
                if (visibleRegion.latLngBounds.contains(latLng)) {
                    atmMarkerCreating = new MarkerCreating(latLng);
                    atmMarkerCreating.createAtmMarker(getContext(), map, R.drawable.ic_atm, false, false, atm, 0);
                    itr.remove();
                }
            } else {
                Toast.makeText(getContext(), "Kết nối thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void nearestDirection(final boolean isDirectionByTime){
        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "", "Đang tìm ATM gần nhất!", true);
        if (MapUtil.checkGPSTurnOn(getActivity(), MapActivity.androidExt)) {
            LocationCollectionManager.getInstance(getContext())
                    .getCurrentLocation(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            final double latitude = location.getLatitude();
                            final double longitude = location.getLongitude();
                            RetrofitClient.getAPIAtm().getAtmNearYourLocation(longitude, latitude)
                                    .enqueue(new Callback<List<Atm>>() {
                                        @Override
                                        public void onResponse(Call<List<Atm>> call, Response<List<Atm>> response) {
                                            listAtmNearYourLocation = response.body();
                                            findNearestAtm(listAtmNearYourLocation,isDirectionByTime,new LatLng(latitude,longitude),progressDialog);
                                        }

                                        @Override
                                        public void onFailure(Call<List<Atm>> call, Throwable t) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getContext(), "Kết nối thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
        }
    }

    private void findNearestAtm(List<Atm> listNearestAtms, final boolean isDirectionByTime, final LatLng startPoint, final ProgressDialog progressDialog){
        final Iterator<Atm> iterator = listNearestAtms.iterator();
        while (iterator.hasNext()) {
            final Atm atm = iterator.next();
            LatLng endPoint = new LatLng(atm.getLatitude(), atm.getLongitude());
            SearchDirectionHandler.direct(
                    getContext(),
                    startPoint,
                    endPoint,
                    isDirectionByTime,
                    new SearchDirectionHandler.DirectResultCallback() {
                        @Override
                        public void onSuccess(DirectRespose directRespose) {
                            if(isDirectionByTime){
                                if (minCost > directRespose.getTime()) {
                                    minCost = directRespose.getTime();
                                    nearestAtm = atm;
                                }
                            }else{
                                if (minCost > directRespose.getDistance()) {
                                    minCost = (double)directRespose.getDistance();
                                    nearestAtm = atm;
                                }
                            }
                            if (!iterator.hasNext()) {
                                // Hien thi duong di
                                if(nearestAtm!=null){
                                    Bundle bundle = new Bundle();
                                    bundle.putString(DirectionFragment.CURRENT_ADDRESS,"Vị trí của bạn");
                                    bundle.putParcelable(DirectionFragment.CURRENT_LATLNG, new LatLng(startPoint.latitude, startPoint.longitude));

                                    bundle.putString(DirectionFragment.DESTINATION_ADDRESS,nearestAtm.getAddress());
                                    bundle.putParcelable(DirectionFragment.DESTINATION_LATLNG, new LatLng(nearestAtm.getLatitude(),nearestAtm.getLongitude()));

                                    progressDialog.dismiss();
                                    NavHostFragment.findNavController(HomeAtmFragment.this).navigate(R.id.action_homeAtmFragment_to_directionFragment,bundle);
                                }else{
                                    Toast.makeText(getContext(), "Không tìm được ATM gần nhất", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            }

                        }

                        @Override
                        public void onFail() {

                        }

                        @Override
                        public void onHaveNoData() {

                        }
                    });

        }
    }

    private void showListAtmNearYourLocation(List<Atm> listAtmNearYourLocation) {
        BottomSheetAtmListFragment btsAtmList = new BottomSheetAtmListFragment(HomeAtmFragment.this, listAtmNearYourLocation, map);
        btsAtmList.show(getFragmentManager(), btsAtmList.getTag());
    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(HomeAtmFragment.this).popBackStack();
    }

    @Override
    public void onClick(Atm atm) {
    }
}