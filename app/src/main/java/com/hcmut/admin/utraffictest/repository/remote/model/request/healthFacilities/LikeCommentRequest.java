package com.hcmut.admin.utraffictest.repository.remote.model.request.healthFacilities;


public class LikeCommentRequest {

    private String id;
    private Like like;

    public LikeCommentRequest(String id, Integer commentIndex) {
        this.id = id;
        this.like = new Like(commentIndex);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id != null && id.length() > 0) {
            this.id = id;
        }
    }

    public Like getLike() {
        return like;
    }

    public void setLike(Like like) {
        if (like != null) {
            this.like = like;
        }
    }

    public static class Like{
        private Integer comment;

        public Like(Integer comment) {
            this.comment = comment;
        }
    }

}
