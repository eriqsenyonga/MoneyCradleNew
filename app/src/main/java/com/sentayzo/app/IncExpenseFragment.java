package com.sentayzo.app;


import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class IncExpenseFragment extends Fragment {


    PieChart pieChart;
    TextView tvIncome, tvExpense, tvBalance, tvIncomeLabel, tvExpenseLabel, tvBalanceLabel;
    View root;


    ConversionClass mCC;


    int whichStatCategory; //expense or income
    int periodType;
    String fromDate;
    String toDate;
    String specificPeriod; //specific period contains date of format MM-yyyy... we extract these in the db
    DbClass mDb;
    Cursor cursor;
    Calendar c;
    Typeface robotoThin, robotoMedium;

    Long idOfEntity;
    int whichOverview;
    boolean isSingleEntity = false;


    public IncExpenseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        whichStatCategory = this.getArguments().getInt("which", 0);
        periodType = StatisticsActivity.PERIOD_MONTH;

        if (this.getArguments().getBoolean("isSingleEntity", false)) {

            isSingleEntity = true;
            idOfEntity = this.getArguments().getLong("idOfEntity");
            whichOverview = this.getArguments().getInt("whichOverview");

        }


        root = inflater.inflate(R.layout.fragment_inc_expense, container, false);
        pieChart = (PieChart) root.findViewById(R.id.piechart);
        tvIncome = (TextView) root.findViewById(R.id.tv_amount_income);
        tvExpense = (TextView) root.findViewById(R.id.tv_amount_expense);
        tvBalance = (TextView) root.findViewById(R.id.tv_amount_balance);
        tvIncomeLabel = (TextView) root.findViewById(R.id.tv_income);
        tvExpenseLabel = (TextView) root.findViewById(R.id.tv_expense);
        tvBalanceLabel = (TextView) root.findViewById(R.id.tv_balance);


        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCC = new ConversionClass(getActivity());

        c = Calendar.getInstance();

        // specificPeriod = new String();

        specificPeriod = mCC.dateForStatistics(c.getTime());

        mDb = new DbClass(getActivity());

        robotoThin = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf");
        robotoMedium = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Medium.ttf");

        setUpTextViews();
        setUpPieChart();

        setData();

    }

    private void setUpTextViews() {

        if (Build.VERSION.SDK_INT < 23) {
            //if api below 23

            tvIncomeLabel.setTypeface(robotoMedium);
            tvIncomeLabel.setTextAppearance(getActivity(), R.style.boldText);
            tvExpenseLabel.setTypeface(robotoMedium);
            tvExpenseLabel.setTextAppearance(getActivity(), R.style.boldText);
            tvBalanceLabel.setTypeface(robotoMedium);
            tvBalanceLabel.setTextAppearance(getActivity(), R.style.boldText);
            tvIncome.setTypeface(robotoThin);
            tvIncome.setTextAppearance(getActivity(), R.style.normalText);
            tvExpense.setTypeface(robotoThin);
            tvExpense.setTextAppearance(getActivity(), R.style.normalText);
            tvBalance.setTypeface(robotoThin);
            tvBalance.setTextAppearance(getActivity(), R.style.boldText);

        } else {
            //if api above 23
            tvIncomeLabel.setTypeface(robotoMedium);
            tvIncomeLabel.setTextAppearance(R.style.boldText);
            tvExpenseLabel.setTypeface(robotoMedium);
            tvExpenseLabel.setTextAppearance(R.style.boldText);
            tvBalanceLabel.setTypeface(robotoMedium);
            tvBalanceLabel.setTextAppearance(R.style.boldText);
            tvIncome.setTypeface(robotoThin);
            tvIncome.setTextAppearance(R.style.normalText);
            tvExpense.setTypeface(robotoThin);
            tvExpense.setTextAppearance(R.style.normalText);
            tvBalance.setTypeface(robotoThin);
            tvBalance.setTextAppearance(R.style.boldText);

        }
    }

    private void setUpPieChart() {

        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawEntryLabels(false);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setTransparentCircleColor(Color.WHITE);


        pieChart.getDescription().setEnabled(false);

    }


    private void setData() {


        if (periodType == StatisticsActivity.PERIOD_MONTH || periodType == StatisticsActivity.PERIOD_YEAR) {

            if (isSingleEntity) {
                cursor = mDb.getIncomeVsExpenseTotals(periodType, specificPeriod, whichOverview, idOfEntity);

            } else {

                cursor = mDb.getIncomeVsExpenseTotals(periodType, specificPeriod);
            }

        } else if (periodType == StatisticsActivity.PERIOD_CUSTOM) {

            // TODO: 3/27/2017 what happens for custom period
            //   cursor = mDb.getIncomeCategoryTotals(fromDate, toDate);

        }


        updateChartAndTotals();


    }

    private void updateChartAndTotals() {

        cursor.moveToFirst();

        //textviews update
        tvIncome.setText(mCC.valueConverter(cursor.getLong(cursor.getColumnIndex("totIncome"))));
        tvExpense.setText(mCC.valueConverter(cursor.getLong(cursor.getColumnIndex("totExpense"))));
        tvBalance.setText(mCC.valueConverter(cursor.getLong(cursor.getColumnIndex("totBalance"))));


        //piechart update
        ArrayList<PieEntry> yEntries = new ArrayList<>();

        yEntries.add(
                new PieEntry(Math.abs(mCC.valueConverterReturnFloat((cursor.getLong(cursor.getColumnIndex("totExpense"))))),
                        "Expense"));

        yEntries.add(
                new PieEntry(Math.abs(mCC.valueConverterReturnFloat((cursor.getLong(cursor.getColumnIndex("totIncome"))))),
                        "Income"));

        PieDataSet dataSet = new PieDataSet(yEntries, getActivity().getResources().getString(R.string.income_vs_expenses));
        dataSet.setSliceSpace(2f);


        ArrayList<Integer> colors3 = new ArrayList<>();
        colors3.add(ContextCompat.getColor(getActivity(), R.color.graph_red));
        colors3.add(ContextCompat.getColor(getActivity(), R.color.graph_green));

        dataSet.setColors(colors3);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(11f);
        pieChart.setData(pieData);
        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        pieChart.highlightValues(null);
        pieChart.invalidate();


    }


    public void periodChanged(int periodType, String specificPeriod, String fromDate, String toDate) {

        // Log.d("period_type_sent", "" + periodType);
        //  Log.d("period_type_current", "" + this.periodType);


        this.periodType = periodType;

        if (this.periodType == StatisticsActivity.PERIOD_MONTH) {
            this.specificPeriod = mCC.dateForStatUseFromDisplay(specificPeriod);
        }

        if (this.periodType == StatisticsActivity.PERIOD_YEAR) {

            this.specificPeriod = specificPeriod;

        }


        if (periodType == StatisticsActivity.PERIOD_CUSTOM) {
            this.fromDate = fromDate;
            this.toDate = toDate;
        }

        setData();

    }
}
