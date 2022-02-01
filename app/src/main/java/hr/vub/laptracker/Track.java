package hr.vub.laptracker;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.osmdroid.util.GeoPoint;

import java.util.List;

@Entity(tableName = "tracks")
public class Track {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id = 0;

    @NonNull
    @ColumnInfo(name = "track_id")
    public String track_id;

    @ColumnInfo(name = "best_time_ms")
    public int best_time_ms;

    @ColumnInfo(name = "selected_track")
    public int selected_track;

    @ColumnInfo(name = "distance")
    public int distance; // m

    @ColumnInfo(name = "avg_speed")
    public int avg_speed; // km/h

    public Track(@NonNull String track_id, int best_time_ms, int distance) {
        selected_track = 0;
        avg_speed = 0;

        this.track_id = track_id;
        this.best_time_ms = best_time_ms;
        this.distance = distance;
    }

    public void calcAvgSpeed() {
        double speed_ms = distance / (best_time_ms / 1000.0);
        avg_speed = (int) (speed_ms * 3.6);
    }

    public void calcDist(List<GeoPoint> points) {
        double dist = 0;
        GeoPoint prevPoint = points.get(0);

        for (int i = 1; i < points.size(); i++) {
            GeoPoint curPoint = points.get(i);
            dist += prevPoint.distanceToAsDouble(curPoint);
            prevPoint = curPoint;
        }

        distance = (int) dist;
    }
}