package com.example.sunrise.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.sunrise.Login;
import com.example.sunrise.R;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    Button signOutBtn;
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

        // Initialize Firebase Auth object
        mAuth = FirebaseAuth.getInstance();

        setupMenu();
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
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        performSignOut();
                    }
                })
                .setNegativeButton("Cancel", null)
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
        Toast.makeText(getContext(), "Sign out success", Toast.LENGTH_SHORT).show();
    }
}