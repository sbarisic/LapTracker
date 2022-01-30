package hr.vub.laptracker;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.*;

import java.util.Locale;

public class SettingsFragment extends Fragment {
    private hr.vub.laptracker.databinding.FragmentSettingsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = hr.vub.laptracker.databinding.FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity act = (MainActivity) getActivity();

        Resources res = getResources();
        android.content.res.Configuration conf = res.getConfiguration();

        String Lang = conf.locale.getLanguage();
        if (Lang == "en") {
            binding.radioEnglish.toggle();
        } else {
            binding.radioCroatian.toggle();
        }

        // binding.radioCroatian.toggle();

        binding.radioCroatian.setOnClickListener(view1 -> {
            setLocale("hr");
        });

        binding.radioEnglish.setOnClickListener(view1 -> {
            setLocale("en");
        });

        binding.btnClearDb.setOnClickListener(view1 -> {
            TrackDAO dao = act.db.trackDAO();
            dao.deleteAllTrackPoints();
            dao.deleteAllTracks();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();

        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        // NavHostFragment.findNavController(SettingsFragment.this).navigate(R.id.action_SettingsFragment_self);

        /*getFragmentManager()
                .beginTransaction()
                .detach(SettingsFragment.this)
                .attach(SettingsFragment.this)
                .commit();*/
    }
}