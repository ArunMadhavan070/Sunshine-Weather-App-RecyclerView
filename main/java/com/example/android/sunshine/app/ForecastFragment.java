/*
package com.example.android.sunshine.app;

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

import com.example.android.sunshine.app.model.Weather;
import com.example.android.sunshine.app.realm.RealmController;

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
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;


public class ForecastFragment extends Fragment {

        private Realm realm;

        private ArrayAdapter<String> mforecastAdapter;

        public ForecastFragment() {
        }


        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override

        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
        {

            inflater.inflate(R.menu.forecastfragment,menu);
        }

        public boolean onOptionsItemSelected(MenuItem item)
        {

            int id = item.getItemId();
            if(id == R.id.action_refresh)
            {
                updateWeather();
                return true;
            }

            return super.onOptionsItemSelected(item);
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            RealmResults<Weather> results1 =
                    realm.where(Weather.class).findAll();

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            mforecastAdapter = new ArrayAdapter<String>(getActivity(),
                    R.layout.list_item_forecast,
                    R.id.list_item_forecast_textview,
                    new ArrayList<String>());

            ListView forecastList = (ListView) rootView.findViewById(R.id.listview_forecast);
            forecastList.setAdapter(mforecastAdapter);
            forecastList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                public void onItemClick(AdapterView<?> adapterView,View view,int i,long l){

                    String forcastText = mforecastAdapter.getItem(i);
                  // Toast toast = Toast.makeText(getActivity(),forcastText,Toast.LENGTH_SHORT);
                  // toast.show();

                    Intent intent = new Intent(getActivity(),DetailActivity.class);
                    intent.putExtra(Intent.EXTRA_TEXT,forcastText);
                    startActivity(intent);

                }

            });

            return rootView;

        }

        private void updateWeather(){

            FetchWeatherTask weatherTask = new FetchWeatherTask();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String location = prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
            weatherTask.execute(location);
        }

        public void onStart(){

            super.onStart();
            updateWeather();
        }

        public class FetchWeatherTask extends AsyncTask<String, Void,  String[] > {






            ArrayList<Weather> weatherData = new ArrayList<>();

            private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();





            private String getReadableDateString(long time){
                // Because the API returns a unix timestamp (measured in seconds),
                // it must be converted to milliseconds in order to be converted to valid date.
                SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
                return shortenedDateFormat.format(time);
            }



            private String formatHighLows(double high, double low) {


                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String  unitType = sharedPrefs.getString(getString(R.string.pref_temp_key),getString(R.string.pref_units_metric));
                if (unitType.equals(getString(R.string.pref_units_imperial)))
                {

                    high = (high * 1.8) + 32;
                    low = (low * 1.8) + 32;

                }
                else if (!unitType.equals(getString(R.string.pref_units_metric)))
                {

                    Log.d(LOG_TAG,"Unit type not found" + unitType);
                }
                // For presentation, assume the user doesn't care about tenths of a degree.
                long roundedHigh = Math.round(high);
                long roundedLow = Math.round(low);

                String highLowStr = roundedHigh + "/" + roundedLow;
                return highLowStr;
            }



            private  String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                    throws JSONException {


                Realm realm;
                realm = RealmController.with(getActivity()).getRealm();



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

                    highAndLow = formatHighLows(high, low);
                    Weather weather = new Weather();
                    weather.setDay(day);
                    weather.setDescription(description);
                    weather.setHighAndLow(highAndLow);
                    weatherData.add(weather);


                        realm.beginTransaction();
                        realm.copyToRealm(weather);
                        realm.commitTransaction();




                    RealmResults<Weather> results1 =
                            realm.where(Weather.class).findAll();


                    resultStrs[i] = day + " - " + description + " - " + highAndLow;


*/
/*Log.d("results1", c.getDay()+ "," + c.getDescription() +"," +c.getHighAndLow());*//*







                }

 RealmResults<Weather> results1 =
                        realm.where(Weather.class).findAll();


                for (String s : resultStrs) {
                    Log.v(LOG_TAG, "Forecast entry: " + s);
                }
                return resultStrs;

            }






            protected  String[] doInBackground(String... params) {

                if(params.length == 0)
                {
                    return null;
                }

                // These two need to be declared outside the try/catch
                // so that they can be closed in the finally block.
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                // Will contain the raw JSON response as a string.
                String forecastJsonStr = null;

                String format = "JSON";
                String units = "metric";
                int numDays = 7;
                String apiKey = "db2df66efa78b17c4542d4e75449e2b0";

                try {

                    final String FORCAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                    final String QUERY_PARAM = "q";
                    final String FORMAT_PARAM = "mode";
                    final String UNITS_PARAM = "units";
                    final String DAYS_PARAM = "cnt";
                    final String API_KEY = "appid";

                    Uri builtUri = Uri.parse(FORCAST_BASE_URL).buildUpon()
                            .appendQueryParameter(QUERY_PARAM,params[0])
                            .appendQueryParameter(FORMAT_PARAM,format)
                            .appendQueryParameter(UNITS_PARAM,units)
                            .appendQueryParameter(DAYS_PARAM,Integer.toString(numDays))
                            .appendQueryParameter(API_KEY,apiKey)
                            .build();

                    URL url = new URL(builtUri.toString());

                    Log.i(LOG_TAG, "FORECAST URL:" + url);


                  //  URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7&appid=db2df66efa78b17c4542d4e75449e2b0");

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
                    Log.i(LOG_TAG, forecastJsonStr);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    // If the code didn't successfully get the weather data, there's no point in attemping
                    // to parse it.
                    return null;
                } finally {
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

                try
                {
                    return getWeatherDataFromJson(forecastJsonStr,numDays);
                }
                catch (JSONException e)
                {
                    Log.e(LOG_TAG,e.getMessage(),e);
                    e.printStackTrace();
                }


                return null;
            }


protected void onPostExecute( ) {

                Realm realm;
                realm = RealmController.with(getActivity()).getRealm();
                RealmResults<Weather> results1 =
                        realm.where(Weather.class).findAll();




                    mforecastAdapter.clear();
                    for (Weather w  : results1)
                    {
                        mforecastAdapter.add(w.getDay());

                    }



            }

        }
    }
*/
