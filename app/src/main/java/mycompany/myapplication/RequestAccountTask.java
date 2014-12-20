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


public class RequestAccountTask extends AsyncTask<String, Integer, String[]> {

    Activity activity = new Activity();

    public RequestAccountTask(Activity activity) {
        this.activity = activity;
    }

    //sends a message to a server indicating that it should respond with the status of the device
    @Override
    protected String[] doInBackground(String... UidHostPort) {
        try {
            //prep new socket
            Socket socket = new Socket();

            //connect to server
            socket.connect(new InetSocketAddress(UidHostPort[1], Integer.parseInt(UidHostPort[2])), 5000);

            //prepare to send message to server
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

            Calendar calendar = Calendar.getInstance();
            long ts = calendar.getTimeInMillis() / 1000;

            //text to send in order to request a new account
            out.print("I\n" + //indicates command for new account
                UidHostPort[0] + "\n" + //requested id
                UidHostPort[3] + "\n" + //latitude
                UidHostPort[4] + "\n" + //longitude
                ts); //time in seconds

            //let user know you're sending
            publishProgress(0);
            out.flush();
            out.close();

            //prepare to receive message from server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while(true) {
                UidHostPort[5] = in.readLine();
                if(UidHostPort[5].length() > 0) break;
            }

            //close input from server
            in.close();

            //disconnect and close socket
            socket.close();


        } catch (UnknownHostException e) {
            UidHostPort[1] = "Host \"" + UidHostPort[0] + ":" + UidHostPort[1] + "\" not reachable\n" + e;
            UidHostPort[0] = "ERROR";
        } catch (IOException e) {
            UidHostPort[1] =  "Outgoing I/O operation failed: " + e;
            UidHostPort[0] = "ERROR";
        } catch(IllegalArgumentException e) {
            UidHostPort[1] = "Illegal argument: " + e;
            UidHostPort[0] = "ERROR";
        }


        //returns the strings for use in RetrieveStatusTask
        return UidHostPort;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        Toast.makeText(activity, "Requesting account...", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onPostExecute(String[] UidHostPort) {
        /*
        if (UidHostPort[0].equals("ERROR")) {
            Toast.makeText(activity,UidHostPort[1],Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(activity, "Requesting account...", Toast.LENGTH_LONG).show();
            //new ConfirmAccountTask(activity).execute(UidHostPort);
        }
        */
        if(UidHostPort[5].equals("OK")) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("pref_key_UID",UidHostPort[0]);
            editor.putString("pref_key_HOST",UidHostPort[1]);
            editor.putString("pref_key_PORT",UidHostPort[2]);
            editor.putBoolean("pref_key_account_setup",true);
            editor.commit();
            Toast.makeText(activity, "Account created successfully.", Toast.LENGTH_LONG).show();
            activity.setResult(Activity.RESULT_OK);
            activity.finish();
        }
        else {
            //error message was sent instead
            Toast.makeText(activity, String.format("Unable to create account. %s",UidHostPort[5]), Toast.LENGTH_LONG).show();
        }
    }
}


