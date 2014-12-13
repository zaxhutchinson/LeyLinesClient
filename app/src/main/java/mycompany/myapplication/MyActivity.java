package mycompany.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.IntentService;
import android.content.Intent;
import android.location.LocationListener;
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

        Button displayButton = (Button)findViewById(R.id.displayButton);
        displayButton.setOnClickListener(this);

        Button pathButton = (Button)findViewById(R.id.pathButton);
        pathButton.setOnClickListener(this);

        gpsIntent = new Intent(this, MockGPSService.class);
        startService(gpsIntent);
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

        //TODO rename these to better reflect their purpose and/or remove when not needed
        EditText editHost = (EditText)findViewById(R.id.editTextHost);
        EditText editPort = (EditText)findViewById(R.id.editTextPort);
        EditText editMessage = (EditText)findViewById(R.id.editTextMessage);

        if (editHost.getText().toString().length() > 0 && editPort.getText().toString().length() > 0 && editMessage.getText().toString().length() > 0) {

            int id = view.getId();

        /* old test button
        if (id == R.id.sendButton) {
            SendToServer sendToServer = new SendToServer().newInstance(editHost.getText().toString(), editPort.getText().toString(), editMessage.getText().toString());
            sendToServer.show(getSupportFragmentManager(), "sendToServer");
        }
        */

            if (id == R.id.refreshButton) {
                RequestRefreshTask requestRefreshTask = new RequestRefreshTask(this);
                requestRefreshTask.execute(editHost.getText().toString(), editPort.getText().toString(), editMessage.getText().toString());

            }
            if (id == R.id.trackerButton) {
                //gpsIntent.
                ToggleTrackerTask toggleTrackerTask = new ToggleTrackerTask(this);
                toggleTrackerTask.execute(editHost.getText().toString(), editPort.getText().toString(), editMessage.getText().toString());
            }
            if (id == R.id.displayButton) {
                ChangeDisplayTask changeDisplayTask = new ChangeDisplayTask(this);
                changeDisplayTask.execute(editHost.getText().toString(), editPort.getText().toString(), editMessage.getText().toString());
            }
            if (id == R.id.pathButton) {
                TogglePathTask togglePathTask = new TogglePathTask(this);
                togglePathTask.execute(editHost.getText().toString(), editPort.getText().toString(), editMessage.getText().toString());
            }
        }
        else {

            StringBuffer isMissing = new StringBuffer();
            if (editHost.getText().toString().length() == 0) isMissing.append("host/");
            if (editPort.getText().toString().length() == 0) isMissing.append("port/");
            if (editMessage.getText().toString().length() == 0) isMissing.append("message/");
            isMissing.deleteCharAt(isMissing.length() - 1);
            isMissing.append(" missing");

            Toast.makeText(this, "Cannot send to server:\n" + isMissing, Toast.LENGTH_LONG).show();

        }
    }


}
