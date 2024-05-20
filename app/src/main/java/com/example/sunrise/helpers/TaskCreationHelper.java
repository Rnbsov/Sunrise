package com.example.sunrise.helpers;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.sunrise.R;
import com.example.sunrise.models.Category;
import com.example.sunrise.models.Tag;
import com.example.sunrise.models.Task;
import com.example.sunrise.models.UserSettings;
import com.example.sunrise.services.CategoryService;
import com.example.sunrise.services.TagService;
import com.example.sunrise.services.TaskService;
import com.example.sunrise.services.UserSettingsService;
import com.example.sunrise.utils.TaskUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskCreationHelper {
    private final Context context;
    private final TaskService taskService;
    private final TagService tagService;
    private final CategoryService categoryService;
    private final UserSettingsService userSettingsService;
    private final List<String> selectedChipIds;
    private BottomSheetDialog bottomSheetDialog;
    private TextInputEditText editTitle;
    private TextInputLayout titleInputLayout;
    private Chip priorityChip;
    private Chip categoryChip;
    private ChipGroup tagChips;
    private String selectedCategoryId;

    public TaskCreationHelper(Context context) {
        this.context = context;
        this.selectedChipIds = new ArrayList<>();

        // Initialize services to interact with Firebase database
        this.taskService = new TaskService();
        this.tagService = new TagService();
        this.categoryService = new CategoryService();
        this.userSettingsService = new UserSettingsService();
    }

    public void showTaskCreationDialog(View v) {
        bottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetContentView = LayoutInflater.from(context).inflate(R.layout.create_task_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetContentView);
        bottomSheetDialog.show();

        titleInputLayout = bottomSheetContentView.findViewById(R.id.titleInputLayout);
        editTitle = bottomSheetContentView.findViewById(R.id.title);
        Button createBtn = bottomSheetContentView.findViewById(R.id.create_btn);

        priorityChip = bottomSheetContentView.findViewById(R.id.priority);
        priorityChip.setOnClickListener(view -> TaskUtils.showPriorityPopupMenu(context, view, priorityChip));

        categoryChip = bottomSheetContentView.findViewById(R.id.category);
        categoryChip.setOnClickListener(view -> TaskUtils.showCategoriesPopupMenu(context, view, categoryChip, categoryService, this::onCategorySelected));

        // Load and set default category
        setDefaultCategory();

        // Tags row
        tagChips = bottomSheetContentView.findViewById(R.id.tagChips);
        populateTagChips();

        createBtn.setOnClickListener(this::createTask);
    }

    private void onCategorySelected(String selectedCategoryId, String selectedCategoryName) {
        // Store it like class var
        this.selectedCategoryId = selectedCategoryId;

        // Set selected category's title to chip
        categoryChip.setText(selectedCategoryName);
    }

    private void populateTagChips() {
        tagService.getTags(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Tag> tagList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Tag tag = snapshot.getValue(Tag.class);
                    tagList.add(tag);
                }

                for (Tag tag : tagList) {
                    Chip tagChip = (Chip) LayoutInflater.from(context).inflate(R.layout.filter_tag_chip_layout, null);
                    tagChip.setText(tag.getTitle());
                    tagChip.setChipBackgroundColor(ColorStateList.valueOf(tag.getColor()));

                    tagChip.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                        if (isChecked) {
                            selectedChipIds.add(tag.getTagId());
                        } else {
                            selectedChipIds.remove(tag.getTagId());
                        }
                    });

                    tagChips.addView(tagChip);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MainActivity", "fetch tags failed");
            }
        });
    }

    private void createTask(View view) {
        String title = Objects.requireNonNull(editTitle.getText()).toString();
        String priority = TaskUtils.getPriorityValue(priorityChip.getText().toString(), context.getString(R.string.priority), context.getString((R.string.priority_regular)));
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        if (title.isEmpty()) {
            titleInputLayout.setError("Please type title");
            return;
        }

        Task task = new Task(title, priority, selectedChipIds, selectedCategoryId, userId);

        // Save the newly created task to Firebase database
        taskService.saveTask(task);

        // Dismiss the bottom sheet dialog after task creation
        bottomSheetDialog.dismiss();
    }

    private void setDefaultCategory() {
        userSettingsService.getUserSettings(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Since the user have only one UserSettings object, we can directly retrieve first entry
                DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                UserSettings userSettings = snapshot.getValue(UserSettings.class);
                if (userSettings != null) {
                    String defaultCategoryId = userSettings.getDefaultCategoryId();
                    categoryService.getCategoryById(defaultCategoryId, new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Category category = dataSnapshot.getValue(Category.class);
                            if (category != null) {
                                selectedCategoryId = category.getCategoryId();
                                categoryChip.setText(category.getTitle());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("TaskCreationHelper", "Failed to load default category");
                        }
                    });
                } else {
                    Log.e("TaskCreationHelper", "UserSettings is null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TaskCreationHelper", "Failed to load user settings");
            }
        });
    }
}
