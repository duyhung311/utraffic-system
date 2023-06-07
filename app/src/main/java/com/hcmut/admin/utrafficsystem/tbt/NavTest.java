package com.hcmut.admin.utrafficsystem.tbt;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class NavTest {
    public static final double[] coords = {
            10.72946, 106.72807,
            10.72939, 106.7279,
            10.72936, 106.72777,
            10.72935, 106.72767,
            10.72932, 106.72759,
            10.72931, 106.72754,
            10.72929, 106.72746,
            10.72923, 106.72744,
            10.72922, 106.72753,
            10.72923, 106.72761,
            10.72927, 106.72775,
            10.72931, 106.72789,
            10.72933, 106.72798,
            10.72934, 106.72806,
            10.72937, 106.72817,
            10.7294, 106.72828,
            10.72943, 106.7284,
    };
    public static final List<Location> locations;

    static {
        locations = new ArrayList<>(coords.length / 2) {{
            for (int i = 0; i < coords.length; i += 2) {
                Location location = new Location("");
                location.setLatitude(coords[i]);
                location.setLongitude(coords[i + 1]);
                add(location);
            }
        }};

        // generate first bearing is bearing to the next point, then others are bearing from previous point to current point
        float bearing = locations.get(0).bearingTo(locations.get(1));
        locations.get(0).setBearing(bearing);
        for (int i = 1; i < locations.size(); i++) {
            bearing = locations.get(i - 1).bearingTo(locations.get(i));
            locations.get(i).setBearing(bearing);
        }
    }
}
