package hr.vub.laptracker;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities = {Track.class, TrackPoint.class}, version = 7, exportSchema = false)
public abstract class LapTrackerDb extends RoomDatabase {
    public abstract TrackDAO trackDAO();

    private static volatile LapTrackerDb INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static LapTrackerDb getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (LapTrackerDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), LapTrackerDb.class, "laptracker_db").allowMainThreadQueries().fallbackToDestructiveMigration().build();

                    // FillDatabase();
                }
            }
        }
        return INSTANCE;
    }

    static void FillDatabase() {
        TrackDAO dao = INSTANCE.trackDAO();
        dao.deleteAllTracks();
        dao.deleteAllTrackPoints();

        // Generate tracks
        Track t1 = new Track("Trojstvo - Sandrovac", 611132);
        dao.insert(t1);

        /*Track t2 = new Track("Track B", 540000);
        dao.insert(t2);

        Track t3 = new Track("Track C", 660000);
        dao.insert(t3);*/

        // Generate points

        Track selTrack = dao.getTracks().get(0);
        //dao.selectTrack(selTrack.id);
        dao.insert(GenerateTrack(selTrack));
    }

    static ArrayList<TrackPoint> GenerateTrack(Track t) {
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

        ArrayList<TrackPoint> trackPts = new ArrayList<>();

        for (int i = 0; i < pts.size(); i++) {
            TrackPoint pt = new TrackPoint(t.id, i, pts.get(i).getLatitude(), pts.get(i).getLongitude());
            trackPts.add(pt);
        }

        return trackPts;
    }
}