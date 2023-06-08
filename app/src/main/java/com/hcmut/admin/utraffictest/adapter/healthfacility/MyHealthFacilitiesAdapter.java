package com.hcmut.admin.utraffictest.adapter.healthfacility;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.hcmut.admin.utraffictest.R;
import com.hcmut.admin.utraffictest.business.SearchPlaceHandler;
import com.hcmut.admin.utraffictest.model.HealthFacility;
import com.hcmut.admin.utraffictest.repository.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyHealthFacilitiesAdapter extends RecyclerView.Adapter<MyHealthFacilitiesAdapter.MyHealthFacilitiesViewHolder> {

    private List<HealthFacility> list;
    private Context context;

    private String[] allSpecialisations;
    private boolean[] checkedSpecialisations;
    private ArrayList<Integer> chosenSpecialisations = new ArrayList<>();
    private ProgressDialog progressDialog;

    public MyHealthFacilitiesAdapter(List<HealthFacility> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHealthFacilitiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_health_facility,parent,false);
        return new MyHealthFacilitiesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHealthFacilitiesViewHolder holder, int position) {
        final HealthFacility registeredHealthFacility = list.get(position);
        if(registeredHealthFacility == null){
            return;
        }

        holder.edtNameHF.setText(registeredHealthFacility.getName());
        holder.edtAddressHF.setText(registeredHealthFacility.getAddress());
        holder.edtWorkTimeHF.setText(registeredHealthFacility.getWork_time());
        holder.tvSpecialisationHF.setText(Arrays.toString( registeredHealthFacility.getSpecialisation() ).replace("[","").replace("]",""));
        holder.edtServiceHF.setText(registeredHealthFacility.getService());
        holder.edtPhoneNumberHF.setText(registeredHealthFacility.getPhone_number());

        holder.tvSpecialisationHF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RetrofitClient.getAPIHealthFacilities().getAllSpecialisation().enqueue(new Callback<List<String>>() {
                        @Override
                        public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                            allSpecialisations = response.body().toArray(new String[0]);
                            checkedSpecialisations = new boolean[allSpecialisations.length];
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                                    holder.tvSpecialisationHF.setText(stringBuilder.toString());
                                }
                            });
                            builder.show();
                        }

                        @Override
                        public void onFailure(Call<List<String>> call, Throwable t) {
                            Toast.makeText(context,"Kết nốt thất bại, vui lòng thử lại",Toast.LENGTH_LONG);
                        }
                    });

                }
            });

        holder.btnUpdateHF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(context, "", "Vui lòng chờ", true);

                String health_facility_id = registeredHealthFacility.getId();
                String name = holder.edtNameHF.getText().toString();
                String address = holder.edtAddressHF.getText().toString();
                String work_time = holder.edtWorkTimeHF.getText().toString();
                List<String> specialisation = new ArrayList<String>(Arrays.asList(String.valueOf(holder.tvSpecialisationHF.getText()).split(",")));
                String service = holder.edtServiceHF.getText().toString();
                String phone_number = holder.edtPhoneNumberHF.getText().toString();
                LatLng latLng = SearchPlaceHandler.getLatLngFromAddressTextInput(context, address);
                if (latLng != null) {
                    Double latitude = latLng.latitude;
                    Double longitude = latLng.longitude;
                    RetrofitClient.getAPIHealthFacilities().updateHealthFacility(
                            health_facility_id,
                            name,
                            address,
                            work_time,
                            specialisation,
                            service,
                            phone_number,
                            latitude,
                            longitude
                    ).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            progressDialog.dismiss();
                            Toast.makeText(context,"Cập nhật thành công",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(context,"Cập nhật thất bại, vui lòng thử lại",Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(context, "Không tìm được địa chỉ, vui lòng nhập lại.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        if(list != null){
            return list.size();
        }
        return 0;
    }

    public class MyHealthFacilitiesViewHolder extends RecyclerView.ViewHolder{
        private EditText edtNameHF;
        private EditText edtAddressHF;
        private EditText edtWorkTimeHF;
        private TextView tvSpecialisationHF;
        private EditText edtServiceHF;
        private EditText edtPhoneNumberHF;
        private AppCompatButton btnUpdateHF;

        public MyHealthFacilitiesViewHolder(@NonNull View itemView) {
            super(itemView);
            edtNameHF = itemView.findViewById(R.id.edtNameHF);
            edtAddressHF = itemView.findViewById(R.id.edtAddressHF);
            edtWorkTimeHF = itemView.findViewById(R.id.edtWorkTimeHF);
            tvSpecialisationHF = itemView.findViewById(R.id.tvSpecialisationHF);
            edtServiceHF = itemView.findViewById(R.id.edtServiceHF);
            edtPhoneNumberHF = itemView.findViewById(R.id.edtPhoneNumberHF);
            btnUpdateHF = itemView.findViewById(R.id.btnUpdateHF);
        }
    }
}
