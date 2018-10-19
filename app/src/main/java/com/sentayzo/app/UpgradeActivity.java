package com.sentayzo.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sentayzo.app.SkusAndBillingThings;

import org.json.JSONException;
import org.json.JSONObject;

public class UpgradeActivity extends AppCompatActivity implements View.OnClickListener, PurchasesUpdatedListener {

    ImageView ivPlatinum;
    ListView lvPremiumBenefits;
    CardView bRemoveAds, bMonthly, bQuarterly, bYearly, cardBlockAds;
    Toolbar toolBar;
    TextView tvRemoveAds;
    private BillingClient mBillingClient;
    SharedPreferences billingPrefs;
    SharedPreferences.Editor editor;
    SkusAndBillingThings skusAndBillingThings;
    boolean isAlreadyPremium = false;
    Intent i;
    String SAVE_TO_DB_URL = "http://moneycradle.plexosys-consult.com/saveSubToDb.php";
    String VERIFY_TOKEN_SAVE_TO_DB = "http://moneycradle.plexosys-consult.com/verifyTokenAndSaveToDb.php";
    ApplicationClass applicationClass = ApplicationClass.getInstance();
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_upgrade);


        i = getIntent();

        bRemoveAds = (CardView) findViewById(R.id.b_remove_ads);
        // ivPlatinum = (ImageView) findViewById(R.id.iv_platinum);
        lvPremiumBenefits = (ListView) findViewById(R.id.lv_premium_benefits);
        bMonthly = (CardView) findViewById(R.id.b_monthly);
        bQuarterly = (CardView) findViewById(R.id.b_quarterly);
        bYearly = (CardView) findViewById(R.id.b_yearly);
        tvRemoveAds = findViewById(R.id.tv_price_remove_ads);
        cardBlockAds = findViewById(R.id.card_block_ads_only);
        progressBar = findViewById(R.id.progressbar);

        skusAndBillingThings = new SkusAndBillingThings(UpgradeActivity.this);

        toolBar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle(R.string.upgrade);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String[] premiumArray = getResources().getStringArray(R.array.premium_benefits);
        List<String> testList = Arrays.asList(premiumArray);

        // Instanciating Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(UpgradeActivity.this,
                R.layout.item_row_premium, R.id.tv_row, testList);

        // setting adapter on listview
        lvPremiumBenefits.setAdapter(adapter);


        billingPrefs = getSharedPreferences("my_billing_prefs", MODE_PRIVATE);

        editor = billingPrefs.edit();

        if (skusAndBillingThings.isPremiumUser()) {
//hide the premium feature that was purchased and also hide block ads only


            isAlreadyPremium = true;


            cardBlockAds.setVisibility(View.GONE);

            if (skusAndBillingThings.getWhichSku().equals(SkusAndBillingThings.SKU_PREMIUM_MONTHLY)) {

                bMonthly.setVisibility(View.GONE);

            } else if (skusAndBillingThings.getWhichSku().equals(SkusAndBillingThings.SKU_PREMIUM_QUARTERLY)) {
                bQuarterly.setVisibility(View.GONE);


            }
            if (skusAndBillingThings.getWhichSku().equals(SkusAndBillingThings.SKU_PREMIUM_YEARLY)) {

                bYearly.setVisibility(View.GONE);

            }
        }


        if (skusAndBillingThings.isPurchasedAds()) {

            cardBlockAds.setVisibility(View.GONE);

        } else {

            cardBlockAds.setVisibility(View.VISIBLE);
        }


        // create new Person


        mBillingClient = BillingClient.newBuilder(this).setListener(this).build();


        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    // The billing client is ready. You can query purchases here.
                    //  Toast.makeText(UpgradeActivity.this, "The billing client is ready. You can query purchases here.", Toast.LENGTH_LONG).show();

                    //   querySkuDetails();

                    if (i.hasExtra("try_free")) {

                        show("Yearly Subscription");
                        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                .setSku(SkusAndBillingThings.SKU_PREMIUM_YEARLY)
                                .setType(BillingClient.SkuType.SUBS) // SkuType.SUB for subscription
                                .build();


                        int responseCode = mBillingClient.launchBillingFlow(UpgradeActivity.this, flowParams);
                    }

                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Toast.makeText(UpgradeActivity.this, "Billing client not ready", Toast.LENGTH_LONG).show();


            }
        });


        bRemoveAds.setOnClickListener(this);
        bYearly.setOnClickListener(this);
        bMonthly.setOnClickListener(this);
        bQuarterly.setOnClickListener(this);

    }
