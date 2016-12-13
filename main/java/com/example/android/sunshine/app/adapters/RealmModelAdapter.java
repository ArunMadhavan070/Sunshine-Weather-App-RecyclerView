package com.example.android.sunshine.app.adapters;

/**
 * Created by coolm on 10/17/2016.
 */
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.sunshine.app.model.Weather;

import io.realm.RealmBaseAdapter;
import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import io.realm.RealmResults;


public class RealmModelAdapter<T extends RealmObject> extends RealmBaseAdapter<T> {



    public RealmModelAdapter(Context context, RealmResults<T> realmResults, boolean automaticUpdate) {

        super(context, realmResults, automaticUpdate);




    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return null;
    }
}
