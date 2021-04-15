package ru.alekssey7227.lifetime.activities.ui.main;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mikepenz.materialize.color.Material;

import java.util.ArrayList;

import ru.alekssey7227.lifetime.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
            case 1:
                root = inflater.inflate(R.layout.fragment_stats_pie, container, false);
                break;
            case 2:
                root = inflater.inflate(R.layout.fragment_stats_bar, container, false);
                BarChart barChart = root.findViewById(R.id.barChart);
                ArrayList<BarEntry> visitors = new ArrayList<>();
                visitors.add(new BarEntry(2014, 420));
                visitors.add(new BarEntry(2015, 475));
                visitors.add(new BarEntry(2016, 508));
                visitors.add(new BarEntry(2017, 520));
                visitors.add(new BarEntry(2018, 400));
                visitors.add(new BarEntry(2019, 370));
                visitors.add(new BarEntry(2020, 100));

                BarDataSet barDataSet = new BarDataSet(visitors, "Visitors");
                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(16f);

                BarData barData = new BarData(barDataSet);

                barChart.setFitBars(true);
                barChart.setData(barData);
                barChart.getDescription().setText("Bar Chart Example");
                barChart.animateY(2000);

                break;
            case 3:
                root = inflater.inflate(R.layout.fragment_stats_radar, container, false);
                break;
            default:
                return null;
        }
        return root;
    }
}