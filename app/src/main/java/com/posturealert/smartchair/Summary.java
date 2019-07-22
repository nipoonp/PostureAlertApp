package com.posturealert.smartchair;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Summary extends AppCompatActivity {

    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String,List<String>> listHash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        listView = (ExpandableListView)findViewById(R.id.lvExp);
        initData();
        listAdapter = new ExpandableListAdapter(this,listDataHeader,listHash);
        listView.setAdapter(listAdapter);
    }

    private void initData() {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        listDataHeader.add("21 Sep");
        listDataHeader.add("20 Sep");
        listDataHeader.add("19 Sep");
        listDataHeader.add("18 Sep");

        List<String> edmtDev = new ArrayList<>();
        edmtDev.add("Good Posture:");
        edmtDev.add("55:52");
        edmtDev.add("Bad Posture:");
        edmtDev.add("21:36");

        List<String> androidStudio = new ArrayList<>();
        androidStudio.add("Good Posture:");
        androidStudio.add("12:52");
        androidStudio.add("Bad Posture:");
        androidStudio.add("14:26");

        List<String> xamarin = new ArrayList<>();
        xamarin.add("Good Posture:");
        xamarin.add("10:22");
        xamarin.add("Bad Posture:");
        xamarin.add("5:06");

        List<String> uwp = new ArrayList<>();
        uwp.add("Good Posture:");
        uwp.add("21:42");
        uwp.add("Bad Posture:");
        uwp.add("11:46");

        listHash.put(listDataHeader.get(0),edmtDev);
        listHash.put(listDataHeader.get(1),androidStudio);
        listHash.put(listDataHeader.get(2),xamarin);
        listHash.put(listDataHeader.get(3),uwp);
    }
}
