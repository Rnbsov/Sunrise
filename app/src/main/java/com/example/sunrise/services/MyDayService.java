package com.example.sunrise.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.sunrise.models.MyDay;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyDayService {
    private final DatabaseReference myDayRef;

    public MyDayService() {
        // Initialize Firebase database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myDayRef = database.getReference("MyDay");
    }

    public void createMyDay(String userId) {
        MyDay myDay = new MyDay(userId);
        myDayRef.child(userId).setValue(myDay);
    }

    /**
     * Method to update a MyDay in Firebase database
     */
    public void updateMyDay(MyDay myDay) {
        // Update the updatedAt timestamp before saving
        myDay.setUpdatedAt(System.currentTimeMillis());

        // Get the reference to the myDay's location in Firebase using its user id
        DatabaseReference userMyDayRef = myDayRef.child(myDay.getUserId());

        // Update the myDay at the specified location in Firebase
        userMyDayRef.setValue(myDay);
    }

    public void addTask(String userId, String taskId) {
        DatabaseReference userMyDayRef = myDayRef.child(userId);
        userMyDayRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    MyDay myDay = dataSnapshot.getValue(MyDay.class);

                    List<String> taskIds = myDay.getMyDayTaskIds();

                    // Check if taskIds is null and initialize it if needed
                    if (taskIds == null) {
                        taskIds = new ArrayList<>();
                    }

                    if (!taskIds.contains(taskId)) {
                        taskIds.add(taskId);
                        myDay.setMyDayTaskIds(taskIds);

                        // Update MyDay in Firebase
                        updateMyDay(myDay);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MyDayService", "Failed to add task to my day: " + databaseError.getMessage());
            }
        });
    }

    public void removeTask(String userId, String taskId) {
        DatabaseReference userMyDayRef = myDayRef.child(userId);
        userMyDayRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    MyDay myDay = dataSnapshot.getValue(MyDay.class);

                    List<String> taskIds = myDay.getMyDayTaskIds();

                    // Check if taskIds is null and initialize it if needed
                    if (taskIds == null) {
                        taskIds = new ArrayList<>();
                    }

                    // Remove taskId if it exists in the list
                    if (taskIds.contains(taskId)) {
                        taskIds.remove(taskId);
                        myDay.setMyDayTaskIds(taskIds);

                        // Update MyDay in Firebase
                        updateMyDay(myDay);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MyDayService", "Failed to remove task from my day: " + databaseError.getMessage());
            }
        });
    }

    public void clearMyDayTasks(String userId) {
        DatabaseReference userMyDayRef = myDayRef.child(userId);
        userMyDayRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    MyDay myDay = dataSnapshot.getValue(MyDay.class);

                    // Clear the taskIds list
                    myDay.setMyDayTaskIds(new ArrayList<>());

                    // Update MyDay in Firebase
                    updateMyDay(myDay);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MyDayService", "Failed to clear tasks from my day: " + databaseError.getMessage());
            }
        });
    }
}
