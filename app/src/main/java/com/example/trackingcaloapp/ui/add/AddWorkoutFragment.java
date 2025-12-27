package com.example.trackingcaloapp.ui.add;

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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.data.local.database.AppDatabase;
import com.example.trackingcaloapp.data.local.entity.Workout;
import com.example.trackingcaloapp.data.local.entity.WorkoutEntry;
import com.example.trackingcaloapp.data.repository.WorkoutEntryRepository;
import com.example.trackingcaloapp.data.repository.WorkoutRepository;
import com.example.trackingcaloapp.ui.addworkout.WorkoutAdapter;
import com.example.trackingcaloapp.utils.Constants;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class AddWorkoutFragment extends Fragment implements WorkoutAdapter.OnWorkoutClickListener {

    private TextInputEditText etSearch;
    private ChipGroup chipGroupCategory;
    private RecyclerView rvWorkouts;
    private TextView tvEmpty;
    private FloatingActionButton fabAddCustomWorkout;

    private WorkoutRepository workoutRepository;
    private WorkoutEntryRepository workoutEntryRepository;
    private WorkoutAdapter workoutAdapter;

    private String selectedCategory = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_workout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppDatabase db = AppDatabase.getInstance(requireContext());
        workoutRepository = new WorkoutRepository(db.workoutDao());
        workoutEntryRepository = new WorkoutEntryRepository(db.workoutEntryDao());

        initViews(view);
        setupSearch();
        setupCategoryChips(view);
        loadWorkouts();
    }


    private void initViews(View view) {
        etSearch = view.findViewById(R.id.etSearch);
        chipGroupCategory = view.findViewById(R.id.chipGroupCategory);
        rvWorkouts = view.findViewById(R.id.rvWorkouts);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        fabAddCustomWorkout = view.findViewById(R.id.fabAddCustomWorkout);

        workoutAdapter = new WorkoutAdapter(this);
        rvWorkouts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvWorkouts.setAdapter(workoutAdapter);

        // Setup FAB click listener
        fabAddCustomWorkout.setOnClickListener(v -> showCreateWorkoutDialog());
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    loadWorkouts();
                } else {
                    searchWorkouts(query);
                }
            }
        });
    }

    private void setupCategoryChips(View view) {
        Chip chipAll = view.findViewById(R.id.chipAll);
        Chip chipCardio = view.findViewById(R.id.chipCardio);
        Chip chipStrength = view.findViewById(R.id.chipStrength);
        Chip chipFlexibility = view.findViewById(R.id.chipFlexibility);

        chipGroupCategory.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;

            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chipAll) {
                selectedCategory = null;
            } else if (checkedId == R.id.chipCardio) {
                selectedCategory = Constants.WORKOUT_CARDIO;
            } else if (checkedId == R.id.chipStrength) {
                selectedCategory = Constants.WORKOUT_STRENGTH;
            } else if (checkedId == R.id.chipFlexibility) {
                selectedCategory = Constants.WORKOUT_FLEXIBILITY;
            }
            loadWorkouts();
        });
    }

    private void loadWorkouts() {
        LiveData<List<Workout>> workoutsLiveData;
        if (selectedCategory == null) {
            workoutsLiveData = workoutRepository.getAllWorkouts();
        } else {
            workoutsLiveData = workoutRepository.getWorkoutsByCategory(selectedCategory);
        }

        workoutsLiveData.observe(getViewLifecycleOwner(), workouts -> {
            if (workouts != null && !workouts.isEmpty()) {
                workoutAdapter.setWorkouts(workouts);
                rvWorkouts.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);
            } else {
                rvWorkouts.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void searchWorkouts(String query) {
        LiveData<List<Workout>> searchLiveData = workoutRepository.searchWorkouts(query);
        searchLiveData.observe(getViewLifecycleOwner(), workouts -> {
            if (workouts != null && !workouts.isEmpty()) {
                workoutAdapter.setWorkouts(workouts);
                rvWorkouts.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);
            } else {
                rvWorkouts.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onWorkoutClick(Workout workout) {
        showAddWorkoutDialog(workout);
    }

    @Override
    public void onWorkoutLongClick(Workout workout) {
        new AlertDialog.Builder(requireContext(), R.style.Theme_App_Dialog)
                .setTitle(workout.getName())
                .setItems(new String[]{"Chỉnh sửa", "Xóa"}, (dialog, which) -> {
                    if (which == 0) {
                        showEditWorkoutDialog(workout);
                    } else {
                        showDeleteWorkoutConfirmation(workout);
                    }
                })
                .show();
    }

    private void showAddWorkoutDialog(Workout workout) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_workout_entry, null);

        TextView tvWorkoutName = dialogView.findViewById(R.id.tvWorkoutName);
        TextView tvWorkoutInfo = dialogView.findViewById(R.id.tvWorkoutInfo);
        TextInputLayout tilQuantity = dialogView.findViewById(R.id.tilQuantity);
        TextInputEditText etQuantity = dialogView.findViewById(R.id.etQuantity);
        TextInputEditText etDuration = dialogView.findViewById(R.id.etDuration);
        TextInputEditText etNote = dialogView.findViewById(R.id.etNote);
        TextView tvCaloriesBurned = dialogView.findViewById(R.id.tvCaloriesBurned);

        tvWorkoutName.setText(workout.getName());
        String categoryName = Constants.getWorkoutCategoryName(workout.getCategory());
        tvWorkoutInfo.setText(String.format("%.0f cal/%s • %s",
                workout.getCaloriesPerUnit(), workout.getUnit(), categoryName));

        tilQuantity.setHint("Số lượng (" + workout.getUnit() + ")");

        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    float quantity = s.toString().isEmpty() ? 0 : Float.parseFloat(s.toString());
                    float calories = workout.calculateCaloriesBurned(quantity);
                    tvCaloriesBurned.setText(String.valueOf((int) calories));
                } catch (NumberFormatException e) {
                    tvCaloriesBurned.setText("0");
                }
            }
        });

        etQuantity.setText("30");

        new AlertDialog.Builder(requireContext(), R.style.Theme_App_Dialog)
                .setView(dialogView)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    try {
                        float quantity = Float.parseFloat(etQuantity.getText().toString());
                        String durationStr = etDuration.getText().toString();
                        int duration = durationStr.isEmpty() ? 0 : Integer.parseInt(durationStr);
                        String note = etNote.getText().toString();
                        addWorkoutEntry(workout, quantity, duration, note);
                    } catch (NumberFormatException e) {
                        // Ignore
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void addWorkoutEntry(Workout workout, float quantity, int duration, String note) {
        WorkoutEntry entry = new WorkoutEntry();
        entry.setWorkoutId(workout.getId());
        entry.setQuantity(quantity);
        entry.setDuration(duration);
        entry.setDate(System.currentTimeMillis());
        entry.setCaloriesBurned(workout.calculateCaloriesBurned(quantity));
        entry.setNote(note);

        workoutEntryRepository.insert(entry);
        Toast.makeText(requireContext(), "Đã thêm " + workout.getName(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Show dialog to create custom workout
     */
    private void showCreateWorkoutDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_create_custom_workout, null);

        TextInputEditText etName = dialogView.findViewById(R.id.etWorkoutName);
        TextInputEditText etCalories = dialogView.findViewById(R.id.etCaloriesPerUnit);
        AutoCompleteTextView spinnerUnit = dialogView.findViewById(R.id.spinnerUnit);
        AutoCompleteTextView spinnerCategory = dialogView.findViewById(R.id.spinnerCategory);

        // Setup unit spinner
        String[] units = {"phút", "km", "lần"};
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, units);
        spinnerUnit.setAdapter(unitAdapter);
        spinnerUnit.setText(units[0], false); // Default: "phút"

        // Setup category spinner
        String[] categoryNames = {"Cardio", "Sức mạnh", "Linh hoạt"};
        String[] categoryValues = {Constants.WORKOUT_CARDIO, Constants.WORKOUT_STRENGTH, Constants.WORKOUT_FLEXIBILITY};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, categoryNames);
        spinnerCategory.setAdapter(categoryAdapter);
        spinnerCategory.setText(categoryNames[0], false); // Default: "Cardio"

        new AlertDialog.Builder(requireContext(), R.style.Theme_App_Dialog)
                .setTitle(R.string.create_custom_workout)
                .setView(dialogView)
                .setPositiveButton("Tạo", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(requireContext(), R.string.error_name_required, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    float caloriesPerUnit = parseFloat(etCalories, 5);

                    // Validate calories > 0
                    if (caloriesPerUnit <= 0) {
                        Toast.makeText(requireContext(), R.string.error_calories_required, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String unit = spinnerUnit.getText().toString();
                    if (unit.isEmpty()) {
                        unit = "phút"; // Default
                    }

                    // Get selected category
                    int categoryIndex = java.util.Arrays.asList(categoryNames)
                            .indexOf(spinnerCategory.getText().toString());
                    String category = categoryIndex >= 0 ? categoryValues[categoryIndex] : Constants.WORKOUT_CARDIO;

                    // Create custom workout with isCustom = true
                    Workout workout = new Workout(name, caloriesPerUnit, unit, category, true);
                    workoutRepository.insert(workout);
                    Toast.makeText(requireContext(), R.string.workout_created, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    /**
     * Parse float from TextInputEditText with default value
     */
    private float parseFloat(TextInputEditText et, float defaultValue) {
        try {
            String text = et.getText().toString().trim();
            return text.isEmpty() ? defaultValue : Float.parseFloat(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Show dialog to edit custom workout
     */
    private void showEditWorkoutDialog(Workout workout) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_create_custom_workout, null);

        TextInputEditText etName = dialogView.findViewById(R.id.etWorkoutName);
        TextInputEditText etCalories = dialogView.findViewById(R.id.etCaloriesPerUnit);
        AutoCompleteTextView spinnerUnit = dialogView.findViewById(R.id.spinnerUnit);
        AutoCompleteTextView spinnerCategory = dialogView.findViewById(R.id.spinnerCategory);

        // Pre-fill values
        etName.setText(workout.getName());
        etCalories.setText(String.valueOf(workout.getCaloriesPerUnit()));

        // Setup unit spinner
        String[] units = {"phút", "km", "lần"};
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, units);
        spinnerUnit.setAdapter(unitAdapter);
        spinnerUnit.setText(workout.getUnit(), false);

        // Setup category spinner
        String[] categoryNames = {"Cardio", "Sức mạnh", "Linh hoạt"};
        String[] categoryValues = {Constants.WORKOUT_CARDIO, Constants.WORKOUT_STRENGTH, Constants.WORKOUT_FLEXIBILITY};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, categoryNames);
        spinnerCategory.setAdapter(categoryAdapter);

        // Set current category
        int currentCategoryIndex = java.util.Arrays.asList(categoryValues).indexOf(workout.getCategory());
        if (currentCategoryIndex >= 0) {
            spinnerCategory.setText(categoryNames[currentCategoryIndex], false);
        }

        new AlertDialog.Builder(requireContext(), R.style.Theme_App_Dialog)
                .setTitle(R.string.edit_workout)
                .setView(dialogView)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(requireContext(), R.string.error_name_required, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    float caloriesPerUnit = parseFloat(etCalories, 5);
                    if (caloriesPerUnit <= 0) {
                        Toast.makeText(requireContext(), R.string.error_calories_required, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String unit = spinnerUnit.getText().toString();
                    if (unit.isEmpty()) {
                        unit = "phút";
                    }

                    int categoryIndex = java.util.Arrays.asList(categoryNames)
                            .indexOf(spinnerCategory.getText().toString());
                    String category = categoryIndex >= 0 ? categoryValues[categoryIndex] : Constants.WORKOUT_CARDIO;

                    // Update workout object
                    workout.setName(name);
                    workout.setCaloriesPerUnit(caloriesPerUnit);
                    workout.setUnit(unit);
                    workout.setCategory(category);

                    workoutRepository.update(workout);
                    Toast.makeText(requireContext(), "Đã cập nhật", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    /**
     * Show confirmation dialog before deleting workout
     */
    private void showDeleteWorkoutConfirmation(Workout workout) {
        new AlertDialog.Builder(requireContext(), R.style.Theme_App_Dialog)
                .setTitle("Xóa bài tập?")
                .setMessage("Bạn có chắc muốn xóa \"" + workout.getName() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    workoutRepository.delete(workout);
                    Toast.makeText(requireContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
