package com.hcmut.admin.utraffictest.repository.remote.model.request.healthFacilities;


import com.hcmut.admin.utraffictest.model.HealthFacility;

public class CommentRequest {

    private String id;
    private HealthFacility.Comment comment;

    public CommentRequest(String id, HealthFacility.Comment comment) {
        this.id = id;
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id != null && id.length() > 0) {
            this.id = id;
        }
    }

    public HealthFacility.Comment getComment() {
        return comment;
    }

    public void setComment(HealthFacility.Comment comment) {
        if (comment != null) {
            this.comment = comment;
        }
    }
}
