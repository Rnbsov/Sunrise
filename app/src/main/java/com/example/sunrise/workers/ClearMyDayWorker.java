package com.example.sunrise.workers;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.sunrise.services.MyDayService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ClearMyDayWorker extends Worker {

    public ClearMyDayWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Clears tasks from MyDay
        clearMyDay();
        return Result.success();
    }

    private void clearMyDay() {
        // Initialize MyDayService
        MyDayService myDayService = new MyDayService();

        // Get userId
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Clear tasks from my day
        myDayService.clearMyDayTasks(userId);
    }
}
