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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class AddWorkoutFragment extends Fragment implements WorkoutAdapter.OnWorkoutClickListener {

    private TextInputEditText etSearch;
    private ChipGroup chipGroupCategory;
    private RecyclerView rvWorkouts;
    private TextView tvEmpty;

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

        new AlertDialog.Builder(requireContext())
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
}
