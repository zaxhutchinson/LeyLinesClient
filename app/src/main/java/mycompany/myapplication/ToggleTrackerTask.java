package mycompany.myapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by tnash219 on 10/24/2014.
 */
public class ToggleTrackerTask extends AsyncTask<String, Void, String[]> {

    Activity activity = new Activity();

    public ToggleTrackerTask(Activity activity) {
        this.activity = activity;
    }

    //sends a message to a server telling it to change the current tracker setting
    @Override
    protected String[] doInBackground(String... UidHostPort) {
        /*
        try {
            //prep new socket
            Socket socketOut = new Socket();

            //connect to server
            socketOut.connect(new InetSocketAddress(UidHostPort[0],Integer.parseInt(UidHostPort[1])),5000);

            //prepare to send message to server
            PrintWriter out = new PrintWriter(socketOut.getOutputStream());

            //sample command with unique device id that should work as an identifier
            out.println("LONGHEXNUMBERHERE TRACKER " + UidHostPort[2]);
            out.flush();
            out.close();

            //disconnect and close socket
            socketOut.close();

        } catch (UnknownHostException e) {
            UidHostPort[1] = "Host \"" + UidHostPort[0] + ":" + UidHostPort[1] + "\" not reachable\n" + e;
            UidHostPort[0] = "ERROR";
        } catch (IOException e) {
            UidHostPort[1] =  "I/O operation failed: " + e;
            UidHostPort[0] = "ERROR";
        } catch(IllegalArgumentException e) {
            UidHostPort[1] = "Illegal argument: " + e;
            UidHostPort[0] = "ERROR";
        }
        */

        UidHostPort[2] = "T";


        //returns the strings for use in RetrieveStatusTask
        return UidHostPort;
    }

    @Override
    protected void onPostExecute(String[] UidHostPort) {
        if (UidHostPort[0].equals("ERROR")) {
            Toast.makeText(activity, UidHostPort[1], Toast.LENGTH_LONG).show();
        }
        else {
            new RetrieveStatusTask(activity).execute(UidHostPort);
        }
    }
}
