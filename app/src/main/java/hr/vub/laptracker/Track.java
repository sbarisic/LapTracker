package hr.vub.laptracker;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tracks")
public class Track {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    public int id;

    @NonNull
    @ColumnInfo(name = "track_id")
    public String track_id;

    public Track(@NonNull String track_id) {
        this.track_id = track_id;
    }
}
