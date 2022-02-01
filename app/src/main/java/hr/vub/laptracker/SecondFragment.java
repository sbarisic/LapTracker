package hr.vub.laptracker;

import android.os.Bundle;
import android.view.*;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hr.vub.laptracker.databinding.FragmentSecondBinding;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    TrackViewAdapter adapter;
    MainActivity act;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        act = (MainActivity) getActivity();

        // data to populate the RecyclerView with
        /*ArrayList<String> animalNames = new ArrayList<>();
        animalNames.add("Horse");
        animalNames.add("Cow");
        animalNames.add("Camel");
        animalNames.add("Sheep");
        animalNames.add("Goat");*/

        List<Track> tracks = act.db.trackDAO().getTracks();

        // set up the RecyclerView
        LinearLayoutManager layoutMgr = new LinearLayoutManager(act);

        RecyclerView recyclerView = binding.recyclerTracks;
        recyclerView.setLayoutManager(layoutMgr);
        adapter = new TrackViewAdapter(getActivity(), tracks);

        adapter.setClickListener((view, position) -> {
            Track clickedTrack = adapter.getItem(position);
            act.db.trackDAO().selectTrack(clickedTrack.id);

            updateBindings();
            // NavHostFragment.findNavController(SecondFragment.this).navigate(R.id.action_SecondFragment_to_FirstFragment);
            //Toast .makeText(getActivity(), "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        });

        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), layoutMgr.getOrientation()));
        recyclerView.setAdapter(adapter);

        updateBindings();
        return binding.getRoot();
    }

    void updateBindings() {
        Track selectedTrack = act.db.trackDAO().getSelectedTrack();

        boolean btnsEnabled = selectedTrack != null;
        //binding.btnOk.setEnabled(btnsEnabled);
        binding.btnDelete.setEnabled(btnsEnabled);

        if (selectedTrack != null) {
            binding.tvSelectedTrack.setText(selectedTrack.track_id);
        } else {
            binding.tvSelectedTrack.setText(R.string.txt_no_track_selected);
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*binding.btnOk.setOnClickListener(view1 -> {
            NavHostFragment.findNavController(SecondFragment.this).navigate(R.id.action_SecondFragment_to_FirstFragment);
        });*/

        binding.btnNewTrack.setOnClickListener(view1 -> {
            act.startRecordingMode();
            NavHostFragment.findNavController(SecondFragment.this).navigate(R.id.action_SecondFragment_to_FirstFragment);
        });

        binding.btnDelete.setOnClickListener(view1 -> {
            TrackDAO dao = act.db.trackDAO();
            Track selectedTrack = dao.getSelectedTrack();

            dao.delete(dao.getPointsForTrack(selectedTrack.id));
            dao.delete(selectedTrack);

            adapter.updateData(dao.getTracks());
            updateBindings();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}