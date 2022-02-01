package hr.vub.laptracker;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import hr.vub.laptracker.databinding.FragmentFirstBinding;


public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private MainActivity act;

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        act = (MainActivity) getActivity();
        act.firstFragment = this;
        View decorView = act.getWindow().getDecorView();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        } else {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);


        // NavController navContr = NavHostFragment.findNavController(this);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnStart.setOnClickListener(view1 -> {
            if (act.recordingMode) {
                binding.txtOutput.setText(R.string.loading);
                act.stopRecordingModeAndSave();
                showSaveDialog();
            } else if (act.raceMode) {
                act.stopRaceMode();
                reloadMap();
            } else {
                if (act.selectedTrack != null) {
                    act.startRaceMode();
                    reloadMap();
                }
            }
        });

        binding.btnTracks.setOnClickListener(view1 -> {
            act.stopLogic();
            NavHostFragment.findNavController(FirstFragment.this).navigate(R.id.action_FirstFragment_to_SecondFragment);
        });

        binding.btnSettings.setOnClickListener(view1 -> {
            act.stopLogic();
            NavHostFragment.findNavController(FirstFragment.this).navigate(R.id.action_FirstFragment_to_SettingsFragment);
        });

        reloadMap();
    }

    void updateTime(int ms) {
        int minutes = (ms / 1000) / 60;
        float seconds = (ms / 1000.0f) % 60;

        binding.txtOutput.setText(String.format("%d:%.2fs", minutes, seconds));
    }

    void finishRace(int totalMs, boolean newRecord) {
        int minutes = (totalMs / 1000) / 60;
        float seconds = (totalMs / 1000.0f) % 60;

        String prefix = getResources().getString(R.string.finish);

        if (newRecord)
            prefix = getResources().getString(R.string.new_record);

        String timeString = String.format("%d:%.2fs", minutes, seconds);

        binding.txtOutput.setText(prefix + " " + timeString);
        Toast.makeText(getActivity(), timeString, Toast.LENGTH_SHORT).show();
    }

    void reloadMap() {
        act.load(binding.map);
        binding.btnStart.setText(R.string.btn_start);

        if (act.selectedTrack == null) {
            binding.txtOutput.setText(R.string.txt_no_track_selected);
        } else {
            binding.txtOutput.setText(act.selectedTrack.track_id);
        }

        if (act.recordingMode) {
            binding.txtOutput.setText(R.string.txt_recording_track);
            binding.btnStart.setText(R.string.btn_save_track);
        }

        if (act.raceMode) {
            binding.btnStart.setText(R.string.btn_cancel);
        }
    }

    void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.txt_saving_track);

        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(R.string.btn_save_track, (dialog, which) -> {
            MainActivity act = (MainActivity)getActivity();

            String trackTitle = input.getText().toString();
            TrackDAO dao = act.db.trackDAO();

            Track newTrack = new Track(trackTitle, 0, MainActivity.measureDistance(act.curTrack));
            newTrack.best_time_ms = (int)(act.endTime - act.startTime);
            newTrack.calcAvgSpeed();

            dao.insert(newTrack);

            List<Track> allTracks = dao.getTracks();
            newTrack = allTracks.get(allTracks.size() - 1);

            ArrayList<TrackPoint> newTrackPoints = new ArrayList<>();
            for (int i = 0; i < act.curTrack.size(); i++) {
                newTrackPoints.add(new TrackPoint(newTrack.id, i, act.curTrack.get(i)));
            }

            dao.insert(newTrackPoints);

            act.stopLogic();
            reloadMap();
        });

        builder.setNegativeButton(R.string.btn_cancel, (dialog, which) -> {
            dialog.cancel();

            act.stopLogic();
            reloadMap();
        });

        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        MainActivity act = (MainActivity) getActivity();
        if (act != null) {
            act.setContentView(R.layout.fragment_first);
            act.recreate();
        } else {
            Log.d("LAPTRACKER", "This shouldn't ever happen");
        }
    }
}