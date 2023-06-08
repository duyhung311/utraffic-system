package com.hcmut.admin.utraffictest.repository.remote.model.request;

public class RatingBody {
    public String report;
    public float score;

    public RatingBody(String report, float score) {
        this.report = report;
        this.score = score;
    }

    public RatingBody() {
    }
}
