package hu.bitnet.civilparking.Fragments;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import hu.bitnet.civilparking.MainActivity;
import hu.bitnet.civilparking.Objects.Constants;
import hu.bitnet.civilparking.R;
import hu.bitnet.civilparking.RequestInterfaces.RequestInterfaceParkingstart;
import hu.bitnet.civilparking.ServerResponses.ServerResponse;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * A simple {@link Fragment} subclass.
 */
public class GMap extends Fragment implements LocationListener, OnMapReadyCallback, LocationSource.OnLocationChangedListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    public MapView mapView;
    public GoogleMap gmap;
    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    android.location.LocationListener locationlistener;
    LatLng latlng;
    Location location;
    Polyline polylin;
    double latitude, x;
    double longitude, y;
    public final static int MILLISECONDS_PER_SECOND = 1000;
    public final static int MINUTE = 60 * MILLISECONDS_PER_SECOND;
    SharedPreferences pref;
    public Marker marker;
    AlertDialog alert_dialog;

    public GMap() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View gmap = inflater.inflate(R.layout.fragment_gmap, container, false);

        mapView = (MapView) gmap.findViewById(R.id.gmapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .build();
        mGoogleApiClient.connect();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(MINUTE);
        mLocationRequest.setFastestInterval(15 * MILLISECONDS_PER_SECOND);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        Button parkStart = (Button)gmap.findViewById(R.id.parkStart);

        TextView title = (TextView)((MainActivity)getActivity()).findViewById(R.id.title);
        title.setText("Parkolás megkezdése");

        ImageButton settings = (ImageButton)((MainActivity)getActivity()).findViewById(R.id.settings);
        settings.setVisibility(View.INVISIBLE);

        parkStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref = getActivity().getPreferences(0);
                loadJSON(pref.getString("sessionId", null), pref.getString("id", null), pref.getString("licenceplate", null));
            }
        });

        return gmap;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
            }, 10);
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMyLocationEnabled(true);
        gmap.getUiSettings().setMyLocationButtonEnabled(true);
        gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gmap.setIndoorEnabled(true);
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

        locationlistener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location loc) {
                double latitude = loc.getLatitude();
                double longitude = loc.getLongitude();
                location = new Location("");
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                return;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String bestProvider) {
                LocationManager locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.INTERNET
                        }, 10);
                        return;
                    }
                    locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationlistener);
                } else {
                    locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationlistener);
                }
            }

            @Override
            public void onProviderDisabled(String s) {
                LocationManager locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    if (checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.INTERNET
                        }, 10);
                        return;
                    }
                    locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationlistener);
                } else {
                    locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationlistener);
                }
            }
        };
        location = locationManager.getLastKnownLocation(bestProvider);
        if (location == null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
                return;
            }
            locationManager.requestLocationUpdates(bestProvider, 0, 0, locationlistener);
            return;
        } else {
            double c = location.getLatitude();
            double d = location.getLongitude();
            pref = getActivity().getPreferences(0);
            c = Double.parseDouble(pref.getString("latitude", null));
            d = Double.parseDouble(pref.getString("longitude", null));
            LatLng myloc = new LatLng(c, d);
            gmap.animateCamera(CameraUpdateFactory.newLatLng(myloc));
            gmap.addMarker(new MarkerOptions().position(myloc).title(pref.getString("name", null)).snippet(pref.getString("description", null))).showInfoWindow();
            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(myloc, 18));

            /*PolylineOptions line=
                    new PolylineOptions().add(new LatLng(47.51678800,
                                    19.11402100),
                            new LatLng(47.51682200,
                                    19.11405000),
                            new LatLng(47.51677900,
                                    19.11399300))
                            .width(5).color(Color.RED);

            gmap.addPolyline(line);*/
        }

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    public void loadJSON(String sessionId, String id, String licenceplate){

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .baseUrl(Constants.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterfaceParkingstart requestInterface = retrofit.create(RequestInterfaceParkingstart.class);
        Call<ServerResponse> response= requestInterface.post(sessionId, id, licenceplate);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if(resp.getAlert() != ""){
                    //Toast.makeText(getContext(), resp.getAlert(), Toast.LENGTH_LONG).show();
                    showDialog3(resp.getAlert());
                    Parking parking= new Parking();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame, parking, parking.getTag())
                            .addToBackStack(null)
                            .commit();
                }
                if(resp.getError() != null){
                    //Toast.makeText(getContext(), resp.getError().getMessage()+" - "+resp.getError().getMessageDetail(), Toast.LENGTH_SHORT).show();
                    if(resp.getError().getMessage().indexOf("A megadott rendszám") > -1){
                        showDialog();
                    }else{
                        showDialog2(resp.getError().getMessage(), resp.getError().getMessageDetail());
                    }
                    pref = getActivity().getPreferences(0);
                    SharedPreferences.Editor editor = pref.edit();
                    /*editor.putBoolean(Constants.IS_LOGGED_IN,false);
                    editor.apply();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);*/
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Hiba a hálózati kapcsolatban. Kérjük, ellenőrizze, hogy csatlakozik-e hálózathoz.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "No response");
            }
        });

    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("A megadott rendszám nem használható!");
        builder.setMessage("A választott parkolón csak beállított rendszámmal lehet parkolást indítani!")
                /*.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })*/
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        alert_dialog = builder.create();
        alert_dialog.show();
    }

    private void showDialog2(String message, String detail) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(message);
        builder.setMessage(detail)
                /*.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })*/
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        alert_dialog = builder.create();
        alert_dialog.show();
    }

    private void showDialog3(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                /*.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })*/
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        alert_dialog = builder.create();
        alert_dialog.show();
    }
}
