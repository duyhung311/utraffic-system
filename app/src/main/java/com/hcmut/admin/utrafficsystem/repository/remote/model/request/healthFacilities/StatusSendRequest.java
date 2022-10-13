package com.hcmut.admin.utrafficsystem.repository.remote.model.request.healthFacilities;


public class StatusSendRequest {

    private String id;
    private StatusSend statusSend;

    public StatusSendRequest(String id, Integer commentIndex) {
        this.id = id;
        this.statusSend = new StatusSend(commentIndex);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id != null && id.length() > 0) {
            this.id = id;
        }
    }

    public StatusSend getStatusSend() {
        return statusSend;
    }

    public void setStatusSend(StatusSend statusSend) {
        if (statusSend != null) {
            this.statusSend = statusSend;
        }
    }

    public static class StatusSend{
        private Integer comment;

        public StatusSend(Integer comment) {
            this.comment = comment;
        }
    }

}
