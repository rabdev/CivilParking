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
public class Hybrid extends Fragment {


    public Hybrid() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View hybrid = inflater.inflate(R.layout.fragment_hybrid, container, false);
        return hybrid;
    }

}
