package hu.bitnet.civilparking.Fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import hu.bitnet.civilparking.ListAdapter;
import hu.bitnet.civilparking.MainActivity;
import hu.bitnet.civilparking.MapsContainer;
import hu.bitnet.civilparking.Objects.Constants;
import hu.bitnet.civilparking.R;
import hu.bitnet.civilparking.RequestInterfaces.RequestInterfaceParkingInfo;
import hu.bitnet.civilparking.ServerResponses.ServerResponse;
import hu.bitnet.civilparking.Objects.ParkingListObject;
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


    public ParkingList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parkingList = inflater.inflate(R.layout.fragment_parkinglist, container, false);
        pref = getActivity().getPreferences(0);
        loadJSON(pref.getString("sessionId", null));

        parkingListView = (ListView)parkingList.findViewById(R.id.parkinglist);

        parkingListAdapter = new ArrayAdapter<ParkingListObject>(getContext(), android.R.layout.simple_list_item_1, new LinkedList<ParkingListObject>());
        parkingListView.setAdapter(parkingListAdapter);

        return parkingList;
    }

    public void loadJSON(String sessionId){

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
                                parkingListAdapter.getItem(position);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("longitude", parkingListAdapter.getItem(position).getLongitude());
                                editor.putString("latitude", parkingListAdapter.getItem(position).getLatitude());
                                editor.putString("name", parkingListAdapter.getItem(position).getName());
                                editor.putString("id", parkingListAdapter.getItem(position).getId());
                                editor.apply();
                                MapsContainer mapsContainer= new MapsContainer();
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.frame, mapsContainer, mapsContainer.getTag())
                                        .addToBackStack("Home")
                                        .commit();
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

}
