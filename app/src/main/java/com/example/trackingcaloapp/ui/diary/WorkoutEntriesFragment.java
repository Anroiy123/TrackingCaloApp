package com.example.trackingcaloapp.ui.diary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.data.local.database.AppDatabase;
import com.example.trackingcaloapp.data.repository.WorkoutEntryRepository;
import com.example.trackingcaloapp.model.WorkoutWithEntry;
import com.example.trackingcaloapp.utils.DateUtils;

public class WorkoutEntriesFragment extends Fragment 
        implements WorkoutEntryAdapter.OnWorkoutEntryClickListener {

    private RecyclerView rvWorkoutEntries;
    private TextView tvEmpty;
    private WorkoutEntryAdapter adapter;
    private WorkoutEntryRepository repository;
    private long selectedDate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedDate = getArguments().getLong("selectedDate", System.currentTimeMillis());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entries_list, container, false);
        
        rvWorkoutEntries = view.findViewById(R.id.rvEntries);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        
        AppDatabase db = AppDatabase.getInstance(requireContext());
        repository = new WorkoutEntryRepository(db.workoutEntryDao());
        
        adapter = new WorkoutEntryAdapter(this);
        rvWorkoutEntries.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvWorkoutEntries.setAdapter(adapter);
        
        loadData();
        
        return view;
    }

    public void updateDate(long newDate) {
        this.selectedDate = newDate;
        if (isAdded()) {
            loadData();
        }
    }

    private void loadData() {
        long startOfDay = DateUtils.getStartOfDay(selectedDate);
        long endOfDay = DateUtils.getEndOfDay(selectedDate);

        repository.getEntriesWithWorkoutByDate(startOfDay, endOfDay).observe(getViewLifecycleOwner(), entries -> {
            if (entries != null && !entries.isEmpty()) {
                adapter.setEntries(entries);
                rvWorkoutEntries.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);
            } else {
                rvWorkoutEntries.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("Chưa có bài tập nào");
            }
        });
    }

    @Override
    public void onWorkoutEntryClick(WorkoutWithEntry entry) {
        // Optional: Show details or edit dialog
    }

    @Override
    public void onWorkoutEntryLongClick(WorkoutWithEntry entry) {
        showDeleteConfirmationDialog(entry);
    }

    private void showDeleteConfirmationDialog(WorkoutWithEntry entry) {
        new AlertDialog.Builder(requireContext(), R.style.Theme_App_Dialog)
                .setTitle("Xoá bài tập?")
                .setMessage("Bạn có chắc muốn xoá \"" + entry.getWorkoutName() + "\"?")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    repository.deleteById(entry.getEntryId());
                    Toast.makeText(requireContext(), "Đã xoá", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }
}
