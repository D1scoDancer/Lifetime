package ru.alekssey7227.lifetime.fragments;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
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
import java.util.Collection;
import java.util.Collections;
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

        for (int i = 1, j = goals.size() - 1; i < goals.size() / 2 && j > goals.size() / 2; i+=2, j--)
        {
            Goal temp = goals.get(i);
            goals.set(i, goals.get(j));
            goals.set(j, temp);
        }

        ArrayList<PieEntry> totalTime = new ArrayList<>();

        int maximum = 10;

        for (int i = 0; i < maximum && i < goals.size(); i++) {

            if (goals.get(i).getTime().getTimeInHours() != 0) {
                String label = goals.get(i).getName();
                if (label.length() > 10) {
                    label = label.substring(0, 8) + "..";
                }
                totalTime.add(new PieEntry((float) goals.get(i).getTime().getTimeInHours(), label));
            }
        }

        PieDataSet pieDataSet = new PieDataSet(totalTime, "");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        for (int color : ColorTemplate.JOYFUL_COLORS) {
            pieDataSet.addColor(color);
        }
        for (int color : ColorTemplate.LIBERTY_COLORS) {
            pieDataSet.addColor(color);
        }

        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                long v = Math.round(value * 100.0);

                if (v % 100 == 0) {
                    return String.format(Locale.ENGLISH, "%d", (int) (v / 100.0));
                } else if (v % 10 == 0) {
                    return String.format(Locale.ENGLISH, "%.1f", v / 100.0);
                } else {
                    return String.format(Locale.ENGLISH, "%.2f", v / 100.0);
                }
            }
        });

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText(getString(R.string.piechart_center));
        pieChart.setCenterTextSize(18f);
        pieChart.animate();

        pieChart.getLegend().setWordWrapEnabled(true);

        int nightMode = root.getContext().getSharedPreferences("night_mode", Context.MODE_PRIVATE).getInt("mode", -1);
        if(nightMode == AppCompatDelegate.MODE_NIGHT_YES){
            pieChart.getLegend().setTextColor(Color.WHITE);
            pieChart.setCenterTextColor(Color.WHITE);
            pieChart.setHoleColor(Color.parseColor("#121212"));
        }

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

        BarDataSet barDataSet = new BarDataSet(hoursPerDay, "Total");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(12f);
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                long v = Math.round(value * 100.0);

                if (v % 100 == 0) {
                    return String.format(Locale.ENGLISH, "%d", (int) (v / 100.0));
                } else if (v % 10 == 0) {
                    return String.format(Locale.ENGLISH, "%.1f", v / 100.0);
                } else {
                    return String.format(Locale.ENGLISH, "%.2f", v / 100.0);
                }
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
        barChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                long v = Math.round(value * 100.0);

                if (v % 100 == 0) {
                    return String.format(Locale.ENGLISH, "%d", (int) (v / 100.0));
                } else
                    return String.format(Locale.ENGLISH, "%.1f", v / 100.0);
                }
        });
        barChart.getAxisRight().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                long v = Math.round(value * 100.0);

                if (v % 100 == 0) {
                    return String.format(Locale.ENGLISH, "%d", (int) (v / 100.0));
                } else {
                    return String.format(Locale.ENGLISH, "%.1f", v / 100.0);
                }
            }
        });

        barChart.setDrawValueAboveBar(false);

        barChart.getLegend().setEnabled(false);

        int nightMode = root.getContext().getSharedPreferences("night_mode", Context.MODE_PRIVATE).getInt("mode", -1);
        if(nightMode == AppCompatDelegate.MODE_NIGHT_YES){
            barChart.getAxisRight().setTextColor(Color.WHITE);
            barChart.getAxisLeft().setTextColor(Color.WHITE);
            barChart.getXAxis().setTextColor(Color.WHITE);
            barChart.getLegend().setTextColor(Color.WHITE);
            barDataSet.setValueTextColor(Color.WHITE);
        }

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
        int maximum = 10;

        if (n < 3) {
            radarChart.setNoDataTextColor(Color.BLACK);
            radarChart.setNoDataTextTypeface(Typeface.DEFAULT_BOLD);
            radarChart.setNoDataText(getString(R.string.radarchart_not_enough));
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

        List<Pair> pairs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            pairs.add(new Pair(goals.get(i), timeList.get(i)));
        }
        Collections.sort(pairs);
        Collections.reverse(pairs);

        for (int i = 1, j = pairs.size() - 1; i < pairs.size() / 2 && j > pairs.size() / 2; i+=2, j--)
        {
            Pair temp = pairs.get(i);
            pairs.set(i, pairs.get(j));
            pairs.set(j, temp);
        }

        ArrayList<RadarEntry> activity = new ArrayList<>();

        for (int i = 0; i < n && i < maximum; i++) {
            activity.add(new RadarEntry(pairs.get(i).eTime));
        }

        RadarDataSet radarDataSet = new RadarDataSet(activity, "Activity"); //TODO: оставляю для информативности
        radarDataSet.setColor(Color.RED);
        radarDataSet.setLineWidth(2f);
        radarDataSet.setValueTextColor(Color.RED);
        radarDataSet.setValueTextSize(10f);

        RadarData radarData = new RadarData(radarDataSet);

        radarData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                long v = Math.round(value * 100.0);

                if (v % 100 == 0) {
                    return String.format(Locale.ENGLISH, "%d", (int) (v / 100.0));
                } else if (v % 10 == 0) {
                    return String.format(Locale.ENGLISH, "%.1f", v / 100.0);
                } else {
                    return String.format(Locale.ENGLISH, "%.2f", v / 100.0);
                }
            }
        });

        String[] labels = new String[n];
        for (int i = 0; i < n && i < maximum; i++) {
            String label = pairs.get(i).goal.getName();
            if (label.length() > 10) {
                label = label.substring(0, 8) + "..";
            }
            labels[i] = label;
        }

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setTextSize(9f);

        radarChart.getDescription().setEnabled(false);
        radarChart.setData(radarData);

        radarChart.getLegend().setEnabled(false);

        radarChart.getYAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.ENGLISH, "%.1f", value);
            }
        });

        radarChart.getYAxis().setAxisMinimum(0f);


        int nightMode = root.getContext().getSharedPreferences("night_mode", Context.MODE_PRIVATE).getInt("mode", -1);
        if(nightMode == AppCompatDelegate.MODE_NIGHT_YES){
            radarChart.getXAxis().setTextColor(Color.WHITE);
            radarChart.getYAxis().setTextColor(Color.WHITE);
        }

        radarChart.notifyDataSetChanged();
        radarChart.invalidate();
    }
}

class Pair implements Comparable<Pair> {
    public Goal goal;
    public Float eTime;

    public Pair(Goal goal, Float eTime) {
        this.goal = goal;
        this.eTime = eTime;
    }

    @Override
    public int compareTo(Pair o) {
        return Float.compare(eTime, o.eTime);
    }
}