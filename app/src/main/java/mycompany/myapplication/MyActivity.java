package mycompany.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationListener;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesClient;


public class MyActivity extends ActionBarActivity implements
        View.OnClickListener {

    Intent gpsIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        //gpsIntent = new Intent(this, MockGPSService.class);
        //startService(gpsIntent);
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
                RequestRefreshTask requestRefreshTask = new RequestRefreshTask(this);
                requestRefreshTask.execute(Uid, Host, Port);
            }
            if (id == R.id.trackerButton) {
                ToggleTrackerTask toggleTrackerTask = new ToggleTrackerTask(this);
                toggleTrackerTask.execute(Uid, Host, Port);
            }
            if (id == R.id.displayButton) {
                ChangeDisplayTask changeDisplayTask = new ChangeDisplayTask(this);
                changeDisplayTask.execute(Uid, Host, Port);
            }
            if (id == R.id.pathButton) {
                TogglePathTask togglePathTask = new TogglePathTask(this);
                togglePathTask.execute(Uid, Host, Port);
            }
        }
        else {
            Toast.makeText(this, "You need to set up an account in the settings menu before you can use this", Toast.LENGTH_LONG).show();
        }
    }


}
