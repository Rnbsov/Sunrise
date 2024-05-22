package com.example.sunrise.helpers;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;

import com.example.sunrise.R;
import com.example.sunrise.models.Category;
import com.example.sunrise.models.Tag;
import com.example.sunrise.services.CategoryService;
import com.example.sunrise.services.TagService;
import com.example.sunrise.utils.ColorPickerDialog;
import com.example.sunrise.utils.ColorUtils;
import com.example.sunrise.utils.IconPickerDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class CategoryCreationHelper {

    private final Context context;
    private final CategoryService categoryService;
    private final List<Integer> colors;
    private final List<Integer> icons;
    private BottomSheetDialog createCategoryBottomSheetDialog;
    private ShapeableImageView setIcon;
    private TextInputLayout titleInputLayout;
    private TextInputEditText editTitle;
    private Chip defaultTagChip;
    private int selectedIconId = -1; // Initialized to -1 to indicate no icon selected initially ( sentinel value )
    private String selectedTagId;
    private int selectedColor = -1; // Initialized to -1 to indicate no color selected initially ( sentinel value )
    private ColorPickerDialog colorPickerDialog;
    private IconPickerDialog iconPickerDialog;

    public CategoryCreationHelper(Context context) {
        this.context = context;
        this.categoryService = new CategoryService();
        this.colors = initializeColors(); // Initialize colors
        this.icons = initializeIcons(); // Initialize icons
    }

    public void showCategoryCreationDialog(ViewGroup parent) {
        // Setup bottom sheet dialog
        createCategoryBottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetContentView = LayoutInflater.from(context).inflate(R.layout.create_category_bottom_sheet, (ViewGroup) parent, false);
        createCategoryBottomSheetDialog.setContentView(bottomSheetContentView);
        createCategoryBottomSheetDialog.show();

        // Find views
        setIcon = bottomSheetContentView.findViewById(R.id.set_icon);
        titleInputLayout = bottomSheetContentView.findViewById(R.id.category_title_input_layout);
        editTitle = bottomSheetContentView.findViewById(R.id.title);
        defaultTagChip = bottomSheetContentView.findViewById(R.id.default_tag);
        Button createBtn = bottomSheetContentView.findViewById(R.id.create_btn);

        // Setting on setIcon click listener
        setIcon.setOnClickListener(this::onSetIconClick);

        // Setting on defaultTagChip click listener
        defaultTagChip.setOnClickListener(this::showTagsPopupMenu);

        // Setting listener on save button
        createBtn.setOnClickListener(view -> createCategory());
    }

    private void onSetIconClick(View v) {
        // Create and show color picker dialog
        colorPickerDialog = new ColorPickerDialog(context, colors, this::onColorSelected);
        colorPickerDialog.show();

        // Create and show icon picker dialog
        iconPickerDialog = new IconPickerDialog(context, icons, this::onIconSelected);
        iconPickerDialog.show();
    }

    private void onColorSelected(int color) {
        selectedColor = color; // Save the selected color as class property

        // Adjust the brightness of the selected color to make it slightly darker
        int darkerColor = ColorUtils.darkenColor(color, 0.6f);
        setIcon.setBackgroundColor(darkerColor); // Set background color for imageView

        setIcon.setImageTintList(ColorStateList.valueOf(color)); // Set color for imageView icon

        // Dismiss the AlertDialog after color selection
        colorPickerDialog.dismiss();
    }

    private void onIconSelected(int iconResId) {
        selectedIconId = iconResId; // Save selected icon as class property
        setIcon.setImageResource(iconResId);
    }

    private void showTagsPopupMenu(View v) {
        PopupMenu popup = new PopupMenu(context, defaultTagChip);

        // Retrieve tags from the database
        retrieveTagsFromDatabase(new TagService(), tagIdMap -> {
            // Add tags to the popup menu
            for (Map.Entry<Integer, String> entry : tagIdMap.entrySet()) {
                popup.getMenu().add(0, entry.getKey(), 0, entry.getValue());
            }

            // Show popupMenu after all asynchronous stuff is done
            popup.show();

            popup.setOnMenuItemClickListener(item -> {
                // Handle tag selection
                int tagIdHashCode = item.getItemId();

                String selectedTagId = tagIdMap.get(tagIdHashCode); // Retrieve tagId using hash code

                // Store it like class var
                this.selectedTagId = selectedTagId;

                // Set selected tag's title to chip
                defaultTagChip.setText(item.getTitle());
                return true;
            });
        });
    }

    private void retrieveTagsFromDatabase(TagService tagService, TagsRetrievedCallback callback) {
        Map<Integer, String> tagIdMap = new HashMap<>(); // Map to store tag ids and their hash codes
        tagService.getTags(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Add tags to the tagIdMap
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Tag tag = snapshot.getValue(Tag.class);
                    if (tag != null) {
                        String tagId = tag.getTagId();
                        String tagName = tag.getTitle();
                        int tagIdHashCode = tagId.hashCode();  // Convert tagId to hashcode cause PopupMenu add item method needs int as id
                        tagIdMap.put(tagIdHashCode, tagName); // Store the relationship between hash code and tagId
                    }
                }

                // Invoke the callback with passed tagIdMap
                callback.onTagsRetrieved(tagIdMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CategoriesFragment", "Getting tags failed");
            }
        });
    }

    private void createCategory() {
        String title = Objects.requireNonNull(editTitle.getText()).toString();
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Some checks before creating category
        if (title.isEmpty()) {
            titleInputLayout.setError("Please type title");
            return;
        }

        // If selected color and icon is not set, get a random color and icon
        int categoryColor = selectedColor != -1 ? selectedColor : getRandomColor();
        int categoryIcon = selectedIconId != -1 ? selectedIconId : getRandomIcon();

        Category category = new Category(title, categoryColor, categoryIcon, selectedTagId, userId);

        // Save the newly created task to Firebase database
        categoryService.saveCategory(category);

        // Dismiss the bottom sheet dialog after category creation
        createCategoryBottomSheetDialog.dismiss();
    }

    private List<Integer> initializeColors() {
        List<Integer> colors = new ArrayList<>();

        // Define hexadecimal colors
        String[] hexValues = {
                "#FFCCCC", // Pastel Red
                "#FFE5CC", // Pastel Orange
                "#FFF2CC", // Pastel Yellow
                "#CCFFCC", // Pastel Green
                "#CCE5FF", // Pastel Blue
                "#FFCCFF"  // Pastel Purple
        };

        // Convert hexadecimal values to color integers and add them to the list
        for (String hex : hexValues) {
            int color = Color.parseColor(hex);
            colors.add(color);
        }

        return colors;
    }

    private List<Integer> initializeIcons() {
        List<Integer> icons = new ArrayList<>();

        // Initialize icons
        icons = Arrays.asList(
                R.drawable.label_24px,
                R.drawable.palette_24px,
                R.drawable.flower_24px
        );

        return icons;
    }

    private int getRandomColor() {
        Random random = new Random();
        return colors.get(random.nextInt(colors.size()));
    }

    private int getRandomIcon() {
        Random random = new Random();
        return icons.get(random.nextInt(icons.size()));
    }

    interface TagsRetrievedCallback {
        void onTagsRetrieved(Map<Integer, String> tagIdMap);
    }
}
