package com.sentayzo.app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolBar;
    ImageButton ibPrev, ibNext, ibPeriod;
    TextView tvCurrentPeriod, tvFrom, tvTo;
    PopupMenu popupMenu;
    ViewPager viewPager;
    TabLayout tabLayout;
    StatPagerAdapter adapter;
    public static int PERIOD_MONTH = 0;
    public static int PERIOD_YEAR = 1;
    public static int PERIOD_CUSTOM = 2;
    int currentPeriodType;

    String fromDate ="", toDate = "";

    LinearLayout linlayYearMonth, linlayCustom, linlayFrom, linlayTo;

    int whichOverview;
    Long idOfEntity;
    boolean singleEntity = false;

    Calendar c;
    ConversionClass mCC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);


        toolBar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DbClass mDb = new DbClass(this);


        ibPrev = (ImageButton) findViewById(R.id.b_previous);
        ibNext = (ImageButton) findViewById(R.id.b_next);
        ibPeriod = (ImageButton) findViewById(R.id.b_period);
        tvCurrentPeriod = (TextView) findViewById(R.id.tv_current_period);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        linlayCustom = findViewById(R.id.linlay_custom);
        linlayYearMonth = findViewById(R.id.linlay_year_month);
        linlayFrom = findViewById(R.id.linlay_from);
        linlayTo = findViewById(R.id.linlay_to);
        tvFrom = findViewById(R.id.tv_from_date);
        tvTo = findViewById(R.id.tv_to_date);

        currentPeriodType = PERIOD_MONTH;


        if (getIntent().hasExtra("singleEntity")) {

            singleEntity = getIntent().getBooleanExtra("singleEntity", false);
            whichOverview = getIntent().getIntExtra("whichOverview", OverviewActivity.KEY_ACCOUNT_OVERVIEW);
            idOfEntity = getIntent().getLongExtra("Id", 1);

            getSupportActionBar().setTitle(getString(R.string.stats) + ": " + mDb.getTitle(idOfEntity, whichOverview));

        }

        c = Calendar.getInstance();
        mCC = new ConversionClass(this);

        tvCurrentPeriod.setText(mCC.dateForStatDisplayFromCalendarInstance(c.getTime()));


        if (singleEntity == true) {

            adapter = new StatPagerAdapter(getSupportFragmentManager(), this, whichOverview, true, idOfEntity);
        } else {

            adapter = new StatPagerAdapter(getSupportFragmentManager(), this);
        }
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        viewPager.setOffscreenPageLimit(2);


        ibPrev.setOnClickListener(this);
        ibNext.setOnClickListener(this);
        ibPeriod.setOnClickListener(this);
        tvCurrentPeriod.setOnClickListener(this);
        linlayTo.setOnClickListener(this);
        linlayFrom.setOnClickListener(this);

        setUpPopupMenu();

    }

    private void setUpPopupMenu() {

        popupMenu = new PopupMenu(this, ibPeriod);

        popupMenu.getMenuInflater().inflate(R.menu.period_filter_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.menu_month) {

                    //TODO ADD WHAT HAPPENS ON MONTH
                    Toast.makeText(StatisticsActivity.this, item.getTitle(), Toast.LENGTH_LONG).show();
                    showCustom(false);
                    currentPeriodType = PERIOD_MONTH;
                    tvCurrentPeriod.setText(mCC.dateForStatDisplayFromCalendarInstance(c.getTime()));
                    updateFragmentsInViewPager();

                    return true;

                } else if (itemId == R.id.menu_year) {

                    //TODO ADD WHAT HAPPENS ON YEAR
                    Toast.makeText(StatisticsActivity.this, item.getTitle(), Toast.LENGTH_LONG).show();
                    showCustom(false);
                    currentPeriodType = PERIOD_YEAR;
                    tvCurrentPeriod.setText(mCC.dateYearForStatDisplayFromCalendarInstance(c.getTime()));
                    updateFragmentsInViewPager();
                    return true;

                } else if (itemId == R.id.menu_custom) {

                    //TODO ADD WHAT HAPPENS ON CUSTOM
                    currentPeriodType = PERIOD_CUSTOM;
                    c.set(Calendar.DAY_OF_MONTH, 1);
                    tvFrom.setText(mCC.dateForDisplayFromCalendarInstance(c.getTime()));
                    c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
                    tvTo.setText(mCC.dateForDisplayFromCalendarInstance(c.getTime()));

                    showCustom(true);

                    fromDate = mCC.dateForDb(tvFrom.getText().toString());
                    toDate = mCC.dateForDb(tvTo.getText().toString());

                    updateFragmentsInViewPager();
                    //next is to updateFragments in ViewPager
                    return true;

                }


                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == android.R.id.home) {

            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        if(v == linlayFrom){

            try {
                DatePickerDialog datePicker = new DatePickerDialog(
                       StatisticsActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view,
                                          int selectedYear, int selectedMonth,
                                          int selectedDay) {
                        // TODO Auto-generated method stub

                        String dateSetString = selectedYear + "-"
                                + (selectedMonth + 1) + "-"
                                + selectedDay;

                        SimpleDateFormat sdf = new SimpleDateFormat(
                                "yyyy-MM-dd", Locale.getDefault());

                        SimpleDateFormat sdf2 = new SimpleDateFormat(
                                "dd - MMM - yyyy", Locale.getDefault());

                        try {
                            tvFrom.setText(sdf2.format(sdf
                                    .parse(dateSetString)));

                            fromDate = mCC.dateForDb(tvFrom.getText().toString());
                            toDate = mCC.dateForDb(tvTo.getText().toString());
                            updateFragmentsInViewPager();

                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Log.d("datePicker", e.toString());
                        }

                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                        .get(Calendar.DAY_OF_MONTH));

                datePicker.setTitle("Select Date");
                datePicker.setCancelable(false);
                datePicker.show();
            } catch (Exception e) {
                e.getStackTrace();
                Log.d("Exception", e.toString());
            }



        }

        if(v == linlayTo){

            try {
                DatePickerDialog datePicker = new DatePickerDialog(
                        StatisticsActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view,
                                          int selectedYear, int selectedMonth,
                                          int selectedDay) {
                        // TODO Auto-generated method stub

                        String dateSetString = selectedYear + "-"
                                + (selectedMonth + 1) + "-"
                                + selectedDay;

                        SimpleDateFormat sdf = new SimpleDateFormat(
                                "yyyy-MM-dd", Locale.getDefault());

                        SimpleDateFormat sdf2 = new SimpleDateFormat(
                                "dd - MMM - yyyy", Locale.getDefault());

                        try {
                            tvTo.setText(sdf2.format(sdf
                                    .parse(dateSetString)));

                            fromDate = mCC.dateForDb(tvFrom.getText().toString());
                            toDate = mCC.dateForDb(tvTo.getText().toString());
                            updateFragmentsInViewPager();

                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Log.d("datePicker", e.toString());
                        }

                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                        .get(Calendar.DAY_OF_MONTH));

                datePicker.setTitle("Select Date");
                datePicker.setCancelable(false);
                datePicker.show();
            } catch (Exception e) {
                e.getStackTrace();
                Log.d("Exception", e.toString());
            }




        }


        if (v == ibPeriod || v == tvCurrentPeriod) {

            popupMenu.show();

        }

        if (v == ibNext) {

            //add one to display stat date eg if month,year  add one month to the month

            if (currentPeriodType == PERIOD_MONTH) {


                String newStatDisplayDate = mCC.statAddOneMonth(tvCurrentPeriod.getText().toString());

                tvCurrentPeriod.setText(newStatDisplayDate);


            } else if (currentPeriodType == PERIOD_YEAR) {

                String newStatDisplayDate = mCC.statAddOneYear(tvCurrentPeriod.getText().toString());

                tvCurrentPeriod.setText(newStatDisplayDate);
            }


            updateFragmentsInViewPager();


        }

        if (v == ibPrev) {

            //add one to display stat date eg if month,year  add one month to the month

            if (currentPeriodType == PERIOD_MONTH) {


                String newStatDisplayDate = mCC.statSubtractOneMonth(tvCurrentPeriod.getText().toString());

                tvCurrentPeriod.setText(newStatDisplayDate);

                //  StatCategoryFragment page1 = (StatCategoryFragment) adapter.getItem(0);
                //  StatCategoryFragment page2 = (StatCategoryFragment) adapter.getItem(1);


            } else if (currentPeriodType == PERIOD_YEAR) {

                String newStatDisplayDate = mCC.statSubtractOneYear(tvCurrentPeriod.getText().toString());

                tvCurrentPeriod.setText(newStatDisplayDate);
            }

            updateFragmentsInViewPager();


        }


    }


    public void updateFragmentsInViewPager() {

        StatCategoryFragment page1 = (StatCategoryFragment) getSupportFragmentManager().getFragments().get(0);
        StatCategoryFragment page2 = (StatCategoryFragment) getSupportFragmentManager().getFragments().get(1);
        IncExpenseFragment page3 = (IncExpenseFragment) getSupportFragmentManager().getFragments().get(2);

        page1.periodChanged(currentPeriodType, tvCurrentPeriod.getText().toString(), fromDate, toDate);
        page2.periodChanged(currentPeriodType, tvCurrentPeriod.getText().toString(), fromDate, toDate);
        page3.periodChanged(currentPeriodType, tvCurrentPeriod.getText().toString(), fromDate, toDate);


    }

    public void showCustom(boolean show) {

        if (show) {
            //if show is true

            linlayYearMonth.setVisibility(View.GONE);
            linlayCustom.setVisibility(View.VISIBLE);

        } else {

            linlayYearMonth.setVisibility(View.VISIBLE);
            linlayCustom.setVisibility(View.GONE);


        }


    }
}
