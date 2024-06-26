package com.example.sunrise.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunrise.R;
import com.example.sunrise.adapters.TagsAdapter;
import com.example.sunrise.constants.ColorsEnum;
import com.example.sunrise.models.Tag;
import com.example.sunrise.services.TagService;
import com.example.sunrise.utils.ColorPickerDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TagsFragment extends Fragment {
    TextInputEditText editTagName;
    TextInputLayout tagNameInputLayout;
    private TagsAdapter adapter;
    private TagService tagService;
    private View fragment;
    private Chip colorChip;
    private BottomSheetDialog bottomSheetDialog;
    private Button createBtn;
    private ColorPickerDialog colorPickerDialog;
    private ColorsEnum selectedColor = ColorsEnum.TRANSPARENT; // Set transparent as default ( sentinel value )

    public TagsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragment = inflater.inflate(R.layout.fragment_tags, container, false);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup fab
        setupExtendedFabButton();

        // Setup tags RecyclerView
        setupRecyclerView();

        // Initialize TaskService
        tagService = new TagService();

        // Fetch tasks
        fetchTagsFromDatabase();
    }

    private void setupRecyclerView() {
        RecyclerView tagsList = fragment.findViewById(R.id.tags_list);

        // Creating and setting linear layout manager to recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        tagsList.setLayoutManager(layoutManager);

        // Initialize adapter
        adapter = new TagsAdapter(new ArrayList<>(), this::OnTagClickListener);
        tagsList.setAdapter(adapter);

        // Add decoration so there is divider line between items
        tagsList.addItemDecoration(new DividerItemDecoration(tagsList.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void OnTagClickListener(Tag tag) {
        System.out.println(tag.getColor() + tag.getTitle());
    }

    private void fetchTagsFromDatabase() {
        ValueEventListener tagsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Tag> tagList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Tag task = snapshot.getValue(Tag.class);
                    tagList.add(task);
                }

                // Updating tagList in adapter
                adapter.setTags(tagList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Log.e("TagsFragment", "Error fetching tags", databaseError.toException());
            }
        };

        // Call getTasks method from TagService to register the listener
        tagService.getTags(tagsListener);
    }

    private void setupExtendedFabButton() {
        ExtendedFloatingActionButton extendedFab = fragment.findViewById(R.id.extendedFab);

        extendedFab.setOnClickListener(this::showTagCreationDialog);
    }

    private void showTagCreationDialog(View view) {
        Context context = requireContext();
        View fragmentRootView = requireView();

        bottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetContentView = LayoutInflater.from(context).inflate(R.layout.create_tag_bottom_sheet, (ViewGroup) fragmentRootView, false);
        bottomSheetDialog.setContentView(bottomSheetContentView);
        bottomSheetDialog.show();

        editTagName = bottomSheetContentView.findViewById(R.id.editTagName);
        createBtn = bottomSheetContentView.findViewById(R.id.create_btn);
        colorChip = bottomSheetContentView.findViewById(R.id.color);
        colorChip.setOnClickListener(this::showColorsDialog);

        createBtn.setOnClickListener(this::createTag);
    }

    private void showColorsDialog(View view) {
        // Create and show color picker dialog
        colorPickerDialog = new ColorPickerDialog(requireContext(), this::onColorSelected);
        colorPickerDialog.show();
    }

    private void onColorSelected(ColorsEnum color) {
        // Handle color selection here
        // Creating color state list, cause there is no way around it
        ColorStateList colorStateList = ColorStateList.valueOf(color.getColor());

        // Setting selected color for icon
        colorChip.setChipIconTint(colorStateList);

        // Setting selected color for text
        colorChip.setTextColor(color.getColor());

        selectedColor = color; // Save the selected color as class property

        // Dismiss the AlertDialog after color selection
            colorPickerDialog.dismiss();
    }

    private void createTag(View view) {
        String tagName = Objects.requireNonNull(editTagName.getText()).toString();
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Some validation
        if (tagName.isEmpty()) {
            tagNameInputLayout.setError("Please type title");
            return;
        }

        // Determine the color to use
        ColorsEnum color = determineSelectedColor();

        Tag tag = new Tag(tagName, color.getColor(), userId);

        // Initialize TaskService to interact with Firebase database
        TagService tagService = new TagService();

        // Save the newly created task to Firebase database
        tagService.saveTag(tag);

        // Dismiss the bottom sheet dialog after task creation
        bottomSheetDialog.dismiss();
    }

    /**
     * Determines the color to be used for the new tag.
     * <p>
     * If the user has not selected a color (i.e., the selected color is transparent),
     * this method will return a random color from the color picker dialog.
     * Otherwise, it will return the color that the user has selected.
     *
     * @return The color to be used for the new tag.
     */
    private ColorsEnum determineSelectedColor() {
        return selectedColor == ColorsEnum.TRANSPARENT ? colorPickerDialog.getRandomColor() : selectedColor;
    }
}