package com.hcmut.admin.utraffictest.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 11/19/2018.
 */

public class TestingDB {
    public List<Cell> cellsList;
    public List<TrafficSegment> segmentsList;

    public TestingDB() {
        cellsList = new ArrayList<>();
        segmentsList = new ArrayList<>();

        //HVB
        segmentsList.add(new TrafficSegment("a", new LatLng(10.791838, 106.673132), new LatLng(10.791829, 106.673196), 1));
        segmentsList.add(new TrafficSegment("b", new LatLng(10.791794, 106.673629), new LatLng(10.791788, 106.673827), 2));
        //LTK
        segmentsList.add(new TrafficSegment("d", new LatLng(10.772656, 106.657556), new LatLng(10.772551, 106.657585), 1));
        segmentsList.add(new TrafficSegment("e", new LatLng(10.772551, 106.657585), new LatLng(10.772458, 106.657617), 3));
        segmentsList.add(new TrafficSegment("f", new LatLng(10.772458, 106.657617), new LatLng(10.772083, 106.657722), 2));
        segmentsList.add(new TrafficSegment("g", new LatLng(10.772083, 106.657722), new LatLng(10.771868, 106.657774), 1));
        //THT
        segmentsList.add(new TrafficSegment("h", new LatLng(10.772089, 106.657840), new LatLng(10.772136, 106.657980), 3));
        segmentsList.add(new TrafficSegment("i", new LatLng(10.772136, 106.657980), new LatLng(10.772187, 106.658113), 4));
        segmentsList.add(new TrafficSegment("j", new LatLng(10.772257, 106.658225), new LatLng(10.772331, 106.658363), 5));

        //10.791838, 106.673132
        //10.791829, 106.673196
        //10.791794, 106.673629
        //10.791788, 106.673827
        //cellsList.add(new Cell(LatLng()));


    }


}
