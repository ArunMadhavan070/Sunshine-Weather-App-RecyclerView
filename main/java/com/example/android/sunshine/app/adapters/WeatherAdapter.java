package com.example.android.sunshine.app.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.sunshine.app.DetailActivity;
import com.example.android.sunshine.app.MainActivity;
import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.app.Prefs;
import com.example.android.sunshine.app.model.Weather;
import com.example.android.sunshine.app.realm.RealmController;


import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by coolm on 10/17/2016.
 */
public class WeatherAdapter extends RealmRecyclerViewAdapater<Weather> {

    private final String LOG_TAG = WeatherAdapter.class.getSimpleName();
    private boolean mUseTodayLayout = true;
    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static int count = 1;


    final Context context;
    private Realm realm;
    private LayoutInflater inflater;
    private RealmChangeListener listener;

    public WeatherAdapter(Context context) {


        if (context == null) {
            throw new IllegalArgumentException("Context can not be null");
        }

        this.context = context;

    }

    // create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate a new card view

        int layoutId;



        if (viewType == VIEW_TYPE_TODAY) {
            layoutId = R.layout.item_books_today;
            View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            //count++;
            return new TodayCardViewHolder(view);


        } else {
            layoutId = R.layout.item_books;
            View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            //count++;
            return new CardViewHolder(view);



        }


    }


    // replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        realm = RealmController.getInstance().getRealm();
        final Weather weather = getItem(position);

        if (viewHolder.getItemViewType() == VIEW_TYPE_TODAY) {

            final TodayCardViewHolder todayHolder = (TodayCardViewHolder) viewHolder;
            todayHolder.textDay.setText("Today\n\n" + weather.getDay());
            todayHolder.textCountry.setText(weather.getCity() + ", " + weather.getCountry());
            todayHolder.textHigh.setText("" + weather.getCityHighTemp());
            todayHolder.textLow.setText("" + weather.getCityLowTemp());
            todayHolder.textDescription.setText(weather.getDescription());
            switch (weather.getDescription()) {

                case "Clear": {
                    Glide.with(context)
                            .load(R.drawable.art_clear)
                            .asBitmap()
                            .fitCenter()
                            .into(todayHolder.cardImage);

                    break;


                }

                case "Clouds":

                {

                    Glide.with(context)
                            .load(R.drawable.art_clouds)
                            .asBitmap()
                            .fitCenter()
                            .into(todayHolder.cardImage);
                    break;


                }

                case "Rain":

                {

                    Glide.with(context)
                            .load(R.drawable.art_rain)
                            .asBitmap()
                            .fitCenter()
                            .into(todayHolder.cardImage);
                    break;


                }

                case "Light Rain":

                {

                    Glide.with(context)
                            .load(R.drawable.art_light_rain)
                            .asBitmap()
                            .fitCenter()
                            .into(todayHolder.cardImage);
                    break;


                }

                case "Light Clouds":

                {

                    Glide.with(context)
                            .load(R.drawable.art_light_clouds)
                            .asBitmap()
                            .fitCenter()
                            .into(todayHolder.cardImage);
                    break;


                }

                case "Fog":

                {

                    Glide.with(context)
                            .load(R.drawable.art_fog)
                            .asBitmap()
                            .fitCenter()
                            .into(todayHolder.cardImage);
                    break;


                }


                case "Storm":

                {

                    Glide.with(context)
                            .load(R.drawable.art_storm)
                            .asBitmap()
                            .fitCenter()
                            .into(todayHolder.cardImage);
                    break;


                }

                case "Snow":

                {

                    Glide.with(context)
                            .load(R.drawable.art_snow)
                            .asBitmap()
                            .fitCenter()
                            .into(todayHolder.cardImage);

                    break;


                }


            }

            listener = new RealmChangeListener() {
                @Override
                public void onChange() {
                    notifyDataSetChanged();
                }
            };
            realm.addChangeListener(listener);

            todayHolder.card.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    RealmResults<Weather> results = realm.where(Weather.class).findAll();

                    // Get the book title to show it in toast message
                    Weather w = results.get(todayHolder.getAdapterPosition());
                    String day = w.getDay();
                    String city = w.getCity();
                    long high = w.getCityHighTemp();
                    long low = w.getCityLowTemp();
                    String desc = w.getDescription();
                    double pressure = w.getPressure();
                    int humidity = w.getHumidity();
                    double windSpeed = w.getWindSpeed();
                    double windDirection = w.getWindDirection();
                    double cityLatitude = w.getCityLatitude();
                    double cityLongitude = w.getCityLongitude();
                    String country = w.getCountry();


                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("day", day);
                    intent.putExtra("high", high);
                    intent.putExtra("low", low);
                    intent.putExtra("city", city);
                    intent.putExtra("pressure", pressure);
                    intent.putExtra("humidity", humidity);
                    intent.putExtra("windspeed", windSpeed);
                    intent.putExtra("winddirection", windDirection);
                    intent.putExtra("citylatitude", cityLatitude);
                    intent.putExtra("citylongitude", cityLongitude);
                    intent.putExtra("description", desc);
                    intent.putExtra("country", country);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }
            });


        } else {

            final CardViewHolder holder = (CardViewHolder) viewHolder;
            holder.textDay.setText(weather.getDay());
            holder.textHigh.setText("" + weather.getCityHighTemp());
            holder.textLow.setText("" + weather.getCityLowTemp());
            holder.textDescription.setText(weather.getDescription());

            // Get weather icon
            switch (weather.getDescription()) {

                case "Clear": {
                    Glide.with(context)
                            .load(R.drawable.art_clear)
                            .asBitmap()
                            .fitCenter()
                            .into(holder.cardImage);

                    break;


                }

                case "Clouds":

                {

                    Glide.with(context)
                            .load(R.drawable.art_clouds)
                            .asBitmap()
                            .fitCenter()
                            .into(holder.cardImage);
                    break;


                }

                case "Rain":

                {

                    Glide.with(context)
                            .load(R.drawable.art_rain)
                            .asBitmap()
                            .fitCenter()
                            .into(holder.cardImage);
                    break;


                }

                case "Light Rain":

                {

                    Glide.with(context)
                            .load(R.drawable.art_light_rain)
                            .asBitmap()
                            .fitCenter()
                            .into(holder.cardImage);
                    break;


                }

                case "Light Clouds":

                {

                    Glide.with(context)
                            .load(R.drawable.art_light_clouds)
                            .asBitmap()
                            .fitCenter()
                            .into(holder.cardImage);
                    break;


                }

                case "Fog":

                {

                    Glide.with(context)
                            .load(R.drawable.art_fog)
                            .asBitmap()
                            .fitCenter()
                            .into(holder.cardImage);
                    break;


                }


                case "Storm":

                {

                    Glide.with(context)
                            .load(R.drawable.art_storm)
                            .asBitmap()
                            .fitCenter()
                            .into(holder.cardImage);
                    break;


                }

                case "Snow":

                {

                    Glide.with(context)
                            .load(R.drawable.art_snow)
                            .asBitmap()
                            .fitCenter()
                            .into(holder.cardImage);

                    break;


                }


            }
            listener = new RealmChangeListener() {
                @Override
                public void onChange() {
                    notifyDataSetChanged();
                }
            };
            realm.addChangeListener(listener);
            holder.card.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    RealmResults<Weather> results = realm.where(Weather.class).findAll();

                    // Get the book title to show it in toast message
                    Weather w = results.get(holder.getAdapterPosition());
                    String day = w.getDay();
                    String city = w.getCity();
                    long high = w.getCityHighTemp();
                    long low = w.getCityLowTemp();
                    String desc = w.getDescription();
                    double pressure = w.getPressure();
                    int humidity = w.getHumidity();
                    double windSpeed = w.getWindSpeed();
                    double windDirection = w.getWindDirection();
                    double cityLatitude = w.getCityLatitude();
                    double cityLongitude = w.getCityLongitude();
                    String country = w.getCountry();


                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("day", day);
                    intent.putExtra("high", high);
                    intent.putExtra("low", low);
                    intent.putExtra("city", city);
                    intent.putExtra("pressure", pressure);
                    intent.putExtra("humidity", humidity);
                    intent.putExtra("windspeed", windSpeed);
                    intent.putExtra("winddirection", windDirection);
                    intent.putExtra("citylatitude", cityLatitude);
                    intent.putExtra("citylongitude", cityLongitude);
                    intent.putExtra("description", desc);
                    intent.putExtra("country", country);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }
            });

        }


    }



    // return the size of your data set (invoked by the layout manager)
    public int getItemCount() {

        if (getRealmAdapter() != null) {



            Log.i("getItemCount","data count"+getRealmAdapter().getCount());
            return getRealmAdapter().getCount();


        }
        return 0;
    }



    @Override
    public final int getItemViewType(int position) {

        Log.i("getItemViewType","data position"+position);
        return (position == 0 ) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }



    public static class CardViewHolder extends RecyclerView.ViewHolder {

        public CardView card;
        public TextView textDay;
        public TextView textHigh;
        public TextView textLow;
        public ImageView cardImage;

        public TextView textDescription;


        public CardViewHolder(View itemView) {
            // standard view holder pattern with Butterknife view injection
            super(itemView);

            card = (CardView) itemView.findViewById(R.id.card_books);
            textDay = (TextView) itemView.findViewById(R.id.text_weather_day);
            textHigh = (TextView) itemView.findViewById(R.id.text_weather_high);
            textLow = (TextView) itemView.findViewById(R.id.text_weather_low);
            textDescription = (TextView) itemView.findViewById(R.id.text_weather_description);
            cardImage = (ImageView) itemView.findViewById(R.id.image_background);




        }


    }

    public static class TodayCardViewHolder extends RecyclerView.ViewHolder {

        public CardView card;
        public TextView textDay;
        public TextView textHigh;
        public TextView textLow;
        public TextView textCountry;
        public ImageView cardImage;

        public TextView textDescription;


        public TodayCardViewHolder(View itemView) {
            // standard view holder pattern with Butterknife view injection
            super(itemView);

            card = (CardView) itemView.findViewById(R.id.card_books);
            textDay = (TextView) itemView.findViewById(R.id.text_weather_day);
            textHigh = (TextView) itemView.findViewById(R.id.text_weather_high);
            textLow = (TextView) itemView.findViewById(R.id.text_weather_low);
            textDescription = (TextView) itemView.findViewById(R.id.text_weather_description);
            cardImage = (ImageView) itemView.findViewById(R.id.image_background);
            textCountry = (TextView) itemView.findViewById(R.id.text_country);



        }


    }


}
