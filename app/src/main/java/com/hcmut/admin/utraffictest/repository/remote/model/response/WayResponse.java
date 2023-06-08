package com.hcmut.admin.utraffictest.repository.remote.model.response;

import java.util.HashMap;

public class WayResponse {
    public final long id;
    public final long[] refs;
    public final HashMap<String, HashMap<String, String>> tags;

    public WayResponse(long id, long[] refs, HashMap<String, HashMap<String, String>> tags) {
        this.id = id;
        this.refs = refs;
        this.tags = tags;
    }
}
