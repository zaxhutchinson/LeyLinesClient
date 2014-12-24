package mycompany.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * Created by tnash219 on 11/16/2014.
 */
public class SettingsActivity extends ActionBarActivity implements
        AdapterView.OnItemSelectedListener,
        ConnectionCallbacks,
        OnConnectionFailedListener {



    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private String mLatitude, mLongitude;

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        buildGoogleApiClient();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        int lifestyleSetting, sensitivitySetting;

        //lifestyle init
        Spinner lifestyleSpinner = (Spinner)findViewById(R.id.spinner);
        TextView lifestyleTextView = (TextView)findViewById(R.id.textView3);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> lifestyleAdapter = ArrayAdapter.createFromResource(this,
                R.array.lifestyle_choices, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        lifestyleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        lifestyleSpinner.setAdapter(lifestyleAdapter);

        lifestyleSetting = sharedPreferences.getInt("pref_key_lifestyle_setting",0);
        lifestyleTextView.setText(getResources().getStringArray(R.array.lifestyle_summaries)[lifestyleSetting]);
        lifestyleSpinner.setSelection(lifestyleSetting);
        lifestyleSpinner.setOnItemSelectedListener(this);




        //sensitivity init
        Spinner sensitivitySpinner = (Spinner)findViewById(R.id.spinner2);
        TextView sensitivityTextView = (TextView)findViewById(R.id.textView4);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> sensitivityAdapter = ArrayAdapter.createFromResource(this,
                R.array.sensitivity_choices, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        sensitivityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        sensitivitySpinner.setAdapter(sensitivityAdapter);

        sensitivitySetting = sharedPreferences.getInt("pref_key_sensitivity_setting",0);
        sensitivityTextView.setText(getResources().getStringArray(R.array.sensitivity_summaries)[sensitivitySetting]);
        sensitivitySpinner.setSelection(sensitivitySetting);
        sensitivitySpinner.setOnItemSelectedListener(this);


        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.test);
        CheckBox checkBox = (CheckBox)findViewById(R.id.checkBox);
        Button button = (Button)findViewById(R.id.button);



        if (sharedPreferences.getBoolean("pref_key_advanced_settings",false)) {
            relativeLayout.setEnabled(true);
            lifestyleSpinner.setEnabled(true);
            sensitivitySpinner.setEnabled(true);
            button.setEnabled(false);
            checkBox.setChecked(false);
        }
        else {
            relativeLayout.setEnabled(false);
            lifestyleSpinner.setEnabled(false);
            sensitivitySpinner.setEnabled(false);
            button.setEnabled(true);
            checkBox.setChecked(true);
        }

        //disallows further editing after account is created and confirmed
        //TODO: implement disabling/deleting accounts at some point
        if(sharedPreferences.getBoolean("pref_key_account_setup",false)) {
            EditText accountField = (EditText)findViewById(R.id.editTextUid);
            accountField.setText("User ID: " + sharedPreferences.getString("pref_key_UID",""));
            accountField.setEnabled(false);
            accountField = (EditText)findViewById(R.id.editTextHost);
            accountField.setText("Host: " + sharedPreferences.getString("pref_key_HOST",""));
            accountField.setEnabled(false);
            accountField = (EditText)findViewById(R.id.editTextPort);
            accountField.setText("Port: " + sharedPreferences.getString("pref_key_PORT",""));
            accountField.setEnabled(false);
            Button submit = (Button)findViewById(R.id.button5);
            submit.setEnabled(false);
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int id = adapterView.getId();
        if (id == R.id.spinner) {
            editor.putInt("pref_key_lifestyle_setting",i);
            TextView textView = (TextView)findViewById(R.id.textView3);
            textView.setText(getResources().getStringArray(R.array.lifestyle_summaries)[i]);

            //make sure these are enabled
            editor.putBoolean("pref_key_distance_settings", true);
            editor.putBoolean("pref_key_distance_total_settings", true);
            editor.putBoolean("pref_key_time_settings", true);
            editor.putBoolean("pref_key_tracker_settings", true);
            editor.putBoolean("pref_key_tracker_disabled_setting", true);
            editor.putBoolean("pref_key_tracker_response_setting", true);

            if (i == 0) {
                editor.putString("pref_key_distance_deviation_relative_setting", getResources().getStringArray(R.array.adventurous_lifestyle)[0]);
                editor.putString("pref_key_distance_deviation_total_setting", getResources().getStringArray(R.array.adventurous_lifestyle)[1]);
                editor.putString("pref_key_time_deviation_setting", getResources().getStringArray(R.array.adventurous_lifestyle)[2]);
                editor.putString("pref_key_tracker_disabled_duration_setting", getResources().getStringArray(R.array.adventurous_lifestyle)[3]);
                editor.putString("pref_key_tracker_response_misses_setting", getResources().getStringArray(R.array.adventurous_lifestyle)[4]);
            }
            else if (i == 1) {
                editor.putString("pref_key_distance_deviation_relative_setting", getResources().getStringArray(R.array.adventurous_lifestyle)[0]);
                editor.putString("pref_key_distance_deviation_total_setting", getResources().getStringArray(R.array.adventurous_lifestyle)[1]);
                editor.putString("pref_key_time_deviation_setting", getResources().getStringArray(R.array.adventurous_lifestyle)[2]);
                editor.putString("pref_key_tracker_disabled_duration_setting", getResources().getStringArray(R.array.adventurous_lifestyle)[3]);
                editor.putString("pref_key_tracker_response_misses_setting", getResources().getStringArray(R.array.adventurous_lifestyle)[4]);
            }
            else if (i == 2) {
                editor.putString("pref_key_distance_deviation_relative_setting", getResources().getStringArray(R.array.adventurous_lifestyle)[0]);
                editor.putString("pref_key_distance_deviation_total_setting", getResources().getStringArray(R.array.adventurous_lifestyle)[1]);
                editor.putString("pref_key_time_deviation_setting", getResources().getStringArray(R.array.adventurous_lifestyle)[2]);
                editor.putString("pref_key_tracker_disabled_duration_setting", getResources().getStringArray(R.array.adventurous_lifestyle)[3]);
                editor.putString("pref_key_tracker_response_misses_setting", getResources().getStringArray(R.array.adventurous_lifestyle)[4]);
            }
            else if (i == 3) {
                editor.putString("pref_key_distance_deviation_relative_setting", getResources().getStringArray(R.array.adventurous_lifestyle)[0]);
                editor.putString("pref_key_distance_deviation_total_setting", getResources().getStringArray(R.array.adventurous_lifestyle)[1]);
                editor.putString("pref_key_time_deviation_setting", getResources().getStringArray(R.array.adventurous_lifestyle)[2]);
                editor.putString("pref_key_tracker_disabled_duration_setting", getResources().getStringArray(R.array.adventurous_lifestyle)[3]);
                editor.putString("pref_key_tracker_response_misses_setting", getResources().getStringArray(R.array.adventurous_lifestyle)[4]);
            }

        }
        else if (id == R.id.spinner2) {
            editor.putInt("pref_key_sensitivity_setting",i);
            TextView textView = (TextView)findViewById(R.id.textView4);
            textView.setText(getResources().getStringArray(R.array.sensitivity_summaries)[i]);
            if (i == 0) {
                editor.putString("pref_key_alert_frequency",getResources().getStringArray(R.array.low_sensitivity)[0]);
                editor.putString("pref_key_gps_collect_frequency",getResources().getStringArray(R.array.low_sensitivity)[1]);
                editor.putString("pref_key_gps_send_frequency", getResources().getStringArray(R.array.low_sensitivity)[2]);
                editor.putFloat("pref_key_distance_relative_importance",Float.parseFloat(getResources().getStringArray(R.array.low_sensitivity)[3]));
                editor.putFloat("pref_key_distance_total_importance",Float.parseFloat(getResources().getStringArray(R.array.low_sensitivity)[3]));
                editor.putFloat("pref_key_time_importance",Float.parseFloat(getResources().getStringArray(R.array.low_sensitivity)[3]));
            }
            else if (i == 1) {
                editor.putString("pref_key_alert_frequency",getResources().getStringArray(R.array.moderate_sensitivity)[0]);
                editor.putString("pref_key_gps_collect_frequency",getResources().getStringArray(R.array.moderate_sensitivity)[1]);
                editor.putString("pref_key_gps_send_frequency",getResources().getStringArray(R.array.moderate_sensitivity)[2]);
                editor.putFloat("pref_key_distance_relative_importance",Float.parseFloat(getResources().getStringArray(R.array.moderate_sensitivity)[3]));
                editor.putFloat("pref_key_distance_total_importance",Float.parseFloat(getResources().getStringArray(R.array.moderate_sensitivity)[3]));
                editor.putFloat("pref_key_time_importance",Float.parseFloat(getResources().getStringArray(R.array.moderate_sensitivity)[3]));
            }
            else if (i == 2) {
                editor.putString("pref_key_alert_frequency",getResources().getStringArray(R.array.high_sensitivity)[0]);
                editor.putString("pref_key_gps_collect_frequency",getResources().getStringArray(R.array.high_sensitivity)[1]);
                editor.putString("pref_key_gps_send_frequency",getResources().getStringArray(R.array.high_sensitivity)[2]);
                editor.putFloat("pref_key_distance_relative_importance",Float.parseFloat(getResources().getStringArray(R.array.high_sensitivity)[3]));
                editor.putFloat("pref_key_distance_total_importance",Float.parseFloat(getResources().getStringArray(R.array.high_sensitivity)[3]));
                editor.putFloat("pref_key_time_importance",Float.parseFloat(getResources().getStringArray(R.array.high_sensitivity)[3]));
            }
        }
        editor.apply();
    }

    public void onCheckboxClicked(View view) {
        int id = view.getId();
        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.test);
        Spinner lifestyleSpinner = (Spinner)findViewById(R.id.spinner);
        Spinner sensitivitySpinner = (Spinner)findViewById(R.id.spinner2);
        Button button = (Button)findViewById(R.id.button);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (R.id.checkBox == id) {
            if (!((CheckBox) view).isChecked()) {
                relativeLayout.setEnabled(true);
                lifestyleSpinner.setEnabled(true);
                sensitivitySpinner.setEnabled(true);
                button.setEnabled(false);
                editor.putBoolean("pref_key_advanced_settings",true);
                editor.apply();
            }
            else {
                relativeLayout.setEnabled(false);
                lifestyleSpinner.setEnabled(false);
                sensitivitySpinner.setEnabled(false);
                button.setEnabled(true);
                editor.putBoolean("pref_key_advanced_settings",false);
                editor.apply();
            }
        }

    }

    public void onButtonClicked(View view) {
        int id = view.getId();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (R.id.button6 == id) {
            if(sharedPreferences.getBoolean("pref_key_account_setup",false)) {
                SendPreferencesTask sendPreferencesTask = new SendPreferencesTask(this);
                sendPreferencesTask.execute();
            }
            else Toast.makeText(this, "You need to set up an account in the settings menu before you can use this", Toast.LENGTH_SHORT).show();
        }

        if (R.id.button == id) {
            Intent preferences = new Intent(this, AdvancedSettingsActivity.class);
            startActivity(preferences);
        }

        if (R.id.button5 == id) {
            if (!sharedPreferences.getBoolean("pref_key_tracker_enabled",false)) {
                TextView textView = (TextView) findViewById(R.id.editTextUid);
                String uid = textView.getText().toString();
                textView = (TextView) findViewById(R.id.editTextHost);
                String host = textView.getText().toString();
                textView = (TextView) findViewById(R.id.editTextPort);
                String port = textView.getText().toString();
                SendAccountFragment sendAccountFragment = new SendAccountFragment().newInstance(uid, host, port, mLatitude, mLongitude);
                sendAccountFragment.show(getSupportFragmentManager(), "sendAccountFragment");
            }
            else Toast.makeText(this, "Disable tracker first", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        //Toast.makeText(this, "Connected to api", Toast.LENGTH_LONG).show();
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitude = String.valueOf(mLastLocation.getLatitude());
            mLongitude = String.valueOf(mLastLocation.getLongitude());
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static class SendAccountFragment extends DialogFragment {

        public SendAccountFragment newInstance(String requestedUid, String selectedHost, String selectedPort, String currentLatitude, String currentLongitude) {
            SendAccountFragment sendAccountFragment = new SendAccountFragment();
            Bundle args = new Bundle();
            args.putString("UID", requestedUid);
            args.putString("HOST", selectedHost);
            args.putString("PORT", selectedPort);
            args.putString("LATITUDE",currentLatitude);
            args.putString("LONGITUDE",currentLongitude);
            sendAccountFragment.setArguments(args);
            return sendAccountFragment;
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final String uid = getArguments().getString("UID");
            final String host = getArguments().getString("HOST");
            final String port = getArguments().getString("PORT");
            final String latitude = getArguments().getString("LATITUDE");
            final String longitude = getArguments().getString("LONGITUDE");
            String message = String.format("Desired UID: %s\nServer Location: %s:%s\nCurrent Location: (%s,%s)\nIs this correct?",uid,host,port,latitude,longitude);

            builder.setMessage(message)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            RequestAccountTask requestAccountTask = new RequestAccountTask(getActivity());
                            requestAccountTask.execute(uid, host, port, latitude, longitude, new String());

                            //temp stuff
//                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
//                            editor.putString("pref_key_UID",uid);
//                            editor.putString("pref_key_HOST",host);
//                            editor.putString("pref_key_PORT",port);
//                            editor.putBoolean("pref_key_account_setup",true);
//                            editor.commit();
//                            getActivity().finish();
//                            Toast.makeText(getActivity(), "Account created successfully", Toast.LENGTH_LONG).show();


                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });

            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    public class SendPreferencesTask extends AsyncTask<Void, String, String> {

        Activity activity;
        SharedPreferences sharedPreferences;

        public SendPreferencesTask(Activity activity) {
            this.activity = activity;
        }


        @Override
        protected String doInBackground(Void... voids) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
            Map<String,?> prefsMap = sharedPreferences.getAll();

            //prepare all necessary settings for sync
            StringBuilder settings = new StringBuilder();
            for(Map.Entry<String,?> entry : prefsMap.entrySet()) {

                if (!entry.getKey().equals("pref_key_UID") &&
                        !entry.getKey().equals("pref_key_HOST") &&
                        !entry.getKey().equals("pref_key_PORT") &&
                        !entry.getKey().equals("pref_key_alert_status") &&
                        !entry.getKey().equals("pref_key_tracker_enabled") &&
                        !entry.getKey().equals("pref_key_display_status") &&
                        !entry.getKey().equals("pref_key_automatic_path"))
                    settings.append(entry.getKey()).append("=").append(entry.getValue().toString()).append("/n");
            }

            String status = new String();

            try {
                //prepare new socket
                Socket socket = new Socket();

                //connect to server
                socket.connect(new InetSocketAddress(sharedPreferences.getString("pref_key_HOST", ""), Integer.parseInt(sharedPreferences.getString("pref_key_PORT", ""))), 5000);

                //prepare to send message to server
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                //text to send in order to update preferences on server
                out.print("S\n" + //indicates command for sending preferences
                        sharedPreferences.getString("pref_key_UID", "") + "\n" + //user id
                        settings.toString()); //all the preferences

                //done with output
                out.flush();

                //prepare to receive message from server
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while(true) {
                    status = in.readLine();
                    if (status != null) break;
                }

                if (!status.equals("OK")) publishProgress(status);

                //stop waiting for input from server
                out.close();
                in.close();

                //disconnect and close socket
                socket.close();

            } catch (UnknownHostException e) {
                publishProgress("Host unreachable: " + e);
            } catch (IOException e) {
                publishProgress("I/O operation failed: " + e);
            } catch(IllegalArgumentException e) {
                publishProgress("Illegal argument: " + e);
            }

            return status;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            Toast.makeText(activity, progress[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(String status) {
            if (!status.equals("OK")) {
                Toast.makeText(activity, "Settings sync unsuccessful", Toast.LENGTH_LONG).show();
            }
            else Toast.makeText(activity, "Settings synced", Toast.LENGTH_LONG).show();
        }
    }
}
