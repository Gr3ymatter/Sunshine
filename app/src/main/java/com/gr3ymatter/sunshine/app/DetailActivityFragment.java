package com.gr3ymatter.sunshine.app;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gr3ymatter.sunshine.app.data.WeatherContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    TextView mDayText;
    TextView mDateText;
    TextView mForecastText;
    TextView mHumidityText;
    TextView mPressureText;
    TextView mWindText;
    TextView mLowText;
    TextView mHighText;
    ImageView mIconView;

    Uri mUri;

    public static String DETAIL_URI = "Uri";
    public  String shareString;

    private static final String[] FORECAST_COLUMNS_ALL = {
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
            WeatherContract.LocationEntry.COLUMN_COORD_LONG,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_DEGREES
    };

    final int DETAIL_LOADER = 0;

    public static DetailActivityFragment newInstance(Uri uri){

        DetailActivityFragment f = new DetailActivityFragment();

        Bundle args = new Bundle();
        args.putParcelable("Uri", uri);
        f.setArguments(args);
        return f;
    }

    public DetailActivityFragment() {
    }



      void onLocationChanged( String newLocation ) {
               // replace the uri, since the location has changed
                 Uri uri = mUri;
               if (null != uri) {
                       long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
                       Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
                       mUri = updatedUri;
                       getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
                   }
            }

    @Override
    public void onResume() {
        super.onResume();

        onLocationChanged(Utility.getPreferredLocation(getActivity()));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        if(mUri != null)
            return new CursorLoader(getActivity(), mUri, FORECAST_COLUMNS_ALL,null, null, null);
        else
            return null;
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data.moveToFirst()){


            mHighText.setText(Utility.formatTemperature(getActivity(),data.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP), Utility.isMetric(getActivity())));
            mLowText.setText(Utility.formatTemperature(getActivity(), data.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP), Utility.isMetric(getActivity())));

            mDayText.setText(Utility.getDayName(getActivity(), data.getLong(ForecastFragment.COL_WEATHER_DATE)));
            mDateText.setText(Utility.getFormattedMonthDay(getActivity(), data.getLong(ForecastFragment.COL_WEATHER_DATE)));

            mForecastText.setText(data.getString(ForecastFragment.COL_WEATHER_DESC));

            mHumidityText.setText(getString(R.string.format_humidity, data.getDouble(ForecastFragment.COL_HUMIDITY)));
            mPressureText.setText(getString(R.string.format_pressure, data.getDouble(ForecastFragment.COL_PRESSURE)));

            shareString = Utility.getFriendlyDayString(getActivity(), data.getLong(ForecastFragment.COL_WEATHER_DATE)) + ", " +
                            mForecastText.getText() + " - " + mHighText.getText() + "/" + mLowText.getText();

            mWindText.setText(Utility.formatWind(getActivity(), data.getDouble(ForecastFragment.COL_WIND_SPEED),
                                data.getDouble(ForecastFragment.COL_WIND_DIRECTION), Utility.isMetric(getActivity())));

            mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(data.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID)));
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        getLoaderManager().initLoader(DETAIL_LOADER, savedInstanceState, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if(getArguments()!= null){
            mUri = getArguments().getParcelable(DETAIL_URI);

        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mDateText = (TextView)rootView.findViewById(R.id.detail_date_textview);
        mDayText = (TextView)rootView.findViewById(R.id.detail_day_textview);
        mForecastText = (TextView)rootView.findViewById(R.id.detail_forecast_textview);
        mHumidityText = (TextView)rootView.findViewById(R.id.detail_humidity_textview);
        mPressureText = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        mWindText = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        mLowText = (TextView) rootView.findViewById(R.id.detail_low_textview);
        mHighText = (TextView) rootView.findViewById(R.id.detail_high_textview);
        mIconView = (ImageView) rootView.findViewById(R.id.detail_icon);





        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
