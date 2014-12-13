package mycompany.myapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by tnash219 on 10/22/2014.
 */
public class RetrieveStatusTask extends AsyncTask<String, UserStatus, String[]> {

    //required for toast messages and manipulation of the status view
    Activity activity;
    TextView status;
    TextView tracker;
    TextView display;
    TextView path;
    //TODO variables/object for drawing map goes here

    public RetrieveStatusTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        status = (TextView)activity.findViewById(R.id.statusText);
        tracker = (TextView)activity.findViewById(R.id.trackerText);
        display = (TextView)activity.findViewById(R.id.displayText);
        path = (TextView)activity.findViewById(R.id.pathText);
        //TODO connect to map variables/object here
    }

    @Override
    protected String[] doInBackground(String... HostOutIn) {
        try {
            //prep new socket
            Socket socketIn = new Socket();

            //connect to server
            socketIn.connect(new InetSocketAddress(HostOutIn[0],Integer.parseInt(HostOutIn[2])),5000);

            //prepare to receive message from server
            BufferedReader in = new BufferedReader(new InputStreamReader(socketIn.getInputStream()));

            //retrieves string with status to be parsed and stores it in userStatus
            UserStatus userStatus = new UserStatus(in.readLine());

            //disconnect and close socket
            socketIn.close();

            //data used to update UI
            publishProgress(userStatus);

            //TODO something to update status file

        }  catch (UnknownHostException e) {
            HostOutIn[1] = "Host \"" + HostOutIn[0] + ":" + HostOutIn[2] + "\" not reachable\n" + e;
            HostOutIn[0] = "ERROR";
        } catch (IOException e) {
            HostOutIn[1] =  "Incoming I/O operation failed: " + e;
            HostOutIn[0] = "ERROR";
        } catch(IllegalArgumentException e) {
            HostOutIn[1] = "Illegal argument: " + e;
            HostOutIn[0] = "ERROR";
        }

        return HostOutIn;
    }

    @Override
    protected void onProgressUpdate(UserStatus... userStatuses) {
        status.setGravity(Gravity.CENTER);
        status.setTextColor(activity.getResources().getColor(userStatuses[0].getAlertColor()));
        status.setText(userStatuses[0].getAlertText());

        tracker.setGravity(Gravity.CENTER);
        tracker.setTextColor(activity.getResources().getColor(userStatuses[0].getTrackerColor()));
        tracker.setText(userStatuses[0].getTrackerText());

        display.setGravity(Gravity.CENTER);
        display.setText(userStatuses[0].getDisplayText());

        path.setGravity(Gravity.CENTER);
        path.setText(userStatuses[0].getPathCreationText());

        //TODO methods for redrawing map goes here
    }

    @Override
    protected void onPostExecute(String[] HostOutIn) {
        if (HostOutIn[0].equals("ERROR")) {
            Toast.makeText(activity, HostOutIn[1], Toast.LENGTH_LONG).show();
        }

        else if(HostOutIn[1].equals("REFRESH")) {
            Toast.makeText(activity, "Status updated.", Toast.LENGTH_LONG).show();
        }

    }
}
