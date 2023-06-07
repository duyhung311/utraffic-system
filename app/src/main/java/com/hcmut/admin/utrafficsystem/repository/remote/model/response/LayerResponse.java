package com.hcmut.admin.utrafficsystem.repository.remote.model.response;

import androidx.annotation.NonNull;

public class LayerResponse {
    public final NodeResponse[] nodes;
    public final WayResponse[] ways;

    public LayerResponse(NodeResponse[] nodes, WayResponse[] ways) {
        this.nodes = nodes;
        this.ways = ways;
    }

    @NonNull
    @Override
    public String toString() {
        return "LayerResponse{" +
                "nodes length=" + nodes.length +
                ", ways length=" + ways.length +
                '}';
    }
}
