package com.posturealert.smartchair;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.posturealert.smartchair.com.posturealert.smartchair.api.APIInterface;
import com.posturealert.smartchair.com.posturealert.smartchair.api.APIReturn;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Dashboard extends AppCompatActivity {

    private static Random randomGenerator = new Random();

    // colors for different sections in pieChart
    public static final int[] MY_COLORS = {
            Color.rgb(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255)), Color.rgb(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255)), Color.rgb(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255)),
            Color.rgb(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255)), Color.rgb(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255)), Color.rgb(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255)),
            Color.rgb(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255)), Color.rgb(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255)), Color.rgb(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255)),
            Color.rgb(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255)), Color.rgb(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255)), Color.rgb(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255)),
    };
    com.github.mikephil.charting.charts.PieChart mChart;
    // we're going to display pie chart for school attendance
    private int[] yValues = {1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private String[] xValues = {"E", "LU", "SF", "LF", "SS", "SB", "LL", "LR", "LC", "RC", "NA", "PP"};
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_pie:
                    mTextMessage.setText("Posture vs Time");
                    return true;
                case R.id.navigation_line:
                    mTextMessage.setText("navigation_line");
                    return true;
                case R.id.navigation_bar:
                    mTextMessage.setText("navigation_bar");
                    return true;
                case R.id.navigation_scatter_changeThis:
                    mTextMessage.setText("navigation_scatter_changeThis");
                    return true;
                case R.id.navigation_notificaion_changeThis:
                    mTextMessage.setText("navigation_notificaion_changeThis");
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        mTextMessage = (TextView) findViewById(R.id.graphTitle);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        retrofit();

        initTime();

    }

    public void retrofit(){
        final TextView textView = (TextView) findViewById(R.id.textView);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://13.55.201.70:8098/").addConverterFactory(GsonConverterFactory.create()).build();

        APIInterface service = retrofit.create(APIInterface.class);
        Call<APIReturn> call = service.getDashBoardPitChart("1001");

        call.enqueue(new Callback<APIReturn>() {
            @Override
            public void onResponse(Call<APIReturn> call, Response<APIReturn> response) {
                APIReturn s = response.body();
                textView.setText(s.getE() + " " + s.getLU() + " " + s.getSF() + " " + s.getLF() + " " + s.getSS() + " " + s.getSB() + " " + s.getLL() + " " + s.getLR() + " " + s.getLC() + " " + s.getRC() + " " + s.getNA() + " " + s.getPP());
                // setting sample Data for Pie Chart
                initPieChart(s.getE(), s.getLU(), s.getSF(), s.getLF(), s.getSS(), s.getSB(), s.getLL(), s.getLR(), s.getLC(), s.getRC(), s.getNA(), s.getPP());
                setDataForPieChart();
            }

            @Override
            public void onFailure(Call<APIReturn> call, Throwable t) {
                Toast.makeText(Dashboard.this, "error :(" + call + t, Toast.LENGTH_LONG).show();
                textView.setText(call + "\n" + t);
            }
        });
    }

    public void initTime() {
        final TextView currDateTime = (TextView) findViewById(R.id.currDateTime);
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        currDateTime.setText("Last Update: " + (sdf.format(date)));

    }

    public void setDataForPieChart() {
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < yValues.length; i++)
            yVals1.add(new Entry(yValues[i], i));

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < xValues.length; i++)
            xVals.add(xValues[i]);

        // create pieDataSet
        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        // adding colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        // Added My Own colors
        for (int c : MY_COLORS)
            colors.add(c);


        dataSet.setColors(colors);

        //  create pie data object and set xValues and yValues and set it to the pieChart
        PieData data = new PieData(xVals, dataSet);
//           data.setValueFormatter(new DefaultValueFormatter(2));
//           data.setValueFormatter(new PercentFormatter());

        data.setValueFormatter(new MyValueFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        // refresh/update pie chart
        mChart.invalidate();

        // animate piechart
        mChart.animateXY(1400, 1400);


        // Legends to show on bottom of the graph
        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);
    }

    public void initPieChart(String a, String b, String c ,String d, String e, String f, String g, String h, String i, String j, String k, String l) {

        yValues[0] = Integer.valueOf(a);
        yValues[1] = Integer.valueOf(b);
        yValues[2] = Integer.valueOf(c);
        yValues[3] = Integer.valueOf(d);
        yValues[4] = Integer.valueOf(e);
        yValues[5] = Integer.valueOf(f);
        yValues[6] = Integer.valueOf(g);
        yValues[7] = Integer.valueOf(h);
        yValues[8] = Integer.valueOf(i);
        yValues[9] = Integer.valueOf(j);
        yValues[10] = Integer.valueOf(k);
        yValues[11] = Integer.valueOf(l);

        mChart = (com.github.mikephil.charting.charts.PieChart) findViewById(R.id.pieChart);

        //   mChart.setUsePercentValues(true);
        mChart.setDescription("");

        mChart.setRotationEnabled(true);

        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                // display msg when value selected
                if (e == null)
                    return;

                Toast.makeText(Dashboard.this,
                        xValues[e.getXIndex()] + " is " + e.getVal() + "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    public class MyValueFormatter implements ValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0"); // use one decimal if needed
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            // write your logic here
            return mFormat.format(value) + ""; // e.g. append a dollar-sign
        }
    }

}