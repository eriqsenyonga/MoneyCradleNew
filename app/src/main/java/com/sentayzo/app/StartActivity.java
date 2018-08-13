package com.sentayzo.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;

public class StartActivity extends Activity {

    SharedPreferences sharedPrefs, billingPrefs;
    Boolean PIN;
    String DATABASE_NAME = "sentayzoDb.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        billingPrefs = getSharedPreferences("my_billing_prefs",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = billingPrefs.edit();

        if (billingPrefs.getBoolean("firstTime", true)) {

            Log.d("StartActivity", "first_time");

            Long timeNow = System.currentTimeMillis();
            Long fiveDays = (long) (5 * 24 * 60 * 60 * 1000);
            Long endTime = timeNow + fiveDays;

            editor.putBoolean("KEY_PURCHASED_ADS", false);
            editor.putBoolean("KEY_PURCHASED_UNLOCK", false);
            editor.putBoolean("KEY_FREE_TRIAL_PERIOD", true);
            editor.putLong("KEY_COUNTER", 0);

            editor.putLong("KEY_START_DATE_TIME", timeNow);

            editor.putLong("KEY_END_OF_FREE_TRIAL_PERIOD", endTime);

            editor.putBoolean("already_executed_end_free_period", false);


            if (doesDatabaseExist(getApplicationContext(), DATABASE_NAME) == true) {

                //if the database is ready, do this

                DbClass dbClass = new DbClass(getApplicationContext());

                dbClass.setUpInitials();




            }


            editor.putBoolean("firstTime", false);

            editor.commit();

            Intent i = new Intent(this, AlarmService.class);

            i.putExtra("endTime", endTime);

            startService(i);

        }

        //editor.putBoolean("KEY_PURCHASED_UNLOCK", true).apply();

        //editor.putBoolean("KEY_FREE_TRIAL_PERIOD", true).commit();
        //editor.putBoolean("KEY_PURCHASED_UNLOCK", true).commit();


        sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        PIN = sharedPrefs.getBoolean("pref_PIN_authorisation", false);

        if (PIN == true) {

            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
            startActivity(intent);

            finish();

        } else {

            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            intent.putExtra("zero", 0);
            startActivity(intent);

            finish();

        }



    }



    private static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        Log.d("does database exist", "" + dbFile.exists());
        return dbFile.exists();
    }
}
