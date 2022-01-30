package hr.vub.laptracker;

import androidx.room.*;

import java.util.List;

@Dao
public interface TrackDAO {
    ////////////////////////////////////////////////////////////////////////////////
    // Track
    ////////////////////////////////////////////////////////////////////////////////

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(Track track);

    @Update()
    void update(Track track);

    @Query("DELETE FROM tracks")
    void deleteAllTracks();

    @Query("SELECT * FROM tracks ORDER BY id ASC")
    List<Track> getTracks();

    @Query("SELECT * FROM tracks WHERE selected_track = 1")
    Track getSelectedTrack();

    @Query("UPDATE tracks SET selected_track = (CASE id WHEN :id THEN 1 ELSE 0 END)")
    void selectTrack(int id);

    ////////////////////////////////////////////////////////////////////////////////
    // TrackPoint
    ////////////////////////////////////////////////////////////////////////////////

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(TrackPoint point);

    @Delete()
    void delete(TrackPoint point);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(List<TrackPoint> points);

    @Query("DELETE FROM track_points")
    void deleteAllTrackPoints();

    @Query("SELECT * FROM track_points WHERE track_id = :id_track ORDER BY track_id ASC")
    List<TrackPoint> getPointsForTrack(int id_track);
}