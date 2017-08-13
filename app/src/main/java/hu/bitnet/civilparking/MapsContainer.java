package hu.bitnet.civilparking;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hu.bitnet.civilparking.Fragments.GMap;
import hu.bitnet.civilparking.Fragments.Hybrid;
import hu.bitnet.civilparking.Fragments.MapDraw;
import hu.bitnet.civilparking.Fragments.Satellite;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapsContainer extends Fragment {

    private ViewPager homeviewPager;
    private TabLayout tabLayout;
    private SectionsPagerAdapter mSectionsPagerAdapter;


    public MapsContainer() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View maps = inflater.inflate(R.layout.fragment_mapscontainer, container, false);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager());
        homeviewPager = (ViewPager) maps.findViewById(R.id.map_container);
        homeviewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) maps.findViewById(R.id.map_tab);
        tabLayout.setupWithViewPager(homeviewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        return maps;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    GMap gMap = new GMap();
                    return gMap;
                case 1:
                    Hybrid hybrid = new Hybrid();
                    return hybrid;
                case 2:
                    Satellite sat = new Satellite();
                    return sat;
                case 3:
                    MapDraw mapDraw = new MapDraw();
                    return mapDraw;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Térkép";
                case 1:
                    return "Hibrid";
                case 2:
                    return "Műhold";
                case 3:
                    return "Alaprajz";
            }
            return null;
        }
    }

}
