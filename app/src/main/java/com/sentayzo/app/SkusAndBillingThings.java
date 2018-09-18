package com.sentayzo.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SkusAndBillingThings {

    public static String SKU_PREMIUM_MONTHLY = "sku_premium_monthly";
    public static String SKU_PREMIUM_QUARTERLY = "sku_premium_quarterly";
    public static String SKU_PREMIUM_YEARLY = "sku_premium_yearly";
    public static String SKU_REMOVE_ADS = "sku_remove_ads";

    public static String KEY_FREE_TRIAL_PERIOD = "KEY_FREE_TRIAL_PERIOD";

    public static String KEY_PURCHASED_ADS = "KEY_PURCHASED_ADS";
    public static String KEY_PURCHASED_UNLOCK = "KEY_PURCHASED_UNLOCK"; //this is from the previous billing method
    public static String KEY_PURCHASED_PREMIUM_ADS = "KEY_PURCHASED_PREMIUM_ADS";
    public static String KEY_PURCHASED_PREMIUM = "KEY_PURCHASED_PREMIUM"; // this is the new subscription stunt
    public static String KEY_PURCHASE_TOKEN = "KEY_PURCHASE_TOKEN";

    public static String KEY_BILLING_PREFS = "my_billing_prefs";

    public static String KEY_SUBSCRIPTION_EXPIRY_DATE = "expiry_date";

    Context context;

    SharedPreferences billingPrefs;
    SharedPreferences.Editor editor;


    public SkusAndBillingThings(Context c) {

        context = c;
        billingPrefs = context.getSharedPreferences(KEY_BILLING_PREFS, MODE_PRIVATE);
        editor = billingPrefs.edit();

    }

    public boolean isAlreadyFullUser() {
        //method to check whether this user is a legacy purchaser

        if (billingPrefs.getBoolean(KEY_PURCHASED_UNLOCK, false)) {

            setIsAlreadyFullUserPrivileges();
            return true;

        } else {

            return false;

        }

    }

    public void setFreeTrialPeriod(boolean bool) {

        editor.putBoolean(KEY_FREE_TRIAL_PERIOD, bool).apply();
    }


    public boolean isFreeTrialPeriod() {

        return billingPrefs.getBoolean(KEY_FREE_TRIAL_PERIOD, true);
    }

    public void setIsAlreadyFullUserPrivileges() {


        //if a user is already a full user from before, give them access to premium content for free
        setPurchasedAds(true);
        setPremiumPurchased(true);

    }


    public void setPremiumPurchased(boolean bool) {

        //if premium is purchased true
        editor.putBoolean(KEY_PURCHASED_PREMIUM_ADS, bool).apply();
        editor.putBoolean(KEY_PURCHASED_PREMIUM, bool).apply();

    }

    public void setSubscriptionExpiryDate(long expiryDate) {
        editor.putLong(KEY_SUBSCRIPTION_EXPIRY_DATE, expiryDate).apply();

    }

    public void setPremiumPurchaseToken(String premiumPurchaseToken) {


        editor.putString(KEY_PURCHASE_TOKEN, premiumPurchaseToken).apply();


    }

    public boolean isPurchasedAds() {


        if (billingPrefs.getBoolean(KEY_PURCHASED_ADS, false)
                || billingPrefs.getBoolean(KEY_PURCHASED_PREMIUM_ADS, false)) {
            //if the user purchased ads normally or has premium then return true

            return true;

        } else {
            //if did not purchase ads or is not premium return false

            return false;
        }


    }

    public void setPurchasedAds(boolean bool) {

        editor.putBoolean(KEY_PURCHASED_ADS, bool).apply();

    }

    public boolean isPremiumUser() {


        return billingPrefs.getBoolean(KEY_PURCHASED_PREMIUM, false);


    }

    public void showPaymentDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(context.getResources().getString(
                R.string.payment_dialog_message)
                + "\n\n"
                + context.getResources()
                .getString(R.string.unlock_all_features) + " ?");

        builder.setNegativeButton(
                context.getResources().getString(R.string.no),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });

        builder.setPositiveButton(context.getResources()
                        .getString(R.string.yes),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        Intent i = new Intent(context, UpgradeActivity.class);
                        context.startActivity(i);
                    }
                });

        Dialog paymentDialog = builder.create();
        paymentDialog.show();

    }




}
