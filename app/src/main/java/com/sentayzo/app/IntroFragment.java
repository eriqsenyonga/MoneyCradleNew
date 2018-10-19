package com.sentayzo.app;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;


public class IntroFragment extends Fragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_DRAWABLE = "drawable";
    private static final String ARG_BACKGROUND_COLOR = "color";

    View v;
    TextView tvTitle, tvDescription;
    ImageView ivIntroImage;
    LinearLayout linearLayout;

    private String title;
    private String description;
    private int mDrawable;
    private int backgroundColor;


    public IntroFragment() {
        // Required empty public constructor
    }

    public static IntroFragment newInstance(String mTitle, String mDescription, int mDrawable, int mBgColor) {
        IntroFragment fragment = new IntroFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, mTitle);
        args.putString(ARG_DESCRIPTION, mDescription);
        args.putInt(ARG_DRAWABLE, mDrawable);
        args.putInt(ARG_BACKGROUND_COLOR, mBgColor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            description = getArguments().getString(ARG_DESCRIPTION);
            mDrawable = getArguments().getInt(ARG_DRAWABLE);
            backgroundColor = getArguments().getInt(ARG_BACKGROUND_COLOR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_intro, container, false);
        tvDescription = (TextView) v.findViewById(R.id.tv_description);
        tvTitle = (TextView) v.findViewById(R.id.tv_title);
        ivIntroImage = v.findViewById(R.id.iv_intro_image);
        linearLayout = (LinearLayout) v.findViewById(R.id.linlay_intro);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tvTitle.setText(title);
        tvDescription.setText(description);
        ivIntroImage.setImageResource(mDrawable);
        linearLayout.setBackgroundColor(getResources().getColor(backgroundColor));


    }
}
