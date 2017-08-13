package hu.bitnet.civilparking;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        MapsContainer mapsContainer = new MapsContainer();
        fragmentManager.beginTransaction()
                .replace(R.id.frame, mapsContainer, mapsContainer.getTag())
                .addToBackStack(null)
                .commit();

    }
}
