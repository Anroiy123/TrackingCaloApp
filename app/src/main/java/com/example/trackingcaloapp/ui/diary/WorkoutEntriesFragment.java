package com.example.trackingcaloapp.ui.diary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackingcaloapp.R;
import com.example.trackingcaloapp.data.local.database.AppDatabase;
import com.example.trackingcaloapp.data.local.entity.WorkoutEntry;
import com.example.trackingcaloapp.data.repository.WorkoutEntryRepository;
import com.example.trackingcaloapp.utils.DateUtils;

import java.util.List;

public class WorkoutEntriesFragment extends Fragment {

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
        
        adapter = new WorkoutEntryAdapter();
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

        LiveData<List<WorkoutEntry>> entriesLiveData = repository.getEntriesByDate(startOfDay, endOfDay);
        entriesLiveData.observe(getViewLifecycleOwner(), entries -> {
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
}

