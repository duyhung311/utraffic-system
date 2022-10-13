package com.hcmut.admin.utrafficsystem.ui.reportdetail.traffic.ratinglist;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.ReportResponse;
import com.hcmut.admin.utrafficsystem.business.ImageDownloader;
import com.hcmut.admin.utrafficsystem.business.ImageListSmartDownloader;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;

public class RatingViewHolder extends RecyclerView.ViewHolder {

    private RatingAdapter.OnItemClickedListener onItemClickedListener;
    private ReportResponse currReportData;

    private LinearLayout img_list;
    private Button btnRating;
    private ImageView imgAvatar;
    private TextView tvSpeed, tvReason, tvName, tvScore;

    public RatingViewHolder(View itemView, RatingAdapter.OnItemClickedListener onItemClickedListener) {
        super(itemView);
        this.onItemClickedListener = onItemClickedListener;

        btnRating = itemView.findViewById(R.id.btn_rating);
        img_list = itemView.findViewById(R.id.img_list);
        imgAvatar = itemView.findViewById(R.id.img_avatar);
        tvSpeed = itemView.findViewById(R.id.tv_speed);
        tvReason = itemView.findViewById(R.id.tv_reason);
        tvName = itemView.findViewById(R.id.tv_name);
        tvScore = itemView.findViewById(R.id.tv_score);
    }

    public void bindData(ReportResponse reportData) {
        if (reportData == null) {
            return;
        }

        currReportData = reportData;
        try {
            if (reportData.getUser().getId().equals(MapActivity.currentUser.getUserId())) {
                btnRating.setEnabled(false);
            }
            tvName.setText(reportData.getUser().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ImageListSmartDownloader.Builder builder = new ImageListSmartDownloader.Builder(img_list);
            for (String imageUrl : reportData.getImages()) {
                builder.addImageUrl(imageUrl);
            }
            builder.build().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            tvSpeed.setText(reportData.getVelocity() + " km/h");
            tvReason.setText(reportData.getCauses().get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            new ImageDownloader(imgAvatar).execute(reportData.getUser().getAvatar());
        }catch (Exception e) {
            e.printStackTrace();
        }
        try {
            tvScore.setText(String.valueOf(reportData.getUser().getReputation()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickedListener != null && currReportData != null) {
                    onItemClickedListener.onItemClicked(btnRating.getId(), v, currReportData);
                }
            }
        });
        img_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickedListener.onItemClicked(img_list.getId(), v, currReportData);
            }
        });
    }
}
