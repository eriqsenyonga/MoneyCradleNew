package com.sentayzo.app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;

import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpgradeFragment extends Fragment {

    // TextView detailsNoAds;

    View rootView;
    FloatingActionsMenu fam;
    FloatingActionButton fab;
    ImageView  ivPlatinum;
    ListView lvPremiumBenefits;


    public UpgradeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_upgrade, container, false);

        //    detailsNoAds = (TextView) rootView.findViewById(R.id.tv_details_block_ads);


        ivPlatinum = (ImageView) rootView.findViewById(R.id.iv_platinum);

        lvPremiumBenefits = (ListView) rootView.findViewById(R.id.lv_premium_benefits);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fam = (FloatingActionsMenu) getActivity().findViewById(R.id.fam_fab);
        fam.setVisibility(View.GONE);

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);


        fab.setVisibility(View.GONE);

        //  detailsNoAds.setText(Html.fromHtml(getString(R.string.block_ads_details)));





        String[] premiumArray = getResources().getStringArray(R.array.premium_benefits);
        List<String> testList = Arrays.asList(premiumArray);

        // Instanciating Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.item_row_premium, R.id.tv_row, testList);

        // setting adapter on listview
        lvPremiumBenefits.setAdapter(adapter);


    }
}
