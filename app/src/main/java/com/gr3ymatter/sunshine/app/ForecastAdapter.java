package com.gr3ymatter.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    int VIEW_TYPE_TODAY = 0;
    int VIEW_TYPE_FUTURE_DAY = 1;

    private boolean mUseSpecialToday =false;


    private class ViewHolder {

        ImageView iconView;
        TextView dateView;
        TextView forecastView;
        TextView lowView;
        TextView highView;


        public ViewHolder(View rootView){

            iconView = (ImageView) rootView.findViewById(R.id.list_item_icon);
            dateView = (TextView) rootView.findViewById(R.id.list_item_date_textview);
            forecastView = (TextView) rootView.findViewById(R.id.list_item_forecast_textview);
            lowView = (TextView) rootView.findViewById(R.id.list_item_low_textview);
            highView = (TextView) rootView.findViewById(R.id.list_item_high_textview);

        }

    }


    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    public String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String highLowStr = Utility.formatTemperature(mContext,high, isMetric) + "/" + Utility.formatTemperature(mContext,low, isMetric);
        return highLowStr;
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        // get row indices for our cursor

        String highAndLow = formatHighLows(
                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
                cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));

        return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
                " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

       int viewType = getItemViewType(cursor.getPosition());

        int layoutId = -1;

        if(viewType == VIEW_TYPE_TODAY){
             layoutId = R.layout.list_item_forecast_today;
        }
        else if(viewType == VIEW_TYPE_FUTURE_DAY)
        {
            layoutId = R.layout.list_item_forecast;
        }
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder holder = new ViewHolder(view);

        view.setTag(holder);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        // Read weather icon ID from cursor

        ViewHolder holder = (ViewHolder) view.getTag();
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        if(getItemViewType(cursor.getPosition()) == VIEW_TYPE_TODAY)
            {
                holder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
            }
        else if(getItemViewType(cursor.getPosition()) == VIEW_TYPE_FUTURE_DAY)
        {
            holder.iconView.setImageResource(Utility.getIconResourceForWeatherCondition(weatherId));
        }

//        // Use placeholder image for now

        // TODO Read date from cursor
        long date = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        holder.dateView.setText(Utility.getFriendlyDayString(mContext, date));
        // TODO Read weather forecast from cursor
        String forcast = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        holder.forecastView.setText(forcast);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        holder.highView.setText(Utility.formatTemperature(mContext, high, isMetric));

        // TODO Read low temperature from cursor
        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        holder.lowView.setText(Utility.formatTemperature(mContext, low, isMetric));

    }


    public void setSpecialTodayView(boolean isSpecialTodayView){
        mUseSpecialToday = isSpecialTodayView;
    }


    @Override
    public int getViewTypeCount() {
                return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseSpecialToday) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }
}