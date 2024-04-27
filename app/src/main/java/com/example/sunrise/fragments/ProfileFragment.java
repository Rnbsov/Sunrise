package com.example.sunrise.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase Auth object
        mAuth = FirebaseAuth.getInstance();

        signOutBtn = view.findViewById(R.id.sign_out_btn);

        signOutBtn.setOnClickListener(v -> {
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
        });

        return view;
    }
}