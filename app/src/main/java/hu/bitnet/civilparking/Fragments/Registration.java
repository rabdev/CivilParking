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
public class Registration extends Fragment {

    public AppCompatButton btn_register;
    public EditText reg_email;
    public EditText reg_password;
    public EditText reg_name;
    public EditText reg_phone;
    public EditText reg_confirm;
    SharedPreferences preferences;
    SharedPreferences pref;

    public Registration() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View reg = inflater.inflate(R.layout.fragment_registration, container, false);
        btn_register = (AppCompatButton)reg.findViewById(R.id.btn_register);
        reg_email = (EditText)reg.findViewById(R.id.reg_email);
        reg_password = (EditText)reg.findViewById(R.id.reg_password);
        reg_name = (EditText)reg.findViewById(R.id.reg_name);
        reg_confirm = (EditText)reg.findViewById(R.id.reg_confirm);

        TextView tv_login = (TextView) reg.findViewById(R.id.tv_login);
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login login = new Login();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame, login, login.getTag())
                        .commit();
            }
        });

        return reg;
    }

}
