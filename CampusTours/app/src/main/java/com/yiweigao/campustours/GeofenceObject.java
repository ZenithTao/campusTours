package com.yiweigao.campustours;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by yiweigao on 4/14/15.
 */

public class GeofenceObject {
    private LatLng coordinates;
    private float radius;

    public GeofenceObject(String latitude, String longitude, String radius) {
        coordinates = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        this.radius = Float.parseFloat(radius);
    }
    
    public LatLng getCoordinates() {
        return coordinates;
    }
    
    public double getLat() {
        return coordinates.latitude;
    }
    
    public double getLng() {
        return coordinates.longitude;
    }

    public float getRadius() {
        return radius;
    }
}
