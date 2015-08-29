package daniel.lunalux;

/**
 * Class to manage and store yard sale information
 */
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class YardSale {
    String phoneNumber;
    String startTime;
    String endTime;

    double locationLatitude;
    double locationLongitude;
    String address;

    public YardSale(JSONObject obj){
        try {
            locationLatitude = obj.getDouble("Latitude");
            locationLongitude = obj.getDouble("Longitude");
            address = obj.getString("Address");
            startTime=obj.getString("startTime");
            endTime=obj.getString("endTime");
        } catch (JSONException e){}
    }

    public YardSale(String number, LatLng loc, String address, String start, String end){
        locationLatitude = loc.latitude;
        locationLongitude = loc.longitude;
        phoneNumber=number;
        this.address = address;
        startTime=start;
        endTime=end;

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
    public String getNumber(){
        return phoneNumber;
    }
    public String getStart(){
        return startTime;
    }
    public String getEnd(){
        return endTime;
    }





}
