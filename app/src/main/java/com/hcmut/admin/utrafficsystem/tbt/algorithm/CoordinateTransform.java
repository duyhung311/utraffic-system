package com.hcmut.admin.utrafficsystem.tbt.algorithm;

import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransformFactory;
import org.osgeo.proj4j.ProjCoordinate;

public class CoordinateTransform {
    private static final CRSFactory csFactory = new CRSFactory();
    private static final CoordinateReferenceSystem wgs84 = csFactory.createFromName("EPSG:4326");
    private static final CoordinateReferenceSystem customCRS = csFactory.createFromParameters(null, "+proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0 +y_0=0.0 +k=1.0 +units=m +nadgrids=@null +wktext +no_defs");
    private static final CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
    private static final org.osgeo.proj4j.CoordinateTransform transform = ctFactory.createTransform(wgs84, customCRS);
    private static final org.osgeo.proj4j.CoordinateTransform inverseTransform = ctFactory.createTransform(customCRS, wgs84);

    public static ProjCoordinate wgs84ToWebMercator(double latitude, double longitude) {
        ProjCoordinate srcCoord = new ProjCoordinate(longitude, latitude);
        ProjCoordinate dstCoord = new ProjCoordinate();
        transform.transform(srcCoord, dstCoord);

        return dstCoord;
    }

    public static ProjCoordinate webMercatorToWgs84(double x, double y) {
        ProjCoordinate srcCoord = new ProjCoordinate(x, y);
        ProjCoordinate dstCoord = new ProjCoordinate();
        inverseTransform.transform(srcCoord, dstCoord);

        return dstCoord;
    }

    public static float getScalePixel(float scaleDenominator) {
        return 1 / (scaleDenominator * 0.001f * 0.28f);
    }
}
