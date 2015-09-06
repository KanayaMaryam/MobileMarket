package daniel.lunalux;

import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.location.LocationManager;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new LongOperation().execute();
    }

    //class that extends AsyncTask class. handles the server stuff outside the main thread
    private class LongOperation  extends AsyncTask<Void, Void, JSONArray> {
        //this function retrieves the JSON data and passes it to onPostExecute in a JSONArray format
        protected JSONArray doInBackground(Void... temp) {
            JSONArray array = null;
            try{
                MapActivity.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location location = MapActivity.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                String urlTemp = "";
                if (location!=null) {
                    MapActivity.longitude = location.getLongitude();
                    MapActivity.latitude = location.getLatitude();
                    urlTemp = "http://yardsalebackendproduction.azurewebsites.net/api/YardSaleEvent?latitude="+MapActivity.latitude+"&longitude="+MapActivity.longitude+"&distance=1000";
                } else { //default back to seattle
                    urlTemp = "http://yardsalebackendproduction.azurewebsites.net/api/YardSaleEvent?latitude="+47.6097+"&longitude="+-122.3331+"&distance=1000";
                }
                URL url = new URL(urlTemp);
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

        //updates the jsonarray in the mapactivity class, then start the activity
        protected void onPostExecute(JSONArray array){

            MapActivity.array = array;
            Intent intent=new Intent(getBaseContext(),MapActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash_sceeen, menu);
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
