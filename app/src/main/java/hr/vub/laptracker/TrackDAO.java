package hr.vub.laptracker;

import androidx.annotation.*;
import androidx.room.*;

import java.util.List;

@Dao
public interface TrackDAO {
    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(Track word);

    @Query("DELETE FROM tracks")
    void deleteAllTracks();

    @Query("SELECT * FROM tracks ORDER BY id ASC")
    List<Track> getTracks();
}