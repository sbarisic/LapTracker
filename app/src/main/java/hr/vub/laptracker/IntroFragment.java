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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentIntroBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.w("LAPTRACKER", "Requesting access");
        AtomicReference<Boolean> AccessGranted = new AtomicReference<>(false);
        AtomicReference<Boolean> ApproxAccessGranted = new AtomicReference<>(false);
        AtomicReference<Boolean> Answered = new AtomicReference<>(false);

        ActivityResultLauncher<String[]> locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                    Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                    if (fineLocationGranted != null && fineLocationGranted) {
                        // Precise location access granted.
                        Log.w("LAPTRACKER", "Access granted");

                        AccessGranted.set(true);
                        Answered.set(true);
                        //NavHostFragment.findNavController(IntroFragment.this).navigate(R.id.action_introFragment_to_FirstFragment);

                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                        // TODO not acceptable
                        // Only approximate location access granted.
                        Log.w("LAPTRACKER", "Approximate access granted");

                        ApproxAccessGranted.set(true);
                        Answered.set(true);
                        /*new AlertDialog.Builder(getActivity().getApplicationContext())
                                .setTitle(R.string.coarse_title)
                                .setMessage(R.string.coarse_message)
                                .setNeutralButton(android.R.string.ok, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();*/


                        /*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getApplicationContext());
                        builder.setMessage("Look at this dialog!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();*/

                    } else {
                        Log.w("LAPTRACKER", "No access");
                        Answered.set(true);

                        /*new AlertDialog.Builder(getActivity().getApplicationContext())
                                .setTitle(R.string.no_permission_title)
                                .setMessage(R.string.no_permission_message)
                                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        getActivity().finishAndRemoveTask();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();*/
                    }
                }
        );

        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        /*while (!Answered.get()) {

        }*/

        /*if (AccessGranted.get())
            NavHostFragment.findNavController(IntroFragment.this).navigate(R.id.action_introFragment_to_FirstFragment);*/

        new CountDownTimer(10000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {

                if (Answered.get()) {
                    Log.w("LAPTRACKER", "Access granted: " + AccessGranted.toString());
                    Log.w("LAPTRACKER", "Approx granted: " + ApproxAccessGranted.toString());

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getApplicationContext());
                    builder.setMessage("Look at this dialog!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                    cancel();
                }
            }

            @Override
            public void onFinish() {
                //NavHostFragment.findNavController(IntroFragment.this).navigate(R.id.action_introFragment_to_FirstFragment);
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}