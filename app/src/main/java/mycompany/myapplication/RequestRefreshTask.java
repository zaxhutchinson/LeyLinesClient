package mycompany.myapplication;


import android.app.Activity;
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
 *
 * CURRENTLY UNUSED - Code was moved into the main activity due to the
 * blocking nature of async tasks.
 */

public class RequestRefreshTask extends AsyncTask<String, String, UserStatus> {

    //required for toast messages and manipulation of the status view
    Activity activity;
    TextView status;
    TextView tracker;
    TextView display;
    TextView path;

    //TODO variables/object for drawing map goes here

    @Override
    protected void onPreExecute() {

    }

    public RequestRefreshTask(Activity activity) {
        this.activity = activity;
    }

    //sends a message to a server indicating that it should respond with the status of the device
    @Override
    protected UserStatus doInBackground(String... UidHostPort) {
        status = (TextView)activity.findViewById(R.id.statusText);
        tracker = (TextView)activity.findViewById(R.id.trackerText);
        display = (TextView)activity.findViewById(R.id.displayText);
        path = (TextView)activity.findViewById(R.id.pathText);

        //TODO connect to map variables/object here

        try {
            //prepare new socket
            Socket socket = new Socket();

            //connect to server
            socket.connect(new InetSocketAddress(UidHostPort[1], Integer.parseInt(UidHostPort[2])), 5000);

            //prepare to send message to server
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

            //prepare to receive message from server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //text to send in order to request a new account
            out.print("R\n" + //indicates command for refresh
                    UidHostPort[0]); //user id

            //done with output
            out.flush();

            while(true) {
                UidHostPort[3] = in.readLine();
                if (UidHostPort[3] != null) break;
            }

            //indicate to user that you're sending to server
            publishProgress("Refreshing...");

            //stop waiting for input from server
            out.close();
            in.close();

            //disconnect and close socket
            socket.close();

        } catch (UnknownHostException e) {
            publishProgress("Host \"" + UidHostPort[1] + ":" + UidHostPort[2] + "\" unreachable\n" + e);
        } catch (IOException e) {
            publishProgress("I/O operation failed: " + e);
        } catch(IllegalArgumentException e) {
            publishProgress("Illegal argument: " + e);
        }


        //retrieves string with status to be parsed and stores it in userStatus
        UserStatus userStatus = new UserStatus(UidHostPort[3]);

        //prepares data for updating preferences
        String[] retrievedPreference = UidHostPort[3].split("[ ]+");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (retrievedPreference.length == 4) {
            editor.putString("pref_key_alert_status", retrievedPreference[0]);
            editor.putBoolean("pref_key_tracker_enabled", Boolean.valueOf(retrievedPreference[1]));
            editor.putString("pref_key_display_status", retrievedPreference[2]);
            editor.putBoolean("pref_key_automatic_path", Boolean.valueOf(retrievedPreference[3]));
            editor.apply();
        }

        //returns the strings for use in onPostExecute
        return userStatus;
    }

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
}


