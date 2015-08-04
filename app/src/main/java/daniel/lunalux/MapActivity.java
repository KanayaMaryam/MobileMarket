package daniel.lunalux;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MapActivity extends FragmentActivity implements LocationListener{

    //private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleMap map;
    private LocationManager locationManager;
    //minimum time and distance delta for onLocationChange to be called
    private static final long MIN_TIME = 40;
    private static final float MIN_DISTANCE = 1000;
    JSONArray saleArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        //set map size to be 3/4th of screen height
        findViewById(R.id.map).getLayoutParams().height = getWindowManager().getDefaultDisplay().getHeight() * 3 / 4;
        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

        //initially start camera at last known location
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location!=null){
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),15));
        }
        //URL url = new URL("http://yardsalebackendtest.azurewebsites.net/api/location?latitude=1&longitude=1");
        String url = "http://yardsalebackendtest.azurewebsites.net/api/location?latitude=1&longitude=1";
        new LongOperation().execute(url);
    }

    //class that extends AsyncTask class. handles the server stuff outside the main thread
    private class LongOperation  extends AsyncTask <String, Void, Void> {

        JSONArray array;
        String data;

        //this function retrieves the JSON data in a string format
        protected Void doInBackground(String... urls) {
            try{
                URL url = new URL(urls[0]);
                URLConnection connection = url.openConnection();
                InputStream in = connection.getInputStream();
                //in = new BufferedInputStream(connection.getInputStream());
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();
                String inputStr;
                while ((inputStr = streamReader.readLine()) != null) {
                    responseStrBuilder.append(inputStr);
                }
                data = responseStrBuilder.toString();
            } catch (Exception e){}
            return null;
        }

        //updates the saleArray
        protected void onPostExecute(){
            try {
                saleArray = new JSONArray(data);
            } catch(JSONException e) {}
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        //animate camera panning from start location to current location. places marker on current location
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        map.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);
        //show info window by default, places marker with current location and title "you are here!"
        map.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("You are here!")).showInfoWindow();

        //make the markers if the JSONArray is populated
        if (saleArray != null) {
            try {
                for (int i = 0; i < saleArray.length(); i++) {
                    JSONObject obj = saleArray.getJSONObject(i);
                    String name = obj.getString("Name");
                    double locationLatitude = obj.getDouble("Latitude");
                    double locationLongitude = obj.getDouble("Longitude");
                    double distance = obj.getDouble("Distance");
                    String address = obj.getString("Address");
                    //Latitude, Longitude, Distance, Address
                    map.addMarker(new MarkerOptions().position(new LatLng(locationLatitude, locationLongitude))
                            .title(name)
                            .snippet("" + address + "\n" + distance)).showInfoWindow();
                }
            } catch (Exception e) {}
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }

    //switch to the sell screen
    public void sell(View view){
        Intent intent = new Intent(this, SellActivity.class);
        startActivity(intent);
    }

}

