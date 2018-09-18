package com.sentayzo.app;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.sentayzo.app.SkusAndBillingThings;

public class UpgradeActivity extends AppCompatActivity implements View.OnClickListener, PurchasesUpdatedListener {

    ImageView ivPlatinum;
    ListView lvPremiumBenefits;
    CardView bRemoveAds, bMonthly, bQuarterly, bYearly;
    Toolbar toolBar;
    TextView tvRemoveAds;
    private BillingClient mBillingClient;
    SharedPreferences billingPrefs;
    SharedPreferences.Editor editor;
    SkusAndBillingThings skusAndBillingThings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_upgrade);


        bRemoveAds = (CardView) findViewById(R.id.b_remove_ads);
        ivPlatinum = (ImageView) findViewById(R.id.iv_platinum);
        lvPremiumBenefits = (ListView) findViewById(R.id.lv_premium_benefits);
        bMonthly = (CardView) findViewById(R.id.b_monthly);
        bQuarterly = (CardView) findViewById(R.id.b_quarterly);
        bYearly = (CardView) findViewById(R.id.b_yearly);
        tvRemoveAds = findViewById(R.id.tv_price_remove_ads);

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


        // create new Person


        mBillingClient = BillingClient.newBuilder(this).setListener(this).build();


        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    // The billing client is ready. You can query purchases here.
                    Toast.makeText(UpgradeActivity.this, "The billing client is ready. You can query purchases here.", Toast.LENGTH_LONG).show();

                    querySkuDetails();

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
                        Toast.makeText(UpgradeActivity.this, "Query details", Toast.LENGTH_LONG).show();

                        if (responseCode == BillingClient.BillingResponse.OK
                                && skuDetailsList != null) {

                            Toast.makeText(UpgradeActivity.this, "Query details 2", Toast.LENGTH_LONG).show();

                            for (SkuDetails skuDetails : skuDetailsList) {

                                Toast.makeText(UpgradeActivity.this, "Query details 3", Toast.LENGTH_LONG).show();

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
            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setSku(SkusAndBillingThings.SKU_PREMIUM_MONTHLY)
                    .setType(BillingClient.SkuType.SUBS) // SkuType.SUB for subscription
                    .build();

            int responseCode = mBillingClient.launchBillingFlow(UpgradeActivity.this, flowParams);
        }

        if (v == bQuarterly) {
            show("Quarterly Subscription");
            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setSku(SkusAndBillingThings.SKU_PREMIUM_QUARTERLY)
                    .setType(BillingClient.SkuType.SUBS) // SkuType.SUB for subscription
                    .build();

            int responseCode = mBillingClient.launchBillingFlow(UpgradeActivity.this, flowParams);
        }

        if (v == bYearly) {
            show("Yearly Subscription");
            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setSku(SkusAndBillingThings.SKU_PREMIUM_YEARLY)
                    .setType(BillingClient.SkuType.SUBS) // SkuType.SUB for subscription
                    .build();


            int responseCode = mBillingClient.launchBillingFlow(UpgradeActivity.this, flowParams);
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
            skusAndBillingThings.setPremiumPurchaseToken(purchase.getPurchaseToken());





        }

        if (purchase.getSku().equals(SkusAndBillingThings.SKU_PREMIUM_QUARTERLY)) {
            //if quarterly subscription, open content for the quarter and set recurring date to 3 month from now

            skusAndBillingThings.setPremiumPurchased(true);
            skusAndBillingThings.setPremiumPurchaseToken(purchase.getPurchaseToken());
        }

        if (purchase.getSku().equals(SkusAndBillingThings.SKU_PREMIUM_YEARLY)) {
            //if yearly subscription, open content for the year and set recurring date to 1 YEAR from now
            skusAndBillingThings.setPremiumPurchased(true);
            skusAndBillingThings.setPremiumPurchaseToken(purchase.getPurchaseToken());

        }

        if (purchase.getSku().equals(SkusAndBillingThings.SKU_REMOVE_ADS)) {


            skusAndBillingThings.setPurchasedAds(true);//            purchase.get


        }


    }
}
