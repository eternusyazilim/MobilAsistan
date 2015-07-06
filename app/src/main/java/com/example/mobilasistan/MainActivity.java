package com.example.mobilasistan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;


public class MainActivity extends Activity  {

    private TextView subText;
    private Button konumSec;
    private GoogleMap googleMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        subText= (TextView) findViewById(R.id.subText);
        konumSec=(Button)findViewById(R.id.button);

        ConnectionDetector kontrol = new ConnectionDetector(getApplicationContext());
        if(!kontrol.isConnectingToInternet()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Lütfen internetizi açıp tekrar girin..!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else if(!kontrol.isGpsOpen()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Lütfen konum açıp tekrar girin..!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else{
            try {
                initilizeMap();
            } catch (Exception e) {
                e.printStackTrace();
            }

            konumSec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(MainActivity.this,Konumlar.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location arg0) {
                    // TODO Auto-generated method stub
                    double latitude = arg0.getLatitude();
                    double longitude = arg0.getLongitude();
                    LatLng latLng = new LatLng(latitude, longitude);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
                    googleMap.setOnMyLocationChangeListener(null);  // Once
                    subText.setVisibility(View.GONE);
                    konumSec.setVisibility(View.VISIBLE);
                }
            });

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
