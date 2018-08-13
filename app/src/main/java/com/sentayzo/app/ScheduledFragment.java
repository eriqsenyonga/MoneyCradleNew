package com.sentayzo.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class ScheduledFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match


    ViewPager mScheduledViewPager;
    View rootView;
    ScheduledPagerAdapter mScheduledPagerAdapter;
    TabLayout tabLayout;
    Tracker t;

    SharedPreferences billingPrefs;


    public static ScheduledFragment newInstance() {
        // Required empty public constructor
        return new ScheduledFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_scheduled, container,
                false);

        mScheduledViewPager = (ViewPager) rootView
                .findViewById(R.id.scheduled_pagerz);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        tabLayout.setSelectedTabIndicatorHeight(15);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        t = ((ApplicationClass) getActivity().getApplication()).getTracker(ApplicationClass.TrackerName.APP_TRACKER);

        t.setScreenName("ScheduledFragment");
        t.send(new HitBuilders.ScreenViewBuilder().build());

        mScheduledPagerAdapter = new ScheduledPagerAdapter(
                getChildFragmentManager(), getActivity());

        mScheduledViewPager.setAdapter(mScheduledPagerAdapter);

        tabLayout.setupWithViewPager(mScheduledViewPager);

        billingPrefs = getActivity()
                .getSharedPreferences("my_billing_prefs", 0);


    }


}
