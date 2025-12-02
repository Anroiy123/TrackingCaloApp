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
import com.example.trackingcaloapp.data.local.entity.FoodEntry;
import com.example.trackingcaloapp.data.repository.FoodEntryRepository;
import com.example.trackingcaloapp.utils.DateUtils;

import java.util.List;

public class FoodEntriesFragment extends Fragment {

    private RecyclerView rvFoodEntries;
    private TextView tvEmpty;
    private FoodEntryAdapter adapter;
    private FoodEntryRepository repository;
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
        
        rvFoodEntries = view.findViewById(R.id.rvEntries);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        
        AppDatabase db = AppDatabase.getInstance(requireContext());
        repository = new FoodEntryRepository(db.foodEntryDao());
        
        adapter = new FoodEntryAdapter();
        rvFoodEntries.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvFoodEntries.setAdapter(adapter);
        
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

        LiveData<List<FoodEntry>> entriesLiveData = repository.getEntriesByDate(startOfDay, endOfDay);
        entriesLiveData.observe(getViewLifecycleOwner(), entries -> {
            if (entries != null && !entries.isEmpty()) {
                adapter.setEntries(entries);
                rvFoodEntries.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);
            } else {
                rvFoodEntries.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("Chưa có thực phẩm nào");
            }
        });
    }
}

