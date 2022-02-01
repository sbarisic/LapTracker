package hr.vub.laptracker;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

import hr.vub.laptracker.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;

    public Context ctx = null;
    public GPSLocation loc = null;
    public LapTrackerDb db = null;
    public FirstFragment firstFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load/initialize the osmdroid configuration, this can be done
        ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        stopLogic();
        createGPSandDB();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    void createGPSandDB() {
        db = LapTrackerDb.getDatabase(this);
        loc = new GPSLocation(ctx, this);
        loc.requestLocationUpdates();
    }

    public void updateLocation(Location pos) {
        if (map == null)
            return;
        //Log.w("LAPTRACKER", "updateLocation");

        GeoPoint geoPos = new GeoPoint(pos.getLatitude(), pos.getLongitude(), pos.getAltitude());
        updateLogic(geoPos);
    }

    boolean logicPaused = false;
    boolean recordingMode = false;
    boolean raceMode = false;

    MapView map = null;
    Track selectedTrack = null;

    Marker curPosMarker = null;
    Marker startMarker = null;
    Marker endMarker = null;
    Polyline line = null;

    long startTime = 0;
    long endTime = 0;
    int timeDelta = 0;

    List<GeoPoint> curTrack = null;

    void removeMarkers() {
        if (map != null)
            map.getOverlays().clear();

        line = null;
        curPosMarker = null;
        startMarker = null;
        endMarker = null;
    }

    public void stopLogic() {
        if (map != null) {
            removeMarkers();
        }

        startTime = 0;
        endTime = 0;
        timeDelta = 0;

        map = null;
        line = null;
        curTrack = null;

        selectedTrack = null;
        curPosMarker = null;
        startMarker = null;
        endMarker = null;

        recordingMode = false;
        raceMode = false;
        logicPaused = true;
    }

    public void startRecordingMode() {
        db.trackDAO().clearSelectedTrack();

        startTime = System.currentTimeMillis();
        curTrack = null;
        recordingMode = true;
        logicPaused = false;
    }

    public void stopRecordingModeAndSave() {
        endTime = System.currentTimeMillis();
        recordingMode = false;
        logicPaused = true;
    }

    public void startRaceMode() {
        raceMode = true;
        logicPaused = false;
    }

    public void stopRaceMode() {
        raceMode = false;
        logicPaused = true;
    }

    public void load(MapView inMap) {
        ctx = getApplicationContext();

        if (db == null && loc == null)
            createGPSandDB();

        TrackDAO dao = db.trackDAO();
        selectedTrack = dao.getSelectedTrack();
        Location curLoc = loc.getLocation();

        inMap.getOverlays().clear();
        inMap.setTileSource(TileSourceFactory.MAPNIK);
        inMap.setBuiltInZoomControls(true);
        inMap.setMultiTouchControls(true);

        IMapController mapController = inMap.getController();
        mapController.setZoom(15);
        mapController.setCenter(new GeoPoint(curLoc.getLatitude(), curLoc.getLongitude()));

        map = inMap;
        removeMarkers();

        // color it
        // https://github.com/osmdroid/osmdroid/blob/master/OpenStreetMapViewer/src/main/java/org/osmdroid/samplefragments/drawing/ShowAdvancedPolylineStyles.java#L125-L180
        // https://github.com/osmdroid/osmdroid/issues/1726

        if (selectedTrack != null) {
            List<TrackPoint> trackPoints = db.trackDAO().getPointsForTrack(selectedTrack.id);
            curTrack = new ArrayList<>();

            for (int i = 0; i < trackPoints.size(); i++) {
                curTrack.add(trackPoints.get(i).toGeoPoint());
            }

            mapController.setCenter(curTrack.get(0));
            updateLine(curTrack);
        }
    }

    void updateLine(List<GeoPoint> points) {
        if (map == null)
            return;

        if (line == null) {
            line = new Polyline();
            map.getOverlays().add(line);
        }

        line.setPoints(points);

        // Start marker
        if (startMarker == null) {
            startMarker = new Marker(map);
            startMarker.setAnchor(0.2f, 0.2f);
            startMarker.setIcon(AppCompatResources.getDrawable(ctx, R.drawable.circle_green));
            map.getOverlays().add(startMarker);
        }

        // End marker
        if (endMarker == null) {
            endMarker = new Marker(map);
            endMarker.setAnchor(0.2f, 0.2f);
            endMarker.setIcon(AppCompatResources.getDrawable(ctx, R.drawable.circle_red));
            map.getOverlays().add(endMarker);
        }

        startMarker.setPosition(points.get(0));
        endMarker.setPosition(points.get(points.size() - 1));
    }

    public void updateLogic(GeoPoint curLoc) {
        if (map == null)
            return;

        //Log.w("LAPTRACKER", "updateLogic");

        if (curPosMarker == null) {
            curPosMarker = new Marker(map);
            curPosMarker.setAnchor(0.2f, 0.2f);
            curPosMarker.setIcon(AppCompatResources.getDrawable(ctx, R.drawable.circle_blue));

            map.getOverlays().add(curPosMarker);
        }

        curPosMarker.setPosition(curLoc);

        if (logicPaused)
            return;

        // Center map to "player"
        IMapController mapController = map.getController();
        mapController.setCenter(curLoc);

        if (recordingMode) {
            if (curTrack == null)
                curTrack = new ArrayList<>();

            curTrack.add(curLoc);
            updateLine(curTrack);
        } else {
            if (curTrack != null) {
                int len = curTrack.size();

                double closestDist = 99999;
                int closestIdx = -1;

                for (int i = 0; i < len; i++) {
                    double dist = curTrack.get(i).distanceToAsDouble(curLoc);

                    if (dist < closestDist) {
                        closestDist = dist;
                        closestIdx = i;
                    }
                }

                if (closestIdx >= 0 && closestDist < 5) {
                    if (startTime == 0) {
                        startTime = System.currentTimeMillis();
                        Log.w("LAPTRACKER", "Start!");
                    }

                    if (closestIdx == len - 1) {
                        if (endTime == 0)
                            endTime = System.currentTimeMillis();

                        Log.w("LAPTRACKER", "Finish 1!");
                    }

                    Log.w("LAPTRACKER", "Idx = " + closestIdx);
                    Log.w("LAPTRACKER", "Dist = " + closestDist);

                    int delta = (int) (System.currentTimeMillis() - startTime);
                    firstFragment.updateTime(delta);
                }

                if (closestIdx == len - 1 && closestDist < 20) {
                    if (endTime == 0)
                        endTime = System.currentTimeMillis();

                    Log.w("LAPTRACKER", "Finish 2!");
                }

                if (startTime != 0 && endTime != 0) {
                    timeDelta = (int) (endTime - startTime);
                    stopRaceMode();
                    calculateAndSaveBestTime();
                }
            }
        }
    }

    void calculateAndSaveBestTime() {
        int oldBestTime = selectedTrack.best_time_ms;

        if (timeDelta < oldBestTime || oldBestTime == 0) {
            selectedTrack.best_time_ms = timeDelta;
            selectedTrack.calcAvgSpeed();

            db.trackDAO().update(selectedTrack);

            firstFragment.finishRace(timeDelta, true);
        } else {
            firstFragment.finishRace(timeDelta, false);
        }
    }

    /*static int measureDistance(List<GeoPoint> points) {
        double dist = 0;
        GeoPoint prevPoint = points.get(0);

        for (int i = 1; i < points.size(); i++) {
            GeoPoint curPoint = points.get(i);
            dist += prevPoint.distanceToAsDouble(curPoint);
            prevPoint = curPoint;
        }

        return (int) dist;
    }*/

    @Override
    public void onResume() {
        super.onResume();
        Log.w("LAPTRACKER", "MainActivity onResume");

        if (map != null) {
            //this will refresh the osmdroid configuration on resuming.
            //if you make changes to the configuration, use
            //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
            map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.w("LAPTRACKER", "MainActivity onPause");

        if (map != null) {
            //this will refresh the osmdroid configuration on resuming.
            //if you make changes to the configuration, use
            //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            //Configuration.getInstance().save(this, prefs);
            map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}