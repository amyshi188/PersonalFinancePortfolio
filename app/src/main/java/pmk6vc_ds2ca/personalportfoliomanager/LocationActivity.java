package pmk6vc_ds2ca.personalportfoliomanager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Response;

public class LocationActivity extends AppCompatActivity implements LocationListener {
    private GoogleApiClient mGoogleApiClient = null;
    private TextView latitudeText;
    private TextView longitudeText;
    private TextView NearestFinancialAdvisers;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        latitudeText = (TextView) findViewById(R.id.Latitude);
        longitudeText = (TextView) findViewById(R.id.Longitude);
        NearestFinancialAdvisers = (TextView) findViewById(R.id.NearestFinancialAdvisersText);

        //Request permission to use GPS
        String request1[] = {Manifest.permission.ACCESS_FINE_LOCATION};
        String request2[] = {Manifest.permission.INTERNET};
        requestPermissions(request1, 1234);
        requestPermissions(request2, 1235);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
        } else {
            Log.d("GPS", "GPS Permissions not granted.");
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) ==
                        PackageManager.PERMISSION_GRANTED) {
        } else {
            Log.d("GPS", "Internet Permissions not granted.");
        }
        //Instantiate LocationManager and check for location every 10000 milliseconds
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        } catch (SecurityException se) {    //error if permission not granted
            Log.d("GPS", se.toString());
        }

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            // Set default location to be Charlottesville, VA
            // Retrieve Yelp data for Charlottesville
            YelpTask yt = new YelpTask();
            double lat_init = 38.035638;
            double long_init = -78.503357;
            yt.execute(lat_init, long_init);
            DecimalFormat df = new DecimalFormat("##.00");
            latitudeText.setText(df.format(lat_init));
            longitudeText.setText(df.format(long_init));
        } else {
            // No network connection
            Context context = getApplicationContext();
            CharSequence text = "No Internet connection available...";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //Called every 10 seconds
    //Get the latitude and longitude values and set the text views to those values
    @Override
    public void onLocationChanged(Location loc) {
        DecimalFormat df = new DecimalFormat("##.00");
        double lat = loc.getLatitude();
        double longt = loc.getLongitude();
        latitudeText.setText(df.format(lat));
        longitudeText.setText(df.format(longt));

        YelpTask yt = new YelpTask();
        yt.execute(lat, longt);

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle b) {

    }

    private class YelpTask extends AsyncTask<Double, Void, String> {
        private String ConsumerKey = "Tgix_2QP4i7Ue9XMgk_98g";
        private String ConsumerSecret = "eSzLb4suQ7vnnhxkLC4a-Sv8na0";
        private String Token = "2dTEw530wUMbHSKaF6Ne6pbu3nzV-91R";
        private String TokenSecret = "rAmRBL8BtFhnQcJwUkq6GFoYBOA";

        @Override
        protected String doInBackground(Double... coordinate) {
            double latitude = coordinate[0];
            double longitude = coordinate[1];

            String retval = "";

            YelpAPIFactory apiFactory = new YelpAPIFactory(this.ConsumerKey, this.ConsumerSecret, this.Token, this.TokenSecret);
            YelpAPI yelpAPI = apiFactory.createAPI();
            Map<String, String> params = new HashMap<String, String>();

            params.put("term", "financial adviser");
            params.put("limit", "5");

            CoordinateOptions coordinate_param = CoordinateOptions.builder()
                    .latitude(latitude)
                    .longitude(longitude).build();

            try {
                Call<SearchResponse> call = yelpAPI.search(coordinate_param, params);
                Response<SearchResponse> response = call.execute();
                SearchResponse sr = response.body();
                int numResults = sr.total();
                ArrayList<Business> businesses = sr.businesses();
                DecimalFormat df = new DecimalFormat("##.00");
                for (int i = 0; i < numResults; i++) {
                    retval += "<b>"+businesses.get(i).name()+"</b>";
                    retval += "<br>    Phone: " + businesses.get(i).displayPhone();
                    retval += "<br>    Rating: " + df.format(businesses.get(i).rating()) + "/5";
                    retval += "<br>";
                }
            } catch (Exception e) {
                Log.d("YELP", "Exception caught");
                retval = "Retrieving nearest financial advisers...\n";
            }
            return retval;
        }

        @Override
        protected void onPostExecute(String s) {
            NearestFinancialAdvisers.setText(Html.fromHtml(s));
        }
    }
}
