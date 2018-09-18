package com.sentayzo.app;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.analytics.Tracker;

import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class FinancialStatementFragment extends Fragment {


    TextView tvTotInc, tvTotExp, tvDefOrSup, tvDiff, tvConclusion, tvTotAssets,
            tvTotLiabilities, tvTotCash, tvTotBank, tvTotTotal, tvConclusion2, tvAsAt;

    String deficit;
    String surplus;
    String conclusionNegative;
    String conclusionPositive;
    String debtString;
    String noDebt;
    ConversionClass mCC;
    Toolbar toolBar;
    View v;

    public FinancialStatementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_financial_statement, container, false);
        tvTotInc = (TextView) v.findViewById(R.id.tv_totInc);
        tvTotExp = (TextView) v.findViewById(R.id.tv_totExp);
        tvDefOrSup = (TextView) v.findViewById(R.id.tv_DefOrSup);
        tvDiff = (TextView) v.findViewById(R.id.tv_diff);
        tvConclusion = (TextView) v.findViewById(R.id.tv_conclusion);
        tvTotAssets = (TextView) v.findViewById(R.id.tv_totAssets);
        tvTotLiabilities = (TextView) v.findViewById(R.id.tv_totLiabilities);
        tvTotCash = (TextView) v.findViewById(R.id.tv_totCash);
        tvTotBank = (TextView) v.findViewById(R.id.tv_totBank);
        tvTotTotal = (TextView) v.findViewById(R.id.tv_totTotal);
        tvConclusion2 = (TextView) v.findViewById(R.id.tv_conclusion2);
        toolBar = (Toolbar) v.findViewById(R.id.app_bar);
        tvAsAt = (TextView) v.findViewById(R.id.tv_as_at);


        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCC = new ConversionClass(getActivity());

        conclusionNegative = getResources().getString(
                R.string.conclusion_negative);
        conclusionPositive = getResources().getString(
                R.string.conclusion_positive);
        surplus = getResources().getString(R.string.sup);
        deficit = getResources().getString(R.string.deficit);
        debtString = getResources().getString(R.string.debt_string);
        noDebt = getResources().getString(R.string.no_debt);

        toolBar.setVisibility(View.GONE);

        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        String asAtDate = mCC.dateForDisplayFromCalendarInstanceLong(currentDate);
        tvAsAt.setText("(" + getString(R.string.as_at) + " " + asAtDate + ")");


        Bundle bundle = getValues();

        tvTotInc.setText(mCC.valueConverter(bundle.getLong("totInc")));
        tvTotExp.setText(mCC.valueConverter(bundle.getLong("totExp")));

        if (bundle.getLong("totInc") >= bundle.getLong("totExp")) {
            tvDefOrSup.setText(surplus);
            tvDiff.setText(mCC.valueConverter(bundle.getLong("totInc")
                    - bundle.getLong("totExp")));
            tvConclusion.setText(conclusionPositive);
            tvConclusion.setTextColor(Color.rgb(49, 144, 4));

        } else {
            tvDefOrSup.setText(deficit);
            tvDiff.setText(mCC.valueConverter(bundle.getLong("totExp")
                    - bundle.getLong("totInc")));
            tvConclusion.setText(conclusionNegative);
            tvConclusion.setTextColor(Color.RED);

        }

        tvTotAssets.setText(mCC.valueConverter(bundle.getLong("totAssets")));
        tvTotLiabilities.setText(mCC.valueConverter(bundle
                .getLong("totLiabilities")));
        tvTotCash.setText(mCC.valueConverter(bundle.getLong("totCash")));
        tvTotBank.setText(mCC.valueConverter(bundle.getLong("totBank")));

        Long total = bundle.getLong("tot");

        tvTotTotal.setText(mCC.valueConverter(total));

        if (total >= 0) {
            tvConclusion2.setText(noDebt);
            tvConclusion2.setTextColor(Color.rgb(49, 144, 4));

        } else {
            tvConclusion2.setText(debtString);
            tvConclusion2.setTextColor(Color.RED);
        }


    }


    private Bundle getValues() {

        DbClass mDbClass = new DbClass(getActivity());
        mDbClass.open();
        Bundle bundle = mDbClass.getInfoForReport();
        mDbClass.close();
        return bundle;

    }
}
