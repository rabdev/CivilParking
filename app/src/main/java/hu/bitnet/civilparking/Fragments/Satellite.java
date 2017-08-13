package hu.bitnet.civilparking.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hu.bitnet.civilparking.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Satellite extends Fragment {


    public Satellite() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View sat = inflater.inflate(R.layout.fragment_satellite, container, false);
        return sat;
    }

}
