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

    Marker curPosMarker = null;
    Track selectedTrack = null;
    List<GeoPoint> curTrack = null;

    public void stopLogic() {
        map = null;
        selectedTrack = null;
        curPosMarker = null;
    }

    public void load(MapView inMap, FirstFragment frag) {
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
            Polyline line = new Polyline();

            List<TrackPoint> trackPoints = db.trackDAO().getPointsForTrack(selectedTrack.id);
            curTrack = new ArrayList<>();

            for (int i = 0; i < trackPoints.size(); i++) {
                curTrack.add(trackPoints.get(i).toGeoPoint());
            }

            line.setPoints(curTrack);
            map.getOverlayManager().add(line);

            // Start marker
            Marker startMarker = new Marker(map);
            startMarker.setAnchor(0.2f, 0.2f);
            startMarker.setIcon(AppCompatResources.getDrawable(ctx, R.drawable.circle_green));
            startMarker.setPosition(curTrack.get(0));
            map.getOverlayManager().add(startMarker);

            mapController.setCenter(curTrack.get(0));

            // End marker
            Marker endMarker = new Marker(map);
            endMarker.setAnchor(0.2f, 0.2f);
            endMarker.setIcon(AppCompatResources.getDrawable(ctx, R.drawable.circle_red));
            endMarker.setPosition(curTrack.get(curTrack.size() - 1));
            map.getOverlayManager().add(endMarker);
        }
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

        curPosMarker.setPosition(curLoc);
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

    ArrayList<GeoPoint> GenerateTrack() {
        ArrayList<GeoPoint> pts = new ArrayList<>();

        pts.add(new GeoPoint(45.92085, 16.96896));
        pts.add(new GeoPoint(45.920565, 16.96894));
        pts.add(new GeoPoint(45.92028, 16.96892));
        pts.add(new GeoPoint(45.92028, 16.96892));
        pts.add(new GeoPoint(45.9201, 16.96891));
        pts.add(new GeoPoint(45.91993, 16.9689));
        pts.add(new GeoPoint(45.91973, 16.9689));
        pts.add(new GeoPoint(45.91956, 16.96891));
        pts.add(new GeoPoint(45.91944, 16.96894));
        pts.add(new GeoPoint(45.91923, 16.96901));
        pts.add(new GeoPoint(45.91903, 16.9691));
        pts.add(new GeoPoint(45.91877, 16.96924));
        pts.add(new GeoPoint(45.91855, 16.9694));
        pts.add(new GeoPoint(45.918215, 16.969715));
        pts.add(new GeoPoint(45.91806, 16.96989));
        pts.add(new GeoPoint(45.917955, 16.970045));
        pts.add(new GeoPoint(45.91785, 16.9702));
        pts.add(new GeoPoint(45.91785, 16.9702));
        pts.add(new GeoPoint(45.917555, 16.97067));
        pts.add(new GeoPoint(45.91739, 16.97094));
        pts.add(new GeoPoint(45.9172933333, 16.9710966667));
        pts.add(new GeoPoint(45.9171966667, 16.9712533333));
        pts.add(new GeoPoint(45.9171, 16.97141));
        pts.add(new GeoPoint(45.9171, 16.97141));
        pts.add(new GeoPoint(45.91692, 16.97176));
        pts.add(new GeoPoint(45.916695, 16.972305));
        pts.add(new GeoPoint(45.91661, 16.97258));
        pts.add(new GeoPoint(45.91661, 16.97258));
        pts.add(new GeoPoint(45.91659, 16.97268));
        pts.add(new GeoPoint(45.916475, 16.973315));
        pts.add(new GeoPoint(45.91644, 16.97357));
        pts.add(new GeoPoint(45.91644, 16.97357));
        pts.add(new GeoPoint(45.916385, 16.974105));
        pts.add(new GeoPoint(45.91636, 16.97434));
        pts.add(new GeoPoint(45.91636, 16.97434));
        pts.add(new GeoPoint(45.9163, 16.97488));
        pts.add(new GeoPoint(45.91627, 16.97517));
        pts.add(new GeoPoint(45.91627, 16.97517));
        pts.add(new GeoPoint(45.91618, 16.9759));
        pts.add(new GeoPoint(45.91613, 16.97624));
        pts.add(new GeoPoint(45.91613, 16.97624));
        pts.add(new GeoPoint(45.91606, 16.97655));
        pts.add(new GeoPoint(45.91599, 16.97677));
        pts.add(new GeoPoint(45.9159, 16.97705));
        pts.add(new GeoPoint(45.91587, 16.9772));
        pts.add(new GeoPoint(45.91585, 16.97759));
        pts.add(new GeoPoint(45.91588, 16.97791));
        pts.add(new GeoPoint(45.91589, 16.97813));
        pts.add(new GeoPoint(45.91592, 16.97848));
        pts.add(new GeoPoint(45.91596, 16.97869));
        pts.add(new GeoPoint(45.91608, 16.97901));
        pts.add(new GeoPoint(45.91616, 16.97917));
        pts.add(new GeoPoint(45.91628, 16.97943));
        pts.add(new GeoPoint(45.91636, 16.97959));
        pts.add(new GeoPoint(45.91643, 16.97983));
        pts.add(new GeoPoint(45.91652, 16.98022));
        pts.add(new GeoPoint(45.91657, 16.98047));
        pts.add(new GeoPoint(45.91664, 16.98068));
        pts.add(new GeoPoint(45.91673, 16.98085));
        pts.add(new GeoPoint(45.91696, 16.98113));
        pts.add(new GeoPoint(45.91708, 16.98125));
        pts.add(new GeoPoint(45.91725, 16.98143));
        pts.add(new GeoPoint(45.9174, 16.98157));
        pts.add(new GeoPoint(45.91758, 16.9818));
        pts.add(new GeoPoint(45.91765, 16.98194));
        pts.add(new GeoPoint(45.9178, 16.98229));
        pts.add(new GeoPoint(45.91793, 16.98261));
        pts.add(new GeoPoint(45.91802, 16.98283));
        pts.add(new GeoPoint(45.9182, 16.98321));
        pts.add(new GeoPoint(45.91828, 16.98337));
        pts.add(new GeoPoint(45.91828, 16.98337));
        pts.add(new GeoPoint(45.91846, 16.98365));
        pts.add(new GeoPoint(45.91867, 16.98391));
        pts.add(new GeoPoint(45.91883, 16.9841));
        pts.add(new GeoPoint(45.91896, 16.98433));
        pts.add(new GeoPoint(45.919, 16.98461));
        pts.add(new GeoPoint(45.91898, 16.98487));
        pts.add(new GeoPoint(45.9189, 16.98515));
        pts.add(new GeoPoint(45.91884, 16.98535));
        pts.add(new GeoPoint(45.91877, 16.98558));
        pts.add(new GeoPoint(45.91867, 16.9859));
        pts.add(new GeoPoint(45.91858, 16.98619));
        pts.add(new GeoPoint(45.91854, 16.98629));
        pts.add(new GeoPoint(45.91845, 16.98659));
        pts.add(new GeoPoint(45.91827, 16.98696));
        pts.add(new GeoPoint(45.91817, 16.98717));
        pts.add(new GeoPoint(45.91806, 16.98737));
        pts.add(new GeoPoint(45.91791, 16.98763));
        pts.add(new GeoPoint(45.91777, 16.98779));
        pts.add(new GeoPoint(45.91766, 16.9879));
        pts.add(new GeoPoint(45.9174, 16.988));
        pts.add(new GeoPoint(45.91721, 16.98802));
        pts.add(new GeoPoint(45.91712, 16.98802));
        pts.add(new GeoPoint(45.91678, 16.988));
        pts.add(new GeoPoint(45.91666, 16.98802));
        pts.add(new GeoPoint(45.91643, 16.9881));
        pts.add(new GeoPoint(45.91633, 16.98814));
        pts.add(new GeoPoint(45.91606, 16.98831));
        pts.add(new GeoPoint(45.91594, 16.98839));
        pts.add(new GeoPoint(45.91572, 16.98857));
        pts.add(new GeoPoint(45.915315, 16.98876));
        pts.add(new GeoPoint(45.91514, 16.98883));
        pts.add(new GeoPoint(45.91514, 16.98883));
        pts.add(new GeoPoint(45.91491, 16.9889));
        pts.add(new GeoPoint(45.91479, 16.98894));

        return pts;
    }
}