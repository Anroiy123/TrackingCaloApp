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
import com.example.trackingcaloapp.data.repository.FoodEntryRepository;
import com.example.trackingcaloapp.model.FoodWithEntry;
import com.example.trackingcaloapp.utils.DateUtils;

public class FoodEntriesFragment extends Fragment 
        implements FoodEntryAdapter.OnFoodEntryClickListener {

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
        
        adapter = new FoodEntryAdapter(this);
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

        repository.getEntriesWithFoodByDate(startOfDay, endOfDay).observe(getViewLifecycleOwner(), entries -> {
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

    @Override
    public void onFoodEntryClick(FoodWithEntry entry) {
        // Optional: Show details or edit dialog
    }

    @Override
    public void onFoodEntryLongClick(FoodWithEntry entry) {
        showDeleteConfirmationDialog(entry);
    }

    private void showDeleteConfirmationDialog(FoodWithEntry entry) {
        new AlertDialog.Builder(requireContext(), R.style.Theme_App_Dialog)
                .setTitle("Xoá thực phẩm?")
                .setMessage("Bạn có chắc muốn xoá \"" + entry.getFoodName() + "\"?")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    repository.deleteById(entry.getEntryId());
                    Toast.makeText(requireContext(), "Đã xoá", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }
}
