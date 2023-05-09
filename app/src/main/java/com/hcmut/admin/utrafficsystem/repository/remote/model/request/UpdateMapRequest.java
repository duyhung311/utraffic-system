package com.hcmut.admin.utrafficsystem.repository.remote.model.request;

import com.hcmut.admin.utrafficsystem.model.osm.BoundingBox;

public class UpdateMapRequest {
    private BoundingBox newBound;
    private BoundingBox oldBound;

    public UpdateMapRequest(BoundingBox newBound, BoundingBox oldBound) {
        this.newBound = newBound;
        this.oldBound = oldBound;
    }

    public BoundingBox getNewBound() {
        return newBound;
    }

    public void setNewBound(BoundingBox newBound) {
        this.newBound = newBound;
    }

    public BoundingBox getOldBound() {
        return oldBound;
    }

    public void setOldBound(BoundingBox oldBound) {
        this.oldBound = oldBound;
    }
}
