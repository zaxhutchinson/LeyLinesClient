package mycompany.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationListener;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;


public class MyActivity extends ActionBarActivity implements
        View.OnClickListener {

    TextView status;
    TextView tracker;
    TextView display;
    TextView path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_my);
        /* old test button
        Button sendButton = (Button)findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);
        */

        Button refreshButton = (Button)findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(this);

        Button trackerButton = (Button)findViewById(R.id.trackerButton);
        trackerButton.setOnClickListener(this);

        //not implemented yet
        Button displayButton = (Button)findViewById(R.id.displayButton);
        displayButton.setOnClickListener(this);
        displayButton.setEnabled(false);

        //not implemented yet
        Button pathButton = (Button)findViewById(R.id.pathButton);
        pathButton.setOnClickListener(this);
        pathButton.setEnabled(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_destination) {
            Intent destination = new Intent(this, DestinationActivity.class);
            startActivity(destination);
        }
        if (id == R.id.action_settings) {
            Intent settings = new Intent(this, SettingsActivity.class);
            startActivity(settings);
        }
        if (id == R.id.action_exit) {
            ExitApplication exitApplication = new ExitApplication();
            exitApplication.show(getSupportFragmentManager(),"exitApplication");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getBoolean("pref_key_account_setup",false)) {

            String Uid = sharedPreferences.getString("pref_key_UID","");
            String Host = sharedPreferences.getString("pref_key_HOST","");
            String Port = sharedPreferences.getString("pref_key_PORT","");

            int id = view.getId();

        /* old test button
        if (id == R.id.sendButton) {
            SendToServer sendToServer = new SendToServer().newInstance(editHost.getText().toString(), editPort.getText().toString(), editMessage.getText().toString());
            sendToServer.show(getSupportFragmentManager(), "sendToServer");
        }
        */

            if (id == R.id.refreshButton) {
                //RequestRefreshTask requestRefreshTask = new RequestRefreshTask(this);
                //requestRefreshTask.execute(Uid, Host, Port, new String());

                status = (TextView)this.findViewById(R.id.statusText);
                tracker = (TextView)this.findViewById(R.id.trackerText);
                display = (TextView)this.findViewById(R.id.displayText);
                path = (TextView)this.findViewById(R.id.pathText);

                //TODO connect to map variables/object here

                String ReturnVal = "";

                try {
                    //prepare new socket
                    Socket socket = new Socket();

                    //connect to server
                    socket.connect(new InetSocketAddress(Host, Integer.parseInt(Port)), 5000);

                    //prepare to send message to server
                    PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

                    //prepare to receive message from server
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    //text to send in order to request a new account
                    out.print("R\n" + //indicates command for refresh
                            Uid); //user id

                    //done with output
                    out.flush();

                    while(true) {
                        ReturnVal = in.readLine();
                        if (ReturnVal != null) break;
                    }

                    //indicate to user that you're sending to server
                    //publishProgress("Refreshing...");

                    //stop waiting for input from server
                    out.close();
                    in.close();

                    //disconnect and close socket
                    socket.close();

                } catch (UnknownHostException e) {
                    //publishProgress("Host \"" + UidHostPort[1] + ":" + UidHostPort[2] + "\" unreachable\n" + e);
                    System.out.println("Host \"" + Host + ":" + Port + "\" unreachable\n" + e);
                } catch (IOException e) {
                    //publishProgress("I/O operation failed: " + e);
                    System.out.println("I/O operation failed: " + e);
                } catch(IllegalArgumentException e) {
                    //publishProgress("Illegal argument: " + e);
                    System.out.println("Illegal argument: " + e);
                }


                //retrieves string with status to be parsed and stores it in userStatus
                UserStatus userStatus = new UserStatus(ReturnVal);

                //prepares data for updating preferences
                String[] retrievedPreference = ReturnVal.split("[ ]+");
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (retrievedPreference.length == 4) {
                    editor.putString("pref_key_alert_status", retrievedPreference[0]);
                    editor.putBoolean("pref_key_tracker_enabled", Boolean.valueOf(retrievedPreference[1]));
                    editor.putString("pref_key_display_status", retrievedPreference[2]);
                    editor.putBoolean("pref_key_automatic_path", Boolean.valueOf(retrievedPreference[3]));
                    editor.apply();
                }

                //returns the strings for use in onPostExecute

                try {
                    status.setGravity(Gravity.CENTER);
                    status.setTextColor(this.getResources().getColor(userStatus.getAlertColor()));
                    status.setText(userStatus.getAlertText());

                    tracker.setGravity(Gravity.CENTER);
                    tracker.setTextColor(this.getResources().getColor(userStatus.getTrackerColor()));
                    tracker.setText(userStatus.getTrackerText());

                    display.setGravity(Gravity.CENTER);
                    display.setText(userStatus.getDisplayText());

                    path.setGravity(Gravity.CENTER);
                    path.setText(userStatus.getPathCreationText());
                } catch (Exception e) {
                    Toast.makeText(this, "Something went wrong: " + e, Toast.LENGTH_LONG).show();
                }
            }

            if (id == R.id.trackerButton) {

                status = (TextView)this.findViewById(R.id.statusText);
                tracker = (TextView)this.findViewById(R.id.trackerText);
                display = (TextView)this.findViewById(R.id.displayText);
                path = (TextView)this.findViewById(R.id.pathText);

                //TODO connect to map variables/object here

                String ReturnVal = "";

                try {
                    //prepare new socket
                    Socket socket = new Socket();

                    //connect to server
                    socket.connect(new InetSocketAddress(Host, Integer.parseInt(Port)), 5000);

                    //prepare to send message to server
                    PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

                    //text to send in order to request a new account
                    out.print("T\n" + //indicates command for toggling tracker
                            Uid + "\n"); //user id

                    //done with output
                    out.flush();


                    //prepare to receive message from server
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while(true) {
                        ReturnVal = in.readLine();
                        if (ReturnVal != null) break;
                    }

                    //stop waiting for input from server
                    out.close();
                    in.close();

                    //disconnect and close socket
                    socket.close();

                } catch (UnknownHostException e) {
                    System.out.println("Host \"" + Host + ":" + Port + "\" unreachable\n" + e);
                    //publishProgress("Host \"" + UidHostPort[1] + ":" + UidHostPort[2] + "\" unreachable\n" + e);
                } catch (IOException e) {
                    System.out.println("I/O operation failed: " + e);
                    //publishProgress("I/O operation failed: " + e);
                } catch(IllegalArgumentException e) {
                    //publishProgress("Illegal argument: " + e);
                    System.out.println("Illegal argument: " + e);
                }


                //retrieves string with status to be parsed and stores it in userStatus
                UserStatus userStatus = new UserStatus(ReturnVal);


                //prepares data for updating preferences
                String[] retrievedPreference = ReturnVal.split("[ ]+");
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (retrievedPreference.length == 4) {
                    editor.putString("pref_key_alert_status", retrievedPreference[0]);
                    editor.putBoolean("pref_key_tracker_enabled", Boolean.valueOf(retrievedPreference[1]));
                    editor.putString("pref_key_display_status", retrievedPreference[2]);
                    editor.putBoolean("pref_key_automatic_path", Boolean.valueOf(retrievedPreference[3]));
                    editor.commit();
                }

                //starts the gps collecting and sending services
                if (sharedPreferences.getBoolean("pref_key_tracker_enabled",false))  {
                    Intent gpsCollector = new Intent(this, MockGPSService.class);
                    this.startService(gpsCollector);
                }

                try {
                    status.setGravity(Gravity.CENTER);
                    status.setTextColor(this.getResources().getColor(userStatus.getAlertColor()));
                    status.setText(userStatus.getAlertText());

                    tracker.setGravity(Gravity.CENTER);
                    tracker.setTextColor(this.getResources().getColor(userStatus.getTrackerColor()));
                    tracker.setText(userStatus.getTrackerText());

                    display.setGravity(Gravity.CENTER);
                    display.setText(userStatus.getDisplayText());

                    path.setGravity(Gravity.CENTER);
                    path.setText(userStatus.getPathCreationText());
                } catch (Exception e) {
                    Toast.makeText(this, "Something went wrong: " + e, Toast.LENGTH_LONG).show();

                }

                /* writes the preferences to file in their current states
                File myPath = new File(Environment.getExternalStorageDirectory().toString());
                File myFile = new File(myPath, "MySharedPreferences");

                try
                {
                    FileWriter fw = new FileWriter(myFile);
                    PrintWriter pw = new PrintWriter(fw);

                    Map<String,?> prefsMap = sharedPreferences.getAll();

                    pw.println("S");
                    pw.println(Uid);

                    for(Map.Entry<String,?> entry : prefsMap.entrySet())
                    {
                        if (!entry.getKey().equals("pref_key_UID")) pw.println(entry.getKey() + "=" + entry.getValue().toString());
                    }

                    pw.close();
                    fw.close();
                }
                catch (Exception e)
                {
                    System.out.println(e.toString());
                }
                */
            }
            if (id == R.id.displayButton) {
                ChangeDisplayTask changeDisplayTask = new ChangeDisplayTask(this);
                changeDisplayTask.execute(Uid, Host, Port, new String());
            }
            if (id == R.id.pathButton) {
                TogglePathTask togglePathTask = new TogglePathTask(this);
                togglePathTask.execute(Uid, Host, Port, new String());
            }
        }
        else {
            Toast.makeText(this, "You need to set up an account in the settings menu before you can use this", Toast.LENGTH_LONG).show();
        }
    }


}
