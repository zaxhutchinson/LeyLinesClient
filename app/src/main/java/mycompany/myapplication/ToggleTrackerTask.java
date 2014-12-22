package mycompany.myapplication;

import android.app.Activity;
import android.os.AsyncTask;
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
public class ToggleTrackerTask extends AsyncTask<String, String, UserStatus> {

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

    public ToggleTrackerTask(Activity activity) {
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

            //text to send in order to request a new account
            out.print("T\n" + //indicates command for toggling tracker
                    UidHostPort[0] + "\n"); //user id

            //indicate to user that you're sending to server
            publishProgress("Updating...");

            //done with output
            out.flush();


            //prepare to receive message from server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while(true) {
                UidHostPort[3] = in.readLine();
                if (UidHostPort[3] != null) break;
            }

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

        }

        //TODO methods for redrawing map goes here
    }
}
