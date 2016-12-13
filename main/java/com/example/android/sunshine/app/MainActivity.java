package com.example.android.sunshine.app;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.example.android.sunshine.app.adapters.HidingScrollListener;
import com.example.android.sunshine.app.adapters.RealmWeathersAdapter;
import com.example.android.sunshine.app.adapters.WeatherAdapter;
import com.example.android.sunshine.app.app.Prefs;
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

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private Context context;


    private Toolbar mToolbar;

    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int WEATHER_NOTIFICATION_ID = 3004;
    final static String GROUP_KEY_EMAILS = "group_key_emails";

    NotificationManager mNM;


    private WeatherAdapter adapter;
    Realm realm;
    private LayoutInflater inflater;

    private RecyclerView recycler;
    private SwipeRefreshLayout mSwipeRefreshLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recycler = (RecyclerView) findViewById(R.id.recycler);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);


        /*mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                setupRecycler();
                updateWeather();
                RealmController.with((Activity) context).refresh();
                setRealmAdapter(RealmController.with((Activity) context).getWeathers());


            }
        });



        //get realm instance
        realm = RealmController.with(this).getRealm();
        RealmController.with(this).refresh();




        setupRecycler();

        initToolbar();






        if (!Prefs.with(this).getPreLoad()) {

            updateWeather();
            //adapter.notifyDataSetChanged();

        }



        // refresh the realm instance

        // get all persisted objects
        // create the helper adapter and notify data set changes
        // changes will be reflected automatically





        setRealmAdapter(RealmController.with(this).getWeathers());
*/
      // recycler.scrollToPosition(RealmController.getInstance().getWeathers().size());





    }


    public void setRealmAdapter(RealmResults<Weather> weathers) {

        RealmWeathersAdapter realmAdapter = new RealmWeathersAdapter(this, weathers, true);
        // Set the data and tell the RecyclerView to draw
        adapter.setRealmAdapter(realmAdapter);

        adapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle(getString(R.string.app_name));
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

    }



    private void setupRecycler() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recycler.setHasFixedSize(true);

        // use a linear layout manager since the cards are vertically scrollable
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(layoutManager);

        // create an empty adapter and add it to the recycler view
        adapter = new WeatherAdapter(this);
        recycler.setAdapter(adapter);

        recycler.setOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideViews();
            }
            @Override
            public void onShow() {
                showViews();
            }
        });
    }

    private void hideViews() {
        mToolbar.animate().translationY(-mToolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));


    }

    private void showViews() {
        mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));

    }

    private void updateWeather(){


        RealmResults<Weather> result1 = realm.where(Weather.class).findAll();

        Log.i("updateWeather","realm size : "+result1.size());


        if(result1.size() > 7) {


            realm.beginTransaction();
            result1.clear();
            realm.commitTransaction();
            realm.refresh();


        }

        FetchWeatherTask weatherTask = new FetchWeatherTask();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String location = pref.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        weatherTask.execute(location);
        adapter.notifyDataSetChanged();

        recycler.scrollToPosition(RealmController.getInstance().getWeathers().size());

      //  mSwipeRefreshLayout.setRefreshing(false);
    }

    public void onStart(){

        realm = RealmController.with(this).getRealm();

        setupRecycler();
        updateWeather();
        RealmController.with((Activity) context).refresh();
        setRealmAdapter(RealmController.with((Activity) context).getWeathers());





     super.onStart();

    }


    public void onPause(){

        Log.i("OnPause","paused");



        super.onPause();

    }

    public void onRestart() {

       /* setupRecycler();
        updateWeather();
        RealmController.with((Activity) context).refresh();
        setRealmAdapter(RealmController.with((Activity) context).getWeathers());
*/

        Log.i("OnRestart", "restarted");


        super.onRestart();
    }

    public void onDestroy() {


        Log.i("OnDestroy", "Destroyed");


        super.onDestroy();
    }









    public  void onResume(){

       mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
           @Override
           public void onRefresh() {
               // Refresh items
               setupRecycler();
               updateWeather();
               RealmController.with((Activity) context).refresh();
               setRealmAdapter(RealmController.with((Activity) context).getWeathers());


           }
       });



       //get realm instance
       realm = RealmController.with(this).getRealm();

       //adapter.setRealmAdapter(null);
      // recycler.setAdapter(null);

       setupRecycler();

       initToolbar();


       updateWeather();


       RealmController.with(this).refresh();

       setRealmAdapter(RealmController.with(this).getWeathers());


       // updateWeather();

        super.onResume();


    }




    private class FetchWeatherTask extends AsyncTask<String, Void,  String[] > {


            ArrayList<Weather> weatherData = new ArrayList<>();
            Realm realmm;
            long roundedHigh;
             long roundedLow;




            private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();


            private String getReadableDateString(long time){
                // Because the API returns a unix timestamp (measured in seconds),
                // it must be converted to milliseconds in order to be converted to valid date.
                SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
                return shortenedDateFormat.format(time);
            }



            private String formatHighLows(double high, double low) {



                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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
                 roundedHigh = Math.round(high);
                 roundedLow = Math.round(low);


                String highLowStr = roundedHigh + "/" + roundedLow;
                return highLowStr;
            }



        private void notifyWeather() {

            realmm =  Realm.getDefaultInstance();
            Context context = getApplicationContext();
            //checking the last update and notify if it' the first of the day
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
            boolean displayNotifications = prefs.getBoolean(displayNotificationsKey,
                    Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));

            if ( displayNotifications ) {

                String lastNotificationKey = context.getString(R.string.pref_last_notification);
                long lastSync = prefs.getLong(lastNotificationKey, 0);

                if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS) {
                    // Last sync was more than 1 day ago, let's send a notification with the weather.


                    realmm.refresh();

                    RealmResults<Weather> result1 = realmm.where(Weather.class).findAll();

                    Weather first =result1.first();


                        String title = context.getString(R.string.app_name);

                        String desc = first.getDescription();
                        String day = "Today "+ first.getDay();
                        String temp = "High "+first.getCityHighTemp()+ "\n" +"Low "+first.getCityLowTemp();

                    Log.i("first row desc",""+desc);
                    Log.i("first row day",""+day);
                    Log.i("first row temp",""+temp);



                        // Define the text of the forecast.


                        // NotificationCompatBuilder is a very convenient way to build backward-compatible
                        // notifications.  Just throw in some data.
                    Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
                    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class),0);
                    Resources resources = context.getResources();
                        NotificationCompat.Builder mBuilder =
                                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                                        .setColor(resources.getColor(R.color.sunshine_light_blue))
                                        .setSmallIcon(R.drawable.ic_launcher)
                                        .setStyle(new NotificationCompat.InboxStyle()
                                                .addLine(day)
                                                .addLine(desc)
                                                .addLine(temp)
                                                .setBigContentTitle("ToDo")
                                                .setSummaryText("Tap On for Details")

                                        )
                                        .setLargeIcon(bm)
                                        .setContentTitle(title)
                                        .setContentText("Todays Weather")
                                        .setContentIntent(contentIntent)
                                        .setGroup(GROUP_KEY_EMAILS)
                                        .setGroupSummary(true);


                    mNM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        // WEATHER_NOTIFICATION_ID allows you to update the notification later on.
                    mNM.notify(WEATHER_NOTIFICATION_ID, mBuilder.build());

                        //refreshing last sync
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putLong(lastNotificationKey, System.currentTimeMillis());
                        editor.commit();


                }
            }
        }








            private  String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                    throws JSONException {



                realmm =  Realm.getDefaultInstance();


                // These are the names of the JSON objects that need to be extracted.

                final String OWM_CITY = "city";
                final String OWM_NAME = "name";
                final String OWM_LIST = "list";
                final String OWM_WEATHER = "weather";
                final String OWM_TEMPERATURE = "temp";
                final String OWM_MAX = "max";
                final String OWM_MIN = "min";
                final String OWM_PRESSURE = "pressure";
                final String OWM_HUMIDITY = "humidity";
                final String OWM_WINDSPEED = "speed";
                final String OWM_WIND_DIRECTION = "deg";
                final String OWM_LATITUDE = "lat";
                final String OWM_LONGITUDE = "lon";
                final String OWM_COORD = "coord";
                final String QWM_COUNTRY = "country";

                final String OWM_DESCRIPTION = "main";

                JSONObject forecastJson = new JSONObject(forecastJsonStr);
                JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
                JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);
                JSONObject coordJson =  cityJson.getJSONObject(OWM_COORD);

                String city = cityJson.getString(OWM_NAME);
                String country = cityJson.getString(QWM_COUNTRY);
                double lat = coordJson.getDouble(OWM_LATITUDE);
                double lon = coordJson.getDouble(OWM_LONGITUDE);

                Log.d(LOG_TAG,"city from json" +city );



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
                    int humidity;
                    double pressure;
                    double windSpeed;
                    double windDirection;

                    // Get the JSON object representing the day
                    JSONObject dayForecast = weatherArray.getJSONObject(i);

                    humidity = dayForecast.getInt(OWM_HUMIDITY);
                    pressure = dayForecast.getDouble(OWM_PRESSURE);
                    windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
                    windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);



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
                    Log.v(LOG_TAG,"weather data captured");

                    highAndLow = formatHighLows(high, low);

                    Weather weather = new Weather();
                        weather.setDay(day);
                        weather.setDescription(description);
                        weather.setCityHighTemp(roundedHigh);
                        weather.setCityLowTemp(roundedLow);
                        weather.setCity(city);
                        weather.setCityLatitude(lat);
                        weather.setCityLongitude(lon);
                        weather.setHumidity(humidity);
                        weather.setPressure(pressure);
                        weather.setWindSpeed(windSpeed);
                        weather.setWindDirection(windDirection);
                        weather.setCountry(country);
                        weatherData.add(weather);




                    resultStrs[i] = day + " - " + description + " - " + highAndLow;



                }

              /*  RealmResults<Weather> result1 = realmm.where(Weather.class).findAll();

                if(result1!= null)

                result1.first().removeFromRealm();*/

                realmm.beginTransaction();
                realmm.copyToRealmOrUpdate(weatherData);
                realmm.commitTransaction();

                realmm.refresh();

                notifyWeather();


                Prefs.with(getApplicationContext()).setPreLoad(true);




                for (String s : resultStrs) {
                    Log.v(LOG_TAG, "Forecast entry: " + s);
                }
                Log.i(LOG_TAG, "finished entry to realm");

                return null;

            }


        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            adapter.notifyDataSetChanged();
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
                    Log.i(LOG_TAG, "call getWeatherDataFromJson");
                    return getWeatherDataFromJson(forecastJsonStr,numDays);
                }
                catch (JSONException e)
                {
                    Log.e(LOG_TAG,e.getMessage(),e);
                    e.printStackTrace();
                }


                return null;
            }

        }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id ==  R.id.action_map){


           /* Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);*/

            openPreferedLocationInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void  openPreferedLocationInMap(){

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String location = sharedPrefs.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));

        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q",location)
                .build();

        Intent intent = new Intent();
        intent.setData(geoLocation);
        intent.setAction(Intent.ACTION_VIEW);
        if (intent.resolveActivity(getPackageManager())!= null) {
            startActivity(intent);
        }
        else
        {
            Log.d("LOG_TAG","Couldn't call" + location);
        }



    }



    }




