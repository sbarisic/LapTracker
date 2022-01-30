package hr.vub.laptracker;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities = {Track.class}, version = 1, exportSchema = false)
public abstract class LapTrackerDb extends RoomDatabase {
    public abstract TrackDAO trackDAO();

    private static volatile LapTrackerDb INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static LapTrackerDb getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (LapTrackerDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), LapTrackerDb.class, "laptracker_db").allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }
}