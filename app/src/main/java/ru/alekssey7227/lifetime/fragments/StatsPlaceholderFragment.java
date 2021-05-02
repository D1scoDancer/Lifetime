package ru.alekssey7227.lifetime.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ru.alekssey7227.lifetime.R;
import ru.alekssey7227.lifetime.backend.Goal;
import ru.alekssey7227.lifetime.backend.StatsUnit;
import ru.alekssey7227.lifetime.database.GoalDBHelper;
import ru.alekssey7227.lifetime.database.StatsDBHelper;
import ru.alekssey7227.lifetime.others.StatsPageViewModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class StatsPlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private StatsPageViewModel statsPageViewModel;

    public static StatsPlaceholderFragment newInstance(int index) {
        StatsPlaceholderFragment fragment = new StatsPlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statsPageViewModel = new ViewModelProvider(this).get(StatsPageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        statsPageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
            case 1:
                root = inflater.inflate(R.layout.fragment_stats_pie, container, false);
                createPieChart(root);
                break;
            case 2:
                root = inflater.inflate(R.layout.fragment_stats_bar, container, false);
                createBarChart(root);

                break;
            case 3:
                root = inflater.inflate(R.layout.fragment_stats_radar, container, false);
                createRadarChart(root);
                break;
            default:
                return null;
        }
        return root;
    }

    private void createPieChart(View root) {
        PieChart pieChart = root.findViewById(R.id.pieChart);

        GoalDBHelper goalDBHelper = new GoalDBHelper(root.getContext());
        SQLiteDatabase db = goalDBHelper.getReadableDatabase();
        List<Goal> goals = goalDBHelper.getAllByDescendingTime(db);

        ArrayList<PieEntry> totalTime = new ArrayList<>();

        int maximum = 10;

        for (int i =0; i < maximum && i < goals.size(); i++) {
            if (goals.get(i).getTime().getTimeInHours() != 0) {
                totalTime.add(new PieEntry((float) goals.get(i).getTime().getTimeInHours(), goals.get(i).getName()));
            }
        }

        PieDataSet pieDataSet = new PieDataSet(totalTime, "");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        for (int color: ColorTemplate.JOYFUL_COLORS){
            pieDataSet.addColor(color);
        }
        for (int color: ColorTemplate.LIBERTY_COLORS){
            pieDataSet.addColor(color);
        }

        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Total"); //TODO: оставляю для информативности
        pieChart.setCenterTextSize(18f);
        pieChart.animate();

        pieChart.notifyDataSetChanged();
        pieChart.invalidate();
    }

    private void createBarChart(View root) {
        BarChart barChart = root.findViewById(R.id.barChart);

        StatsDBHelper statsDBHelper = new StatsDBHelper(root.getContext());
        SQLiteDatabase db2 = statsDBHelper.getReadableDatabase();

        ArrayList<BarEntry> hoursPerDay = new ArrayList<>();

        List<Calendar> calendars = statsDBHelper.getAllDates(db2);

        ArrayList<Float> timeList = new ArrayList<>();

        for (Calendar calendar : calendars) {
            List<StatsUnit> dayUnits = statsDBHelper.getByDate(db2, calendar);
            long time = 0;
            for (StatsUnit unit : dayUnits) {
                time += unit.getEstimatedTime().getTimeInMinutes();
            }
            timeList.add((float) (time / 60.0));
        }

        for (int i = 0; i < calendars.size(); i++) {
            hoursPerDay.add(new BarEntry(i, timeList.get(i)));
        }

        BarDataSet barDataSet = new BarDataSet(hoursPerDay, "Total"); //TODO: оставляю для информативности
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(12f);
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == (int) value)
                    return String.format(Locale.ENGLISH, "%d", (int) value);
                else
                    return String.format(Locale.ENGLISH, "%s", value);
            }
        });

        BarData barData = new BarData(barDataSet);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();

        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int i = (int) value;
                Calendar calendar = calendars.get(i);
                String day = String.format(Locale.ENGLISH, "%02d", calendar.get(Calendar.DAY_OF_MONTH));
                String month = String.format(Locale.ENGLISH, "%02d", calendar.get(Calendar.MONTH) + 1);
                return day + "." + month;
            }
        });

        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisRight().setAxisMinimum(0f);

        barChart.setDrawValueAboveBar(false);

        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }


    private void createRadarChart(View root) {
        RadarChart radarChart = root.findViewById(R.id.radarChart);

        GoalDBHelper goalDBHelper = new GoalDBHelper(root.getContext());
        SQLiteDatabase db = goalDBHelper.getReadableDatabase();

        StatsDBHelper statsDBHelper = new StatsDBHelper(root.getContext());

        List<Goal> goals = goalDBHelper.readAllGoals(db);
        int n = goals.size();

        if (n < 3) {
            radarChart.setNoDataTextColor(Color.BLACK);
            radarChart.setNoDataTextTypeface(Typeface.DEFAULT_BOLD);
            radarChart.setNoDataText("Not enough goals. Need at least 3");
            return;
        }

        ArrayList<Float> timeList = new ArrayList<>();

        for (Goal goal : goals) {
            List<StatsUnit> goalUnits = statsDBHelper.get(goal.getId());
            long time = 0;
            for (StatsUnit unit : goalUnits) {
                time += unit.getEstimatedTime().getTimeInMinutes();
            }
            timeList.add((float) (time / 60.0));
        }
        ArrayList<RadarEntry> activity = new ArrayList<>();

        for (Float f : timeList) {
//            if (f > 0f) {
            activity.add(new RadarEntry(f));
//            }
        }

        RadarDataSet radarDataSet = new RadarDataSet(activity, "Activity"); //TODO: оставляю для информативности
        radarDataSet.setColor(Color.RED);
        radarDataSet.setLineWidth(2f);
        radarDataSet.setValueTextColor(Color.RED);
        radarDataSet.setValueTextSize(10f);

        RadarData radarData = new RadarData();
        radarData.addDataSet(radarDataSet);

        String[] labels = new String[n];
        for (int i = 0; i < n; i++) {
            labels[i] = goals.get(i).getName();
        }

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setTextSize(9f);

        radarChart.getDescription().setEnabled(false);
        radarChart.setData(radarData);

        radarChart.notifyDataSetChanged();
        radarChart.invalidate();
    }
}