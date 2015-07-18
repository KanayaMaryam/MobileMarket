package daniel.lunalux;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;

public class MapActivity extends FragmentActivity implements LocationListener{

    //private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleMap map;
    private LocationManager locationManager;
    //minimum time and distance delta for onLocationChange to be called
    private static final long MIN_TIME = 40;
    private static final float MIN_DISTANCE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setUpMapIfNeeded();
        setContentView(R.layout.activity_map);
        //set map size to be 3/4th of screen height
        findViewById(R.id.map).getLayoutParams().height = getWindowManager().getDefaultDisplay().getHeight() * 3 / 4;
        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        //initially start camera of map pointed at Spokane (placeholder)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(47.6589, -117.4250), 15));
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (map != null) {
                setUpMap();
            }
        }
    }


    private void setUpMap() {
        //map.addMarker(new MarkerOptions().position(new LatLng(47.6589, 117.4250)).title("Zong"));
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

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }


}

