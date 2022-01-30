package hr.vub.laptracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

public class GPSLocation extends Service implements LocationListener {
    private final Context mContext;
    private final MainActivity act;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    float bearing; // bearing

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSLocation(Context context, MainActivity mainAct) {
        this.mContext = context;
        this.act = mainAct;
    }

    public Boolean requestLocationUpdates() {
        Log.d("LAPTRACKER", "requestLocationUpdates");
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

        try {
            Log.d("LAPTRACKER", "Requesting GPS location updates");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, GPSLocation.this);
        } catch (SecurityException e) {
            Log.d("LAPTRACKER", "Oh noez! " + e);
            return false;
        }

        return true;
    }

    public Location getLocation() {
        Log.d("LAPTRACKER", "GPSLocation getLocation");

        if (location == null) {
            try {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } catch (SecurityException e) {
                Log.d("LAPTRACKER", "Oh noez! " + e);
                return null;
            }
        }

        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            bearing = location.getBearing();
        }

        return location;
    }

    @Override
    public void onLocationChanged(Location location) {
        act.updateLocation(location);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}