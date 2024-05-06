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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunrise.R;
import com.example.sunrise.adapters.TagsAdapter;
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
import java.util.Random;

public class TagsFragment extends Fragment {
    TextInputEditText editTagName;
    TextInputLayout tagNameInputLayout;
    private TagsAdapter adapter;
    private TagService tagService;
    private View fragment;
    private Chip colorChip;
    private BottomSheetDialog bottomSheetDialog;
    private Button createBtn;
    private List<Integer> colors;
    private ColorPickerDialog colorPickerDialog;
    private int selectedColor = Color.TRANSPARENT;

    public TagsFragment() {
        // Required empty public constructor
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

        setupExtendedFabButton();

        // Initialize colors
        List<Integer> colors = generateColors();

        RecyclerView tagsList = fragment.findViewById(R.id.tags_list);

        // Creating and setting linear layout manager to recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        tagsList.setLayoutManager(layoutManager);

        // Initialize adapter
        adapter = new TagsAdapter(new ArrayList<>(), this::OnTagClickListener);
        tagsList.setAdapter(adapter);

        // Add decoration so there is divider line between items
        tagsList.addItemDecoration(new DividerItemDecoration(tagsList.getContext(), DividerItemDecoration.VERTICAL));

        // Initialize TaskService
        tagService = new TagService();

        System.out.println("before fetchTags");
        // Fetch tasks
        fetchTagsFromDatabase();
    }

    private void OnTagClickListener(Tag tag) {
        System.out.println(tag.getColor() + tag.getTitle());
    }

    private void fetchTagsFromDatabase() {
        System.out.println("inside fetchTags");
        ValueEventListener tagsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Tag> tagList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Tag task = snapshot.getValue(Tag.class);
                    tagList.add(task);
                }

                Log.d("Firebase listener", "Firebase listener");
                for (Tag task : tagList) {
                    System.out.println(task);
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



    private void showColorsDialog(View view) {
        // Create and show color picker dialog
        colorPickerDialog = new ColorPickerDialog(requireContext(), colors, this::onColorSelected);
        colorPickerDialog.show();
    }

    private void onColorSelected(int color) {
        // Handle color selection here
        // Creating color state list, cause there is no way around it
        ColorStateList colorStateList = getColorStateList(color);

        // Setting selected color for icon
        colorChip.setChipIconTint(colorStateList);

        // Setting selected color for text
        colorChip.setTextColor(color);

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

        int color;
        if (selectedColor == Color.TRANSPARENT) {
            // If the user didn't choose any color, select a random color from the array
            Random random = new Random();
            color = colors.get(random.nextInt(colors.size()));
        } else {
            color = selectedColor; // Use the selected color
        }

        Tag tag = new Tag(tagName, color, userId);

        // Initialize TaskService to interact with Firebase database
        TagService tagService = new TagService();

        // Save the newly created task to Firebase database
        tagService.saveTag(tag);

        // Dismiss the bottom sheet dialog after task creation
        bottomSheetDialog.dismiss();
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

}