package com.hcmut.admin.utraffictest.repository.remote.model.request.healthFacilities;

public class RatingRequest {

    private String id;
    private float rate;

    public RatingRequest(String id, float rate) {
        this.id = id;
        this.rate = rate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id != null && id.length() > 0) {
            this.id = id;
        }
    }

    public float getRating() {
        return rate;
    }

    public void setRating(float rate) {
            this.rate = rate;
    }
}
