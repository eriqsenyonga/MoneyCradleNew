package com.sentayzo.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import java.io.File;

public class StartActivity extends Activity {

    SharedPreferences sharedPrefs, billingPrefs, userSharedPrefs;
    Boolean PIN;
    String DATABASE_NAME = "sentayzoDb.db";
    SkusAndBillingThings skusAndBillingThings;
  //  Typeface productSansBold, productSansRegular;
   // TextView tvAppName, tvAppFunction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen_layout);
/*

        tvAppName = findViewById(R.id.tv_appname);
        tvAppFunction = findViewById(R.id.tv_app_name);


        productSansBold = Typeface.createFromAsset(getAssets(), "fonts/Product-Sans-Bold.ttf");
        productSansRegular = Typeface.createFromAsset(getAssets(), "fonts/Product-Sans-Regular.ttf");


        tvAppName.setTypeface(productSansBold);
        tvAppFunction.setTypeface(productSansRegular);

*/

        skusAndBillingThings = new SkusAndBillingThings(StartActivity.this);

        billingPrefs = getSharedPreferences("my_billing_prefs",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = billingPrefs.edit();


        if (skusAndBillingThings.isAlreadyFullUser()) {

            //if user already purchased full version from before
            //communicate that they are kawa and dont have to do anything really


        }


        if (billingPrefs.getBoolean("firstTime", true)) {

            Log.d("StartActivity", "first_time");

           /* Long timeNow = System.currentTimeMillis();
            Long sevenDays = (long) (7 * 24 * 60 * 60 * 1000);
            Long endTime = timeNow + sevenDays;*/

            skusAndBillingThings.setPurchasedAds(false);
            //  editor.putBoolean("KEY_PURCHASED_UNLOCK", false);
            skusAndBillingThings.setPremiumPurchased(false);
            //   skusAndBillingThings.setFreeTrialPeriod(true);

         /*   editor.putLong("KEY_COUNTER", 0);

            editor.putLong("KEY_START_DATE_TIME", timeNow);

            editor.putLong("KEY_END_OF_FREE_TRIAL_PERIOD", endTime);

            editor.putBoolean("already_executed_end_free_period", false);*/


            if (doesDatabaseExist(getApplicationContext(), DATABASE_NAME) == true) {

                //if the database is ready, do this

                DbClass dbClass = new DbClass(getApplicationContext());

                dbClass.setUpInitials();


            }


            editor.putBoolean("firstTime", false).apply();


       /*     Intent i = new Intent(this, AlarmService.class);

            i.putExtra("endTime", endTime);

            startService(i);

            */

            Intent intent = new Intent(StartActivity.this, IntroActivity.class);
            intent.putExtra("zero", 0);
            startActivity(intent);

            finish();

        }

        //editor.putBoolean("KEY_PURCHASED_UNLOCK", true).apply();

        //editor.putBoolean("KEY_FREE_TRIAL_PERIOD", true).commit();
        //editor.putBoolean("KEY_PURCHASED_UNLOCK", true).commit();


        sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        PIN = sharedPrefs.getBoolean("pref_PIN_authorisation", false);

        if (PIN == true) {

            Intent intent = new Intent(StartActivity.this, PinActivity.class);
            startActivity(intent);

            finish();

        }


   /*     if(skusAndBillingThings.isPremiumUser()){

            skusAndBillingThings.checkSubValidityByExpiryDate();

        }
*/

        userSharedPrefs = getSharedPreferences("USER_DETAILS",
                Context.MODE_PRIVATE);

        if (userSharedPrefs.getBoolean("logged_in", false)) {

            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            intent.putExtra("zero", 0);
            startActivity(intent);
            finish();

        } else {

            Intent intent = new Intent(StartActivity.this, IntroActivity.class);
            intent.putExtra("zero", 0);
            startActivity(intent);

            finish();


        }


    }


    private static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        //    Log.d("does database exist", "" + dbFile.exists());
        return dbFile.exists();
    }
}
