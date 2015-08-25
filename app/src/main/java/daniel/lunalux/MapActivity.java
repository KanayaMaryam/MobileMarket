package daniel.lunalux;

import android.os.Bundle;
import android.os.AsyncTask;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.app.AppCompatActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements LocationListener{

    private GoogleMap map;
    private LocationManager locationManager;
    //minimum time and distance delta for onLocationChange to be called
    private static final long MIN_TIME = 40;
    private static final float MIN_DISTANCE = 1000;
    List<YardSale> saleArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Seller Locations");
        setContentView(R.layout.activity_map);

        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

        //initially start camera at last known location
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double longitude = 0;
        double latitude = 0;
        String url = "";
        if (location!=null){
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),15));
            url = "http://yardsalebackendtest.azurewebsites.net/api/location?latitude="+latitude+"&longitude="+longitude;
        }
        new LongOperation().execute(url); //TODO: could move this operation to the Splash Screen to make it faster
    }

    //class that extends AsyncTask class. handles the server stuff outside the main thread
    private class LongOperation  extends AsyncTask <String, Void, JSONArray> {
        //this function retrieves the JSON data and passes it to onPostExecute in a JSONArray format
        protected JSONArray doInBackground(String... urls) {
            JSONArray array = null;

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
                String data = responseStrBuilder.toString();
                array = new JSONArray(data);
            } catch (Exception e){}


            return array;
        }

        //updates the saleArray by converting the array of JSON objects here into array of YardSale objects
        protected void onPostExecute(JSONArray array){
            if (array != null){
                saleArray = new ArrayList<YardSale>();
                try {
                    for (int i = 0; i < array.length(); i++) {
                        saleArray.add(new YardSale(array.getJSONObject(i)));
                    }
                } catch(JSONException e){}
            }
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

        //make the markers if the ArrayList is populated
        if (saleArray != null) {
            try {
                for (int i = 0; i < saleArray.size(); i++) {
                    YardSale obj = saleArray.get(i);
                    String name = obj.getName();
                    double locationLatitude = obj.getLocationLatitude();
                    double locationLongitude = obj.getLocationLongitude();
                    double distance = obj.getDistance();
                    String address = obj.getAddress();
                    //Latitude, Longitude, Distance, Address
                    map.addMarker(new MarkerOptions().position(new LatLng(locationLatitude, locationLongitude))
                            .title(name)
                            .snippet("" + address + "\n" + distance));
                }
            } catch (Exception e) {}
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }

    //switch to the sell screen
    public void sell(MenuItem item){
        Intent intent = new Intent(this, SellActivity.class);
        startActivity(intent);
    }

}

