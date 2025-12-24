package com.example.trackingcaloapp.ui.addworkout;

import android.app.Dialog;
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
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.data.local.database.AppDatabase;
import com.example.trackingcaloapp.data.local.entity.Workout;
import com.example.trackingcaloapp.data.local.entity.WorkoutEntry;
import com.example.trackingcaloapp.data.repository.WorkoutEntryRepository;
import com.example.trackingcaloapp.data.repository.WorkoutRepository;
import com.example.trackingcaloapp.utils.Constants;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

/**
 * Bottom Sheet Dialog để thêm bài tập
 */
public class AddWorkoutDialogFragment extends BottomSheetDialogFragment implements WorkoutAdapter.OnWorkoutClickListener {

    private TextInputEditText etSearch;
    private ChipGroup chipGroupCategory;
    private RecyclerView rvWorkouts;
    private TextView tvEmpty;

    private WorkoutRepository workoutRepository;
    private WorkoutEntryRepository workoutEntryRepository;
    private WorkoutAdapter workoutAdapter;

    private String selectedCategory = null;

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
        setupCategoryChips();
        loadWorkouts();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Expand bottom sheet
        Dialog dialog = getDialog();
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
            }
        }
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.etSearch);
        chipGroupCategory = view.findViewById(R.id.chipGroupCategory);
        rvWorkouts = view.findViewById(R.id.rvWorkouts);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        workoutAdapter = new WorkoutAdapter(this);
        rvWorkouts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvWorkouts.setAdapter(workoutAdapter);
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

    private void setupCategoryChips() {
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
        showAddWorkoutEntryDialog(workout);
    }

    private void showAddWorkoutEntryDialog(Workout workout) {
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

        new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    try {
                        float quantity = Float.parseFloat(etQuantity.getText().toString());
                        int duration = 0;
                        String durationText = etDuration.getText().toString();
                        if (!durationText.isEmpty()) {
                            duration = Integer.parseInt(durationText);
                        }
                        String note = etNote.getText().toString().trim();
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
        entry.setNote(note);
        entry.setDate(System.currentTimeMillis());
        entry.setCaloriesBurned(workout.calculateCaloriesBurned(quantity));

        workoutEntryRepository.insert(entry);

        Toast.makeText(requireContext(), R.string.workout_entry_added, Toast.LENGTH_SHORT).show();
        dismiss(); // Close bottom sheet
    }
}

