package com.hcmut.admin.utraffictest.repository.remote.model.request;

import com.hcmut.admin.utraffictest.repository.remote.API.APITurnByTurn;
import com.hcmut.admin.utraffictest.repository.remote.RetrofitClient;
import com.hcmut.admin.utraffictest.repository.remote.model.BaseResponse;
import com.hcmut.admin.utraffictest.repository.remote.model.response.LayerResponse;

import retrofit2.Callback;

public class LayerRequest {
    private static class Bound {
        public final float minLon;
        public final float maxLon;
        public final float minLat;
        public final float maxLat;

        public Bound(float minLon, float minLat, float maxLon, float maxLat) {
            this.minLon = minLon;
            this.maxLon = maxLon;
            this.minLat = minLat;
            this.maxLat = maxLat;
        }
    }

    public final Bound bound;

    public LayerRequest(float minLon, float minLat, float maxLon, float maxLat) {
        this.bound = new Bound(minLon, minLat, maxLon, maxLat);
    }

    public LayerRequest(float[] bound) {
        this(bound[0], bound[1], bound[2], bound[3]);
    }

    public void post(Callback<BaseResponse<LayerResponse>> callback) {
        APITurnByTurn apiTurnByTurn = RetrofitClient.getApiTurnByTurn();
        apiTurnByTurn.postGetLayer(this).enqueue(callback);
    }
}
