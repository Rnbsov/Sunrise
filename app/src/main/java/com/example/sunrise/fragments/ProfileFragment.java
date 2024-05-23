package com.example.sunrise.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunrise.Login;
import com.example.sunrise.R;
import com.example.sunrise.adapters.NavigationAdapter;
import com.example.sunrise.navigation.ProfileNavigationRoutes;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class ProfileFragment extends Fragment {

    private ShapeableImageView profilePicture;
    private TextView usernameTextView;
    private TextView userEmailTextView;
    private FirebaseAuth mAuth;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup appbar menu options
        setupMenu();

        // Initialize Firebase Auth object
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        profilePicture = view.findViewById(R.id.profile_picture);
        usernameTextView = view.findViewById(R.id.username);
        userEmailTextView = view.findViewById(R.id.user_email);

        // Setup profile data
        setupProfileData();

        // Setup navigation routes
        setupNavigationRoutes(view);
    }

    /**
     * Sets up the profile data by retrieving information from Firebase Auth and setting it to the views.
     */
    private void setupProfileData() {
        Uri photoUrl = mAuth.getCurrentUser().getPhotoUrl();
        String username = mAuth.getCurrentUser().getDisplayName();
        String email = mAuth.getCurrentUser().getEmail();

        // Set username and email
        usernameTextView.setText(username);
        userEmailTextView.setText(email);

        // Load profile picture using Picasso
        Picasso.get()
                .load(photoUrl)
                .into(profilePicture);
    }

    /**
     * Sets up the navigation routes in the RecyclerView.
     */
    private void setupNavigationRoutes(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.navigation_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true); // Enable some optimization of recyclerView

        List<ProfileNavigationRoutes> navigationItems = Arrays.asList(ProfileNavigationRoutes.values());
        NavigationAdapter<ProfileNavigationRoutes> adapter = new NavigationAdapter<>(navigationItems, item -> {
            switch (item) {
                case Feedback -> handleFeedbackClick();
                case Settings -> {
                    Toast.makeText(requireContext(), "Settings click", Toast.LENGTH_LONG).show();
                }
                case About -> {
                    Toast.makeText(requireContext(), "About click", Toast.LENGTH_LONG).show();
                }
            }
        });

        recyclerView.setAdapter(adapter);
    }

    /**
     * Handles the click event for the feedback route.
     */
    private void handleFeedbackClick() {
        // Create an intent to open email application
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:contatornbsov@gmail.com"));
        try {
            startActivity(emailIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(requireContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Sets up the menu options for the profile fragment, such as sign out
     */
    private void setupMenu() {
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.profile_fragment_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();

                if (itemId == R.id.logout) {
                    showSignOutConfirmationDialog();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    /**
     * Shows an AlertDialog asking the user if they really want to sign out
     */
    private void showSignOutConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.dialog_title_sign_out))
                .setMessage(getString(R.string.dialog_message_sign_out))
                .setPositiveButton(getString(R.string.button_sign_out), (dialog, which) -> performSignOut())
                .setNegativeButton(getString(R.string.button_cancel), null)
                .show();
    }

    /**
     * Performs the sign-out process
     */
    private void performSignOut() {
        // handle logout
        FirebaseAuth.getInstance().signOut();

        // Send user to Login activity
        Intent intent = new Intent(getContext(), Login.class);
        startActivity(intent);

        // Finish the parent of fragments which is MainActivity
        if (getActivity() != null) {
            getActivity().finish();
        }

        // Notify user about successful sign out
        Toast.makeText(getContext(), getString(R.string.sign_out_success_message), Toast.LENGTH_SHORT).show();
    }
}