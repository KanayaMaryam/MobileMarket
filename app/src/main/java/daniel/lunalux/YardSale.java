package daniel.lunalux;

/**
 * Class to manage and store yard sale information
 */
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class YardSale {
    String phoneNumber;
    String startTime; //standard iso UTC time
    String endTime; //standard iso UTC time

    double locationLatitude;
    double locationLongitude;
    String address; //full address, including city and state

    public YardSale(JSONObject obj){
        try {
            locationLatitude = obj.getDouble("Latitude");
            locationLongitude = obj.getDouble("Longitude");
            address = obj.getString("Address");
            startTime=obj.getString("StartDateTime");
            endTime=obj.getString("EndDateTime");
            phoneNumber=obj.getString("PhoneNumber");
        } catch (JSONException e){}
    }

    public YardSale(String number, double lat, double lon, String address, String start, String end){
        locationLatitude = lat;
        locationLongitude = lon;
        this.address = address;
        phoneNumber=number;
        startTime=start;
        endTime=end;
    }

    public void submit(){

    }

    public String startToLocal(){
        DateFormat pstFormat = new SimpleDateFormat("EEE K:mm a, d MMM yyyy", Locale.US);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date date = null;
        try{
            date = df.parse(startTime);
        } catch (ParseException e){}
        return pstFormat.format(date);
    }

    public String endToLocal(){
        DateFormat pstFormat = new SimpleDateFormat("EEE K:mm a, d MMM yyyy", Locale.US);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date date = null;
        try{
            date = df.parse(endTime);
        } catch (ParseException e){}
        return pstFormat.format(date);
    }

    public String getAddress(){return address; }
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
    public String getPhoneNumber(){return phoneNumber;}
}
