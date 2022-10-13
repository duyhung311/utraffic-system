package com.hcmut.admin.utrafficsystem.util;

import com.hcmut.admin.utrafficsystem.business.UserLocation;

public class MovingDetection {
    private static final float MOVING_DISTANCE_LIMIT = 67.2f; // 1.39 (m) * 15 (s)

    private UserLocation prevLocation;
    private UserLocation currLocation;

    public void setCurrLocation(UserLocation currLocation) {
        this.currLocation = currLocation;
    }

    /**
     *
     * @return true if distance between two location greater than limited distance, else return false
     */
    public boolean isMoving() {
        if (prevLocation == null || currLocation == null) {
            prevLocation = currLocation;
            return true;
        }
        float realDistance = prevLocation.distanceTo(currLocation);
        boolean isMoving = realDistance > MOVING_DISTANCE_LIMIT;
        if (isMoving) {
            prevLocation = currLocation;
        }
        return isMoving;
    }

    /**
     * Calculate limited Distance for any time frame for two location
     * @return
     */
    private float meansureDistanceLimit() {
        float timeFrame = (float) DateConverter.caculateTimeFrame(
                prevLocation.getTimestamp(), currLocation.getTimestamp());
        float movingLimitUnit = 1.39f;
        return timeFrame * movingLimitUnit;
    }
}
