package hu.bitnet.civilparking.Fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import hu.bitnet.civilparking.R;

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

}
