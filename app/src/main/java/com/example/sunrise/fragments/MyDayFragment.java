package com.example.sunrise.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sunrise.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyDayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyDayFragment extends Fragment {

    public MyDayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_day, container, false);
        return v;
    }
}