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
    public MapView map = null;
    public GPSLocation loc = null;

    public LapTrackerDb db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load/initialize the osmdroid configuration, this can be done
        ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        db = LapTrackerDb.getDatabase(this);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    public void updateLocation(Location pos) {
        if (map == null)
            return;
        Log.w("LAPTRACKER", "updateLocation");

        GeoPoint geoPos = new GeoPoint(pos.getLatitude(), pos.getLongitude());
        updateLogic(geoPos);
    }

    boolean logicPaused = false;
    boolean recordingMode = false;

    Marker curPosMarker = null;
    Track selectedTrack = null;
    Marker startMarker = null;
    Marker endMarker = null;

    Polyline line = null;
    List<GeoPoint> curTrack = null;
    

    public void stopLogic() {
        if (map != null) {
            map.getOverlayManager().clear();
        }

        logicPaused = true;
        map = null;
        line = null;
        curTrack = null;

        selectedTrack = null;
        curPosMarker = null;
        startMarker = null;
        endMarker = null;

        recordingMode = false;
    }

    public void startRecordingMode() {
        recordingMode = true;
        logicPaused = false;
    }

    public void stopRecordingModeAndSave() {
        recordingMode = false;
        logicPaused = true;
    }

    public void load(MapView inMap) {
        ctx = getApplicationContext();

        selectedTrack = db.trackDAO().getSelectedTrack();

        loc = new GPSLocation(ctx, this);
        loc.requestLocationUpdates();

        map = inMap;
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(15);
        //GeoPoint startPoint = new GeoPoint(pos.getLatitude(), pos.getLongitude());
        //mapController.setCenter(startPoint);

        // color it
        // https://github.com/osmdroid/osmdroid/blob/master/OpenStreetMapViewer/src/main/java/org/osmdroid/samplefragments/drawing/ShowAdvancedPolylineStyles.java#L125-L180
        // https://github.com/osmdroid/osmdroid/issues/1726

        if (selectedTrack != null) {
            List<TrackPoint> trackPoints = db.trackDAO().getPointsForTrack(selectedTrack.id);
            curTrack = new ArrayList<>();

            for (int i = 0; i < trackPoints.size(); i++) {
                curTrack.add(trackPoints.get(i).toGeoPoint());
            }

            updateLine(curTrack);
            mapController.setCenter(curTrack.get(0));
        }
    }

    void updateLine(List<GeoPoint> points) {
        if (map == null)
            return;

        if (line == null) {
            line = new Polyline();
            map.getOverlayManager().add(line);
        }

        line.setPoints(points);

        // Start marker
        if (startMarker == null) {
            startMarker = new Marker(map);
            startMarker.setAnchor(0.2f, 0.2f);
            startMarker.setIcon(AppCompatResources.getDrawable(ctx, R.drawable.circle_green));
            map.getOverlayManager().add(startMarker);
        }

        // End marker
        if (endMarker == null) {
            endMarker = new Marker(map);
            endMarker.setAnchor(0.2f, 0.2f);
            endMarker.setIcon(AppCompatResources.getDrawable(ctx, R.drawable.circle_red));
            map.getOverlayManager().add(endMarker);
        }

        startMarker.setPosition(points.get(0));
        endMarker.setPosition(points.get(points.size() - 1));
    }

    public void updateLogic(GeoPoint curLoc) {
        if (map == null)
            return;

        Log.w("LAPTRACKER", "updateLogic");

        // Center map to "player"
        IMapController mapController = map.getController();
        mapController.setCenter(curLoc);

        if (curPosMarker == null) {
            curPosMarker = new Marker(map);
            curPosMarker.setAnchor(0.2f, 0.2f);
            curPosMarker.setIcon(AppCompatResources.getDrawable(ctx, R.drawable.circle_blue));

            map.getOverlays().add(curPosMarker);
        }

        curPosMarker.setPosition(curLoc);

        if (logicPaused)
            return;

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

                if (closestIdx >= 0) {
                    Log.w("LAPTRACKER", "Idx = " + closestIdx);
                    Log.w("LAPTRACKER", "Dist = " + closestDist);
                }
            }
        }
    }

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