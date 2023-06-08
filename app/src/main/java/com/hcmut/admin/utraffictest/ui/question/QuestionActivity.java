package com.hcmut.admin.utraffictest.ui.question;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hcmut.admin.utraffictest.R;

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        TextView title = findViewById(R.id.title_question);
        title.setText("Hướng dẫn");
        findViewById(R.id.ivBack).setOnClickListener(this);
        findViewById(R.id.btn_report_question).setOnClickListener(this);
        findViewById(R.id.btn_rating_question).setOnClickListener(this);
        findViewById(R.id.btn_traffic_status_question).setOnClickListener(this);
        findViewById(R.id.btn_find_direction_question).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_report_question:
                startActivityDetail("Gửi cảnh báo", "report.html");
                break;
            case R.id.btn_rating_question:
                startActivityDetail("Đánh giá", "rating.html");
                break;
            case R.id.btn_traffic_status_question:
                startActivityDetail("Thông tin giao thông", "status.html");
                break;
            case R.id.btn_find_direction_question:
                startActivityDetail("Tìm kiếm đường đi", "search.html");
                break;
            case R.id.ivBack:
                onBackPressed();
                break;
        }
    }

    private void startActivityDetail(String title, String url){
        Intent intent = new Intent(this, QuestionDetailActivity.class);
        intent.putExtra("TITLE", title);
        intent.putExtra("URL", url);
        startActivity(intent);
    }
}
