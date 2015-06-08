package com.gr3ymatter.sunshine.app;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    ArrayList<String> fakeForecast;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Create Fake Data To Implement Into Adapter for ListView
        fakeForecast = new ArrayList<>();
        fakeForecast.add("Today - Sunny - 88/63");
        fakeForecast.add("Tomorrow - Foggy - 70/46");
        fakeForecast.add("Weds - Cloudy - 72/63");
        fakeForecast.add("Thurs - Rainy - 64/51");
        fakeForecast.add("Fri - Foggy - 70/46");
        fakeForecast.add("Sat - Sunny - 76/68");
        fakeForecast.add("Sun - Sunny - 20/7");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                //First is the context
                this.getActivity(),
                //Second is the layout we want to populate.
                // We need to give some reference to the way we want each list item to be defined.
                R.layout.list_item_forecast,
                //Third is the ID of the TextView we want to populate
                R.id.list_item_forecast_textview,
                //Fourth is the Data we want to populate with.
                fakeForecast);
        //NOTE: ID of the TextView is Within The XML Layout.


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Find the ListView You want to populate.
        ListView listView =  (ListView)rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(arrayAdapter); //Bind Data to ListView
        return rootView;
    }
}
