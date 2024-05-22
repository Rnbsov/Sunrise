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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class TaskCreationHelper {
    private final Context context;
    private final TaskService taskService;
    private final TagService tagService;
    private final CategoryService categoryService;
    private final UserSettingsService userSettingsService;
    private List<String> selectedChipIds;
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
        loadDefaultCategory();

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
                    addTagChip(tag);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MainActivity", "fetch tags failed");
            }
        });
    }


    /**
     * Adds a tag chip to the {@link #tagChips} ChipGroup for the provided tag.
     *
     * @param tag the tag object containing the details to be displayed on the chip
     */
    private void addTagChip(Tag tag) {
        // Inflate a new chip from the layout
        Chip tagChip = (Chip) LayoutInflater.from(context).inflate(R.layout.filter_tag_chip_layout, null);

        // Set title and background color of the chip
        tagChip.setText(tag.getTitle());
        tagChip.setChipBackgroundColor(ColorStateList.valueOf(tag.getColor()));

        // Add a checked change listener to handle the selection state of the chip
        tagChip.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                // If the chip is checked, add the tag's id to the list of selected chip ids
                selectedChipIds.add(tag.getTagId());
            } else {
                // If the chip is unchecked, remove the tag's id from the list of selected chip ids
                selectedChipIds.remove(tag.getTagId());
            }
        });

        // Add the chip to the ChipGroup
        tagChips.addView(tagChip);
    }

    private void createTask(View view) {
        String title = Objects.requireNonNull(editTitle.getText()).toString();
        String priority = TaskUtils.getPriorityValue(priorityChip.getText().toString(), context.getString(R.string.priority), context.getString((R.string.priority_regular)));
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Check if the title is empty and show an error if it is
        if (title.isEmpty()) {
            titleInputLayout.setError("Please type title");
            return;
        }

        // Check if the category is selected (i.e., selectedCategoryId is not null or empty)
        if (selectedCategoryId == null || selectedCategoryId.isEmpty()) {
            // If no category is selected, show an error on the category chip
            categoryChip.setError("Please select a category");
            return;
        }

        // Remove duplicates if there is any
        selectedChipIds = new ArrayList<>(new HashSet<>(selectedChipIds));

        Task task = new Task(title, priority, selectedChipIds, selectedCategoryId, userId);

        // Save the newly created task to Firebase database
        taskService.saveTask(task);

        // Dismiss the bottom sheet dialog after task creation
        bottomSheetDialog.dismiss();
    }

    /**
     * Loads the default category from user settings and sets it as the selected category chip.
     * This method fetches the default categoryId from the user settings and then retrieves
     * the corresponding category from the database to display its title on the category chip.
     */
    private void loadDefaultCategory() {
        // Retrieve the default category id from user settings
        userSettingsService.getUserSettings(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Since the user have only one UserSettings object, we can directly retrieve first entry
                DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                UserSettings userSettings = snapshot.getValue(UserSettings.class);
                if (userSettings != null) {
                    String defaultCategoryId = userSettings.getDefaultCategoryId();

                    // Once the default category id is obtained, set the default category
                    setDefaultCategory(defaultCategoryId);
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

    /**
     * Sets the provided category id as the selected category chip.
     * This method retrieves the category details from the database using the given category id
     * and sets its title on the category chip along with saving default category id as {@link #selectedCategoryId}
     * also add category's default tag to {@link #selectedChipIds}
     *
     * @param defaultCategoryId the ID of the default category to be set
     */
    private void setDefaultCategory(String defaultCategoryId) {
        // Retrieve the category details from the database
        categoryService.getCategoryById(defaultCategoryId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Category category = dataSnapshot.getValue(Category.class);
                if (category != null) {
                    // Save default category id as class field
                    selectedCategoryId = category.getCategoryId();
                    // Set the title of the default category on the category chip
                    categoryChip.setText(category.getTitle());
                    // Add category's default tag
                    selectedChipIds.add(category.getDefaultTagId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TaskCreationHelper", "Failed to load default category");
            }
        });
    }
}
