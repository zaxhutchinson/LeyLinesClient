package mycompany.myapplication;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by tnash219 on 10/24/2014.
 */
//public class ToggleTrackerTask extends AsyncTask<String, String, UserStatus> {
public class ToggleTrackerTask extends IntentService {
    //required for toast messages and manipulation of the status view
    //Activity activity;
    //TextView status;
    //TextView tracker;
    //TextView display;
    //TextView path;

    //TODO variables/object for drawing map goes here
    public ToggleTrackerTask() { super("ToggleTrackerTask"); }
    //public ToggleTrackerTask(Activity activity) {
    //    this.activity = activity;
    //}

    //sends a message to a server indicating that it should respond with the status of the device
    //@Override
    //protected UserStatus doInBackground(String... UidHostPort) {
    @Override
    protected void onHandleIntent(Intent intent) {

        //status = (TextView)activity.findViewById(R.id.statusText);
        //tracker = (TextView)activity.findViewById(R.id.trackerText);
        //display = (TextView)activity.findViewById(R.id.displayText);
        //path = (TextView)activity.findViewById(R.id.pathText);

        //TODO connect to map variables/object here

        String Uid = intent.getStringExtra("Uid");
        String Host = intent.getStringExtra("Host");
        String Port = intent.getStringExtra("Port");

        String ReturnVal = "";

        try {
            //prepare new socket
            Socket socket = new Socket();

            //connect to server
            socket.connect(new InetSocketAddress(Host, Integer.parseInt(Port)), 5000);

            //prepare to send message to server
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

            //text to send in order to request a new account
            out.print("T\n" + //indicates command for toggling tracker
                    Uid + "\n"); //user id

            //indicate to user that you're sending to server
            //publishProgress("Updating...");

            //done with output
            out.flush();


            //prepare to receive message from server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while(true) {
                ReturnVal = in.readLine();
                if (ReturnVal != null) break;
            }

            //stop waiting for input from server
            out.close();
            in.close();

            //disconnect and close socket
            socket.close();

        } catch (UnknownHostException e) {
            System.out.println("Host \"" + Host + ":" + Port + "\" unreachable\n" + e);
            //publishProgress("Host \"" + UidHostPort[1] + ":" + UidHostPort[2] + "\" unreachable\n" + e);
        } catch (IOException e) {
            System.out.println("I/O operation failed: " + e);
            //publishProgress("I/O operation failed: " + e);
        } catch(IllegalArgumentException e) {
            //publishProgress("Illegal argument: " + e);
            System.out.println("Illegal argument: " + e);
        }


        //retrieves string with status to be parsed and stores it in userStatus
        UserStatus userStatus = new UserStatus(ReturnVal);


        //prepares data for updating preferences
        String[] retrievedPreference = ReturnVal.split("[ ]+");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (retrievedPreference.length == 4) {
            editor.putString("pref_key_alert_status", retrievedPreference[0]);
            editor.putBoolean("pref_key_tracker_enabled", Boolean.valueOf(retrievedPreference[1]));
            editor.putString("pref_key_display_status", retrievedPreference[2]);
            editor.putBoolean("pref_key_automatic_path", Boolean.valueOf(retrievedPreference[3]));
            editor.commit();
        }

        //starts the gps collecting and sending services
        if (sharedPreferences.getBoolean("pref_key_tracker_enabled",false))  {
            Intent gpsCollector = new Intent(this, MockGPSService.class);
            this.startService(gpsCollector);
            //Intent gpsSender = new Intent(this, SendGPSService.class);
            //activity.startService(gpsSender);
        }

        //Intent resultData = new Intent();
        //resultData.putExtra("returnVal", "Re")

        //returns the strings for use in onPostExecute
        //return userStatus;
    }

    /*
    @Override
    protected void onProgressUpdate(String... progress) {
        Toast.makeText(activity, progress[0], Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(UserStatus userStatus) {
        try {
            status.setGravity(Gravity.CENTER);
            status.setTextColor(activity.getResources().getColor(userStatus.getAlertColor()));
            status.setText(userStatus.getAlertText());

            tracker.setGravity(Gravity.CENTER);
            tracker.setTextColor(activity.getResources().getColor(userStatus.getTrackerColor()));
            tracker.setText(userStatus.getTrackerText());

            display.setGravity(Gravity.CENTER);
            display.setText(userStatus.getDisplayText());

            path.setGravity(Gravity.CENTER);
            path.setText(userStatus.getPathCreationText());
        } catch (Exception e) {
            Toast.makeText(activity, "Something went wrong: " + e, Toast.LENGTH_LONG).show();

        }

        //TODO methods for redrawing map goes here
    }
    */
}
