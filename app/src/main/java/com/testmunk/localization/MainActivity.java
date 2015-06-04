package com.testmunk.localization;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;


public class MainActivity extends FragmentActivity
        implements GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMap();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(3000)
                .setFastestInterval(3000);
    }

    private void initMap() {
        if (map == null) {
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

            map.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
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

    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        final LatLng position = new LatLng(location.getLatitude(), location.getLongitude());

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 6));

        new UpdateCityTask().execute(position);
    }

    private class UpdateCityTask extends AsyncTask<LatLng, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(LatLng... params) {
            LatLng position = params[0];

            Geocoder geocoder = new Geocoder(MainActivity.this);
            try {
                return geocoder.getFromLocation(position.latitude, position.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            super.onPostExecute(addresses);

            if (addresses.size() > 0) {
                updateLocationView(addresses.get(0).getLocality());
            } else {
                updateLocationView(null);
            }
        }
    }

    private void updateLocationView(String city) {
        TextView locationTextView = (TextView) findViewById(R.id.title);

        if (city != null) {
            locationTextView.setVisibility(View.VISIBLE);
            locationTextView.setText("You are in " + city);
        } else {
            locationTextView.setVisibility(View.GONE);
        }
    }
}
