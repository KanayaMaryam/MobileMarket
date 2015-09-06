package daniel.lunalux;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import org.json.JSONArray;
import org.json.JSONObject;

public class MapActivity extends AppCompatActivity implements LocationListener{
    public static double longitude;
    public static double latitude;
    public static GoogleMap map;
    public static JSONArray array;
    public static LocationManager locationManager;
    //minimum time and distance delta for onLocationChange to be called
    private static final long MIN_TIME = 40;
    private static final float MIN_DISTANCE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Seller Locations");
        setContentView(R.layout.activity_map);

        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        final Context mContext = this;
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }
            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(mContext);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(mContext);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(mContext);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),15));
        if (MapActivity.array != null){
            try {
                for (int i = 0; i < array.length(); i++) {
                    YardSale obj = new YardSale((JSONObject)array.get(i));
                    MapActivity.map.addMarker(new MarkerOptions().position(new LatLng(obj.getLocationLatitude(), obj.getLocationLongitude()))
                            .title(obj.getAddress())
                            .snippet("Phone: " + obj.getPhoneNumber()
                                    + "\nStart Time: " + obj.startToLocal() +
                                    "\nEnd Time: " + obj.endToLocal()));
                }
            } catch (Exception e) {e.printStackTrace();}
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
        this.longitude = location.getLongitude();
        this.latitude = location.getLatitude();
        map.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title("You are here!")).showInfoWindow();

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

