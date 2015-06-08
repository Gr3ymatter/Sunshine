package com.gr3ymatter.sunshine.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    ArrayList<String> fakeForecast;

    public ForecastFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
       // super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_refresh:
                new FetchWeatherTask().execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

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


    /*
      This is Just a PlaceHolder class. AsyncAdapter is tied to a UI component
      so it can be destroyed at anytime. There are better ways to implement this task such as
      services, SyncAdapters etc.
     */
    private class FetchWeatherTask extends AsyncTask<Void, Void, Void>
    {

        private String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                Log.v(LOG_TAG, forecastJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

           return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


}
