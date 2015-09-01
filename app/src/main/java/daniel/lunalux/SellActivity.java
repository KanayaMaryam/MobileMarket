package daniel.lunalux;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.net.Uri;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class SellActivity extends AppCompatActivity {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);
    }

    public void showTimePickerDialog(View v) {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setButton((Button) v);
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setButton((Button) v);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void takePicture(View v){
        // create Intent to take a picture and return control to the calling application
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // start the image capture Intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {}

            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image saved to:\n" +mCurrentPhotoPath, Toast.LENGTH_LONG).show();
                Log.d("Image", "Image saved to:\n" +mCurrentPhotoPath);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "You canceled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "capture failed", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "did not work", Toast.LENGTH_LONG).show();
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }
    public void submitInfo(View v){
        boolean valid=true;
        String address = "";
        String city = "";
        String state = "";
        String number = "";
        double lat = 47.7717; //default filler values for lat and long
        double lon = -122.2044;
        String start = "";
        String end = "";
        String date = "";
        TextView addressbox=(TextView) findViewById(R.id.address);
        if(addressbox.getText().toString().equals("")&&valid){
            valid=false;
            displayToast("Fill in Address");
        }
        else{
            address=addressbox.getText().toString();
            TextView citybox=(TextView) findViewById(R.id.city);
            if(citybox.getText().toString().equals("")&&valid){
                valid=false;
                displayToast("Fill in City");
            }
            else{
                city=citybox.getText().toString();
                TextView statebox=(TextView) findViewById(R.id.state);
                if(statebox.getText().toString().equals("")&&valid){
                    valid=false;
                    displayToast("Fill in State");
                }
                else{
                    state=statebox.getText().toString();
                    TextView numberbox=(TextView) findViewById(R.id.phoneNumber);
                    if(numberbox.getText().toString().equals("")&&valid){
                        valid=false;
                        displayToast("Fill in Phone Number");
                    }
                    else{
                        number=numberbox.getText().toString();
                        Button startbutton=(Button) findViewById(R.id.PickTimeFromButton);
                        if(startbutton.getText().toString().equals("Pick Time")&&valid){
                            valid=false;
                            displayToast("Fill in Start Time");
                        } else {
                            start = startbutton.getText().toString();
                            Button endbutton=(Button) findViewById(R.id.PickTimeToButton);
                            if(endbutton.getText().toString().equals("Pick Time")&&valid){
                                valid=false;
                                displayToast("Fill in End Time");
                            }
                            else{
                                end=endbutton.getText().toString();
                                Button datebutton=(Button) findViewById(R.id.PickDateButton);
                                if(datebutton.getText().toString().equals("Pick Date")&&valid){
                                    valid=false;
                                    displayToast("Fill in Date");
                                } else {
                                    date = datebutton.getText().toString();
                                }
                            }
                        }
                    }
                }
            }
        }

        //TODO: convert address to latitude and longitude
        if (valid){ //convert the text chunks of local time to UTC and submit
            TimeZone tz = TimeZone.getTimeZone("UTC");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
            df.setTimeZone(tz);
            Date starttime=new Date();
            Date endtime=new Date();
            String[] datechunks=date.split("-");
            String[] startchunks=start.split(":");
            String[] endchunks=end.split(":");
            Calendar cal = Calendar.getInstance(); //get default time zone and locale calendar
            cal.set(Calendar.MONTH, Integer.parseInt(datechunks[1])-1);
            cal.set(Calendar.DATE, Integer.parseInt(datechunks[2]));
            cal.set(Calendar.YEAR, Integer.parseInt(datechunks[0]));
            cal.set(Calendar.HOUR,Integer.parseInt(startchunks[0])-12); //some kind of weird offset needed to display the correct time
            cal.set(Calendar.MINUTE,Integer.parseInt(startchunks[1]));
            cal.set(Calendar.SECOND,0);
            starttime = cal.getTime();
            start = df.format(starttime);
            cal.set(Calendar.HOUR,Integer.parseInt(endchunks[0]));
            cal.set(Calendar.MINUTE,Integer.parseInt(endchunks[1]));
            endtime = cal.getTime();
            end = df.format(endtime);
            //displayToast(start); //debug purposes only
            new YardSale(number, lat,lon, address + ", " + city + " " + state, start, end).submit(); //calls submit
        }
    }
    public void displayToast(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

}
