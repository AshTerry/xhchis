package com.xlsgrid.net.xhchis;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.hc.asmr.asmr.IPlayerCallback;
import com.hc.asmr.asmr.IPrepareCallback;
import com.hc.therapy.Therapy;
import com.xlsgrid.net.xhchis.databinding.FragmentSecondBinding;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    private Therapy therapy;

    private String TAG = "SecondFragment";

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTherapy();

        binding.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!therapy.getEnable()) {
                    String solutionJSON = getArguments().getString("solution");
                    therapy.prepare(solutionJSON, new IPrepareCallback() {
                        @Override
                        public void onAllDone(@Nullable Exception e) {
                            if (null == e) {
                                therapy.play();
                                refreshCacheSizeTip();
                            }
                        }

                        @Override
                        public void onPredownloadDone() {
                            therapy.play();
                        }
                    });
                } else {
                    if (therapy.isPlaying()) {
                        therapy.pause();
                    } else {
                        therapy.play();
                    }
                }
            }
        });

        binding.btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                therapy.stop();
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });

        binding.btnCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                therapy.clearCache();
                refreshCacheSizeTip();
            }
        });
//        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(SecondFragment.this)
//                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        therapy.stop();
    }

    private void initTherapy() {
        String secret = "CLchQBRgFUrPe0yxTBPvR+ZVDF2/SRKYJXjbzcICr6M=";
        String appID = "95B93B818E15E7EF";
        therapy = new Therapy(
                secret,
                appID,
                this.getContext()
        );

        therapy.setPlayCallback(new IPlayerCallback() {
            @Override
            public void onTick(long l) {
                Duration current = Duration.ofSeconds(l);
                String progressValue = current.toString();
                binding.txtTime.setText(progressValue);
            }

            @Override
            public void onFinished() {
                Log.e(TAG, "onFinished");
            }

            @Override
            public void onPlay() {
                Log.e(TAG, "onPlay");
                binding.btnPlay.setText("Pause");
            }

            @Override
            public void onPause() {
                Log.e(TAG, "onPause");
                binding.btnPlay.setText("Play");
            }

            @Override
            public void onErrorWhenPlaying(@NonNull Exception e) {
                Log.e(TAG, "onErrorWhenPlaying" + e.toString());
            }
        });
    }

    private void refreshCacheSizeTip() {
        long size = therapy.getCacheSize();
        double sizeInMB = size / 1024.0 / 1024.0;

        BigDecimal bd = new BigDecimal(sizeInMB);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        binding.btnCache.setText(bd.toString() + "MB");
    }
}