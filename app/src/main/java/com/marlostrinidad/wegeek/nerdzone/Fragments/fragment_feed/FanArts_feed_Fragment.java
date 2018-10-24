package com.marlostrinidad.wegeek.nerdzone.Fragments.fragment_feed;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marlostrinidad.wegeek.nerdzone.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class FanArts_feed_Fragment extends Fragment {


    public FanArts_feed_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fan_arts_feed_, container, false);
    }

}
