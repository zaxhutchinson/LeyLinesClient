package mycompany.myapplication;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import java.security.Timestamp;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by tnash219 on 10/20/2014.
 */
public class MockGPSService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean isConnected;

    /**
     * An IntentService must always have a constructor that calls the super constructor. The
     * string supplied to the super constructor is used to give a name to the IntentService's
     * background thread.
     */
    public MockGPSService() {
        super("MockGPSService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        CollectGPSTask collectGPSTask = new CollectGPSTask();
        collectGPSTask.execute();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public class CollectGPSTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            long startTime;
            while (sharedPreferences.getBoolean("pref_key_tracker_enabled",false)) {
                startTime = System.currentTimeMillis();
                while (Long.parseLong(sharedPreferences.getString("pref_key_gps_collect_frequency","")) * 1000 + startTime > System.currentTimeMillis()) {

                }
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                if (mLastLocation != null) {
                    editor.putString("pref_key_latitude_list",sharedPreferences.getString("pref_key_latitude_list","") + "," + String.valueOf(mLastLocation.getLatitude()));
                    editor.putString("pref_key_longitude_list",sharedPreferences.getString("pref_key_longitude_list","") + "," + String.valueOf(mLastLocation.getLongitude()));
                    editor.putString("pref_key_time_list",sharedPreferences.getString("pref_key_time_list","") + "," + System.currentTimeMillis() / 1000);
                }

            }
            System.out.println("No longer collecting gps coords");
            return null;
        }
    }



    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }



    /*

    old code

    LocationRequest mLocationRequest;
    LocationClient mLocationClient;
    boolean tracking;

    ArrayList<String> current_path = new ArrayList<String>();

    @Override
    protected void onHandleIntent(Intent workIntent) {
        String dataString = workIntent.getDataString();

        mLocationRequest = LocationRequest.create();

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        int gpsCollectFreq = sharedPreferences.getInt("pref_key_gps_collect_frequency",30000);

        mLocationRequest.setInterval(gpsCollectFreq);
        // Hard coded fastest GPS check to 1 secs.
        mLocationRequest.setFastestInterval(1000);

        mLocationClient = new LocationClient(this,this,this);

        tracking = sharedPreferences.getBoolean("pref_key_tracker_disabled_setting", true);

        mLocationClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}
    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

        if(tracking) {
            mLocationClient.requestLocationUpdates(mLocationRequest,this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDisconnected() {
        Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        Calendar calendar = Calendar.getInstance();
        long ts = calendar.getTimeInMillis();

        String new_loc = Double.toString(location.getLatitude()) + "\n" +
                         Double.toString(location.getLongitude()) + "\n" +
                         Long.toString(ts) + "\n";

        current_path.add(new_loc);
        Toast.makeText(this, new_loc, Toast.LENGTH_SHORT).show();
    }
    */

}


