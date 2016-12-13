package com.example.android.sunshine.app.adapters;



/**
 * Created by coolm on 10/17/2016.
 */

import android.support.v7.widget.RecyclerView;

import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;

public abstract class  RealmRecyclerViewAdapater<T extends RealmObject> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private RealmBaseAdapter<T> realmBaseAdapter;

    public T getItem(int position) {

        return realmBaseAdapter.getItem(position);
    }

    public RealmBaseAdapter<T> getRealmAdapter() {

        return realmBaseAdapter;
    }

    public void setRealmAdapter(RealmBaseAdapter<T> realmAdapter) {

        this.realmBaseAdapter = realmAdapter;
    }




}
