package com.example.android.sunshine.app.realm;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.example.android.sunshine.app.model.Weather;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by coolm on 10/17/2016.
 */
public class RealmController {

    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }



    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {

        return instance;
    }

    public Realm getRealm() {

        return realm;
    }

    //Refresh the realm istance
    public void refresh() {

        realm.refresh();
    }

    //clear all objects from Weather.class
    public void clearAll() {

        realm.beginTransaction();
        realm.clear(Weather.class);
        realm.commitTransaction();
    }

    //find all objects in the Book.class
    public RealmResults<Weather> getWeathers() {

        return realm.where(Weather.class).findAll();
    }

    //query a single item with the given id
    public Weather getWeather(String id) {

        return realm.where(Weather.class).equalTo("id", id).findFirst();
    }

    //check if Weather.class is empty
    public boolean hasWeather() {

        return !realm.allObjects(Weather.class).isEmpty();
    }

    //query example
    /*public RealmResults<Weather> queryedWeather() {

        return realm.where(Weather.class)
                .contains("day", "Day 0")
                .or()
                .contains("description", "Realm")
                .findAll();

    }*/
}