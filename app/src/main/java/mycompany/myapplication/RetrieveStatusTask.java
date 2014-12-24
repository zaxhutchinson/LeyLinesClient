package mycompany.myapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * to be removed
 * was part of a two part status verification handshake
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
    protected String[] doInBackground(String... UidHostPort) {
        /*
        try {
            //prep new socket
            Socket socketIn = new Socket();

            //connect to server
            socketIn.connect(new InetSocketAddress(UidHostPort[0],Integer.parseInt(UidHostPort[2])),5000);

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
            UidHostPort[1] = "Host \"" + UidHostPort[0] + ":" + UidHostPort[2] + "\" not reachable\n" + e;
            UidHostPort[0] = "ERROR";
        } catch (IOException e) {
            UidHostPort[1] =  "Incoming I/O operation failed: " + e;
            UidHostPort[0] = "ERROR";
        } catch(IllegalArgumentException e) {
            UidHostPort[1] = "Illegal argument: " + e;
            UidHostPort[0] = "ERROR";
        }
        */

        //demonstration stuff
        String invert;
        if (UidHostPort[2].equals("T")) invert = tracker.getText().toString().equals("Enabled") ? "FALSE" : "TRUE";
        else invert = tracker.getText().toString().equals("Enabled") ? "TRUE" : "FALSE";
        UserStatus userStatus = new UserStatus(String.format("NONE %s NONE FALSE",invert));
        publishProgress(userStatus);

        return UidHostPort;
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
    protected void onPostExecute(String[] UidHostPort) {
        if (UidHostPort[0].equals("ERROR")) {
            Toast.makeText(activity, UidHostPort[1], Toast.LENGTH_LONG).show();
        }
        //else if(UidHostPort[1].equals("REFRESH")) {
          //  Toast.makeText(activity, "Status updated.", Toast.LENGTH_LONG).show();
        //}

        else Toast.makeText(activity, "Status updated.", Toast.LENGTH_LONG).show();


    }
}
