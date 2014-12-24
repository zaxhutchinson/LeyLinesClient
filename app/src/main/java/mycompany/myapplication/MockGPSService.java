package mycompany.myapplication;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by tnash219 on 10/20/2014.
 *
 * MockGPSService sets up google related stuff to retrieve GPS
 * Coordinates, and calls the async task that does the collection
 * and sending of coordinates to the server.
 */
public class MockGPSService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    // Holds the GPS coordinate as it is retrieved.
    private Location mLastLocation;


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean isConnected;

    // Used to keep track of our collect and send intervals
    private long collectStartTime;
    private long sendStartTime;

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
        setIntentRedelivery(true);

        // Set up preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        // Set up google api client
        buildGoogleApiClient();
        mGoogleApiClient.connect();

        // Start the async GPS task
        CollectGPSTask collectGPSTask = new CollectGPSTask();
        collectGPSTask.execute();
        stopSelf();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /* The heart and soul of Leylines tracking. It collects and sends
     * the GPS coordinates to the server off the main UI Thread of
     * the application.
     *
     * Warning: due to the nature of Android asynchronous programming
     * no other async task can run while this is in operation as it
     * blocks all other async tasks.
     */
    public class CollectGPSTask extends AsyncTask<Void, String, Void> {

        @Override
        protected void onPreExecute() {
            collectStartTime = System.currentTimeMillis();
            sendStartTime = System.currentTimeMillis();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            System.out.println("Now collecting & sending gps coordinates");

            // While tracking is enabled...run the service.
            while (sharedPreferences.getBoolean("pref_key_tracker_enabled",false)) {

                // If we've passed our collection interval, grab another GPS
                // coordinate and reset the collectStartTime.
                if (Long.parseLong(sharedPreferences.getString("pref_key_gps_collect_frequency","")) * 1000 + collectStartTime < System.currentTimeMillis()) {

                    collectStartTime = System.currentTimeMillis();

                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                            mGoogleApiClient);
                    if (mLastLocation != null) {
                        editor.putString("pref_key_latitude_list", sharedPreferences.getString("pref_key_latitude_list", "") + "," + String.valueOf(mLastLocation.getLatitude()));
                        editor.putString("pref_key_longitude_list", sharedPreferences.getString("pref_key_longitude_list", "") + "," + String.valueOf(mLastLocation.getLongitude()));
                        editor.putString("pref_key_time_list", sharedPreferences.getString("pref_key_time_list", "") + "," + System.currentTimeMillis() / 1000);
                    }

                    System.out.println("New GPS: " + mLastLocation.toString());

                }

                // If we're past our send interval, send all the GPS coordinates we have
                // collected thus far and reset the sendStartTime.
                if (Long.parseLong(sharedPreferences.getString("pref_key_gps_send_frequency","")) * 1000 + sendStartTime < System.currentTimeMillis()) {

                    sendStartTime = System.currentTimeMillis();

                    System.out.println("Sending GPS bundle");

                    String[] latitudes = sharedPreferences.getString("pref_key_latitude_list", "").split(",");
                    String[] longitudes = sharedPreferences.getString("pref_key_longitude_list", "").split(",");
                    String[] times = sharedPreferences.getString("pref_key_time_list", "").split(",");

                    try {
                        //prepare new socket
                        Socket socket = new Socket();

                        //connect to server
                        socket.connect(new InetSocketAddress(sharedPreferences.getString("pref_key_HOST", ""), Integer.parseInt(sharedPreferences.getString("pref_key_PORT", ""))), 5000);

                        //prepare to send message to server
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                        //prepare to receive message from server
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        //checks if nothing went wrong with the collection of gps coordinates
                        if (latitudes.length == longitudes.length && longitudes.length == times.length) {
                            for (int i = 0; i < times.length; i++) {

                                //text to send in order to send gps coordinates
                                out.print("G\n" + //indicates command for new gps position
                                        sharedPreferences.getString("pref_key_UID", "") + "\n" + //user id
                                        latitudes[i] + "\n" + //latitude
                                        longitudes[i] + "\n" + //longitude
                                        times[i] + "\n"); //time in seconds
                            }
                        }

                        //done with output
                        out.flush();

                        //stop waiting for input from server
                        out.close();
                        in.close();

                        //disconnect and close socket
                        socket.close();

                    } catch (UnknownHostException e) {
                        publishProgress("Host unreachable\n" + e);
                    } catch (IOException e) {
                        publishProgress("I/O operation failed: " + e);
                    } catch (IllegalArgumentException e) {
                        publishProgress("Illegal argument: " + e);
                    }

                    //clear out strings when done
                    editor.putString("pref_key_latitude_list", "");
                    editor.putString("pref_key_longitude_list", "");
                    editor.putString("pref_key_time_list", "");

                    System.out.println("Done sending GPS Bundle");

                }

            }
            System.out.println("No longer collecting & sending gps coordinates");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            stopSelf();
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


