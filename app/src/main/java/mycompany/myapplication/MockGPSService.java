package mycompany.myapplication;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

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

        tracking = sharedPreferences.getBoolean("pref_key_tracker_disabled_setting", false);
    }

    @Override
    public void onProviderEnabled(String str) {}
    @Override
    public void onProviderDisabled(String str) {}
    @Override
    public void onStatusChanged(String str, int i, Bundle bundle) {}
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}
    @Override
    public void onConnected(Bundle bundle) {}
    @Override
    public void onDisconnected() {}
    @Override
    public void onLocationChanged(Location location) {}

}
