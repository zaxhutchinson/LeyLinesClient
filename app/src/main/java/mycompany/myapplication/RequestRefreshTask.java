package mycompany.myapplication;


import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by tnash219 on 10/21/2014.
 */


public class RequestRefreshTask extends AsyncTask<String, Void, String[]> {

    Activity activity = new Activity();

    public RequestRefreshTask(Activity activity) {
        this.activity = activity;
    }

    //sends a message to a server indicating that it should respond with the status of the device
    @Override
    protected String[] doInBackground(String... HostOutIn) {
        try {
            //prep new socket
            Socket socketOut = new Socket();

            //connect to server
            socketOut.connect(new InetSocketAddress(HostOutIn[0],Integer.parseInt(HostOutIn[1])),5000);

            //prepare to send message to server
            PrintWriter out = new PrintWriter(socketOut.getOutputStream());

            /*
            Date date = new Date();
            DateFormat dateFormat = DateFormat.getDateTimeInstance();
            out.println(date.getTime() + " " + dateFormat.format(date));
            */

            //sample command with unique device id that should work as an identifier
            out.println("LONGHEXNUMBERHERE REFRESH " + HostOutIn[2]);
            out.flush();
            out.close();

            //disconnect and close socket
            socketOut.close();

            //temporary indicator for status refreshed toast
            HostOutIn[1] = "REFRESH";

        } catch (UnknownHostException e) {
            HostOutIn[1] = "Host \"" + HostOutIn[0] + ":" + HostOutIn[1] + "\" not reachable\n" + e;
            HostOutIn[0] = "ERROR";
        } catch (IOException e) {
            HostOutIn[1] =  "Outgoing I/O operation failed: " + e;
            HostOutIn[0] = "ERROR";
        } catch(IllegalArgumentException e) {
            HostOutIn[1] = "Illegal argument: " + e;
            HostOutIn[0] = "ERROR";
        }


        //returns the strings for use in RetrieveStatusTask
        return HostOutIn;
    }

    @Override
    protected void onPostExecute(String[] HostOutIn) {
        if (HostOutIn[0].equals("ERROR")) {
            Toast.makeText(activity,HostOutIn[1],Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(activity, "Refreshing...", Toast.LENGTH_LONG).show();
            new RetrieveStatusTask(activity).execute(HostOutIn);
        }
    }
}


