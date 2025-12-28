package com.example.trackingcaloapp.ui.profile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.model.UserInfo;
import com.example.trackingcaloapp.model.ValidationResult;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

/**
 * Bottom Sheet Dialog để ghi nhanh cân nặng.
 */
public class QuickWeightLogDialogFragment extends BottomSheetDialogFragment {

    private ProfileViewModel viewModel;

    private TextView tvCurrentWeight, tvWeightChange;
    private TextInputLayout tilWeight;
    private TextInputEditText etWeight, etNote;
    private MaterialButton btnCancel, btnSave;

    private float currentWeight = 0f;

    public static QuickWeightLogDialogFragment newInstance() {
        return new QuickWeightLogDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_quick_weight_log, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireParentFragment()).get(ProfileViewModel.class);

        initViews(view);
        setupListeners();
        loadCurrentWeight();
    }

    private void initViews(View view) {
        tvCurrentWeight = view.findViewById(R.id.tvCurrentWeight);
        tvWeightChange = view.findViewById(R.id.tvWeightChange);
        tilWeight = view.findViewById(R.id.tilWeight);
        etWeight = view.findViewById(R.id.etWeight);
        etNote = view.findViewById(R.id.etNote);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnSave = view.findViewById(R.id.btnSave);
    }

    private void setupListeners() {
        btnCancel.setOnClickListener(v -> dismiss());
        btnSave.setOnClickListener(v -> saveWeight());

        etWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateWeightChangePreview();
            }
        });
    }

    private void loadCurrentWeight() {
        UserInfo info = viewModel.getUserInfo().getValue();
        if (info != null) {
            currentWeight = info.getWeight();
            tvCurrentWeight.setText(String.format(Locale.getDefault(),
                    "Cân nặng hiện tại: %.1f kg", currentWeight));
        }
    }

    private void updateWeightChangePreview() {
        try {
            String weightStr = etWeight.getText().toString().trim();
            if (weightStr.isEmpty()) {
                tvWeightChange.setVisibility(View.GONE);
                return;
            }

            float newWeight = Float.parseFloat(weightStr);
            float change = newWeight - currentWeight;

            if (Math.abs(change) < 0.01f) {
                tvWeightChange.setVisibility(View.GONE);
                return;
            }

            String changeText;
            int colorRes;
            if (change > 0) {
                changeText = String.format(Locale.getDefault(), "+%.1f kg", change);
                colorRes = R.color.error;
            } else {
                changeText = String.format(Locale.getDefault(), "%.1f kg", change);
                colorRes = R.color.success;
            }

            tvWeightChange.setText(changeText);
            tvWeightChange.setTextColor(requireContext().getColor(colorRes));
            tvWeightChange.setVisibility(View.VISIBLE);

        } catch (NumberFormatException e) {
            tvWeightChange.setVisibility(View.GONE);
        }
    }

    private void saveWeight() {
        tilWeight.setError(null);

        try {
            String weightStr = etWeight.getText().toString().trim();
            if (weightStr.isEmpty()) {
                tilWeight.setError("Vui lòng nhập cân nặng");
                return;
            }

            float weight = Float.parseFloat(weightStr);
            String note = etNote.getText().toString().trim();
            if (note.isEmpty()) note = null;

            ValidationResult result = viewModel.validateWeight(weight);
            if (!result.isValid()) {
                tilWeight.setError(result.getError(ValidationResult.FIELD_WEIGHT));
                return;
            }

            // Disable buttons during save
            setButtonsEnabled(false);

            viewModel.logWeight(weight, note);
            Toast.makeText(requireContext(), "Đã ghi nhận cân nặng!", Toast.LENGTH_SHORT).show();
            dismiss();

        } catch (NumberFormatException e) {
            tilWeight.setError("Vui lòng nhập số hợp lệ");
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        btnSave.setEnabled(enabled);
        btnCancel.setEnabled(enabled);
    }
}
