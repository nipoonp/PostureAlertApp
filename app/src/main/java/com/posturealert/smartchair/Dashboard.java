package com.posturealert.smartchair;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.posturealert.smartchair.com.posturealert.smartchair.api.APIInterface;
import com.posturealert.smartchair.com.posturealert.smartchair.api.APIReturn;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Dashboard extends AppCompatActivity {

    private static Random randomGenerator = new Random();
    String fnameDb, lnameDb, idDb, emailDb, weightDb, heightDb, passwordDb;


    // colors for different sections in pieChart
    public static final int[] MY_COLORS = {
            Color.rgb(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255)), Color.rgb(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255)), Color.rgb(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255)),
            Color.rgb(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255)), Color.rgb(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255)), Color.rgb(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255)),
            Color.rgb(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255)), Color.rgb(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255)), Color.rgb(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255)),
            Color.rgb(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255)), Color.rgb(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255)), Color.rgb(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255)),
    };
    com.github.mikephil.charting.charts.PieChart mChart;
    com.github.mikephil.charting.charts.LineChart lineChart;
    com.github.mikephil.charting.charts.BarChart barChart;

    // we're going to display pie chart for school attendance
    private int[] yValues = {1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private String[] xValues = {"E", "LU", "SF", "LF", "SS", "SB", "LL", "LR", "LC", "RC", "NA", "PP"};
    private TextView mTextMessage;
    private int graph = 0;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_pie:
                    mTextMessage.setText("Posture vs Time");
                    lineChart.setVisibility(View.INVISIBLE);
                    mChart.setVisibility(View.VISIBLE);
                    barChart.setVisibility(View.INVISIBLE);
                    graph = 0;
                    retrofit();
                    return true;
                case R.id.navigation_line:
                    mTextMessage.setText("navigation_line");
                    mChart.setVisibility(View.INVISIBLE);
                    lineChart.setVisibility(View.VISIBLE);
                    barChart.setVisibility(View.INVISIBLE);
                    graph = 1;
                    retrofit();
                    return true;
                case R.id.navigation_bar:
                    mTextMessage.setText("navigation_bar");
                    mChart.setVisibility(View.INVISIBLE);
                    lineChart.setVisibility(View.INVISIBLE);
                    barChart.setVisibility(View.VISIBLE);
                    graph = 2;
                    retrofit();

                    return true;
                case R.id.navigation_scatter_changeThis:
                    mTextMessage.setText("navigation_scatter_changeThis");
                    graph = 3;
                    retrofit();
                    return true;
                case R.id.navigation_notificaion_changeThis:
                    mTextMessage.setText("navigation_notificaion_changeThis");
                    graph = 4;
                    retrofit();
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fnameDb = extras.getString("firstname");
            lnameDb = extras.getString("lastname");  // When you click login AFTER REGISTER SCREEN, values are recevied.
            idDb = extras.getString("id");
            emailDb = extras.getString("email");
            weightDb = extras.getString("weight");  // When you click login AFTER REGISTER SCREEN, values are recevied.
            heightDb = extras.getString("height");
            passwordDb = extras.getString("password");
        }

        mTextMessage = (TextView) findViewById(R.id.graphTitle);
        mChart = (com.github.mikephil.charting.charts.PieChart) findViewById(R.id.pieChart);
        lineChart = (com.github.mikephil.charting.charts.LineChart) findViewById(R.id.lineChart);
        barChart = (com.github.mikephil.charting.charts.BarChart) findViewById(R.id.barChart);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        graph = 0;

        initTime();

    }

    public void retrofit(){
        final TextView textView = (TextView) findViewById(R.id.textView);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://13.55.201.70:8099/").addConverterFactory(GsonConverterFactory.create()).build();



        APIInterface service = retrofit.create(APIInterface.class);
        Call<APIReturn> call;

        switch(graph){
            case 0:
                call = service.getDashBoardPitChart(idDb);
                break;
            case 1:
                call = service.getDashBoardLineChart(idDb);
                break;
            case 2:
                call = service.dashBoardPosturePercentageChart(idDb);
                break;
            case 3:
                call = service.getDashBoardLineChart(idDb);
                break;
            case 4:
                call = service.getDashBoardLineChart(idDb);
                break;
            default:
                call = service.getDashBoardLineChart(idDb);
                break;
        }


        call.enqueue(new Callback<APIReturn>() {
            @Override
            public void onResponse(Call<APIReturn> call, Response<APIReturn> response) {
                APIReturn s = response.body();

                ArrayList<Entry> entries = new ArrayList<>();
                ArrayList<String> labels = new ArrayList<String>();
                LineDataSet dataset = new LineDataSet(entries, "# of Calls");
                LineData data = new LineData(labels, dataset);

                switch(graph){
                    case 0:
                        textView.setText(s.getE() + " " + s.getLU() + " " + s.getSF() + " " + s.getLF() + " " + s.getSS() + " " + s.getSB() + " " + s.getLL() + " " + s.getLR() + " " + s.getLC() + " " + s.getRC() + " " + s.getNA() + " " + s.getPP());
                        // setting sample Data for Pie Chart
                        initPieChart(s.getE(), s.getLU(), s.getSF(), s.getLF(), s.getSS(), s.getSB(), s.getLL(), s.getLR(), s.getLC(), s.getRC(), s.getNA(), s.getPP());
                        setDataForPieChart();
                        break;
                    case 1:
                        entries.clear();
                        labels.clear();

                        int pos_values[] = s.getPostureValues();
                        int time_values[] = s.getTimeValues();

                        String formatted;

                        // WE must use GMT+12 because we want to display the
                        // time accorind to the NZ time
                        DateFormat format = new SimpleDateFormat("HH:mm:ss");
                        format.setTimeZone(TimeZone.getTimeZone("GMT+12:00"));

                        for (int i = 0; i < pos_values.length; i++){
//                            System.out.println(pos_values[i] + " " + time_values[i]);
                            Date date = new Date(time_values[i] * 1000);
                            formatted = format.format(date);
                            labels.add(formatted);
                            entries.add(new Entry(pos_values[i], i));
                        }




                        dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
                        dataset.setDrawCubic(true);
                        dataset.setDrawFilled(false);

                        lineChart.setData(data);
                        lineChart.animateY(5000);

                        break;
                    case 2:
//                        entries.clear();
//                        labels.clear();
//
//                        entries = new ArrayList<>();
//                        entries.add(new BarEntry(4f, 0));
//                        entries.add(new BarEntry(8f, 1));
//                        entries.add(new BarEntry(6f, 2));
//                        entries.add(new BarEntry(12f, 3));
//                        entries.add(new BarEntry(18f, 4));
//                        entries.add(new BarEntry(9f, 5));
//
//                        dataset = new BarDataSet(entries, "# of Calls");
//
//
//
//                        labels = new ArrayList<String>();
//                        labels.add("January");
//                        labels.add("February");
//                        labels.add("March");
//                        labels.add("April");
//                        labels.add("May");
//                        labels.add("June");
//
//                        BarData data = new BarData(labels, dataset);
//                        dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
//                        barChart.setData(data);
//                        barChart.animateY(5000);

                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    default:
                        break;
                }
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