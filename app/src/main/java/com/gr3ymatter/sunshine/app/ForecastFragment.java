package com.gr3ymatter.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gr3ymatter.sunshine.app.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

   android.support.v4.app.LoaderManager mLoaderManager;
    ForecastAdapter mForecastAdapter;
    final public static String FORECAST_DATA = "forcast_data";
    SharedPreferences pref;
    final int LOADER_ID = 0;

    public static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;
    static final int COL_HUMIDITY = 9;
    static final int COL_WIND_SPEED = 10;
    static final int COL_PRESSURE = 11;
    static final int COL_WIND_DIRECTION = 12;




    public ForecastFragment() {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        String locationSetting = Utility.getPreferredLocation(getActivity());

        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";

        Uri uri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(locationSetting, System.currentTimeMillis());

        CursorLoader cursorLoader = new CursorLoader(getActivity(), uri, FORECAST_COLUMNS, null,null, sortOrder);

        return cursorLoader;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mForecastAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mForecastAdapter.swapCursor(null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, savedInstanceState, this);
    }


    public void onLocationChanged(){
        updateWeather();
        getLoaderManager().restartLoader(LOADER_ID, null,this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //Find the ListView You want to populate.
        final ListView listView =  (ListView)rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter); //Bind Data to ListView

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting, cursor.getLong(COL_WEATHER_DATE)
                            ));
                    startActivity(intent);
                }
            }
        });


//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getActivity(),forecastAdapter.getItem(position).toString(), Toast.LENGTH_SHORT).show();
//                Intent startActivityIntent = new Intent(getActivity(), DetailActivity.class);
//                startActivityIntent.putExtra(FORECAST_DATA, forecastAdapter.getItem(position).toString());
//                startActivity(startActivityIntent);

          //  }
        //});
        return rootView;
    }


    private void updateWeather(){
        pref =  PreferenceManager.getDefaultSharedPreferences(getActivity());
        String locationString = pref.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        String unitString = pref.getString(getString(R.string.pref_units_key), getString(R.string.pref_units_metric));
        new FetchWeatherTask(getActivity()).execute(locationString, unitString);
    }


}
