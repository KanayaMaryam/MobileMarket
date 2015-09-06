package daniel.lunalux;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.AsyncTask;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

public class MapActivity extends AppCompatActivity implements LocationListener{
    public static double longitude;
    public static double latitude;
    public double yourLocationLat;
    public double yourLocationLong;

    private GoogleMap map;
    private LocationManager locationManager;
    //minimum time and distance delta for onLocationChange to be called
    private static final long MIN_TIME = 40;
    private static final float MIN_DISTANCE = 1000;
    private ArrayList<YardSale> markers = new ArrayList<YardSale>();
    private ArrayList<Marker> markerobjects = new ArrayList<Marker>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Loading Seller Locations...");
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
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

        //initially start camera at last known location
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        String url = "";
        if (location!=null){
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            yourLocationLat=location.getLongitude();
            yourLocationLat=location.getLatitude();
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),15));
            url = "http://yardsalebackendproduction.azurewebsites.net/api/YardSaleEvent?latitude="+latitude+"&longitude="+longitude+"&distance=1000";
        } else { //default back to seattle
            url = "http://yardsalebackendproduction.azurewebsites.net/api/YardSaleEvent?latitude="+47.6097+"&longitude="+-122.3331+"&distance=1000";
        }
        new LongOperation().execute(url);
    }

    //class that extends AsyncTask class. handles the server stuff outside the main thread
    private class LongOperation  extends AsyncTask <String, Void, JSONArray> {
        //this function retrieves the JSON data and passes it to onPostExecute in a JSONArray format
        protected JSONArray doInBackground(String... urls) {
            JSONArray array = null;
            try{
                URL url = new URL(urls[0]);
                URLConnection connection = url.openConnection();

                //in = new BufferedInputStream(connection.getInputStream());
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();
                String inputStr;
                while ((inputStr = streamReader.readLine()) != null) {
                    responseStrBuilder.append(inputStr);
                }
                streamReader.close();
                String data = responseStrBuilder.toString();
                array = new JSONArray(data);

            } catch (Exception e){
                e.printStackTrace();
            }
            return array;
        }

        //updates the map with the markers
        protected void onPostExecute(JSONArray array){
            if (array != null){
                try {
                    for (int i = 0; i < array.length(); i++) {

                        YardSale obj = new YardSale((JSONObject)array.get(i));
                        markers.add(obj);
                        markerobjects.add(map.addMarker(new MarkerOptions().position(new LatLng(obj.getLocationLatitude(), obj.getLocationLongitude()))
                                .title(obj.getAddress())
                                .snippet("Phone: " + obj.getPhoneNumber()
                                         + "\nStart Time: " + obj.startToLocal() +
                                          "\nEnd Time: " + obj.endToLocal())));
                    }
                    setTitle("Seller Locations");
                } catch (Exception e) {e.printStackTrace();}
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
    public void cycleright(View v){
        double minLatDif=Integer.MAX_VALUE;
        int minLatInd=-2;
        for(int i=0; i<markers.size(); i++){
            double marklat=markers.get(i).getLocationLatitude();
            if(latitude!=marklat){
                double dif=marklat-latitude;
                if(dif>0&&dif<minLatDif){
                    minLatDif=dif;
                    minLatInd=i;
                }
            }
        }
        if(latitude!=yourLocationLat){
                double dif=yourLocationLat-latitude;
                if(dif>0&&dif<minLatDif){
                    minLatDif=dif;
                    minLatInd=-1;
                }
        }
        if(minLatDif==-2){
            return;
        }
        LatLng latLng=new LatLng(0, 0);
        if(minLatInd>=0){
            latLng = new LatLng(markers.get(minLatInd).getLocationLatitude(), markers.get(minLatInd).getLocationLongitude());
            latitude=markers.get(minLatInd).getLocationLatitude();
            longitude=markers.get(minLatInd).getLocationLongitude();

        }
        if(minLatInd==-1){
            latLng = new LatLng(yourLocationLat, yourLocationLong);
        }
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        map.animateCamera(cameraUpdate);
        markerobjects.get(minLatInd).showInfoWindow();

    }
    public void cycleleft(View v){
        double minLatDif=Integer.MAX_VALUE;
        int minLatInd=-2;
        for(int i=0; i<markers.size(); i++){
            double marklat=markers.get(i).getLocationLatitude();
            if(latitude!=marklat){
                double dif=marklat-latitude;
                if(dif>0&&dif<minLatDif){
                    minLatDif=dif;
                    minLatInd=i;
                }
            }
        }
        if(latitude!=yourLocationLat){
            double dif=yourLocationLat-latitude;
            if(dif>0&&dif<minLatDif){
                minLatDif=dif;
                minLatInd=-1;
            }
        }
        if(minLatDif==-2){
            return;
        }
        LatLng latLng=new LatLng(0, 0);
        if(minLatInd>=0){
            latLng = new LatLng(markers.get(minLatInd).getLocationLatitude(), markers.get(minLatInd).getLocationLongitude());
            latitude=markers.get(minLatInd).getLocationLatitude();
            longitude=markers.get(minLatInd).getLocationLongitude();

        }
        if(minLatInd==-1){
            latLng = new LatLng(yourLocationLat, yourLocationLong);
        }
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        map.animateCamera(cameraUpdate);
        markerobjects.get(minLatInd).showInfoWindow();
    }

}

