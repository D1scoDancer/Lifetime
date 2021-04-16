package ru.alekssey7227.lifetime.activities.ui.main;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

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

        ArrayList<PieEntry> visitors = new ArrayList<>();
        visitors.add(new PieEntry(420, "2014"));
        visitors.add(new PieEntry(475, "2015"));
        visitors.add(new PieEntry(508, "2016"));
        visitors.add(new PieEntry(520, "2017"));
        visitors.add(new PieEntry(400, "2018"));
        visitors.add(new PieEntry(370, "2019"));
        visitors.add(new PieEntry(100, "2020"));

        PieDataSet pieDataSet = new PieDataSet(visitors, "Visitors");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Visitors");
        pieChart.animate();
    }

    private void createBarChart(View root) {
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
    }

    private void createRadarChart(View root) {
        RadarChart radarChart = root.findViewById(R.id.radarChart);

        ArrayList<RadarEntry> visitors = new ArrayList<>();
        visitors.add(new RadarEntry(420));
        visitors.add(new RadarEntry(475));
        visitors.add(new RadarEntry(508));
        visitors.add(new RadarEntry(520));
        visitors.add(new RadarEntry(400));
        visitors.add(new RadarEntry(370));
        visitors.add(new RadarEntry(100));

        RadarDataSet radarDataSet = new RadarDataSet(visitors, "Visitors");
        radarDataSet.setColor(Color.RED);
        radarDataSet.setLineWidth(2f);
        radarDataSet.setValueTextColor(Color.RED);
        radarDataSet.setValueTextSize(14f);

        RadarData radarData = new RadarData();
        radarData.addDataSet(radarDataSet);

        String[] labels = {"2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020"};
        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        radarChart.getDescription().setText("RadarChart example");
        radarChart.setData(radarData);
    }
}