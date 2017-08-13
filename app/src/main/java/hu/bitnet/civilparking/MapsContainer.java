package hu.bitnet.civilparking;


import android.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import hu.bitnet.civilparking.Fragments.GMap;
import hu.bitnet.civilparking.Fragments.Hybrid;
import hu.bitnet.civilparking.Fragments.MapDraw;
import hu.bitnet.civilparking.Fragments.Satellite;
import hu.bitnet.civilparking.Objects.Constants;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;
import static com.google.android.gms.maps.GoogleMap.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapsContainer extends Fragment{

    private ViewPager homeviewPager;
    private TabLayout tabLayout;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    GMap gMap;


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
                int position = tab.getPosition();
                if (position==0){
                    gMap.gmap.setMapType(MAP_TYPE_NORMAL);
                } else if (position == 1) {
                    gMap.gmap.setMapType(MAP_TYPE_TERRAIN);
                } else if (position==2){
                    gMap.gmap.setMapType(MAP_TYPE_SATELLITE);
                }

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
                    gMap = new GMap();
                    return gMap;
                case 1:
                    gMap = new GMap();
                    return gMap;
                case 2:
                    gMap = new GMap();
                    return gMap;
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
