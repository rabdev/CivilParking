package hu.bitnet.civilparking;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import hu.bitnet.civilparking.Fragments.Login;
import hu.bitnet.civilparking.Fragments.ParkingList;
import hu.bitnet.civilparking.Objects.Constants;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        preferences = getPreferences(0);

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSIONS);


        }

        if (preferences.getBoolean(Constants.IS_LOGGED_IN, true)) {
            setContentView(R.layout.activity_main);
            ParkingList parkingList = new ParkingList();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame, parkingList, parkingList.getTag())
                    .addToBackStack(null)
                    .commit();

        } else {
            Login login = new Login();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame, login, login.getTag())
                    .commit();
        }


        /*MapsContainer mapsContainer = new MapsContainer();
        fragmentManager.beginTransaction()
                .replace(R.id.frame, mapsContainer, mapsContainer.getTag())
                .addToBackStack(null)
                .commit();*/

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
