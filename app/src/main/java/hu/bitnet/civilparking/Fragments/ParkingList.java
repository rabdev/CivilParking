package hu.bitnet.civilparking.Fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hu.bitnet.civilparking.ListAdapter;
import hu.bitnet.civilparking.MainActivity;
import hu.bitnet.civilparking.MapsContainer;
import hu.bitnet.civilparking.Objects.Constants;
import hu.bitnet.civilparking.Objects.ParkingListObject;
import hu.bitnet.civilparking.R;
import hu.bitnet.civilparking.RequestInterfaces.RequestInterfaceParkingInfo;
import hu.bitnet.civilparking.RequestInterfaces.RequestInterfaceParkingStop;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ParkingList extends Fragment {

    ListView parkingListView;
    private ArrayAdapter<ParkingListObject> parkingListAdapter;
    private ArrayList<ParkingListObject> parkingList;
    SharedPreferences pref;
    EditText licenceplate;
    AlertDialog alert_dialog;


    public ParkingList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parkingList = inflater.inflate(R.layout.fragment_parkinglist, container, false);
        pref = getActivity().getPreferences(0);

        TextView title = (TextView)((MainActivity)getActivity()).findViewById(R.id.title);
        title.setText("Elérhető parkolóhelyek");

        ImageButton settings = (ImageButton)((MainActivity)getActivity()).findViewById(R.id.settings);
        settings.setVisibility(View.VISIBLE);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout logout = new Logout();
                FragmentTransaction main = getActivity().getSupportFragmentManager().beginTransaction();
                main.setCustomAnimations(R.anim.from_middle, R.anim.to_middle, R.anim.from_middle, R.anim.to_middle);
                main.replace(R.id.frame, new Logout()).addToBackStack(null);
                main.commit();
                /*FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame, logout, "Logout")
                        .addToBackStack(null)
                        .commit();*/
            }
        });

        licenceplate = (EditText)parkingList.findViewById(R.id.licenceplate);
        if(pref.getString(Constants.LicensePlate, null) != null){
            licenceplate.setText(pref.getString(Constants.LicensePlate, null));
        }
        loadJSON(pref.getString("sessionId", null));

        parkingListView = (ListView)parkingList.findViewById(R.id.parkinglist);

        parkingListAdapter = new ArrayAdapter<ParkingListObject>(getContext(), android.R.layout.simple_list_item_1, new LinkedList<ParkingListObject>());
        parkingListView.setAdapter(parkingListAdapter);

        return parkingList;
    }

    public void loadJSON(final String sessionId){

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .baseUrl(Constants.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterfaceParkingInfo requestInterface = retrofit.create(RequestInterfaceParkingInfo.class);
        Call<ServerResponse> response= requestInterface.post(sessionId);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if(resp.getAlert() != ""){
                    Toast.makeText(getContext(), resp.getAlert(), Toast.LENGTH_LONG).show();
                }
                if(resp.getError() != null){
                    Toast.makeText(getContext(), resp.getError().getMessage()+" - "+resp.getError().getMessageDetail(), Toast.LENGTH_SHORT).show();
                    pref = getActivity().getPreferences(0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(Constants.IS_LOGGED_IN,false);
                    editor.apply();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                if(resp.getParkingList() != null){
                    if(resp.getParkingList().length == 0){
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                        alertDialog.setTitle("Nincs eredmény!");
                        alertDialog.setMessage("Próbálkozzon később!");
                        //alertDialog.setIcon(R.drawable.ic_parking);

                        alertDialog.setPositiveButton("Rendben", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                /*Home home1 = new Home();
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.frame, home1, "Home")
                                        .addToBackStack(null)
                                        .commit();*/
                            }
                        });

                        alertDialog.setNegativeButton("Mégsem", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                /*Home home1 = new Home();
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.frame, home1, "Home")
                                        .addToBackStack(null)
                                        .commit();*/
                            }
                        });

                        alertDialog.show();
                    }else{
                        parkingList = new ArrayList<ParkingListObject>(Arrays.asList(resp.getParkingList()));
                        parkingListAdapter.addAll(resp.getParkingList());
                        parkingListAdapter = new ListAdapter(getContext(), parkingList);
                        parkingListView.setAdapter(parkingListAdapter);

                        parkingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Pattern p = Pattern.compile("^[a-zA-Z]{3}[-]{1}[0-9]{3}$");
                                Matcher m = p.matcher(licenceplate.getText());
                                if(m.matches()) {
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString(Constants.LicensePlate, licenceplate.getText().toString());
                                    editor.apply();
                                    parkingListAdapter.getItem(position);
                                    SharedPreferences.Editor editor2 = pref.edit();
                                    editor2.putString("longitude", parkingListAdapter.getItem(position).getLongitude());
                                    editor2.putString("latitude", parkingListAdapter.getItem(position).getLatitude());
                                    editor2.putString("name", parkingListAdapter.getItem(position).getName());
                                    editor2.putString("id", parkingListAdapter.getItem(position).getId());
                                    editor2.putString("description", parkingListAdapter.getItem(position).getDescription());
                                    editor2.putString("licenceplate", licenceplate.getText().toString());
                                    editor2.apply();
                                    loadJSONCheck(sessionId, "4", licenceplate.getText().toString());
                                    MapsContainer mapsContainer= new MapsContainer();
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    fragmentManager.beginTransaction()
                                            .replace(R.id.frame, mapsContainer, mapsContainer.getTag())
                                            .addToBackStack("Home")
                                            .commit();
                                }else {
                                    showDialog();
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Hiba a hálózati kapcsolatban. Kérjük, ellenőrizze, hogy csatlakozik-e hálózathoz.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "No response");
            }
        });

    }

    public void loadJSONCheck(final String sessionId, final String id, String licenceplate){

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
                    loadJSONStop(sessionId, id);
                }
                if(resp.getError() != null){
                    //Toast.makeText(getContext(), resp.getError().getMessage()+" - "+resp.getError().getMessageDetail(), Toast.LENGTH_SHORT).show();
                    if(resp.getError().getMessage().indexOf("Már van függőben") > -1){
                        showDialog2();
                    }else {
                        //do nothing
                    }
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

    public void loadJSONStop(String sessionId, String id){

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .baseUrl(Constants.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterfaceParkingStop requestInterface = retrofit.create(RequestInterfaceParkingStop.class);
        Call<ServerResponse> response= requestInterface.post(sessionId, id);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                /*if(resp.getAlert() != ""){
                    Toast.makeText(getContext(), resp.getAlert(), Toast.LENGTH_LONG).show();
                    ParkingList parkingList= new ParkingList();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame, parkingList, parkingList.getTag())
                            .addToBackStack(null)
                            .commit();
                }*/
                if(resp.getError() != null){
                    if(resp.getError().getMessage().indexOf("A parkolás sikeresen lezárult") > -1) {
                        //Toast.makeText(getContext(), resp.getError().getMessage() + " - " + resp.getError().getMessageDetail(), Toast.LENGTH_SHORT).show();
                    }
                    /*pref = getActivity().getPreferences(0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(Constants.IS_LOGGED_IN,false);
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
            builder.setTitle("Hibás rendszám");
            builder.setMessage("A rendszám formátuma nem megfelelő")
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

    private void showDialog2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Parkolás folyamatban");
        builder.setMessage("Amíg van folyamatban parkolása, nem indíthat újat.")
                .setPositiveButton("Leállítom", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        loadJSONStop(pref.getString("sessionId", null), "1");
                        loadJSONStop(pref.getString("sessionId", null), "2");
                        loadJSONStop(pref.getString("sessionId", null), "3");
                        loadJSONStop(pref.getString("sessionId", null), "4");
                        loadJSONStop(pref.getString("sessionId", null), "5");
                    }
                })
                .setNegativeButton("Rendben", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        alert_dialog = builder.create();
        alert_dialog.show();
    }

}
