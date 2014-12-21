package mycompany.myapplication;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;

/**
 * Created by tnash219 on 10/21/2014.
 */


public class RequestAccountTask extends AsyncTask<String, String, String[]> {

    Activity activity = new Activity();

    public RequestAccountTask(Activity activity) {
        this.activity = activity;
    }

    //sends a message to a server indicating that it should respond with the status of the device
    @Override
    protected String[] doInBackground(String... UidHostPort) {
        try {
            //prepare new socket
            Socket socket = new Socket();

            //connect to server
            socket.connect(new InetSocketAddress(UidHostPort[1], Integer.parseInt(UidHostPort[2])), 5000);

            //prepare to send message to server
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

            //get the current time
            Calendar calendar = Calendar.getInstance();
            long ts = calendar.getTimeInMillis() / 1000;

            //text to send in order to request a new account
            out.print("I\n" + //indicates command for new account
                UidHostPort[0] + "\n" + //requested id
                UidHostPort[3] + "\n" + //latitude
                UidHostPort[4] + "\n" + //longitude
                ts); //time in seconds

            //indicate to user that you're sending to server
            publishProgress("Requesting account...");

            //done with output housekeeping
            out.flush();


            //prepare to receive message from server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while(true) {
                UidHostPort[5] = in.readLine();
                if(UidHostPort[5].length() > 0) break;
            }

            //stop waiting for input from server
            out.close();
            in.close();

            //disconnect and close socket
            socket.close();

            //store account details on phone
            if (UidHostPort[5].equals("OK")) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("pref_key_UID", UidHostPort[0]);
                editor.putString("pref_key_HOST", UidHostPort[1]);
                editor.putString("pref_key_PORT", UidHostPort[2]);
                editor.putBoolean("pref_key_account_setup", true);
                editor.apply();
            }


        } catch (UnknownHostException e) {
            publishProgress("Host \"" + UidHostPort[0] + ":" + UidHostPort[1] + "\" unreachable\n" + e);
        } catch (IOException e) {
            publishProgress("Outgoing I/O operation failed: " + e);
        } catch(IllegalArgumentException e) {
            publishProgress("Illegal argument: " + e);
        }

        //returns the strings for use in onPostExecute
        return UidHostPort;
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        Toast.makeText(activity, progress[0], Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onPostExecute(String[] UidHostPort) {
        if(UidHostPort[5].equals("OK")) {
            Toast.makeText(activity, "Account created succssfully.", Toast.LENGTH_LONG).show();
            activity.setResult(Activity.RESULT_OK);
            activity.finish();
        }
        else {
            Toast.makeText(activity, String.format("Unable to create account. %s",UidHostPort[5]), Toast.LENGTH_LONG).show();
        }
    }
}


