package com.example.trackingcaloapp.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

/**
 * Bottom Sheet Dialog để chỉnh sửa thông tin cá nhân.
 */
public class EditPersonalInfoDialogFragment extends BottomSheetDialogFragment {

    private ProfileViewModel viewModel;

    private TextInputLayout tilName, tilAge, tilHeight, tilWeight;
    private TextInputEditText etName, etAge, etHeight, etWeight;
    private AutoCompleteTextView spinnerGender;
    private MaterialButton btnCancel, btnSave;

    private final String[] genders = {"Nam", "Nữ"};

    public static EditPersonalInfoDialogFragment newInstance() {
        return new EditPersonalInfoDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit_personal_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireParentFragment()).get(ProfileViewModel.class);

        initViews(view);
        setupSpinner();
        setupButtons();
        loadCurrentData();
    }

    private void initViews(View view) {
        tilName = view.findViewById(R.id.tilName);
        tilAge = view.findViewById(R.id.tilAge);
        tilHeight = view.findViewById(R.id.tilHeight);
        tilWeight = view.findViewById(R.id.tilWeight);

        etName = view.findViewById(R.id.etName);
        etAge = view.findViewById(R.id.etAge);
        etHeight = view.findViewById(R.id.etHeight);
        etWeight = view.findViewById(R.id.etWeight);
        spinnerGender = view.findViewById(R.id.spinnerGender);

        btnCancel = view.findViewById(R.id.btnCancel);
        btnSave = view.findViewById(R.id.btnSave);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_dropdown_item_1line, genders);
        spinnerGender.setAdapter(adapter);
    }

    private void setupButtons() {
        btnCancel.setOnClickListener(v -> dismiss());
        btnSave.setOnClickListener(v -> saveData());
    }

    private void loadCurrentData() {
        UserInfo info = viewModel.getUserInfo().getValue();
        if (info != null) {
            etName.setText(info.getName());
            etAge.setText(String.valueOf(info.getAge()));
            etHeight.setText(String.valueOf(info.getHeight()));
            etWeight.setText(String.valueOf(info.getWeight()));
            spinnerGender.setText(info.getGenderDisplay(), false);
        }
    }

    private void saveData() {
        clearErrors();

        try {
            String name = etName.getText().toString().trim();
            int age = Integer.parseInt(etAge.getText().toString().trim());
            float height = Float.parseFloat(etHeight.getText().toString().trim());
            float weight = Float.parseFloat(etWeight.getText().toString().trim());
            String gender = spinnerGender.getText().toString().equals(genders[0]) ? "male" : "female";

            // Disable buttons during save
            setButtonsEnabled(false);

            ValidationResult result = viewModel.savePersonalInfo(name, age, gender, height, weight);

            if (result.isValid()) {
                Toast.makeText(requireContext(), "Đã lưu thông tin!", Toast.LENGTH_SHORT).show();
                dismiss();
            } else {
                setButtonsEnabled(true);
                showErrors(result);
            }
        } catch (NumberFormatException e) {
            setButtonsEnabled(true);
            Toast.makeText(requireContext(), "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        btnSave.setEnabled(enabled);
        btnCancel.setEnabled(enabled);
    }

    private void clearErrors() {
        tilName.setError(null);
        tilAge.setError(null);
        tilHeight.setError(null);
        tilWeight.setError(null);
    }

    private void showErrors(ValidationResult result) {
        if (result.hasError(ValidationResult.FIELD_NAME)) {
            tilName.setError(result.getError(ValidationResult.FIELD_NAME));
        }
        if (result.hasError(ValidationResult.FIELD_AGE)) {
            tilAge.setError(result.getError(ValidationResult.FIELD_AGE));
        }
        if (result.hasError(ValidationResult.FIELD_HEIGHT)) {
            tilHeight.setError(result.getError(ValidationResult.FIELD_HEIGHT));
        }
        if (result.hasError(ValidationResult.FIELD_WEIGHT)) {
            tilWeight.setError(result.getError(ValidationResult.FIELD_WEIGHT));
        }
    }
}
