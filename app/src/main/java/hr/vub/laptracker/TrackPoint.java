package hr.vub.laptracker;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.osmdroid.util.GeoPoint;

@Entity(tableName = "track_points")
public class TrackPoint {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id = 0;

    @NonNull
    @ColumnInfo(name = "track_id")
    public int track_id;

    @ColumnInfo(name = "index")
    public int index;

    @ColumnInfo(name = "lon")
    public double lon;

    @ColumnInfo(name = "lat")
    public double lat;

    public TrackPoint(@NonNull int track_id, int index, double lat, double lon) {
        this.track_id = track_id;
        this.index = index;
        this.lon = lon;
        this.lat = lat;
    }

    public TrackPoint(@NonNull int track_id, int index, GeoPoint pt) {
        this(track_id, index, pt.getLatitude(), pt.getLongitude());
    }

    public GeoPoint toGeoPoint() {
        return new GeoPoint(lat, lon);
    }
}
