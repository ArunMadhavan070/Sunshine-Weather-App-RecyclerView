package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity {






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
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

        return super.onOptionsItemSelected(item);
    }*/

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {



        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        private static final String FORCAST_SHARE_HASHTAG = "#SunshineApp";
        private String day;
       /* private int mForcastTemp_high;
        private int mForcastTemp_low;*/


        private String city ;
        private long high ;
        private long low ;
        private String description ;
        private double pressure ;
        private int humidity ;
        private double windSpeed ;
        private double windDirection ;
        private double cityLatitude ;
        private double cityLongitude ;
        private String country ;
        private Context context;



        private String mForcastResult;


        public DetailFragment() {

            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Intent intent = getActivity().getIntent();
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            if (intent!=null) {
                day = intent.getStringExtra("day");
                city = intent.getStringExtra("city");
                country = intent.getStringExtra("country");
                description = intent.getStringExtra("description");

                high = intent.getLongExtra("high", 0);
                low = intent.getLongExtra("low", 0);



                humidity = intent.getIntExtra("humidity", 0);

                pressure = intent.getDoubleExtra("pressure", 0);
                windSpeed = intent.getDoubleExtra("windspeed", 0);
                windDirection = intent.getDoubleExtra("winddirection", 0);
                cityLatitude = intent.getDoubleExtra("citylatitude", 0);
                cityLongitude = intent.getDoubleExtra("citylongitude", 0);

                TextView daytv = (TextView) rootView.findViewById(R.id.detail_day_textview);
                TextView hightv = (TextView) rootView.findViewById(R.id.detail_high_textview);
                TextView lowtv = (TextView) rootView.findViewById(R.id.detail_low_textview);
                ImageView iconImage = (ImageView) rootView.findViewById(R.id.detail_icon);
                TextView descriptiontv = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
                TextView humiditytv = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
                TextView pressuretv = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
                TextView windtv = (TextView) rootView.findViewById(R.id.detail_wind_textview);

                daytv.setText(day);


                String direction = "Unknown";
                if (windDirection >= 337.5 || windDirection < 22.5) {
                    direction = "N";
                } else if (windDirection >= 22.5 && windDirection < 67.5) {
                    direction = "NE";
                } else if (windDirection >= 67.5 && windDirection < 112.5) {
                    direction = "E";
                } else if (windDirection >= 112.5 && windDirection < 157.5) {
                    direction = "SE";
                } else if (windDirection >= 157.5 && windDirection < 202.5) {
                    direction = "S";
                } else if (windDirection >= 202.5 && windDirection < 247.5) {
                    direction = "SW";
                } else if (windDirection >= 247.5 && windDirection < 292.5) {
                    direction = "W";
                } else if (windDirection >= 292.5 && windDirection < 337.5) {
                    direction = "NW";
                }
                else

                direction = String.valueOf(windDirection);

                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String  unitType = sharedPrefs.getString(getString(R.string.pref_temp_key),getString(R.string.pref_units_metric));
                if (unitType.equals(getString(R.string.pref_units_imperial)))
                {

                    hightv.setText(""+ high +" \u2109");
                    lowtv.setText(""+ low +" \u2109");
                    windSpeed = 0.621371192237334f * windSpeed;

                    windtv.setText(String.format(getString(R.string.format_wind_mph),windSpeed,direction));
                   // windtv.setText("Wind Speed : "+windSpeed +" m/s\n"+"Wind Direction : "+direction);




                }
                else
                {

                    hightv.setText(""+ high +"\u00b0");
                    lowtv.setText(""+ low +"\u00b0");
                    windtv.setText(String.format(getString(R.string.format_wind_kmh),windSpeed,direction));
                    //windtv.setText("Wind Speed : "+windSpeed +" Km/hr\n"+"Wind Direction : "+direction);


                }


                descriptiontv.setText(description);
                humiditytv.setText("Humidity : "+humidity+"%" );
                pressuretv.setText("Pressure : "+pressure + " hpa");
                switch (description) {

                    case "Clear": {
                        Glide.with(getContext())
                                .load(R.drawable.art_clear)
                                .asBitmap()
                                .fitCenter()
                                .into(iconImage);

                        break;


                    }

                    case "Clouds":

                    {

                        Glide.with(getContext())
                                .load(R.drawable.art_clouds)
                                .asBitmap()
                                .fitCenter()
                                .into(iconImage);
                        break;


                    }

                    case "Rain":

                    {

                        Glide.with(getContext())
                                .load(R.drawable.art_rain)
                                .asBitmap()
                                .fitCenter()
                                .into(iconImage);
                        break;


                    }

                    case "Light Rain":

                    {

                        Glide.with(getContext())
                                .load(R.drawable.art_light_rain)
                                .asBitmap()
                                .fitCenter()
                                .into(iconImage);
                        break;


                    }

                    case "Light Clouds":

                    {

                        Glide.with(getContext())
                                .load(R.drawable.art_light_clouds)
                                .asBitmap()
                                .fitCenter()
                                .into(iconImage);
                        break;


                    }

                    case "Fog":

                    {

                        Glide.with(getContext())
                                .load(R.drawable.art_fog)
                                .asBitmap()
                                .fitCenter()
                                .into(iconImage);
                        break;


                    }


                    case "Storm":

                    {

                        Glide.with(getContext())
                                .load(R.drawable.art_storm)
                                .asBitmap()
                                .fitCenter()
                                .into(iconImage);
                        break;


                    }

                    case "Snow":

                    {

                        Glide.with(getContext())
                                .load(R.drawable.art_snow)
                                .asBitmap()
                                .fitCenter()
                                .into(iconImage);

                        break;


                    }


                }


            }

            return rootView;
        }

        private Intent createShareForcastIntent() {

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,mForcastResult + FORCAST_SHARE_HASHTAG);
            return shareIntent;
        }


        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
            inflater.inflate(R.menu.detailfragment,menu);
            MenuItem menuItem = menu.findItem(R.id.action_share);

            ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

            if(mShareActionProvider != null){

                mShareActionProvider.setShareIntent(createShareForcastIntent());
            }
            else
            {
                Log.d(LOG_TAG,"Share Action Provider is null");
            }

        }
    }


}