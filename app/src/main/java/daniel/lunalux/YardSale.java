package daniel.lunalux;

/**
 * Class to manage and store yard sale information
 */
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class YardSale {
    String name;
    double locationLatitude;
    double locationLongitude;
    double distance;
    String address;

    public YardSale(JSONObject obj){
        try {
            name = obj.getString("Name");
            locationLatitude = obj.getDouble("Latitude");
            locationLongitude = obj.getDouble("Longitude");
            distance = obj.getDouble("Distance");
            address = obj.getString("Address");
        } catch (JSONException e){}
    }

    public YardSale(String name, LatLng loc, double distance, String address){
        this.name = name;
        locationLatitude = loc.latitude;

        locationLongitude = loc.longitude;
        this.distance = distance;
        this.address = address;
    }

    public String getName(){
        return name;
    }

    public String getAddress(){
        return address;
    }

    public double getLocationLatitude(){
        return locationLatitude;
    }

    public double getLocationLongitude(){
        return locationLongitude;
    }

    public double getDistance(){
        return distance;
    }


}
