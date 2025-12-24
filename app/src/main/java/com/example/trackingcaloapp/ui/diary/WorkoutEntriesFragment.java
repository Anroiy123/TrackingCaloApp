package com.example.trackingcaloapp.ui.diary;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.ItemTouchHelper;
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
        
        // Setup swipe-to-delete
        setupSwipeToDelete();

        loadData();
        
        return view;
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private final ColorDrawable background = new ColorDrawable(Color.parseColor("#F44336"));

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                WorkoutEntry entry = adapter.getEntryAt(position);

                if (entry != null) {
                    new AlertDialog.Builder(requireContext())
                            .setTitle(R.string.confirm_delete)
                            .setMessage(R.string.swipe_to_delete)
                            .setPositiveButton(R.string.delete, (dialog, which) -> {
                                repository.deleteById(entry.getId());
                                Toast.makeText(requireContext(), R.string.entry_deleted, Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton(R.string.cancel, (dialog, which) -> adapter.notifyItemChanged(position))
                            .setOnCancelListener(dialog -> adapter.notifyItemChanged(position))
                            .show();
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                if (dX < 0) {
                    background.setBounds(
                            itemView.getRight() + (int) dX,
                            itemView.getTop(),
                            itemView.getRight(),
                            itemView.getBottom()
                    );
                    background.draw(c);
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(rvWorkoutEntries);
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

