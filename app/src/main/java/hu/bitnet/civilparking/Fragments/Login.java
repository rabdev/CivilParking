package hu.bitnet.civilparking.Fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import hu.bitnet.civilparking.MainActivity;
import hu.bitnet.civilparking.Objects.Constants;
import hu.bitnet.civilparking.R;
import hu.bitnet.civilparking.RequestInterfaces.RequestInterfaceLogin;
import hu.bitnet.civilparking.RequestInterfaces.RequestInterfaceLoginError;
import hu.bitnet.civilparking.ServerResponses.ServerResponse;
import hu.bitnet.civilparking.ServerResponses.ServerResponseError;
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
public class Login extends Fragment {

    public AppCompatButton btn_login;
    public EditText et_email;
    public EditText et_password;
    SharedPreferences preferences;
    SharedPreferences pref;

    public Login() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View login = inflater.inflate(R.layout.fragment_login, container, false);
        AppCompatButton btn_login = (AppCompatButton)login.findViewById(R.id.btn_login);
        et_email = (EditText)login.findViewById(R.id.et_email);
        et_password = (EditText)login.findViewById(R.id.et_password);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();
                loadJSON(email, password);
            }
        });

        TextView title = (TextView)((MainActivity)getActivity()).findViewById(R.id.title);
        title.setText("Bejelentkezés");

        ImageButton settings = (ImageButton)((MainActivity)getActivity()).findViewById(R.id.settings);
        settings.setVisibility(View.INVISIBLE);

        TextView tv_register = (TextView) login.findViewById(R.id.tv_register);
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Registration registration = new Registration();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame, registration, registration.getTag())
                        .commit();
            }
        });
        return login;
    }

    public void loadJSON(final String email, final String password) {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .baseUrl(Constants.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterfaceLogin requestInterface = retrofit.create(RequestInterfaceLogin.class);
        Call<ServerResponse> response = requestInterface.post(email, password);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp.getAlert() != "") {
                    Toast.makeText(getContext(), resp.getAlert(), Toast.LENGTH_LONG).show();
                }
                if (resp.getError() != null) {
                    Toast.makeText(getContext(), resp.getError().getMessage() + " - " + resp.getError().getMessageDetail(), Toast.LENGTH_SHORT).show();
                }
                if (resp.getProfile() != null) {
                    Toast.makeText(getContext(), "Sikeres bejelentkezés", Toast.LENGTH_LONG).show();
                    preferences = getActivity().getPreferences(0);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(Constants.IS_LOGGED_IN, true);
                    //Log.d(TAG, "Pref: " + preferences.getString("email", null));
                    //Log.d(TAG, "Profile: " + resp.getProfile().getEmail().toString());
                    /*if(!preferences.getString("email", null).equals(resp.getProfile().getEmail().toString())){
                        pref = getActivity().getSharedPreferences(Constants.RSSI, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = pref.edit();
                        editor1.putString(Constants.LicensePlate, "");
                        editor1.apply();
                        Log.d(TAG, "valami"+ preferences.getString(Constants.LicensePlate, null));
                    }*/
                    editor.putString("sessionId", resp.getProfile().getSessionId().toString());
                    editor.putString("firstName", resp.getProfile().getFirstName().toString());
                    editor.putString("lastName", resp.getProfile().getLastName().toString());
                    //editor.putString("email", resp.getProfile().getEmail().toString());
                    //editor.putString("phone", resp.getProfile().getPhone().toString());
                    editor.apply();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                loadJSONError(email, password);
                //Toast.makeText(getContext(), "Hiba a hálózati kapcsolatban. Kérjük, ellenőrizze, hogy csatlakozik-e hálózathoz.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "No response");
            }
        });
    }

    public void loadJSONError(String email, String password){

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .baseUrl(Constants.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterfaceLoginError requestInterface = retrofit.create(RequestInterfaceLoginError.class);
        Call<ServerResponseError> response = requestInterface.post(email, password);
        response.enqueue(new Callback<ServerResponseError>() {
            @Override
            public void onResponse(Call<ServerResponseError> call, Response<ServerResponseError> response) {
                ServerResponseError resp = response.body();
                if(resp.getAlert() != ""){
                    Toast.makeText(getContext(), resp.getAlert(), Toast.LENGTH_LONG).show();
                }
                if(resp.getError() != null){
                    Toast.makeText(getContext(), resp.getError().getMessage()+" - "+resp.getError().getMessageDetail(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponseError> call, Throwable t) {
                //Toast.makeText(getContext(), "Hiba a hálózati kapcsolatban. Kérjük, ellenőrizze, hogy csatlakozik-e hálózathoz.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "No response");
            }
        });

    }

}
