package hr.vub.laptracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import hr.vub.laptracker.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);

        MainActivity mainAct = (MainActivity) getActivity();


        mainAct.map = binding.map;
        mainAct.map.setTileSource(TileSourceFactory.MAPNIK);
        mainAct.map.setBuiltInZoomControls(true);
        mainAct.map.setMultiTouchControls(true);

        IMapController mapController = mainAct.map.getController();
        mapController.setZoom(15);
        GeoPoint startPoint = new GeoPoint(45.89857767203944, 16.842182187700285);
        mapController.setCenter(startPoint);

        /*CompassOverlay mCompassOverlay = new CompassOverlay(mainAct.ctx, new InternalCompassOrientationProvider(mainAct.ctx), mainAct.map);
        mCompassOverlay.enableCompass();
        mainAct.map.getOverlays().add(mCompassOverlay);*/

        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(mainAct.ctx), mainAct.map);
        mLocationOverlay.enableMyLocation();
        mainAct.map.getOverlays().add(mLocationOverlay);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*binding.buttonFirst.setOnClickListener(view1 -> NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment));

        binding.buttonSettings.setOnClickListener(view12 -> NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SettingsFragment));*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}