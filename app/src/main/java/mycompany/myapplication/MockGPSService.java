package mycompany.myapplication;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;

import java.security.Timestamp;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by tnash219 on 10/20/2014.
 */
public class MockGPSService extends IntentService implements
        GooglePlayServicesClient.OnConnectionFailedListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        LocationListener {

    /**
     * An IntentService must always have a constructor that calls the super constructor. The
     * string supplied to the super constructor is used to give a name to the IntentService's
     * background thread.
     */
    public MockGPSService() {
        super("MockGPSService");
    }

    LocationRequest mLocationRequest;
    LocationClient mLocationClient;
    boolean tracking;
    SharedPreferences sharedPreferences;

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

}
