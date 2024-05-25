package com.example.sunrise.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sunrise.R;
import com.example.sunrise.models.Task;
import com.example.sunrise.services.TagService;
import com.example.sunrise.services.TaskService;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    private PieChart pieChart;
    private TaskService taskService;
    private TagService tagService;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find pieChart view
        pieChart = view.findViewById(R.id.pieChart);

        // Initialize services
        taskService = new TaskService();
        tagService = new TagService();

        // Fetch tasks and populate the pie chart
        fetchTasksAndSetupPieChart();
    }

    /**
     * Fetches tasks from the database and sets up the pie chart.
     */
    private void fetchTasksAndSetupPieChart() {
        // Retrieve tasks from the database using TaskService
        taskService.getTasks(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Create a list to hold tasks
                List<Task> tasks = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    if (task != null) {
                        tasks.add(task);
                    }
                }

                // Get the tag distribution and fetch tag titles
                Map<String, Integer> tagDistribution = getTagDistribution(tasks);
                fetchTagTitlesAndSetupPieChart(tagDistribution);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("StatisticsFragment", databaseError.getMessage());
            }
        });
    }

    /**
     * Calculates the distribution of tags among the tasks.
     *
     * @param tasks the list of tasks
     * @return a map where keys are tag IDs and values are their counts
     */
    private Map<String, Integer> getTagDistribution(List<Task> tasks) {
        // Create a map to hold the count of each tag
        Map<String, Integer> tagCountMap = new HashMap<>();

        // Iterate through each task
        for (Task task : tasks) {
            // Iterate through each tag in the task
            for (String tag : task.getTags()) {
                // Update the count of the tag in the map
                tagCountMap.put(tag, tagCountMap.getOrDefault(tag, 0) + 1);
            }
        }

        return tagCountMap;
    }

    /**
     * Fetches the titles of tags and sets up the pie chart.
     *
     * @param tagDistribution the distribution of tags
     */
    private void fetchTagTitlesAndSetupPieChart(Map<String, Integer> tagDistribution) {
        // Extract the tag IDs from the distribution map
        List<String> tagIds = new ArrayList<>(tagDistribution.keySet());

        // Retrieve the tag titles using TagService
        tagService.retrieveTagTitlesByTagIds(tagIds, tagTitles -> {
            // Prepare the pie chart data with the tag titles as labels
            List<PieEntry> pieEntries = preparePieChartData(tagDistribution, tagTitles);

            // Setup the pie chart with the prepared data
            setupPieChart(pieEntries);
        });
    }

    /**
     * Prepares the data for the pie chart.
     *
     * @param tagDistribution the distribution of tags
     * @param tagTitles the titles of tags
     * @return a list of PieEntry objects
     */
    private List<PieEntry> preparePieChartData(Map<String, Integer> tagDistribution, Map<String, String> tagTitles) {
        // Create a list to hold PieEntry objects
        List<PieEntry> entries = new ArrayList<>();

        // Iterate through the tag distribution map
        for (Map.Entry<String, Integer> entry : tagDistribution.entrySet()) {
            // Get the tag ID and count
            String tagId = entry.getKey();
            int count = entry.getValue();

            // Get the title for the tag ID
            String tagTitle = tagTitles.get(tagId);

            // Create a PieEntry object with the count and title
            entries.add(new PieEntry(entry.getValue(), tagTitle));
        }

        return entries;
    }

    /**
     * Sets up the pie chart with the provided data entries.
     *
     * @param pieEntries the list of PieEntry objects
     */
    private void setupPieChart(List<PieEntry> pieEntries) {
        // Create a PieDataSet object with the pie entries and a label
        PieDataSet dataSet = new PieDataSet(pieEntries, "Task Tags");

        // Set the colors for the data set
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        // Create a PieData object with the data set
        PieData pieData = new PieData(dataSet);

        // Set this data to the pie chart
        pieChart.setData(pieData);

        pieChart.invalidate(); // refresh the pie chart
    }
}
