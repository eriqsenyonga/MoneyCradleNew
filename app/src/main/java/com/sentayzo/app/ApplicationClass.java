package com.sentayzo.app;

import android.app.Application;
import android.support.multidex.MultiDexApplication;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

public class ApplicationClass extends MultiDexApplication {
 
	// The following line should be changed to include the correct property id.


    private RequestQueue mRequestQueue;
    public static final String TAG = ApplicationClass.class.getName();


    private static final String PROPERTY_ID = "UA-57249913-2";

    public static int GENERAL_TRACKER = 0;
    FirebaseAuth firebaseAuth;
    private static ApplicationClass mInstance;

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public ApplicationClass() {
        super();
    }

    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            
            
            Tracker t = analytics.newTracker(R.xml.app_tracker);
           
            t.enableAdvertisingIdCollection(true);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        FirebaseApp.initializeApp(getApplicationContext());
        firebaseAuth = FirebaseAuth.getInstance();


    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public static synchronized ApplicationClass getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public <T> void add(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancel() {

        mRequestQueue.cancelAll(TAG);
    }
}

