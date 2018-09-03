package com.sentayzo.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class UpgradeActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView ivPlatinum;
    ListView lvPremiumBenefits;
    CardView bRemoveAds, bMonthly, bQuarterly, bYearly;
    Toolbar toolBar;

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

        toolBar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("Upgrade");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String[] premiumArray = getResources().getStringArray(R.array.premium_benefits);
        List<String> testList = Arrays.asList(premiumArray);

        // Instanciating Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(UpgradeActivity.this,
                R.layout.item_row_premium, R.id.tv_row, testList);

        // setting adapter on listview
        lvPremiumBenefits.setAdapter(adapter);


        bRemoveAds.setOnClickListener(this);
        bYearly.setOnClickListener(this);
        bMonthly.setOnClickListener(this);
        bQuarterly.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == bRemoveAds) {
            show("Remove Ads");
        }

        if (v == bMonthly) {
            show("Monthly Subscription");
        }

        if (v == bQuarterly) {
            show("Quarterly Subscription");
        }

        if (v == bYearly) {
            show("Yearly Subscription");
        }
    }

    private void show(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
