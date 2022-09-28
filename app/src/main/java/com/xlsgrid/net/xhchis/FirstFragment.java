package com.xlsgrid.net.xhchis;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.github.gzuliyujiang.wheelpicker.OptionPicker;
import com.github.gzuliyujiang.wheelpicker.contract.OnOptionPickedListener;
import com.google.android.material.snackbar.Snackbar;
import com.hc.instant.model.Classification;
import com.hc.instant.model.Cure;
import com.hc.instant.model.Level;
import com.xlsgrid.net.xhchis.databinding.FragmentFirstBinding;

import java.time.Duration;
import java.util.List;

public class FirstFragment extends Fragment {

    private TherapyService therapyService = new TherapyService();

    private FragmentFirstBinding binding;

    private String TAG = "FirstFragment";

    private List<Cure> allCures;

    private boolean isLoginOK = false;

    String userID = "TestUser001";

    private Cure selectedCure;

    public void setSelectedCure(Cure selectedCure) {
        this.selectedCure = selectedCure;
        binding.btnType.setText(selectedCure.toString());

        List<Classification> classifications = selectedCure.getAllClassificatios();
        if (null != classifications){
            setSelectedClassify(classifications.get(0));
        } else  {
            setSelectedClassify(null);
        }

        setSelectedDuration(null);
        setSelectedLevel(null);

        updateStartButton();
    }

    private Classification selectedClassify;

    public void setSelectedClassify(Classification selectedClassify) {
        this.selectedClassify = selectedClassify;

        if (null == selectedClassify){
            binding.btnSubClass.setVisibility(View.INVISIBLE);
        } else  {
            binding.btnSubClass.setVisibility(View.VISIBLE);
            binding.btnSubClass.setText(selectedClassify.toString());
        }
        updateStartButton();
    }

    private Long selectedDuration;

    public void setSelectedDuration(Long selectedDuration) {
        this.selectedDuration = selectedDuration;
        String text = (selectedDuration == null) ? "请选择时长" : selectedDuration.toString();
        binding.btnDuration.setText(text);
        updateStartButton();
    }

    private Level selectedLevel;

    public void setSelectedLevel(Level selectedLevel) {
        this.selectedLevel = selectedLevel;
        String text = (selectedLevel == null) ? "请选择程度" : selectedLevel.toString();
        binding.btnLevel.setText(text);
        updateStartButton();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnStart.setVisibility(View.INVISIBLE);


        binding.btnLogin.setVisibility(isLoginOK ? View.INVISIBLE : View.VISIBLE);
        binding.loConfigure.setVisibility(!isLoginOK ? View.INVISIBLE : View.VISIBLE);

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //0
                allCures = therapyService.fetchAllCures();

                //1. 授权
                boolean isAuthrized = therapyService.authorize();
                if (isAuthrized){
                    Log.e(TAG,"授权完成");
                } else  {
                    return;
                }
                //2。登录
                try {
                    isLoginOK = therapyService.login(userID, getContext());
                    if (isLoginOK) {
                        binding.btnLogin.setVisibility(View.INVISIBLE);
                        binding.loConfigure.setVisibility(View.VISIBLE);
                    }
                }catch ( Exception e) {
                    Log.e(TAG,e.toString());
                }
            }
        });

        binding.btnType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OptionPicker picker = new OptionPicker(getActivity());
                picker.setTitle("疗愈类型");
                picker.setBodyWidth(140);
                picker.setData(allCures);
                picker.setDefaultPosition(0);
                picker.setOnOptionPickedListener(new OnOptionPickedListener() {
                    @Override
                    public void onOptionPicked(int position, Object item) {
                        setSelectedCure(allCures.get(position));
                    }
                });
                picker.show();
            }
        });

        binding.btnSubClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null == selectedCure) return;
                List<Classification> allClassify = selectedCure.getAllClassificatios();
                if (null == allClassify) {
                    return;
                }
                OptionPicker picker = new OptionPicker(getActivity());
                picker.setTitle("分类选择");
                picker.setBodyWidth(140);
                picker.setData(allClassify);
                picker.setDefaultPosition(0);
                picker.setOnOptionPickedListener(new OnOptionPickedListener() {
                    @Override
                    public void onOptionPicked(int position, Object item) {
                        setSelectedClassify(allClassify.get(position));
                    }
                });
                picker.show();
            }
        });

        binding.btnDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null == selectedCure) return;

                List<Long> allDurations = selectedCure.getAllDurations();

                OptionPicker picker = new OptionPicker(getActivity());
                picker.setTitle("时间选择");
                picker.setBodyWidth(140);
                picker.setData(allDurations);
                picker.setDefaultPosition(0);
                picker.setOnOptionPickedListener(new OnOptionPickedListener() {
                    @Override
                    public void onOptionPicked(int position, Object item) {
                        setSelectedDuration(allDurations.get(position));
                    }
                });
                picker.show();
            }
        });

        binding.btnLevel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (null == selectedCure) return;

                List<Level> allLevels = selectedCure.getAllLevels();

                OptionPicker picker = new OptionPicker(getActivity());
                picker.setTitle("程度选择");
                picker.setBodyWidth(140);
                picker.setData(allLevels);
                picker.setDefaultPosition(0);
                picker.setOnOptionPickedListener(new OnOptionPickedListener() {
                    @Override
                    public void onOptionPicked(int position, Object item) {
                        setSelectedLevel(allLevels.get(position));
                    }
                });
                picker.show();
            }
        });

        binding.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String jsonStr = therapyService.fetchSolutionJSON(
                            selectedCure,
                            userID,
                            selectedClassify,
                            Duration.ofMinutes(selectedDuration),
                            1,
                            selectedLevel,
                            getContext()
                    );

                    Bundle bundle = new Bundle();
                    bundle.putString("solution",jsonStr);
                    NavHostFragment.findNavController(FirstFragment.this)
                            .navigate(R.id.action_FirstFragment_to_SecondFragment,bundle);
                }catch (Exception e) {
                    Log.e(TAG,e.toString());
                }
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    void updateStartButton() {
        if (selectedCure != null && selectedLevel != null && selectedDuration != null) {
            binding.btnStart.setVisibility(View.VISIBLE);
        } else  {
            binding.btnStart.setVisibility(View.INVISIBLE);
        }
    }

    void doToast(String text) {
    }
}