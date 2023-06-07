package com.hcmut.admin.utrafficsystem.tbt.drawable;

import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.hcmut.admin.utrafficsystem.repository.remote.API.APIService;
import com.hcmut.admin.utrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.utrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.Coord;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.DirectResponse;
import com.hcmut.admin.utrafficsystem.tbt.algorithm.CoordinateTransform;
import com.hcmut.admin.utrafficsystem.tbt.algorithm.Navigation;
import com.hcmut.admin.utrafficsystem.tbt.mapnik.symbolizer.LineSymbolizer;
import com.hcmut.admin.utrafficsystem.tbt.mapnik.symbolizer.SymMeta;
import com.hcmut.admin.utrafficsystem.tbt.mapnik.symbolizer.Symbolizer;
import com.hcmut.admin.utrafficsystem.tbt.osm.Node;
import com.hcmut.admin.utrafficsystem.tbt.osm.Way;
import com.hcmut.admin.utrafficsystem.tbt.utils.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NewApi")
public class Route {
    private static final String TAG = Route.class.getSimpleName();
    private static final float BUFFER_DISTANCE_TO_ACTUAL_SIZE_RATIO = 0.75f;
    private final Config config;
    private final Location destination;
    private final boolean isTimeDirectionSelected;
    private final ReentrantLock lock = new ReentrantLock();
    private SymMeta symMeta;
    private List<Coord> route = new ArrayList<>(0);
    private float routeTime = 0;
    private float routeDistance = 0;
    private boolean isRequestingRoute = false;
    private BiConsumer<Float, Float> onRouteChanged = (disInMeter, timeInMin) -> {
    };

    public Route(Config config, Location destination, boolean isTimeDirectionSelected) {
        this.config = config;
        this.destination = destination;
        this.isTimeDirectionSelected = isTimeDirectionSelected;
    }

    public void setOnRouteChanged(BiConsumer<Float, Float> onRouteChanged) {
        this.onRouteChanged = onRouteChanged;
    }

    public Location setCurLocation(Location currentLocation) {
        lock.lock();
        Pair<Location, Float> result = Navigation.isLocationNearPolyline(currentLocation, route);
        Location curPoint = result.first;
        if (curPoint == null && !isRequestingRoute) {
            symMeta = null;
            onRouteChanged.accept(null, null);
            requestRoute(currentLocation, destination);
            lock.unlock();
            return currentLocation;
        }
        Float distance = Float.isNaN(result.second) ? null : (1 - result.second) * routeDistance;
        Float time = Float.isNaN(result.second) ? null : (1 - result.second) * routeTime;
        onRouteChanged.accept(distance, time);
        lock.unlock();
        return curPoint;
    }

    private void requestRoute(Location source, Location destination) {
        Log.d(TAG, "Requesting route" + source + " -> " + destination);
        isRequestingRoute = true;
        APIService apiService = RetrofitClient.getApiService();
        String type = isTimeDirectionSelected ? "time" : "distance";
        apiService.getFindDirect(source.getLatitude(), source.getLongitude(), destination.getLatitude(), destination.getLongitude(), type).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse<List<DirectResponse>>> call, @NonNull Response<BaseResponse<List<DirectResponse>>> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    List<DirectResponse> directResponses = response.body().getData();
                    if (directResponses != null && directResponses.size() > 0) {
                        DirectResponse directResponse = directResponses.get(0);
                        parseRoute(source, directResponse);
                    }
                    setCurLocation(source);
                }
                isRequestingRoute = false;
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse<List<DirectResponse>>> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                isRequestingRoute = false;
            }
        });
    }

    private void parseRoute(Location source, DirectResponse directResponse) {
        if (directResponse == null || directResponse.getCoords() == null || directResponse.getCoords().size() == 0) {
            Toast.makeText(config.context, "Đoạn đường không được hỗ trợ hoặc" +
                            " Không thể tìm thấy đường đến đó, vui lòng thử lại!", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        SymMeta fillSymMeta = null;
        SymMeta caseSymMeta = null;
        route = new ArrayList<>(directResponse.getCoords().size() + 1);
        routeDistance = directResponse.getDistance();
        routeTime = (float) directResponse.getTime();

        Coord firstCoord = directResponse.getCoords().get(0);
        route.add(new Coord() {{
            setLat(source.getLatitude());
            setLng(source.getLongitude());
            seteLat(firstCoord.getLat());
            seteLng(firstCoord.getLng());
            setStreet(new SegmentStreet() {{
                name = "Start";
                type = "unclassified";
            }});
        }});
        route.addAll(directResponse.getCoords());
        for (Coord coord : directResponse.getCoords()) {
            Node node = new Node((float) coord.getLng(), (float) coord.getLat());
            Node enode = new Node((float) coord.geteLng(), (float) coord.geteLat());
            String color = coord.getStatus().color;
            String type = coord.getStreet().type;
            float scaled = CoordinateTransform.getScalePixel(config.getScaleDenominator());
            double width = Navigation.bufferDistanceMap.getOrDefault(type, 5.0) * scaled * BUFFER_DISTANCE_TO_ACTUAL_SIZE_RATIO;

            String fillStrokeWidth = String.valueOf(width);
            String caseStrokeWidth = String.valueOf(width + 4);

            Way way = new Way(List.of(node, enode));
            LineSymbolizer fillLineSymbolizer = new LineSymbolizer(config, fillStrokeWidth, color, null, "butt", "round", null, null);

            String caseColor = Symbolizer.darkenColor(color, 1, 0.5f);
            LineSymbolizer caseLineSymbolizer = new LineSymbolizer(config, caseStrokeWidth, caseColor, null, "butt", "round", null, null);

            SymMeta localFillSymMeta = fillLineSymbolizer.toDrawable(way, null);
            SymMeta localCaseSymMeta = caseLineSymbolizer.toDrawable(way, null);
            if (fillSymMeta == null) {
                fillSymMeta = localFillSymMeta;
                caseSymMeta = localCaseSymMeta;
            } else {
                fillSymMeta = fillSymMeta.append(localFillSymMeta);
                caseSymMeta = caseSymMeta.append(localCaseSymMeta);
            }
        }

        if (caseSymMeta != null) {
            symMeta = caseSymMeta.append(fillSymMeta);
            symMeta.save(config);
        }
    }

    public void draw() {
        if (symMeta != null) {
            symMeta.draw();
        }
    }
}
