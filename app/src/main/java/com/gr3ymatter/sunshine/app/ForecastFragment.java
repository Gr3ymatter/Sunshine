package com.gr3ymatter.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    ArrayList<String> fakeForecast;
    Uri.Builder builder;
    ArrayAdapter<String> forecastAdapter;
    final public static String FORECAST_DATA = "forcast_data";
    SharedPreferences pref;

    public ForecastFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
       // super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_refresh:
                updateWeather();
                return true;
            case R.id.action_location:
                viewLocation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void viewLocation(){
        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        Uri locationUri = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q",pref.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default))).build();
        mapIntent.setData(locationUri);
        if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        forecastAdapter = new ArrayAdapter<String>(
                //First is the context
                this.getActivity(),
                //Second is the layout we want to populate.
                // We need to give some reference to the way we want each list item to be defined.
                R.layout.list_item_forecast,
                //Third is the ID of the TextView we want to populate
                R.id.list_item_forecast_textview,
                //Fourth is the Data we want to populate with.
                new ArrayList<String>());
        //NOTE: ID of the TextView is Within The XML Layout.

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //Find the ListView You want to populate.
        final ListView listView =  (ListView)rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(forecastAdapter); //Bind Data to ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(),forecastAdapter.getItem(position).toString(), Toast.LENGTH_SHORT).show();
                Intent startActivityIntent = new Intent(getActivity(), DetailActivity.class);
                startActivityIntent.putExtra(FORECAST_DATA, forecastAdapter.getItem(position).toString());
                startActivity(startActivityIntent);

            }
        });
        return rootView;
    }


    private void updateWeather(){
        pref =  PreferenceManager.getDefaultSharedPreferences(getActivity());
        String locationString = pref.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        String unitString = pref.getString(getString(R.string.pref_units_key), getString(R.string.pref_units_default));
        new FetchWeatherTask().execute(locationString, unitString);

    }

    /*
      This is Just a PlaceHolder class. AsyncAdapter is tied to a UI component
      so it can be destroyed at anytime. There are better ways to implement this task such as
      services, SyncAdapters etc.
     */
    private class FetchWeatherTask extends AsyncTask<String, Void, String[]>
    {

        private String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {

            if(params == null)
            {
                return null;
            }

            String format = "json";
            String units = "metric";
            int numDays = 7;


            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {


                final String BASE_URI = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";

                Uri siteUri = Uri.parse(BASE_URI).buildUpon().appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays)).build();

                URL siteURL = new URL(siteUri.toString());

                Log.d(LOG_TAG, siteURL.toString());

                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) siteURL.openConnection();
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

            try {
               return getWeatherDataFromJson(forecastJsonStr, numDays, params[1]);
            }
            catch (JSONException ex){
                ex.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String[] strings) {

            if(strings != null){

                forecastAdapter.clear();

                for(int i = 0; i <strings.length; i++){
                    forecastAdapter.add(strings[i]);
                }

            }


        }

        private String getReadableDateString(long time){

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM, dd");
            return simpleDateFormat.format(time);

        }



        private String formatHighLows(double high, double low, String unitFormat){

            long formattedHigh = (long)high;
            long formattedLow = (long)low;
            if(unitFormat.equals(R.string.pref_units_imperial))
            {
                formattedHigh = formattedHigh*9/5 + 32;
                formattedLow  = formattedLow*9/5 + 32;
            }

            formattedHigh = Math.round(formattedHigh);
            formattedLow = Math.round(formattedLow);

            return formattedHigh + "/" + formattedLow;

        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays, String units)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            String[] resultStrs = new String[numDays];
            for(int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay+i);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);



                highAndLow = formatHighLows(high, low, units);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Forecast entry: " + s);
            }
            return resultStrs;

        }

    }


}
