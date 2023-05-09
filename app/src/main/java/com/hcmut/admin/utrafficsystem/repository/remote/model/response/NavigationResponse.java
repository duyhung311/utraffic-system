package com.hcmut.admin.utrafficsystem.repository.remote.model.response;

import com.hcmut.admin.utrafficsystem.model.osm.data.Node;
import com.hcmut.admin.utrafficsystem.model.osm.data.Way;

public class NavigationResponse {
    Node nodesFromNewBound;
    Way wayFromNewBound;
    boolean isChanging;

    public Node getNodesFromNewBound() {
        return nodesFromNewBound;
    }

    public void setNodesFromNewBound(Node nodesFromNewBound) {
        this.nodesFromNewBound = nodesFromNewBound;
    }

    public Way getWayFromNewBound() {
        return wayFromNewBound;
    }

    public void setWayFromNewBound(Way wayFromNewBound) {
        this.wayFromNewBound = wayFromNewBound;
    }

    public boolean isChanging() {
        return isChanging;
    }

    public void setChanging(boolean changing) {
        isChanging = changing;
    }
}
