package com.example.sunrise.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunrise.R;
import com.example.sunrise.adapters.CategoryAdapter;
import com.example.sunrise.models.Category;
import com.example.sunrise.models.Tag;
import com.example.sunrise.services.TagService;
import com.example.sunrise.utils.ColorPickerDialog;
import com.example.sunrise.utils.IconPickerDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CategoriesFragment extends Fragment {

    private RecyclerView categoriesRecyclerView;
    private CategoryAdapter categoryAdapter;
    private View fragment;
    private ShapeableImageView setIcon;
    private TextInputLayout titleInputLayout;
    private TextInputEditText editTitle;
    private Chip defaultTagChip;
    private int selectedIconId;
    private String selectedTagId;
    private int selectedColor;
    private ColorPickerDialog colorPickerDialog;
    private IconPickerDialog iconPickerDialog;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragment = inflater.inflate(R.layout.fragment_categories, container, false);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CardView tagsTile = fragment.findViewById(R.id.tags_tile);

        tagsTile.setOnClickListener(v -> {
            // Get the NavController
            NavController navController = Navigation.findNavController(view);

            // Navigating to tagsFragment
            navController.navigate(R.id.action_page_categories_to_tagsFragment);
        });

        // Initialize RecyclerView and adapter
        categoriesRecyclerView = fragment.findViewById(R.id.categories_recycler_view);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(Category category) {
                // Handle category item click
                Toast.makeText(requireContext(), "Clicked on category: " + category.getTitle(), Toast.LENGTH_SHORT).show();
            }
        }, this::onCategoryAddButtonClick);

        // Set adapter to RecyclerView
        categoriesRecyclerView.setAdapter(categoryAdapter);

        // Set categories to adapter

// Get the resource ID of the drawable
        int drawableResourceId = R.drawable.account_circle_24px;
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Category 1", Color.RED, drawableResourceId, "lalalal"));
        categories.add(new Category("Category 2", Color.BLUE, drawableResourceId, "lalalal"));

        categoryAdapter.setCategories(categories);
    }

    private void onCategoryAddButtonClick(View v) {
        Context context = requireContext();
        View fragmentRootView = requireView(); // Get the root view of the fragment

        // Setup bottom sheet dialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetContentView = LayoutInflater.from(context).inflate(R.layout.create_category_bottom_sheet, (ViewGroup) fragmentRootView, false);
        bottomSheetDialog.setContentView(bottomSheetContentView);
        bottomSheetDialog.show();

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
        Context context = requireContext();

        // Initialize colors
        List<Integer> colors = generateColors();

        // Create and show color picker dialog
        colorPickerDialog = new ColorPickerDialog(context, colors, this::onColorSelected);
        colorPickerDialog.show();

        // Initialize icons
        List<Integer> icons = Arrays.asList(
                R.drawable.label_24px,
                R.drawable.palette_24px,
                R.drawable.flower_24px
        );

        // Create and show icon picker dialog
        iconPickerDialog = new IconPickerDialog(context, icons, this::onIconSelected);
        iconPickerDialog.show();
    }

    private void onColorSelected(int color) {
        selectedColor = color; // Save the selected color as class property

        // Adjust the brightness of the selected color to make it slightly darker
        int darkerColor = darkenColor(color, 0.6f); //
        setIcon.setBackgroundColor(darkerColor); // Set background color for imageView

        ColorStateList colorStateList = getColorStateList(color);

        setIcon.setImageTintList(colorStateList); // Set color for imageView icon

        // Dismiss the AlertDialog after color selection
        colorPickerDialog.dismiss();
    }

    private void onIconSelected(int iconResId) {
        selectedIconId = iconResId;

        setIcon.setImageResource(iconResId);
    }

    private void showTagsPopupMenu(View v) {
        PopupMenu popup = new PopupMenu(requireContext(), defaultTagChip);

        TagService tagService = new TagService();
        Map<Integer, String> tagIdMap = new HashMap<>(); // Map to store tag IDs and their hash codes
        tagService.getTags(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Add tags to the popup menu
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Tag tag = snapshot.getValue(Tag.class);
                    String tagId = Objects.requireNonNull(tag).getTagId();
                    String tagName = tag.getTitle();

                    // Convert tagId to hashcode cause add method needs int
                    int tagIdHashCode = tagId.hashCode();
                    tagIdMap.put(tagIdHashCode, tagId); // Store the relationship between hash code and tagId
                    popup.getMenu().add(0, tagIdHashCode, 0, tagName);
                }

                // Show popupMenu after all asynchronous stuff is done
                popup.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CategoriesFragment", "Getting tags failed");
            }
        });
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
    }

    private void createCategory() {
    }

    private List<Integer> generateColors() {
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

    private static ColorStateList getColorStateList(int color) {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled},
                new int[]{android.R.attr.state_pressed}
        };

        int[] colors = new int[]{
                color,
                color // Set same color for both pressed and enabled states
        };

        return new ColorStateList(states, colors);
    }

    private int darkenColor(int color, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= factor; // Reduce brightness by the factor
        return Color.HSVToColor(hsv);
    }
}
