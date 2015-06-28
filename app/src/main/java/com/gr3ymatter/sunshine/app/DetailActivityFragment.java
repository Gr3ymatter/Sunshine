package com.gr3ymatter.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    TextView forecastTextView;
    final int DETAIL_LOADER = 0;

    public DetailActivityFragment() {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

       return new CursorLoader(getActivity(), getActivity().getIntent().getData(), ForecastFragment.FORECAST_COLUMNS,null, null, null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data.moveToFirst()){
            String high = Utility.formatTemperature(data.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP), Utility.isMetric(getActivity()));
            String low = Utility.formatTemperature(data.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP), Utility.isMetric(getActivity()));
           String highAndLow = high+"/"+low;
            String weatherString = Utility.formatDate(data.getLong(ForecastFragment.COL_WEATHER_DATE)) +
                    " - " + data.getString(ForecastFragment.COL_WEATHER_DESC) +
                    " - " + highAndLow;
            forecastTextView.setText(weatherString);
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

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        forecastTextView = (TextView)rootView.findViewById(R.id.detail_forecastString);
        Intent intent = getActivity().getIntent();

        String mForecastStr;

        if (intent != null) {
            mForecastStr = intent.getDataString();
            forecastTextView.setText(mForecastStr);
        }
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
