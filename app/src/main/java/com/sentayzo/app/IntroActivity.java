package com.sentayzo.app;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntroFragment firstFragment = IntroFragment.newInstance("Peace of mind"
                                                                ,"Achieve financial peace of mind with Money Cradle"
                                                                ,R.drawable.svg_finpeace
                                                                , R.color.primary);


        IntroFragment secondFragment = IntroFragment.newInstance("Organize"
                ,"Organize your accounts, expenses and income"
                ,R.drawable.svg_orrganise
                , R.color.primary_light);

        IntroFragment thirdFragment = IntroFragment.newInstance("Insights & Reports"
                ,"View meaningful insights about your finances with our reports and statistics"
                ,R.drawable.svg_insights
                , R.color.primary_dark);


        IntroFragment fourthFragment = IntroFragment.newInstance("Learn & Grow"
                ,"Improve your financial intelligence with Money Cradle Success Academy"
                ,R.drawable.svg_finintel
                , R.color.blue);


        addSlide(firstFragment);
        addSlide(secondFragment);
        addSlide(thirdFragment);
        addSlide(fourthFragment);

/*
        SliderPage sliderPage4 = new SliderPage();
        sliderPage4.setTitle("Peace of Mind");
        sliderPage4.setDescription("Achieve financial peace of mind with Money Cradle");
        sliderPage4.setImageDrawable(R.drawable.svg_finpeace);
        sliderPage4.setBgColor(getResources().getColor(R.color.primary));
       // addSlide(AppIntroFragment.newInstance(sliderPage4));
        addSlide( AppIntro2Fragment.newInstance("Peace of Mind", "Achieve financial peace of mind with Money Cradle", R.drawable.svg_finpeace,getResources().getColor(R.color.primary) ));


        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle("Organize");
        sliderPage.setDescription("Organize your accounts, expenses and income");
        sliderPage.setImageDrawable(R.drawable.svg_orrganise);
        sliderPage.setBgColor(getResources().getColor(R.color.primary));
        addSlide(AppIntroFragment.newInstance(sliderPage));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("Insights & Reports");
        sliderPage2.setDescription("View meaningful insights about your finances with our reports and statistics");
        sliderPage2.setImageDrawable(R.drawable.svg_insights);
        sliderPage2.setBgColor(getResources().getColor(R.color.primary_dark));
        addSlide(AppIntroFragment.newInstance(sliderPage2));


        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle("Learn & Grow");
        sliderPage3.setDescription("Improve your financial intelligence with Money Cradle Success Academy");
        sliderPage3.setImageDrawable(R.drawable.svg_finintel);
        sliderPage3.setBgColor(getResources().getColor(R.color.accent));
        addSlide(AppIntroFragment.newInstance(sliderPage3));

*/



    }


    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        Intent i = new Intent(IntroActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        Intent i = new Intent(IntroActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }


}
