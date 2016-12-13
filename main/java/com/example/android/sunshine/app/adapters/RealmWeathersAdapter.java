package com.example.android.sunshine.app.adapters;

/**
 * Created by coolm on 10/17/2016.
 */
import android.content.Context;

import com.example.android.sunshine.app.model.Weather;


import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class RealmWeathersAdapter extends RealmModelAdapter<Weather> {



    public RealmWeathersAdapter(Context context, RealmResults<Weather> realmResults, boolean automaticUpdate) {

       super(context, realmResults, automaticUpdate);







    }
}
