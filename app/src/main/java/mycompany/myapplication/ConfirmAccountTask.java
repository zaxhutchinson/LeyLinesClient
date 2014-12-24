package mycompany.myapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * to be removed
 * was for a two part account creation handshake
 */
public class ConfirmAccountTask extends AsyncTask<String, Void, String[]> {

    //required for toast messages and manipulation of the status view
    Activity activity;
    TextView status;
    TextView tracker;
    TextView display;
    TextView path;
    //TODO variables/object for drawing map goes here

    public ConfirmAccountTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String[] doInBackground(String... UidHostPort) {
        try {
            //prep new socket
            Socket socketIn = new Socket();

            //connect to server
            socketIn.connect(new InetSocketAddress(UidHostPort[1],Integer.parseInt(UidHostPort[2])),5000);

            //prepare to receive message from server
            BufferedReader in = new BufferedReader(new InputStreamReader(socketIn.getInputStream()));

            UidHostPort[3] = in.readLine();

            //disconnect and close socket
            socketIn.close();

        }  catch (UnknownHostException e) {
            UidHostPort[1] = "Host \"" + UidHostPort[1] + ":" + UidHostPort[2] + "\" not reachable\n" + e;
            UidHostPort[0] = "ERROR";
        } catch (IOException e) {
            UidHostPort[1] =  "Incoming I/O operation failed: " + e;
            UidHostPort[0] = "ERROR";
        } catch(IllegalArgumentException e) {
            UidHostPort[1] = "Illegal argument: " + e;
            UidHostPort[0] = "ERROR";
        }

        return UidHostPort;
    }

    @Override
    protected void onPostExecute(String[] UidHostPort) {
        //account created on server time to confirm on device
        if(UidHostPort[3].equals("OK")) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("pref_key_UID",UidHostPort[0]);
            editor.putString("pref_key_HOST",UidHostPort[1]);
            editor.putString("pref_key_PORT",UidHostPort[2]);
            editor.putBoolean("pref_key_account_setup",true);
            Toast.makeText(activity, "Account created successfully.", Toast.LENGTH_LONG).show();
            activity.setResult(Activity.RESULT_OK);
            activity.finish();
        }
        else {
            //error message was sent instead
            Toast.makeText(activity, String.format("Unable to create account. %s",UidHostPort[3]), Toast.LENGTH_LONG).show();
        }
    }
}
