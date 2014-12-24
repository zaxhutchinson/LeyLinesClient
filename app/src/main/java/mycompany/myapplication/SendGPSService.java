package mycompany.myapplication;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * CURRENTLY UNUSED -- Combined GPS collection and sending into
 * a single entity. The code below is now in MockGPSService.
 */
public class SendGPSService extends IntentService {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public SendGPSService() {
        super("SendGPSService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        setIntentRedelivery(true);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        SendGPSTask collectGPSTask = new SendGPSTask();
        collectGPSTask.execute();

    }

    public class SendGPSTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            long startTime;
            System.out.println("Now sending gps coordinates");
            while (sharedPreferences.getBoolean("pref_key_tracker_enabled",false)) {
                startTime = System.currentTimeMillis();
                while (Long.parseLong(sharedPreferences.getString("pref_key_gps_send_frequency","")) * 1000 + startTime > System.currentTimeMillis()) {

                }

                    String[] latitudes = sharedPreferences.getString("pref_key_latitude_list","").split(",");
                    String[] longitudes = sharedPreferences.getString("pref_key_longitude_list","").split(",");
                    String[] times = sharedPreferences.getString("pref_key_time_list","").split(",");

                try {
                    //prepare new socket
                    Socket socket = new Socket();

                    //connect to server
                    socket.connect(new InetSocketAddress(sharedPreferences.getString("pref_key_HOST",""), Integer.parseInt(sharedPreferences.getString("pref_key_PORT",""))), 5000);

                    //prepare to send message to server
                    PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

                    //prepare to receive message from server
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    //checks if nothing went wrong with the collection of gps coordinates
                    if (latitudes.length == longitudes.length && longitudes.length == times.length) {
                        for (int i = 0; i < times.length; i++) {

                            //text to send in order to send gps coordinates
                            out.print("G\n" + //indicates command for new gps position
                                    sharedPreferences.getString("pref_key_UID", "") + "\n" + //user id
                                    latitudes[i] + "\n" + //latitude
                                    longitudes[i] + "\n" + //longitude
                                    times[i] + "\n"); //time in seconds
                        }
                    }

                    //done with output
                    out.flush();

                    //stop waiting for input from server
                    out.close();
                    in.close();

                    //disconnect and close socket
                    socket.close();

                } catch (UnknownHostException e) {
                    publishProgress("Host unreachable\n" + e);
                } catch (IOException e) {
                    publishProgress("I/O operation failed: " + e);
                } catch(IllegalArgumentException e) {
                    publishProgress("Illegal argument: " + e);
                }

                    //clear out strings when done
                    editor.putString("pref_key_latitude_list","");
                    editor.putString("pref_key_longitude_list","");
                    editor.putString("pref_key_time_list","");


            }
            System.out.println("No longer sending gps coordinates");
            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            System.out.println("Leylines: " + progress[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            stopSelf();
        }
    }
}
