package hr.vub.laptracker;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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

    public Track(@NonNull String track_id, int best_time_ms) {
        this.track_id = track_id;
        this.best_time_ms = best_time_ms;
        selected_track = 0;
    }
}
