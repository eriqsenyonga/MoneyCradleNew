package com.sentayzo.app;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {


    View root;
    FrameLayout flAddress;
    FloatingActionsMenu fam;
    FloatingActionButton fab;

    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_about, container, false);

        flAddress = (FrameLayout) root.findViewById(R.id.fl_address);
        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        fam = (FloatingActionsMenu) getActivity().findViewById(R.id.fam_fab);
        fam.setVisibility(View.GONE);

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        String versionName = BuildConfig.VERSION_NAME;

        Element versionElement = new Element();
        versionElement.setTitle("Version " + versionName);

      /*  Element callElement = new Element();
        callElement.setTitle("Call Us");
        callElement.setValue("0703289142");
        callElement.setIconDrawable(R.drawable.ic_wallet_logo_png);
        callElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:0703289142"));
                startActivity(intent);
            }
        });*/


        View aboutPage = new AboutPage(getActivity())
                .isRTL(false).setDescription(getString(R.string.about_money_cradle_new))
                .setImage(R.drawable.ic_wallet_logo_png)
                .addPlayStore("com.sentayzo.app")
                .addItem(versionElement)
                //      .addItem(adsElement)
                .addGroup("Connect with us")
                .addEmail("MoneyCradle@plexosys-consult.com")
                .addWebsite("http://www.plexosys-consult.com")
                //   .addItem(callElement)
                .addFacebook("MoneyCradle")

                .create();

        flAddress.addView(aboutPage);


    }

}
