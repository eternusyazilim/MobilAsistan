package com.example.mobilasistan;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;


import java.util.ArrayList;
import java.util.List;


public class Konumlar extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener ,CheckBox.OnCheckedChangeListener {

    ListView list;
    private GoogleApiClient mGoogleApiClient;
    ArrayList<String> konumlar=new ArrayList<String>();
    Button button;
    TextView secimYap;
    CheckBox hepsi;
    CheckBox hastahane;
    CheckBox eczane;
    CheckBox cafe;
    CheckBox ibadethane;
    CheckBox okul;
    CheckBox diger;
    ArrayAdapter<String> veriAdaptoru;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konumlar);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
        // Views
        list= (ListView) findViewById(R.id.listView);
        button=(Button)findViewById(R.id.button2);
        secimYap = (TextView) findViewById(R.id.textView2);
        hepsi= (CheckBox) findViewById(R.id.Hepsi);
        hastahane= (CheckBox) findViewById(R.id.hastahane);
        eczane= (CheckBox) findViewById(R.id.eczane);
        cafe= (CheckBox) findViewById(R.id.kafe);
        ibadethane= (CheckBox) findViewById(R.id.ibadet);
        okul= (CheckBox) findViewById(R.id.okul);
        diger= (CheckBox) findViewById(R.id.diger);

        // Listeners
        hepsi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    hepsi.setChecked(true);
                    hastahane.setChecked(true);
                    eczane.setChecked(true);
                    cafe.setChecked(true);
                    ibadethane.setChecked(true);
                    okul.setChecked(true);
                    diger.setChecked(true);
                    button.setEnabled(true);
                }
            }
        });
        hastahane.setOnCheckedChangeListener(this);
        eczane.setOnCheckedChangeListener(this);
        cafe.setOnCheckedChangeListener(this);
        ibadethane.setOnCheckedChangeListener(this);
        okul.setOnCheckedChangeListener(this);
        diger.setOnCheckedChangeListener(this);




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hepsi.isChecked() ||hastahane.isChecked() ||eczane.isChecked() ||cafe.isChecked() ||ibadethane.isChecked() || okul. isChecked() ||diger.isChecked()){
                    konumlar.clear();
                    secimYap.setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);
                    veriAdaptoru= new ArrayAdapter<String>(Konumlar.this, android.R.layout.simple_list_item_1, android.R.id.text1, konumlar);
                    lokasyonCek(hepsi.isChecked(),hastahane.isChecked(),eczane.isChecked(),cafe.isChecked(),ibadethane.isChecked(), okul. isChecked(),diger.isChecked());
                    list.setAdapter(veriAdaptoru);
                    veriAdaptoru.notifyDataSetChanged();
                }
                else{
                    secimYap.setVisibility(View.VISIBLE);
                    list.setVisibility(View.GONE);
                }

            }
        });




    }
    private void lokasyonCek(final boolean hepsi, final boolean hastahane, final boolean eczane, final boolean cafe, final boolean ibadethane, final boolean okul, final boolean diger) {

        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    List types=placeLikelihood.getPlace().getPlaceTypes();
                    Log.i("liste", String.format("Place '%s' has likelihood: %g",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));
                    if(hepsi)
                    konumlar.add(placeLikelihood.getPlace().getName().toString());
                    else{
                        if(hastahane && (types.contains(Place.TYPE_HOSPITAL) || types.contains(Place.TYPE_DOCTOR) ||types.contains(Place.TYPE_DENTIST) || types.contains(Place.TYPE_VETERINARY_CARE)))
                            konumlar.add(placeLikelihood.getPlace().getName().toString());
                        if(eczane && types.contains(Place.TYPE_PHARMACY))
                            konumlar.add(placeLikelihood.getPlace().getName().toString());
                        if(cafe &&( types.contains(Place.TYPE_CAFE) ||(types.contains(Place.TYPE_RESTAURANT) || types.contains(Place.TYPE_NIGHT_CLUB))))
                            konumlar.add(placeLikelihood.getPlace().getName().toString());
                        if(ibadethane && (types.contains(Place.TYPE_MOSQUE) || types.contains(Place.TYPE_CHURCH)))
                            konumlar.add(placeLikelihood.getPlace().getName().toString());
                        if(okul && (types.contains(Place.TYPE_UNIVERSITY) || types.contains(Place.TYPE_SCHOOL)))
                            konumlar.add(placeLikelihood.getPlace().getName().toString());
                        if(diger)
                            konumlar.add(placeLikelihood.getPlace().getName().toString());
                    }

                }
                likelyPlaces.release();
                veriAdaptoru.notifyDataSetChanged();
            }
        });



    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_konumlar, menu);
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

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(!isChecked){
            hepsi.setChecked(false);

        }
        else{
            button.setEnabled(true);
        }
    }
}
