package hu.bitnet.civilparking.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import hu.bitnet.civilparking.MainActivity;
import hu.bitnet.civilparking.Objects.Constants;
import hu.bitnet.civilparking.R;

/**
 * Created by Attila on 2018.01.20..
 */

public class Logout  extends Fragment {

    public AppCompatButton btn_logout;
    SharedPreferences pref;

    public Logout() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View logout = inflater.inflate(R.layout.fragment_logout, container, false);
        AppCompatButton btn_logout = (AppCompatButton)logout.findViewById(R.id.btn_logout);

        pref = getActivity().getPreferences(0);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean(Constants.IS_LOGGED_IN, false);
                editor.apply();
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

        TextView title = (TextView)((MainActivity)getActivity()).findViewById(R.id.title);
        title.setText("Kijelentkezés");

        ImageButton settings = (ImageButton)((MainActivity)getActivity()).findViewById(R.id.settings);
        settings.setVisibility(View.INVISIBLE);

        return logout;
    }
}