/*
    private void querySkuDetails() {


        List skuList = new ArrayList<>();
        skuList.add(SkusAndBillingThings.SKU_PREMIUM_MONTHLY);
        skuList.add(SkusAndBillingThings.SKU_PREMIUM_QUARTERLY);
        skuList.add(SkusAndBillingThings.SKU_PREMIUM_YEARLY);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);

        mBillingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                        // Process the result.
                     //   Toast.makeText(UpgradeActivity.this, "Query details", Toast.LENGTH_LONG).show();

                        if (responseCode == BillingClient.BillingResponse.OK
                                && skuDetailsList != null) {

                          //  Toast.makeText(UpgradeActivity.this, "Query details 2", Toast.LENGTH_LONG).show();

                            for (SkuDetails skuDetails : skuDetailsList) {

                             //   Toast.makeText(UpgradeActivity.this, "Query details 3", Toast.LENGTH_LONG).show();

                                String sku = skuDetails.getSku();
                                String price = skuDetails.getPrice();
                                if (SkusAndBillingThings.SKU_PREMIUM_MONTHLY.equals(sku)) {
                                    tvRemoveAds.setText(price);
                                }
                            }
                        }


                    }
                });
    }

  */

    @Override
    public void onClick(View v) {
        if (v == bRemoveAds) {
            show("Remove Ads");
            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setSku(SkusAndBillingThings.SKU_REMOVE_ADS)
                    .setType(BillingClient.SkuType.INAPP) // SkuType.SUB for subscription
                    .build();

            int responseCode = mBillingClient.launchBillingFlow(UpgradeActivity.this, flowParams);


        }

        if (v == bMonthly) {
            show("Monthly Subscription");


            if (isAlreadyPremium) {
//if already premium user and is either upgrading or downgrading

                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                        .setOldSku(skusAndBillingThings.getWhichSku())
                        .setSku(SkusAndBillingThings.SKU_PREMIUM_MONTHLY)
                        .setType(BillingClient.SkuType.SUBS) // SkuType.SUB for subscription
                        .build();

                int responseCode = mBillingClient.launchBillingFlow(UpgradeActivity.this, flowParams);

            } else {
//if new purchase
                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                        .setSku(SkusAndBillingThings.SKU_PREMIUM_MONTHLY)
                        .setType(BillingClient.SkuType.SUBS) // SkuType.SUB for subscription
                        .build();

                int responseCode = mBillingClient.launchBillingFlow(UpgradeActivity.this, flowParams);
            }


        }

        if (v == bQuarterly) {

            if (isAlreadyPremium) {
//if already premium user and is either upgrading or downgrading

                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                        .setOldSku(skusAndBillingThings.getWhichSku())
                        .setSku(SkusAndBillingThings.SKU_PREMIUM_QUARTERLY)
                        .setType(BillingClient.SkuType.SUBS) // SkuType.SUB for subscription
                        .build();

                int responseCode = mBillingClient.launchBillingFlow(UpgradeActivity.this, flowParams);

            } else {
//if new purchase
                show("Quarterly Subscription");
                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                        .setSku(SkusAndBillingThings.SKU_PREMIUM_QUARTERLY)
                        .setType(BillingClient.SkuType.SUBS) // SkuType.SUB for subscription
                        .build();

                int responseCode = mBillingClient.launchBillingFlow(UpgradeActivity.this, flowParams);
            }
        }

        if (v == bYearly) {

            if (isAlreadyPremium) {
//if already premium user and is either upgrading or downgrading

                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                        .setOldSku(skusAndBillingThings.getWhichSku())
                        .setSku(SkusAndBillingThings.SKU_PREMIUM_YEARLY)
                        .setType(BillingClient.SkuType.SUBS) // SkuType.SUB for subscription
                        .build();

                int responseCode = mBillingClient.launchBillingFlow(UpgradeActivity.this, flowParams);

            } else {
//if new purchase

                show("Yearly Subscription");
                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                        .setSku(SkusAndBillingThings.SKU_PREMIUM_YEARLY)
                        .setType(BillingClient.SkuType.SUBS) // SkuType.SUB for subscription
                        .build();


                int responseCode = mBillingClient.launchBillingFlow(UpgradeActivity.this, flowParams);
            }
        }
    }

    private void show(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        if (responseCode == BillingClient.BillingResponse.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {

                handlePurchase(purchase);

            }


        } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            // Handle any other error codes.
        }

    }

    private void handlePurchase(Purchase purchase) {

        if (purchase.getSku().equals(SkusAndBillingThings.SKU_PREMIUM_MONTHLY)) {
            //if monthly subscription, open content for the month and set recurring date to 1 month from now

            //do server verification and then update sharedPrefs

            skusAndBillingThings.setPremiumPurchased(true);
            skusAndBillingThings.setWhichSku(SkusAndBillingThings.SKU_PREMIUM_MONTHLY);
            skusAndBillingThings.setPremiumPurchaseToken(purchase.getPurchaseToken());
        //    Log.d("Original Json", purchase.getOriginalJson());


            //  saveSubscriptionToDb(purchase.getPurchaseToken(), purchase.getSku(), purchase.getOrderId());

            verifyTokenAndSaveToDb(purchase.getPurchaseToken(), purchase.getSku());

/*

THIS IS THE FORMAT OF THE ORGINAL JSON
            {
                "orderId": "GPA.3347-3227-2419-64404",
                    "packageName": "com.sentayzo.app",
                    "productId": "sku_premium_monthly",
                    "purchaseTime": 1538436442764,
                    "purchaseState": 0,
                    "purchaseToken": "bcdlpdabcobjekhbfnnejdef.AO-J1Own735J9-IlgFBto3UaaSQ0nK7ZivOvK_PGoRCB7XgNzcIX-_pB9P12hXy38ME4rwipvouM3ti8hzZKTzFs2jBlAuYe26AVBDnVzlfm75Q8SUY7b0wA0Sz22_GyD1cmg2wrg4J-",
                    "autoRenewing": true
            }

*/

        }

        if (purchase.getSku().equals(SkusAndBillingThings.SKU_PREMIUM_QUARTERLY)) {
            //if quarterly subscription, open content for the quarter and set recurring date to 3 month from now

            skusAndBillingThings.setPremiumPurchased(true);
            skusAndBillingThings.setPremiumPurchaseToken(purchase.getPurchaseToken());
            skusAndBillingThings.setWhichSku(SkusAndBillingThings.SKU_PREMIUM_QUARTERLY);


            //  saveSubscriptionToDb(purchase.getPurchaseToken(), purchase.getSku(), purchase.getOrderId());
            verifyTokenAndSaveToDb(purchase.getPurchaseToken(), purchase.getSku());


        }

        if (purchase.getSku().equals(SkusAndBillingThings.SKU_PREMIUM_YEARLY)) {
            //if yearly subscription, open content for the year and set recurring date to 1 YEAR from now
            skusAndBillingThings.setPremiumPurchased(true);
            skusAndBillingThings.setWhichSku(SkusAndBillingThings.SKU_PREMIUM_YEARLY);

            skusAndBillingThings.setPremiumPurchaseToken(purchase.getPurchaseToken());

            //  saveSubscriptionToDb(purchase.getPurchaseToken(), purchase.getSku(), purchase.getOrderId());
            verifyTokenAndSaveToDb(purchase.getPurchaseToken(), purchase.getSku());


        }

        if (purchase.getSku().equals(SkusAndBillingThings.SKU_REMOVE_ADS)) {

            skusAndBillingThings.setPurchasedAds(true);//            purchase.get

        }

    }


    public void verifyTokenAndSaveToDb(final String purchaseToken, final String purchaseSku) {

        progressBar.setVisibility(View.VISIBLE);

        SharedPreferences userSharedPrefs = getSharedPreferences("USER_DETAILS",
                Context.MODE_PRIVATE);

        final String email = userSharedPrefs.getString("email", "");

        StringRequest saveToDbRequest = new StringRequest(Request.Method.POST, VERIFY_TOKEN_SAVE_TO_DB,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Toast.makeText(UpgradeActivity.this, " savED to db", Toast.LENGTH_LONG).show();


                        if (response.substring(0, 1).equalsIgnoreCase("{")) {

                            //if the response is valid json as opposed to fatal errors

                            handlePurchaseJsonResponseFromServer(response);


                        } else {
                            Toast.makeText(UpgradeActivity.this, " Something went wrong", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(UpgradeActivity.this, "Error saving to db", Toast.LENGTH_LONG).show();

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
            int paymentState = purchaseObject.getInt("paymentState");


            skusAndBillingThings.setSubscriptionExpiryDateAndPaymentState(expiryDate, paymentState);

            //time in milliseconds: long timeInMillis = System.currentTimeMillis();
            //     long timeInMillis = System.currentTimeMillis();
            progressBar.setVisibility(View.GONE);
            startActivity(new Intent(UpgradeActivity.this, MainActivity.class));


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
