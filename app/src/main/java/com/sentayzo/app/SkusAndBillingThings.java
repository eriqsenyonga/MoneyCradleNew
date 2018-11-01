package com.sentayzo.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
    public static String KEY_WHICH_SUBSCRIPTION_SKU = "KEY_WHICH_SUBSCRIPTION_SKU";
    public static String KEY_PAYMENT_STATE = "KEY_PAYMENT_STATE";
    public static String KEY_HAS_ACCESS = "KEY_HAS_ACCESS";
    String VERIFY_TOKEN_SAVE_TO_DB = "http://moneycradle.plexosys-consult.com/verifyTokenAndSaveToDb.php";
    String CHECK_IF_EMAIL_IS_PREMIUM = "http://moneycradle.plexosys-consult.com/checkIfUserPremiumByEmail.php";
    ApplicationClass applicationClass = ApplicationClass.getInstance();
    public static String KEY_BILLING_PREFS = "my_billing_prefs";

    public static String KEY_SUBSCRIPTION_EXPIRY_DATE = "expiry_date";

    Context context;

    SharedPreferences billingPrefs, userSharedPrefs;
    SharedPreferences.Editor editor;
    Dialog checkingSubDialog;


    public SkusAndBillingThings(Context c) {

        context = c;
        billingPrefs = context.getSharedPreferences(KEY_BILLING_PREFS, MODE_PRIVATE);
        editor = billingPrefs.edit();

        userSharedPrefs = context.getSharedPreferences("USER_DETAILS",
                Context.MODE_PRIVATE);


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        builder.setView(v);
        builder.setCancelable(false);
        checkingSubDialog = builder.create();


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
        editor.putBoolean(KEY_HAS_ACCESS, bool).apply();

    }

    public void setWhichSku(String sku) {

        editor.putString(KEY_WHICH_SUBSCRIPTION_SKU, sku).apply();


    }

    public String getWhichSku() {

        return billingPrefs.getString(KEY_WHICH_SUBSCRIPTION_SKU, "");

    }

    public String getPurchaseToken() {
        return billingPrefs.getString(KEY_PURCHASE_TOKEN, "");
    }

    public void setSubscriptionExpiryDateAndPaymentState(long expiryDate, int paymentState) {
        editor.putLong(KEY_SUBSCRIPTION_EXPIRY_DATE, expiryDate).apply();
        editor.putInt(KEY_PAYMENT_STATE, paymentState).apply();

        setHasAccess(expiryDate);


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

    public void showPaymentDialog(String title) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_upgrade, null);
        CardView bShowPremiumPlans = v.findViewById(R.id.b_show_plans);
        CardView bTryFree = v.findViewById(R.id.b_try_for_free);
        TextView tvTitle = v.findViewById(R.id.tv_upgrade_title);

        tvTitle.setText(title);

        builder.setView(v);
        builder.setCancelable(true);
        bShowPremiumPlans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, UpgradeActivity.class);
                context.startActivity(i);
            }
        });

        bTryFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, UpgradeActivity.class);
                i.putExtra("try_free", true);
                context.startActivity(i);

            }
        });


       /* builder.setMessage(context.getResources().getString(
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
*/
        Dialog paymentDialog = builder.create();
        paymentDialog.show();

    }

    public void showAccountHoldDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_account_hold, null);
        CardView bSubscriptionSettings = v.findViewById(R.id.b_sub_settings);

        builder.setView(v);
        builder.setCancelable(true);

        bSubscriptionSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://play.google.com/store/account/subscriptions?sku=" + getWhichSku() + "&package=com.sentayzo.app";
                Uri webpage = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                context.startActivity(intent);

            }
        });

        Dialog accountHoldDialog = builder.create();
        accountHoldDialog.show();

    }


    public void setHasAccess(long expiryTimeMillis) {

        if (expiryTimeMillis > System.currentTimeMillis()) {
//If expiry is in the future, then, has access

            setHasAccess(true);
        } else {
//if expiry is in the past, then has no access
            setHasAccess(false);

        }

    }

    public void setHasAccess(boolean bool) {


        editor.putBoolean(KEY_HAS_ACCESS, bool).apply();


    }

    public boolean hasAccess() {

        return billingPrefs.getBoolean(KEY_HAS_ACCESS, false);

    }


    public void checkSubValidityByExpiryDate() {


        if (isAlreadyFullUser()) {

            setHasAccess(true);

        } else {
            long expiryDate = billingPrefs.getLong(KEY_SUBSCRIPTION_EXPIRY_DATE, 0);

            if (expiryDate > System.currentTimeMillis()) {

                //if expiry in future
                setHasAccess(true);

            } else {

                //if the expiry date is in the past, then the user hasnt paid for something yet or it has just not been updated yet
                //so check online to verify the actual current status

                checkOnlineForUpdatedCurrentStatus();


            }
        }

    }

    private void checkOnlineForUpdatedCurrentStatus() {

        checkingSubDialog.show();

        userSharedPrefs = context.getSharedPreferences("USER_DETAILS",
                Context.MODE_PRIVATE);

        final String email = userSharedPrefs.getString("email", "");
        final String purchaseToken = getPurchaseToken();
        final String purchaseSku = getWhichSku();

        StringRequest saveToDbRequest = new StringRequest(Request.Method.POST, VERIFY_TOKEN_SAVE_TO_DB,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //     Toast.makeText(UpgradeActivity.this, " savED to db", Toast.LENGTH_LONG).show();


                        if (response.substring(0, 1).equalsIgnoreCase("{")) {

                            //if the response is valid json as opposed to fatal errors

                            handlePurchaseJsonResponseFromServer(response);


                        } else {
                            checkingSubDialog.cancel();
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error instanceof NoConnectionError) {
                            checkingSubDialog.cancel();

                            setHasAccess(false);
                            Toast.makeText(context, "Error: No internet connection ", Toast.LENGTH_LONG).show();

                        } else if (error instanceof TimeoutError) {
                            checkingSubDialog.cancel();

                            setHasAccess(false);
                            Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_LONG).show();


                        } else {

                            checkingSubDialog.cancel();

                            setHasAccess(false);
                            Toast.makeText(context, "Error checking subscription status", Toast.LENGTH_LONG).show();
                        }

                        checkingSubDialog.cancel();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("email", email);
                map.put("purchase_token", purchaseToken);
                map.put("purchase_sku", purchaseSku);

                return map;
            }
        };

        applicationClass.add(saveToDbRequest);

    }

    private void handlePurchaseJsonResponseFromServer(String response) {


          /*
       sample json from server

       {
            "autoRenewing": true,
                "cancelReason": null,
                "countryCode": "UG",
                "developerPayload": "",
                "emailAddress": null,
                "expiryTimeMillis": "1538684443548",
                "familyName": null,
                "givenName": null,
                "kind": "androidpublisher#subscriptionPurchase",
                "linkedPurchaseToken": null,
                "orderId": "GPA.3370-8983-9561-97544",
                "paymentState": 1,
                "priceAmountMicros": "47880000",
                "priceCurrencyCode": "USD",
                "profileId": null,
                "profileName": null,
                "purchaseType": 0,
                "startTimeMillis": "1538682526968",
                "userCancellationTimeMillis": null
        }*/

        try {

            JSONObject purchaseObject = new JSONObject(response);

            long expiryDate = purchaseObject.getLong("expiryTimeMillis");
            setSubscriptionExpiryDate(expiryDate);

            if (purchaseObject.isNull("paymentState")) {

                if (expiryDate > System.currentTimeMillis()) {
                    //if payment state is null and expiry in future, grant access

                    setHasAccess(true);

                } else {

                    setHasAccess(false);
                    setPremiumPurchased(false);
                    // remove extra accounts
                    new DbClass(context).closeExtraAccountsIfAny();


                }

            } else {

                int paymentState = purchaseObject.getInt("paymentState");

                setSubscriptionExpiryDateAndPaymentState(expiryDate, paymentState);

                if (paymentState == 0 && expiryDate < System.currentTimeMillis()) {

                    //hasnt paid and expiry is in the past, account is onHold

                    setHasAccess(false);


                } else if (paymentState == 1 && expiryDate > System.currentTimeMillis()) {
                    //if has paid
                    setHasAccess(true);

                } else if (paymentState == 0 && expiryDate > System.currentTimeMillis()) {
                    //hasnt paid and expiry in future, account is in Grace period

                    setHasAccess(true);


                } else if (purchaseObject.getInt("cancelReason") >= 0 && expiryDate < System.currentTimeMillis()) {
                    //if the subscription was cancelled for any reason and expiryDate is in the past
                    //cancelReason

                    setHasAccess(false);
                    setPremiumPurchased(false);

                    // remove extra accounts
                    new DbClass(context).closeExtraAccountsIfAny();

                }
            }
            checkingSubDialog.cancel();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void setSubscriptionExpiryDate(long expiryDate) {

        editor.putLong(KEY_SUBSCRIPTION_EXPIRY_DATE, expiryDate).apply();
        // editor.putInt(KEY_PAYMENT_STATE, paymentState).apply();
        setHasAccess(expiryDate);
    }

    public void setAllToDefault() {
        editor.putLong(KEY_SUBSCRIPTION_EXPIRY_DATE, 0).apply();
        editor.putString(KEY_WHICH_SUBSCRIPTION_SKU, "").apply();
        setPremiumPurchased(false);
        setPurchasedAds(false);
        editor.putString(KEY_PURCHASE_TOKEN, "").apply();

    }


    public void checkIfUserIsPremium() {

        checkingSubDialog.show();

        final String email = userSharedPrefs.getString("email", "");

        StringRequest checkServerForEmailRequest = new StringRequest(Request.Method.POST, CHECK_IF_EMAIL_IS_PREMIUM,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //     Toast.makeText(UpgradeActivity.this, " savED to db", Toast.LENGTH_LONG).show();

                        handleDatabaseResponseFromServer(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error instanceof NoConnectionError) {

                            setHasAccess(false);
                            checkingSubDialog.cancel();
                            Toast.makeText(context, "Error: No internet connection ", Toast.LENGTH_LONG).show();

                        } else if (error instanceof TimeoutError) {
                            checkingSubDialog.cancel();

                            setHasAccess(false);
                            Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_LONG).show();


                        } else {

                            checkingSubDialog.cancel();
                            setHasAccess(false);
                            Toast.makeText(context, "Error checking subscription status", Toast.LENGTH_LONG).show();
                        }

                        checkingSubDialog.cancel();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("email", email);

                return map;
            }
        };

        applicationClass.add(checkServerForEmailRequest);

    }

    private void handleDatabaseResponseFromServer(String response) {

/*
IF USER EXISTS RESPONSE
        {
            "exists": true,
                "user": {
            "purchase_token": "aajcmclmiblipgimkkmmaane.AO-J1OwZX4ofCVajh_kezCwCDIz7OBwGEOTUJeLoG2tKlLrkY9OaRNson7laA1c7e73bkj0z7X0lTEEqrqtR9yvmg703Hw5Cm46PYfVEtwRGVlx4g77cO9TuTqMZycvQHrq9xxuhS7m_",
                    "purchase_sku": "sku_premium_monthly",
                    "expiry_time": "1539956924710",
                    "current_status": "cancelled"
        }
        }

IF USER DOESN'T EXIST RESPONSE
        {
            "exists": false
        }
        */
        try {
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.getBoolean("exists")) {
                //if the user exists in the db then add the purchase_token and purchase_sku to the local

                setPremiumPurchaseToken(jsonObject.getString("purchase_token"));
                setWhichSku(jsonObject.getString("purchase_sku"));
                setSubscriptionExpiryDate(jsonObject.getLong("expiry_time"));

                checkOnlineForUpdatedCurrentStatus();


            } else {

                checkingSubDialog.cancel();
            }


        } catch (JSONException e) {
            e.printStackTrace();
            checkingSubDialog.cancel();

        }


    }
}
