package hr.vub.laptracker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.concurrent.atomic.AtomicReference;

import hr.vub.laptracker.databinding.FragmentIntroBinding;

public class IntroFragment extends Fragment {
    private FragmentIntroBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentIntroBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.w("LAPTRACKER", "Requesting access");

        MainActivity mainAct = (MainActivity)getActivity();

        ActivityResultLauncher<String[]> locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                    Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                    String MessageTitle = "TITLE";
                    String MessageMsg = "MESSAGE";

                    if (fineLocationGranted != null && fineLocationGranted) {
                        // Precise location access granted.
                        Log.w("LAPTRACKER", "Access granted");

                        NavHostFragment.findNavController(IntroFragment.this).navigate(R.id.action_introFragment_to_FirstFragment);
                        return;
                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                        // TODO not acceptable
                        // Only approximate location access granted.
                        Log.w("LAPTRACKER", "Approximate access granted");

                        MessageTitle = mainAct.ctx.getResources().getString(R.string.coarse_title);
                        MessageMsg = mainAct.ctx.getResources().getString(R.string.coarse_message);
                    } else {
                        Log.w("LAPTRACKER", "No access");

                        MessageTitle = mainAct.ctx.getResources().getString(R.string.no_permission_title);
                        MessageMsg = mainAct.ctx.getResources().getString(R.string.no_permission_message);
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(mainAct);
                    builder.setTitle(MessageTitle).setMessage(MessageMsg).setCancelable(false).setPositiveButton(android.R.string.ok, (dialog, id) -> {
                                // mainAct.finishAndRemoveTask();
                                System.exit(0);
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
        );

        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}